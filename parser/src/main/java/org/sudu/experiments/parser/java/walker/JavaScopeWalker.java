package org.sudu.experiments.parser.java.walker;

import org.antlr.v4.runtime.tree.ErrorNode;
import org.antlr.v4.runtime.tree.ParseTree;
import org.antlr.v4.runtime.tree.TerminalNode;
import org.sudu.experiments.parser.common.Name;
import org.sudu.experiments.parser.common.tree.IntervalNode;
import org.sudu.experiments.parser.common.graph.ScopeWalker;
import org.sudu.experiments.parser.common.graph.node.InferenceNode;
import org.sudu.experiments.parser.common.graph.node.decl.DeclNode;
import org.sudu.experiments.parser.common.graph.node.decl.MethodNode;
import org.sudu.experiments.parser.common.graph.node.ref.*;
import org.sudu.experiments.parser.common.graph.type.TypeMap;
import org.sudu.experiments.parser.java.gen.JavaParser;
import org.sudu.experiments.parser.java.gen.JavaParserBaseListener;

import static org.sudu.experiments.parser.ParserConstants.IntervalTypes.Java.*;
import static org.sudu.experiments.parser.ParserConstants.IntervalTypes.UNKNOWN;
import static org.sudu.experiments.parser.ParserConstants.*;

import java.util.ArrayList;
import java.util.List;

public class JavaScopeWalker extends JavaParserBaseListener {

  public ScopeWalker scopeWalker;
  public int offset = 0;
  private int[] tokenTypes, tokenStyles;

  public JavaScopeWalker(ScopeWalker scopeWalker, int offset, int[] tokenTypes, int[] tokenStyles) {
    scopeWalker.offset = offset;
    this.scopeWalker = scopeWalker;
    this.tokenTypes = tokenTypes;
    this.tokenStyles = tokenStyles;
  }

  public JavaScopeWalker(IntervalNode node) {
    scopeWalker = new ScopeWalker(node);
  }

  @Override
  public void enterUnknownInterval(JavaParser.UnknownIntervalContext ctx) {
    super.enterUnknownInterval(ctx);
  }

  @Override
  public void enterPackageDeclaration(JavaParser.PackageDeclarationContext ctx) {
    super.enterPackageDeclaration(ctx);
    scopeWalker.enterFakeScope();
    scopeWalker.addInterval(ctx, PACKAGE);
  }

  @Override
  public void exitPackageDeclaration(JavaParser.PackageDeclarationContext ctx) {
    super.exitPackageDeclaration(ctx);
    scopeWalker.exitFakeScope();
  }

  @Override
  public void enterImportDeclaration(JavaParser.ImportDeclarationContext ctx) {
    super.enterImportDeclaration(ctx);
    scopeWalker.enterFakeScope();
    scopeWalker.addInterval(ctx, IMPORT);
  }

  @Override
  public void exitImportDeclaration(JavaParser.ImportDeclarationContext ctx) {
    super.exitImportDeclaration(ctx);
    scopeWalker.exitFakeScope();
  }

  @Override
  public void enterTypeDeclaration(JavaParser.TypeDeclarationContext ctx) {
    super.enterTypeDeclaration(ctx);
    scopeWalker.enterScope();
    scopeWalker.addInterval(ctx, TYPE_DECL);
    scopeWalker.enterInterval();
  }

  @Override
  public void exitTypeDeclaration(JavaParser.TypeDeclarationContext ctx) {
    super.exitTypeDeclaration(ctx);
    scopeWalker.exitScope();
    scopeWalker.exitInterval();
  }

  @Override
  public void enterClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
    super.enterClassDeclaration(ctx);
    scopeWalker.enterScope();

    var node = getNode(ctx.identifier());
    String typeString = node.getText();
    var type = scopeWalker.associateType(typeString, scopeWalker.currentScope);

    if (ctx.typeType() != null) {
      String supertypeString = getType(ctx.typeType());
      var supertype = scopeWalker.getType(supertypeString);
      scopeWalker.addSupertype(type, supertype);
      scopeWalker.currentScope.importTypes.add(supertype);
    }

    addSupertypes(type, ctx.typeList());

    scopeWalker.newIntervalStart =
        ctx.classBody().LBRACE().getSymbol().getStartIndex() + 1;
  }

  @Override
  public void exitClassDeclaration(JavaParser.ClassDeclarationContext ctx) {
    super.exitClassDeclaration(ctx);
    scopeWalker.exitScope();

    scopeWalker.newIntervalStart =
        ctx.classBody().RBRACE().getSymbol().getStartIndex() + 1;
  }

  @Override
  public void enterClassBodyDeclaration(JavaParser.ClassBodyDeclarationContext ctx) {
    super.enterClassBodyDeclaration(ctx);
    if (ctx.parent instanceof JavaParser.EnumBodyDeclarationsContext) return;
    if (isMember(ctx)) {
      addMember(ctx.memberDeclaration(), isStatic(ctx));
      if (haveArgs(ctx.memberDeclaration())) {
        scopeWalker.addInterval(ctx, CLASS_BODY, scopeWalker.currentScope.parent);
      }
    } else scopeWalker.enterScope();

    if (!(isMember(ctx) && haveArgs(ctx.memberDeclaration()))) {
      scopeWalker.addInterval(ctx, CLASS_BODY);
    }
    scopeWalker.enterInterval();
  }

  private boolean isStatic(JavaParser.ClassBodyDeclarationContext ctx) {
    return ctx.modifier() != null && isStatic(ctx.modifier());
  }

  private boolean isStatic(JavaParser.InterfaceBodyDeclarationContext ctx) {
    return ctx.modifier() != null && isStatic(ctx.modifier());
  }

  private boolean isStatic(List<JavaParser.ModifierContext> modifiers) {
    return modifiers.stream().anyMatch(
        mod -> mod.classOrInterfaceModifier() != null
            && mod.classOrInterfaceModifier().STATIC() != null
    );
  }

  @Override
  public void exitClassBodyDeclaration(JavaParser.ClassBodyDeclarationContext ctx) {
    super.exitClassBodyDeclaration(ctx);
    if (ctx.parent instanceof JavaParser.EnumBodyDeclarationsContext) return;
    if (isMember(ctx)) {
      scopeWalker.exitMember();
      if (haveArgs(ctx.memberDeclaration())) scopeWalker.exitScope();
    } else scopeWalker.exitScope();

    scopeWalker.exitInterval();
  }

  @Override
  public void enterInterfaceDeclaration(JavaParser.InterfaceDeclarationContext ctx) {
    super.enterInterfaceDeclaration(ctx);
    scopeWalker.enterScope();

    var node = getNode(ctx.identifier());
    String typeString = node.getText();
    var type = scopeWalker.associateType(typeString, scopeWalker.currentScope);

    addSupertypes(type, ctx.typeList());

    scopeWalker.newIntervalStart =
        ctx.interfaceBody().LBRACE().getSymbol().getStartIndex() + 1;
  }

  @Override
  public void exitInterfaceDeclaration(JavaParser.InterfaceDeclarationContext ctx) {
    super.exitInterfaceDeclaration(ctx);
    scopeWalker.exitScope();

    scopeWalker.newIntervalStart =
        ctx.interfaceBody().RBRACE().getSymbol().getStartIndex() + 1;
  }

  @Override
  public void enterInterfaceBodyDeclaration(JavaParser.InterfaceBodyDeclarationContext ctx) {
    super.enterInterfaceBodyDeclaration(ctx);
    if (isMember(ctx))
      addMember(ctx.interfaceMemberDeclaration(), isStatic(ctx));
    else scopeWalker.enterScope();
    scopeWalker.addInterval(ctx, CLASS_BODY);
    scopeWalker.enterInterval();
  }

  @Override
  public void exitInterfaceBodyDeclaration(JavaParser.InterfaceBodyDeclarationContext ctx) {
    super.exitInterfaceBodyDeclaration(ctx);
    if (isMember(ctx)) {
      scopeWalker.exitMember();
      if (haveArgs(ctx.interfaceMemberDeclaration())) scopeWalker.exitScope();
    } else scopeWalker.exitScope();

    scopeWalker.exitInterval();
  }

  @Override
  public void enterEnumDeclaration(JavaParser.EnumDeclarationContext ctx) {
    super.enterEnumDeclaration(ctx);
    scopeWalker.enterScope();
    scopeWalker.newIntervalStart = ctx.LBRACE().getSymbol().getStartIndex() + 1;
  }

  @Override
  public void exitEnumDeclaration(JavaParser.EnumDeclarationContext ctx) {
    super.exitEnumDeclaration(ctx);
    scopeWalker.exitScope();
    scopeWalker.newIntervalStart = ctx.RBRACE().getSymbol().getStartIndex() + 1;
  }

  @Override
  public void enterRecordDeclaration(JavaParser.RecordDeclarationContext ctx) {
    super.enterRecordDeclaration(ctx);
    scopeWalker.enterScope();
    scopeWalker.newIntervalStart = ctx.recordBody().LBRACE().getSymbol().getStartIndex() + 1;
  }

  @Override
  public void exitRecordDeclaration(JavaParser.RecordDeclarationContext ctx) {
    super.exitRecordDeclaration(ctx);
    scopeWalker.exitScope();
    scopeWalker.newIntervalStart = ctx.recordBody().RBRACE().getSymbol().getStartIndex() + 1;
  }

  @Override
  public void enterAnnotationTypeDeclaration(JavaParser.AnnotationTypeDeclarationContext ctx) {
    super.enterAnnotationTypeDeclaration(ctx);
    scopeWalker.enterScope();
    scopeWalker.newIntervalStart = ctx.annotationTypeBody().LBRACE().getSymbol().getStartIndex() + 1;
  }

  @Override
  public void exitAnnotationTypeDeclaration(JavaParser.AnnotationTypeDeclarationContext ctx) {
    super.exitAnnotationTypeDeclaration(ctx);
    scopeWalker.exitScope();
    scopeWalker.newIntervalStart = ctx.annotationTypeBody().RBRACE().getSymbol().getStartIndex() + 1;
  }

  @Override
  public void enterAnnotationTypeElementDeclaration(JavaParser.AnnotationTypeElementDeclarationContext ctx) {
    super.enterAnnotationTypeElementDeclaration(ctx);
    scopeWalker.enterScope();
    scopeWalker.addInterval(ctx, UNKNOWN);
    scopeWalker.enterInterval();
  }

  @Override
  public void exitAnnotationTypeElementDeclaration(JavaParser.AnnotationTypeElementDeclarationContext ctx) {
    super.exitAnnotationTypeElementDeclaration(ctx);
    scopeWalker.exitScope();
    scopeWalker.exitInterval();
  }

  @Override
  public void enterLocalVariableDeclaration(JavaParser.LocalVariableDeclarationContext ctx) {
    super.enterLocalVariableDeclaration(ctx);
  }

  @Override
  public void exitAnnotation(JavaParser.AnnotationContext ctx) {
    super.exitAnnotation(ctx);
    if (ctx.qualifiedName() != null) {
      ctx.qualifiedName().identifier().forEach(id -> mark(getNode(id), TokenTypes.ANNOTATION, TokenStyles.NORMAL));
    } else if (ctx.altAnnotationQualifiedName() != null) {
      var ids = ctx.altAnnotationQualifiedName().identifier();
      var lastId = ids.get(ids.size() - 1);
      mark(getNode(lastId), TokenTypes.ANNOTATION, TokenStyles.NORMAL);
    }
  }

  @Override
  public void exitLocalVariableDeclaration(JavaParser.LocalVariableDeclarationContext ctx) {
    super.exitLocalVariableDeclaration(ctx);
    handleLocalVariable(ctx);
  }

  private void handleLocalVariable(JavaParser.LocalVariableDeclarationContext ctx) {
    if (ctx.typeType() != null && ctx.variableDeclarators() != null) {
      var typeString = getType(ctx.typeType());
      var type = scopeWalker.getType(typeString);
      for (var variableDeclarator: ctx.variableDeclarators().variableDeclarator()) {
        var node = getNode(variableDeclarator);
        var decl = new DeclNode(Name.fromNode(node, offset), type, DeclNode.LOCAL_VAR);
        scopeWalker.addDecl(decl);
        if (variableDeclarator.variableInitializer() != null &&
            variableDeclarator.variableInitializer().expression() != null
        ) scopeWalker.addRef(handleExpression(variableDeclarator.variableInitializer().expression()));
      }
    }
    if (ctx.identifier() != null && ctx.expression() != null) {
      var node = getNode(ctx.identifier());
      var ref = handleExpression(ctx.expression());
      var type = ref != null ? ref.type : null;
      var decl = new DeclNode(Name.fromNode(node, offset), type, DeclNode.LOCAL_VAR);
      mark(ctx.VAR(), TokenTypes.KEYWORD, TokenStyles.NORMAL);
      scopeWalker.addInference(new InferenceNode(decl, ref));
    }
  }

  @Override
  public void enterStatement(JavaParser.StatementContext ctx) {
    super.enterStatement(ctx);
    if (ctx.IF() != null) {
      var ref = handleExpression(ctx.parExpression().expression());
      scopeWalker.addRef(ref);
    }
    if (ctx.FOR() != null) {
      scopeWalker.enterScope();
      handleForControl(ctx.forControl());
    }
  }

  @Override
  public void exitStatement(JavaParser.StatementContext ctx) {
    super.exitStatement(ctx);
    if (ctx.expression() != null && !ctx.expression().isEmpty()) {
      scopeWalker.addRef(handleExpression(ctx.expression(0)));
    }
    if (ctx.FOR() != null) {
      scopeWalker.exitScope();
    }
  }

  private void handleForControl(JavaParser.ForControlContext ctx) {
    if (ctx.enhancedForControl() != null) {
      var enhanced = ctx.enhancedForControl();
      var declName = Name.fromRule(enhanced.variableDeclaratorId(), offset);
      var ref = handleExpression(enhanced.expression());
      if (enhanced.VAR() != null) {
        var type = ref != null ? TypeMap.getArrayElemType(ref.type) : null;
        var decl = new DeclNode(declName, scopeWalker.getType(type), DeclNode.LOCAL_VAR);
        scopeWalker.addInference(new InferenceNode(decl, ref, InferenceNode.FOR_EACH));
        mark(enhanced.VAR(), TokenTypes.KEYWORD, TokenStyles.NORMAL);
      } else {
        var type = enhanced.typeType().getText();
        scopeWalker.addDecl(new DeclNode(declName, scopeWalker.getType(type), DeclNode.LOCAL_VAR));
        scopeWalker.addRef(ref);
      }
    } else {
      if (ctx.forInit() != null) {
        var init = ctx.forInit();
        if (init.localVariableDeclaration() != null) {
          handleLocalVariable(init.localVariableDeclaration());
        } else {
          var refs = handleExpressionList(init.expressionList());
          refs.forEach(scopeWalker::addRef);
        }
      }
      if (ctx.expression() != null) {
        var ref = handleExpression(ctx.expression());
        scopeWalker.addRef(ref);
      }
      if (ctx.forUpdate != null && !ctx.forUpdate.expression().isEmpty()) {
        var refs = handleExpressionList(ctx.forUpdate);
        refs.forEach(scopeWalker::addRef);
      }
    }
  }

  @Override
  public void enterBlock(JavaParser.BlockContext ctx) {
    super.enterBlock(ctx);
    if (ctx.parent instanceof JavaParser.MethodBodyContext ||
        ctx.parent instanceof JavaParser.ConstructorDeclarationContext
    ) return;
    scopeWalker.enterScope();
  }

  @Override
  public void exitBlock(JavaParser.BlockContext ctx) {
    super.exitBlock(ctx);
    if (ctx.parent instanceof JavaParser.MethodBodyContext ||
        ctx.parent instanceof JavaParser.ConstructorDeclarationContext
    ) return;
    scopeWalker.exitScope();
  }

  public void handleField(JavaParser.FieldDeclarationContext ctx, boolean isStatic) {
    String typeString = getType(ctx.typeType());
    String type = scopeWalker.getType(typeString);
    var declarators = getVarDeclarators(ctx.variableDeclarators());

    List<DeclNode> fields = declarators.stream()
        .map(decl -> new DeclNode(Name.fromRule(decl, offset), type, DeclNode.FIELD))
        .toList();

    scopeWalker.enterMember(fields);
    var refs = getVarDeclaratorsRefs(ctx.variableDeclarators());
    scopeWalker.addRefs(refs);
    mark(declarators.stream().map(it -> getNode(it.identifier())).toList(), TokenTypes.FIELD, isStatic ? TokenStyles.ITALIC : TokenStyles.NORMAL);
  }

  private void handleConst(JavaParser.ConstDeclarationContext ctx) {
    var type = ctx.typeType().getText();
    type = scopeWalker.getType(type);
    var decls = new ArrayList<DeclNode>();
    for (var decl: ctx.constantDeclarator()) {
      var name = Name.fromRule(decl.identifier(), offset);
      if (decl.variableInitializer().expression() != null) {
        var ref = handleExpression(decl.variableInitializer().expression());
        scopeWalker.addRef(ref);
      }
      decls.add(new DeclNode(name, type, DeclNode.FIELD));
      mark(getNode(decl.identifier()), TokenTypes.FIELD, TokenStyles.ITALIC);
    }
    scopeWalker.enterMember(decls);
  }

  public void handleMethod(JavaParser.MethodDeclarationContext ctx, boolean isStatic) {
    addMethodDecl(ctx.identifier(), ctx.typeTypeOrVoid(), ctx.formalParameters());
    mark(getNode(ctx.identifier()), TokenTypes.METHOD, isStatic ? TokenStyles.ITALIC : TokenStyles.NORMAL);
  }

  public void handleMethod(JavaParser.InterfaceMethodDeclarationContext ctx, boolean isStatic) {
    if (ctx.interfaceCommonBodyDeclaration() == null) return;
    var cbm = ctx.interfaceCommonBodyDeclaration();
    addMethodDecl(cbm.identifier(), cbm.typeTypeOrVoid(), cbm.formalParameters());
    mark(getNode(cbm.identifier()), TokenTypes.METHOD, isStatic ? TokenStyles.ITALIC : TokenStyles.NORMAL);
  }

  public void handleMethod(JavaParser.GenericInterfaceMethodDeclarationContext ctx, boolean isStatic) {
    if (ctx.interfaceCommonBodyDeclaration() == null) return;
    var cbm = ctx.interfaceCommonBodyDeclaration();
    addMethodDecl(cbm.identifier(), cbm.typeTypeOrVoid(), cbm.formalParameters());
    mark(getNode(cbm.identifier()), TokenTypes.METHOD, isStatic ? TokenStyles.ITALIC : TokenStyles.NORMAL);
  }

  public void handleConstructorDeclaration(JavaParser.ConstructorDeclarationContext ctx) {
    super.enterConstructorDeclaration(ctx);
    TerminalNode node = getNode(ctx.identifier());
    Name decl = Name.fromNode(node, offset);
    String typeString = node.getText();
    String type = scopeWalker.getType(typeString);
    List<DeclNode> args = getArgsTypes(ctx.formalParameters());
    List<String> types = args.stream().map(arg -> arg.type).toList();
    scopeWalker.enterMember(new MethodNode(decl, type, MethodNode.CREATOR, types));
    scopeWalker.enterScope();
    scopeWalker.addDecls(args);
  }

  private void addMethodDecl(
      JavaParser.IdentifierContext identifier,
      JavaParser.TypeTypeOrVoidContext typeTypeOrVoid,
      JavaParser.FormalParametersContext formalParameters
  ) {
    TerminalNode node = getNode(identifier);
    Name decl = Name.fromNode(node, offset);
    String typeString = getType(typeTypeOrVoid);
    String type = scopeWalker.getType(typeString);
    List<DeclNode> args = getArgsTypes(formalParameters);
    List<String> types = args.stream().map(arg -> arg.type).toList();
    scopeWalker.enterMember(new MethodNode(decl, type, MethodNode.METHOD, types));
    scopeWalker.enterScope();
    scopeWalker.addDecls(args);
  }

  private void mark(TerminalNode node, int type, int style) {
    if (tokenTypes == null || tokenStyles == null) return;
    int ind = node.getSymbol().getTokenIndex();
    tokenTypes[ind] = type;
    tokenStyles[ind] = style;
  }

  private void mark(List<TerminalNode> nodes, int type, int style) {
    nodes.forEach(node -> mark(node, type, style));
  }

  private boolean isMember(JavaParser.ClassBodyDeclarationContext ctx) {
    return ctx.memberDeclaration() != null
        && (ctx.memberDeclaration().fieldDeclaration() != null
        || ctx.memberDeclaration().methodDeclaration() != null
        || ctx.memberDeclaration().genericMethodDeclaration() != null
        || ctx.memberDeclaration().constructorDeclaration() != null);
  }

  private boolean isMember(JavaParser.InterfaceBodyDeclarationContext ctx) {
    return ctx.interfaceMemberDeclaration() != null
        && (ctx.interfaceMemberDeclaration().interfaceMethodDeclaration() != null
        || ctx.interfaceMemberDeclaration().genericInterfaceMethodDeclaration() != null
        || ctx.interfaceMemberDeclaration().constDeclaration() != null
    );
  }

  private boolean haveArgs(JavaParser.MemberDeclarationContext ctx) {
    return ctx.methodDeclaration() != null
        || ctx.genericMethodDeclaration() != null
        || ctx.constructorDeclaration() != null;
  }

  private boolean haveArgs(JavaParser.InterfaceMemberDeclarationContext ctx) {
    return ctx.interfaceMethodDeclaration() != null
        || ctx.genericInterfaceMethodDeclaration() != null;
  }

  private void addMember(JavaParser.MemberDeclarationContext ctx, boolean isStatic) {
    if (ctx.fieldDeclaration() != null) handleField(ctx.fieldDeclaration(), isStatic);
    else if (ctx.methodDeclaration() != null) handleMethod(ctx.methodDeclaration(), isStatic);
    else if (ctx.genericMethodDeclaration() != null)
      handleMethod(ctx.genericMethodDeclaration().methodDeclaration(), isStatic);
    else if (ctx.constructorDeclaration() != null) handleConstructorDeclaration(ctx.constructorDeclaration());
    else throw new IllegalArgumentException();
  }

  private void addMember(JavaParser.InterfaceMemberDeclarationContext ctx, boolean isStatic) {
    if (ctx.interfaceMethodDeclaration() != null) handleMethod(ctx.interfaceMethodDeclaration(), isStatic);
    else if (ctx.genericInterfaceMethodDeclaration() != null)
      handleMethod(ctx.genericInterfaceMethodDeclaration(), isStatic);
    else if (ctx.constDeclaration() != null) handleConst(ctx.constDeclaration());
    else throw new IllegalArgumentException();
  }

  private void addSupertypes(
      String type,
      List<JavaParser.TypeListContext> typeListContexts
  ) {
    if (typeListContexts != null && !typeListContexts.isEmpty()) {
      var typeTypeList = typeListContexts.stream().flatMap(typeList -> typeList.typeType().stream()).toList();
      for (var typeType: typeTypeList) {
        String supertypeString = getType(typeType);

        var supertype = scopeWalker.getType(supertypeString);
        scopeWalker.addSupertype(type, supertype);
        scopeWalker.currentScope.importTypes.add(supertype);
      }
    }
  }

  private RefNode handleExpression(JavaParser.ExpressionContext ctx) {
    if (isBooleanBinOperation(ctx)) return handleBooleanExpression(ctx);
    if (isTernary(ctx)) return handleTernary(ctx);
    if (isQualified(ctx)) return handleQualified(ctx);
    if (ctx.primary() != null) return handlePrimary(ctx.primary());
    if (ctx.creator() != null) return handleCreator(ctx.creator());
    if (ctx.getChildCount() == 1 && ctx.methodCall() != null)
      return handleMethodCall(ctx.methodCall());
    if (ctx.INSTANCEOF() != null) {
      var expr = handleExpression(ctx.expression(0));
      if (expr == null) return new RefNode(null, "boolean", RefNode.TYPE);
      else return new ExprRefNode(List.of(expr), "boolean");
    }
    if (isNonNullAndEmpty(ctx.typeType())) {
      var type = getType(ctx.typeType(0));
      var expr = handleExpression(ctx.expression(0));
      if (expr == null) return new RefNode(null, type, RefNode.TYPE);
      else return new ExprRefNode(List.of(expr), type);
    }
    if (ctx.expression() != null && !ctx.expression().isEmpty()) {
      List<RefNode> refs = new ArrayList<>();
      for (var expr: ctx.expression()) {
        refs.add(handleExpression(expr));
      }
//      List<RefNode> refs = ctx.expression().stream().map(this::handleExpression).toList();
      return new ExprRefNode(refs);
    }
    System.err.println("Unsupported expression: " + ctx.getText());
    return null;
  }

  private List<RefNode> handleExpressionList(JavaParser.ExpressionListContext ctx) {
    return ctx.expression().stream()
        .map(this::handleExpression)
        .toList();
  }

  private MethodCallNode handleMethodCall(JavaParser.MethodCallContext ctx) {
    List<RefNode> args = ctx.expressionList() != null
        ? handleExpressionList(ctx.expressionList())
        : new ArrayList<>();

    if (ctx.identifier() != null) {
      var node = getNode(ctx.identifier());
      return new MethodCallNode(Name.fromNode(node, offset), args);
    } else if (ctx.THIS() != null) {
      // todo
    } else if (ctx.SUPER() != null) {
      // todo
    }
    return null;
  }

  private RefNode handleQualified(JavaParser.ExpressionContext ctx) {
    List<RefNode> refs = new ArrayList<>();
    handleQualifiedRec(ctx, refs);

    for (var ref: refs) {
      if (ref == null) return refs.get(0);
    }
    var preLast = refs.get(refs.size() - 2);
    var last = refs.get(refs.size() - 1);

    var cur = new QualifiedRefNode(preLast, last);
    for (int i = refs.size() - 3; i >= 0; i--) {
      cur = new QualifiedRefNode(refs.get(i), cur);
    }
    return cur;
  }

  private MethodCallNode handleCreator(JavaParser.CreatorContext ctx) {
    boolean isArrayCreator = ctx.arrayCreatorRest() != null;
    var node = getNode(ctx.createdName());
    var decl = Name.fromNode(node, offset);
    var typeString = node.getText();
    if (isArrayCreator) {
      scopeWalker.getType(typeString);
      typeString += getArrayType(ctx.arrayCreatorRest());
      handleArrayCreator(ctx.arrayCreatorRest());
    }
    var type = scopeWalker.getType(typeString);

    List<RefNode> args;
    if (ctx.classCreatorRest() != null &&
        ctx.classCreatorRest().arguments() != null &&
        ctx.classCreatorRest().arguments().expressionList() != null
    ) args = handleExpressionList(ctx.classCreatorRest().arguments().expressionList());
    else args = new ArrayList<>();

    return new MethodCallNode(decl, type, isArrayCreator ? MethodNode.ARRAY_CREATOR : MethodNode.CREATOR, args);
  }

  public void handleArrayCreator(JavaParser.ArrayCreatorRestContext ctx) {
    var exprs = ctx.expression().stream().map(this::handleExpression).toList();
    scopeWalker.addRefs(exprs);
  }

  private void handleQualifiedRec(JavaParser.ExpressionContext ctx, List<RefNode> refs) {
    if (ctx.expression() != null &&
        ctx.expression().size() == 1 &&
        ctx.dot != null
    ) handleQualifiedRec(ctx.expression(0), refs);

    if (ctx.identifier() != null) refs.add(new RefNode(Name.fromNode(getNode(ctx.identifier()), offset)));
    else if (ctx.primary() != null) refs.add(handlePrimary(ctx.primary()));
    else if (ctx.methodCall() != null) refs.add(handleMethodCall(ctx.methodCall()));
    else refs.add(null);
  }

  private RefNode handlePrimary(JavaParser.PrimaryContext ctx) {
    if (ctx.expression() != null) return handleExpression(ctx.expression());
    if (ctx.THIS() != null) return new RefNode(Name.fromNode(ctx.THIS(), offset), null, RefNode.THIS);
    if (ctx.SUPER() != null) return new RefNode(Name.fromNode(ctx.SUPER(), offset), null, RefNode.SUPER);
    if (ctx.literal() != null) return handleLiteral(ctx.literal());
    if (ctx.identifier() != null) return new RefNode(Name.fromNode(getNode(ctx.identifier()), offset));
    return null;
  }

  private RefNode handleBooleanExpression(JavaParser.ExpressionContext ctx) {
    scopeWalker.addRef(handleExpression(ctx.expression(0)));
    scopeWalker.addRef(handleExpression(ctx.expression(1)));
    Name decl = new Name("", ctx.getStart().getStartIndex());
    return new RefNode(decl, scopeWalker.getType("boolean"), RefNode.TYPE);
  }

  private RefNode handleLiteral(JavaParser.LiteralContext ctx) {
    var node = ctx.getStart();
    var type = getLiteralType(ctx);
    return new RefNode(Name.fromToken(node, offset), type, RefNode.TYPE);
  }

  private String getLiteralType(JavaParser.LiteralContext ctx) {
    if (ctx.integerLiteral() != null) return scopeWalker.getType("int");
    if (ctx.floatLiteral() != null) return scopeWalker.getType("float");
    if (ctx.CHAR_LITERAL() != null) return scopeWalker.getType("char");
    if (ctx.BOOL_LITERAL() != null) return scopeWalker.getType("boolean");
    if (ctx.STRING_LITERAL() != null ||
        ctx.TEXT_BLOCK() != null) return scopeWalker.getType("String");
    return null;
  }

  private String getArrayType(JavaParser.ArrayCreatorRestContext ctx) {
    StringBuilder sb = new StringBuilder();
    for (var child: ctx.children) {
      if (!(child instanceof TerminalNode terminal) || terminal instanceof ErrorNode) continue;
      sb.append(terminal.getText());
    }
    return sb.toString();
  }

  private RefNode handleTernary(JavaParser.ExpressionContext ctx) {
    scopeWalker.addRef(handleExpression(ctx.expression(0)));
    var result = handleExpression(ctx.expression(1));
    scopeWalker.addRef(handleExpression(ctx.expression(2)));
    return result;
  }

  public String toString(JavaParser.QualifiedNameContext ctx) {
    return ctx.toString();
  }

  private List<JavaParser.VariableDeclaratorIdContext> getVarDeclarators(JavaParser.VariableDeclaratorsContext ctx) {
    List<JavaParser.VariableDeclaratorIdContext> result = new ArrayList<>();
    for (var declarator: ctx.variableDeclarator()) {
      var rule = declarator.variableDeclaratorId();
      result.add(rule);
    }
    return result;
  }

  private List<RefNode> getVarDeclaratorsRefs(JavaParser.VariableDeclaratorsContext ctx) {
    List<RefNode> result = new ArrayList<>();
    for (var declarator: ctx.variableDeclarator()) {
      if (declarator.variableInitializer() != null) {
        var varInit = declarator.variableInitializer();
        if (varInit.arrayInitializer() != null) {
          // todo
        } else {
          var ref = handleExpression(varInit.expression());
          result.add(ref);
        }
      }
    }
    return result;
  }

  private List<DeclNode> getArgsTypes(JavaParser.FormalParametersContext ctx) {
    List<DeclNode> result = new ArrayList<>();
    if (ctx.formalParameterList() != null) {
      var list = ctx.formalParameterList();
      for (var formalParam: list.formalParameter()) {
        String typeString = getType(formalParam.typeType());
        String type = scopeWalker.getType(typeString);

        TerminalNode node = getNode(formalParam.variableDeclaratorId().identifier());
        Name decl = Name.fromNode(node, offset);
        result.add(new DeclNode(decl, type, DeclNode.ARGUMENT));
      }
      if (list.lastFormalParameter() != null) {
        String typeString = getType(list.lastFormalParameter().typeType());
        String type = scopeWalker.getType(typeString);

        TerminalNode node = getNode(list.lastFormalParameter().variableDeclaratorId().identifier());
        Name decl = Name.fromNode(node, offset);
        result.add(new DeclNode(decl, type, DeclNode.ARGUMENT));
      }
    }
    return result;
  }

  private boolean isStatic(JavaParser.FieldDeclarationContext ctx) {
    if (ctx.parent instanceof JavaParser.MemberDeclarationContext memberDeclaration) {
      return isStatic(memberDeclaration);
    }
    return false;
  }

  private boolean isStatic(JavaParser.MemberDeclarationContext ctx) {
    if (ctx.parent instanceof JavaParser.ClassBodyDeclarationContext classBodyDeclaration) {
      return classBodyDeclaration.STATIC() != null;
    }
    return false;
  }

  private boolean hasThis(JavaParser.ExpressionContext ctx) {
    if (ctx.getChildCount() < 3
        || ctx.expression() == null
        || ctx.expression().isEmpty()
    ) return false;
    var expression = ctx.expression(0);
    return expression.primary() != null
        && expression.primary().THIS() != null
        && ctx.dot != null;
  }

  private boolean isQualified(JavaParser.ExpressionContext ctx) {
    return ctx.getChildCount() >= 3
        && ctx.expression() != null
        && !ctx.expression().isEmpty()
        && ctx.dot != null
        && (ctx.identifier() != null || ctx.methodCall() != null);
  }

  private boolean isBooleanBinOperation(JavaParser.ExpressionContext ctx) {
    return ctx.getChildCount() == 3
        && (isNonNullAndEmpty(ctx.LT())
        || isNonNullAndEmpty(ctx.GT())
        || ctx.LE() != null
        || ctx.GE() != null
        || ctx.EQUAL() != null
        || ctx.NOTEQUAL() != null);
  }

  static boolean isTernary(JavaParser.ExpressionContext ctx) {
    return ctx.getChildCount() == 5
        && ctx.QUESTION() != null
        && ctx.COLON() != null;
  }

  private <T extends ParseTree> boolean isNonNullAndEmpty(List<T> nodes) {
    return nodes != null && !nodes.isEmpty();
  }

  private TerminalNode getNode(JavaParser.VariableDeclaratorContext ctx) {
    return getNode(ctx.variableDeclaratorId());
  }

  private TerminalNode getNode(JavaParser.VariableDeclaratorIdContext ctx) {
    return getNode(ctx.identifier());
  }

  private TerminalNode getNode(JavaParser.IdentifierContext ctx) {
    return (TerminalNode) ctx.getChild(0);
  }

  private TerminalNode getNode(JavaParser.CreatedNameContext ctx) {
    return ctx.identifier() != null && !ctx.identifier().isEmpty()
        ? getNode(ctx.identifier(ctx.identifier().size() - 1))
        : (TerminalNode) ctx.primitiveType().getChild(0);
  }

  private String getType(JavaParser.TypeTypeOrVoidContext ctx) {
    return ctx.typeType() != null
        ? getType(ctx.typeType())
        : ctx.VOID().getText();
  }

  private String getType(JavaParser.TypeTypeContext ctx) {
    return ctx.getText();
  }

}
