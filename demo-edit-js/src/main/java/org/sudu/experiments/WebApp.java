package org.sudu.experiments;

import org.sudu.experiments.demo.*;
import org.sudu.experiments.demo.wasm.WasmDemo;
import org.sudu.experiments.demo.worker.WorkerTest;
import org.sudu.experiments.fonts.JetBrainsMono;
import org.sudu.experiments.js.*;
import org.teavm.jso.JSObject;
import org.teavm.jso.browser.Window;
import org.teavm.jso.core.JSArrayReader;
import org.teavm.jso.core.JSError;

public class WebApp {

  public static final String preDiv = "panelDiv";

  boolean fontsLoaded;
  WorkerContext workerStarted;

  public static void main(String[] args) {
    JsTests.testArray();
    JsTests.testPromise();

    if (JsCanvas.checkFontMetricsAPI()) {
      WebApp webApp = new WebApp();
      WorkerContext.start(webApp::setWorker, "teavm/worker.js");
      FontFace.loadFonts(JetBrainsMono.webConfig())
          .then(webApp::loadFonts, WebApp::fontLoadError);
    } else {
      FireFoxWarning.display(preDiv);
    }
  }

  void loadFonts(JSArrayReader<JSObject> fontFaces) {
    for (int i = 0; i < fontFaces.getLength(); i++) {
      FontFace font = fontFaces.get(i).cast();
      font.addToDocument();
    }
    onFontsLoad();
  }

  private static void fontLoadError(JSError error) {
    JsHelper.consoleInfo("font load error", error);
  }

  private void onFontsLoad() {
    fontsLoaded = true;
    if (workerStarted != null) startApp(workerStarted);
  }

  private void setWorker(WorkerContext worker) {
    workerStarted = worker;
    if (fontsLoaded) startApp(workerStarted);
  }

  static void startApp(WorkerContext worker) {
    var window = new WebWindow(
        WebApp::createScene,
        WebApp::onWebGlError,
        "canvasDiv", worker);
    window.focus();
  }

  static Scene createScene(SceneApi api) {
    String name = Window.current().getLocation().getHash();
    return switch (name) {
      case "" -> new DemoEdit0(api);
      case "#wasm" -> new WasmDemo(api);
      default -> TestSceneSelector.selectScene(name.substring(1)).apply(api);
    };
  }

  static void onWebGlError() {
    JsHelper.addPreText(preDiv, "FATAL: WebGL is not enabled in the browser");
  }
}
