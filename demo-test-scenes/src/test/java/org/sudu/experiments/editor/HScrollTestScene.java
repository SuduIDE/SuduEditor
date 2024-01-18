package org.sudu.experiments.editor;

import org.sudu.experiments.*;
import org.sudu.experiments.editor.ui.colors.EditorColorScheme;
import org.sudu.experiments.editor.ui.colors.IdeaCodeColors;
import org.sudu.experiments.fonts.FontDesk;
import org.sudu.experiments.fonts.Fonts;
import org.sudu.experiments.input.MouseEvent;
import org.sudu.experiments.input.MouseListener;
import org.sudu.experiments.math.Color;
import org.sudu.experiments.math.V2i;
import org.sudu.experiments.math.V4f;
import org.sudu.experiments.ui.ScrollBar;
import org.sudu.experiments.ui.SetCursor;

import java.util.ArrayList;
import java.util.function.Consumer;

public class HScrollTestScene extends Scene {

  final TestGraphics g = new TestGraphics();

  final int MAX_NUM = 150;
  final int dx = 50;
  final int fontSize = 20;
  final int horizontalSize = dx * MAX_NUM;
  final V2i viewportSize = new V2i();

  ScrollBar scrollBar = new ScrollBar();
  int scrollPosH = 0;

  CodeLineRenderer codeLineRenderer;
  CodeLine codeLine;
  TestCanvas renderCanvas;
  FontDesk[] fontDesk = new FontDesk[1];

  boolean needsUpdate = true;

  final EditorColorScheme colors = EditorColorScheme.darculaIdeaColorScheme();

  public HScrollTestScene(SceneApi api) {
    super(api);

    api.input.onMouse.add(new HScrollInputListener());
    api.input.onScroll.add(this::onMouseWheel);

    CodeElement[] codeElements = new CodeElement[]{
      new CodeElement("Первое слово", 0),
      new CodeElement("Второе", 0),
      new CodeElement("3-е", 0),
      new CodeElement("Слово номер четыре", 0),
      new CodeElement("Lorem ipsum dolor sit amet, ", 0),
      new CodeElement("consectetur adipiscing elit, ", 0),
      new CodeElement("sed do eiusmod tempor incididunt ut labore et dolore magna aliqua sed do eiusmod tempor incididunt ut labore et dolore magna aliqua", 0),
      new CodeElement("Ut enim ad minim veniam", 0),
      new CodeElement("3-е", 0),
      new CodeElement("Слово номер четыре", 0),
      new CodeElement("Lorem ipsum dolor sit amet, ", 0),
      new CodeElement("consectetur adipiscing elit, ", 0),
      new CodeElement("Ut enim ad minim veniam", 0),
      new CodeElement("3-е", 0),
      new CodeElement("Слово номер четыре", 0),
      new CodeElement("Lorem ipsum dolor sit amet, ", 0),
      new CodeElement("consectetur adipiscing elit, ", 0)
    };

    codeLine = new CodeLine(codeElements);
    codeLineRenderer = new CodeLineRenderer(new CodeLineRenderer.Context(fontDesk, false));

    fontDesk[0] = g.fontDesk(Fonts.Consolas, fontSize);
    g.mCanvas.setFont(fontDesk[0]);

    renderCanvas = (TestCanvas) g.createCanvas(EditorConst.TEXTURE_WIDTH, fontDesk[0].iSize);
  }

  @Override
  public boolean update(double timestamp) {
    return false;
  }

  Color error = new Color(188, 63, 60);
  V4f debugColor = new Color("#CC7832");
  V4f debugColorBg = new Color("#A9B7C6");
  ArrayList<CodeElement> usages = new ArrayList<>();

  @Override
  public void paint() {
    g.clear(IdeaCodeColors.Darcula.editBg);
    g.enableBlend(true);
    scrollBar.layoutHorizontal(scrollPosH, 0, viewportSize.x, horizontalSize, viewportSize.y, 20);
    scrollBar.draw(g);
    g.enableBlend(false);

    codeLineRenderer.updateTextureOnScroll(renderCanvas, fontDesk[0].iSize, scrollPosH);

    codeLineRenderer.draw(200, 0, g,
        viewportSize.x, fontSize, scrollPosH, colors, null,
        null, usages, false, false, null);

    drawDebug(300, 0);

    if (needsUpdate) {
      codeLineRenderer.updateTexture(codeLine, renderCanvas, g, fontDesk[0].iSize, viewportSize.x, scrollPosH);
      needsUpdate = false;
    }

    g.drawRect(scrollPosH, 0, new V2i(1, viewportSize.y), error);

    Debug.consoleInfo("hScrollPos: " + scrollPosH);
    Debug.consoleInfo("lineMeasure: " + codeLine.lineMeasure());
    Debug.consoleInfo("textureWidth: " + EditorConst.TEXTURE_WIDTH);
    Debug.consoleInfo("Canvas: ");
    renderCanvas.debug();
    Debug.consoleInfo("Graphics:");
    g.testContext().debug();
    Debug.consoleInfo("Renderer:");
    codeLineRenderer.debug();
    Debug.consoleInfo("____________________");
  }

  void drawDebug(int yPosition, int dx) {
    CodeLineRenderer clr = codeLineRenderer;
    for (int i = 0, n = clr.numOfTextures; i < n; i++) {
      var texture = clr.lineTextures.get(n);
      g.drawText(dx, yPosition + (fontSize + 5) * i,
          new V2i(CodeLineRenderer.TEXTURE_WIDTH, fontSize),
          new V4f(0, 0, CodeLineRenderer.TEXTURE_WIDTH, fontSize),
          texture, debugColor, debugColorBg, clr.context.cleartype);
    }
  }

  @Override
  public void onResize(V2i size, float dpr) {
    viewportSize.set(size);
  }

  @Override
  public void dispose() {
    g.dispose();
    renderCanvas.dispose();
    codeLineRenderer.dispose();
  }

  boolean onMouseWheel(MouseEvent event, double dX, double dY) {
    int change = (Math.abs((int) dX) + 64) / 2;
    int changeX = (int) Math.signum(dX);
    int changeY = (int) Math.signum(dY);
    scrollPosH = clampScrollPos(scrollPosH + change * (changeX + changeY));

    return true;
  }

  private class HScrollInputListener implements MouseListener {
    Consumer<ScrollBar.Event> vScrollHandler =
      event -> scrollPosH = event.getPosition(horizontalSize());


    @Override
    public Consumer<MouseEvent> onMouseDown(MouseEvent event, int button) {
      if (button == MouseListener.MOUSE_BUTTON_LEFT) {
        return scrollBar.onMouseDown(event.position, vScrollHandler, false);
      }

      return null;
    }

    @Override
    public boolean onMouseUp(MouseEvent event, int button) {
      return true;
    }

    @Override
    public boolean onMouseMove(MouseEvent event) {
      return scrollBar.onMouseMove(event.position, SetCursor.wrap(api.window));
    }
  }

  int clampScrollPos(int pos) {
    return Math.min(Math.max(0, pos), horizontalSize());
  }

  int horizontalSize() {
    return horizontalSize - viewportSize.x;
  }

}
