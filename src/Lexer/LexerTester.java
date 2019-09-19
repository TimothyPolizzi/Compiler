package Lexer;

import java.util.*;
import Lexer.Lexer;

public class LexerTester {

  public static void main(String[] args) {
    String text = "Tim is here$";
    String quote = "\"I enjoy food\"$";
    String quoteNoEnd = "\"Big Yoshi$";
    String integer = "123456790$";
    String decimal = "123.456$";
    String keywords = "int a = 123; for(int i = 0; i < 10; i++) { print(\"hi\");};$";
    String javaKeywords = "int abc = 123; for(int incr = 0; incr < 10; incr++) { if( a != b) { System.out.print(\"hi\")};}$";
    String noSpaces = "abcint123printwhile";

    Lexer lex = new Lexer(noSpaces);
  }

}
