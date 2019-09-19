package Lexer;

import java.util.regex.Pattern;

public class Token {
  private String flavor;
  private String original;
  private int line;
  private int pos;

  public Token(String original, int line, int pos) {
    this.original = original;
    this.line = line;
    this.pos = pos;

    setFlavor();
  }

  private void setFlavor() {
    
  }

  public String toString(){
    String toString = "DEBUG Lexer - " + flavor + " [ " + original + " ] found at (" + line + ":"
        + pos + ")";
    return toString;
  }
}
