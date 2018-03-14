import java.util.Arrays;
import java.util.Collections;

public class TwoThreeTree {
	
	private static class Node {
		private static final int MAX_CHILDREN = 4;
		private static final int MAX_KEYS = 3;
		
		private Node[] children = new Node[Node.MAX_CHILDREN];
		private int[] keys = new int[Node.MAX_KEYS];
		private int numKeys = 0;
		
		
		
		/** 
		 * Constructor needs to take at least a key
		 */
		public Node(int x)  {
			this.keys[0] = x;
			this.numKeys++;
		}
				
		private boolean isLeaf()  { return ( children[0] == null ); }
		
		/**
		 * Recursive function adding a key to a series of node
		 * @param key value to insert
		 * @return null is the node did not split, a reference to the resulting node (itself) if the node split.
		 */
		public Node addKey(int key) {
			Node childToMerge = null;
			
			// Exit case: If the node is a leaf, merge an node with just a key with the current node (ie, add the key)
			if( isLeaf() ) {
				childToMerge = this.merge(new Node(key));
			// Recursive case: go down the children until finding the leaf
			}else {
				int i = 0;
				while(keys[i] < key && i < Node.MAX_KEYS) {
					i++;
				}
				// Check for duplicate
				if (keys[i] == key) {
					return null;
				}
				// Go to the appropriate child to insert the key
				childToMerge = children[i].addKey(key);
			}
			
			// If a split occurred within one of the children, merges the new node and returns the result of the merge back to parent
			if(childToMerge!= null) {
				return this.merge(childToMerge);
			}
			
			// Signify no nodes were split
			return null;
		}
		
		enum TYPE{
			children, keys
		}
		private void moveRight(TYPE type, int index) {
			switch (type) {
				// Move the children to the right, freeing the index
				case children:
					int i = this.numKeys;
					while(i > index) {
						children[i+1] = children[i];
						i--;
					}
					break;
				case keys:
					i = this.numKeys -1;
					while(i > index) {
						keys[i+1] = keys[i];
						i--;
					}
					break;
			}
		}
		/**
		 * merges the keys and children of two nodes and check if the resulting node needs to split
		 * precondition: child has exactly one key and no more than 2 children
		 * @param child node to merge with
		 * @return null if the resulting node did not split, a reference to the resulting node if the node split.
		 */
		private Node merge(Node child) {
			int keyIndex = 0;
			while(keyIndex < this.numKeys && child.keys[0] > this.keys[keyIndex]) {
				keyIndex++;
			}
			// Check for duplicate, does not do anything
			if (child.keys[0] == this.keys[keyIndex]) {
				return null;
			}
			// Move the keys to make room
			moveRight(TYPE.keys,  keyIndex);
			keys[keyIndex] = child.keys[0];	
			numKeys++;
			
			// make sure there is room
			moveRight(TYPE.children, keyIndex);
			// add the child
			children[keyIndex] = children[0];
			moveRight(TYPE.children, keyIndex +1);
			children[keyIndex + 1] = children[1];

			if (numKeys == Node.MAX_KEYS) {
				return this.splitNode();
			}
			return null;
		}
		
		/**
		 * Split a node of 4 children and 3 keys
		 * precondition: the node calling the function has to have 4 children and three keys
		 * @return a reference to the center node created, to be given to its parent node for merging
		 */
		public Node splitNode() {
			// Create the new nodes
			Node node0 = new Node(this.keys[0]);
			Node node1 = new Node(this.keys[2]);
			
			// Set the children of the middle nodes
			node0.children[0] = this.children[0];
			node0.children[1] = this.children[1];
			node1.children[2] = this.children[2];
			node1.children[3] = this.children[3];
			
			// Set this node
			this.children[0] = node0;
			this.children[1] = node1;
			this.children[2] = null;
			this.children[3] = null;
			this.keys[0] = this.keys[1];
			this.numKeys = 1;

			return this;
		}
		
		/**
		 * Return the node either contains the given value or the last node before the end of the tree.
		 * @param value value to look for
		 * @return a node containing the value or the last node encountered
		 */
		public Node search(int value) {
			// Exit Case: there are no children
			if(children[0] == null) {
				return this;
			}
			
			// Find the appropriate index 
			int index = 0;
			while(keys[index] < value && index <= numKeys) {
				index++;
			}
			
			// Exit Case: if this is the right place
			if (keys[index] == value) {
				return this;
			}
			
			// Recursive case: go to the appropriate child to search there
			return children[index-1].search(value);
		}
		
		/**
		 * Return the keys separated with a space
		 * @return "key1 key2 key3"
		 */
		public String toString() {
			String returnValue = "";
			for(int i = 0; i < numKeys; i++) {
				returnValue += keys[i] + " ";
			}
			return returnValue.substring(0, returnValue.length()-1);
					}
	} // end of Node class


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
	public void insert(int value) {
		if (root == null) {
			root = new Node(value);
		} else {
			root.addKey(value);
		}
	}
	
		
	/**
	 * Looks for a number in the tree. Returns a string the the key/keys in that node or in the last leaf node visited
	 * if the value is not in the tree.
	 * @param x number to look for
	 * @return key(s) of the last node visited
	 */
	public String search(int value) {
		if (root != null) {
			return root.search(value).toString();
		}
		
		return "The tree is empty";
	}
	
}
