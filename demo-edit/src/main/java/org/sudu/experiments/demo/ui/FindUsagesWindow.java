package org.sudu.experiments.demo.ui;

import org.sudu.experiments.Const;
import org.sudu.experiments.WglGraphics;
import org.sudu.experiments.demo.Colors;
import org.sudu.experiments.demo.EditorComponent;
import org.sudu.experiments.demo.EditorConst;
import org.sudu.experiments.demo.Model;
import org.sudu.experiments.demo.SetCursor;
import org.sudu.experiments.fonts.FontDesk;
import org.sudu.experiments.math.V2i;
import org.sudu.experiments.math.V4f;
import org.sudu.experiments.parser.common.Pos;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Supplier;

public class FindUsagesWindow {
  private final V2i windowSize = new V2i();
  public final ArrayList<FindUsagesDialog> usagesList = new ArrayList<>();
  private final WglGraphics graphics;
  private final V4f frameColor = Colors.findUsagesBorder;
  private double dpr;
  private FontDesk font;
  private V4f bgColor = Colors.findUsagesBg;
  private Runnable onClose = Const.emptyRunnable;

  public FindUsagesWindow(WglGraphics graphics) {
    this.graphics = graphics;
  }

  static void setScreenLimitedPosition(FindUsagesDialog findUsages, int x, int y, V2i screen) {
    findUsages.setPos(
        Math.max(0, Math.min(x, screen.x - findUsages.size().x)),
        Math.max(0, Math.min(y, screen.y - findUsages.size().y)));
  }

  // todo: change font and size if dps changed on
  public void setTheme(FontDesk f, V4f bg) {
    font = f;
    bgColor = bg;
  }

  private FindUsagesDialog displayFindUsagesMenu(V2i pos, Supplier<FindUsagesItem[]> items) {
    FindUsagesDialog findUsages = new FindUsagesDialog();
    findUsages.setItems(items.get());
    setFindUsagesStyle(findUsages);
    findUsages.measure(graphics.mCanvas, dpr);
    setScreenLimitedPosition(findUsages, pos.x, pos.y, windowSize);

    findUsages.onEnter((mouse, index, item) -> removeUsageWindowAfter(findUsages));

    usagesList.add(findUsages);
    return findUsages;
  }

  public void display(V2i mousePos, Supplier<FindUsagesItem[]> actions, Runnable onClose) {
    if (font == null || isVisible()) {
      throw new IllegalArgumentException();
    }
    this.onClose = onClose;
    FindUsagesDialog usagesMenu = displayFindUsagesMenu(mousePos, actions);
    usagesMenu.onClickOutside(this::hide);
  }

  public boolean hide() {
    if (isVisible()) {
      removeUsageWindowAfter(null);
      onClose.run();
      onClose = Const.emptyRunnable;
      return true;
    }
    return false;
  }

  private void setFindUsagesStyle(FindUsagesDialog fu) {
    fu.setFont(font);
    fu.setBgColor(bgColor);
    fu.setFrameColor(frameColor);
  }

  public void onResize(V2i newSize, double newDpr) {
    windowSize.set(newSize);
    if (this.dpr != newDpr) {
      for (FindUsagesDialog usages : usagesList) {
        usages.measure(graphics.mCanvas, newDpr);
      }
      this.dpr = newDpr;
    }
  }

  public void paint() {
    // let's do 0-garbage rendering
    if (!usagesList.isEmpty()) graphics.enableBlend(true);
    for (FindUsagesDialog findUsages : usagesList) {
      findUsages.render(graphics, dpr);
    }
  }

  public boolean onMouseMove(V2i mouse, SetCursor windowCursor) {
    boolean r = false;
    for (int i = usagesList.size() - 1; i >= 0; --i) {
      r = usagesList.get(i).onMouseMove(mouse, windowCursor);
      if (r) break;
    }
    return r;
  }

  public boolean onMousePress(V2i position, int button, boolean press, int clickCount) {
    boolean r = false;
    for (int i = usagesList.size() - 1; i >= 0; --i) {
      r = usagesList.get(i).onMousePress(position, button, press, clickCount);
      if (r) break;
    }
    return r;
  }

  private void removeUsageWindowAfter(FindUsagesDialog wall) {
    for (int i = usagesList.size() - 1; i >= 0; i--) {
      FindUsagesDialog tb = usagesList.get(i);
      if (wall == tb) break;
      usagesList.remove(i);
      tb.dispose();
    }
  }

  private void disposeList(ArrayList<FindUsagesDialog> list) {
    for (FindUsagesDialog toolbar : list) {
      toolbar.dispose();
    }
    list.clear();
  }

  public boolean isVisible() {
    return usagesList.size() > 0;
  }

  public void dispose() {
    disposeList(usagesList);
  }

  public final Supplier<FindUsagesItem[]> buildUsagesItems(List<Pos> usages, EditorComponent editorComponent, Model model) {
    FindUsagesItemBuilder tbb = new FindUsagesItemBuilder();
    int cnt = 0;
    int maxFileNameLen = 0;
    int maxLineLen = 0;
    int maxCodeContentLen = 0;
    for (var pos : usages) {
      // TODO(Get file names from server)
      String fileName = "Main.java";
      String codeContent = model.document.line(pos.line).makeString().trim();
      String codeContentFormatted = codeContent.length() > 43 ? codeContent.substring(0, 40) + "..." : codeContent;
      String lineNumber = String.valueOf(pos.line + 1);
      // noinspection DataFlowIssue
      maxFileNameLen = Math.max(fileName.length(), maxFileNameLen);
      maxLineLen = Math.max(lineNumber.length(), maxLineLen);
      maxCodeContentLen = Math.max(codeContentFormatted.length(), maxCodeContentLen);
    }
    for (var pos : usages) {
      // TODO(Get file names from server)
      String fileName = formatFindUsagesItem(
          "Main.java",
          maxFileNameLen
      );
      String codeContent = model.document.line(pos.line).makeString().trim();
      String codeContentFormatted = formatFindUsagesItem(
          codeContent.length() > 43 ? codeContent.substring(0, 40) + "..." : codeContent,
          maxCodeContentLen
      );
      String lineNumber = formatFindUsagesItem(
          String.valueOf(pos.line + 1),
          maxLineLen
      );

      if (++cnt > EditorConst.MAX_SHOW_USAGES_NUMBER) {
        tbb.addItem(
            "... and " + (usages.size() - (cnt - 1)) + " more usages",
            "",
            "",
            Colors.findUsagesColorsContinued,
            () -> {
            }
        );
        break;
      }
      tbb.addItem(
          fileName,
          lineNumber,
          codeContentFormatted,
          Colors.findUsagesColors,
          () -> editorComponent.gotoUsageMenuElement(pos)
      );
    }
    return tbb.supplier();
  }

  private String formatFindUsagesItem(String item, int maxLength) {
    StringBuilder s = new StringBuilder();
    for (int i = 0; i < Math.max(0, maxLength - item.length()); ++i) {
      s.append(" ");
    }
    return item + s;
  }
}
