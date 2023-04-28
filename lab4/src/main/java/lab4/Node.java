package lab4;


public class Node<E extends Comparable<E>> {
	final protected static boolean RED = false;
	final protected static boolean BLACK = true;
	protected E ex;
	protected Node<E> left, right, lMax;
	protected boolean color;
	protected boolean isLeaf;

	public Node() {}
	
	public Node(E e) {
		this.isLeaf = true;
		this.color = BLACK;
		this.ex = e;
		this.lMax = this;
	}

	public Node(Node<E> lMax, Node<E> left, Node<E> right) {
		this.isLeaf = false;
		this.color = RED;
		this.lMax = lMax;
		this.left = left;
		this.right = right;
	}
}
