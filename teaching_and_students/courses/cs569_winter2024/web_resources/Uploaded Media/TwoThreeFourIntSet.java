import java.util.AbstractCollection;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;

/**
 * Implements a multi-set of ints using a 2-3-4 tree data-structure.
 * Since the Java Collections library does not have a MultiSet interface, we try
 * to follow java.util.Set's interface, but don't implement it.
 *
 * Note before reusing this code: Not thread-safe! Not guaranteed to be correct!
 */
public class TwoThreeFourIntSet extends AbstractCollection<Integer> {

    public static boolean printDebug = false;
    private IntNode root;
    private int size;
    private int height;

    public TwoThreeFourIntSet() {
	root = new IntNode();
	size = 0;
	height = 1;
	// System.out.println("Root: " + root.hashCode());
    }

    public boolean add(int elem) {
	if (printDebug) { System.out.println("Adding " + elem); }
	// Special case for full root
	if (root.isFull()) {
	    IntNode oldRoot = root;
	    // Temporarily create an invalid tree containing 0 elements and
	    // 1 "left" child. This will be made into a valid tree with
	    // 1 element and 2 children by splitfullNode.
	    root = new IntNode();
	    // System.out.println("New root: " + root.hashCode());
	    root.addChild(oldRoot);
	    splitFullNode(root, 0);
	    height += 1;
	}
	boolean success = add(root, elem);
	if (success) {
	    size += 1;
	    if (printDebug) { System.out.println(root); }
	}
	return success;
    }

    public boolean containsAll(Collection<?> c) {
	// Override AbstractCollection.containsAll to use our recursive
	// contains instead of iterating through iterator.
	for (Object o: c) {
	    if (!contains(o)) {
		return false;
	    }
	}
	return true;
    }

    /**
     * Removes given element if present, from the subtree rooted at given node.
     * Note that tree may be modified even if elem is not present.
     * For those playing along at home, this is a straightforward implementation
     * of the B-Tree removal algorithm as described in Cormen/Leiserson/Rivest's
     * Introduction to Algorithms book, with the case numbers used in the debug
     * statement matching the numbering system used in their description.
     */
    private boolean remove(IntNode node, int elem) {
	if (printDebug) { System.out.println("e: " + elem + " n: " + node); }
	int i = 0;
	for (i = 0; i < node.size(); ++i) {
	    if (elem == node.getElement(i)) {
		// Exact key match
		if (node.isLeaf()) {
		    if (printDebug) { System.out.println("Leaf 1"); }
		    node.removeElement(i);
		    return true;
		}
		IntNode leftChild = node.getChild(i);
		if (leftChild.size() > 1) {
		    if (printDebug) { System.out.println("Promote 2a"); }
		    IntNode predecessor = rightMost(leftChild);
		    int replacement = predecessor.getElement(predecessor.size() - 1);
		    node.setElement(i, replacement);
		    return remove(leftChild, replacement);
		}
		IntNode rightChild = node.getChild(i + 1);
		if (rightChild.size() > 1) {
		    if (printDebug) { System.out.println("Promote 2b");  }
		    IntNode successor = leftMost(rightChild);
		    int replacement = successor.getElement(0);
		    node.setElement(i, replacement);
		    return remove(rightChild, replacement);
		}
		// Both children have only 1 child.
		if (printDebug) { System.out.println("Merge 2c"); }
		merge(node, i);  // All elements moved into leftChild.
		return remove(leftChild, elem);
	    }
	    if (elem < node.getElement(i)) {
		break;
	    }
	}
	// No exact match. i either points to first element greater than
	// elem, or 1 past the end. (i thus also points to child tree expected
	// to contain value if present.)
	if (node.isLeaf()) {
	    // Element not found.
	    if (printDebug) { System.out.println("NOT FOUND"); }
	    return false;
	}
	IntNode childWithElem = node.getChild(i);
	if (childWithElem.size() > 1) {
	    if (printDebug) { System.out.println("Descend 3"); }
	    return remove(childWithElem, elem);
	}
	// child is a 2-node. Need to get some extra elements from siblings
	// and/or parents before we can descend.
	if (i < node.size()) {
	    // i is not rightmost child, so it has a sibling to the right.
	    IntNode rightSibling = node.getChild(i + 1);
	    if (rightSibling.size() > 1) {
		if (printDebug) { System.out.println("Transfer 3a right"); }
		// Move parent element into left child
		childWithElem.addElement(node.getElement(i));
		// Move leftmost element of right sibling to parent
		node.setElement(i, rightSibling.getElement(0));
		if (!childWithElem.isLeaf()) {
		    // Make right sibling's leftmost tree the rightmost tree of node
		    childWithElem.addChild(rightSibling.getChild(0));
		    // Delete leftmost child from right sibling
		    rightSibling.removeChild(0);
		}
		// Delete leftmost element from right sibling
		rightSibling.removeElement(0);
		return remove(childWithElem, elem);
	    }
	}
	if (i > 0) {
	    // i is not leftmost child, so it has a sibling to the left.
	    IntNode leftSibling = node.getChild(i - 1);
	    if (leftSibling.size() > 1) {
		if (printDebug) { System.out.println("Transfer 3a left"); }
		// Move parent element into right child
		childWithElem.addElement(0, node.getElement(i - 1));
		// Move rightmost element of left sibling to parent
		node.setElement(i - 1, leftSibling.getElement(leftSibling.size() - 1));
		if (!childWithElem.isLeaf()) {
		    // Make left sibling's rightmost tree the leftmost tree of node
		    childWithElem.addChild(0, leftSibling.getChild(leftSibling.size()));
		    // Delete rightmost child from left sibling
		    leftSibling.removeChild(leftSibling.size());
		}
		// Delete rightmost element from left sibling
		leftSibling.removeElement(leftSibling.size() - 1);
		return remove(childWithElem, elem);
	    }
	}
	// Immediate siblings all have too few children, so can't just transfer
	// elements. Perform merge with sibling and a parent element.
	if (i < node.size()) {
	    if (printDebug) { System.out.println("Merge 3b right"); }
	    merge(node, i);
	    return remove(childWithElem, elem);
	} else {
	    if (printDebug) { System.out.println("Merge 3b left"); }
	    merge(node, i - 1);
	    return remove(node.getChild(node.size()), elem);
	}
    }

    /**
     * Checks if root node is empty.
     * @return true if root was replaced, false otherwise.
     **/
    private boolean checkRoot() {
	if (root.size() == 0) {
	    if (printDebug) { System.out.println("REPLACING ROOT!"); }
	    root = root.getChild(0);
	    height -= 1;
	    return true;
	}
	return false;
    }

    /**
     * Merges element in node at given index with child trees to left and right
     * into a single larger tree.
     * The tree to the left is preserved and modified in place; the tree to the
     * right of the given tree is removed from the node, along with the element.
     *
     * This may create an invalid tree if merging the only element in a 2-tree
     * with the left/right children. This should only happen at the root; in
     * such a case, the root should be replaced with the single child.
     **/
    private void merge(IntNode node, int index) {
	IntNode leftChild = node.getChild(index);
	IntNode rightChild = node.getChild(index + 1);
	int parentElem = node.getElement(index);
	// Add parent element into left child
	leftChild.addElement(parentElem);
	// Transfer elements and children trees from right to left sibling.
	for (int i = 0; i < rightChild.size(); ++i) {
	    leftChild.addElement(rightChild.getElement(i));
	}
	if (!rightChild.isLeaf()) {
	    // Note: <= instead of < since there is 1 more child than element.
	    for (int i = 0; i <= rightChild.size(); ++i) {
		leftChild.addChild(rightChild.getChild(i));
	    }
	}
	// Delete from parent.
	node.removeElement(index);
	node.removeChild(index + 1);
	checkRoot();
    }

    public boolean remove(int elem) {
	if (printDebug) { System.out.println("******* Remove " + elem); }
	boolean success = remove(root, elem);
	if (success) {
	    size -= 1;
	}
	return success;
    }

    public boolean isEmpty() { return root.isEmpty(); }
    public int size() { return size; } 
    public int height() { return height; }

    public boolean contains(Object o) {
	return containsRecursive((Integer)o);
    }

    private static boolean containsRecursive(IntNode node, int elem) {
	for (int i = 0; i < node.size(); ++i) {
	    if (elem == node.getElement(i)) {
		return true;
	    }
	    if (elem < node.getElement(i)) {
		if (node.isLeaf()) {
		    return false;
		} else {
		    return containsRecursive(node.getChild(i), elem);
		}
	    }
	}
	// Past last element of node; element (if present) must be in or below
	// rightmost child.
	if (node.isLeaf()) {
	    return false;
	}
	return containsRecursive(node.getChild(node.size()), elem);
    }

    public boolean containsRecursive(int elem) {
	return containsRecursive(root, elem);
    }

    /**
     * Returns iterator that iterates through all elements in tree in
     * non-decreasing order. Iterator is not thread safe.
     * Used by AbstractCollection.toString().
     **/
    public Iterator<Integer> iterator() {
	return new TreeIter(root);
    }

    /**
     * Returns string representation of given tree, showing the children using
     * nested parentheses.
     **/
    public String treeString() {
	return root.toString();
    }

    /**
     * Returns the left-most descendant of the given node.
     **/
    private IntNode leftMost(IntNode node) {
	return node.isLeaf() ? node : leftMost(node.getChild(0));
    }

    /**
     * Returns the right-most descendant of the given node.
     **/
    private IntNode rightMost(IntNode node) {
	return node.isLeaf()? node : rightMost(node.getChild(node.size()));
    }

    /**
     * Splits full child of given parent node into 2 separate children, pushing
     * middle element of child into parent.
     *
     * Child node at given index is preserved as the tree to the left of the
     * element pushed into parent, and new split node is placed to the right
     * of element pushed to parent.
     **/
    public void splitFullNode(IntNode parent, int indexInParent) {
	IntNode node = parent.getChild(indexInParent);
	if (printDebug) { System.out.println("Splitting " + node); }
	// Child should be a full 4-tree (i.e. 3 elements)
	IntNode rightSibling = new IntNode();
	// System.out.println("New sibling: " + node.hashCode());
	// Make rightmost element of node leftmost element of new sibling
	rightSibling.addElement(node.getElement(2));
	if (!node.isLeaf()) {
	    // Make rightmost children into leftmost children of new sibling
	    for (int i = 2; i <= 3; ++i) {
		IntNode child = node.getChild(i);
		rightSibling.addChild(child);
	    }
	}
	// We will copy the original middle element into the parent.
	int oldMiddleElement = node.getElement(1);
	// Now that elements are copied into sibling, delete those elements
	// and middle element from original node.
	if (!node.isLeaf()) {
	    for (int i = node.size(); i >= 2; --i) {
		node.removeChild(i);
	    }
	}
	for (int i = node.size() - 1; i >= 1; --i) {
	    node.removeElement(i);
	}

	// Insert new sibling into parent, to the right of original node.
	parent.addElement(indexInParent, oldMiddleElement);
	parent.addChild(indexInParent + 1, rightSibling);
    }

    public boolean add(IntNode node, int elem) {
	int pos = 0;
	while (pos < node.size() && elem >= node.getElement(pos)) {
	    ++pos;
	}
	// pos now points to first element greater than or equal to elem.
	if (node.isLeaf()) {
	    // Insert directly into node.
	    node.addElement(pos, elem);
	    return true;
	} else {
	    IntNode child = node.getChild(pos);
	    // Split child if already full.
	    if (child.isFull()) {
		splitFullNode(node, pos);
		// Check if we need to add to the newly added sibling instead.
		if (elem >= node.getElement(pos)) {
		    child = node.getChild(pos + 1);
		}
	    }
	    return add(child, elem);
	}
    }

    public boolean validate() {
	return root.validate();
    }

    private class IntNodeIndexPair {
	IntNode node;
	int index;

	IntNodeIndexPair(IntNode node, int index) {
	    this.node = node;
	    this.index = index;
	}
    }

    /**
     * Iterator through sorted tree elements, using in-order tree traversal.
     **/
    private class TreeIter implements Iterator<Integer> {
	// List of all ancestors with index into elements of ancestor.
	// The index says which child we are currently inside.
	LinkedList<IntNodeIndexPair> ancestors;

	/**
	 * In-order iterator of every value in sub-tree rooted at given node.
	 */
	public TreeIter(IntNode rootNode) {
	    ancestors = new LinkedList<IntNodeIndexPair>();
	    if (rootNode.size() > 0) {
		addLeftMostToAncestors(rootNode);
	    }
	}

	/**
	 * Pushes given node and all left-most descendants to ancestor stack.
	 **/
	private void addLeftMostToAncestors(IntNode node) {
	    ancestors.add(new IntNodeIndexPair(node, 0));
	    if (!node.isLeaf()) {
		addLeftMostToAncestors(node.getChild(0));
	    }
	}

	public boolean hasNext() {
	    return !ancestors.isEmpty();
	}

	public Integer next() {
	    IntNodeIndexPair next = ancestors.getLast();
	    int retVal = next.node.getElement(next.index);
	    if (next.node.isLeaf()) {
		// Modify in-place the last entry
		++next.index;
		while (!ancestors.isEmpty()) {
		    // Ya, the first time this is called, this is redundant.
		    next = ancestors.getLast();
		    if (next.index < next.node.size()) {
			break;
		    }
		    // Out of elements at this level, pop stack to go to parent.
		    ancestors.removeLast();
		}
		// Completely out of parents, so reached the end.
	    } else {
		// Point to next element in this node. It will be returned the
		// next time we return to this level of the tree.
		++next.index;
		// Travel down child in sub-tree to the right of current value
		addLeftMostToAncestors(next.node.getChild(next.index));
	    }
	    return retVal;
	}

	public void remove() {
	    throw new UnsupportedOperationException();
	}
    }
}
