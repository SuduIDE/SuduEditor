package org.sudu.experiments.parser.common.graph.reader;

import org.sudu.experiments.arrays.ArrayReader;
import org.sudu.experiments.parser.common.graph.node.FakeNode;
import org.sudu.experiments.parser.common.graph.node.InferenceNode;
import org.sudu.experiments.parser.common.graph.node.MemberNode;
import org.sudu.experiments.parser.common.graph.node.ScopeNode;
import org.sudu.experiments.parser.common.graph.type.Type;

import java.util.ArrayList;
import java.util.List;

import static org.sudu.experiments.parser.common.graph.ScopeGraphConstants.Nodes.*;

public class ScopeGraphReader {

  private final ArrayReader reader;
  private final char[] chars;

  private ScopeNode[] scopeNodes;
  private DeclNodeReader declNodeReader;
  private RefNodeReader refNodeReader;

  public ScopeNode root;
  public List<Type> types;

  public ScopeGraphReader(
      int[] ints,
      char[] chars
  ) {
    this.reader = new ArrayReader(ints);
    this.chars = chars;
  }

  public void readFromInts() {
    readTypes();
    readScopeRoot();
    readAssociatedScopes();
  }

  private void readTypes() {
    int len = reader.next();
    types = new ArrayList<>(len);
    readTypesNames(len);
    readSupertypes();
  }

  private void readTypesNames(int len) {
    for (int i = 0; i < len; i++) {
      String name = nextString();
      types.add(new Type(name));
    }
  }

  public void readSupertypes() {
    for (Type type: types) type.supertypes = readTypeList();
  }

  private void readScopeRoot() {
    int len = reader.next();
    scopeNodes = new ScopeNode[len];
    declNodeReader = new DeclNodeReader(reader, chars, types);
    refNodeReader = new RefNodeReader(reader, chars, types);
    root = readScope(null);
  }

  private void readAssociatedScopes() {
    for (int i = 0; i < types.size(); i++) {
      int typeInd = reader.next();
      if (typeInd == -1) continue;
      int scopeInd = reader.next();
      var type = types.get(typeInd);
      type.associatedScope = scopeNodes[scopeInd];
    }
  }

  private ScopeNode readScope(ScopeNode parent) {
    int scopeInd = reader.next();
    int scopeType = reader.next();

    var declNodeList = declNodeReader.readDeclNodes();
    ScopeNode scopeNode = switch (scopeType) {
      case FAKE_NODE -> new FakeNode(parent);
      case BASE_NODE -> new ScopeNode(parent);
      case MEMBER_NODE -> new MemberNode(parent, declNodeList);
      default -> throw new IllegalStateException("Unknown scope type: " + scopeType);
    };

    scopeNode.declarations = declNodeList;
    scopeNode.references = refNodeReader.readRefNodes();
    scopeNode.importTypes = readTypeList();
    scopeNode.inferences = readInferences();
    scopeNode.children = readChildrenScopes(scopeNode);
    scopeNodes[scopeInd] = scopeNode;
    return scopeNode;
  }

  private List<Type> readTypeList() {
    int len = reader.next();
    List<Type> result = new ArrayList<>();
    for (int i = 0; i < len; i++) {
      int typeInd = reader.next();
      result.add(types.get(typeInd));
    }
    return result;
  }

  private List<ScopeNode> readChildrenScopes(ScopeNode parent) {
    int len = reader.next();
    List<ScopeNode> result = new ArrayList<>();
    for (int i = 0; i < len; i++) result.add(readScope(parent));
    return result;
  }

  private List<InferenceNode> readInferences() {
    int len = reader.next();
    List<InferenceNode> result = new ArrayList<>();
    for (int i = 0; i < len; i++) {
      var decl = declNodeReader.readDeclNode();
      var ref = refNodeReader.readRefNode();
      result.add(new InferenceNode(decl, ref));
    }
    return result;
  }

  private String nextString() {
    int offset = reader.next(),
        count = reader.next();
    return new String(chars, offset, count);
  }

}