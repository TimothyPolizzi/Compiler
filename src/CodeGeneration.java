import java.util.List;
import java.util.regex.Pattern;

/**
 * Code Generation for the SAD Compiler for Alan Labouseur's compilers class.
 *
 * @author Tim Polizzi
 */
public class CodeGeneration {

  private String exeEnv;
  private JumpTable jumpTable;
  private VariableTable variableTable;
  private int bytesUsed;

  /**
   * Create a new CodeGeneration object that generates Machine Code from a given AST
   *
   * @param ast The given AST to be converted into machine code.
   */
  public CodeGeneration(SyntaxTree ast, int programNo) {
    exeEnv = "";
    bytesUsed = 0;
    jumpTable = new JumpTable();
    variableTable = new VariableTable();

    System.out.println("\nINFO Code Generation - Generating code for program " + programNo + "...");

    generateFromAST(ast);

    System.out.println(toString());
  }

  /**
   * Generate code from the AST
   *
   * @param ast The AST that code will be generated from
   */
  private void generateFromAST(SyntaxTree ast) {
    Node root = ast.getRoot();
    List<Node> childList = root.getChildren();
    int depth = 0;

    dft(childList, depth);
    variableTable.calculateAddresses(bytesUsed);

    //TODO: find and replace all temp vars with addresses
  }

  private void dft(List<Node> children, int depth) {
    depth++;

    for (Node child : children) {

      // If a root node
      if (child.getChildren().size() > 0) {
        if (Pattern.matches("", child.getVal())) {

        }

        dft(child.getChildren(), depth);

        // if a leaf node
      } else {
        if (Pattern.matches("int", child.getVal())) {
          Node varName = child.getParent().getChildren().get(1);
          initializeInt(varName.getVal().charAt(0));

        } else if (Pattern.matches("string", child.getVal())) {

        } else if (Pattern.matches("boolean", child.getVal())) {

        } else if (Pattern.matches("char", child.getVal())) {

        }
      }
    }
  }

  /**
   * A helper method that will initialize an integer with a given variable var from the source
   * code.
   *
   * init int (load acc)
   *
   * @param var The variable in the source code.
   */
  private void initializeInt(char var) {
    String initInt = "A9008D";
    variableTable.addVar(var);
    initInt += variableTable.getTemp(var);
    exeEnv += initInt;
    bytesUsed += initInt.length() / 2;
  }

  /**
   * Assigns a variable var from the source code a a value val which is also from the source code.
   *
   * load acc to val + store val
   *
   * @param var The variable from the source code.
   * @param val The value that var is to be assigned to.
   */
  private void assignInt(char var, int val) {
    String assignInt = "A9";
    assignInt += String.format("%02X", val);
    assignInt += "8D";
    assignInt += variableTable.getTemp(var);
    exeEnv += assignInt;
    bytesUsed += assignInt.length() / 2;
  }

  /**
   * Assigns a given variable to the value of another given variable.
   *
   * load acc w/ var2 and assign var1 the value
   *
   * @param var1 The variable to have it's value reassigned.
   * @param var2 The variable who's value will be copied.
   */
  private void assignVar(char var1, char var2) {
    String assignVar = "AD";
    assignVar += variableTable.getTemp(var2);
    assignVar += "8D";
    assignVar += variableTable.getTemp(var1);
    exeEnv += assignVar;
    bytesUsed += assignVar.length() / 2;
  }

  /**
   * Prints out the value of a given variable.
   *
   * print -> load Y reg w/ var, load X reg w/ 1, syscall
   *
   * @param var The variable to have it's value printed.
   */
  private void print(char var) {
    String toPrint = "AC";
    toPrint += variableTable.getTemp(var);
    toPrint += "A201FF";
    exeEnv += toPrint;
    bytesUsed += toPrint.length() / 2;
  }

  /**
   * Returns the machine code in string form in the proper format.
   *
   * @return The formatted String.
   */
  public String toString() {
    String toReturn = "";
    String remainder = exeEnv;
    int count = 0;

    while (remainder.length() >= 2) {
      if (count >= 8) {
        toReturn += "\n";
        count = 0;
      }
      toReturn += remainder.substring(0, 2) + " ";
      remainder = remainder.substring(2);
      count++;
    }

    return toReturn;
  }

  public JumpTable getJumpTable() {
    return jumpTable;
  }

  public VariableTable getVariableTable() {
    return variableTable;
  }

  public void printTables() {
    System.out.println("\nINFO printing Jump and Variable Tables");

    System.out.println("\n" + jumpTable.toString());

    System.out.println("\n" + variableTable.toString());
  }
}
