/**
 * Code Generation for the SAD Compiler for Alan Labouseur's compilers class.
 *
 * @author Tim Polizzi
 */
public class CodeGeneration {

  private String exeEnv;
  private JumpTable jumpTable;
  public VariableTable variableTable;

  public CodeGeneration() {
    exeEnv = "";
    jumpTable = new JumpTable();
    variableTable = new VariableTable();

  }

  //init int (load acc)
  public void initializeInt(char var) {
    String initInt = "A9008D";
    variableTable.addVar(var);
    String tempVar = variableTable.getTemp(var);
    exeEnv += initInt + tempVar;
  }

  //load acc to val + store val
  public void assignInt(char var, int val) {
    String assignInt = "A9";
    assignInt += String.format("%02X", val);
    assignInt += "8D";
    assignInt += variableTable.getTemp(var);
    exeEnv += assignInt;
  }

  //TODO: load acc 1 to val 1 and set it to val 2

  //TODO: print -> load Y reg w/ var, load X reg w/ 1, syscall

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
