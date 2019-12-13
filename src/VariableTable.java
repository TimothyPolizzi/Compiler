import java.util.ArrayList;
import java.util.List;

public class VariableTable {

  private List<VariableItem> itemList;
  private int totalVars;

  public VariableTable() {
    itemList = new ArrayList<>();
    totalVars = 0;
  }

  /**
   * Adds a variable to the table.
   *
   * @param var The original name of the variable in the source code.
   */
  public void addVar(char var, int scope) {
    itemList.add(new VariableItem(var, totalVars, scope));
    totalVars++;
  }

  /**
   * Gets the address in memory of a temporary variable.
   *
   * @param temp The temporary variable.
   * @return The address in memory of the variable, or -1 if it could not be found.
   */
  public int getAddress(String temp) {
    for (VariableItem item : itemList) {
      if (item.getTemp().equals(temp)) {
        return item.getAddress();
      }
    }

    return -1;
  }

  /**
   * Gets the temporary variable associated with a given character variable. Will get the item at
   * the given scope or the next highest scope the variable could be found at if it is not in the
   * given scope.
   *
   * @param var The variable in the source code.
   * @return The stored temporary variable, or null if the item could not be found;
   */
  public String getTemp(char var, int depth) {
    for (VariableItem item : itemList) {
      if (item.getVar() == var && item.getScope() == depth) {
        return item.getTemp();
      }
    }
    if(depth > 0) {
      return getTemp(var, depth-1);
    }

    return null;
  }

  /**
   * Sets the address in memory of a given temporary variable.
   *
   * @param temp The temporary variable.
   * @param address The address the variable is to be set to.
   */
  public void setAddress(String temp, int address) {
    for (VariableItem item : itemList) {
      if (item.getTemp().equals(temp)) {
        item.setAddress(address);
      }
    }
  }

  /**
   * Calculates the addresses in memory for the variables.
   *
   * @param bytesUsed The number of bytes used by the code.
   */
  public void calculateAddresses(int bytesUsed) {
    for (int i = 0; i < totalVars; i++) {
      itemList.get(i).setAddress(bytesUsed + i);
    }
  }

  public int storedVars() {
    return totalVars;
  }

  public List<VariableItem> getItemList() { return itemList;}

  /**
   * Returns a nicely formatted table of the variables and their jump distances.
   *
   * @return A well formatted String of variables and jump distances.
   */
  public String toString() {
    String toReturn = String
        .format("%-5s | %-5s | %-5s | %-5s\n", "Temp", "Var", "Scope", "Address");

    String line = "--------------------------------\n";

    toReturn += line;

    for (int i = 0; i < totalVars; i++) {
      VariableItem currentItem = itemList.get(i);

      String stringLine = String
          .format("%-5s | %-5s | %-5d | %X00\n", currentItem.getTemp(), currentItem.getVar(),
              currentItem.getScope(),currentItem.getAddress());
      toReturn += stringLine;
    }

    return toReturn;
  }

}
