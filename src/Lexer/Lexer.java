package Lexer;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * A lexer for the SAD Compiler for Alan Labouseur's CMPT432 class.
 *
 * @author Tim Polizzi
 */
public class Lexer {

  private ArrayList<Token> tokenList;
  private int current;
  private int lastGood;

  /**
   * Analyzes a file file_to_read, and returns an list of tokens.
   *
   * @param fileToRead The file that is to be turned into a list of tokens.
   */
  public Lexer(String fileToRead) {
    tokenList = new ArrayList<Token>();
    current = 0;
    lastGood = 0;

    char[] charList = fileToRead.toCharArray();

    while (current < charList.length) {
      String currentToken = "";
      int line = 0;
      char currentChar = charList[current];

      // Check if the current item is a symbol
      if (isSymbol(currentChar)) {
        currentToken += currentChar;

        // If that symbol is two parts, check here
        if (isTwoPart(currentChar) && charList[current + 1] == '=') {
          current++;
          currentToken += charList[current];
        }

        current++;

        //check for comments, skip the commented section
      } else if (currentChar == '/') {
        current++;

        if (charList[current] == '*') {
          while ((charList[current] == '$') ||
              (charList[current] != '*' && charList[current + 1] == '/')) {
            current++;
          }
        } else {
          tokenList.add(new Token(Character.toString(charList[current - 1]), line, current - 1, false));
        }

        // what to do in case of quotes
      } else if (currentChar == '"') {
        int quoteLoop = current;

        tokenList.add(new Token(Character.toString(charList[quoteLoop]), line, quoteLoop, false));
        quoteLoop++;

        while (charList[quoteLoop] != '"' && charList[quoteLoop] != '$') {
          tokenList.add(new Token(Character.toString(charList[quoteLoop]), line, quoteLoop, true));
          quoteLoop++;
        }

        tokenList.add(new Token(Character.toString(charList[quoteLoop]), line, quoteLoop, false));
        current = quoteLoop + 1;

        // Check if it is a legal character or integer or '.' (for doubles)
      } else if (legalVal(currentChar)) {
        int start = current;
        lastGood = current + 1;
        String tempToken = "";

        while ((current < charList.length) && legalVal(charList[current])) {
          if (isKeyword(tempToken) || isNumber(tempToken)) {
            lastGood = current;
          }
          tempToken += charList[current];
          current++;
        }

        if (isKeyword(tempToken) || isNumber(tempToken)) {
          lastGood = current;
        }

        int endBound = lastGood - start;
        String goodToken = tempToken.substring(0, endBound);
        tokenList.add(new Token(goodToken, line, start, false));

        current = lastGood;

        // Something isn't right here
      } else {
        currentToken += currentChar;
        current++;
      }

      // skip spaces, ignore empty string
      if (!currentToken.equals(" ") && !currentToken.equals("")) {
        tokenList.add(new Token(currentToken, line, current, false));
      }
    }

    // print statement for testing purposes, this would be the "verbose mode" switch
    for(Token t : tokenList) {
      System.out.println(t.toString());
    }
  }

  /**
   * Checks if a given string is a keyword in the SAD compiler's grammar.
   *
   * @param toCheck The string to be checked if it is a keyword.
   * @return True if toCheck is a keyword, false otherwise.
   */
  private boolean isKeyword(String toCheck) {
    String keywordRegex = "(print)|(while)|(i((nt)|(f)))|(string)|(boolean)|(false)|(true)";
    boolean toReturn = false;

    if (Pattern.matches(keywordRegex, toCheck)) {
      toReturn = true;
    }

    return toReturn;
  }

  /**
   * Checks if a given string is a number in the SAD compiler's grammar.
   *
   * @param toCheck The string to be checked if it is a keyword.
   * @return True if toCheck is a number, false otherwise.
   */
  private boolean isNumber(String toCheck) {
    // Integers are any number, without leading zeros
    String intRegex = "0|([1-9]\\d*)";
    // Doubles may are integers with a '.' followed by any digits
    String doubleRegex = intRegex + "\\.\\d+";

    String numberRegex = intRegex + "|" + doubleRegex;
    boolean toReturn = false;

    if (Pattern.matches(numberRegex, toCheck)) {
      toReturn = true;
    }

    return toReturn;
  }

  /**
   * Checks the input to see if it is a symbol legal in the grammar.
   *
   * @param toCheck The input that is to be checked if it is legal.
   * @return True if the input is legal, false otherwise.
   */
  private boolean isSymbol(char toCheck) {
    // Legal symbols: {,},(,),=,+,<,>,$
    String symbolRegex = "[{}()=!+<>$]";
    boolean toReturn = false;

    if (Pattern.matches(symbolRegex, Character.toString(toCheck))) {
      toReturn = true;
    }

    return toReturn;
  }

  /**
   * Checks to see if the inputted symbol is part of a two part symbol.
   *
   * @param toCheck The string that is to be checked.
   * @return True if the item is a part of a two part symbol, false otherwise.
   */
  private boolean isTwoPart(char toCheck) {
    String symbolRegex = "[=!<>]";
    boolean toReturn = false;

    if (Pattern.matches(symbolRegex, Character.toString(toCheck))) {
      toReturn = true;
    }

    return toReturn;
  }

  /**
   * Checks to see if the value is a number, character or a '.' (for doubles).
   *
   * @param toCheck The string that is to be checked.
   * @return True if the item is a legal symbol, false otherwise.
   */
  private boolean legalVal(char toCheck) {
    String symbolRegex = "[a-z]|\\d|\\.";
    boolean toReturn = false;

    if (Pattern.matches(symbolRegex, Character.toString(toCheck))) {
      toReturn = true;
    }

    return toReturn;
  }
}
