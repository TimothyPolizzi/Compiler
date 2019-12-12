import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

/**
 * A Semantic Analyzer for the SAD Compiler for Alan Labouseur's compilers class.
 *
 * @author Tim Polizzi
 */
public class SemanticAnalyzer {

  private List<Token> tokenList;
  private SyntaxTree ast;
  private SymbolTable symbols;

  private boolean success;
  private boolean verbose;

  private int errCount;
  private int warnCount;
  private int programNo;
  private int scope;

  /**
   * Taking a list of tokens, create a AST and then analyze it for scope and type errors.
   *
   * @param tokenList is the list of tokens used to generate the AST.
   */
  public SemanticAnalyzer(int programNo, List<Token> tokenList, boolean verbose) {
    this.tokenList = tokenList;
    this.verbose = verbose;
    this.programNo = programNo;

    symbols = new SymbolTable();
    success = true;
    errCount = 0;
    warnCount = 0;
    scope = -1; // Sure this is a bad idea, however it should allow scope to be 0 at start and go up

    System.out.println("\nINFO Semantic Analysis - Analyzing program " + programNo + "...");

    ast = block();

    if (errCount != 0) {
      success = false;
      System.out.println("INFO Semantic Analysis - Analysis failed with " + errCount + " error(s)");
    } else {
      System.out.println(
          "INFO Semantic Analysis - Analysis completed successfully with " + warnCount
              + " warning(s)");
    }
  }

  /**
   * Used to add a block, and adds a new scope.
   */
  private SyntaxTree block() {
    SyntaxTree blockTree = new SyntaxTree("Block");
    verboseWriter("Block");

    if (qol("L_BRACE")) {
      scope++;
      match("L_BRACE");
      stmt(blockTree);

    } else {
      match("L_BRACE");
    }

    bestPractices();

    scope--;
    match("R_BRACE");

    return blockTree;
  }

  /**
   * Stmt -> PrintStmt | AssignStmt | VarDecl | WhileStmt | IfStmt | Block
   */
  private void stmt(SyntaxTree parent) {
    //noinspection DuplicatedCode
    if (qol("PRINT_STMT")) {
      parent.add(printStmt());

    } else if (qol("[a-z]|CHAR")) {
      parent.add(assignStmt());

    } else if (qol("[ISB]_TYPE")) {
      parent.add(varDecl());

    } else if (qol("WHILE_LOOP")) {
      parent.add(whileStmt());

    } else if (qol("IF_STMT")) {
      parent.add(ifStmt());

    } else if (qol("L_BRACE")) {
      parent.add(block());
    }

    if (qol("L_BRACE|(PRINT|IF)_STMT|(CHAR|[a-z])|([ISB]_TYPE)|WHILE_LOOP")) {
      stmt(parent);
    }
  }

  /**
   * PrintStmt -> print ( Expr )
   */
  private SyntaxTree printStmt() {
    SyntaxTree printStmtTree = new SyntaxTree("Print Statement");
    verboseWriter("printStatement");

    if (qol("PRINT_STMT")) {
      match("PRINT_STMT");
      match("L_PAREN");
      expr(printStmtTree);
      match("R_PAREN");
    }

    return printStmtTree;
  }

  /**
   * AssignStmt -> id = Expr
   */
  private SyntaxTree assignStmt() {
    SyntaxTree assignStmtTree = new SyntaxTree("Assignment Statement");
    verboseWriter("assignmentStatement");

    Token idToken = null;
    List<Token> exprTokens = null;

    if (qol("[a-z]|CHAR")) {
      idToken = terminal(assignStmtTree); //Name of the variable to be assigned
      match("ASSIGN_OP");
      exprTokens = expr(assignStmtTree); //Value the variable is to be assigned to
    }

    if (!typeCheck(idToken, exprTokens, assignStmtTree.getRoot())) {
      return assignStmtTree;
    }

    String varValue = assignStmtTree.getRoot().getChildren().get(1).getVal();

    if(!Pattern.matches("^\\[.*", varValue)) {
      symbols.activeSymbol(idToken.getOriginal(), scope).setVal(slapTogether(exprTokens));
    } else {
      symbols.activeSymbol(idToken.getOriginal(), scope).setVal(varValue);
    }

    return assignStmtTree;
  }

  /**
   * VarDecl -> type id
   */
  private SyntaxTree varDecl() {
    SyntaxTree varDeclTree = new SyntaxTree("Variable Declaration");
    verboseWriter("varDecl");

    Token typeToken = null;
    Token idToken = null;

    if (qol("[ISB]_TYPE")) {
      typeToken = terminal(varDeclTree); //The type of the declared variable
      idToken = terminal(varDeclTree); //Name of the declared variable
      if (!checkScope(idToken, typeToken)) {
        return varDeclTree;
      }
      symbols.newSymbol(idToken.getOriginal(), typeToken.getOriginal(), null, scope,
          idToken.getLine());
    }

    return varDeclTree;
  }

  /**
   * WhileStmt -> while BoolExpr Block
   */
  private SyntaxTree whileStmt() {
    SyntaxTree whileStmtTree = new SyntaxTree("While Statement");
    verboseWriter("whileStatement");

    if (qol("WHILE_LOOP")) {
      match("WHILE_LOOP");
      boolExpr(whileStmtTree);
      whileStmtTree.add(block());
    }

    return whileStmtTree;
  }

  /**
   * IfStmt -> if BoolExpr Block
   */
  private SyntaxTree ifStmt() {
    SyntaxTree ifStmtTree = new SyntaxTree("If Statement");
    verboseWriter("ifStatement");

    if (qol("IF_STMT")) {
      match("IF_STMT");
      boolExpr(ifStmtTree);
      ifStmtTree.add(block());
    }

    return ifStmtTree;
  }

  /**
   * Expr -> IntExpr | StrExpr | BoolExpr | ID
   */
  private List<Token> expr(SyntaxTree parent) {
    List<Token> toReturn = new ArrayList<>();

    if (qol("INT|[0-9]")) {
      toReturn = intExpr(parent);
    } else if (qol("STRING")) {
      strExpr(parent);
    } else if (qol("L_PAREN|[TF]_BOOL")) {
      toReturn = boolExpr(parent);
    } else if (qol("[a-z]|CHAR")) {
      toReturn.add(terminal(parent));
    }

    return toReturn;
  }

  /**
   * IntExpr -> digit intOp Expr | digit
   */
  private List<Token> intExpr(SyntaxTree parent) {
    List<Token> toReturn = new ArrayList<>();

    if (qol("[0-9]|INT") && Pattern
        .matches("\\+|INT_OP", tokenList.get(1).getFlavor())) {
      toReturn.add(terminal(parent)); // val
      toReturn.add(terminal(parent)); // intOp (+)
      toReturn.addAll(expr(parent));
    } else if (qol("[0-9]|INT")) {
      toReturn.add(terminal(parent));
    }

    return toReturn;
  }

  /**
   * StrExpr -> " CharList "
   */
  private String strExpr(SyntaxTree parent) {
    String toReturn = "";

    if (qol("STRING")) {
      match("STRING"); // add a string
      toReturn = charList(parent);
      match("STRING");
    }

    return toReturn;
  }

  /**
   * BoolExpr -> ( Expr BoolOp Expr ) | BoolVal
   */
  private List<Token> boolExpr(SyntaxTree parent) {
    List<Token> toReturn = new ArrayList<>();

    if (qol("L_PAREN")) {
      match("L_PAREN");
      toReturn.addAll(expr(parent));
      toReturn.add(terminal(parent)); // add a boolean operator (!= or ==)
      toReturn.addAll(expr(parent));
      match("R_PAREN");

    } else if (qol("[TF]_BOOL")) {
      toReturn.add(terminal(parent)); // add a boolean
    }

    return toReturn;
  }

  private String charList(SyntaxTree parent) {
    String toReturn = charList("[");
    toReturn += "]";

    parent.add(toReturn);
    return toReturn;
  }

  /**
   * Because the CharList was previously treated as a list of CHAR tokens, it is now being changed
   * to function as a single string. This is heck.
   */
  private String charList(String str) {
    SyntaxTree falseParent = new SyntaxTree(""); // To not add the Chars to the main tree
    Token charToken = null;

    if (qol("[a-z]|CHAR")) {
      charToken = terminal(falseParent);
      str += charToken.getOriginal(); // add a character
      return charList(str);

    } else if (qol(" ")) {
      charToken = terminal(falseParent);
      str += charToken.getOriginal(); // add a space
      return charList(str);

    } else if (qol("STRING")) {
      //intentionally left blank for lambda set
    }

    return str;
  }

  /**
   * pops the terminal
   */
  private Token terminal(SyntaxTree parent) {
    Token termVal = pop();

    parent.add(termVal.getOriginal());

    return termVal;
  }

  /**
   * Checks a variable that is being assigned to see if it is the correct type to be assigned to the
   * variable, or if the variable has not been declared. (assignment trigger)
   *
   * TODO: In case of assigning to a variable, grab it's type from the table
   *
   * @return True if it is correct, false otherwise.
   */
  public boolean typeCheck(Token id, List<Token> childrenTokens, Node root) {
    List<String> types = sameTypes(childrenTokens);
    SymbolItem varType = symbols.activeSymbol(id.getOriginal(), scope);

    // What if the variable is not declared
    if (varType == null) {
      errCount++;
      System.out.println("Error: The AssignOp " + id.getOriginal() + " on line "
          + id.getLine() + " was used before being declared.");
      return false;
    }

    // What if the multiple types in an expression don't match
    if (!varType.getType().equals("string") && types == null) {
      errCount++;
      System.out.println("Error: The AssignOp expression " + id.getOriginal() + " on line "
          + id.getLine() + " does not match the type of the declared variable " + varType
          .getType() + " " + id.getOriginal());
      return false;
    }

    // What if the type of the thing to be assigned doesn't match
    if (!varType.getType().equals("string")
        && !matches(types.get(0), varType.getType())) {
      return assignOpError(id);
    }

    // Weird case
    if (varType.getType().equals("string")) {
      Node currentNode = root;
      List<String> leaves = new ArrayList<>();

      // Goal here is to grab all the possible terminals
      for (Node n : currentNode.getChildren()) {
        if (n.getChildren().size() > 0) {
          typeCheck(id, childrenTokens, n);
        } else {
          leaves.add(n.getVal());
        }
      }

      //remember those terminals? I'm gonna see if any of them AREN'T full of characters, AKA not strings
      for (String item : leaves) {
        if (item.matches("[\\W]")) {
          return assignOpError(id);
        }
      }

      //case where there is a declared string being initialized to not a string
      if (types != null && types.get(0) != null) {
        return assignOpError(id);
      }

      // . . . ngl I forgot what this one breaks
//      if (types == null && leaves.size() > 1) {
//        return assignOpError(id);
//      }

    }

    return true;
  }

  private boolean matches(String type, String varType) {
    boolean toReturn = false;

    if (varType.matches("int|string|boolean")) {
      if (type.matches(".*BOOL") && varType.equals("boolean")) {
        toReturn = true;
      } else if (varType.toUpperCase().equals(type)) {
        toReturn = true;
      }
    }

    return toReturn;
  }

  /**
   * Hair-loss reduction method
   */
  private boolean assignOpError(Token id) {
    errCount++;
    System.out.println("Error: The AssignOp " + id.getOriginal() + " on line "
        + id.getLine() + " does not match the type of the declared variable " + symbols
        .activeSymbol(id.getOriginal(), scope).getType() + " " + id.getOriginal());
    return false;
  }

  /**
   * List of string types of the tokens, or null if they are not the same types OR if there are no
   * tokens.
   */
  private List<String> sameTypes(List<Token> tokenList) {
    List<String> returnList = null;
    String type = null;

    for (int i = 0; i < tokenList.size(); i++) {
      Token t = tokenList.get(i);

      if (!Pattern.matches(".*OP|.*EQUAL", t.getFlavor())) {
        if (type == null) {
          type = t.getFlavor();
          returnList = new ArrayList<>();
          /* So if t is not the same type as another item break, unless t is a bool in which case
           check if that item is too, or if there is a CHAR, which is actually a variable*/
        } else if (!t.getFlavor().equals(type) && !t.getFlavor().equals("CHAR") &&
            !(Pattern.matches(".*BOOL", t.getFlavor()) || Pattern.matches(".*BOOL", type))) {
          return null;
        }
        returnList.add(t.getFlavor());
      } else if (Pattern.matches(".*EQUAL", t.getFlavor())) {
        type = boolRec(tokenList);
      }
    }

    return returnList;
  }

  /**
   * Goal is to find out if a boolean expression is legal
   */
  private String boolRec(List<Token> tokenList) {
    String left = null;
    String right = null;

    for (int i = 0; i < tokenList.size(); i++) {
      Token t = tokenList.get(i);

      if (Pattern.matches(".*EQUAL", t.getFlavor())) {
        left = tokenList.get(i - 1).getFlavor();
        right = boolRec(tokenList.subList(i + 1, tokenList.size()));

        if (left.equals(right) || (Pattern.matches(".*BOOL", left) && Pattern
            .matches(".*BOOL", right))) {
          return "BOOL";
        }
      }
    }

    if (tokenList.size() < 2) {
      return tokenList.get(0).getFlavor();
    }

    return null;
  }

  /**
   * Checks a variable to see if it is legal in the scope it is currently being attempted to be
   * declared in. (declaration trigger)
   *
   * @return True if it is legal, false otherwise.
   */
  public boolean checkScope(Token id, Token type) {
    SymbolItem activeSymbol = symbols.activeSymbol(id.getOriginal(), scope);

    if (activeSymbol != null && activeSymbol.getScope() == scope) {
      errCount++;
      System.out.println(
          "Error: The " + type.getFlavor() + " " + id.getOriginal() + " on line "
              + id.getLine() + " is already declared in the scope.");
      return false;
    }

    return true;
  }

  /**
   * Checks to see if variables have been unused, and if variables have been used without being
   * assigned. (end of scope/block trigger)
   */
  public void bestPractices() {
    List<SymbolItem> symbolList = symbols.getList();
    for (SymbolItem item : symbolList) {
      if (item.getScope() == scope && item.getVal() == null) {
        warnCount++;
        System.out.println(
            "Warning: The variable " + item.getVar() + " which was declared on line " + item
                .getPos() + " has not been assigned a value.");
        return;
      }
    }
  }

  /**
   * Error handling.
   *
   * @param expected The thing that was expected to be found, and wasn't.
   */
  private void error(String expected) {
    Token currentToken = peek(tokenList);
    System.out.println(
        "ERROR Parser - Expected [" + expected + "] got [" + currentToken.getFlavor()
            + "] with value '" + currentToken.getOriginal() + "' on line " + currentToken
            .getLine());

//    fail = true;
    errCount++;
  }

  /**
   * Look to match a terminal and kill everything if it doesn't.
   */
  private void match(String toMatch) {
    Token currentToken = peek(tokenList);
    if (Pattern.matches(toMatch, currentToken.getFlavor())) {
      //pop topmost token off of stack
      pop();
    } else {
      error(toMatch);
      //clears the stack
    }
  }

  /**
   * sneak a look at the first item in the tokenlist
   */
  private Token peek(List<Token> tokenList) {
    return tokenList.get(0);
  }

  /**
   * This is a method to make the code neater and stop my hair loss.
   *
   * @return A boolean that determines if a given regex matches the top of the tokenList
   */
  private boolean qol(String regex) {
    return Pattern.matches(regex, peek(tokenList).getFlavor());
  }

  /**
   * Prints the method that would be printed in verbose mode if the program is in verbose mode.
   *
   * @param method The name of the method that would be printed.
   */
  private void verboseWriter(String method) {
    if (verbose) {
      System.out.println("DEBUG Semantic Analysis - " + method + "()");
    }
  }

  /**
   * Pops the top item off of token list
   *
   * @return The topmost token of tokenlist
   */
  private Token pop() {
    return tokenList.remove(0);
  }

  /**
   * did it work?
   *
   * @return y or n
   */
  public boolean success() {
    return success;
  }

  /**
   * Get the AST.
   *
   * @return tree, the AST.
   */
  public SyntaxTree getTree() {
    return ast;
  }

  /**
   * Don't judge till you've tried it
   */
  private String slapTogether(List<Token> tokenList) {
    String toReturn = "";

    for (Token t : tokenList) {
      toReturn += t.getOriginal() + " ";
    }

    return toReturn;
  }

  /**
   * Prints the AST.
   */
  public void printTree() {
    System.out.println("\nINFO Semantic Analysis - Printing AST for program " + programNo + "...");
    System.out.println(ast.depthFirstTraversal());
  }

  /**
   * Prints the symbol table
   */
  public void printTable() {
    System.out.println(symbols.toString());
  }

}
