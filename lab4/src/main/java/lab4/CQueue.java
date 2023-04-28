package lab4;


public class CQueue<E extends Comparable<E>> extends TTree<E>{
	protected int height;
	protected Node<E> minNode, maxNode;
	
	public CQueue () {
		this.root = null;
		this.height = -1;
		this.minNode = null;
		this.maxNode = null;
	}
	
	public CQueue (E e) {
		this.root = new Node<>(e);
		this.height = 0;
		this.minNode = root;
		this.maxNode = root;
	}
	
	public CQueue (Node<E> root, int height, Node<E> minNode, Node<E> maxNode) {
		this.root = root;
		this.height = height;
		this.minNode = minNode;
		this.maxNode = maxNode;
	}

	public void shallowCopy (CQueue<E> other) {
		if (other == null) {
			return;
		}
		this.root = other.root;
		this.height = other.height;
		this.minNode = other.minNode;
		this.maxNode = other.maxNode;
	}
	
	protected void removeLeaf (Node<E> n) {
		if (n.left != null) {
			n.left.right = n.right;
		}
		else {
			minNode = n.right;
		}
		
		if (n.right != null) {
			n.right.left = n.left;
		}
		else {
			maxNode = n.left;
		}
	}

	protected static <E extends Comparable<E>> Node<E> glueTree (Node<E> lN, Node<E> rN, int lH, int rH, Node<E> lMax) {
		if (lN == null) {
			return rN;
		}
		else if (rN == null) {
			return lN;
		}
		else if (lH == rH) {
			return new Node<>(lMax, lN, rN);
		}
		else if (lH > rH) {
			lN.right = glueTree(lN.right, rN, lH-1, rH, lMax);
			lN = fixUp(lN);
			return lN;
		}
		else {
			if (rN.left.color == RED) {
				rN.left = glueTree(lN, rN.left, lH, rH, lMax);
				rN = fixUp(rN);
			}
			else {
				rN.left = glueTree(lN, rN.left, lH, rH-1, lMax);
			}
			return rN;
		}
	}
	
	public static <E extends Comparable<E>> CQueue<E> concatenate (CQueue<E> qLeft, CQueue<E> qRight) {
		if (qLeft == null || qLeft.height == -1) {
			return qRight;
		}
		else if (qRight == null || qRight.height == -1) {
			return qLeft;
		}

		qLeft.maxNode.right = qRight.minNode;
		qRight.minNode.left = qLeft.maxNode;

		int newHeight = Math.max(qLeft.height, qRight.height);
		Node<E> newRoot = glueTree(qLeft.root, qRight.root, qLeft.height, qRight.height, qLeft.maxNode);
		if (newRoot.color == RED) {
			newRoot.color = BLACK;
			newHeight++;
		}

		return new CQueue<> (newRoot, newHeight, qLeft.minNode, qRight.maxNode);
	}

	protected static <E extends Comparable<E>> void cutAt (Node<E> n) {
		if (n != null && n.right != null) {
			n.right.left = null;
			n.right = null;
		}
	}
	
	protected static <E extends Comparable<E>> void splitAt(Node<E> n, int h, E e, CQueue<E> qLeft, CQueue<E> qRight) {
		if (n.isLeaf) {
			if (e.compareTo(n.ex) < 0) {
				qRight.root = n;
				qRight.minNode = n;
				qRight.height = 0;
				qLeft.maxNode = n.left;
				cutAt(n.left);
			}
			else {
				qLeft.root = n;
				qLeft.maxNode = n;
				qLeft.height = 0;
				qRight.minNode = n.right;
				cutAt(n);
			}
		}
		else {
			if (e.compareTo(n.lMax.ex) == 0) {
				qLeft.root = n.left;
				qLeft.height = h-1;
				qLeft.maxNode = n.lMax;
				if (qLeft.root.color == RED) {
					qLeft.root.color = BLACK;
					qLeft.height ++;
				}
				qRight.root = n.right;
				qRight.height = h-1;
				qRight.minNode = n.lMax.right;
				cutAt(n.lMax);
			}
			else if (e.compareTo(n.lMax.ex) < 0) {
				if (n.left.color == RED) {
					n.left.color = BLACK;
					splitAt(n.left, h, e, qLeft, qRight);
				}
				else {
					splitAt(n.left, h-1, e, qLeft, qRight);
				}
				int tempHeight = qRight.height;
				qRight.root = glueTree(qRight.root, n.right, qRight.height, h-1, n.lMax);
				qRight.height = Math.max(tempHeight, h-1);
				if (qRight.root.color == RED) {
					qRight.root.color = BLACK;
					qRight.height ++;
				}
			}
			else {
				splitAt(n.right, h-1, e, qLeft, qRight);
				if (n.left.color == RED) {
					n.left.color = BLACK;
					qLeft.root = glueTree(n.left, qLeft.root, h, qLeft.height, n.lMax);
					qLeft.height = h;
				}
				else {
					qLeft.root = glueTree(n.left, qLeft.root, h-1, qLeft.height, n.lMax);
					qLeft.height = h - 1;
				}
				
				if (qLeft.root.color == RED) {
					qLeft.root.color = BLACK;
					qLeft.height ++;
				}
			}
		}
	}
	
	public CQueue<E> split(E e, boolean returnLoR, boolean inclusive) {
		CQueue<E> qLeft = new CQueue<>();
		CQueue<E> qRight = new CQueue<>();

		if (root == null) {
			return qLeft;
		}
		else if (e.compareTo(minNode.ex) < 0 || (e.compareTo(minNode.ex) == 0 && !inclusive)) {
			if (returnLoR == RIGHT) {
				qRight.shallowCopy(this);
				this.shallowCopy(qLeft);
				return qRight;
			}
			else {
				return qLeft;
			}
		}
		else if (e.compareTo(maxNode.ex) > 0 || (e.compareTo(maxNode.ex) == 0 && inclusive)) {
			if (returnLoR == RIGHT) {
				return qRight;
			}
			else {
				qLeft.shallowCopy(this);
				this.shallowCopy(qRight);
				return qLeft;
			}
		}
		else {
			Node<E> itr = root;
			while (!itr.isLeaf) {
				if (e.compareTo(itr.lMax.ex) <= 0) {
					itr = itr.left;
				}
				else {
					itr = itr.right;
				}
			}
			if (e.compareTo(itr.ex) == 0) {
				if (inclusive) {
					e = itr.ex;
				}
				else {
					e = itr.left.ex;
				}
			}
			else if (e.compareTo(itr.ex) < 0) {
				e = itr.left.ex;
			}
			else {
				e = itr.ex;
			}
		}

		qLeft.minNode = this.minNode;
		qRight.maxNode = this.maxNode;
		splitAt(this.root, this.height, e, qLeft, qRight);

		if (returnLoR == RIGHT) {
			this.shallowCopy(qLeft);
			return qRight;
		}
		else {
			this.shallowCopy(qRight);
			return qLeft;
		}
	}
}
