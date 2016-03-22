package src;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.text.AttributedCharacterIterator.Attribute;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.TreeMap;


public class HepatitisMain {

	int numCategories;
	int numAtts;
	List<String> categoryNames;
	List<String> attNames;
	List<Instance> trainInstance = new ArrayList<Instance>();
	List<Instance> testInstance = new ArrayList<Instance>();

	LeafNode baseLine = null;


	/**
	 * Parses training and test data sets using based on the format given.
	 * After parsing the data, makes use of Instance object to store them.
	 * Finally keep the instnace object in arrays trainInstance and testInstance.
	 * @param fname
	 * @param fname2
	 */
	public void parse(String fname, String fname2){
		System.out.println("Reading data from file "+fname);
		//Put all the possible class types here.
		ArrayList<String> classTypes = new ArrayList<String>();
		ArrayList<String> attributeTypes = new ArrayList<String>();

		try{
			BufferedReader trainReader = new BufferedReader(new FileReader(new File(fname)));
			BufferedReader testReader =  new BufferedReader(new FileReader(new File(fname2)));

			try{
				//Read class names
				String classtype = trainReader.readLine();
				//Advance the reader on test too!
				String dummy = testReader.readLine();

				Scanner classScanner = new Scanner(classtype);

				//Add the classtypes on an array
				while(classScanner.hasNext()){
					classTypes.add(classScanner.next());
				}
				//Check the class names
				System.out.println("Class Names: "+classTypes.toString());

				//Read attributes
				String attTypes = trainReader.readLine();
				//Advance reader on test.dat
				String dummy2 = testReader.readLine();

				Scanner attScanner = new Scanner(attTypes);

				//Add the attributes in an array
				while(attScanner.hasNext()){
					attributeTypes.add(attScanner.next());
				}

				//Read data and make Instance objects out of them
				System.out.println("Reading Training Data!");
				//Training
				for(String train = trainReader.readLine(); train != null; train = trainReader.readLine()){
					Map<String, Boolean> trainMap = new HashMap<String, Boolean>();
					Scanner scTrain = new Scanner(train);
					String type = null;
					//Need to go thru attribute list to get attribute names
					//scan train string.

					//Get class type
					if(scTrain.hasNext()){
						type = scTrain.next();
						//						System.out.println("Type: "+type);
					}

					//Get other attributes
					for(int i = 0; scTrain.hasNext();i++){
						trainMap.put(attributeTypes.get(i), new Boolean(scTrain.next()));
					}

					trainInstance.add(new Instance(type, trainMap));

					//					System.out.println(trainMap.toString());
				}
				System.out.println("Training Data Read!");

				System.out.println("Reading Test Data!");
				//Test
				for(String test = testReader.readLine(); test != null; test = testReader.readLine()){
					Map<String, Boolean> testMap = new HashMap<String, Boolean>();
					Scanner scTest = new Scanner(test);
					String type = null;

					//Need to go thru attribute list to get attribute names
					//scan train string.

					//Get class type
					if(scTest.hasNext()){
						type = scTest.next();
						//						System.out.println("Type: "+type);
					}

					for(int i = 0; scTest.hasNext();i++){
						testMap.put(attributeTypes.get(i), new Boolean(scTest.next()));
					}

					testInstance.add(new Instance(type, testMap));
				}
				System.out.println("Test Data Read!");
				this.attNames = attributeTypes;
				this.categoryNames = classTypes;
				System.out.println("============================================================================================");
			}
			catch(IOException e){
				System.out.println("File is Empty!");
			}
		}
		catch(FileNotFoundException e){
			System.out.println("File Name Invalid!");
		}
		return;
	}

	public boolean pure(List<Instance> myList){
		String x = myList.get(0).getClassType();

		for(Instance i:myList){
			if(!x.equals(i.getClassType())){
				return false;
			}
		}
		return true;
	}



	public double calculateWeightedImputurity(List<Instance> myInstance, String attribute){
		//Purity x Impurity
		ArrayList<Instance> trueList = new ArrayList<Instance>();
		ArrayList<Instance> falseList = new ArrayList<Instance>();
		int countTrueA = 0;
		int countTrueB = 0;
		int countFalseA = 0;
		int countFalseB = 0;

		//Split instance list to true or false lists
		for(Instance i:myInstance){

			if(i.getFromMap(attribute)){
				String type = i.getClassType();

				if(type.equals(this.categoryNames.get(0))){
					countTrueA++;
				}
				else if(type.equals(this.categoryNames.get(1))){
					countTrueB++;
				}

				trueList.add(i);
			}
			else{
				String type = i.getClassType();

				if(type.equals(this.categoryNames.get(0))){
					countFalseA++;
				}
				else if(type.equals(this.categoryNames.get(1))){
					countFalseB++;
				}
				falseList.add(i);
			}
		}

		//Calculate the impurites of true and false nodes
		//P(A)P(B) = (m/(m+n)) x (n/(m+n))
		double trueImpurity = ((double)countTrueA/(double)trueList.size()) * ((double)countTrueB/(double)trueList.size()) ;
		double falseImpurity = ((double)countFalseA/(double)falseList.size()) * ((double)countFalseB/(double)falseList.size()) ;




		//calculate weighted Impurity
		//Sum(P(trueNode) x impurity of trueNode.......)

		double weightedImpur = (((double)trueList.size()/(double)myInstance.size()) * (double)trueImpurity)
				+ (((double)falseList.size()/(double)myInstance.size()*(double)falseImpurity));


		return weightedImpur;
	}


	public void runTest(Node root){
		double accuracy = 0;
		int liveHits = 0;
		int dieHits = 0;
		int liveMiss = 0;
		int dieMiss = 0;


		for(Instance i: testInstance){
			Node tempNode = root;
			//Loop till you get to a leaf
			while(tempNode.getLeft() != null && tempNode.getRight() != null){

				if(i.getFromMap(tempNode.getBestAttribute())){
					tempNode = tempNode.getLeft();
				}
				else{
					tempNode = tempNode.getRight();
				}
			}

			//tempNode Class type == testInstance ClassType
			if(tempNode != null && tempNode.getBestAttribute().equals(i.getClassType())){
				System.out.println("entered");
				if(categoryNames.get(0).equals(i.getClassType())){
					liveHits++;
				}
				if(categoryNames.get(1).equals(i.getClassType())){
					dieHits++;
				}
			}

			System.out.println("Instance Type: " + i.getClassType());
			System.out.print("My Decision Tree Value: ");
			tempNode.report("");
			System.out.println(" ");
		}
		accuracy = (double) (liveHits+dieHits) / testInstance.size();
		System.out.println("ClassType-"+categoryNames.get(0)+":"+liveHits);
		System.out.println("ClassType-"+categoryNames.get(1)+":"+dieHits);
		System.out.println("Test Data Size: "+testInstance.size());
		System.out.println("Overall Accuracy: "+ accuracy);

	}


	/**
	 * Finds the most occuring class type over all of the training list.
	 * @return
	 */
	private LeafNode findBaseLine(){
		Map<String,Integer> count = new HashMap<String,Integer>();

		//Count each class type in the given set of instances
		for(Instance i:trainInstance){
			String classType = i.getClassType();

			if(count.containsKey(classType)){
				int y = count.get(classType);
				count.put(classType,y+1);
			}
			else{
				count.put(classType,1);
			}
		}

		//Get the most occuring class type
		String best = null;
		int i = Integer.MIN_VALUE;

		for(Entry<String,Integer> s:count.entrySet()){
			//If classTypes are tied
			if(s.getValue() == i){
				double rng1 = Math.random();
				double rng2 = Math.random();

				if(rng1 < rng2){
					best = s.getKey();
					i = s.getValue();
				}
			}
			else if(s.getValue() > i){
				best = s.getKey();
				i = s.getValue();
			}
		}
		return new LeafNode(best, ((double)i/(double)trainInstance.size()));
	}




	public Node buildTree(List<Instance> myInstance, List<String> availableAttributes){
		/*if instance is empty return a leaf node containing the name and probability of the overall
		most probable class (ie, the baseline predictor)*/
		if(myInstance.isEmpty()){
			System.out.println("Instances is emtpy!");
			return this.baseLine;
		}

		/*instances are pure
		return a leaf node containing the name of the class of the instances
		in the node and probability 1 */
		if(pure(myInstance)){
			System.out.println("Instances are pure");
			String type = myInstance.get(0).getClassType();
			return new LeafNode(type,1.0);
		}


		/*attributes is empty
		 * return a leaf node containing the name and probability of the
		 * majority class of the instances in the node (choose randomly
		 * if classes are equal)
		 * */
		if(availableAttributes.isEmpty()){
			System.out.println("Attribute list is empty");
			Map<String,Integer> count = new HashMap<String,Integer>();

			//Count each class type in the given set of instances
			for(Instance i:myInstance){
				String classType = i.getClassType();
				if(count.containsKey(classType)){
					int y = count.get(classType);
					count.put(classType,y+1);
				}
				else{
					count.put(classType,1);
				}
			}

			//Get the most occuring class type
			String best = null;
			int i = Integer.MIN_VALUE;

			for(Entry<String,Integer> s:count.entrySet()){
				//If classTypes are tied
				System.out.println("Choosing best attribute");
				if(s.getValue() == i){
					System.out.println("Need to RNG!");
					double rng1 = Math.random();
					double rng2 = Math.random();

					if(rng1 > rng2){
						best = s.getKey();
						i = s.getValue();
					}
				}
				else if(s.getValue() > i){
					best = s.getKey();
					i = s.getValue();
				}
			}
			return new LeafNode(best, ((double)i/(double)myInstance.size()));
		}

		else{

			String bestAttr = null;
			double bestPur = Double.MAX_VALUE;
			ArrayList<Instance> bestTrueList = new ArrayList<Instance>();
			ArrayList<Instance> bestFalseList = new ArrayList<Instance>();

			//FIND THE BEST ATTRIBUTE!

			for(String attr: availableAttributes){
				ArrayList<Instance> trueList = new ArrayList<Instance>();
				ArrayList<Instance> falseList = new ArrayList<Instance>();

				//Split instance list to true or false lists
				for(Instance i:myInstance){

					if(i.getFromMap(attr)){
						trueList.add(i);
					}
					else{
						falseList.add(i);
					}
				}

				/*compute purity of each set.
				 *if weighted average purity of these sets is best so far
				 *bestAtt = this attribute
				 *bestInstsTrue = set of true instance
				 *bestInstsFalse = set of false instances
				 * */

				double tempPur = calculateWeightedImputurity(myInstance, attr);
				System.out.println("CurrentAttribute: "+attr + " Impurity: "+tempPur);

				if(bestPur > tempPur){
					bestAttr = attr;
					bestPur = tempPur;
					bestTrueList = trueList;
					bestFalseList = falseList;
				}
			}
			System.out.println("Best Purity: "+bestAttr);
			//build subtrees using the remaining attributes
			//		availableAttributes.remove(bestAttr);

			for(int i = 0 ; i < 5 ; ++i)
				System.out.println();

			ArrayList<String> temp = new ArrayList<String>(availableAttributes);
			temp.remove(bestAttr);

			Node left = buildTree(bestTrueList,temp);
			Node right = buildTree(bestFalseList,temp);
			Node bestNode =  new Node(bestAttr,left,right);

			return bestNode;
		}
	}




	/**
	 * Main Method caller for the main class
	 */
	public void start(){
		this.baseLine = findBaseLine();
		//build the tree
		Node myTree = buildTree(trainInstance, attNames);
		myTree.report("");
		System.out.println(" \n\n");
		runTest(myTree);
		System.out.print("BaseLine Prediction: ");
		baseLine.report("");
	}



	public static void main(String[] args){
		HepatitisMain hm = new HepatitisMain();
		hm.parse(args[0], args[1]);
		hm.start();
		System.exit(0);
	}

}
