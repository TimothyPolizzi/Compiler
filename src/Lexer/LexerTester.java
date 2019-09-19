package Lexer;

import java.util.*;
import Lexer.Lexer;

public class LexerTester {

  public static void main(String[] args) {
    String text = "Tim is here$";
    String quote = "\"I enjoy food\" 123$";
    String quoteNoEnd = "\"Big Yoshi$";
    String integer = "123456790$";
    String decimal = "123.456$";
    String keywords = "int a = 123; for(int i = 0; i < 10; i++) { print(\"hi\");};$";
    String javaKeywords = "int abc = 123; \nfor(int incr = 0; incr < 10; incr++) {\nif( a != b) {"
        + "\n System.out.print(\"hi\")\n}\n}$";
    String noSpaces = "abcint123printwhile$";

    String multiProgram = integer + decimal + quote + javaKeywords + noSpaces;

    Lexer lex = new Lexer(multiProgram, true);
  }

}
