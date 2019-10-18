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
   * Gets a string that contains the whole SyntaxTree in a String format.
   *
   * @return a String containing the SyntaxTree.
   */
  public String depthFirstTraversal() {
    List childList = root.getChildren();
    int depth = 0;
    String toReturn = root.getVal().toString() + "\n";

    toReturn += dft(toReturn, childList, depth);

    return toReturn;
  }

  private String dft(String toReturn, List children, int depth) {
    depth++;
    for (Object child : children) {
      if (child instanceof Node) {
        return dft(toReturn, ((Node)child).getChildren(), depth);
      } else {
        int j = 0;
        while (j < depth) {
          toReturn += "-";
          j++;
        }
        toReturn += child.toString() + "\n";
      }
    }

    return toReturn;
  }
}
