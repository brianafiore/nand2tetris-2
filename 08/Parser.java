import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Arithmetic commands:
 * 	[add, sub, neg, eq, gt, lt, and, or, not]
 * 	Syntax: command
 * 
 * Memory Access commands:
 * 	[push, pop]
 * 	Syntax: command segment index
 * 
 * Program flow commands:
 * 	[label, goto, if-goto]
 * 	Syntax: command symbol
 * 
 * Function calling commands:
 * 	function functionName numLocalVars
 * 	call functionName nArgs
 * 	return
 * 
 * Memory segments:
 * argument, local, static, constant, this, that, pointer, temp
 * */


public class Parser {
	
	private String arg1 = null; //first argument of current command
	private Integer arg2 = null; //second argument of current command
	private String commandType = null; //type of current command 
	private Boolean hasMoreCommands = true; //true if file has more commands to read
	private Integer commandIndex = 0; //pointer of reader
	
	private List<String> lines;
	
	public Parser(List<String> lines){
		this.lines = lines;
	}
	
	public Boolean hasMoreCommands(){
		return this.hasMoreCommands;
	}
	
	public void advance(){
		String currentCommand = lines.get(commandIndex);
		
		currentCommand = currentCommand.replaceAll("(//.*)", "").trim();
		
		Matcher arithmeticCmdMatcher = Pattern.compile("^(add|sub|neg|eq|gt|lt|and|or|not)$").matcher(currentCommand);
		Matcher memAccessCmdMatcher = Pattern.compile("^(pop|push)(?:\\s)(argument|local|static|constant|this|that|pointer|temp)(?:\\s)(\\d+)$").matcher(currentCommand);
		Matcher programFlowCmdMatcher = Pattern.compile("^(label|goto|if\\-goto)(?:\\s)(.+)$").matcher(currentCommand);
		Matcher functionCmdMatcher = Pattern.compile("^(function|call)(?:\\s)([A-Za-z_\\.\\:0-9]+)(?:\\s)(\\d+)$").matcher(currentCommand);
		Matcher returnCmdMatcher = Pattern.compile("^(return)$").matcher(currentCommand);
		
		//gets attributes from each line and stores into the class variables
		if(arithmeticCmdMatcher.matches()){
			commandType = "C_ARITHMETIC";
			arg1 = arithmeticCmdMatcher.group(1);
			
		} else if(memAccessCmdMatcher.matches()){
			//C_POP or C_PUSH
			commandType = "C_" + memAccessCmdMatcher.group(1).toUpperCase();
			arg1 = memAccessCmdMatcher.group(2);
			arg2 = Integer.parseInt(memAccessCmdMatcher.group(3));
			
		} else if(programFlowCmdMatcher.matches()){
			if(programFlowCmdMatcher.group(1).equals("if-goto")){
				commandType = "C_IF";
			}else{
				//C_LABEL, C_GOTO
				commandType = "C_" + programFlowCmdMatcher.group(1).toUpperCase();
			}
			arg1 = programFlowCmdMatcher.group(2);
			
		} else if(functionCmdMatcher.matches()){
			//C_FUNCTION, C_CALL
			commandType = "C_" + functionCmdMatcher.group(1).toUpperCase();
			arg1 = functionCmdMatcher.group(2);
			arg2 = Integer.parseInt(functionCmdMatcher.group(3));
			
		} else if(returnCmdMatcher.matches()){
			commandType = "C_RETURN";
			
		} else{
			commandType = "";
		}
		
		commandIndex++;
		
		if(commandIndex > lines.size()-1){
			this.hasMoreCommands = false;
		}
	}
	
	public String commandType(){
		return this.commandType;
	}
	
	public String arg1(){
		return this.arg1;
	}
	
	public Integer arg2(){
		return this.arg2;
	}
	
}
