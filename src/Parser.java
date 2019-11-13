import java.util.List;
import java.util.regex.Pattern;

/**
 * A parser for Alan Labouseur's Compilers class.
 *
 * @author Tim Polizzi
 */
public class Parser {

  private List<Token> tokenList;
  private boolean verbose;
  private boolean fail;
  private int errCount;
  private boolean success;
  private int programNo;
  private SyntaxTree tree;

  /**
   * Alan you really like giving me brain damage.
   *
   * This program works by recursively combining trees with size 1 to make a big tree.
   *
   * @param programNo The numerical identifier of which program is being parsed.
   * @param tokenList The list of tokens that has been returned from the Lexer.
   * @param verbose The boolean value that determines if verbose mode is on.
   */
  public Parser(int programNo, List<Token> tokenList, boolean verbose) {
    this.tokenList = tokenList;
    this.verbose = verbose;
    this.programNo = programNo;
    fail = false;
    success = true;
    errCount = 0;

    System.out.println("\nINFO Parser - Parsing program " + programNo + "...");

    verboseWriter("parse");
    parse();

    if (fail) {
      System.out.println("INFO Parser - Parser failed with " + errCount + " error(s)");
      success = false;
    } else {
      System.out.println("INFO Parser - Parse completed successfully");
    }
  }

  /**
   * Program -> Block $
   */
  public SyntaxTree parse() {
    verboseWriter("parseProgram");
    tree = new SyntaxTree("Program");

    if (qol("L_BRACE")) {
      tree.add(block());
      match("EOP");
      tree.add("$");
    } else {
      match("L_BRACE");
    }
    return tree;
  }

  /**
   * Block -> { StmtList }
   */
  private SyntaxTree block() {
    verboseWriter("block");
    SyntaxTree blockTree = new SyntaxTree("Block");

    if (qol("L_BRACE")) {
      match("L_BRACE");
      blockTree.add("{");
      blockTree.add(stmtList());
      match("R_BRACE");
      blockTree.add("}");
    }

    return blockTree;
  }

  /**
   * StmtList -> Stmt StmtList | lambda
   */
  private SyntaxTree stmtList() {
    verboseWriter("statementList");
    SyntaxTree stmtListTree = new SyntaxTree("StmtList");

    // Check if next token is: {,if, print, <char item>, int, char, boolean, while
    if (qol("L_BRACE|(PRINT|IF)_STMT|(CHAR|[a-z])|([ISB]_TYPE)|WHILE_LOOP")) {
      stmtListTree.add(stmt());
      stmtListTree.add(stmtList());
    } else if (qol("R_BRACE")) {
      // intentionally left blank for lambda set
    }

    return stmtListTree;
  }

  /**
   * Stmt -> PrintStmt | AssignStmt | VarDecl | WhileStmt | IfStmt | Block
   */
  private SyntaxTree stmt() {
    SyntaxTree stmtTree = new SyntaxTree("Stmt");
    verboseWriter("statement");

    if (qol("PRINT_STMT")) {
      stmtTree.add(printStmt());

    } else if (qol("[a-z]|CHAR")) {
      stmtTree.add(assignStmt());

    } else if (qol("[ISB]_TYPE")) {
      stmtTree.add(varDecl());

    } else if (qol("WHILE_LOOP")) {
      stmtTree.add(whileStmt());

    } else if (qol("IF_STMT")) {
      stmtTree.add(ifStmt());

    } else if (qol("L_BRACE")) {
      stmtTree.add(block());

    }

    return stmtTree;
  }

  /**
   * PrintStmt -> print ( Expr )
   */
  private SyntaxTree printStmt() {
    verboseWriter("printStatement");
    SyntaxTree printStmtTree = new SyntaxTree("PrintStmt");

    if (qol("PRINT_STMT")) {
      match("PRINT_STMT");
      printStmtTree.add("print");
      match("L_PAREN");
      printStmtTree.add("(");
      printStmtTree.add(expr());
      match("R_PAREN");
      printStmtTree.add(")");
    }

    return printStmtTree;
  }

  /**
   * AssignStmt -> id = Expr
   */
  private SyntaxTree assignStmt() {
    verboseWriter("assignmentStatement");
    SyntaxTree assignStmtTree = new SyntaxTree("AssignStmt");

    if (qol("[a-z]|CHAR")) {
      assignStmtTree.add(id());
      match("ASSIGN_OP");
      assignStmtTree.add("=");
      assignStmtTree.add(expr());
    }

    return assignStmtTree;
  }

  /**
   * VarDecl -> type id
   */
  private SyntaxTree varDecl() {
    verboseWriter("varDecl");
    SyntaxTree varDeclTree = new SyntaxTree("VarDecl");

    if (qol("[ISB]_TYPE")) {
      varDeclTree.add(type());
      varDeclTree.add(id());
    }

    return varDeclTree;
  }

  /**
   * WhileStmt -> while BoolExpr Block
   */
  private SyntaxTree whileStmt() {
    verboseWriter("whileStatement");
    SyntaxTree whileStmtTree = new SyntaxTree("WhileStmt");

    if (qol("WHILE_LOOP")) {
      match("WHILE_LOOP");
      whileStmtTree.add("while");
      whileStmtTree.add(boolExpr());
      whileStmtTree.add(block());
    }

    return whileStmtTree;
  }

  /**
   * IfStmt -> if BoolExpr Block
   */
  private SyntaxTree ifStmt() {
    verboseWriter("ifStatement");
    SyntaxTree ifStmtTree = new SyntaxTree("IfStmt");

    if (qol("IF_STMT")) {
      match("IF_STMT");
      ifStmtTree.add("if");
      ifStmtTree.add(boolExpr());
      ifStmtTree.add(block());
    }

    return ifStmtTree;
  }

  /**
   * Expr -> IntExpr | StrExpr | BoolExpr | ID
   */
  private SyntaxTree expr() {
    verboseWriter("expression");
    SyntaxTree exprTree = new SyntaxTree("Expr");

    if (qol("INT|[0-9]")) {
      exprTree.add(intExpr());
    } else if (qol("STRING")) {
      exprTree.add(strExpr());
    } else if (qol("L_PAREN|[TF]_BOOL")) {
      exprTree.add(boolExpr());
    } else if (qol("[a-z]|CHAR")) {
      exprTree.add(id());
    }

    return exprTree;
  }

  /**
   * IntExpr -> digit intOp Expr | digit
   */
  private SyntaxTree intExpr() {
    verboseWriter("intExpression");
    SyntaxTree intExprTree = new SyntaxTree("IntExpr");

    if (qol("[0-9]|INT") && Pattern
        .matches("\\+|INT_OP", tokenList.get(1).getFlavor())) {
      intExprTree.add(digit());
      intExprTree.add(intOp());
      intExprTree.add(expr());
    } else if (qol("[0-9]|INT")) {
      intExprTree.add(digit());
    }

    return intExprTree;
  }

  /**
   * StrExpr -> " CharList "
   */
  private SyntaxTree strExpr() {
    verboseWriter("stringExpression");
    SyntaxTree strExprTree = new SyntaxTree("StrExpr");

    if (qol("STRING")) {
      match("STRING");
      strExprTree.add("\"");
      strExprTree.add(charList());
      match("STRING");
      strExprTree.add("\"");
    }

    return strExprTree;
  }

  /**
   * BoolExpr -> ( Expr BoolOp Expr ) | BoolVal
   */
  private SyntaxTree boolExpr() {
    verboseWriter("booleanExpression");
    SyntaxTree boolExprTree = new SyntaxTree("BoolExpr");

    if (qol("L_PAREN")) {
      match("L_PAREN");
      boolExprTree.add("(");
      boolExprTree.add(expr());
      boolExprTree.add(boolOp());
      boolExprTree.add(expr());
      match("R_PAREN");
      boolExprTree.add(")");

    } else if (qol("[TF]_BOOL")) {
      boolExprTree.add(boolVal());
    } else {
      error("L_PAREN");
    }

    return boolExprTree;
  }

  /**
   * ID -> CharVal
   */
  private SyntaxTree id() {
    verboseWriter("id");
    SyntaxTree idTree = new SyntaxTree("ID");

    if (qol("[a-z]|CHAR")) {
      idTree.add(charVal());
    } else {
      error("CHAR");
    }

    return idTree;
  }

  /**
   * CharList -> CharVal CharList | space CharList | lambda
   */
  private SyntaxTree charList() {
    verboseWriter("characterList");
    SyntaxTree charListTree = new SyntaxTree("CharList");

    if (qol("[a-z]|CHAR")) {
      charListTree.add(charVal());
      charListTree.add(charList());

    } else if (qol(" ")) {
      charListTree.add(space());
      charListTree.add(charList());

    } else if (qol("STRING")) {
      //intentionally left blank for lambda set
    }

    return charListTree;
  }

  /**
   * type -> int | string | boolean
   */
  private SyntaxTree type() {
    verboseWriter("type");
    SyntaxTree typeTree = new SyntaxTree("type");

    if (qol("[ISB]_TYPE")) {
      typeTree.add(pop().getOriginal());
      return typeTree;
    }
    return null;
  }

  /**
   * charVal -> a | b | c | ... | z
   */
  private SyntaxTree charVal() {
    verboseWriter("characterValue");
    SyntaxTree charValTree = new SyntaxTree("charVal");

    if (qol("[a-z]|CHAR")) {
      charValTree.add(pop().getOriginal());
      return charValTree;
    }
    return null;
  }

  /**
   * space -> " "
   */
  private SyntaxTree space() {
    verboseWriter("space");
    SyntaxTree spaceTree = new SyntaxTree("space");

    if (qol(" ")) {
      spaceTree.add(pop().getOriginal());
      return spaceTree;
    }
    return null;
  }

  /**
   * digit -> 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
   */
  private SyntaxTree digit() {
    verboseWriter("digit");
    SyntaxTree digitTree = new SyntaxTree("digit");

    if (qol("[0-9]|INT")) {
      digitTree.add(pop().getOriginal());
      return digitTree;
    }
    return null;
  }

  /**
   * boolOp -> == | !=
   */
  private SyntaxTree boolOp() {
    verboseWriter("booleanOperator");
    SyntaxTree boolOpTree = new SyntaxTree("BoolOp");

    if (qol("(NOT_)?EQUAL")) {
      boolOpTree.add(pop().getOriginal());
      return boolOpTree;
    }
    return null;
  }

  /**
   * boolVal -> true | false
   */
  private SyntaxTree boolVal() {
    verboseWriter("booleanValue");
    SyntaxTree boolValTree = new SyntaxTree("boolVal");

    if (qol("[TF]_BOOL")) {
      boolValTree.add(pop().getOriginal());
      return boolValTree;
    }
    return null;
  }

  /**
   * intOp -> +
   */
  private SyntaxTree intOp() {
    verboseWriter("integerOperator");
    SyntaxTree intOpTree = new SyntaxTree("intOp");

    if (qol("INT_OP")) {
      intOpTree.add(pop().getOriginal());
      return intOpTree;
    }
    return null;
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

    fail = true;
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
      System.out.println("DEBUG Parser - " + method + "()");
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
   * Get the CST.
   *
   * @return tree, the CST.
   */
  public SyntaxTree getTree() {
    return tree;
  }

  /**
   * Prints the CST.
   */
  public void printTree() {
    System.out.println("\nINFO Parser - Printing CST for program " + programNo + "...");
    System.out.println(tree.depthFirstTraversal());
  }
}
