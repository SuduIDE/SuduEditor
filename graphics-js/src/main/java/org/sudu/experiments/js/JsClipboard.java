package org.sudu.experiments.js;


import org.teavm.jso.JSBody;
import org.teavm.jso.JSObject;
import org.teavm.jso.core.JSObjects;
import org.teavm.jso.core.JSString;

public abstract class JsClipboard implements JSObject {

  public static String noClipboardDefined() {
    return "navigator.clipboard is undefined";
  }

  @JSBody(script = "return navigator;")
  public static native JSObject navigator();

  public static boolean hasClipboard() {
    return JSObjects.hasProperty(navigator(), "clipboard");
  }

  @JSBody(script = "return navigator.clipboard;")
  public static native JsClipboard get();

  public static boolean isReadTextSupported() {
    return hasClipboard() &&
        JSObjects.hasProperty(get(), "readText");
  }

  public abstract Promise<?> writeText(JSString text);

  public abstract Promise<JSString> readText();
}
