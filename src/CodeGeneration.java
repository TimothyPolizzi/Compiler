import java.util.ArrayList;
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
  private SymbolTable table;
  private int currentEndOfHeap;
  private String stringForHeap;
  private SyntaxTree ast;

  /**
   * Create a new CodeGeneration object that generates Machine Code from a given AST
   *
   * @param ast The given AST to be converted into machine code.
   */
  public CodeGeneration(SyntaxTree ast, int programNo, SymbolTable table) {
    exeEnv = "";
    bytesUsed = 0;
    currentEndOfHeap = 96;
    stringForHeap = "";
    jumpTable = new JumpTable();
    variableTable = new VariableTable();
    this.table = table;
    this.ast = ast;

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
    exeEnv +="00";
    bytesUsed++;
    variableTable.calculateAddresses(bytesUsed);

    // Replaces temporary variables with the locations in memory
    for (VariableItem item : variableTable.getItemList()) {
      exeEnv = exeEnv.replaceAll(item.getTemp(), String.format("%H00",item.getAddress()));
    }

    // Replaces temporary jumps with the actual jump
    for(String temp : jumpTable.getTemps()) {
      exeEnv = exeEnv.replaceAll(temp, String.format("%H00", jumpTable.getJump(temp)));
    }

    int diff = currentEndOfHeap - bytesUsed;

    while(diff > 0) {
      exeEnv += "00";
      diff--;
    }

    exeEnv += stringForHeap;
  }

  private void dft(List<Node> children, int depth) {
    depth++;
    boolean inIf = false;
    String currentJump = "";

    for (Node child : children) {

      // If a root node
      if (child.getChildren().size() > 0) {
        if(!inIf) {
          List<Node> kids = child.getChildren();
          if (Pattern.matches("Print.*", child.getVal())) {
            print(child.getChildren().get(0).getVal().charAt(0), ast.getDepth(child) - 1);
            return;
          } else if (Pattern.matches("If.*", child.getVal())) {
            if (kids.get(1).getVal().equals("==")) {
              compare(kids.get(0), kids.get(2), true);
            } else {
              compare(kids.get(0), kids.get(2), false);
            }
            currentJump = ifStatement();
            inIf = true;
          } else if(Pattern.matches("While.*", child.getVal())) {
            whileLoop(kids.get(0), kids.get(2), kids.get(1));
          }

          dft(child.getChildren(), depth);

        } else {
          dft(child.getChildren(), depth);
          jumpTable.set(currentJump, exeEnv.length()/2);
          inIf = false;

        }

        // if a leaf node
      } else {
        if (Pattern.matches("int|string|boolean|[a-z]$", child.getVal())) {
          Node varName = child.getParent().getChildren().get(1);
          // if initializing an integer
          if (Pattern.matches("int|string|boolean", child.getVal())) {
            char varChar = varName.getVal().charAt(0);
            initializeVar(varChar, ast.getDepth(varName) - 2);
            return;
            // if assigning a variable
          } else if (Pattern.matches("[a-z]", child.getVal())) {
            char thisVar = child.getVal().charAt(0);
            int thisDepth = ast.getDepth(child)-2;

            // assigning an integer
            if (Pattern.matches("\\d+", varName.getVal())) {
              assignInt(thisVar, Integer.parseInt(varName.getVal()), thisDepth);
              // assigning a string
            } else if (Pattern.matches("\\[[a-z ]*]", varName.getVal())) {
              assignString(thisVar, varName.getVal(), thisDepth);
              // assigning a boolean
            } else if (Pattern.matches("true|false", varName.getVal())) {
              assignBoolean(thisVar, Boolean.parseBoolean(varName.getVal()), thisDepth);
              // assigning a variable to another variable
            } else if (Pattern.matches("[a-z]", varName.getVal())) {
              assignVar(thisVar, thisDepth, varName.getVal().charAt(0), ast.getDepth(varName));
            }
          }
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
  private void initializeVar(char var, int scope) {
    String initInt = "A9008D";
    variableTable.addVar(var, scope);
    initInt += variableTable.getTemp(var, scope);
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
  private void assignInt(char var, int val, int scope) {
    String assignInt = "A9";
    assignInt += String.format("%02X", val);
    assignInt += "8D";
    assignInt += variableTable.getTemp(var, scope);
    exeEnv += assignInt;
    bytesUsed += assignInt.length() / 2;
  }

  /**
   * Assigns a variable var from the source code a a value val which is also from the source code.
   *
   * @param var The variable from the source code.
   * @param val The value that var is to be assigned to.
   */
  private void assignBoolean(char var, boolean val, int scope) {
    int boolState = 0;

    String assignInt = "A9";
    if (val) {
      boolState = 1;
    }
    assignInt += String.format("%02X", boolState);
    assignInt += "8D";
    assignInt += variableTable.getTemp(var, scope);
    exeEnv += assignInt;
    bytesUsed += assignInt.length() / 2;
  }

  /**
   * Assigns a string val to the end of the heap space located at the end of the code.
   *
   * @param var The name of the variable that is being assigned in the source code.
   * @param val The value of the string the variable is to be assigned to.
   */
  private void assignString(char var, String val, int scope) {
    String assignStr = "A9";
    assignStr += String.format("%02X", storeString(val));
    assignStr += "8D";
    assignStr += variableTable.getTemp(var, scope);
    exeEnv += assignStr;
    bytesUsed += assignStr.length() / 2;
  }

  /**
   * Prepares a string to be stored in heap.
   */
  private int storeString(String toBeStored) {
    String addToHeap = "";
    toBeStored = toBeStored.substring(1,toBeStored.length()-1);
    for (char c : toBeStored.toCharArray()) {
      addToHeap += String.format("%02X", (int) c);
      currentEndOfHeap--;
    }
    addToHeap += "00";
    bytesUsed++;
    stringForHeap += addToHeap;
    return --currentEndOfHeap;
  }

  /**
   * Assigns a given variable to the value of another given variable.
   *
   * load acc w/ var2 and assign var1 the value
   *
   * @param var1 The variable to have it's value reassigned.
   * @param var2 The variable who's value will be copied.
   */
  private void assignVar(char var1, int scope1, char var2, int scope2) {
    String assignVar = "AD";
    assignVar += variableTable.getTemp(var2, scope2);
    assignVar += "8D";
    assignVar += variableTable.getTemp(var1, scope1);
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
  private void print(char var, int scope) {
    String toPrint = "AC";
    toPrint += variableTable.getTemp(var, scope);
    toPrint += "A201FF";
    exeEnv += toPrint;
    bytesUsed += toPrint.length() / 2;
  }

  /**
   * Compares the values of two item and checks for equality.
   * @param left
   * @param right
   * @parm equal
   */
  private void compare(Node left, Node right, Boolean equal){
    String compareEqual = "";
    boolean firstPass = true;
    Node[] nodes = {left, right};
    char c = 'A';

    for(Node n : nodes) {
      c++;

      // Compare integers
      if (Pattern.matches("\\d+", n.getVal())) {
        if(firstPass) {
          compareEqual += "A2";
        }
        compareEqual += String.format("%02X", Integer.parseInt(n.getVal()));

        // Compare strings
      } else if (Pattern.matches("\\[[a-z]*]", n.getVal())) {
        if(firstPass) {
          compareEqual += "AE";
        }
        initializeVar(c, ast.getDepth(n)-1);
        assignString(c, n.getVal(), ast.getDepth(n)-1);
        compareEqual += variableTable.getTemp(c, ast.getDepth(n)-1);

        // Compare booleans
      } else if (Pattern.matches("true|false", n.getVal())) {
        if(firstPass) {
          compareEqual += "A2";
        }
        int bool = 0;
        if(n.getVal().equals("true")) {
          bool = 1;
        }

        compareEqual += String.format("%02X", bool);

        // Compare variables
      } else if (Pattern.matches("[a-z]", n.getVal())) {
        if(firstPass) {
          compareEqual += "AE";
        }
        compareEqual += variableTable.getTemp(n.getVal().charAt(0), ast.getDepth(n));
      }
      if(firstPass) {
        compareEqual += "EC";
      }
      firstPass = false;
    }
    compareEqual += "D0";
    if(!equal) {
      compareEqual += "20";
    }

    bytesUsed += compareEqual.length() / 2;
    exeEnv += compareEqual;
  }

  /**
   *
   */
  private String ifStatement() {
    String ifString = "";
    String thisJump = "J" + jumpTable.getJumps();
    jumpTable.add(thisJump);
    ifString += thisJump;
    exeEnv += ifString;

    return thisJump;
  }

  /**
   *
   * @param arg1
   * @param arg2
   * @param comparator
   */
  private void whileLoop(Node arg1, Node arg2, Node comparator) {
    String loopStr = "";
    Node[] nodes = {arg1, arg2};
    List<String> temps = new ArrayList<>();
    char tempVar = 'A';

    for(Node n : nodes) {
      // If thing is a terminal (A9)
      if (Pattern.matches("(\\d+)|(true|false)", n.getVal())) {
        loopStr += "A9";
        // bool true
        if (n.getVal().equals("true")) {
          loopStr += "01";
          // bool false
        } else if (n.getVal().equals("false")) {
          loopStr += "00";
          // int
        } else {
          loopStr += String.format("%02X", Integer.parseInt(n.getVal()));
        }
        loopStr += "8D";
        variableTable.addVar(tempVar, ast.getDepth(n));
        String temp = variableTable.getTemp(tempVar, ast.getDepth(n));
        temps.add(temp);
        loopStr += temp;
      } else {
        loopStr += "AD";
        // If thing is a string (aka a mistake)
        if(Pattern.matches("\\[[a-z]*]", n.getVal())) {
          char tempVar2 = (char)(tempVar + 1);

          initializeVar(tempVar, ast.getDepth(n));
          assignString(tempVar, n.getVal(), ast.getDepth(n));
          loopStr += variableTable.getTemp(tempVar, ast.getDepth(n));
          loopStr += "8D";
          variableTable.addVar(tempVar2, ast.getDepth(n));
          String temp = variableTable.getTemp(tempVar2, ast.getDepth(n));
          temps.add(temp);
          loopStr += temp;
        } else {
          loopStr += variableTable.getTemp(n.getVal().charAt(0), ast.getDepth(n));
          loopStr += "8D";
          variableTable.addVar(tempVar, ast.getDepth(n));
          String temp = variableTable.getTemp(tempVar, ast.getDepth(n));
          temps.add(temp);
          loopStr += temp;
        }
      }
      tempVar++;
    }
    int returnTo = (loopStr.length() + exeEnv.length()) / 2;
    loopStr += "AE" + temps.get(0) + "EC" + temps.get(1);
    loopStr += "A900D0";
    loopStr += String.format("%02X", returnTo);
    loopStr += "A901";
    int returnTwo = (loopStr.length() + exeEnv.length()) / 2;
    loopStr += "A2008D" + temps.get(0);
    loopStr += "EC" + temps.get(0);
    loopStr += "D0" + returnTwo;

    bytesUsed += loopStr.length() / 2;
    exeEnv += loopStr;
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
