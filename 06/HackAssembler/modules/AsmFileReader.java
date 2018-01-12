package modules;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class AsmFileReader {
	
	String fileName;
	ArrayList<String> fileContents;
	SymbolTable symbolTable = new SymbolTable();
	
	public AsmFileReader(String fileName){
		this.fileName = fileName;
		this.fileContents = parseFileContents(fileName);
	}
	
	public AsmFileReader(ArrayList<String> fileContents){
		this.fileContents = fileContents;
	}	

	private static ArrayList<String> parseFileContents(String fileName){
		ArrayList<String> fileContents = new ArrayList<String>();
		try {
			String path = Paths.get("").toAbsolutePath().toString()+"\\src\\";
			BufferedReader br = new BufferedReader(new FileReader(path+fileName));
			
			Pattern commentsAndEmptyLines = Pattern.compile("^//.*(?:\\r|$)|^\\s*");
			Pattern inlineComments = Pattern.compile("(\\S+)\\s*//.*");
			
			String nextLine;
			
			while((nextLine = br.readLine()) != null){
				nextLine = nextLine.replaceAll("\\s+", ""); //remove all whitespaces from file
				//if a line is a comment or an emptyline, don't add to the array
				Matcher matchCommentsAndNewLines = commentsAndEmptyLines.matcher(nextLine);
				if(!matchCommentsAndNewLines.matches()){
					//if a line has a comment in front of it, add only the command
					Matcher matchInlineComments = inlineComments.matcher(nextLine);
					if(matchInlineComments.matches()){
						nextLine = matchInlineComments.group(1);
					}
					fileContents.add(nextLine);
				}
			}
			br.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return fileContents;
	}

	public ArrayList<String> getFileContents() {
		return this.fileContents;
	}
	
	public void printContents(){
		if(!fileContents.equals(null) && fileContents.size() > 0){
			for(int i = 0; i < fileContents.size(); i++){
				System.out.println(fileContents.get(i));
			}
		}
	}
	
	//takes the contents of an array and returns the binary equivalent of the instructions
	public ArrayList<String> getContentsAsBinary() {
		ArrayList<String> binaryList = new ArrayList<String>();
		if(!fileContents.equals(null) && fileContents.size() > 0){
			Parser parser = new Parser(fileContents);
			Code code = new Code();
			int variableAddress = 16;
			while(parser.hasMoreCommands()){
				String binaryCode = "";
				parser.advance();
				//A instruction
				if(parser.commandType() == "A_COMMAND"){
					String symbol = parser.symbol();
					//if it is not a number check if variable was already defined
					if(!Pattern.matches("^\\d+", symbol)){
						if(!symbolTable.contains(symbol)){
							symbolTable.addEntry(symbol, variableAddress);
							variableAddress++;
						}
						//retrieve the address from the variable
						symbol = symbolTable.getAddress(symbol).toString();
					}
					binaryCode = decimalTo16BitBinary(symbol);
				//C instruction
				} else if(parser.commandType() == "C_COMMAND"){
					String first3Bits = "111";
					String compBits = arrayListToString(code.comp(parser.comp()));
					String destBits = arrayListToString(code.dest(parser.dest()));
					String jumpBits = arrayListToString(code.jump(parser.jump()));
					binaryCode = first3Bits+compBits+destBits+jumpBits;
				}
				if(parser.commandType() != "L_COMMAND"){
					binaryList.add(binaryCode);
				}
				
			}
		}
		return binaryList;
	}
	
	//converts a decimal number to its 16 bit binary equivalent
	private String decimalTo16BitBinary(String decimal){
		String binary = "";
		int decimalInt = stringToInt(decimal);
		for(int i = 0; i < 16; i++){
			int bit = decimalInt%2;
			if(bit == 1){
				binary += "1";
			} else {
				binary += "0";
			}
			decimalInt = (decimalInt-bit)/2;
		}
		binary = new StringBuilder(binary).reverse().toString();
		return binary;
	}
	
	//converts a number represented as a string to an integer
	private int stringToInt(String number){
		int num = 0;
		if(Pattern.matches("^[0-9]+$", number)){
			char[] numberArray = number.toCharArray();
			int n = number.length()-1;
			for(int i = n; i >= 0; i--){
				int numberAsInt = ((int)numberArray[i])-48;
				num += numberAsInt*Math.pow(10, n-i);
			}
		}
		return num;
	}
	
	private String arrayListToString(ArrayList<Integer> list){
		String value = "";
		for(int i = 0; i < list.size(); i++){
			value += list.get(i).toString();
		}
		return value;
	}

	public void populateSymbolTable() {
		if(!fileContents.equals(null) && fileContents.size() > 0){
			Parser parser = new Parser(fileContents);
			int instructionCount = 0;
			while(parser.hasMoreCommands()){
				parser.advance();
				if(parser.commandType() == "L_COMMAND"){
					symbolTable.addEntry(parser.symbol(), instructionCount);
//					System.out.println("Symbol "+ parser.symbol()+ " mapped to: " + instructionCount);
				}else {
					instructionCount++;
				}
			}
		}	
	}
	
	public SymbolTable getSymbolTable(){
		return this.symbolTable;
	}

}
