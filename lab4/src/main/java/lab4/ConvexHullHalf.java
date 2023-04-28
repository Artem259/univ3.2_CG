package lab4;

import java.util.ArrayList;


public class ConvexHullHalf extends TTree<Coordinate2D> {
	protected CNode root;
	public ConvexHullHalf() {
		super();
	}
	
	protected static void DOWN (CNode n) {
		n.left.hull = new SubHull(CQueue.concatenate(n.hull.split(n.lMax.ex, LEFT, true), n.left.hull));
		n.right.hull = new SubHull(CQueue.concatenate(n.right.hull, n.hull));
	}
	
	protected static void UP (CNode n) {
		n.hull = SubHull.bridge(n.left.hull, n.right.hull);
	}
	
	protected static CNode rotateLeft (CNode n) {
		DOWN(n);
		CNode tempCNode = n.right;
		boolean tempColor = n.color;
		DOWN(tempCNode);
		n.right = tempCNode.left;
		n.color = tempCNode.color;
		UP(n);
		tempCNode.left = n;
		tempCNode.color = tempColor;
		UP(tempCNode);
		return tempCNode;
	}
	
	protected static CNode rotateRight (CNode n) {
		DOWN(n);
		CNode tempCNode = n.left;
		boolean tempColor = n.color;
		DOWN(tempCNode);
		n.left = tempCNode.right;
		n.color = tempCNode.color;
		UP(n);
		tempCNode.right = n;
		tempCNode.color = tempColor;
		UP(tempCNode);
		return tempCNode;
	}

	protected CNode addLeaf (Coordinate2D c) {
		size ++;
		return new CNode(c);
	}

	protected static void flipTripleColor (CNode n) {
		n.color = !n.color;
		n.left.color = !n.left.color;
		n.right.color = !n.right.color;
	}
	
	protected static CNode fixUp (CNode n) {
		if (n.isLeaf) {
			return n;
		}
		
		if (n.left.color == BLACK && n.right.color == RED) {
			n = rotateLeft(n);
		}
		else {
			if (n.left.color == RED && n.left.left.color == RED) {
				n = rotateRight(n);
			}
			if (n.left.color == RED && n.right.color == RED) {
				flipTripleColor(n);
			}
		}
		return n;
	}
	
	protected CNode insertAt (CNode n, Coordinate2D e) {
		if (e.compareTo(n.lMax.ex) <= 0) {
			if (n.isLeaf) {
				if (e.compareTo(n.ex) == 0) {
					n.ex = e;
				}
				else {
					CNode nNew = addLeaf(e);
					n = new CNode(nNew, nNew, n);
				}
			}
			else {
				DOWN(n);
				n.left = insertAt(n.left, e);
				UP(n);
			}
		}
		else {
			if (n.isLeaf) {
				CNode nNew = addLeaf(e);
				n = new CNode(n, n, nNew);
			}
			else {
				DOWN(n);
				n.right = insertAt(n.right, e);
				UP(n);
			}
		}
	
		n = fixUp(n);
		return n;
	}
	
	protected CNode deleteAt (CNode n, Coordinate2D e) {
		if (e.compareTo(n.lMax.ex) <= 0) {
			if (n.left.isLeaf) {
				if (e.compareTo(n.left.ex) != 0) {
					return n;
				}
				else {
					DOWN(n);
					removeLeaf(n.left);
					return n.right;
				}
			}
		
			DOWN(n);
			
			if (e.compareTo(n.lMax.ex) == 0) {
				CNode tempCNode = n.left;
				while (!tempCNode.right.isLeaf) {
					tempCNode = tempCNode.right;
				}
				n.lMax = tempCNode.lMax;
			}
			
			if (n.left.color == RED || n.left.left.color == RED) {
				n.left = deleteAt(n.left,e);
				UP(n);
			}
			else {
				flipTripleColor(n);
				n.left = deleteAt(n.left,e);
				if (n.left.color == RED) {
					UP(n);
					flipTripleColor(n);
				}
				else if (n.right.left.color == BLACK) {
					UP(n);
					n = rotateLeft(n);
				}
				else {
					n.right = rotateRight(n.right);
					UP(n);
					n = rotateLeft(n);
					flipTripleColor(n);
				}
			}
		}
		else {
			if (n.right.isLeaf) {
				if (e.compareTo(n.right.ex) != 0) {
					return n;
				}
				else {
					DOWN(n);
					removeLeaf(n.right);
					n.left.color = BLACK;
					return n.left;
				}
			}
			else if (n.right.left.color == RED) {
				DOWN(n);
				n.right = deleteAt(n.right,e);
				UP(n);
			}
			else if (n.color == RED) {
				
				flipTripleColor(n);
				DOWN(n);
				n.right = deleteAt(n.right,e);
				UP(n);
				if (n.right.color == RED) {
					flipTripleColor(n);
				}
				else if (n.left.left.color == RED) {
					n = rotateRight(n);
					flipTripleColor(n);
				}
			}
			else {
				n = rotateRight(n);
				DOWN(n);
				n.right = deleteAt(n.right,e);
				UP(n);
				if (n.right.color == RED) {
					n = rotateLeft(n);
				}
			}
		}
		return n;
	}

	public void insert (Coordinate2D e) {
		if (root == null) {
			root = new CNode(e);
			size = 1;
		}
		else {
			root = insertAt(root, e);
			if (root.color == RED) {
				root.color = BLACK;
			}
		}
	}

	public void delete (Coordinate2D e) {
		if (root == null) {
			return;
		}
		if (root.isLeaf) {
			if (e.compareTo(root.ex) == 0) {
				root = null;
				size = 0;
			}
		}
		else {
			if (root.left.color == BLACK && root.right.color == BLACK) {
				root.color = RED;
			}
			root = deleteAt(root, e);
			if (root.color == RED) {
				root.color = BLACK;
			}
		}
	}

	public ArrayList<Coordinate2D> getHull() {
		return root != null ? root.hull.getHull() : new ArrayList<>();
	}
}
