package Parser;

import Lexer.Token;
import java.util.ArrayList;
import java.util.Collection;
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
  private List cst;
  private boolean success;
  private int programNo;

  /**
   * Alan you really like giving us brain damage. I'm going to omit the comment for the recursive
   * calls otherwise I'd have a paragraph a piece, but in order to save me the trouble of having to
   * write a brand new type of tree just for this project, I'm using Lists. I take a list, pass it
   * off to my recursive method then tell it to fill it. Each recursive method slaps in all its
   * terminals, and makes a new list for each non-terminal, then repeats the process.
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
    cst = new ArrayList();
    success = true;

    System.out.println("\nINFO Parser - Parsing program " + programNo + "...");

    verboseWriter("parse");
    cst.add("Program");
    ArrayList programList = new ArrayList();

    cst.add(parse(programList));

    if (fail) {
      System.out.println("\nINFO Parser - Parser failed with 1 error");
      success = false;
    } else {
      System.out.println("\nINFO Parser - Parse completed successfully");
    }
  }

  /**
   * Program -> Block $
   */
  public ArrayList parse(ArrayList programList) {
    verboseWriter("parseProgram");
    if (qol("L_BRACE")) {
      ArrayList blockList = new ArrayList();

      programList.add("Block");
      programList.add(block(blockList));
      match("EOP");
      programList.add("$");
    }
    return programList;
  }

  /**
   * Block -> { StmtList }
   */
  private ArrayList block(ArrayList blockList) {
    verboseWriter("block");
    if (qol("L_BRACE")) {
      ArrayList stmtList = new ArrayList();

      match("L_BRACE");
      blockList.add("{");
      blockList.add("Statement List");
      blockList.add(stmtList(stmtList));
      match("R_BRACE");
      blockList.add("}");
    }

    return blockList;
  }

  /**
   * StmtList -> Stmt StmtList | lambda
   */
  private ArrayList stmtList(ArrayList stmtList) {
    verboseWriter("statementList");
    // Check if next token is: {,if, print, <char item>, int, char, boolean, while
    if (qol("L_BRACE|(PRINT|IF)_STMT|(CHAR|[a-z])|([ISB]_TYPE)|WHILE_LOOP")) {
      ArrayList sList = new ArrayList();
      ArrayList stmtList2 = new ArrayList();

      stmtList.add("Statement");
      stmtList.add(stmt(sList));
      stmtList.add("Statement List");
      stmtList.add(stmtList(stmtList2));
    } else if (qol("R_BRACE")) {
      // intentionally left blank for lambda set
    }
    return stmtList;
  }

  /**
   * Stmt -> PrintStmt | AssignStmt | VarDecl | WhileStmt | IfStmt | Block
   */
  private ArrayList stmt(ArrayList sList) {
    ArrayList subList = new ArrayList();
    verboseWriter("statement");
    if (qol("PRINT_STMT")) {

      sList.add("Print Statement");
      sList.add(printStmt(subList));
    } else if (qol("[a-z]|CHAR")) {

      sList.add("Assign Statement");
      sList.add(assignStmt(subList));
    } else if (qol("[ISB]_TYPE")) {

      sList.add("Variable Deceleration");
      sList.add(varDecl(subList));
    } else if (qol("WHILE_LOOP")) {

      sList.add("While Statement");
      sList.add(whileStmt(subList));
    } else if (qol("IF_STMT")) {

      sList.add("If Statement");
      sList.add(ifStmt(subList));
    } else if (qol("L_BRACE")) {

      sList.add("Block");
      sList.add(block(subList));
    }

    return sList;
  }

  /**
   * PrintStmt -> print ( Expr )
   */
  private ArrayList printStmt(ArrayList printList) {
    verboseWriter("printStatement");
    if (qol("PRINT_STMT")) {
      ArrayList exprList = new ArrayList();

      match("PRINT_STMT");
      printList.add("print");
      match("L_PAREN");
      printList.add("(");
      printList.add("Expression");
      printList.add(expr(exprList));
      match("R_PAREN");
      printList.add(")");
    }

    return printList;
  }

  /**
   * AssignStmt -> id = Expr
   */
  private ArrayList assignStmt(ArrayList assignList) {
    verboseWriter("assignmentStatement");
    if (qol("[a-z]|CHAR")) {
      ArrayList idList = new ArrayList();
      ArrayList exprList = new ArrayList();

      assignList.add("ID");
      assignList.add(id(idList));
      match("ASSIGN_OP");
      assignList.add("=");
      assignList.add("Expression");
      assignList.add(expr(exprList));
    }

    return assignList;
  }

  /**
   * VarDecl -> type id
   */
  private ArrayList varDecl(ArrayList varDeclList) {
    verboseWriter("varDecl");
    if (qol("[ISB]_TYPE")) {
      ArrayList idList = new ArrayList();

      varDeclList.add("Type");
      varDeclList.add(type());
      varDeclList.add("ID");
      varDeclList.add(id(idList));
    }

    return varDeclList;
  }

  /**
   * WhileStmt -> while BoolExpr Block
   */
  private ArrayList whileStmt(ArrayList whileStmtList) {
    verboseWriter("whileStatement");
    if (qol("WHILE_LOOP")) {
      ArrayList boolExprList = new ArrayList();
      ArrayList blockList = new ArrayList();

      match("WHILE_LOOP");
      whileStmtList.add("while");
      whileStmtList.add("Boolean Expression");
      whileStmtList.add(boolExpr(boolExprList));
      whileStmtList.add("Block");
      whileStmtList.add(block(blockList));
    }

    return whileStmtList;
  }

  /**
   * IfStmt -> if BoolExpr Block
   */
  private ArrayList ifStmt(ArrayList ifStmtList) {
    verboseWriter("ifStatement");
    if (qol("IF_STMT")) {
      ArrayList boolExprList = new ArrayList();
      ArrayList blockList = new ArrayList();

      match("IF_STMT");
      ifStmtList.add("if");
      ifStmtList.add("Boolean Expression");
      ifStmtList.add(boolExpr(boolExprList));
      ifStmtList.add("Block");
      ifStmtList.add(block(blockList));
    }

    return ifStmtList;
  }

  /**
   * Expr -> IntExpr | StrExpr | BoolExpr | ID
   */
  private ArrayList expr(ArrayList exprList) {
    ArrayList subList = new ArrayList();
    verboseWriter("expression");
    if (qol("[0-9]")) {
      exprList.add("Integer Expression");
      exprList.add(intExpr(subList));
    } else if (qol("STRING")) {
      exprList.add("String Expression");
      exprList.add(strExpr(subList));
    } else if (qol("L_PAREN|[TF]_BOOL")) {
      exprList.add("Boolean Expression");
      exprList.add(boolExpr(subList));
    } else if (qol("[a-z]")) {
      exprList.add("ID");
      exprList.add(id(subList));
    }

    return exprList;
  }

  /**
   * IntExpr -> digit intOp Expr | digit
   */
  private ArrayList intExpr(ArrayList intExprList) {
    verboseWriter("intExpression");
    if (qol("[0-9] +")) {
      ArrayList exprList = new ArrayList();

      intExprList.add("Digit");
      intExprList.add(digit());
      intExprList.add("Integer Operator");
      intExprList.add(intOp());
      intExprList.add("Expression");
      intExprList.add(expr(exprList));
    } else if (qol("[0-9]")) {
      intExprList.add("Digit");
      intExprList.add(digit());
    }

    return intExprList;
  }

  /**
   * StrExpr -> " CharList "
   */
  private ArrayList strExpr(ArrayList strExprList) {
    verboseWriter("stringExpression");
    if (qol("STRING")) {
      ArrayList charList = new ArrayList();

      match("STRING");
      strExprList.add("\"");
      strExprList.add("Character List");
      strExprList.add(charList(charList));
      match("STRING");
      strExprList.add("\"");
    }

    return strExprList;
  }

  /**
   * BoolExpr -> ( Expr BoolOp Expr ) | BoolVal
   */
  private ArrayList boolExpr(ArrayList boolExprList) {
    verboseWriter("booleanExpression");
    if (qol("L_PAREN")) {
      ArrayList exprList1 = new ArrayList();
      ArrayList exprList2 = new ArrayList();

      match("L_PAREN");
      boolExprList.add("(");
      boolExprList.add("Expression");
      boolExprList.add(expr(exprList1));
      boolExprList.add("Boolean Operator");
      boolExprList.add(boolOp());
      boolExprList.add("Expression");
      boolExprList.add(expr(exprList2));
      match("R_PAREN");
      boolExprList.add(")");
    } else if (qol("[TF]_BOOL")) {

      boolExprList.add("Boolean Value");
      boolExprList.add(boolVal());
    }

    return boolExprList;
  }

  /**
   * ID -> CharVal
   */
  private ArrayList id(ArrayList idList) {
    verboseWriter("id");
    if (qol("[a-z]|CHAR")) {

      idList.add("Character Value");
      idList.add(charVal());
    }

    return idList;
  }

  /**
   * CharList -> CharVal CharList | space CharList | lambda
   */
  private ArrayList charList(ArrayList charList) {
    ArrayList charList2 = new ArrayList();
    verboseWriter("characterList");
    if (qol("[a-z]|CHAR")) {

      charList.add("Character Value");
      charList.add(charVal());
      charList.add("Character List");
      charList.add(charList(charList2));
    } else if (qol(" ")) {

      charList.add("Space");
      charList.add(space());
      charList.add("Character List");
      charList.add(charList(charList2));
    } else if (qol("STRING")) {
      //intentionally left blank for lambda set
    }

    return charList;
  }

  /**
   * type -> int | string | boolean
   */
  private String type() {
    verboseWriter("type");
    if (qol("[ISB]_TYPE")) {
      //TODO:pop token
      return pop().getOriginal();
    }
    return null;
  }

  /**
   * charVal -> a | b | c | ... | z
   */
  private String charVal() {
    verboseWriter("characterValue");
    if (qol("[a-z]|CHAR")) {
      //pop token
      return pop().getOriginal();
    }
    return null;
  }

  /**
   * space -> " "
   */
  private String space() {
    verboseWriter("space");
    if (qol(" ")) {
      match(" ");
      return " ";
    }
    return null;
  }

  /**
   * digit -> 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 | 8 | 9
   */
  private String digit() {
    verboseWriter("digit");
    if (qol("[0-9]")) {
      //pop token
      return pop().getOriginal();
    }
    return null;
  }

  /**
   * boolOp -> == | !=
   */
  private String boolOp() {
    verboseWriter("booleanOperator");
    if (qol("[NOT_]?EQUAL")) {
      //pop token
      return pop().getOriginal();
    }
    return null;
  }

  /**
   * boolVal -> true | false
   */
  private String boolVal() {
    verboseWriter("booleanValue");
    if (qol("[TF]_BOOL")) {
      //pop token
      return pop().getOriginal();
    }
    return null;
  }

  /**
   * intOp -> +
   */
  private String intOp() {
    verboseWriter("integerOperator");
    if (qol("INT_OP")) {
      match("INT_OP");
      return "+";
    }
    return null;
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
      System.out.println(
          "ERROR Parser - Expected [" + toMatch + "] got [" + currentToken.getFlavor()
              + "] with value '" + currentToken.getOriginal() + "' on line " + currentToken
              .getLine());
      fail = true;
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
   * Prints the CST in a human readable form
   */
  public void printCST() {
    System.out.println("\nINFO Parser - CST for program " + programNo + "...");

    printCST(cst, 0);
  }

  private List printCST(List cont, int level) {
    for (int i = 0; i < cont.size(); i++) {
      if (cont.get(i) instanceof Collection) {
        printCST((List) cont.get(i), level + 1);
      } else {
        int iter = 0;
        while (iter < level) {
          System.out.print("-");
          iter++;
        }
        System.out.println(cont.get(i));
      }
    }

    return null;
  }

  /**
   * Gets the CST
   *
   * @return cst
   */
  public List getCST() {
    return cst;
  }

  /**
   * did it work?
   *
   * @return y or n
   */
  public boolean success() {
    return success;
  }
}
