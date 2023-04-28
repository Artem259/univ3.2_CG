package lab4;

import java.util.ArrayList;


public class SubHull extends CQueue<Coordinate2D> {
	public SubHull(Coordinate2D c) {
		super(c);
	}
	
	public SubHull(CQueue<Coordinate2D> q) {
		super(q.root, q.height, q.minNode, q.maxNode);
	}
	
	public static SubHull bridge (SubHull lHull, SubHull rHull) {
		Node<Coordinate2D> lItr = lHull.root;
		Node<Coordinate2D> rItr = rHull.root;
		
		boolean done = false;
		double middleX = (lHull.maxNode.ex.x + rHull.minNode.ex.x)/2.0;
		
		while ( !done ) {
			double t = computeSlope(lItr.lMax, rItr.lMax);
			int iL = determineCase(lItr.lMax, t);
			int iR = determineCase(rItr.lMax, t);
		
			switch (iL) {
			case -1:
				switch (iR) {
					case -1 -> rItr = rItr.right;
					case 0 -> {
						lItr = lItr.right;
						if (!rItr.isLeaf && rItr.right != null) {
							rItr = rItr.right;
						}
					}
					case +1 -> {
						double lHeight = lItr.lMax.ex.y +
								computeSlope(lItr.lMax, lItr.lMax.right) * (middleX - lItr.lMax.ex.x);
						double rHeight = rItr.lMax.ex.y +
								computeSlope(rItr.lMax.left, rItr.lMax) * (middleX - rItr.lMax.ex.x);
						if (lHeight <= rHeight) {
							rItr = rItr.left;
						} else {
							lItr = lItr.right;
						}
					}
				}
				break;
			case 0:
				switch (iR) {
					case -1 -> {
						if (!lItr.isLeaf && lItr.left != null) {
							lItr = lItr.left;
						}
						rItr = rItr.right;
					}
					case 0 -> {
						lItr = lItr.lMax;
						rItr = rItr.lMax;
						done = true;
					}
					case +1 -> {
						if (!lItr.isLeaf && lItr.left != null) {
							lItr = lItr.left;
						}
						rItr = rItr.left;
					}
				}
				break;
			case +1:
				switch (iR) {
					case -1 -> {
						lItr = lItr.left;
						rItr = rItr.right;
					}
					case 0 -> {
						lItr = lItr.left;
						if (!rItr.isLeaf && rItr.right != null) {
							rItr = rItr.right;
						}
					}
					case +1 -> lItr = lItr.left;
				}
				break;
			}
		}
		return new SubHull(concatenate(lHull.split(lItr.ex, LEFT, true), rHull.split(rItr.ex, RIGHT, false)));
	}
	
	public ArrayList<Coordinate2D> getHull() {
		if (root == null) {
			return new ArrayList<>();
		}

		ArrayList<Coordinate2D> res = new ArrayList<>();
		Node<Coordinate2D> n = minNode;
		while (n != null) {
			res.add(n.ex);
			n = n.right;
		}
		return res;
	}
	
	protected static double computeSlope (Node<Coordinate2D> leftN, Node<Coordinate2D> rightN) {
		return (rightN.ex.y - leftN.ex.y)/(rightN.ex.x - leftN.ex.x);
	}
	
	protected static int determineCase (Node<Coordinate2D> n, double t) {
		boolean leftAbove = true;
		boolean rightAbove = false;
		
		if ( (n.left != null) && computeSlope(n.left, n) < t  ) {
			leftAbove = false;
		}
		
		if ( (n.right != null) && computeSlope(n, n.right) > t  ) {
			rightAbove = true;
		}
		
		if (leftAbove && rightAbove) {
			return -1;
		}
		else if (!leftAbove && !rightAbove) {
			return +1;
		}
		else {
			return 0;
		}
	}
}
