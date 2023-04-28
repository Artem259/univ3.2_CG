package lab4;


public class CNode extends Node<Coordinate2D> {
	protected CNode left, right, lMax;
	protected SubHull hull;
	
	public CNode (Coordinate2D c) {
		this.isLeaf = true;
		this.color = BLACK;
		this.ex = c;
		this.lMax = this;

		hull = new SubHull(c);
	}

	public CNode (CNode lMax, CNode left, CNode right) {
		this.isLeaf = false;
		this.color = RED;
		this.lMax = lMax;
		this.left = left;
		this.right = right;
		
		hull = SubHull.bridge(left.hull, right.hull);
	}
}
