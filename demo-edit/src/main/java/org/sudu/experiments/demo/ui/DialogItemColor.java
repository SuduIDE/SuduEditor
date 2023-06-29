package org.sudu.experiments.demo.ui;

import org.sudu.experiments.math.Color;
import org.sudu.experiments.math.V4f;

public class DialogItemColor {

  public final FindUsagesItemColors findUsagesColors;
  public final FindUsagesItemColors findUsagesColorsContinued;
  public final FindUsagesItemColors findUsagesColorsError;
  public final Color findUsagesColorBorder;

  public final ToolbarItemColors toolbarItemColors;

  public static DialogItemColor darkColorScheme() {
    return new DialogItemColor(
        FindUsagesItemColors.darkFindUsagesItemColors(),
        FindUsagesItemColors.darkFindUsagesItemColorsExtraLine(),
        FindUsagesItemColors.darkFindUsagesItemColorsError(),
        new Color("#616161"),
        ToolbarItemColors.darkToolbarItemColors()
    );
  }

  public static DialogItemColor lightColorScheme() {
    return new DialogItemColor(
        FindUsagesItemColors.lightFindUsagesItemColors(),
        FindUsagesItemColors.lightFindUsagesItemColorsExtraLine(),
        FindUsagesItemColors.lightFindUsagesItemColorsError(),
        new Color("#B9BDC9"),
        ToolbarItemColors.lightToolbarItemColors()
    );
  }

  public DialogItemColor(
      FindUsagesItemColors findUsagesItemColors,
      FindUsagesItemColors findUsagesColorsContinued,
      FindUsagesItemColors findUsagesColorsError,
      Color findUsagesColorsBorder,
      ToolbarItemColors toolbarItemColors
  ) {
    this.findUsagesColors = findUsagesItemColors;
    this.findUsagesColorsContinued = findUsagesColorsContinued;
    this.findUsagesColorsError = findUsagesColorsError;
    this.findUsagesColorBorder = findUsagesColorsBorder;
    this.toolbarItemColors = toolbarItemColors;
  }
}
