/**
 * A Variable Item to be used in the Code Generation for the SAD Compiler for Alan Labouseur's
 * compilers class.
 *
 * @author Tim Polizzi
 */
public class VariableItem {

  private String temp;
  private char var;
  private int address;
  private int scope;

  /**
   * Generates a new instance of a VariableTable.
   */
  public VariableItem(char var, int totalVars, int scope) {
    temp = "T" + totalVars + "XX";
    this.var = var;
    this.scope = scope;
  }

  public void setAddress(int address) {
    this.address = address;
  }

  public char getVar() {
    return var;
  }

  public int getAddress() {
    return address;
  }

  public String getTemp() {
    return temp;
  }

  public int getScope() {
    return scope;
  }
}
