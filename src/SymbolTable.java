import java.util.ArrayList;
import java.util.List;

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
   * Searches the SymbolTable for any instances of a given symbol.
   *
   * @param symbol The symbol that is being searched for.
   * @return The list of symbols that have been found.
   */
  public List<SymbolItem> checkForSymbol(String symbol) {
    List<SymbolItem> foundList = new ArrayList<>();

    for (SymbolItem item : staticTable) {
      if (item.getVar().equals(symbol)) {
        foundList.add(item);
      }
    }

    return foundList;
  }

  public ArrayList<SymbolItem> getList() {
    return staticTable;
  }
}
