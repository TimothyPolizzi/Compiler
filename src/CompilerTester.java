import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;

public class CompilerTester {

  public static void main(String[] args) {
//    individualTests();
    readTest();
  }

  private static void readTest() {
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
    while (cont) {
      String filePath = scan.next();
      if (filePath.equals("exit")) {
        cont = false;
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
          e.printStackTrace();
        }
      }
    }

    Compiler comp = new Compiler(input, debug);
  }

  private static void individualTests() {
    String text = "Tim is here$";
    String quote = "\"I enjoy food\" 123$";
    String quoteNoEnd = "\"Big Yoshi$";
    String integer = "123456790$";
    String decimal = "123.456$";
    String keywords = "int a = 123; for(int i = 0; i < 10; i++) { print(\"hi\");};$";
    String javaKeywords = "int abc = 123; \nfor(int incr = 0; incr < 10; incr++) {\nif( a != b) {"
        + "\n System.out.print(\"hi\")\n}\n}$";
    String noSpaces = "abcint123printwhile";

    String multiProgram = integer + decimal + quote + javaKeywords + noSpaces;

    // Test Here
    Compiler comp = new Compiler(multiProgram, true);
  }

}
