package org.sudu.experiments;

import org.sudu.experiments.js.*;
import org.sudu.experiments.js.node.*;
import org.sudu.experiments.update.DiffModelChannelUpdater;
import org.sudu.experiments.diff.folder.ItemFolderDiffModel;
import org.sudu.experiments.update.FileDiffChannelUpdater;
import org.sudu.experiments.update.FileEditChannelUpdater;
import org.teavm.jso.JSObject;
import org.teavm.jso.core.JSString;

public class DiffEngine implements DiffEngineJs {
  public static final boolean debug = false;

  final NodeWorkersPool pool;

  DiffEngine(JsArray<NodeWorker> worker) {
    pool = new NodeWorkersPool(worker);
  }

  @Override
  public void dispose() {
    JsHelper.consoleInfo("DiffEngine.dispose");
    pool.terminateAll();
    SshPool.terminate();
  }

  @Override
  public JsFolderDiffSession startFolderDiff(
      JSObject leftPath, JSObject rightPath, Channel channel
  ) {
    LoggingJs.info("Starting folder diff");
    boolean scanFileContent = true;
    DirectoryHandle leftDir = JsFolderInput.directoryHandle(leftPath);
    DirectoryHandle rightDir = JsFolderInput.directoryHandle(rightPath);

    if (leftDir == null)
      throw new IllegalArgumentException(
          "illegal leftPath argument " + JsHelper.jsToString(leftPath));
    if (rightDir == null)
      throw new IllegalArgumentException(
          "illegal leftPath argument " + JsHelper.jsToString(leftPath));

    LoggingJs.info(JsHelper.concat("  DiffEngine LeftPath: ", leftPath));
    LoggingJs.info(JsHelper.concat("  DiffEngine RightPath: ", rightPath));

    ItemFolderDiffModel root = new ItemFolderDiffModel(null, "");
    root.setItems(leftDir, rightDir);

    DiffModelChannelUpdater updater = new DiffModelChannelUpdater(
        root,
        scanFileContent,
        pool, channel
    );
    updater.beginCompare();
    return new JsFolderDiffSession0(updater);
  }

  public JsFileDiffSession startFileDiff(
      JSObject leftInput, JSObject rightInput,
      Channel channel,
      JsFolderDiffSession parent
  ) {
    LoggingJs.info("Starting new file diff ...");

    boolean isLeftFile = JsFileInput.hasPath(leftInput);
    boolean isRightFile = JsFileInput.hasPath(rightInput);
    boolean isLeftText = JsFileInputContent.isInstance(leftInput);
    boolean isRightText = JsFileInputContent.isInstance(rightInput);
    boolean validatedLeft = isLeftFile ^ isLeftText;
    boolean validatedRight = isRightFile ^ isRightText;

    if (!validatedLeft)
      LoggingJs.error(JsHelper.concat(
          "startFileDiff: left input is invalid ", leftInput));

    if (!validatedRight)
      LoggingJs.error(JsHelper.concat(
          "startFileDiff: right input is invalid ", rightInput));

    if (!validatedLeft || !validatedRight)
      return null;

    JSString leftStr = isLeftFile
        ? JsFileInputFile.getPath(leftInput)
        : JsFileInputContent.getContent(leftInput);

    JSString rightStr = isRightFile
        ? JsFileInputFile.getPath(rightInput)
        : JsFileInputContent.getContent(rightInput);

    LoggingJs.info(JsHelper.concat("  left: ", leftStr));
    LoggingJs.info(JsHelper.concat("  right: ", rightStr));

    LoggingJs.info("  parent instanceof JsFolderDiffSession0: " +
        (parent instanceof JsFolderDiffSession0));

    DiffModelChannelUpdater parentUpdater =
        JsHelper.jsIf(parent) ? ((JsFolderDiffSession0) parent).updater : null;

    FileDiffChannelUpdater updater
        = new FileDiffChannelUpdater(channel, parentUpdater, pool);

    if (isLeftFile) {
      dfdf FileHandle leftHandle = new NodeFileHandle(leftStr);
      updater.compareLeft(leftHandle);
    } else {
      updater.sendFileRead(true, leftStr, null);
    }
    if (isRightFile) {
      dfdf FileHandle rightHandle = new NodeFileHandle(rightStr);
      updater.compareRight(rightHandle);
    } else {
      updater.sendFileRead(false, rightStr, null);
    }
    return new JsFileDiffSession0();
  }

  @Override
  public JsFileDiffSession startFileEdit(JSString input, Channel channel) {
    JsHelper.consoleInfo("Starting file edit ...");

    boolean isFile = JsFileInput.hasPath(input);

    JSString str = isFile
        ? JsFileInputFile.getPath(input)
        : JsFileInputContent.getContent(input);

    JsHelper.consoleInfo("  input: ", str);

    FileEditChannelUpdater updater = new FileEditChannelUpdater(channel);
    if (isFile) {
      FileHandle handle = new NodeFileHandle(str);
      updater.beginCompare(handle);
    } else {
      updater.sendMessage(str, null);
    }
    return new JsFileDiffSession0();
  }

  @Override
  public JsDiffTestApi testApi() {
    return debug ? new DiffTestApi(pool) : null;
  }
}
