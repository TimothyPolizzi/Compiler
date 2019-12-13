import java.util.LinkedHashMap;
import java.util.Set;

/**
 * A Jump Table to be used in the Code Generation for the SAD Compiler for Alan Labouseur's
 * compilers class.
 *
 * @author Tim Polizzi
 */
public class JumpTable {

  private LinkedHashMap<String, Integer> table;

  /**
   * Generates a new Jump Table.
   */
  public JumpTable() {
    table = new LinkedHashMap<>();
  }

  /**
   * Adds a new temporary variable to the Jump Table.
   *
   * @param temp The String name of the temporary variable.
   */
  public void add(String temp) {
    final int NOT_SET = -1;
    table.put(temp, NOT_SET);
  }

  /**
   * Sets the distance of the Jump for a temporary variable.
   *
   * @param temp The String name of the temporary variable.
   * @param distance The integer distance of the jump of the variable.
   */
  public void set(String temp, int distance) {
    table.replace(temp, distance);
  }

  /**
   * Gets the jump distance of a given temporary variable.
   *
   * @param temp The String name of the temporary variable.
   * @return The integer distance of the jump of the variable.
   */
  public int getDistance(String temp) {
    return table.get(temp);
  }

  public int getJumps() {
    return table.size();
  }

  public int getJump(String temp) {
    return table.get(temp);
  }

  public Set<String> getTemps() {
    return table.keySet();
  }

  /**
   * Returns a nicely formatted table of the variables and their jump distances.
   *
   * @return A well formatted String of variables and jump distances.
   */
  public String toString() {
    String toReturn = String.format("%-5s | %-5s\n", "Temp", "Dist");

    String line = "--------------\n";

    toReturn += line;

    for (String key : table.keySet()) {
      String stringLine = String.format("%-5s | %-5d\n", key, table.get(key));
      toReturn += stringLine;
    }

    return toReturn;
  }
}
