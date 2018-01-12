import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;

public class VMTranslator {
	
	public static void main(String[] args) throws IOException {
		
		//get parameter
		String arg = args[0];
		Path path = Paths.get(arg);
		List<String> lines = new ArrayList<String>();
		
		File file = path.toFile();
		
		//get path and fileNames;
		if(Files.isDirectory(path)){
			String dir = path.toString();
			Path filePath = Paths.get(dir, path.getFileName()+".asm");
			
			//delete old file and create new
			Files.deleteIfExists(filePath);
			if(Files.notExists(filePath, LinkOption.NOFOLLOW_LINKS)){
				Files.createFile(filePath);
			}
			
			//create a new code writer
			CodeWriter writer = new CodeWriter(path, filePath);
			writer.writeInit();
			writer.close();
			
			//parse each file in directory
			DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.vm");
			for(Path entry : stream){
				lines = Files.readAllLines(entry, StandardCharsets.UTF_8);
				writer = new CodeWriter(entry, filePath);
				parseVmFile(lines, writer);
				writer.close();
			}
		} else {
			String dir = path.getParent().toString();
			String fileName = file.getName().substring(0, file.getName().lastIndexOf('.'));
			Path filePath = Paths.get(dir, fileName +".asm");
			
			//delete old file and create new
			Files.deleteIfExists(filePath);
			if(Files.notExists(filePath, LinkOption.NOFOLLOW_LINKS)){
				Files.createFile(filePath);
			}
			
			//parse a single file
			CodeWriter writer = new CodeWriter(path, filePath);
			lines = Files.readAllLines(path, StandardCharsets.UTF_8);
			parseVmFile(lines, writer);
			writer.close();
			
		}
		
	}
	
	private static void parseVmFile(List<String> lines, CodeWriter writer) throws IOException{
		
		//pass lines to parser
		Parser parser = new Parser(lines);
		
		while(parser.hasMoreCommands()){
			parser.advance();
			
			String command;
			String segment;
			Integer index;
			
			String commandType = parser.commandType();
			switch(commandType){
				case "C_ARITHMETIC":
					command = parser.arg1();
					writer.writeArithmetic(command);
					break;
				case "C_PUSH":
					segment = parser.arg1();
					index = parser.arg2();
					writer.writePushPop(commandType, segment, index);
					break;
				case "C_POP":
					segment = parser.arg1();
					index = parser.arg2();
					writer.writePushPop(commandType, segment, index);
					break;
				case "C_LABEL":
					segment = parser.arg1();
					writer.writeLabel(segment);
					break;
				case "C_GOTO":
					segment = parser.arg1();
					writer.writeGoto(segment);
					break;
				case "C_IF":
					segment = parser.arg1();
					writer.writeIf(segment);
					break;
				case "C_FUNCTION":
					segment = parser.arg1();
					index = parser.arg2();
					writer.writeFunction(segment, index);
					break;
				case "C_CALL":
					segment = parser.arg1();
					index = parser.arg2();
					writer.writeCall(segment, index);
					break;
				case "C_RETURN":
					writer.writeReturn();
					break;
			
			}
					
		}
	}
	
}
