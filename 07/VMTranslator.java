import java.io.IOException;
import java.nio.file.Paths;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class VMTranslator {
	
	public static void main(String[] args) throws IOException {
		
		//read file lines
		String filePath = args[0];
		if(!filePath.matches("^.+\\.vm$")){
			throw new IOException("Invalid File.");
		}
		
		List<String> lines = readVmFile(filePath);
		
		//pass lines to parser
		Parser parser = new Parser(lines);
		
		CodeWriter writer = new CodeWriter(filePath);
		
		
		
		while(parser.hasMoreCommands()){
			parser.advance();
			String command;
			String commandType = parser.commandType();
			if(commandType.equals("C_ARITHMETIC")){
				command = parser.arg1();
				writer.writeArithmetic(command);
			} else if (commandType.equals("C_PUSH") || commandType.equals("C_POP")){
				String segment = parser.arg1();
				Integer index = parser.arg2();
				writer.writePushPop(commandType, segment, index);
			}
			
		}
		
		writer.close();
		
	}
	
	private static List<String> readVmFile(String filePath) throws IOException{
		Path path = Paths.get(filePath);
		return Files.readAllLines(path, StandardCharsets.UTF_8);
	}
	
}
