package Lexer;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * A lexer for the SAD Compiler for Alan Labouseur's compilers class.
 *
 * @author Tim Polizzi
 */
public class Lexer {

  private ArrayList<Token> masterList;
  private int errCount;
  private int warnCount;
  private boolean multiLineQuote;

  /**
   * Analyzes a file fileToRead, and generates a list of Tokens.
   *
   * @param fileToRead The file that is to be turned into a list of tokens.
   * @param verbose A boolean that when True, shows all generated tokens.
   */
  public Lexer(String fileToRead, int programNo, boolean verbose) {
    masterList = new ArrayList<Token>();
    errCount = 0;
    warnCount = 0;

    int lineNo = 1;

    System.out.println("\nINFO Lexer - Lexing program " + programNo + "...");

    ArrayList<String> lineList = breakLines(fileToRead);

    for (String line : lineList) {
      char[] charList = line.toCharArray();
      ArrayList<Token> currentLine = lexLine(charList, lineNo, verbose);

      masterList.addAll(currentLine);
      lineNo++;
    }

    eopWarning(masterList);

    if (errCount == 0) {
      System.out
          .println("INFO Lexer - Lex completed with 0 errors and " + warnCount + " warning(s)");
    } else {
      System.out.println("ERROR Lexer - Lex failed with " + errCount + " error(s) and " + warnCount
          + " warning(s)");
    }

  }

  /**
   * Lexes a file in non-verbose mode.
   *
   * @param fileToRead The file to be Lexed.
   */
  public Lexer(String fileToRead, int programNo) {
    this(fileToRead, programNo, false);
  }

  /**
   * Breaks any lines into separate strings from toBreak.
   *
   * @param toBreak The string that is to be broken into lines.
   * @return An ArrayList containing all the newly broken down strings.
   */
  private ArrayList<String> breakLines(String toBreak) {
    ArrayList<String> lines = new ArrayList<>();

    if (toBreak.contains("\n")) {
      while (toBreak.contains("\n")) {
        lines.add(toBreak.substring(0, toBreak.indexOf("\n") + 1));
        toBreak = toBreak.substring(toBreak.indexOf("\n") + 1);
      }
      if (!toBreak.equals("")) {
        lines.add(toBreak);
      }
    } else {
      lines.add(toBreak);
    }

    return lines;
  }

  /**
   * Generates a list of tokens from a line of code, using the grammar of the SAD compiler.
   *
   * @param charList The list of characters in the line that is to be broken into tokens.
   * @param lineNum The line number on which this is occurring.
   */
  private ArrayList<Token> lexLine(char[] charList, int lineNum, boolean verbose) {
    int current = 0;
    int lastGood;
    ArrayList<Token> tokenList = new ArrayList<Token>();

    while (current < charList.length) {
      String currentToken = "";
      char currentChar = charList[current];

      // Check if the current item is a symbol
      if (isSymbol(currentChar) && !multiLineQuote) {
        currentToken += currentChar;

        // If that symbol is two parts, check here
        if (isTwoPart(currentChar) && charList[current + 1] == '=') {
          current++;
          currentToken += charList[current];
        }

        current++;

        //check for comments, skip the commented section
      } else if (Pattern.matches("/", Character.toString(currentChar)) &&
          Pattern.matches("\\*", Character.toString(charList[current + 1])) && !multiLineQuote) {
        current = ignoreComments(charList, current);

        // what to do in case of quotes
      } else if (currentChar == '"' || multiLineQuote) {
        int quoteLoop = current;
        Token thisToken = null;

        if (!multiLineQuote) {
          thisToken =
              new Token(Character.toString(charList[quoteLoop]), lineNum, quoteLoop, false);

          tokenList = updateToken(thisToken, tokenList, verbose);

          quoteLoop++;
        }

        boolean cont = true;
        while (cont) {
          if (Pattern.matches("\"", Character.toString(charList[quoteLoop]))) {
            cont = false;
            multiLineQuote = false;
          } else if (Pattern.matches("[$]", Character.toString(charList[quoteLoop]))) {
            cont = false;
            System.out.println("WARNING Lexer - Missing EndQuote Character '\"'");
            warnCount++;
          } else if (Pattern.matches("\n", Character.toString(charList[quoteLoop]))) {
            multiLineQuote = true;
            quoteLoop++;
            cont = false;
          } else if (Pattern.matches("/", Character.toString(charList[quoteLoop])) &&
              Pattern.matches("\\*", Character.toString(charList[quoteLoop + 1]))
              && !multiLineQuote) {
            quoteLoop = ignoreComments(charList, quoteLoop);
          } else {
            thisToken =
                new Token(Character.toString(charList[quoteLoop]), lineNum, quoteLoop, true);
            tokenList = updateToken(thisToken, tokenList, verbose);

            quoteLoop++;
          }
        }
        if (quoteLoop < charList.length &&
            Pattern.matches("\"", Character.toString(charList[quoteLoop]))) {
          thisToken =
              new Token(Character.toString(charList[quoteLoop]), lineNum, quoteLoop, false);
          tokenList = updateToken(thisToken, tokenList, verbose);

          quoteLoop++;
        }

        current = quoteLoop;

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
        Token thisToken = new Token(goodToken, lineNum, start, false);

        tokenList = updateToken(thisToken, tokenList, verbose);

        current = lastGood;

        // Something isn't right here
      } else {
        currentToken += currentChar;
        current++;
      }

      // skip spaces, ignore empty string, ignore new lines
      if (!currentToken.equals(" ") && !currentToken.equals("") && !Pattern
          .matches("\\n", currentToken)) {
        Token thisToken = new Token(currentToken, lineNum, current, false);
        tokenList = updateToken(thisToken, tokenList, verbose);
      }
    }

    return tokenList;
  }

  /**
   * Ignores commented text.
   *
   * @param charList The list of characters currently being lexed.
   * @param current The index of the currently being lexed character.
   * @return The new value of the item to be lexed.
   */
  private int ignoreComments(char[] charList, int current) {
    current += 2;

    boolean cont2 = true;
    while (cont2) {
      // Case for program terminating before finding a end comment
      if (Pattern.matches("\\$", Character.toString(charList[current]))) {
        System.out.println("WARNING Lexer - Missing EndComment Character '*/'");
        warnCount++;
        cont2 = false;
        // Case for end of comment
      } else if (Pattern.matches("\\*", Character.toString(charList[current])) &&
          Pattern.matches("/", Character.toString(charList[current + 1]))) {
        cont2 = false;
        current += 2;
      } else {
        current++;
      }
    }
    return current;
  }

  /**
   * Returns a warning if the code is missing an EOP character.
   *
   * @param tokenList A list containing the tokens generated from lexing the current program.
   */
  private void eopWarning(ArrayList<Token> tokenList) {
    if (!tokenList.get(tokenList.size() - 1).getOriginal()
        .equals("$")) { // If the last token is not '$'
      System.out.println("WARNING Lexer - Missing EOP Character '$'");
      warnCount++;
    }

  }

  /**
   * Adds a token to an array list and displays tokens or errors.
   */
  private ArrayList<Token> updateToken(Token thisToken, ArrayList<Token> tokenList,
      boolean isVerbose) {
    if (thisToken.errorCheck()) {
      System.out.println(thisToken.toString());
      errCount++;
    } else {
      tokenList.add(thisToken);
      if (isVerbose) {
        System.out.println(thisToken.toString());
      }
    }
    return tokenList;
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

  /**
   * Returns the master list of tokens from the program.
   *
   * @return The master list of tokens from the program.
   */
  public ArrayList<Token> getTokenList() {
    return masterList;
  }

  /**
   * Returns the state of the Lex
   *
   * @return false if the lex failed, true otherwise.
   */
  public boolean success() {
    boolean success = true;

    if (errCount > 0) {
      success = false;
    }

    return success;
  }
}
