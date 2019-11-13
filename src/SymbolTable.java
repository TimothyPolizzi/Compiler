import java.util.ArrayList;

/**
 * An implementation of a symbol table for the SAD Compiler for Alan Labouseur's compilers class.
 *
 * @author Tim Polizzi
 */
public class SymbolTable {

  private ArrayList<SymbolItem> staticTable;

  /**
   * Creates a new SymbolTable item
   */
  public SymbolTable() {
    staticTable = new ArrayList<>();
  }

  /**
   * Adds a new SymbolItem to the Symbol table.
   *
   * @param var The String name of the variable to be added as a Symbol.
   * @param type The String name of the type of variable to be added as a Symbol.
   * @param scope The integer scope of the variable to be added as a Symbol.
   * @param pos The integer line number of the initialization of the variable to be added as a
   * Symbol.
   */
  public void newSymbol(String var, String type, int scope, int pos) {
    staticTable.add(new SymbolItem(var, type, scope, pos));
  }

  /**
   * Generates a String containing all the information in the SymbolTable.
   *
   * @return The String with a good graphical representation of the SymbolTable.
   */
  public String toString() {
    String bar = "-----------------------------";
    String header = String
        .format("%1$-6s| %2$-6s| %3$-6s| %4$-6s", "Name", "Type", "Scope", "Line");
    String toReturn = bar + "\n" + header + "\n" + bar;

    for (SymbolItem item : staticTable) {
      toReturn += "\n" + item.toString();
    }

    return toReturn;
  }

  /**
   * A private item that is used to store symbols in the table.
   */
  private class SymbolItem {

    private String var;
    private String type;
    private int scope;
    private int pos;

    /**
     * Creates a SymbolItem and requires the basic information of the symbol to be stored.
     *
     * @param var The string name and identifier of the variable that is to be stored.
     * @param type A string that shows the type of the variable stored.
     * @param scope The integer representing the scope that the variable is located in.
     * @param pos The integer containing the line number at which the symbol is represented on.
     */
    public SymbolItem(String var, String type, int scope, int pos) {
      this.var = var;
      this.type = type;
      this.scope = scope;
      this.pos = pos;
    }

    public String getVar() {
      return var;
    }

    public int getPos() {
      return pos;
    }

    public int getScope() {
      return scope;
    }

    /**
     * Returns the string of a SymbolItem
     *
     * @return The string containing the symbol item
     */
    public String toString() {
      String toReturn = String.format("%1$-6s| %2$-6s| %3$-6d| %4$-6d", var, type, scope, pos);
      return toReturn;
    }
  }
}
