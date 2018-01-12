package modules;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import customExceptions.ParserException;

public class Parser {
	
	private ArrayList<String> fileContents = new ArrayList<String>();
	
	private String currentCommand;
	private String comp,dest,jump,symbol; //separate parts of currentCommand
	private String commandType; //type of current command
	private int nextCommandIndex = 0;
	
	private Pattern compPattern = Pattern.compile("^([AMD]|AM|MD|AD|AMD)=([01]$|[\\-\\!][AMD1]$|[AMD]$|[AMD][\\+\\-\\|\\&][AMD]$|[AMD][\\+\\-]1$)");
	private Pattern jumpPattern = Pattern.compile("^([AMD0]);(J[A-Z]{2})");
	private Pattern addressPattern = Pattern.compile("^@([\\S]+)");
	private Pattern labelPattern = Pattern.compile("^\\(([\\S]+)\\)");

	
	public Parser(ArrayList<String> fileContents){
		this.fileContents = fileContents;
	}
	
	public boolean hasMoreCommands(){
		if(nextCommandIndex < fileContents.size()){
			return true;
		}
		return false;
	}
	
	public void advance(){
		if(hasMoreCommands()){
			currentCommand = fileContents.get(nextCommandIndex);
			nextCommandIndex++;
			
			updateParameters();
		}
	}
	
	private void updateParameters() {
		
		this.symbol = "null";
		this.comp = "0";
		this.dest = "null";
		this.jump = "null";
		this.commandType = "null";
		
		Matcher labelMatcher, addressMatcher, compMatcher, jumpMatcher;
		labelMatcher = labelPattern.matcher(currentCommand);
		addressMatcher = addressPattern.matcher(currentCommand);
		compMatcher = compPattern.matcher(currentCommand);
		jumpMatcher = jumpPattern.matcher(currentCommand);
		
		if(labelMatcher.find()){
			this.commandType = "L_COMMAND";
			this.symbol = labelMatcher.group(1);
		} else if(addressMatcher.find()) {
			this.commandType = "A_COMMAND";
			this.symbol = addressMatcher.group(1);
		}else if(compMatcher.find()){
			this.commandType = "C_COMMAND";
			String d = compMatcher.group(1);
			if(!d.equals("0")){
				this.dest = d;
			}
			this.comp = compMatcher.group(2);
		}else if(jumpMatcher.find()){
			this.commandType = "C_COMMAND";
			this.comp = jumpMatcher.group(1);
			this.jump = jumpMatcher.group(2);
		}else {
			throw new ParserException("Invalid instruction");
		}
		
	}

	public String commandType(){
		return this.commandType;
	}
	
	public ArrayList<String> getFileContents(){
		return this.fileContents;
	}
	
	public String getCurrentCommand(){
		return this.currentCommand;
	}

	public String symbol() {
		return this.symbol;
	}

	public String dest() {
		return this.dest;
	}

	public String jump() {
		return this.jump;
	}

	public String comp() {
		return this.comp;
	}
}
