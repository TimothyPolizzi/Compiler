/**
 * Code Generation for the SAD Compiler for Alan Labouseur's compilers class.
 *
 * @author Tim Polizzi
 */
public class CodeGeneration {

  private String exeEnv;
  private JumpTable jumpTable;
  public VariableTable variableTable;
  private SyntaxTree ast;

  /**
   * Create a new CodeGeneration object that generates Machine Code from a given AST
   *
   * @param ast The given AST to be converted into machine code.
   */
  public CodeGeneration(SyntaxTree ast) {
    exeEnv = "";
    jumpTable = new JumpTable();
    variableTable = new VariableTable();
    this.ast = ast;
  }

  /**
   * A helper method that will initialize an integer with a given variable var from the source
   * code.
   *
   * init int (load acc)
   *
   * @param var The variable in the source code.
   */
  public void initializeInt(char var) {
    String initInt = "A9008D";
    variableTable.addVar(var);
    String tempVar = variableTable.getTemp(var);
    exeEnv += initInt + tempVar;
  }

  /**
   * Assigns a variable var from the source code a a value val which is also from the source code.
   *
   * load acc to val + store val
   *
   * @param var The variable from the source code.
   * @param val The value that var is to be assigned to.
   */
  public void assignInt(char var, int val) {
    String assignInt = "A9";
    assignInt += String.format("%02X", val);
    assignInt += "8D";
    assignInt += variableTable.getTemp(var);
    exeEnv += assignInt;
  }

  /**
   * Assigns a given variable to the value of another given variable.
   *
   * load acc w/ var2 and assign var1 the value
   *
   * @param var1 The variable to have it's value reassigned.
   * @param var2 The variable who's value will be copied.
   */
  public void assignVar(char var1, char var2) {
    String assignVar = "AD";
    assignVar += variableTable.getTemp(var2);
    assignVar += "8D";
    assignVar += variableTable.getTemp(var1);
    exeEnv += assignVar;
  }

  /**
   * Prints out the value of a given variable.
   *
   * print -> load Y reg w/ var, load X reg w/ 1, syscall
   *
   * @param var The variable to have it's value printed.
   */
  public void print(char var) {
    String toPrint = "AC";
    toPrint += variableTable.getTemp(var);
    toPrint += "A201FF";
    exeEnv += toPrint;
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
}
