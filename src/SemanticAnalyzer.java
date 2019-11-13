/**
 * A Semantic Analyzer for the SAD Compiler for Alan Labouseur's compilers class.
 *
 * @author Tim Polizzi
 */
public class SemanticAnalyzer {

  private SyntaxTree ast;
  private SyntaxTree cst;
  private SymbolTable symbols;

  /**
   * Taking a Concrete Syntax Tree, determine if it is valid based on scope and typing rules, and
   * generate an Abstract Syntax Tree and a SymbolTable for it.
   *
   * @param cst is the concrete syntax tree that will be checked for validity and used to generate
   * the AST
   */
  public SemanticAnalyzer(SyntaxTree cst) {
    this.cst = cst;
    symbols = new SymbolTable();
  }

  /**
   * Generates an AST from the given CST.
   *
   * @return A SyntaxTree containing the AST generated from the given CST.
   */
  public SyntaxTree generateAST() {
    return null;
  }

  /**
   * Checks a variable that is being assigned to see if it is the correct type to be assigned to the
   * variable, or if the variable has not been declared. (assignment trigger)
   *
   * @return True if it is correct, false otherwise.
   */
  public boolean typeCheck() {
    return false;
  }

  /**
   * Checks a variable to see if it is legal in the scope it is currently being attempted to be
   * declared in. (declaration trigger)
   *
   * @return True if it is legal, false otherwise.
   */
  public boolean checkScope() {
    return false;
  }

  /**
   * Checks to see if variables have been unused, and if variables have been used without being
   * assigned. (end of scope/block trigger)
   */
  public boolean bestPractices() {
    return false;
  }

}
