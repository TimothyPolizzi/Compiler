package Lexer;

import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * A lexer for the SAD Compiler for Alan Labouseur's CMPT432 class.
 * @author Tim Polizzi
 */
public class Lexer {
  private ArrayList<String> tokenList;
  private int current;
  private int lastGood;

  /**
   * Analyzes a file file_to_read, and returns an list of tokens.
   * @param fileToRead The file that is to be turned into a list of tokens.
   */
  public Lexer(String fileToRead) {
    tokenList = new ArrayList<String>();
    current = 0;
    lastGood = 0;
  }

  /**
   * Checks the legality of a given string in the SAD Compiler language, and returns the flavor of
   * token that can be made if it is legal.
   * @param toCheck The string that needs to be checked for legality.
   * @return The flavor of the token if it is legal, null otherwise.
   */
  public String checkIfLegal(String toCheck) {
    // Integers are any number, without leading zeros
    String intRegex = "0|([1-9]\\d*)";
    // Doubles may are integers with a '.' followed by any digits
    String doubleRegex = intRegex + "\\.\\d+";
    // Legal keywords: print, while, if, int, string, boolean, false, true
    String keywordRegex = "(print)|(while)|(i((nt)|(f)))|(string)|(boolean)|(false)|(true)";
    // Legal symbols: ',{,},(,),=,",==,!=,+,<,>,<=,>=
    String symbolRegex = "{ | } | ( | ) | = | \" | == | != | +";

    if(Pattern.matches(symbolRegex, toCheck)) {
      System.out.println("works");
    }

    return null;
  }
}
