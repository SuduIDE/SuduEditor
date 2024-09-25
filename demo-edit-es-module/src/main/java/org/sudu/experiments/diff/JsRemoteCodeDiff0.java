package org.sudu.experiments.diff;

import org.sudu.experiments.*;
import org.sudu.experiments.esm.EditArgs;
import org.sudu.experiments.esm.JsITextModel;
import org.sudu.experiments.esm.JsTextModel;
import org.sudu.experiments.esm.ThemeImport;
import org.sudu.experiments.js.JsDisposable;
import org.sudu.experiments.js.JsFunctions;
import org.sudu.experiments.js.JsHelper;
import org.sudu.experiments.js.Promise;
import org.teavm.jso.JSObject;
import org.teavm.jso.core.JSObjects;
import org.teavm.jso.core.JSString;

import java.util.function.Function;

public class JsRemoteCodeDiff0 implements JsRemoteCodeDiff {

  public final WebWindow window;
  private FileDiffWindow w;

  final JsFileDiffViewController0 controller;

  public JsRemoteCodeDiff0(
      WebWindow ww,
      EditArgs args
  ) {
    this.window = ww;
    this.w = ((RemoteFileDiffScene) window.scene()).w;
    controller = new JsFileDiffViewController0(w);
    if (args.hasTheme()) setTheme(args.getTheme());
    if (args.hasReadonly())
      setReadonly(args.getReadonly(), args.getReadonly());
  }

  @Override
  public final void dispose() {
    window.dispose();
    w = null;
  }

  @Override
  public void disconnectFromDom() {
    window.disconnectFromDom();
  }

  @Override
  public void reconnectToDom(JSString containedId) {
    window.connectToDom(containedId);
  }

  @Override
  public void focus() {
    if (1<0)
      JsHelper.consoleInfo("setting focus to ", window.canvasDivId());
    window.focus();
  }

  @Override
  public void setReadonly(boolean leftReadonly, boolean rightReadonly) {
    w.rootView.setReadonly(leftReadonly, rightReadonly);
  }

  @Override
  public void setFontFamily(JSString fontFamily) {
    w.rootView.setFontFamily(fontFamily.stringValue());
  }

  @Override
  public void setFontSize(float fontSize) {
    w.rootView.setFontSize(fontSize);
  }

  @Override
  public void setTheme(JSObject theme) {
    var t = ThemeImport.fromJs(theme);
    if (t != null)
      w.applyTheme(t);
  }

  @Override
  public void setLeftModel(JsITextModel model) {
    if (model instanceof JsTextModel jsTextModel) {
      w.rootView.setLeftModel(jsTextModel.javaModel);
    } else if (JSObjects.isUndefined(model)) {
      throw new IllegalArgumentException("left model is undefined");
    } else {
      throw new IllegalArgumentException("bad left model");
    }
  }

  @Override
  public void setRightModel(JsITextModel model) {
    if (model instanceof JsTextModel jsTextModel) {
      w.rootView.setRightModel(jsTextModel.javaModel);
    } else if (JSObjects.isUndefined(model)) {
      throw new IllegalArgumentException("right model is undefined");
    } else {
      throw new IllegalArgumentException("bad right model");
    }
  }

  @Override
  public JsITextModel getLeftModel() {
    return JsTextModel.fromJava(w.rootView.getLeftModel());
  }

  @Override
  public JsITextModel getRightModel() {
    return JsTextModel.fromJava(w.rootView.getRightModel());
  }

  @Override
  public JsFileDiffViewController getController() {
    return controller;
  }

  @Override
  public JsDisposable onControllerUpdate(
      JsFunctions.Consumer<JsFileDiffViewController> callback
  ) {
    return JsDisposable.empty();
  }

  static Function<SceneApi, Scene> sf(Channel channel) {
    return api -> new RemoteFileDiffScene(api, channel);
  }

  public static Promise<JsRemoteCodeDiff> create(
      EditArgs arguments, Channel channel
  ) {
    return JsLauncher.start(
        arguments,
        sf(channel),
        JsRemoteCodeDiff0::new
    );
  }
}