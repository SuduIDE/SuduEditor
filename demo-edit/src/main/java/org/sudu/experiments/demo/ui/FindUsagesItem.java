package org.sudu.experiments.demo.ui;

import org.sudu.experiments.demo.EditorColorScheme;
import org.sudu.experiments.demo.TextRect;

public class FindUsagesItem {
  final TextRect tFiles = new TextRect();
  final TextRect tLines = new TextRect();
  final TextRect tContent = new TextRect();
  final Runnable action;
  FindUsagesItemColors colors;
  String fileName, lineNumber, codeContent;
  boolean isHover = false;

  public FindUsagesItem(Runnable r, String fileName, String lineNumber, String codeContent, FindUsagesItemColors colors) {
    this.fileName = fileName;
    this.lineNumber = lineNumber;
    this.codeContent = codeContent;
    this.colors = colors;
    action = r;
    tFiles.color.set(colors.fileColor);
    tFiles.bgColor.set(colors.bgColor);
    tLines.color.set(colors.lineColor);
    tLines.bgColor.set(colors.bgColor);
    tContent.color.set(colors.contentColor);
    tContent.bgColor.set(colors.bgColor);
  }

  public void setHover(boolean b) {
    tFiles.bgColor.set(b ? colors.bgHighlight : colors.bgColor);
    tLines.bgColor.set(b ? colors.bgHighlight : colors.bgColor);
    tContent.bgColor.set(b ? colors.bgHighlight : colors.bgColor);
    isHover = b;
  }

  public void setTheme(EditorColorScheme scheme) {
      colors = scheme.dialogItemColors.findUsagesColors;
      tFiles.setColors(
          scheme.dialogItemColors.findUsagesColors.fileColor,
          scheme.dialogItemColors.findUsagesColors.bgColor
      );
      tLines.setColors(
          scheme.dialogItemColors.findUsagesColors.lineColor,
          scheme.dialogItemColors.findUsagesColors.bgColor
      );
      tContent.setColors(
          scheme.dialogItemColors.findUsagesColors.contentColor,
          scheme.dialogItemColors.findUsagesColors.bgColor
      );
      if (isHover) setHover(true);
  }
}
