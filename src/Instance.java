package src;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;



public class Instance {
	private String classType;
	private Map<String, Boolean> myMap = new HashMap<String, Boolean>();


	/**
	 * Represents an instance of data from the files. Contains
	 * Class Type and attributes.
	 * Uses a map to contain the data in the form of Map<String, Boolean>
	 * @param classType
	 * @param map
	 */
	public Instance(String classType, Map<String, Boolean> map ){
		this.classType = classType;
		myMap = map;
	}

	public String getClassType() {
		return classType;
	}

	public boolean getFromMap(String x){
		return this.myMap.get(x);
	}

	public Map<String, Boolean> getMap(){
		return this.myMap;
	}

	public String toString(){
		String x = null;
		for(Entry<String,Boolean> a:myMap.entrySet()){
			x = x+a+" ";
		}

		return "ClassType: "+this.classType +"\n" + x;
	}

}
