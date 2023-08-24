package org.sudu.experiments.demo.worker.diff;

import org.sudu.experiments.demo.CodeLine;
import org.sudu.experiments.demo.Document;
import org.sudu.experiments.parser.common.Pos;

import java.util.ArrayList;
import java.util.List;

public class ElemDiff {

  public List<Pos> diffN, diffM;

  public ElemDiff() {
    diffN = new ArrayList<>();
    diffM = new ArrayList<>();
  }

  @Override
  public String toString() {
    return diffN + " --> " + diffM;
  }

}
