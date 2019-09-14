package Lexer;

import java.util.*;
import Lexer.Lexer;

public class LexerTester {

  public static void main(String[] args) {
    Lexer lex = new Lexer("Tim is here");
    System.out.print(lex.checkIfLegal("{"));
  }

}
