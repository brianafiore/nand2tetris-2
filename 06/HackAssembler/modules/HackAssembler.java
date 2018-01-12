package modules;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;

import customExceptions.InvalidArgsException;

public class HackAssembler {

	public static void main(String[] args) {
		//get file name from main call
		String fileName = getFileNameFromArgs(args);
		AsmFileReader reader = new AsmFileReader(fileName);
		reader.populateSymbolTable();
		ArrayList<String> binaryContents = reader.getContentsAsBinary();
		String newFileName = "My" + fileName.replace(".asm", ".hack");
		writeFile(newFileName, binaryContents);
	}
	
	//checks if the argument passed on to the program call is valid and returns
	//the name of the file to be read
	private static String getFileNameFromArgs(String[] args){
		String fileName = "";
		if(args.length == 0){
			throw new InvalidArgsException("No file to parse.");
		} else if(args.length>1){
			throw new InvalidArgsException("Can't handle more than 1 File.");
		}else if(!args[0].endsWith(".asm")){
			throw new InvalidArgsException("Not an asm file.");
		}else{
			fileName = args[0];
		}
		return fileName;
	}
	
	//writes a new file to the source folder
	private static void writeFile(String fileName, ArrayList<String> contents){
		try {
			String path = Paths.get("").toAbsolutePath().toString()+"\\src\\";
			BufferedWriter bw = new BufferedWriter(new FileWriter(path+fileName,false));
			for(int i = 0; i < contents.size(); i++){
				bw.append(contents.get(i)+"\n");
			}
			bw.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

}
