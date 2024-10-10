/**
 * IntNode implements a node in a multi-way tree populated with ints.
 */
final class IntNode {
    // if isLeaf() children.length == 0
    // else children.length == elements.length + 1
    private int[] elements;
    private IntNode[] children;

    public IntNode() {
	elements = new int[0];
	children = new IntNode[0];
    }

    // Accessors
    public IntNode getChild(int index) { return children[index]; }
    public int getElement(int index) { return elements[index]; }
    public int size() { return elements.length; }
    public boolean isLeaf() { return children.length == 0; }
    public boolean isEmpty() { return elements.length == 0; }
    public boolean isFull() { return elements.length == 3; }

    // Mutators
    /**
     * Adds new right-most element.
     **/
    void addElement(int elem) {
	// elements and children always are the exact size as the number of
	// values in the array, allocating and copying a new array each time
	// the size is modified.
	// You normally wouldn't write it like this, but the seemingly
	// inefficient implementation happens to make Daikon run in a reasonable
	// amount of time; it's amazing what cubic time blow ups looks like. :-)
	// This will stress the garbage collector. Oh well...
	int[] newElements = new int[elements.length + 1];
	for (int i = 0; i < elements.length; ++i) {
	    newElements[i] = elements[i];
	}
	newElements[newElements.length - 1] = elem;
	elements = newElements;
    }

    /**
     * Inserts element at given index, shifting elements at or after the index
     * to the right.
     * index must be in range [0, size()]; no error checking is performed.
     **/
    void addElement(int index, int elem) {
	int[] newElements = new int[elements.length + 1];
	for (int i = 0; i < index; ++i) {
	    newElements[i] = elements[i];
	}
	newElements[index] = elem;
	for (int i = index; i < elements.length; ++i) {
	    newElements[i + 1] = elements[i];
	}
	elements = newElements;
    }

    /**
     * Adds new right-most child node.
     **/
    void addChild(IntNode child) {
	IntNode[] newChildren = new IntNode[children.length + 1];
	for (int i = 0; i < children.length; ++i) {
	    newChildren[i] = children[i];
	}
	newChildren[newChildren.length - 1] = child;
	children = newChildren;
    }

    /**
     * Inserts new child node at given index, shifting child nodes at or after
     * the index to the right.
     * index must be in range [0, size() + 1]; no error checking is preformed.
     **/
    void addChild(int index, IntNode child) {
	IntNode[] newChildren = new IntNode[children.length + 1];
	for (int i = 0; i < index; ++i) {
	    newChildren[i] = children[i];
	}
	newChildren[index] = child;
	for (int i = index; i < children.length; ++i) {
	    newChildren[i + 1] = children[i];
	}
	children = newChildren;
    }

    void setChild(int index, IntNode child) { children[index] = child; }
    void setElement(int index, int elem) { elements[index] = elem; }
    /**
     * Removes the child at given index, shifting remaining children to left.
     */
    void removeChild(int index) {
	IntNode[] newChildren = new IntNode[children.length - 1];
	for (int i = 0; i < index; ++i) {
	    newChildren[i] = children[i];
	}
	for (int i = index + 1; i < children.length; ++i) {
	    newChildren[i - 1] = children[i];
	}
	children = newChildren;
    }

    /**
     * Removes the element at the given index, shifting remaining elements left.
     **/
    void removeElement(int index) {
	int[] newElements = new int[elements.length - 1];
	for (int i = 0; i < index; ++i) {
	    newElements[i] = elements[i];
	}
	for (int i = index + 1; i < elements.length; ++i) {
	    newElements[i - 1] = elements[i];
	}
	elements = newElements;
    }

    /**
     * Returns string representation of tree using nested parentheses for
     * each child in the tree.
     **/
    public String toString() {
	String s = "(";
	for (int i = 0; i < elements.length; ++i) {
	    if (!isLeaf()) {
	      s += getChild(i);
	    }
	    s += " " + getElement(i) + " ";
	}
	if (!isLeaf()) {
	  s += getChild(elements.length);
	}
	s += ")";
	return s;
    }

    public boolean validate() {
	if (elements.length > 3) {
	    return false;
	}
	if (children.length > 4) {
	    return false;
	}
	if (children.length > 0 && children.length != elements.length + 1) {
	    return false;
	}

	for (IntNode child: children) {
	    if (!child.validate()) {
		return false;
	    }
	}
	return true;
    }
}
