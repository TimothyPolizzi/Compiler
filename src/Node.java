import java.util.ArrayList;
import java.util.List;

public class Node {

  private String val;
  private List children;
  private Node parent;

  /**
   * Generates a new node that contains a Token with a given parent.
   *
   * @param parent The parent Node of the node.
   * @param val The Token value of the node.
   */
  public Node(Node parent, String val) {
    this(val);
    this.parent = parent;
  }

  /**
   * Generates a new node that contains a value.
   *
   * @param val The value of the node.
   */
  public Node(String val) {
    children = new ArrayList();
    this.val = val;
  }

  /**
   * Sets the Node parent of the node.
   *
   * @param parent The Node parent of the node.
   */
  public void setParent(Node parent) {
    this.parent = parent;
  }

  /**
   * Gets the parent Node of this node.
   *
   * @return The Node parent node of this node.
   */
  public Node getParent() {
    return parent;
  }

  /**
   * Gets the value stored in the node.
   *
   * @return the value stored in the node.
   */
  public String getVal() {
    return val;
  }

  /**
   * Appends a child Node to the List of children.
   *
   * @param child the Node to be added to the list of children.
   */
  public void addChild(Node child) {
    children.add(child);
  }

  /**
   * Gets the List of children of the current Node.
   *
   * @return the List of children.
   */
  public List<Node> getChildren() {
    return children;
  }
}
