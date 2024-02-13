package org.sudu.experiments.ui.window;

import org.sudu.experiments.WglGraphics;
import org.sudu.experiments.input.MouseEvent;
import org.sudu.experiments.math.V2i;

import java.util.function.Consumer;

public abstract class ViewArray extends View {

  protected View[] views;

  protected ViewArray(View ... views) {
    this.views = views;
  }

  @Override
  public void dispose() {
    for (View view : views) {
      view.dispose();
    }
  }

  @Override
  public void draw(WglGraphics g) {
    for (View view : views) {
      view.draw(g);
    }
  }

  protected boolean update(double timestamp) {
    boolean r = false;
    for (View view : views) {
      r |= view.update(timestamp);
    }
    return r;
  }

  @Override
  public void setPosition(V2i newPos, V2i newSize, float newDpr) {
    super.setPosition(newPos, newSize, newDpr);
    layoutViews();
  }

  protected abstract void layoutViews();

  @Override
  protected boolean onMouseClick(MouseEvent event, int button, int clickCount) {
    return super.onMouseClick(event, button, clickCount);
  }

  @Override
  protected Consumer<MouseEvent> onMouseDown(MouseEvent event, int button) {
    for (View view : views) {
      if (view.hitTest(event.position)) {
        var onMouseDown = view.onMouseDown(event, button);
        if (onMouseDown != null) return onMouseDown;
      }
    }
    return null;
  }

  @Override
  protected boolean onMouseUp(MouseEvent event, int button) {
    boolean result = false;
    for (View view : views) {
      if (view.hitTest(event.position)) {
        result |= view.onMouseUp(event, button);
      }
    }
    return result;
  }

  @Override
  protected boolean onMouseMove(MouseEvent event) {
    boolean result = false;
    for (View view : views) {
      if (view.hitTest(event.position)) {
        result |= view.onMouseMove(event);
      }
    }
    return result;
  }

  @Override
  protected boolean onScroll(MouseEvent event, float dX, float dY) {
    boolean result = false;
    for (View view : views) {
      if (view.hitTest(event.position)) {
        result |= view.onScroll(event, dX, dY);
      }
    }
    return result;
  }
}
