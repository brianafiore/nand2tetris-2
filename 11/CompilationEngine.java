import java.util.regex.Pattern;

/**
 * Gets its input from a JackTokenizer and emits its parsed
 * structure into an output file/stream.
*/


public class CompilationEngine {
	
	private static final String IF_LABEL = "IF_TRUE";
	private static final String ELSE_LABEL = "IF_FALSE";
	private static final String ENDIF_LABEL = "IF_END";
	private static final String WHILE_LABEL = "WHILE_EXP";
	private static final String ENDWHILE_LABEL = "WHILE_END";
	
	//Token types
	private static final int KEYWORD = 0;
	private static final int SYMBOL = 1;
	private static final int IDENTIFIER = 2;
	private static final int INT_CONST = 3;
	private static final int STRING_CONST = 4;
	
	VMWriter writer;
	String program;
	JackTokenizer tokenizer;
	StringBuilder builder;
	StringBuilder indent;
	SymbolTable symbolTable;
	String className;
	int ifCount;
	int whileCount;
	
	public CompilationEngine(String program, VMWriter writer){
		this.program = program;
		this.writer = writer;
		this.tokenizer = new JackTokenizer(program);
		this.builder = new StringBuilder();
		this.indent = new StringBuilder();
		this.symbolTable = new SymbolTable();
		this.ifCount = 0;
		this.whileCount = 0;
	}
	
	public void compileClass(){
		//start
		tokenizer.advance();
		
		//class header
		tokenizer.advance(); //class
		className = tokenizer.keyWord();
		tokenizer.advance(); //Name
		tokenizer.advance(); //{
		
		//class body
		while(tokenizer.tokenType() == KEYWORD){
			if(tokenizer.keyWord().equals("static")||tokenizer.keyWord().equals("field")){
				compileClassVarDec();
			} else {
				compileSubroutine();
			}
		}
		
		//end
		tokenizer.advance(); //}
		
	}
	
	public void compileClassVarDec(){
		//start
		
		String kind = tokenizer.keyWord();
		//class var dec body
		tokenizer.advance(); //static|field
		
		String type = "";
		//type
		if(tokenizer.tokenType() == KEYWORD){
			//boolean|int|char
			type = tokenizer.keyWord();
		} else{
			//className
			type = tokenizer.identifier();
		}
		tokenizer.advance();
		
		//var name
		String name = tokenizer.identifier();
		
		symbolTable.define(name, type, kind);
		tokenizer.advance();
		
		while(tokenizer.symbol() == ','){
			//variable1, variable2,...
			tokenizer.advance(); //,
			
			name = tokenizer.identifier();
			symbolTable.define(name, type, kind);
			
			tokenizer.advance(); //varname
//			writeIdentifier(name);
		}
		
		tokenizer.advance(); //;
		
		//end
	}
	
	public void compileSubroutine(){
		//start
		ifCount = -1;
		whileCount = -1;
		
		//reset subroutine scope symbol table
		symbolTable.startSubroutine(); 
		
		//constructor|function|method
		String type = tokenizer.keyWord();
		tokenizer.advance();
		
		//return type
		if(tokenizer.tokenType() == KEYWORD){
			//boolean|int|char|void
			
		} else{
			//className
			
		}
		tokenizer.advance();
		
		//subroutineName
		String subroutineName = tokenizer.identifier();
		tokenizer.advance(); //identifier
		tokenizer.advance(); //(
		
		compileParameterList(type.equals("method"));
		
		tokenizer.advance(); //)
		
		//subroutinedec body start
		tokenizer.advance(); //{
		
		//vardec
		while(tokenizer.keyWord().equals("var")){
			compileVarDec();
		}
		
		if(type.equals("function")){
			writer.writeFunction(className+"."+subroutineName, symbolTable.varCount("local"));
			
		} else if (type.equals("constructor")){
			writer.writeFunction(className+"."+subroutineName, 0);
			writer.writePush("constant", symbolTable.varCount("field"));
			writer.writeCall("Memory.alloc", 1);
			writer.writepop("pointer", 0);
			
		} else if (type.equals("method")){
			writer.writeFunction(className+"."+subroutineName, symbolTable.varCount("local"));
			writer.writePush("argument", 0);
			writer.writepop("pointer", 0);
		}
		
		compileStatements();
		tokenizer.advance(); //}
		
		//subroutinebody end
		//end
	}
	
	public void compileParameterList(boolean isMethod){
		//parameter list start
		String kind = "argument";
		String type = className;
		if(isMethod){
			symbolTable.define("this", type, kind);
		}
		while(tokenizer.tokenType() != SYMBOL){
			if(tokenizer.tokenType() == KEYWORD){
				//boolean|int|char
				type = tokenizer.keyWord();
			} else{
				//className
				type = tokenizer.identifier();
			}
			tokenizer.advance();
			//var name
			String name = tokenizer.identifier();
			
			symbolTable.define(name, type, kind);
			tokenizer.advance(); //write identifier
			
			if(tokenizer.symbol()== ','){
				tokenizer.advance(); //,
			}
		}
		//end parameter list
	}
	
	public void compileVarDec(){
		//var dec start
		String kind = "local";
		tokenizer.advance(); //var
		
		String type = "";
		//type
		if(tokenizer.tokenType() == KEYWORD){
			//boolean|int|char
			type = tokenizer.keyWord();
		} else{
			//className
			type = tokenizer.identifier();
		}
		tokenizer.advance();
		
		//var name
		String name = tokenizer.identifier();
		
		symbolTable.define(name, type, kind);
		tokenizer.advance(); //identifier
		
		
		while(tokenizer.symbol() == ','){
			//variable1, variable2,...
			tokenizer.advance(); //,
			
			name = tokenizer.identifier();
			symbolTable.define(name, type, kind);
			
			tokenizer.advance();//varname
		}
		
		tokenizer.advance(); //;
		
		//vardec end
	}
	
	public void compileStatements(){
		//statements start
		
		while(tokenizer.tokenType() != SYMBOL){
			switch(tokenizer.keyWord()){
			case "let":
				compileLet();
				break;
			case "if":
				ifCount++;
				compileIf();
				break;
			case "while":
				whileCount++;
				compileWhile();
				break;
			case "do":
				compileDo();
				break;
			case "return":
				compileReturn();
				break;
			}
		}
		
		//statements end
	}
	
	public void compileDo(){
		//do start
		
		tokenizer.advance(); //do
		//subroutine call
		String name = tokenizer.identifier(); //class|obj|method name
		tokenizer.advance();
		int nArgs = 0;
		
		if(name.matches("[A-Z].*")){
			//function call
			name+=tokenizer.symbol(); //.
			tokenizer.advance(); 
			name+=tokenizer.identifier(); //function name
			tokenizer.advance();
			
		} else {
			//method call
			nArgs++;
			
			if(tokenizer.symbol()=='.'){
				//from object
				writer.writePush(symbolTable.kindOf(name), symbolTable.indexOf(name));
				name = symbolTable.typeOf(name);
				name += tokenizer.symbol();
				tokenizer.advance(); //.
				name += tokenizer.identifier();
				tokenizer.advance(); //method name
				
			} else {
				//from class
				writer.writePush("pointer", 0); //this
				name = className+'.'+name;	
			}
		}
		
		tokenizer.advance(); //(
		nArgs += compileExpressionList();
		tokenizer.advance(); //)
		writer.writeCall(name, nArgs);
				
		tokenizer.advance(); //;
		
		//do statement end
		writer.writepop("temp", 0); //remove unnecessary value from the stack
		
	}
	
	public void compileLet(){
		//let start
		
		tokenizer.advance(); //let
		
		String varName = tokenizer.identifier(); //varname
		tokenizer.advance();
		
		if(tokenizer.symbol() == '['){
			//array access
			tokenizer.advance(); //[
			compileExpression();
			tokenizer.advance(); //]
			
			writer.writePush(symbolTable.kindOf(varName), symbolTable.indexOf(varName)); //push var address onto the stack
			writer.writeArithmetic("add"); //offset address of array on the stack
			
			tokenizer.advance(); //=
			compileExpression();
			tokenizer.advance(); //;
			
			writer.writepop("temp", 0); //store value of expression on temp segment
			writer.writepop("pointer", 1); //anchor array address to 'that' segment
			writer.writePush("temp", 0); //get value from expression
			writer.writepop("that", 0); //put value of expression onto array address
			
		} else {
			
			tokenizer.advance(); //=
			compileExpression();
			tokenizer.advance(); //;
			
			writer.writepop(symbolTable.kindOf(varName), symbolTable.indexOf(varName));
		}
		
		//let statement end
		
	}
	
	public void compileWhile(){
		int index = whileCount;
		tokenizer.advance(); //while
		writer.writeLabel(WHILE_LABEL + index);
		tokenizer.advance(); //(
		compileExpression();
		writer.writeArithmetic("not");
		tokenizer.advance(); //)
		writer.writeIf(ENDWHILE_LABEL + index);
		tokenizer.advance(); //{
		compileStatements();
		tokenizer.advance(); //}
		writer.writeGoto(WHILE_LABEL + index);
		writer.writeLabel(ENDWHILE_LABEL + index);
		
	}
	
	public void compileReturn(){
		
		tokenizer.advance(); //return
		
		if(tokenizer.tokenType() != SYMBOL){
			compileExpression();
		} else {
			writer.writePush("constant", 0);
		}
		
		writer.writeReturn();
		tokenizer.advance(); //;
		
	}
	
	public void compileIf(){
		
		int index = ifCount;
		tokenizer.advance(); //if
		tokenizer.advance(); //(
		compileExpression();
		tokenizer.advance(); //)
		writer.writeIf(IF_LABEL + index);
		writer.writeGoto(ELSE_LABEL + index);
		tokenizer.advance(); //{
		writer.writeLabel(IF_LABEL + index);
		compileStatements();
		tokenizer.advance(); //}
		
		if(tokenizer.keyWord().equals("else")){
			writer.writeGoto(ENDIF_LABEL + index);
			writer.writeLabel(ELSE_LABEL + index);
			tokenizer.advance(); //else
			tokenizer.advance(); //{
			compileStatements();
			tokenizer.advance(); //}
			writer.writeLabel(ENDIF_LABEL + index);
		} else {
			writer.writeLabel(ELSE_LABEL + index);
		}
		
	}
	
	public void compileExpression(){
		
		compileTerm();
		String operation = ""; 
		while(Pattern.matches("(?:[\\+\\-\\=\\*\\/\\&\\|\\<\\>\\~])", String.valueOf(tokenizer.symbol()))){
			switch(tokenizer.symbol()){
			case '+':
				operation="add";
				break;
			case '-':
				operation="sub";
				break;
			case '=':
				operation="eq";
				break;
			case '*':
				operation="Math.multiply";
				break;
			case '/':
				operation="Math.divide";
				break;
			case '&':
				operation="and";
				break;
			case '|':
				operation="or";
				break;
			case '<':
				operation="lt";
				break;
			case '>':
				operation="gt";
				break;
			case '~':
				operation="not";
				break;
			}
			
			tokenizer.advance(); //op
			compileTerm();
			if(operation.equals("Math.multiply") || operation.equals("Math.divide")){
				writer.writeCall(operation, 2);
			}else{
				writer.writeArithmetic(operation);
			}
			
		}
		
	}
	
	public void compileTerm(){
		
		switch(tokenizer.tokenType()){
		case INT_CONST:
			writer.writePush("constant", tokenizer.intVal());
			tokenizer.advance(); //int
			break;
		case STRING_CONST:
			writeString(tokenizer.stringVal());
			tokenizer.advance();
			break;
		case KEYWORD:
			//true|false|null|this
			switch(tokenizer.keyWord()){
			case "true":
				writer.writePush("constant", 0);
				writer.writeArithmetic("not");
				break;
			case "false":
				writer.writePush("constant", 0);
				break;
			case "null":
				writer.writePush("constant", 0);
				break;
			case "this":
				writer.writePush("pointer", 0);
				break;
			}
			tokenizer.advance();
			break;
		case SYMBOL:
			if(tokenizer.symbol() == '('){
				tokenizer.advance(); //(
				compileExpression();
				tokenizer.advance(); //)
			} else if(tokenizer.symbol() == '-'){
				tokenizer.advance(); //-
				compileTerm();
				writer.writeArithmetic("neg");
			} else if(tokenizer.symbol() == '~'){
				tokenizer.advance();
				compileTerm();
				writer.writeArithmetic("not");
			}
			break;
		case IDENTIFIER:
			String name = tokenizer.identifier(); //name (class|sub|var)
			tokenizer.advance();
			
			//subroutine call
			if(tokenizer.symbol() == '.'){
				if(name.matches("[a-z].*")){
					//is object
					writer.writePush(symbolTable.kindOf(name), symbolTable.indexOf(name));
				}
				name += tokenizer.symbol(); //.
				tokenizer.advance();
				name += tokenizer.identifier(); //subroutineName
				tokenizer.advance();
				tokenizer.advance(); //(
				int nArgs = compileExpressionList();
				tokenizer.advance(); //)
				writer.writeCall(name, nArgs);
			} else if(tokenizer.symbol() == '('){
				tokenizer.advance(); //(
				int nArgs = compileExpressionList();
				tokenizer.advance(); //)
				writer.writeCall(name, nArgs);
			} else if (tokenizer.symbol() == '['){ 
				//varname[expression]
				tokenizer.advance(); //[
				compileExpression();
				tokenizer.advance(); //]
				writer.writePush(symbolTable.kindOf(name), symbolTable.indexOf(name)); //push var address onto the stack
				writer.writeArithmetic("add"); //offset address of array on the stack
				writer.writepop("pointer", 1); //anchor array address to that segment
				writer.writePush("that", 0); //put value of expression onto array address
			}else{
				//varname
				writer.writePush(symbolTable.kindOf(name), symbolTable.indexOf(name));
			}
			
			
			break;
		}
		
		
	}
	
	public int compileExpressionList(){
		int nArgs = 0;
		while(tokenizer.symbol() != ')'){
			compileExpression();
			while(tokenizer.symbol() == ','){
				tokenizer.advance(); //,
				compileExpression();
				nArgs++;
			}
			nArgs++;			
		}
		
		return nArgs;
	}
	
	private void writeString(String str){
		int len = str.length();
		writer.writePush("constant", len-2);
		writer.writeCall("String.new", 1);
		for(int i = 1; i < len-1; i++){
			writer.writePush("constant", (int) str.charAt(i));
			writer.writeCall("String.appendChar", 2);
		}
		
	}

}
