import java.util.Arrays;
import java.util.Collections;

public class TwoThreeTree {
	
	private static class Node {
		private static final int MAX_CHILDREN = 4;
		private static final int MAX_KEYS = 3;
		
		private Node[] children = new Node[Node.MAX_CHILDREN];
		private int numChildren = 0;
		private int[] keys = new int[Node.MAX_KEYS];
		private int numKeys = 0;
		
		
		
		/** 
		 * Constructor needs to take at least a key
		 */
		public Node(int x)  {
			this.keys[0] = x;
			this.numKeys++;
		}
		
		/**
		 * Add a child to the node if the max number of children was not already reached
		 * precondition: numChildren - numKeys <= 1
		 * @param child node child to add at the right position
		 */
		public void addChild(Node child) {
			if(this.numChildren < Node.MAX_CHILDREN && (numChildren - numKeys <= 1) ) {
				// Check where to add the child
				int i = numChildren;
				while (i >= 0) {
					if( i == 0) {
						children[i] = child;
					} else if (keys[i-1] > child.keys[0]) {
						children[i] = children [i-1];
					} else {
						children[i] = child;
						break;
					}
					i--;
				}
				this.numChildren++;
			}
		}
		
		/**
		 * Add a key to the node if the max number of key was not already reached
		 * @param key value to add to the leaf
		 * @return null is the leaf node did not split, a reference to the resulting node (itself) if the node split.
		 */
		private Node addKeytoLeaf(int key) {
			if(this.numKeys < Node.MAX_KEYS) {
				// Check where to insert the new key
				int i = numKeys;
				keys[numKeys] = key;
				Arrays.sort(keys, 0, numKeys);
				numKeys++;

				/**
				while( i >= 0 ) {
					// If the new key goes to the right of the current key
					if( i == 0 ) {
						keys[i] = key;
					// If the new key  goes to the left of the current key, moves the current key to free up some space
					} else if ( keys[i-1] > key) {
						keys[i] = keys[i-1];
					// If the new key goes to the last position
					} else {
						keys[i] = key;
						break;
					}
					i--;
				} */
				
				if (numKeys == Node.MAX_KEYS) {
					// Send the new Node back to its parent to merge the key and children
					return splitLeaf(this);
				}
			}
			// Signify no new nodes were created (nothing was split)
			return null;
		}
		
		private Node splitLeaf(Node leaf) {
			Node center = new Node(keys[1]);
			Node right = new Node(keys[2]);
			Node left = new Node(keys[0]);
			
			center.addChild(left);
			center.addChild(right);
			
			leaf = center;
			return leaf;
			
			
		}
		
		private boolean isLeaf()  { return ( numChildren == 0 ); }
		
		/**
		 * Recursive function adding a key to a series of node
		 * @param key value to insert
		 * @return null is the node did not split, a reference to the resulting node (itself) if the node split.
		 */
		public Node addKey(int key) {
			Node childToMerge = null;
			
			// Exit case: If the node is a leaf
			if( isLeaf() ) {
				childToMerge = this.addKeytoLeaf(key);
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
		
		/**
		 * merges the keys and children of two nodes and check if the resulting node needs to split
		 * @param child node to merge with
		 * @return null if the resulting node did not split, a reference to the resulting node if the node split.
		 */
		private Node merge(Node child) {
			this.addKey(child.keys[0]);
			this.addChild(child.children[0]);
			this.addChild(child.children[1]);
			if (numChildren == Node.MAX_CHILDREN || numKeys == Node.MAX_KEYS) {
				return splitNode(this);
			}
			return null;
		}
		
		/**
		 * Split a node of 4 children and 3 keys
		 * precondition: the node calling the function has to have 4 children and three keys
		 * @return a reference to the center node created, to be given to its parent node for merging
		 */
		public Node splitNode(Node overLoadedNode) {
			// Create the new nodes
			Node center = new Node(keys[1]);
			Node right = new Node(keys[0]);
			Node left = new Node(keys[2]);
			
			// Set the children of the middle nodes
			center.addChild(right);
			center.addChild(left);
			
			// Set the children of the left and right nodes
			right.addChild(overLoadedNode.children[0]);
			right.addChild(overLoadedNode.children[1]);
			left.addChild(overLoadedNode.children[2]);
			left.addChild(overLoadedNode.children[3]);
			
			overLoadedNode = center;
			return center;
		}
		
		/**
		 * Return the node either contains the given value or the last node before the end of the tree.
		 * @param value value to look for
		 * @return a node containing the value or the last node encountered
		 */
		public Node search(int value) {
			// Exit Case: there are no children
			if(numChildren == 0) {
				return this;
			}
			
			// Recursive Case : go through the keys of the node to check to which node to go next
			for(int i = 1; i <= numKeys; i++) {
				if(keys[i] > value) {
					// go to the child at position i-1
					return children[i-1].search(value);
				} else if (keys[i] < value) {
					// go to the next key
					i++;
				} else {
					// this is the right place!
					return this;
				}
			}
			// Should never reach that portion, just to shut up the compiler
			return null;
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
