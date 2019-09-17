package Lexer;

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
}
