package org.sudu.experiments.update;

import org.sudu.experiments.DirectoryHandle;
import org.sudu.experiments.FileHandle;
import org.sudu.experiments.FsItem;
import org.sudu.experiments.diff.DiffTypes;
import org.sudu.experiments.diff.ItemKind;
import org.sudu.experiments.diff.SizeScanner;
import org.sudu.experiments.diff.folder.RemoteFolderDiffModel;
import org.sudu.experiments.editor.worker.ArgsCast;
import org.sudu.experiments.editor.worker.diff.DiffUtils;
import org.sudu.experiments.js.JsHelper;
import org.sudu.experiments.math.ArrayOp;
import org.sudu.experiments.protocol.BackendMessage;
import org.sudu.experiments.protocol.FrontendMessage;
import org.sudu.experiments.worker.ArrayView;
import org.sudu.experiments.worker.WorkerJobExecutor;

import java.util.Arrays;
import java.util.function.Consumer;

public class RemoteCollector {

  private final RemoteFolderDiffModel root;
  private final DirectoryHandle leftHandle, rightHandle;

  private final WorkerJobExecutor executor;
  private final boolean scanFileContent;

  private Consumer<Object[]> sendResult;
  private Consumer<Object[]> onComplete;

  private static final int CMP_SIZE = 1250;
  private int inComparing = 0;
  private int compared = 0;

  private boolean firstMessageSent = false;

  private FrontendMessage lastFrontendMessage;

  public RemoteCollector(
      RemoteFolderDiffModel root,
      DirectoryHandle leftHandle,
      DirectoryHandle rightHandle,
      boolean scanFileContent,
      WorkerJobExecutor executor
  ) {
    this.root = root;
    this.leftHandle = leftHandle;
    this.rightHandle = rightHandle;
    this.executor = executor;
    this.scanFileContent = scanFileContent;
  }

  public void beginCompare() {
    compare(root, leftHandle, rightHandle);
  }

  public void onMessageGot(FrontendMessage message) {
    lastFrontendMessage = message;
    JsHelper.consoleInfo("Got frontend message:");
    JsHelper.consoleInfo(message.toString());
    sendResult();
  }

  private void compare(
      RemoteFolderDiffModel model,
      FsItem leftItem,
      FsItem rightItem
  ) {
    ++inComparing;
    if (leftItem instanceof DirectoryHandle leftDir &&
        rightItem instanceof DirectoryHandle rightDir
    ) compareFolders(model, leftDir, rightDir);
    else if (leftItem instanceof FileHandle leftFile
        && rightItem instanceof FileHandle rightFile
    ) compareFiles(model, leftFile, rightFile);
    else throw new IllegalArgumentException();
  }

  private void compareFolders(
      RemoteFolderDiffModel model,
      DirectoryHandle leftDir,
      DirectoryHandle rightDir
  ) {
    executor.sendToWorker(
        result -> onFoldersCompared(model, result),
        DiffUtils.CMP_FOLDERS,
        leftDir, rightDir
    );
  }

  private void onFoldersCompared(
      RemoteFolderDiffModel model,
      Object[] result
  ) {
    if (result.length == 0) return;
    int[] ints = ((ArrayView) result[0]).ints();

    int commonLen = ints[0];
    int leftLen = ints[1];
    int rightLen = ints[2];

    int[] diffs = Arrays.copyOfRange(ints, 3, 3 + commonLen);
    FsItem[] leftItem = Arrays.copyOfRange(result, 1, 1 + leftLen, FsItem[].class);
    FsItem[] rightItem = Arrays.copyOfRange(result, 1 + leftLen, 1 + leftLen + rightLen, FsItem[].class);

    int len = diffs.length;
    model.children = new RemoteFolderDiffModel[len];
    model.childrenComparedCnt = 0;

    int lP = 0, rP = 0;
    int mP = 0;
    boolean edited = false;

    while (mP < len) {
      if (diffs[mP] == DiffTypes.DELETED) {
        edited = true;
        model.children[mP] = new RemoteFolderDiffModel(model, leftItem[lP].getName());
        int kind = leftItem[lP] instanceof DirectoryHandle
            ? ItemKind.FOLDER
            : ItemKind.FILE;
        model.child(mP).setItemKind(kind);
        model.child(mP).setDiffType(DiffTypes.DELETED);
        read(model.child(mP), leftItem[lP], DiffTypes.DELETED);
        mP++;
        lP++;
      } else if (diffs[mP] == DiffTypes.INSERTED) {
        edited = true;
        model.children[mP] = new RemoteFolderDiffModel(model, rightItem[rP].getName());
        int kind = rightItem[rP] instanceof DirectoryHandle
            ? ItemKind.FOLDER
            : ItemKind.FILE;
        model.child(mP).setItemKind(kind);
        model.child(mP).setDiffType(DiffTypes.INSERTED);
        read(model.child(mP), rightItem[rP], DiffTypes.INSERTED);
        mP++;
        rP++;
      } else {
        model.children[mP] = new RemoteFolderDiffModel(model, leftItem[lP].getName());
        int kind = leftItem[lP] instanceof DirectoryHandle
            ? ItemKind.FOLDER
            : ItemKind.FILE;
        model.child(mP).setItemKind(kind);
        compare(model.child(mP), leftItem[lP], rightItem[rP]);
        mP++;
        lP++;
        rP++;
      }
    }
    if (len == 0) model.itemCompared();
    if (edited) model.markUp(DiffTypes.EDITED);
    onItemCompared();
  }

  private void compareFiles(
      RemoteFolderDiffModel model,
      FileHandle leftFile,
      FileHandle rightFile
  ) {
    if (scanFileContent) {
      executor.sendToWorker(
          result -> onFilesCompared(model, result),
          DiffUtils.CMP_FILES,
          leftFile, rightFile
      );
    } else {
      new SizeScanner(leftFile, rightFile) {
        @Override
        protected void onComplete(int sizeL, int sizeR) {
          onFilesCompared(model, sizeL == sizeR);
        }
      };
    }
  }

  private void onFilesCompared(
      RemoteFolderDiffModel model,
      Object[] result
  ) {
    boolean equals = ArgsCast.intArray(result, 0)[0] == 1;
    onFilesCompared(model, equals);
  }

  private void onFilesCompared(
      RemoteFolderDiffModel model,
      boolean equals
  ) {
    if (!equals) model.markUp(DiffTypes.EDITED);
    model.itemCompared();
    onItemCompared();
  }

  private void read(
      RemoteFolderDiffModel model,
      FsItem handle,
      int diffType
  ) {
    if (handle instanceof DirectoryHandle dirHandle) readFolder(model, dirHandle, diffType);
    else model.itemCompared();
  }

  private void readFolder(
      RemoteFolderDiffModel model,
      DirectoryHandle dirHandle,
      int diffType
  ) {
    ++inComparing;
    executor.sendToWorker(
        result -> onFolderRead(model, result),
        DiffUtils.READ_FOLDER,
        dirHandle, new int[]{diffType}
    );
  }

  private void onFolderRead(
      RemoteFolderDiffModel model,
      Object[] result
  ) {
    int[] ints = ArgsCast.intArray(result, 0);
    String[] paths = new String[result.length - 1];
    for (int i = 0; i < paths.length; i++) paths[i] = (String) result[i + 1];
    var updModel = RemoteFolderDiffModel.fromInts(ints, paths);
    model.update(updModel);
    onItemCompared();
  }

  private void onItemCompared() {
    ++compared;
    if (--inComparing < 0) throw new IllegalStateException("inComparing cannot be negative");
    if (inComparing == 0) onComplete();
    else sendResult();
  }

  private void sendResult() {
    if (firstMessageSent) return;
    if (sendResult != null && compared % CMP_SIZE == 0) {
      firstMessageSent = true;
      send(sendResult);
    }
  }

  private void onComplete() {
    if (onComplete != null) send(onComplete);
  }

  private void send(Consumer<Object[]> send) {
    String leftRootName = leftHandle.getName();
    String rightRootName = rightHandle.getName();
    ArrayOp.sendArrayList(
        BackendMessage.serialize(root, leftRootName, rightRootName),
        send
    );
  }

  public void setSendResult(Consumer<Object[]> sendResult) {
    this.sendResult = sendResult;
  }

  public void setOnComplete(Consumer<Object[]> onComplete) {
    this.onComplete = onComplete;
  }
}