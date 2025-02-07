package org.sudu.experiments;

import org.teavm.jso.JSObject;
import org.teavm.jso.JSProperty;
import org.teavm.jso.core.JSString;

// export type FileInput = { path: string }

public interface JsFileInputFile extends JsFileInput {

  static JSString getPath(JSObject input) {
    return input.<JsFileInputFile>cast().getPath();
  }

  @JSProperty
  JSString getPath();
}
