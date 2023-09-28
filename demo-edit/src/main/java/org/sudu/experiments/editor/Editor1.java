package org.sudu.experiments.editor;

import org.sudu.experiments.SceneApi;
import org.sudu.experiments.fonts.Fonts;
import org.sudu.experiments.math.V2i;

public class Editor1 extends Editor0 {

  static final boolean fullscreen = true;

  public Editor1(SceneApi api) {
    super(api);
    editor.setModel(new Model(
        StartFile.START_CODE_JAVA, Languages.JAVA,
        new Uri(StartFile.START_CODE_FILE)));
  }

  @Override
  protected String[] menuFonts() {
    return Fonts.editorFonts(true);
  }

  @Override
  protected void layout(V2i newSize, float dpr) {
    if (fullscreen) {
      super.layout(newSize, dpr);
    } else {
      editor.setPos(
          new V2i(newSize.x / 10, newSize.y / 10),
          new V2i(newSize.x * 8 / 10, newSize.y * 8 / 10),
          dpr);
    }
  }
}