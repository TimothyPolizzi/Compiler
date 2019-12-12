import java.util.List;

/**
 * A tree that can only add direct children. To utilize this properly, it will have a tree created
 * for each production and add them back up together to make a fully functional tree.
 */
public class SyntaxTree {

  private Node root;
  private int size;

  /**
   * Generates a new SyntaxTree with a given String as its root.
   *
   * @param str the String to be the root of the SyntaxTree.
   */
  public SyntaxTree(String str) {
    root = new Node(str);
  }

  /**
   * Gets the root of the SyntaxTree.
   *
   * @return the Node root of the SyntaxTree.
   */
  public Node getRoot() {
    return root;
  }

  /**
   * Gets the current size of the SyntaxTree.
   *
   * @return the int value of the size of the SyntaxTree.
   */
  public int getSize() {
    return size;
  }

  /**
   * Adds a new Node containing the specified String to the SyntaxTree.
   *
   * @param root the String the new Node is to contain.
   */
  public void add(String root) {
    this.root.addChild(new Node(this.root, root));
    size++;
  }

  /**
   * Adds a preexisting tree to the current tree's children.
   *
   * @param tree The SyntaxTree to be added to tree.
   */
  public void add(SyntaxTree tree) {
    Node newChild = tree.getRoot();

    root.addChild(newChild);
    newChild.setParent(root);

    size += tree.getSize();
  }

  /**
   * Gets the depth from the root from a given node.
   * @param n The node to be traversed from.
   * @return The integer value of the distance between the node and the root.
   */
  public int getDepth(Node n) {
    int count = 0;

    while (n.getParent() != null) {
      n = n.getParent();
      count++;
    }

    return count;
  }

  /**
   * Gets a string that contains the whole SyntaxTree in a String format.
   *
   * @return a String containing the SyntaxTree.
   */
  public String depthFirstTraversal() {
    List childList = root.getChildren();
    int depth = 0;
    String toReturn = root.getVal().toString() + "\n";

    toReturn += dft(childList, depth);

    return toReturn;
  }

  private String dft(List children, int depth) {
    String toReturn = "";
    depth++;

    for (Object child : children) {

      if (((Node) child).getChildren().size() > 0) {
        toReturn += addDashes(depth) + ((Node) child).getVal() + "\n";
        toReturn += dft(((Node) child).getChildren(), depth);

      } else {
        toReturn += addDashes(depth) + ((Node) child).getVal() + "\n";
      }
    }
    return toReturn;
  }

  private String addDashes(int depth) {
    String toReturn = "";

    int j = 0;
    while (j < depth) {
      toReturn += "-";
      j++;
    }

    return toReturn;
  }
}
