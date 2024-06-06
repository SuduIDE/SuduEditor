package org.sudu.experiments.js.node;

import org.sudu.experiments.js.JsArray;
import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;
import org.teavm.jso.core.JSString;
import org.teavm.jso.typedarrays.ArrayBufferView;

interface NodeFs extends JSObject {
  @JSProperty("constants") Constants constants();

  // https://nodejs.org/api/fs.html#fsconstants
  interface Constants extends JSObject {
    @JSProperty("O_RDONLY") int O_RDONLY();
    @JSProperty("O_RDWR") int O_RDWR();
    @JSProperty("O_CREAT") int O_CREAT();
    @JSProperty("O_EXCL") int O_EXCL();
    @JSProperty("O_APPEND") int O_APPEND();
  }

  // https://nodejs.org/api/fs.html#class-fsstats
  interface Stats extends JSObject {
    boolean isDirectory();
    boolean isFile();
    @JSProperty("size") double size();
  }

  Stats lstatSync(JSString name);
  Stats lstatSync(JSString name, JSObject options);

  interface Dirent extends JSObject {
    boolean isDirectory();
    boolean isFile();
    @JSProperty("name") JSString name();
  }

  JsArray<JSString> readdirSync(JSString string);
  JsArray<JSString> readdirSync(JSString string, JSObject options);

  int openSync(JSString name, int mode);
  int readSync(
      int handle,
      ArrayBufferView buffer, int bufferOffset,
      int bytesToRead, double position);
  void closeSync(int handle);
}


public abstract class Fs implements NodeFs {
  @JSBody(script = "return fs")
  public static native Fs fs();

  @JSBody(script = "return {throwIfNoEntry: false};")
  public static native JSObject lStatNoThrow();
}
