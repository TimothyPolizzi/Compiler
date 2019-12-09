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
   * @param val The String representation of the value of the Symbol.
   * @param scope The integer scope of the variable to be added as a Symbol.
   * @param pos The integer line number of the initialization of the variable to be added as a
   * Symbol.
   */
  public void newSymbol(String var, String type, String val, int scope, int pos) {
    staticTable.add(new SymbolItem(var, type, val, scope, pos));
  }

  /**
   * Generates a String containing all the information in the SymbolTable.
   *
   * @return The String with a good graphical representation of the SymbolTable.
   */
  public String toString() {
    String bar = "-----------------------------------------";
    String header = String
        .format("%1$-6s| %2$-8s| %3$-8s| %4$-6s| %5$-6s", "Name", "Type", "Scope", "Line", "Value");
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

  /**
   * @return The current "this" symbol, or null if the symbol does not exist.
   */
  public SymbolItem activeSymbol(String toFind, int currentScope) {
    List<SymbolItem> symbols = checkForSymbol(toFind);
    SymbolItem toReturn = null;

    if (symbols.size() < 1) {
      return null;
    }

    for (SymbolItem item : symbols) {
      if (item.getScope() <= currentScope) {
        if (item.getScope() == currentScope) {
          return item;
        } else if (toReturn == null) {
          toReturn = item;
        }else if (toReturn.getScope() > item.getScope()) {
          toReturn = item;
        }
      }
    }

    return toReturn;
  }

  public ArrayList<SymbolItem> getList() {
    return staticTable;
  }
}
