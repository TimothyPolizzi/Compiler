import com.sun.tools.javac.jvm.Code;
import java.util.ArrayList;
import java.util.Collections;

public class Compiler {

  public Compiler(String toCompile, boolean verbose) {
    ArrayList<String> programs = breakIntoPrograms(toCompile);
    int iter = 1;

    System.out.print("DEBUG Verbose mode is ");
    if (verbose) {
      System.out.println("on");
    } else {
      System.out.println("off");
    }

    for (String program : programs) {
      Lexer lex = new Lexer(program, iter, verbose);
      ArrayList<Token> tokenList = lex.getTokenList();
      if (lex.success()) {
        ArrayList<Token> parseTokens = (ArrayList<Token>) tokenList.clone();
        Parser parse = new Parser(iter, parseTokens, verbose);
        if (parse.success()) {
          parse.printTree();
          ArrayList<Token> analysisTokens = (ArrayList<Token>) tokenList.clone();
          SemanticAnalyzer analyzer = new SemanticAnalyzer(iter, analysisTokens, verbose);
          analyzer.printTree();
          if (analyzer.success()) {
            analyzer.printTable();
            CodeGeneration codeGen = new CodeGeneration(analyzer.getTree(), iter);
            codeGen.printTables();
          }
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
