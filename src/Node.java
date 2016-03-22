package src;


public class Node {
	private String bestAttribute;
	private Node left;
	private Node right;

	public Node(String bestAttribute, Node left, Node right){
		this.bestAttribute = bestAttribute;
		this.left = left;
		this.right = right;
	}

	public String getBestAttribute() {
		return bestAttribute;
	}

	public void setBestAttribute(String bestAttribute) {
		this.bestAttribute = bestAttribute;
	}

	public Node getLeft() {
		return left;
	}

	public void setLeft(Node left) {
		this.left = left;
	}

	public Node getRight() {
		return right;
	}

	public void setRight(Node right) {
		this.right = right;
	}


	public void report(String indent){
		System.out.format("%s%s = True:\n",indent, bestAttribute);
		left.report(indent+" ");
		System.out.format("%s%s = False:\n",indent, bestAttribute);
		right.report(indent+" ");
	}
}
