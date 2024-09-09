package org.sudu.experiments.diff;

import org.sudu.experiments.Channel;
import org.sudu.experiments.LoggingJs;
import org.sudu.experiments.diff.folder.ModelFilter;
import org.sudu.experiments.diff.folder.RemoteFolderDiffModel;
import org.sudu.experiments.editor.EditorWindow;
import org.sudu.experiments.editor.ui.colors.EditorColorScheme;
import org.sudu.experiments.js.JsArray;
import org.sudu.experiments.js.JsMemoryAccess;
import org.sudu.experiments.math.ArrayOp;
import org.sudu.experiments.math.Numbers;
import org.sudu.experiments.protocol.BackendMessage;
import org.sudu.experiments.protocol.FrontendMessage;
import org.sudu.experiments.protocol.FrontendState;
import org.sudu.experiments.ui.Focusable;
import org.sudu.experiments.ui.ToolWindow0;
import org.sudu.experiments.ui.fs.RemoteFileTreeNode;
import org.sudu.experiments.ui.fs.RemoteHandle;
import org.sudu.experiments.ui.fs.RemoteDirectoryNode;
import org.sudu.experiments.ui.fs.RemoteFileNode;
import org.sudu.experiments.ui.window.Window;
import org.sudu.experiments.ui.window.WindowManager;
import org.sudu.experiments.update.DiffModelChannelUpdater;
import org.teavm.jso.JSObject;
import org.teavm.jso.browser.Performance;
import org.teavm.jso.core.JSString;
import org.teavm.jso.typedarrays.Int32Array;

import java.util.*;
import java.util.function.*;

import static org.sudu.experiments.diff.FolderDiffRootView.Selection;

public class RemoteFolderDiffWindow extends ToolWindow0 {

  Window window;
  Focusable focusSave;
  FolderDiffRootView rootView;
  RemoteDirectoryNode leftRoot, rightRoot;
  RemoteFolderDiffModel rootModel;

  protected Channel channel;
  private final double startTime;

  private boolean updatedRoots = false;
  boolean finished = false;

  String searchString = "*";

  private final int MAP_SIZE = 100;
  private final Consumer<String>[] openFileMap = new Consumer[MAP_SIZE];
  private int keyCnt = 0;

  public RemoteFolderDiffWindow(
      EditorColorScheme theme,
      WindowManager wm,
      Supplier<String[]> fonts,
      Channel channel
  ) {
    super(wm, theme, fonts);
    rootView = new FolderDiffRootView(windowManager.uiContext);
    rootView.applyTheme(theme);

    rootModel = new RemoteFolderDiffModel(null, "");
    leftRoot = new RemoteDirectoryNode("", getHandle(true, () -> rootModel), 0);
    rightRoot = new RemoteDirectoryNode("", getHandle(false, () -> rootModel), 0);

    rootView.left.setRoot(leftRoot);
    rootView.right.setRoot(rightRoot);

    window = createWindow(rootView);
    window.onFocus(this::onFocus);
    window.onBlur(this::onBlur);
    windowManager.addWindow(window);

    startTime = Performance.now();

    this.channel = channel;
    this.channel.setOnMessage(this::onChannelMessage);

    rootView.left.setOnSelectedLineChanged(this::leftSelectedChanged);
    rootView.right.setOnSelectedLineChanged(this::rightSelectedChanged);

  }

  @Override
  public void applyTheme(EditorColorScheme theme) {
    super.applyTheme(theme);
    window.setTheme(theme.dialogItem);
    rootView.applyTheme(theme);
  }

  protected void dispose() {
    window = null;
    rootView = null;
    leftRoot = rightRoot = null;
  }

  private void onBlur() {
    var f = windowManager.uiContext.focused();
    if (rootView.left == f || rootView.right == f)
      focusSave = f;
  }

  private void onFocus() {
    windowManager.uiContext.setFocus(focusSave);
  }

  private void openFile(JsArray<JSObject> jsResult) {
    int key = ((Int32Array) jsResult.pop()).get(0);
    var openFileRun = openFileMap[key];
    if (openFileRun != null && jsResult.getLength() != 0) {
      String source = ((JSString) jsResult.get(0)).stringValue();
      openFileRun.accept(source);
      openFileMap[key] = null;
    }
  }

  private void update(JsArray<JSObject> jsResult) {
    var msg = BackendMessage.deserialize(jsResult);
    rootModel.update(msg.root);

    if (!updatedRoots) {
      updatedRoots = true;
      leftRoot.setLine(msg.leftRootName);
      rightRoot.setLine(msg.rightRootName);
      window.setTitle(msg.leftRootName + " <-> " + msg.rightRootName);
      leftRoot.doOpen();
      rightRoot.doOpen();
      updateDiffInfo();
    }
    window.context.window.repaint();
    updateDiffInfo();
    if (rootModel.isCompared()) {
      finished = true;
      LoggingJs.Static.logger.log(LoggingJs.INFO,
          JSString.valueOf("RemoteFolderDiff finished"));
      rootView.fireFinished();
    }
  }

  protected void updateDiffInfo() {
    rootView.left.updateModel(rootModel, rightRoot, ModelFilter.LEFT);
    rootView.right.updateModel(rootModel, leftRoot, ModelFilter.RIGHT);
    rootView.setDiffModel(DiffModelBuilder.getDiffInfo(
        rootView.left.model(),
        rootView.right.model()
    ));
  }

  private void onChannelMessage(JsArray<JSObject> jsResult) {
    Int32Array array = jsResult.pop().cast();
    switch (array.get(0)) {
      case DiffModelChannelUpdater.FRONTEND_MESSAGE -> update(jsResult);
      case DiffModelChannelUpdater.OPEN_FILE -> openFile(jsResult);
    }
    LoggingJs.Static.logger.log(LoggingJs.INFO,
        JSString.valueOf("Got message in " + Numbers.iRnd(Performance.now() - startTime) + "ms")
    );
  }

  private RemoteHandle getHandle(
      boolean left,
      Supplier<RemoteFolderDiffModel> modelSupplier
  ) {
    return new RemoteHandle() {
      @Override
      public void updateView() {
        updateDiffInfo();
      }

      @Override
      public void openDir(RemoteDirectoryNode node) {
        var model = getModel();
        if (model.children == null) return;

        int foldersLen = 0;

        var children = new RemoteFileTreeNode[1];
        int childPtr = 0;

        int filter = left ? ModelFilter.LEFT : ModelFilter.RIGHT;
        int mP = model.nextInd(0, filter);
        while (mP >= 0) {
          RemoteFileTreeNode childNode;
          var child = model.child(mP);
          if (child.isFile()) {
            childNode = new RemoteFileNode(child.path, getHandle(left, getModel(mP)), node.depth + 1);
          } else {
            foldersLen++;
            childNode = new RemoteDirectoryNode(child.path, getHandle(left, getModel(mP)), node.depth + 1);
          }
          children = ArrayOp.addAt(childNode, children, childPtr++);
          mP = model.nextInd(mP + 1, filter);
        }
        children = Arrays.copyOf(children, childPtr);
        node.setChildren(children);
        node.folderCnt = foldersLen;
      }

      @Override
      public void openFile(RemoteFileNode node) {
        var opposite = getOppositeFile(node);
        if (opposite != null) setSelected(node, opposite);
        if (opposite != null) {
          var window = new FileDiffWindow(windowManager, theme, fonts);
          openFileMap[keyCnt] = (source) -> window.open(source, node.name(), left);
          sendOpenFile(node, left);
          openFileMap[keyCnt] = (source) -> window.open(source, opposite.name(), !left);
          sendOpenFile(opposite, !left);
        } else {
          var window = new EditorWindow(windowManager, theme, fonts);
          openFileMap[keyCnt] = (source) -> window.open(source, node.name());
          sendOpenFile(node, left);
        }
      }

      @Override
      public void sendModel() {
        var result = FrontendMessage.serialize(
            leftRoot,
            rightRoot,
            rootModel,
            searchString
        );
        result.push(DiffModelChannelUpdater.FRONTEND_MESSAGE_ARRAY);
        channel.sendMessage(result);
      }

      @Override
      public void closeDir(RemoteDirectoryNode node) {
        super.closeDir(node);
      }

      @Override
      public RemoteDirectoryNode getOppositeDir(RemoteDirectoryNode node) {
        return getOppositeDir(node.model());
      }

      @Override
      public RemoteFileNode getOppositeFile(RemoteFileNode node) {
        var dir = getOppositeDir(node.model().parent());
        if (dir == null) return null;
        return dir.findSubFile(node.name());
      }

      @Override
      public RemoteFolderDiffModel getModel() {
        return modelSupplier.get();
      }

      private Supplier<RemoteFolderDiffModel> getModel(int i) {
        return () -> getModel().child(i);
      }

      private RemoteDirectoryNode getOppositeDir(RemoteFolderDiffModel model) {
        var root = left ? rightRoot : leftRoot;
        Deque<String> deque = new LinkedList<>();
        var curModel = model;
        while (curModel != null) {
          deque.addFirst(curModel.path);
          curModel = curModel.parent();
        }
        deque.removeFirst();
        return getOpposite(root, deque);
      }

      private RemoteDirectoryNode getOpposite(RemoteDirectoryNode current, Deque<String> deque) {
        if (deque.isEmpty()) return current;
        var path = deque.removeFirst();
        var subNode = current.findSubDir(path);
        if (subNode == null) return null;
        else return getOpposite(subNode, deque);
      }

      public void setSelected(RemoteFileTreeNode node, RemoteFileTreeNode opposite) {
        if (left) {
          rootView.left.setSelected(node);
          rootView.right.setSelected(opposite);
        } else {
          rootView.right.setSelected(node);
          rootView.left.setSelected(opposite);
        }
      }
    };
  }

  private void sendOpenFile(RemoteFileNode node, boolean left) {
    var result = JsArray.create();
    var path = node.getFullPath(left ? leftRoot.name() : rightRoot.name());
    result.push(JsMemoryAccess.bufferView(new int[]{keyCnt}));
    result.push(JSString.valueOf(path));
    result.push(DiffModelChannelUpdater.OPEN_FILE_ARRAY);
    channel.sendMessage(result);
    keyCnt = (keyCnt + 1) % MAP_SIZE;
  }

  private void serializeFrontendState() {
    int leftSelected = rootView.left.selectedIndex();
    int rightSelected = rootView.right.selectedIndex();
    JsArray<JSObject> serialized = FrontendState.serialize(
        0,
        leftSelected,
        rightSelected,
        leftRoot,
        rightRoot,
        rootModel,
        searchString
    );
    FrontendState state = FrontendState.deserialize(serialized);
    System.out.println(state);
  }

  public FolderDiffRootView.Selection getSelected() {
    return getSelected(rootView.left.isFocused());
  }

  public FolderDiffRootView.Selection getSelected(boolean left) {
    var root = left ? rootView.left : rootView.right;
    if (root.selectedIndex() < 0) return null;
    var node = root.model()[root.selectedIndex()];
    if (node.isEmpty()) return new Selection(null, left, false);

    String path = getFullPath((RemoteFileTreeNode) node, left);
    boolean isFolder = node instanceof RemoteDirectoryNode;
    return new Selection(path, left, isFolder);
  }

  private String getFullPath(RemoteFileTreeNode node, boolean left) {
    var rootPath = left ? leftRoot.name() : rightRoot.name();
    return node.getFullPath(rootPath);
  }

  void leftSelectedChanged(int idx) {
    rootView.right.checkScroll(idx);
    rootView.fireSelectionChanged(getSelected(true));
  }

  void rightSelectedChanged(int idx) {
    rootView.left.checkScroll(idx);
    rootView.fireSelectionChanged(getSelected(false));
  }
}
