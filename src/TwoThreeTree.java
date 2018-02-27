package src;

import java.util.PriorityQueue;

public class TwoThreeTree {
	
	private class Node implements Comparable<Node>{
		private Node leftChild = null;
		private Node middleChild = null;
		private Node rightChild = null;
		private Node parentNode = null;
		private Integer key1 = null;
		private Integer key2 = null;
		
		/** Default constructor
		 * 
		 */
		public Node(int x)  {
			this.key1 = x;
		}
		
		
		// Mutator
		public void setLeft(Node aNode) 		{ this.leftChild = aNode; }
		public void setMiddle(Node aNode)	 	{ this.middleChild = aNode; }
		public void setRight(Node aNode) 		{ this.rightChild = aNode; }
		public void setParent(Node aNode) 		{ this.parentNode = aNode; }
		public void setKey1(Integer aKey)		{ this.key1 = aKey; }
		public void setKey2(Integer aKey)		{ this.key2 = aKey; }
		
		// Accessory
		public Node getLeft()		{ return this.leftChild; }
		public Node getRight()		{ return this.rightChild; }
		public Node getMiddle() 	{ return this.middleChild; }
		public Node getParent() 	{ return this.parentNode; }
		public Integer getKey1()	{ return this.key1; }
		public Integer getKey2()	{ return this.key2; }


		/**
		 * Compare the Nodes using their first key.
		 */
		@Override
		public int compareTo(Node other) {
			
			// if the key of this has not been set up yet, the node come last
			if(this.key1 == null) {
				return -1;
			} else if (other.key1 == null) {
				return +1;
			} else {
				return Integer.compare(this.key1, other.key1);
			}
		}
	}


	private Node root = null;
	
	/** Default constructor
	 * 
	 */
	public TwoThreeTree() {
	}
	
	/**
	 * insert a number into the tree. Does not insert duplicates
	 * @param x number to add
	 */
	public void insert(int x) {

		// set up the root
	if (this.root == null) {
			this.root = new Node(x);
			return;
		}
		Node current = this.root;
		
		// Find the last node
		while(current.getLeft() != null) {
			// If the value is smaller than the first key, go to the left
			if (x < current.getKey1()) {
				current = current.getLeft();
			// If there is a second key and the value is between key1 and key2, go to the middle child
			} else if( x == current.getKey1()) {
				return;
				// If the second key exist (ie if the node has three children)
			} else if (current.getKey2() != null) {
				// if the value goes between the left and right child
				if (x < current.getKey2()) {
					current = current.getMiddle();
				// if the value is already in the tree
				} else if (x == current.getKey2()) {
					return;
				// if the value goes after the right child
				} else {
					current = current.getRight();
				}
			// If the value goes after the right child
			} else {
				current = current.getRight();
			}
		}
		
		// Compare with the last node traversed and insert it
		// If there is room
		if (current.getKey2() == null) {
			// Add the key to the node
			current.setKey2(x);
			// Orders the keys by increasing numbers
			this.orderKeys(current);
		}
		// Else split node(s), and values up as need
		else {
			this.splitLeaf (current, x);
		}
	}
	
	/**
	 * Spilt a leaf and sends the new node up
	 * precondition: current != null
	 * @param current leaf to split
	 * @param x value to insert
	 */
	private void splitLeaf(Node current, Integer x) {
		// Exit case : there is room in the second key to insert x or the parent is the root (ie getParent == null)
		// Calling recursive split case : there is a value to insert (x != null)
		
		// orders the 3 keys to create appropriate new Nodes (left child, middle to move up, right child)
		PriorityQueue<Integer> maxHeap = new PriorityQueue<Integer>();
		maxHeap.add(current.getKey1());
		maxHeap.add(current.getKey2());
		maxHeap.add(x);

		// Split up left/right and middle, sets up children
		Node leftNode = new Node(maxHeap.remove());
		Node middleNode = new Node(maxHeap.remove());
		Node rightNode = new Node(maxHeap.remove());
		middleNode.setLeft(leftNode);
		middleNode.setRight(rightNode);
		rightNode.setParent(middleNode);
		leftNode.setParent(middleNode);
		
		// Call on parent with middle key
		current.setKey1(null);	// signifies to split that this node is no longer relevant
		sendUp(current.parentNode, middleNode);
	}
	
	/**
	 * Recursive function sending a node up with its children
	 * Recursively split the node if needed
	 * @param current Node to send up
	 * @param x Node to insert with two children to attach back to the parent node
	 */
	private void sendUp (Node current, Node x) {
		// Exit case 1: the current node is the root
		if(current == null) {
			this.root = x;
		}
		// Exit case 2: there is room in the current node to add the key
		else if(current.getKey2() == null) {
			// Add the key
			current.setKey2(x.getKey1());
			this.orderKeys(current);
			
			// Attaches the children in proper order
			PriorityQueue<Node> maxHeap = new PriorityQueue<Node>();
			maxHeap.add(current.getLeft());
			maxHeap.add(current.getRight());
			maxHeap.add(x.getRight());
			maxHeap.add(x.getLeft());
			maxHeap.remove(); // get ride of the irrelevant node
			current.setLeft(maxHeap.remove());
			current.setMiddle(maxHeap.remove());
			current.setRight(maxHeap.remove());
			current.getRight().setParent(current);
			current.getMiddle().setParent(current);
			current.getLeft().setParent(current);;
		}
		// Recursive case : the node needs to split and send a value + children up
		else {
			PriorityQueue<Node> maxHeap = new PriorityQueue<Node>();
			// One of those children is not longer relevant, its key1 = null so the compareTp method will make sure it
			// is the lowest value of the maxheap.
			// Get all the possible children of the current node (4, that will later be divided among two new nodes
			maxHeap.add(current.getLeft());
			maxHeap.add(current.getMiddle());
			maxHeap.add(current.getRight());
			maxHeap.add(x.getLeft());
			maxHeap.add(x.getRight());
			//maxHeap.remove(); // get ride of the irrelevant node
			
			// Split the current node by comparing the key
			PriorityQueue<Integer> maxHeapInt = new PriorityQueue<Integer>();
			maxHeapInt.add(current.key1);
			maxHeapInt.add(current.key2);
			maxHeapInt.add(x.key1);
			maxHeap.remove(); // Get ride of the irrelevant node
			Node leftNode = new Node(maxHeapInt.remove());
			Node middleNode = new Node(maxHeapInt.remove());
			Node rightNode = new Node(maxHeapInt.remove());
			
			// Set the children of the node to send up
			middleNode.setLeft(leftNode);
			middleNode.setRight(rightNode);
			leftNode.setParent(middleNode);
			rightNode.setParent(middleNode);
			
			// Set the children of those children
			leftNode.setLeft(maxHeap.remove());
			leftNode.setRight(maxHeap.remove());
			rightNode.setLeft(maxHeap.remove());
			rightNode.setRight(maxHeap.remove());

			// Set the parents of the children's children
			leftNode.getLeft().setParent(leftNode);
			leftNode.getRight().setParent(leftNode);
			rightNode.getLeft().setParent(rightNode);
			rightNode.getRight().setParent(rightNode);
			
			// Recursive call to move the middle key up,
			// With reference to its children so they can be attached to the parent
			current.setKey1(null); // signifies the current node is no longer relevant
			this.sendUp(current.getParent(), middleNode);
		}
	}
	
	private void orderKeys(Node current) {
		if (current.getKey1() > current.getKey2()) {
			int temp = current.getKey1();
			current.setKey1(current.getKey2());
			current.setKey2(temp);
		}
	}

	
	/**
	 * Looks for a number in the tree. Returns a string the the key/keys in that node or in the last leaf node visited
	 * if the value is not in the tree.
	 * @param x number to look for
	 * @return key(s) of the last node visited
	 */
	public String search(int x) {
		if(this.root == null) {
			return "empty tree";
		}
		
		Node current = this.root;
		while(current.getLeft() != null) {
			if ( x < current.getKey1() ) {
				current = current.getLeft();
			} else if ( x == current.getKey1() ) {
				String returnValue = current.getKey1().toString();
				if(current.getKey2() != null) {
					returnValue += " " + current.getKey2();
				}
				return returnValue;
			} else if ( current.getKey2() != null ) {
				if ( x == current.getKey2() ) {
					return current.getKey1() + " " + current.getKey1();
				} else if ( x < current.getKey2() ) {
					current = current.getMiddle();
				} else {
					current = current.getRight();
				}
			} else {
				current = current.getRight();
			}
		}
		
		// if the value was not found, return the keys of the last visited node
		String returnValue = current.getKey1().toString();
		if(current.getKey2() != null) {
			returnValue += " " + current.getKey2();
		}
		return returnValue;
	}
	
}
