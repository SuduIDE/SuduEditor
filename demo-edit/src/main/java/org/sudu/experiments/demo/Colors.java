package org.sudu.experiments.demo;

import org.sudu.experiments.math.Color;
import org.sudu.experiments.math.V4f;

interface Colors {
  Color editBgColor = new Color(43);
  Color selectionBg = new Color("#214283");
  Color editCurrentLineBg = new Color(50);
  Color defaultTextC = new Color("#A9B7C6");


  static CodeElementColor makeDefault(Color color) {
    return new CodeElementColor(color, editBgColor, selectionBg);
  }

  static CodeElementColor makeDefault(Color color, Color bgColor) {
    return new CodeElementColor(color, bgColor, selectionBg);
  }

  Color editNumbersVLine = new Color(85);
  Color editFooterFill = new Color(60, 63, 65);
  V4f white = new V4f(1, 1, 1, 1);
  V4f black = new V4f(0, 0, 0, 1);

  V4f scrollBarBody1 = Color.Cvt.fromRGBA(50, 50, 50, 100);
  V4f scrollBarBody2 = Color.Cvt.fromRGBA(80, 80, 80, 200);
  V4f toolbarBg = Color.Cvt.fromRGBA(40, 40, 40, 200);
  V4f toolbarTextBg = Color.Cvt.fromRGBA(40, 40, 40, 128);
  V4f toolbarTextBg2 = Color.Cvt.fromRGBA(70, 70, 70, 200);

  Toolbar.ButtonColors toolbarText2 = new Toolbar.ButtonColors(
      new Color("#6897BB").v4f, toolbarTextBg, toolbarTextBg2);
  Toolbar.ButtonColors toolbarText3 = new Toolbar.ButtonColors(
      new Color("#629755").v4f, toolbarTextBg, toolbarTextBg2);
  Toolbar.ButtonColors toolbarTextWhite = new Toolbar.ButtonColors(
      new V4f(white), toolbarTextBg, toolbarTextBg2);

  static Toolbar.ButtonColors rngToolButton() {
    return new Toolbar.ButtonColors(
        Color.Cvt.fromHSV(Math.random(), 1, 1), toolbarTextBg, toolbarTextBg2);
  }

}
