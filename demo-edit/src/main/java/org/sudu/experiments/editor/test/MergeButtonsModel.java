package org.sudu.experiments.editor.test;

import org.sudu.experiments.diff.DiffTypes;
import org.sudu.experiments.diff.folder.FolderDiffModel;
import org.sudu.experiments.editor.worker.diff.DiffInfo;
import org.sudu.experiments.editor.worker.diff.DiffRange;
import org.sudu.experiments.math.Numbers;
import org.sudu.experiments.math.XorShiftRandom;

import java.util.function.BiConsumer;

public class MergeButtonsModel {
  public int[] lines;
  public Runnable[] actions;

  public MergeButtonsModel(int n) {
    actions = new Runnable[n];
    lines = new int[n];
  }

  public static MergeButtonsModel[] getModels(DiffInfo diffInfo, BiConsumer<DiffRange, Boolean> applyDiff) {
    int n = 0;
    for (var range: diffInfo.ranges) if (range.type != DiffTypes.DEFAULT) n++;

    var left = new MergeButtonsModel(n);
    var right = new MergeButtonsModel(n);
    int i = 0;
    for (var range: diffInfo.ranges) {
      if (range.type == DiffTypes.DEFAULT) continue;
      left.lines[i] = line(range.fromL, diffInfo.lineDiffsL.length);
      left.actions[i] = () -> applyDiff.accept(range, true);
      right.lines[i] = line(range.fromR, diffInfo.lineDiffsR.length);
      right.actions[i] = () -> applyDiff.accept(range, false);
      i++;
    }
    return new MergeButtonsModel[]{left, right};
  }

  public static MergeButtonsModel[] getFolderModels(
      DiffInfo diffInfo,
      FolderDiffModel[] leftDiffs,
      FolderDiffModel[] rightDiffs,
      byte[] leftColors,
      byte[] rightColors,
      BiConsumer<FolderDiffModel, Boolean> applyDiff
  ) {
    int n = 0, m = 0;
    for (var line: diffInfo.lineDiffsL) if (line.type != DiffTypes.DEFAULT) n++;
    for (var line: diffInfo.lineDiffsR) if (line.type != DiffTypes.DEFAULT) m++;

    var left = new MergeButtonsModel(n);
    for (int lineInd = 0, modelInd = 0; lineInd < diffInfo.lineDiffsL.length; lineInd++) {
      var leftLine = diffInfo.lineDiffsL[lineInd];
      if (leftLine.type == DiffTypes.DEFAULT) continue;
      left.lines[modelInd] = line(lineInd, diffInfo.lineDiffsL.length);
      var leftModel = leftDiffs[lineInd];
      left.actions[modelInd] = () -> applyDiff(leftModel, true, applyDiff);
      leftColors[lineInd] = DiffTypes.FOLDER_ALIGN_DIFF_TYPE;
      modelInd++;
    }

    var right = new MergeButtonsModel(m);
    for (int lineInd = 0, modelInd = 0; lineInd < diffInfo.lineDiffsR.length; lineInd++) {
      var rightLine = diffInfo.lineDiffsR[lineInd];
      if (rightLine.type == DiffTypes.DEFAULT) continue;
      right.lines[modelInd] = line(lineInd, diffInfo.lineDiffsR.length);
      var rightModel = rightDiffs[lineInd];
      right.actions[modelInd] = () -> applyDiff(rightModel, false, applyDiff);
      rightColors[lineInd] = DiffTypes.FOLDER_ALIGN_DIFF_TYPE;
      modelInd++;
    }
    return new MergeButtonsModel[]{left, right};
  }

  private static void applyDiff(
      FolderDiffModel model,
      boolean left,
      BiConsumer<FolderDiffModel, Boolean> applyDiff
  ) {
    if (applyDiff == null) return;
    applyDiff.accept(model, left);
  }

  private static int line(int line, int docLen) {
    return Numbers.clamp(0, line, docLen - 1);
  }

  public static class TestModel extends MergeButtonsModel {
    public TestModel(int docLines) {
      super(docLines);
      int n = docLines / 4;
      XorShiftRandom rand = new XorShiftRandom();
      int space = docLines / (1 + n);
      for (int i = 0, pi = 0; i < n; i++) {
        lines[i] = pi;
        actions[i] = action(pi);
        pi += 1 + rand.nextInt(space);
      }
    }
    static Runnable action(int pi) {
      return () -> System.out.println("Runnable #" + pi);
    }
  }
}
