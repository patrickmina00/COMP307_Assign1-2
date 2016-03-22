package src;

public class LeafNode extends Node{

	double probability;
	String type;


	public LeafNode(String type, double probability) {
		super(type,null,null);
		this.type = type;
		this.probability = probability;
	}

	public String getType(){
		return this.type;
	}

	public double getProbability() {
		return probability;
	}


	public void setProbability(double probability) {
		this.probability = probability;
	}


	public void report(String indent){
		System.out.format("%sClass %s, prob=%4.2f\n",indent, type, probability);
	}
}
