package Lexer;

import java.util.ArrayList;
import java.util.regex.Pattern;

/**
 * A lexer for the SAD Compiler for Alan Labouseur's CMPT432 class.
 *
 * @author Tim Polizzi
 */
public class Lexer {

  private ArrayList<ArrayList<Token>> masterList;

  /**
   * Analyzes a file fileToRead, and generates a list of Tokens.
   *
   * @param fileToRead The file that is to be turned into a list of tokens.
   * @param verbose A boolean that when True, shows all generated tokens.
   */
  public Lexer(String fileToRead, boolean verbose) {
    masterList = new ArrayList<ArrayList<Token>>();

    ArrayList<String> programList = findPrograms(fileToRead);

    int programNo = 0;
    int lineNo = 1;

    while (programList.size() > programNo) {
      int printProgramNo = programNo + 1;
      System.out.println("\n\nINFO Lexer - Lexing program " + printProgramNo + "...");

      ArrayList<String> lineList = breakLines(programList.get(programNo));
      int errCount = 0;

      for (String line : lineList) {
        char[] charList = line.toCharArray();
        ArrayList<Token> currentLine = lexLine(charList, lineNo, verbose);

        for (Token t : currentLine) {
          if (t.errorCheck()) {
            errCount++;
          }
        }

        masterList.add(currentLine);
        lineNo++;
      }

      if (errCount == 0) {
        System.out.println("INFO Lexer - Lex completed with 0 errors");
      } else {
        System.out.println("ERROR Lexer - Lex failed with " + errCount + " error(s)");
      }

      programNo++;
    }
  }

  /**
   * Lexes a file in non-verbose mode.
   *
   * @param fileToRead The file to be Lexed.
   */
  public Lexer(String fileToRead) {
    this(fileToRead, false);
  }

  /**
   * Finds the end of program markers and separates programs using them.
   *
   * @param program The initial program that needs to be checked and separated (if need be)
   * @return An ArrayList containing the String representations of all programs located originally
   * in program.
   */
  private ArrayList<String> findPrograms(String program) {
    ArrayList<String> programs = new ArrayList<>();

    if (program.contains("$")) {
      while (program.contains("$")) {
        programs.add(program.substring(0, program.indexOf("$")+1));
        program = program.substring(program.indexOf("$")+1);
      }
    } else {
      programs.add(program);
    }

    return programs;
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
        lines.add(toBreak.substring(0, toBreak.indexOf("\n")+1));
        toBreak = toBreak.substring(toBreak.indexOf("\n")+1);
      }
    } else {
      lines.add(toBreak);
    }
    lines.add(toBreak);

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
          tokenList.add(
              new Token(Character.toString(charList[current - 1]), lineNum, current - 1, false));
        }

        // what to do in case of quotes
      } else if (currentChar == '"') {
        int quoteLoop = current;

        tokenList
            .add(new Token(Character.toString(charList[quoteLoop]), lineNum, quoteLoop, false));
        quoteLoop++;

        while (charList[quoteLoop] != '"' && charList[quoteLoop] != '$') {
          tokenList
              .add(new Token(Character.toString(charList[quoteLoop]), lineNum, quoteLoop, true));
          quoteLoop++;
        }

        tokenList
            .add(new Token(Character.toString(charList[quoteLoop]), lineNum, quoteLoop, false));
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
        tokenList.add(new Token(goodToken, lineNum, start, false));

        current = lastGood;

        // Something isn't right here
      } else {
        currentToken += currentChar;
        current++;
      }

      // skip spaces, ignore empty string
      if (!currentToken.equals(" ") && !currentToken.equals("") && !Pattern.matches("\\n", currentToken)) {
        tokenList.add(new Token(currentToken, lineNum, current, false));
      }
    }

    // print statement for verbose
    if (verbose) {
      for (Token t : tokenList) {
        System.out.println(t.toString());
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
}
