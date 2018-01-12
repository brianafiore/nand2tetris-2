import java.io.BufferedWriter;
import java.io.IOException;
import java.util.regex.Pattern;

/**
 * Gets its input from a JackTokenizer and emits its parsed
 * structure into an output file/stream.
*/


public class CompilationEngine {
	
	private static final String TAG = "Compilation Engine";
	private static final String TAB = "\t";
	private static final String NEWLINE = "\n";
	
	//Token types
	private static final int KEYWORD = 0;
	private static final int SYMBOL = 1;
	private static final int IDENTIFIER = 2;
	private static final int INT_CONST = 3;
	private static final int STRING_CONST = 4;
	
	BufferedWriter writer;
	String program;
	JackTokenizer tokenizer;
	StringBuilder builder;
	StringBuilder indent;
	
	public CompilationEngine(String program, BufferedWriter writer){
		this.program = program;
		this.writer = writer;
		this.tokenizer = new JackTokenizer(program);
		this.builder = new StringBuilder();
		this.indent = new StringBuilder();
	}
	
	public void compileClass(){
		//start
		start("class");
		tokenizer.advance();
		//class header
		writeToken(KEYWORD); //class
		writeToken(IDENTIFIER); //Main
		writeToken(SYMBOL); //{
		
		//class body
		while(tokenizer.tokenType() == KEYWORD){
			if(tokenizer.keyWord().equals("static")||tokenizer.keyWord().equals("field")){
				compileClassVarDec();
			} else {
				compileSubroutine();
			}
		}
		
		//end
		writeToken(SYMBOL); //}
		end("class");
		
		try{
			writer.write(builder.toString());
			writer.flush();
			writer.close();
		} catch (IOException e){
			System.out.println(TAG+"/ERROR WRITING STREAM");
		}
		
	}
	
	public void compileClassVarDec(){
		//start
		start("classVarDec");
		
		//class var dec body
		writeToken(KEYWORD); //static|field
		
		//type
		if(tokenizer.tokenType() == KEYWORD){
			//boolean|int|char
			writeToken(KEYWORD);
		} else{
			//className
			writeToken(IDENTIFIER);
		}
		
		//var name
		writeToken(IDENTIFIER);
		
		while(tokenizer.symbol() == ','){
			//variable1, variable2,...
			writeToken(SYMBOL); //,
			writeToken(IDENTIFIER); //varname
		}
		
		writeToken(SYMBOL); //;
		
		//end
		end("classVarDec");
	}
	
	public void compileSubroutine(){
		//start
		start("subroutineDec");
		
		//constructor|function|method
		writeToken(KEYWORD);
		
		//return type
		if(tokenizer.tokenType() == KEYWORD){
			//boolean|int|char|void
			writeToken(KEYWORD);
		} else{
			//className
			writeToken(IDENTIFIER);
		}
		
		//subroutineName
		writeToken(IDENTIFIER);
		writeToken(SYMBOL); //(
		
		compileParameterList();
		
		writeToken(SYMBOL); //)
		
		//subroutinedec body start
		start("subroutineBody");
		
		writeToken(SYMBOL); //{
		
		//vardec
		while(tokenizer.keyWord().equals("var")){
			compileVarDec();
		}
		
		compileStatements();
		writeToken(SYMBOL); //}
		
		//subroutinebody end
		end("subroutineBody");
		
		//end
		end("subroutineDec");
		
	}
	
	public void compileParameterList(){
		//parameter list start
		start("parameterList");
		
		while(tokenizer.tokenType() != SYMBOL){
			if(tokenizer.tokenType() == KEYWORD){
				//boolean|int|char
				writeToken(KEYWORD);
			} else{
				//className
				writeToken(IDENTIFIER);
			}
			//var name
			writeToken(IDENTIFIER);
			if(tokenizer.symbol()== ','){
				writeToken(SYMBOL); //,
			}
		}
		
		end("parameterList");
	}
	
	public void compileVarDec(){
		//var dec start
		start("varDec");
		
		writeToken(KEYWORD); //var
		
		//type
		if(tokenizer.tokenType() == KEYWORD){
			//boolean|int|char
			writeToken(KEYWORD);
		} else{
			//className
			writeToken(IDENTIFIER);
		}
		
		//var name
		writeToken(IDENTIFIER);
		
		while(tokenizer.symbol() == ','){
			//variable1, variable2,...
			writeToken(SYMBOL); //,
			writeToken(IDENTIFIER); //varname
		}
		
		writeToken(SYMBOL); //;
		
		//vardec end
		end("varDec");
		
	}
	
	public void compileStatements(){
		//statements start
		start("statements");
		
		while(tokenizer.tokenType() != SYMBOL){
			switch(tokenizer.keyWord()){
			case "let":
				compileLet();
				break;
			case "if":
				compileIf();
				break;
			case "while":
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
		end("statements");
	}
	
	public void compileDo(){
		//do start
		start("doStatement");
		
		
		writeToken(KEYWORD); //do
		//subroutine call
		
		writeToken(IDENTIFIER); //name
		
		if(tokenizer.symbol() == '.'){
			writeToken(SYMBOL); //.
			writeToken(IDENTIFIER); //subroutineName	
		}
		
		writeToken(SYMBOL); //(
		compileExpressionList();
		writeToken(SYMBOL); //)
				
		writeToken(SYMBOL); //;
		
		//do statement end
		end("doStatement");
		
	}
	
	public void compileLet(){
		//let start
		start("letStatement");
		
		writeToken(KEYWORD); //let
		
		writeToken(IDENTIFIER); //varname
		if(tokenizer.symbol() == '['){
			writeToken(SYMBOL); //[
			compileExpression();
			writeToken(SYMBOL); //]
		}
		
		writeToken(SYMBOL); //=
		compileExpression();
				
		writeToken(SYMBOL); //;
		
		//let statement end
		end("letStatement");
		
	}
	
	public void compileWhile(){
		//while start
		start("whileStatement");
		
		writeToken(KEYWORD); //while
		
		writeToken(SYMBOL); //(
		compileExpression();
		writeToken(SYMBOL); //)
		writeToken(SYMBOL); //{
		compileStatements();
		writeToken(SYMBOL); //}
		
		end("whileStatement");
	}
	
	public void compileReturn(){
		start("returnStatement");
		
		writeToken(KEYWORD); //return
		
		if(tokenizer.tokenType() != SYMBOL){
			compileExpression();
		}
		writeToken(SYMBOL); //;
		
		end("returnStatement");
	}
	
	public void compileIf(){
		start("ifStatement");
		
		writeToken(KEYWORD); //if
		writeToken(SYMBOL); //(
		compileExpression();
		writeToken(SYMBOL); //)
		writeToken(SYMBOL); //{
		compileStatements();
		writeToken(SYMBOL); //}
		
		if(tokenizer.keyWord().equals("else")){
			writeToken(KEYWORD);//else
			writeToken(SYMBOL); //{
			compileStatements();
			writeToken(SYMBOL); //}
		}
		
		end("ifStatement");
	}
	
	public void compileExpression(){
		start("expression");
		
		compileTerm();
		
		while(Pattern.matches("(?:[\\+\\-\\=\\*\\/\\&\\|\\<\\>\\~])", String.valueOf(tokenizer.symbol()))){
			writeToken(SYMBOL); //op
			compileTerm();
		}
		
		end("expression");
	}
	
	public void compileTerm(){
		start("term");
		
		switch(tokenizer.tokenType()){
		case INT_CONST:
			writeToken(INT_CONST);
			break;
		case STRING_CONST:
			writeToken(STRING_CONST);
			break;
		case KEYWORD:
			//true|false|null|this
			writeToken(KEYWORD);
			break;
		case SYMBOL:
			if(tokenizer.symbol() == '('){
				writeToken(SYMBOL); //(
				compileExpression();
				writeToken(SYMBOL); //)
			} else if(tokenizer.symbol() == '-' || tokenizer.symbol() == '~'){
				writeToken(SYMBOL); //-|~
				compileTerm();
			}
			break;
		case IDENTIFIER:
			writeToken(IDENTIFIER); //name
			
			//subroutine call
			if(tokenizer.symbol() == '.'){
				writeToken(SYMBOL); //.
				writeToken(IDENTIFIER); //subroutineName
				writeToken(SYMBOL); //(
				compileExpressionList();
				writeToken(SYMBOL); //)
			} else if(tokenizer.symbol() == '('){
				writeToken(SYMBOL); //(
				compileExpressionList();
				writeToken(SYMBOL); //)
			} else if (tokenizer.symbol() == '['){ //varname[expression]
				writeToken(SYMBOL); //[
				compileExpression();
				writeToken(SYMBOL); //]
			}
			break;
		}
		
		end("term");
		
	}
	
	public void compileExpressionList(){
		start("expressionList");
		
		while(tokenizer.symbol() != ')'){
			compileExpression();
			while(tokenizer.symbol() == ','){
				writeToken(SYMBOL); //,
				compileExpression();
			}
		}
		
		end("expressionList");
		
	}
	
	private void writeToken(int type){
		String token = "";
		switch(type){
		case KEYWORD:
			token = "<keyword> " + tokenizer.keyWord() + " </keyword>";
			builder.append(token);
			break;
		case SYMBOL:
			char symbol = tokenizer.symbol();
			String newSymbol;
			if(symbol == '<'){
				newSymbol = "&lt;";
			} else if(symbol == '>'){
				newSymbol = "&gt;";
			} else if(symbol == '"'){
				newSymbol = "&quot;";
			} else if(symbol == '&'){
				newSymbol = "&amp;";
			} else{
				newSymbol = String.valueOf(symbol);
			}
			token = "<symbol> " + newSymbol + " </symbol>";
			builder.append(token);
			break;
		case IDENTIFIER:
			token = "<identifier> " + tokenizer.identifier() + " </identifier>";
			builder.append(token);
			break;
		case INT_CONST:
			token = "<integerConstant> " + tokenizer.intVal() + " </integerConstant>";
			builder.append(token);
			break;
		case STRING_CONST:
			token = "<stringConstant> " + tokenizer.stringVal() + " </stringConstant>";
			builder.append(token);
			break;
		}
//		System.out.println(token);
		builder.append(NEWLINE+indent);
		tokenizer.advance();
	}
	
	private void start(String name){
		builder.append("<" + name + ">");
		indent.append(TAB);
		builder.append(NEWLINE+indent);
	}
	
	private void end(String name){
		indent.deleteCharAt(indent.length()-1); //unindent
		builder.deleteCharAt(builder.length()-1); //delete last tab
		builder.append("</" + name + ">");
		builder.append(NEWLINE+indent);
	}
}
