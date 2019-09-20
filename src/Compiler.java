import Lexer.Lexer;
import java.util.ArrayList;

public class Compiler {

  public Compiler(String toCompile, boolean verbose) {
    ArrayList<String> programs = breakIntoPrograms(toCompile);
    int iter = 1;

    for (String program : programs) {
      Lexer lex = new Lexer(program, iter, verbose);
      // Parser parse = new Parser(lex.getTokenList());
      // TODO: Next Step
      // TODO: Code Gen

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
      if (!toBreak.equals("")) {
        programs.add(toBreak);
      }
    } else {
      programs.add(toBreak);
    }

    return programs;
  }

}
