package org.sudu.experiments.js.node;

import org.sudu.experiments.js.JsArrayReader;
import org.sudu.experiments.js.JsFunctions.BiConsumer;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;
import org.teavm.jso.core.JSError;
import org.teavm.jso.core.JSString;


// https://github.com/mscdex/ssh2/blob/master/SFTP.md
public interface JsSftpClient extends JSObject {

  interface Attrs extends JSObject {
    @JSProperty
    int getMode();
    @JSProperty
    int getSize();
    @JSProperty
    int getAtime();
    @JSProperty
    int getMtime();

    boolean isDirectory();
    boolean isFile();
    boolean isSymbolicLink();
  }

  interface DirEntry extends JSObject {
    @JSProperty
    JSString getFilename();
    @JSProperty
    Attrs getAttrs();
  }

  void readdir(
      JSString path,
      BiConsumer<JSError, JsArrayReader<DirEntry>> callback);
}
