package org.sudu.experiments.ui.window;

import org.sudu.experiments.Disposable;
import org.sudu.experiments.DprUtil;
import org.sudu.experiments.WglGraphics;
import org.sudu.experiments.input.MouseEvent;
import org.sudu.experiments.math.Color;
import org.sudu.experiments.math.V2i;
import org.sudu.experiments.ui.ScrollBar;
import org.sudu.experiments.ui.SetCursor;
import org.sudu.experiments.ui.UiContext;

import java.util.function.Consumer;

public class ScrollView extends View {

  private final SetCursor windowCursor;
  private final Consumer<ScrollBar.Event> vScrollHandler = this::onMoveScrollV;
  private final Consumer<ScrollBar.Event> hScrollHandler = this::onMoveScrollH;

  private ScrollContent content;
  private ScrollBar vScroll, hScroll;
  private float scrollWidth = 10;
  private Color scrollBarLineToSet, scrollBarBgToSet;

  public ScrollView(UiContext uiContext) {
    this(new ScrollContent(), uiContext.windowCursor);
  }

  public ScrollView(ScrollContent content, UiContext uiContext) {
    this(content, uiContext.windowCursor);
  }

  public ScrollView(ScrollContent content, SetCursor setCursor) {
    this.content = content;
    windowCursor = setCursor;
    content.setScrollView(this);
  }

  @Override
  public void dispose() {
    content.setScrollView(null);
    content = Disposable.assign(content, null);
  }

  public void setContent(ScrollContent content) {
    this.content = Disposable.assign(this.content, content);
    content.setScrollView(this);
    this.content.setPosition(pos, size, dpr);
    if (dpr != 0) {
      updateVirtualSize();
    }
  }

  public void setScrollColor(Color scrollBarLine, Color scrollBarBg) {
    this.scrollBarLineToSet = scrollBarLine;
    this.scrollBarBgToSet = scrollBarBg;
    if (vScroll != null) vScroll.setColor(scrollBarLine, scrollBarBg);
    if (hScroll != null) hScroll.setColor(scrollBarLine, scrollBarBg);
  }

  public ScrollContent content() {
    return content;
  }

  protected void setPosition(V2i newPos, V2i newSize, float newDpr) {
    super.setPosition(newPos, newSize, newDpr);
    content.setPosition(newPos, newSize, newDpr);
    if (newDpr != 0) updateVirtualSize();
  }

  public void setScrollWidth(float scrollWidth) {
    this.scrollWidth = scrollWidth;
  }

  public void setScrollPos(int x, int y) {
    content.setScrollPos(x, y);
    if (needHScroll()) layoutHScroll();
    if (needVScroll()) layoutVScroll();
  }

  private void updateVirtualSize() {
    content.updateVirtualSize();
    content.limitScrollPos();

    if (needHScroll()) layoutHScroll();
    else hScroll = null;

    if (needVScroll()) layoutVScroll();
    else vScroll = null;
  }

  private boolean needVScroll() {
    return size.y > 0 && content.virtualSize.y > size.y;
  }

  private boolean needHScroll() {
    return size.x > 0 && content.virtualSize.x > size.x;
  }

  private void layoutHScroll() {
    ensureHScroll().layoutHorizontal(
        content.scrollPos.x,
        pos.x, size.x, content.virtualSize.x,
        pos.y + size.y,
        scrollWidthPx());
  }

  private void layoutVScroll() {
    ensureVScroll().layoutVertical(
        content.scrollPos.y,
        pos.y, size.y, content.virtualSize.y,
        pos.x + size.x,
        scrollWidthPx());
  }

  private int scrollWidthPx() {
    return DprUtil.toPx(scrollWidth, dpr);
  }

  private ScrollBar ensureHScroll() {
    hScroll = (hScroll != null) ? hScroll : new ScrollBar();
    ensureColor(hScroll);
    return hScroll;
  }

  private ScrollBar ensureVScroll() {
    vScroll = (vScroll != null) ? vScroll : new ScrollBar();
    ensureColor(vScroll);
    return vScroll;
  }

  private void ensureColor(ScrollBar scrollBar) {
    scrollBar.setColorIfNotSame(scrollBarLineToSet, scrollBarBgToSet);
  }

  protected void draw(WglGraphics graphics) {
    content.draw(graphics);
    if (vScroll != null || hScroll != null) {
      graphics.enableBlend(true);
      if (vScroll != null) vScroll.drawBg(graphics);
      if (hScroll != null) hScroll.drawBg(graphics);
      if (vScroll != null) vScroll.drawButton(graphics);
      if (hScroll != null) hScroll.drawButton(graphics);
      graphics.enableBlend(false);
    }
  }

  private boolean scrollHitTest(V2i position) {
    return vScroll != null && vScroll.hitTest(position)
        || hScroll != null && hScroll.hitTest(position);
  }

  protected boolean onMouseClick(MouseEvent event, int button, int clickCount) {
    return scrollHitTest(event.position)
        || content.onMouseClick(event, button, clickCount);
  }

  private void onMoveScrollV(ScrollBar.Event event) {
    int vScrollPos = event.getPosition(content.virtualSize.y - size.y);
    content.setScrollPosY(vScrollPos);
    layoutVScroll();
  }

  private void onMoveScrollH(ScrollBar.Event event) {
    int hScrollPos = event.getPosition(content.virtualSize.x - size.x);
    content.setScrollPosX(hScrollPos);
    layoutHScroll();
  }

  protected Consumer<MouseEvent> onMouseDown(MouseEvent event, int button) {
    if (vScroll != null) {
      var lock = vScroll.onMouseDown(event.position, vScrollHandler, true);
      if (lock != null) return lock;
    }
    if (hScroll != null) {
      var lock = hScroll.onMouseDown(event.position, hScrollHandler, false);
      if (lock != null) return lock;
    }
    return content.onMouseDown(event, button);
  }

  protected boolean onMouseUp(MouseEvent event, int button) {
    return scrollHitTest(event.position)
        || content.onMouseUp(event, button);
  }

  protected boolean onMouseMove(MouseEvent event) {
    boolean a = vScroll != null &&
        vScroll.onMouseMove(event.position, windowCursor);
    boolean b = hScroll != null &&
        hScroll.onMouseMove(event.position, windowCursor);
    return a || b || content.onMouseMove(event);
  }

  boolean onScroll(MouseEvent event, float dX, float dY) {
    if (!hitTest(event.position)) return false;

    int changeY = DprUtil.toPx(dY * .25f, dpr);
    int changeX = DprUtil.toPx(dX * .25f, dpr);

    if (event.shift) {
      changeX += changeY;
      changeY = 0;
    }

    if (vScroll != null && changeY != 0) {
      content.setScrollPosY(content.scrollPos.y + changeY);
      layoutVScroll();
    }

    if (hScroll != null && changeX != 0) {
      content.setScrollPosX(content.scrollPos.x + changeX);
      layoutHScroll();
    }

    return true;
  }

  @Override
  protected V2i minimalSize() {
    int sizePx = scrollWidthPx();
    V2i minimalSize = content.minimalSize();
    minimalSize.x = Math.max(minimalSize.x, sizePx);
    minimalSize.y = Math.max(minimalSize.y, sizePx);
    return minimalSize;
  }
}