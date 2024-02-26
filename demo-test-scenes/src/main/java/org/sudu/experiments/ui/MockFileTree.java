package org.sudu.experiments.ui;

import org.sudu.experiments.math.XorShiftRandom;

public class MockFileTree {
  public static FileTreeNode randomFolder(String n, int maxD, Runnable update) {
    return makeFolder(n, 0, maxD, update, new XorShiftRandom());
  }

  static FileTreeNode makeFolder(String n, int d, int maxD, Runnable update, XorShiftRandom r) {
    int folders = d < maxD ? 1 + r.poissonNumber(FileViewDemo.foldersAverage - 1) : 0;
    int files = d <= maxD ? 1 + r.poissonNumber(FileViewDemo.filesAverage - 1) : 0;

    FileTreeNode[] ch = new FileTreeNode[folders + files];
    for (int i = 0; i < folders; i++) {
      ch[i] = makeFolder("Folder " + i, d + 1, maxD, update, r);
    }

    for (int i = 0; i < files; i++) {
      FileTreeNode f = new FileTreeNode("ClassFile " + i, d);
      f.onDblClick(() ->
          System.out.println("open file " + f.name())
      );

      if (r.nextFloat() < .25f) f.setBold(true);
      ch[folders + i] = f;
    }

    FileTreeNode folder = new FileTreeNode(n, d, ch);
    folder.toggleOnCLick(update, FileViewDemo.folderDoubleClick);

    if (d + d <= maxD) folder.open();
    else folder.close();

    return folder;
  }
}
