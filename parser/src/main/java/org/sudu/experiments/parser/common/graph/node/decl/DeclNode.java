package org.sudu.experiments.parser.common.graph.node.decl;

import org.sudu.experiments.parser.common.Name;
import org.sudu.experiments.parser.common.graph.node.ref.RefNode;
import org.sudu.experiments.parser.common.graph.type.TypeMap;

import java.util.Objects;

/**
 * int a = 10;
 * var b = 11;
 */
public class DeclNode {

  public Name decl;
  public String type;
  public int declType;
  public static final int LOCAL_VAR = 1;
  public static final int ARGUMENT = 2;
  public static final int FIELD = 3;
  public static final int CALLABLE = 4;
  public static final int TYPE = 5;


  public DeclNode(Name decl, String type, int declType) {
    this.decl = decl;
    this.type = type;
    this.declType = declType;
  }

  public boolean match(RefNode ref, TypeMap typeMap) {
    if (ref.refType == RefNode.TYPE && declType == TYPE) return ref.ref.match(decl);
    boolean nameMatch = ref.ref == null || ref.ref.match(decl);
    boolean typeMatch = ref.type == null || typeMap.matchType(type, ref.type);
    if (!(nameMatch && typeMatch)) return false;
    if (declType == LOCAL_VAR) {
      return ref.ref != null && decl.position < ref.ref.position;
    }
    return true;
  }

  @Override
  public String toString() {
    return decl.toString() + ": " + type;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;
    DeclNode declNode = (DeclNode) o;
    return Objects.equals(decl, declNode.decl)
        && Objects.equals(type, declNode.type)
        && Objects.equals(declType, declNode.declType);
  }

  @Override
  public int hashCode() {
    return Objects.hash(decl, type, declType);
  }
}
