package org.sudu.experiments.parser.common.graph.node.ref;

import org.sudu.experiments.parser.common.graph.type.TypeMap;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class ExprRefNode extends RefNode {

  public static final int BASE_EXPRESSION = 7;
  public static final int ARRAY_INDEX = 8;

  public List<RefNode> refNodes;

  public ExprRefNode(List<RefNode> refNodes) {
    this(refNodes, BASE_EXPRESSION);
  }

  public ExprRefNode(List<RefNode> refNodes, int refType) {
    this(refNodes, refNodes.isEmpty() || refNodes.get(0) == null ? null : refNodes.get(0).type, refType);
  }

  public ExprRefNode(List<RefNode> refNodes, String type) {
    this(refNodes, type, BASE_EXPRESSION);
  }

  public ExprRefNode(List<RefNode> refNodes, String type, int refType) {
    super(null, null, refType);
    this.refNodes = new ArrayList<>();
    for (var expr: refNodes) {
      if (expr instanceof ExprRefNode exprRef) {
        this.refNodes.addAll(exprRef.refNodes);
      } else {
        this.refNodes.add(expr);
      }
    }
    this.type = type != null && refType == ARRAY_INDEX
        ? TypeMap.getArrayElemType(type) : type;
  }

  @Override
  public String toString() {
    return refNodes.stream().map(it -> refNodes.toString()).collect(Collectors.joining(", ", "", ""));
  }
}
