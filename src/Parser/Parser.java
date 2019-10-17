package Parser;

import Lexer.Token;
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

  /**
   * Alan you like giving us brain damage.
   *
   * @param programNo The numerical identifier of which program is being parsed.
   * @param tokenList The list of tokens that has been returned from the Lexer.
   * @param verbose The boolean value that determines if verbose mode is on.
   */
  public Parser(int programNo, List<Token> tokenList, boolean verbose) {
    this.tokenList = tokenList;
    this.verbose = verbose;
    fail = false;

    System.out.println("\nINFO Parser - Parsing program " + programNo + "...");

    verboseWriter("parse");

    parse();

    if(fail) {
      System.out.println("\nINFO Parser - Parser failed with 1 error");
    } else {
      System.out.println("\nINFO Parser - Parse completed successfully");
    }
  }

  /**
   * Start
   */
  public void parse() {
    verboseWriter("parseProgram");
    if (qol("L_BRACE")) {
      block();
      match("EOP");
    }
  }

  /**
   * stage 1
   */
  private void block() {
    verboseWriter("block");
    if (qol("L_BRACE")) {
      match("L_BRACE");
      stmtList();
      match("R_BRACE");
    }
  }

  /**
   *
   */
  private void stmtList() {
    verboseWriter("statementList");
    // Check if next token is: {,if, print, <char item>, int, char, boolean, while
    if (qol("L_BRACE|(PRINT|IF)_STMT|[a-z]|([ISB]_TYPE)|WHILE_LOOP")) {
      stmt();
      stmtList();
    } else if (qol("R_BRACE")) {
      return;
    }
  }

  private void stmt() {
    verboseWriter("statement");
    if (qol("PRINT_STMT")) {
      printStmt();
    } else if (qol("[a-z]")) {
      assignStmt();
    } else if (qol("[ISB]_TYPE")) {
      varDecl();
    } else if (qol("WHILE_LOOP")) {
      whileStmt();
    } else if (qol("IF_STMT")) {
      ifStmt();
    } else if (qol("L_BRACE")) {
      block();
    }
  }

  private void printStmt() {
    verboseWriter("printStatement");
    if (qol("PRINT_STMT")) {
      match("PRINT_STMT");
      match("L_PAREN");
      expr();
      match("R_PAREN");
    }
  }

  private void assignStmt() {
    verboseWriter("assignmentStatement");
    if (qol("[a-z]")) {
      id();
      match("ASSIGN_OP");
      expr();
    }
  }

  private void varDecl() {
    verboseWriter("varDecl");
    if (qol("[ISB]_TYPE")) {
      type();
      id();
    }
  }

  private void whileStmt() {
    verboseWriter("whileStatement");
    if (qol("WHILE_LOOP")) {
      match("WHILE_LOOP");
      boolExpr();
      block();
    }
  }

  private void ifStmt() {
    verboseWriter("ifStatement");
    if (qol("IF_STMT")) {
      match("IF_STMT");
      boolExpr();
      block();
    }
  }

  private void expr() {
    verboseWriter("expression");
    if (qol("[0-9]")) {
      intExpr();
    } else if (qol("STRING")) {
      strExpr();
    } else if (qol("L_PAREN|[TF]_BOOL")) {
      boolExpr();
    } else if (qol("[a-z]")) {
      id();
    }
  }

  private void intExpr() {
    verboseWriter("intExpression");
    if(qol("[0-9] +")) {
      digit();
      intOp();
      expr();
    } else if(qol("[0-9]")) {
      digit();
    }
  }

  private void strExpr() {
    verboseWriter("stringExpression");
    if(qol("STRING")) {
      match("STRING");
      charList();
      match("STRING");
    }
  }

  private void boolExpr() {
    verboseWriter("booleanExpression");
    if(qol("L_PAREN")) {
      match("L_PAREN");
      expr();
      boolOp();
      expr();
      match("R_PAREN");
    } else if (qol("[TF]_BOOL")) {
      boolVal();
    }
  }

  private void id() {
    verboseWriter("id");
    if(qol("[a-z]")) {
      charVal();
    }
  }

  private void charList() {
    verboseWriter("characterList");
    if(qol("[a-z]")) {
      charVal();
      charList();
    } else if (qol(" ")) {
      space();
      charList();
    } else if (qol("STRING")) {
      return;
    }
  }

  private void type() {
    verboseWriter("type");
    if(qol("[ISB]_TYPE")) {
      // pop token
    }
  }

  private void charVal() {
    verboseWriter("characterValue");
    if(qol("[a-z]")) {
      //pop token
    }
  }

  private void space() {
    verboseWriter("space");
    if(qol(" ")) {
      match(" ");
    }
  }

  private void digit() {
    verboseWriter("digit");
    if(qol("[0-9]")) {
      //pop token
    }
  }

  private void boolOp() {
    verboseWriter("booleanOperator");
    if(qol("[NOT_]?EQUAL")) {
      //pop token
    }
  }

  private void boolVal() {
    verboseWriter("booleanValue");
    if(qol("[TF]_BOOL")) {
      //pop token
    }
  }

  private void intOp() {
    verboseWriter("integerOperator");
    if(qol("INT_OP")) {
      match("INT_OP");
    }
  }

  /**
   * Look to match a terminal and kill everything if it doesn't.
   */
  private List<Token> match(String toMatch) {
    Token currentToken = peek(tokenList);
    if (Pattern.matches(toMatch, currentToken.getFlavor())) {
      //pop topmost token off of stack
    }
    System.out.println(
        "ERROR Parser - Expected [" + toMatch + "] got [" + currentToken.getFlavor()
            + "] with value '" + currentToken.getOriginal() + "' on line " + currentToken
            .getLine());
    //clears the stack
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
   * @return A boolean that determines if a given regex matches the top of the tokenList
   */
  private boolean qol(String regex) {
    return Pattern.matches(regex, peek(tokenList).getFlavor());
  }

  /**
   * Prints the method that would be printed in verbose mode if the program is in verbose mode.
   * @param method The name of the method that would be printed.
   */
  private void verboseWriter(String method) {
    if(verbose){
      System.out.println("DEBUG Parser - " + method + "()");
    }
  }

}
