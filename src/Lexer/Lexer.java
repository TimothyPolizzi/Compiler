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

    char[] charList = fileToRead.toCharArray();

    while(lastGood < charList.length) {
      String currentToken = "";

      while((current < charList.length) && !isSymbol(Character.toString(charList[current]))) {
        currentToken = charList[current] + currentToken;
        if(isLegal(currentToken)) {

        }

      }
    }
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

    return null;
  }

  /**
   * Checks the input to see if it is a symbol legal in the grammar.
   * @param toCheck The input that is to be checked if it is legal.
   * @return True if the input is legal, false otherwise.
   */
  private boolean isSymbol(String toCheck) {
    // Legal symbols: ',{,},(,),=,",==,!=,+,<,>,<=,>=
    String symbolRegex = "\\{ | } | ( \\| ) | = | \" | == | != | \\+ | < | > | <= | >= | .";
    boolean toReturn = false;

    if(Pattern.matches(symbolRegex, toCheck)) {
      toReturn = true;
    }

    return toReturn;
  }

  private boolean isLegal(String toCheck) {
    boolean toReturn = false;

    return toReturn;
  }
}
