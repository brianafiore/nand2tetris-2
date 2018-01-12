package modules;

import java.util.Hashtable;

public class SymbolTable {
	
	Hashtable<String, Integer> table = new Hashtable<String, Integer>();
	
	public SymbolTable(){
		//add default values
		addEntry("SP",0);
		addEntry("LCL",1);
		addEntry("ARG",2);
		addEntry("THIS",3);
		addEntry("THAT",4);
		addEntry("SCREEN",16384);
		addEntry("KBD",24576);
		for(int i = 0; i < 16; i++){
			String key = "R"+String.valueOf(i);
			addEntry(key,i);
		}
	}
	
	public void addEntry(String key, int value) {
		this.table.put(key, value);
	}

	public boolean contains(String key) {
		if(this.table.containsKey(key)){
			return true;
		}
		return false;
	}

	public Integer getAddress(String key) {
		return this.table.get(key);
	}
	
}
