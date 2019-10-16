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

    System.out.println("\nINFO Parser - Parsing program " + programNo + "...");

    parse();
  }

  /**
   * Start
   */
  public void parse() {
    if (qol("L_BRACE")) {
      block();
      match("EOP");
    }
  }

  /**
   * stage 1
   */
  private void block() {
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
    // Check if next token is: {,if, print, <char item>, int, char, boolean, while
    if (qol("L_BRACE|(PRINT|IF)_STMT|[a-z]|([ISB]_TYPE)|WHILE_LOOP")) {
      stmt();
      stmtList();
    } else if (qol("R_BRACE")) {
      return;
    }
  }

  private void stmt() {
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
    if (qol("PRINT_STMT")) {
      match("PRINT_STMT");
      match("L_PAREN");
      expr();
      match("R_PAREN");
    }
  }

  private void assignStmt() {
    if (qol("[a-z]")) {
      id();
      match("ASSIGN_OP");
      expr();
    }
  }

  private void varDecl() {
    if (qol("[ISB]_TYPE")) {
      type();
      id();
    }
  }

  private void whileStmt() {
    if (qol("WHILE_LOOP")) {
      match("WHILE_LOOP");
      boolExpr();
      block();
    }
  }

  private void ifStmt() {
    if (qol("IF_STMT")) {
      match("IF_STMT");
      boolExpr();
      block();
    }
  }

  private void expr() {
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
    if(qol("[0-9] +")) {
      digit();
      intOp();
      expr();
    } else if(qol("[0-9]")) {
      digit();
    }
  }

  private void strExpr() {
    if(qol("STRING")) {
      match("STRING");
      charList();
      match("STRING");
    }
  }

  private void boolExpr() {
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
    if(qol("[a-z]")) {
      charVal();
    }
  }

  private void charList() {
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
    if(qol("[ISB]_TYPE")) {
      // pop token
    }
  }

  private void charVal() {
    if(qol("[a-z]")) {
      //pop token
    }
  }

  private void space() {
    if(qol(" ")) {
      match(" ");
    }
  }

  private void digit() {
    if(qol("[0-9]")) {
      //pop token
    }
  }

  private void boolOp() {
    if(qol("[NOT_]?EQUAL")) {
      //pop token
    }
  }

  private void boolVal() {
    if(qol("[TF]_BOOL")) {
      //pop token
    }
  }

  private void intOp() {
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

}
