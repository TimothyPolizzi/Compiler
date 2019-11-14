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

    System.out.println("\nINFO Semantic Analysis - Analyzing program " + programNo + "...");

    generateAST();

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
   * Generates an AST from the given tokens.
   *
   * @return A SyntaxTree containing the AST generated from the given tokens.
   */
  public SyntaxTree generateAST() {
    ast = block();

    return ast;
  }

  /**
   * Used to add a block, and adds a new scope.
   */
  private SyntaxTree block() {
    SyntaxTree blockTree = new SyntaxTree("<Block>");
    verboseWriter("Block");

    if (qol("L_BRACE")) {
      match("L_BRACE");
      stmt(blockTree);

    } else {
      match("L_BRACE");
    }
    return blockTree;
  }

  /**
   * Stmt -> PrintStmt | AssignStmt | VarDecl | WhileStmt | IfStmt | Block
   */
  private void stmt(SyntaxTree parent) {
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
    SyntaxTree printStmtTree = new SyntaxTree("<Print Statement>");
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
    SyntaxTree assignStmtTree = new SyntaxTree("<Assignment Statement>");
    verboseWriter("assignmentStatement");

    if (qol("[a-z]|CHAR")) {
      terminal(assignStmtTree);
      match("ASSIGN_OP");
      expr(assignStmtTree);
    }

    return assignStmtTree;
  }

  /**
   * VarDecl -> type id
   */
  private SyntaxTree varDecl() {
    SyntaxTree varDeclTree = new SyntaxTree("<Variable Declaration>");
    verboseWriter("varDecl");

    if (qol("[ISB]_TYPE")) {
      terminal(varDeclTree);
      terminal(varDeclTree);
    }

    return varDeclTree;
  }

  /**
   * WhileStmt -> while BoolExpr Block
   */
  private SyntaxTree whileStmt() {
    SyntaxTree whileStmtTree = new SyntaxTree("<While Statement>");
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
    SyntaxTree ifStmtTree = new SyntaxTree("<If Statement>");
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
  private void expr(SyntaxTree parent) {
    if (qol("INT|[0-9]")) {
      intExpr(parent);
    } else if (qol("STRING")) {
      strExpr(parent);
    } else if (qol("L_PAREN|[TF]_BOOL")) {
      boolExpr(parent);
    } else if (qol("[a-z]|CHAR")) {
      terminal(parent);
    }
  }

  /**
   * IntExpr -> digit intOp Expr | digit
   */
  private void intExpr(SyntaxTree parent) {
    if (qol("[0-9]|INT") && Pattern
        .matches("\\+|INT_OP", tokenList.get(1).getFlavor())) {
      terminal(parent);
      terminal(parent);
      expr(parent);
    } else if (qol("[0-9]|INT")) {
      terminal(parent);
    }
  }

  /**
   * StrExpr -> " CharList "
   */
  private void strExpr(SyntaxTree parent) {
    if (qol("STRING")) {
      match("STRING");
      charList(parent);
      match("STRING");
    }
  }

  /**
   * BoolExpr -> ( Expr BoolOp Expr ) | BoolVal
   */
  private void boolExpr(SyntaxTree parent) {
    if (qol("L_PAREN")) {
      match("L_PAREN");
      expr(parent);
      terminal(parent);
      expr(parent);
      match("R_PAREN");

    } else if (qol("[TF]_BOOL")) {
      terminal(parent);
    }
  }

  /**
   * CharList -> CharVal CharList | space CharList | lambda
   */
  private void charList(SyntaxTree parent) {

    if (qol("[a-z]|CHAR")) {
      terminal(parent);
      charList(parent);

    } else if (qol(" ")) {
      terminal(parent);
      charList(parent);

    } else if (qol("STRING")) {
      //intentionally left blank for lambda set
    }
  }

  /**
   * pops the terminal
   */
  private void terminal(SyntaxTree parent) {

    parent.add("[" + pop().getOriginal() + "]");
  }

  /**
   * Checks a variable that is being assigned to see if it is the correct type to be assigned to the
   * variable, or if the variable has not been declared. (assignment trigger)
   *
   * @return True if it is correct, false otherwise.
   */
  public boolean typeCheck() {
    return false;
  }

  /**
   * Checks a variable to see if it is legal in the scope it is currently being attempted to be
   * declared in. (declaration trigger)
   *
   * @return True if it is legal, false otherwise.
   */
  public boolean checkScope() {
    return false;
  }

  /**
   * Checks to see if variables have been unused, and if variables have been used without being
   * assigned. (end of scope/block trigger)
   */
  public boolean bestPractices() {
    return false;
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
  private List<Token> match(String toMatch) {
    Token currentToken = peek(tokenList);
    if (Pattern.matches(toMatch, currentToken.getFlavor())) {
      //pop topmost token off of stack
      pop();
    } else {
      error(toMatch);
      //clears the stack
    }
    return null;
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
   * Prints the AST.
   */
  public void printTree() {
    System.out.println("\nINFO Semantic Analysis - Printing AST for program " + programNo + "...");
    System.out.println(ast.depthFirstTraversal());
  }

}
