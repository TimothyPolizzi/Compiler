package Lexer;

import java.util.regex.Pattern;

public class Token {

  private String flavor;
  private String original;
  private int line;
  private int pos;
  private boolean string;

  /**
   * Generates a Token object that contains a String, it's line and position on the line, and the
   * "flavor" of the String which describes it's type.
   */
  public Token(String original, int line, int pos, boolean string) {
    this.original = original;
    this.line = line;
    this.pos = pos;
    this.string = string;
    flavor = "error";

    setFlavor();
  }

  /**
   * Sets the "Flavor" of the Token, so that it may be used in other parts of the compiler.
   */
  private void setFlavor() {
    String regexInt = "0|[1-9][0-9]*";
    String regexDouble = regexInt + "\\.[0-9]*";
    String regexChar = "[a-z]";

    String[] regexArr = {"\\$", "\\{", "}", "print", "\\(", "\\)", "=", "while", "if", "int",
        "string", "boolean", regexInt, regexDouble, regexChar, "\"", "true", "false", " ", "==",
        "!=", "\\+", "<", ">", ">=", "<="};
    String[] flavors = {"EOP", "L_BRACE", "R_BRACE", "PRINT_STMT", "L_PAREN", "R_PAREN",
        "ASSIGN_OP", "WHILE_LOOP", "IF_STMT", "I_TYPE", "S_TYPE", "B_TYPE", "INT", "DOUBLE",
        "CHAR", "STRING", "T_BOOL", "F_BOOL", "SPACE", "EQUAL", "NOT_EQUAL", "INT_OP", "L_BOOL",
        "G_BOOL", "GE_BOOL", "LE_BOOL"};

    if (string) {
      flavor = "LITERAL";
    } else {
      for (int i = 0; i < regexArr.length; i++) {
        if (Pattern.matches(regexArr[i], original)) {
          flavor = flavors[i];
        }
      }
    }
  }

  /**
   * Checks to see if there was an error in setting a flavor to the Token, which will only occur if
   * a illegal character has been entered into the lexer.
   */
  public boolean errorCheck() {
    boolean error = false;

    if (flavor.equals("error")) {
      error = true;
    }

    return error;
  }

  /**
   * Converts the Token into a string representation of itself, where it is either a Debug message
   * or an Error message.
   *
   * @return A string representation of the Token as either a Debug message or an Error message.
   */
  public String toString() {
    String toString;

    if (!errorCheck()) {
      toString =
          "DEBUG Lexer - " + flavor + " [ " + original + " ] found at (" + line + ":" + pos + ")";
    } else {
      toString = "ERROR Lexer - Error:" + line + ":" + pos + " Unrecognized Token: " + original;
    }
    return toString;
  }

  /**
   * Gets the original string the token was made out of.
   *
   * @return The original string.
   */
  public String getOriginal() {
    return original;
  }
}
