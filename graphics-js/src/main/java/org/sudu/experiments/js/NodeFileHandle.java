package org.sudu.experiments.js;

import org.sudu.experiments.FileHandle;
import org.sudu.experiments.FsItem;
import org.teavm.jso.JSBody;
import org.teavm.jso.core.JSError;
import org.teavm.jso.core.JSObjects;
import org.teavm.jso.core.JSString;
import org.teavm.jso.typedarrays.ArrayBuffer;

import java.util.Arrays;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

public class NodeFileHandle implements FileHandle {

  final int fileHandle;
  final String[] path;

  public NodeFileHandle(int h, String[] path) {
    this.fileHandle = h;
    this.path = path;
  }

  @Override
  public void getSize(IntConsumer result) {

  }

  private int jsFileSize() {
    double jsFileSize = jsFile.getSize();
    return intSize(jsFileSize);
  }

  private int intSize(double jsSize) {
    int result = (int) jsSize;
    if (result != jsSize) {
      JsHelper.consoleInfo("File is too large: " + getName(), jsSize);
      return 0;
    }
    return result;
  }

  @Override
  public String getName() {
    return jsName().stringValue();
  }

  private JSString jsName() {
//    return fileHandle != null ? fileHandle.getName() : jsFile.getName();
  }

  @Override
  public String[] getPath() {
    return path;
  }

  @Override
  public void readAsText(Consumer<String> consumer, Consumer<String> onError) {
//    JsFunctions.Consumer<JSError> onJsError = wrapError(onError);
//    JsFunctions.Consumer<JSString> onString = jsString
//        -> consumer.accept(jsString.stringValue());
//    if (jsFile != null) {
//      jsFile.text().then(onString, onJsError);
//    } else {
//      fileHandle.getFile().then(
//          file -> file.text().then(onString, onJsError), onJsError);
//    }
  }

  @Override
  public void readAsBytes(
      Consumer<byte[]> consumer, Consumer<String> onError,
      int begin, int length
  ) {
//    JsFunctions.Consumer<JSError> onJsError = wrapError(onError);
//    JsFunctions.Consumer<ArrayBuffer> onBuffer = toJava(consumer);
//    if (jsFile != null) {
//      readBlob(begin, length, onBuffer, onJsError, jsFile);
//    } else {
//      fileHandle.getFile().then(
//          file -> readBlob(begin, length, onBuffer, onJsError, file),
//          onJsError
//      );
//    }
  }

  private void readBlob(
      int begin, int length,
      JsFunctions.Consumer<ArrayBuffer> onBuffer,
      JsFunctions.Consumer<JSError> onJsError, JsFile file
  ) {
    JsBlob blob = length < 0 ? begin == 0 ? file : file.slice(begin)
        : file.slice(begin, begin + length);
    blob.arrayBuffer().then(onBuffer, onJsError);
  }

  static JsFunctions.Consumer<ArrayBuffer> toJava(Consumer<byte[]> consumer) {
    return jsArrayBuffer -> consumer.accept(
        JsMemoryAccess.toByteArray(jsArrayBuffer));
  }

  static JsFunctions.Consumer<JSError> wrapError(Consumer<String> onError) {
    return jsError -> onError.accept(jsError.getMessage());
  }

  @Override
  public String toString() {
    return jsFile != null
        ? FsItem.toString(getClass().getSimpleName(),
            path, getName(), false)
        : FsItem.fullPath(path, getName());
  }

  @Override
  public int hashCode() {
    return Objects.hashCode(getName()) * 31 + Arrays.hashCode(path);
  }
}

