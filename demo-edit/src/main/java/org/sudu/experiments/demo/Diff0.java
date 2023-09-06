package org.sudu.experiments.demo;

import org.sudu.experiments.FileHandle;
import org.sudu.experiments.SceneApi;
import org.sudu.experiments.demo.ui.Focusable;
import org.sudu.experiments.demo.ui.colors.EditorColorScheme;
import org.sudu.experiments.fonts.Fonts;
import org.sudu.experiments.input.*;
import org.sudu.experiments.math.V2i;

import java.util.Objects;
import java.util.function.Consumer;

public class Diff0 extends Scene1
    implements MouseListener, EditorUi.ThemeApi, InputListeners.ScrollHandler
{

  final EditorComponent editor1;
  final EditorComponent editor2;
  final EditorUi ui;

  public Diff0(SceneApi api) {
    super(api);

    ui = new EditorUi(uiContext);
    editor1 = new EditorComponent(uiContext, ui);
    editor2 = new EditorComponent(uiContext, ui);
    uiContext.initFocus(editor1);

    api.input.onMouse.add(ui);

    api.input.onScroll.add(ui);
    api.input.onScroll.add(this);

    api.input.onMouse.add(this);

    api.input.onKeyPress.add(this::onKeyPress);
    api.input.onKeyPress.add(new CtrlO(api, this::openFile));

//    api.input.onCopy.add(editor::onCopy);
//    api.input.onPaste.add(() -> editor::handleInsert);
    api.input.onContextMenu.add(this::onContextMenu);

    toggleDark();
  }

  private void openFile(FileHandle handle) {
    EditorComponent activeEditor = getActiveEditor();
    if (activeEditor != null) activeEditor.openFile(handle);
  }

  private EditorComponent getActiveEditor() {
    if (uiContext.isFocused(editor1)) return editor1;
    if (uiContext.isFocused(editor2)) return editor2;
    return null;
  }

  @Override
  public void dispose() {
    ui.dispose();
    editor1.dispose();
    editor2.dispose();
  }

  @Override
  public boolean update(double timestamp) {
    boolean u1 = editor1.update(timestamp);
    boolean u2 = editor2.update(timestamp);
    return u1 || u2;
  }

  @Override
  public void paint() {
    super.paint();
    editor1.paint();
    editor2.paint();
    ui.paint();
  }

  protected String[] menuFonts() { return Fonts.editorFonts(false); }

  @Override
  public void onResize(V2i newSize, float newDpr) {
    super.onResize(newSize, newDpr);
    layout(newSize, newDpr);
  }

  protected void layout(V2i newSize, float dpr) {
    V2i pos = new V2i();
    V2i size = new V2i(newSize.x / 2, newSize.y);
    editor1.setPos(pos, size, dpr);
    pos.x = newSize.x - newSize.x / 2;
    editor2.setPos(pos, size, dpr);
  }

  public void toggleDarcula() {
    applyTheme(EditorColorScheme.darculaIdeaColorScheme());
  }

  public void toggleDark() {
    applyTheme(EditorColorScheme.darkIdeaColorScheme());
  }

  public void toggleLight() {
    applyTheme(EditorColorScheme.lightIdeaColorScheme());
  }

  private void applyTheme(EditorColorScheme theme) {
    Objects.requireNonNull(theme);
    ui.setTheme(theme);
    editor1.setTheme(theme);
    editor2.setTheme(theme);
  }

  boolean onKeyPress(KeyEvent event) {
    if (event.keyCode == KeyCode.F10) {
      api.window.addChild("child", Diff0::new);
      return true;
    }
    return false;
  }

  boolean onContextMenu(MouseEvent event) {
    if (uiContext.isFocused(editor1)) {
      ui.showContextMenu(event, editor1, Diff0.this, Diff0.this::menuFonts);
    }
    if (uiContext.isFocused(editor2)) {
      ui.showContextMenu(event, editor2, Diff0.this, Diff0.this::menuFonts);
    }
    return true;
  }

  @Override
  public boolean onMouseMove(MouseEvent event) {
    boolean b1 = editor1.hitTest(event.position) && editor1.onMouseMove(event);
    boolean b2 = editor2.hitTest(event.position) && editor2.onMouseMove(event);
    return b1 || b2;
  }

  @Override
  public boolean onMouseClick(MouseEvent event, int button, int clickCount) {
    boolean b1 = editor1.hitTest(event.position) &&
        editor1.onMouseClick(event, button, clickCount);
    boolean b2 = editor2.hitTest(event.position) &&
        editor2.onMouseClick(event, button, clickCount);
    return b1 || b2;
  }

  @Override
  public Consumer<MouseEvent> onMouseDown(MouseEvent event, int button) {
    boolean hit1 = editor1.hitTest(event.position);
    boolean hit2 = editor2.hitTest(event.position);
    Focusable focused = uiContext.focused();
    boolean noFocus = focused == null;
    boolean focus1 = focused == editor1;
    boolean focus2 = focused == editor2;

    if (hit1 && (noFocus || focus2)) {
      uiContext.setFocus(editor1);
    }
    if (hit2 && (noFocus || focus1)) {
      uiContext.setFocus(editor2);
    }

    if (hit1) {
      Consumer<MouseEvent> lock = editor1.onMouseDown(event, button);
      if (lock != null) return lock;
    }

    return hit2 ? editor2.onMouseDown(event, button) : null;
  }

  @Override
  public boolean onMouseUp(MouseEvent event, int button) {
    boolean hit1 = editor1.hitTest(event.position);
    boolean hit2 = editor2.hitTest(event.position);
    boolean b1 = hit1 && editor1.onMouseUp(event, button);
    boolean b2 = hit2 && editor2.onMouseUp(event, button);
    return b1 || b2;
  }

  @Override
  public boolean onScroll(MouseEvent event, float dX, float dY) {
    boolean hit1 = editor1.hitTest(event.position);
    boolean hit2 = editor2.hitTest(event.position);
    boolean b1 = hit1 && editor1.onScroll(dX, dY);
    boolean b2 = hit2 && editor2.onScroll(dX, dY);
    return b1 || b2;
  }
}