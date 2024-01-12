package org.sudu.experiments;

import org.sudu.experiments.editor.*;
import org.sudu.experiments.esm.*;
import org.sudu.experiments.js.*;
import org.sudu.experiments.parser.common.Pos;
import org.sudu.experiments.utils.LanguageSelectorUtils;
import org.sudu.experiments.utils.PromiseUtils;
import org.sudu.experiments.utils.ProviderUtils;
import org.sudu.experiments.worker.WorkerJobExecutor;
import org.teavm.jso.JSObject;
import org.teavm.jso.core.JSBoolean;
import org.teavm.jso.core.JSObjects;
import org.teavm.jso.core.JSString;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

import static org.sudu.experiments.js.JsHelper.jsToJava;
import static org.sudu.experiments.js.JsHelper.javaToJs;

public class JsCodeEditor0 implements JsCodeEditor {

  public static final String errorNotArray = "provided result is not an array";

  private final WebWindow window;
  private final EditorComponent editor;

  public JsCodeEditor0(EditArgs args, JsArray<WorkerContext> workers) {
    this.window = new WebWindow(
        Editor0::new,
        JsCodeEditor0::onWebGlError,
        args.getContainerId().stringValue(),
        workers);
    editor = demoEdit0().editor();
    if (args.hasTheme()) setTheme(args.getTheme());
    if (args.hasReadonly()) setReadonly(args.getReadonly());
  }

  private Editor0 demoEdit0() {
    return (Editor0) window.scene();
  }

  @Override
  public void dispose() {
    window.dispose();
  }

  @Override
  public void focus() {
    if (1<0) JsHelper.consoleInfo("setting focus to ",
        JsHelper.WithId.get(window.canvasDiv));
    window.focus();
  }

  @Override
  public void setText(JSString t) {
    Model prevModel = editor.model();
    Model model = new Model(SplitJsText.split(t), prevModel.docLanguage(), prevModel.uri);
    editor.setModel(model);
  }

  @Override
  public JSString getText() {
    char[] chars = editor.model().document.getChars();
    return TextDecoder.decodeUTF16(chars);
  }

  @Override
  public void setFontFamily(JSString fontFamily) {
    editor.changeFont(fontFamily.stringValue(), editor.getFontVirtualSize());
  }

  @Override
  public void setFontSize(int fontSize) {
    editor.changeFont(editor.getFontFamily(), fontSize);
  }

  @Override
  public void setTheme(JSString theme) {
    demoEdit0().setTheme(theme.stringValue());
  }

  @Override
  public void setModel(JsITextModel model) {
    if (model instanceof JsTextModel jsTextModel) {
      editor.setModel(jsTextModel.javaModel);
    } else {
      throw new IllegalArgumentException("bad model");
    }
  }

  @Override
  public void setPosition(JsPosition pos) {
    editor.setPosition(pos.getColumn() - 1, pos.getLineNumber() - 1);
  }

  @Override
  public JsPosition getPosition() {
    return JsPosition.fromJava(editor.caretCharPos(), editor.caretLine());
  }

  private void setSelection(JsRange sel) {
    editor.setSelection(
        sel.getEndColumn() - 1,
        sel.getEndLineNumber() - 1,
        sel.getStartColumn() - 1,
        sel.getStartLineNumber() - 1
    );
  }

  @Override
  public JsITextModel getModel() {
    return JsTextModel.fromJava(editor.model());
  }

  @Override
  public JSString getProperty(JSString key) {
    return JSString.valueOf(editor.getProperty(key.stringValue()));
  }

  @Override
  public JsDisposable registerDefinitionProvider(JSObject languageSelector, JsDefinitionProvider provider) {
    var defProvider = new DefDeclProvider(
        LanguageSelectorUtils.toSelectors(languageSelector),
        convert(provider));

    return JsDisposable.of(editor.registrations()
            .definitionProviders.disposableAdd(defProvider));
  }

  @Override
  public JsDisposable registerDeclarationProvider(JSObject languageSelector, JsDeclarationProvider provider) {
    var defProvider = new DefDeclProvider(
        LanguageSelectorUtils.toSelectors(languageSelector),
        convert(provider));

    return JsDisposable.of(editor.registrations()
            .declarationProviders.disposableAdd(defProvider));
  }

  @Override
  public JsDisposable registerReferenceProvider(JSObject languageSelector, JsReferenceProvider provider) {
    var referenceProvider = new ReferenceProvider(
        LanguageSelectorUtils.toSelectors(languageSelector), convert(provider));
    return JsDisposable.of(editor.registrations()
            .referenceProviders.disposableAdd(referenceProvider));
  }

  @Override
  public JsDisposable registerDocumentHighlightProvider(JSObject languageSelector, JsDocumentHighlightProvider provider) {
    var hlProvider = new DocumentHighlightProvider(
        LanguageSelectorUtils.toSelectors(languageSelector),
        convert(provider));

    return JsDisposable.of(editor.registrations()
        .documentHighlightProviders.disposableAdd(hlProvider));
  }

  static DefDeclProvider.Provider convert(JsDefinitionProvider provider) {
    return (model, line, column, onResult, onError) ->
        PromiseUtils.<JsArrayReader<JsLocation>>promiseOrT(
            provider.provideDefinition(
                JsTextModel.fromJava(model),
                JsPosition.fromJava(column, line),
                JsCancellationToken.create()),
            jsArr -> acceptLocations(jsArr, onResult, onError),
            onError);
  }

  static DefDeclProvider.Provider convert(JsDeclarationProvider provider) {
    return (model, line, column, onResult, onError) ->
        PromiseUtils.<JsArrayReader<JsLocation>>promiseOrT(
            provider.provideDeclaration(
                JsTextModel.fromJava(model),
                JsPosition.fromJava(column, line),
                JsCancellationToken.create()),
            jsArr -> acceptLocations(jsArr, onResult, onError),
            onError);
  }

  static ReferenceProvider.Provider convert(JsReferenceProvider provider) {
    return (model, line, column, includeDecl, onResult, onError) ->
        PromiseUtils.<JsArrayReader<JsLocation>>promiseOrT(
            provider.provideReferences(
                JsTextModel.fromJava(model),
                JsPosition.fromJava(column, line),
                JsReferenceProvider.Context.create(includeDecl),
                JsCancellationToken.create()),
            jsArr -> acceptLocations(jsArr, onResult, onError),
            onError);
  }

  static DocumentHighlightProvider.Provider convert(JsDocumentHighlightProvider provider) {
    return (model, line, column, onResult, onError) ->
        PromiseUtils.<JsArrayReader<JsDocumentHighlight>>promiseOrT(
            provider.provideDocumentHighlights(
                JsTextModel.fromJava(model),
                JsPosition.fromJava(column, line),
                JsCancellationToken.create()),
            jsArr -> acceptHighlight(jsArr, onResult, onError),
            onError);
  }

  static void acceptLocations(
      JsArrayReader<JsLocation> jsArr,
      Consumer<Location[]> c, Consumer<String> onError
  ) {
    if (JsHelper.jsIf(jsArr)) {
      if (JsArray.isArray(jsArr)) {
        c.accept(ProviderUtils.toLocations(jsArr));
      } else {
        onError.accept(errorNotArray);
      }
    } else {
      Debug.consoleInfo("provider result is undefined");
    }
  }

  static void acceptHighlight(
      JsArrayReader<JsDocumentHighlight> jsArr,
      Consumer<DocumentHighlight[]> c,
      Consumer<String> onError
  ) {
    if (JsHelper.jsIf(jsArr)) {
      if (JsArray.isArray(jsArr)) {
        c.accept(ProviderUtils.toHighlights(jsArr));
      } else {
        onError.accept(errorNotArray);
      }
    } else {
      Debug.consoleInfo("provider result is undefined");
    }
  }

  static String[] toStringArray(Object[] obj) {
    var res = new String[obj.length];
    for (int i = 0; i < obj.length; i++) {
      res[i] = obj[i].toString();
    }
    return res;
  }

  @Override
  public Promise<JsArrayReader<JSString>> executeOnWorker(JSString method, JsArrayReader<JSString> args) {
    var method0 = method.stringValue();
    var args0 = jsToJava(args);

    return Promise.create((postResult, postError) -> {
      Consumer<Object[]> consumer = (result) -> {
        postResult.f(javaToJs(toStringArray(result)));
      };
      window.sendToWorker(consumer, WorkerJobExecutor.ACTIVITY_CHANNEL,
          method0, (Object[]) args0);
    });
  }


  @Override
  public JsDisposable registerEditorOpener(JsCodeEditorOpener opener) {
    return JsDisposable.of(editor.registrations().openers.disposableAdd(
        (uri, selection, pos) -> opener.openCodeEditor(
            this, JsUri.fromJava(uri),
            selectionOrPositionToJs(selection, pos))));
  }

  static JSObject selectionOrPositionToJs(Range range, Pos pos) {
    if (range != null) return JsRange.fromJava(range);
    if (pos != null) return JsPosition.fromJava(pos);
    return JSObjects.undefined();
  }

  @Override
  public void revealLineInCenter(int line) {
    editor.revealLineInCenter(line - 1);
  }

  @Override
  public void revealLine(int line) {
    editor.revealLine(line - 1);
  }

  @Override
  public void revealPosition(JsPosition position) {
    revealLine(position.getLineNumber());
    // todo: also set hScroll position here
  }

  @Override
  public void setReadonly(JSBoolean flag) {
    editor.readonly = flag.booleanValue();
  }

  @Override
  public JsDisposable onDidChangeModel(JsFunctions.Consumer<JsIModelChangedEvent> f) {
    var listener = convert(f);
    return JsDisposable.of(editor.registrations()
            .modelChangeListeners.disposableAdd(listener));
  }

  static BiConsumer<Model, Model> convert(JsFunctions.Consumer<JsIModelChangedEvent> jsCallback) {
    return (oldModel, newModel) -> jsCallback.f(
        JsIModelChangedEvent.create(
            JsUri.fromJava(oldModel.uri),
            JsUri.fromJava(newModel.uri)
        ));
  }

  static void onWebGlError() {
    JsHelper.consoleInfo("FATAL: WebGL is not enabled in the browser");
  }

  static Promise<JsCodeEditor> newEdit(EditArgs arguments) {
    if (JsCanvas.checkFontMetricsAPI()) {
      return Promise.create((postResult, postError) ->
          WorkerContext.start(
              worker -> postResult.f(new JsCodeEditor0(arguments, worker)),
              postError,
              arguments.workerUrl(),
              arguments.numWorkerThreads()));
    } else {
      return Promise.reject(FireFoxWarning.message);
    }
  }


}
