import java.util.Hashtable;

/**
 * This module provides services for creating and using a symbol table. 
 * The symbol table gives each symbol a running number (index) within the scope.
 * The index starts at 0, increments by 1 each time an identifier is added to the table,
 * and resets to 0 when starting a new scope.
 * 
 * The following kinds of identifiers may appear in the symbol table:
 * Static: Scope: class.
 * Field: Scope: class.
 * Argument: Scope: subroutine (method/function/constructor).
 * Var: Scope: subroutine (method/function/constructor).
*/


public class SymbolTable {
	
	//kinds of identifiers
	private static final String STATIC = "static";
	private static final String FIELD = "field";
	private static final String ARG = "argument";
	private static final String LOCAL = "local";
	
	//table column indexes
	private static final int TYPE = 0;
	private static final int KIND = 1;
	private static final int INDEX = 2;
	
	private Hashtable<String, Object[]> classVars;
	private Hashtable<String, Object[]> subroutineVars;
	
	private int staticCount;
	private int fieldCount;
	private int argCount;
	private int varDecCount;
	
	public SymbolTable(){
		this.classVars = new Hashtable<String,Object[]>();
		this.subroutineVars = new Hashtable<String,Object[]>();
		staticCount = 0;
		fieldCount = 0;
		argCount = 0;
		varDecCount = 0;
	}
	
	public void startSubroutine(){
		subroutineVars.clear(); //clear hashtable
		argCount = 0;
		varDecCount = 0;
	}
	
	public void define(String name, String type, String kind){
		Object[] values = new Object[3];
		values[TYPE] = type;
		values[KIND] = kind;
		switch(kind){
		case STATIC:
			values[INDEX] = staticCount;
			classVars.put(name, values);
			staticCount++;
			break;
		case FIELD:
			values[INDEX] = fieldCount;
			classVars.put(name, values);
			fieldCount++;
			break;
		case ARG:
			values[INDEX] = argCount;
			subroutineVars.put(name, values);
			argCount++;
			break;
		case LOCAL:
			values[INDEX] = varDecCount;
			subroutineVars.put(name, values);
			varDecCount++;
			break;
		}
	}
	
	public int varCount(String kind){
		int count = 0;
		switch(kind){
		case STATIC:
			count = staticCount;
			break;
		case FIELD:
			count = fieldCount;
			break;
		case ARG:
			count = argCount;
			break;
		case LOCAL:
			count = varDecCount;
			break;
		}
		return count;
	}
	
	public String typeOf(String name){
		String type = "";
		if(classVars.containsKey(name)){
			type = (String) classVars.get(name)[TYPE];
		} else{
			type = (String) subroutineVars.get(name)[TYPE];
		}
		return type;
	}
	
	public String kindOf(String name){
		String kind = "none";
		if(subroutineVars.containsKey(name)){
			kind = (String) subroutineVars.get(name)[KIND];
		} else if(classVars.containsKey(name)){
			kind = (String) classVars.get(name)[KIND];
		} else {
			System.out.println("Variable not defined");
		}
		return kind;
	}
	
	public int indexOf(String name){
		int index = 0;
		if(classVars.containsKey(name)){
			index = (int) classVars.get(name)[INDEX];
		} else{
			index = (int) subroutineVars.get(name)[INDEX];
		}
		return index;
	}
	
}
