import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class CompilerTester {

  public static void main(String[] args) {
    individualTests();
//    readFromFileTest();
//    stdInRead(true);
  }

  /**
   * Reads a file off of the local machine via specified path. I like this one bc I can just slap
   * together a text file nice and easy and not need to worry about piping things correctly.
   */
  private static void readFromFileTest() {
    boolean debug = false;
    String input = "";

    System.out.println("Would you like to be in debug mode? y/n");

    Scanner scan = new Scanner(System.in);

    boolean loop = true;
    while (loop) {
      String debugScan = scan.next();

      if (debugScan.equals("y")) {
        debug = true;
        loop = false;
      } else if (debugScan.equals("n")) {
        debug = false;
        loop = false;
      } else {
        System.out.println("I couldn't understand that");
      }
    }

    System.out.println("Please put the path to the file in or type 'exit' to quit.");

    boolean cont = true;
    boolean exit = false;

    while (cont) {
      String filePath = scan.next();
      if (filePath.equals("exit")) {
        cont = false;
        exit = true;
      } else {
        File textFile = new File(filePath);
        try {
          Scanner fr = new Scanner(textFile);

          while (fr.hasNextLine()) {
            String nextLine = fr.nextLine();
//            System.out.println(nextLine);
            input = input + nextLine + "\n";
          }

          cont = false;
        } catch (FileNotFoundException e) {
          System.out.println("That file could not be scanned. Please try again or type 'exit'.");
        }
      }
    }

    if (!exit) {
      Compiler comp = new Compiler(input, debug);
    }
  }

  /**
   * The wonderful one-off tests I write. There's a lot. I use a lot.
   */
  private static void individualTests() {
    CodeGeneration v = new CodeGeneration();

    String twoLines = "{s=\"two\nlines\"}$";
    String noEnd = "{a = \"unterminated string }$"; // Lexer Breaks

    String alanBreakCaps = "{print(\"No Caps Mister Bond\")}$";
    String alanBreakNums = "{print(\"no digits 007\")}$";
    String alanComments = "{\nstring s\ns = \"this string is /* in */ visible\"\n}$";
    String alanComments2 = "{\n/* what about comments */\nstring b\n}$";

    String stringAnaly1 = "{string x\nx=\"abc\"}$";
    String stringAnaly2 = "{string x\nx=1}$";
    String stringAnaly3 = "{string x\nx=1+\"abc\"}$";
    String stringAnaly4 = "{string i\nx=1}$";
    String pleaseDont = "{int i i = \"abc\"}$";

    String scoping = "{int i int i i = 1}$";
    String noScope = "{int i i = 10 { int i i = 2} i = 9}$";
    String alanScopeTest =
        "{\n"
            + "int a\n"
            + "boolean b \n"
            + "{\n"
            + "string c\n"
            + "a = 5\n"
            + "b = true /* no comment */\n"
            + "c = \"inta\"\n"
            + "print(c)\n"
            + "}\n"
            + "print(b)\n"
            + "print(a)\n"
            + "}$";

    // Test Here
//    Compiler comp = new Compiler(scoping, false);
    v.initializeInt('a');
    v.initializeInt('b');
    v.initializeInt('c');
    v.assignInt('a', 2);
    v.assignInt('b', 255);
    v.assignInt('c', 100);
    System.out.println(v.variableTable.toString());
    System.out.println(v.toString());
  }

  /**
   * You can use this method to read stdin to the compiler. Debug is true for debug mode, false
   * otherwise.
   */
  private static void stdInRead(boolean debug) {
    Scanner scanner = new Scanner(System.in);

    String file = "";

    while (scanner.hasNextLine()) {
      file += scanner.nextLine() + "\n";
    }

    Compiler comp = new Compiler(file, debug);
  }

}
