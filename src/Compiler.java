import Lexer.Lexer;
import Parser.Parser;
import java.util.ArrayList;

public class Compiler {

  public Compiler(String toCompile, boolean verbose) {
    ArrayList<String> programs = breakIntoPrograms(toCompile);
    int iter = 1;

    System.out.print("DEBUG Verbose mode is ");
    if(verbose){
      System.out.println("on");
    } else {
      System.out.println("off");
    }

    for (String program : programs) {
      Lexer lex = new Lexer(program, iter, verbose);
      if(lex.success()) {
        Parser parse = new Parser(iter, lex.getTokenList(), verbose);
        if (parse.success()) {
          parse.printCST();
//          // TODO: Semantic Analysis
//          // TODO: Code Gen
        }
      }

      iter++;
    }
  }

  public Compiler(String toCompile) {
    this(toCompile, false);
  }

  /**
   * Separates out separate programs to be run.
   *
   * @param toBreak The string to be broken into programs.
   * @return An ArrayList containing all of the program strings.
   */
  private ArrayList<String> breakIntoPrograms(String toBreak) {
    ArrayList<String> programs = new ArrayList<>();

    if (toBreak.contains("$")) {
      while (toBreak.contains("$")) {
        programs.add(toBreak.substring(0, toBreak.indexOf("$") + 1));
        toBreak = toBreak.substring(toBreak.indexOf("$") + 1);
      }
      if (!toBreak.equals("") && !toBreak.equals("\n")) {
        programs.add(toBreak);
      }
    } else {
      programs.add(toBreak);
    }

    return programs;
  }

}
