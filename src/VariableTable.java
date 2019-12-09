import java.util.ArrayList;

/**
 * A Variable Table to be used in the Code Generation for the SAD Compiler for Alan Labouseur's
 * compilers class.
 *
 * @author Tim Polizzi
 */
public class VariableTable {

  private ArrayList<String> temp;
  private ArrayList<Character> var;
  private ArrayList<Integer> address;

  /**
   * Generates a new instance of a VariableTable.
   */
  public VariableTable() {
    temp = new ArrayList<>();
    var = new ArrayList<>();
    address = new ArrayList<>();
  }

  /**
   * Adds a variable to the table.
   *
   * @param var The original name of the variable in the source code.
   */
  public void addVar(char var) {
    this.temp.add("T" + storedVars() + "XX");
    this.var.add(var);
    address.add(0x2f + address.size());
  }

  /**
   * Gets the address in memory of a temporary variable.
   *
   * @param temp The temporary variable.
   * @return The address in memory of the variable.
   */
  public int getAddress(String temp) {
    return address.get(this.temp.indexOf(temp));
  }

  /**
   * Gets the temporary variable associated with a given character variable.
   *
   * @param var The variable in the source code.
   * @return The stored temporary variable.
   */
  public String getTemp(char var) {
    return temp.get(this.var.indexOf(var));
  }

  /**
   * Sets the address in memory of a given temporary variable.
   *
   * @param temp The temporary variable.
   * @param address The address the variable is to be set to.
   */
  public void setAddress(String temp, int address) {
    this.address.add(this.temp.indexOf(temp), address);
  }

  private int storedVars() {
    return var.size();
  }

  public ArrayList<Character> getVar() {
    return var;
  }

  public ArrayList<Integer> getAddress() {
    return address;
  }

  public ArrayList<String> getTemp() {
    return temp;
  }

  /**
   * Returns a nicely formatted table of the variables and their jump distances.
   *
   * @return A well formatted String of variables and jump distances.
   */
  public String toString() {
    String toReturn = String.format("%-5s | %-5s | %-5s\n", "Temp", "Var", "Address");

    String line = "-------------------------\n";

    toReturn += line;

    for (int i = 0; i < temp.size(); i++) {
      String stringLine = String
          .format("%-5s | %-5s | %-5X\n", temp.get(i), var.get(i), address.get(i));
      toReturn += stringLine;
    }

    return toReturn;
  }
}
