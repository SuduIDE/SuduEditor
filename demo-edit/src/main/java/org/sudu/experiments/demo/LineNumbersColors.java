package org.sudu.experiments.demo;

import org.sudu.experiments.math.Color;
import org.sudu.experiments.math.V4f;

class LineNumbersColors {

  public final V4f textColor;
  public final V4f bgColor;
  public final V4f caretTextColor;
  public final V4f caretBgColor;

  static LineNumbersColors darcula() {
    return new LineNumbersColors(
        new Color("#606366"),
        new Color("#2B2B2B"),
        new Color("#A4A3A3"),
        new Color("#323232")
    );
  }

  // TODO: get colors
  static LineNumbersColors dark() {
    return new LineNumbersColors(
        new Color(""),
        new Color(""),
        new Color(""),
        new Color("")
    );
  }

  static LineNumbersColors light() {
    return new LineNumbersColors(
        new Color("#AEB3C2"),
        new Color("#FFFFFF"),
        new Color("#767A8A"),
        new Color("#F5F8FE")
    );
  }

  LineNumbersColors(
      V4f textColor, V4f bgColor,
      V4f caretTextColor, V4f caretBgColor
  ) {
    this.textColor = textColor;
    this.bgColor = bgColor;
    this.caretTextColor = caretTextColor;
    this.caretBgColor = caretBgColor;
  }
}