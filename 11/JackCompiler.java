import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;

/**
 * The compiler operates on a given source, where source is either a file name of the
 * form Xxx.jack or a directory name containing one or more such files. For each 
 * Xxx.jack input file, the compiler creates a JackTokenizer and an output Xxx.vm file.
 * Next, the compiler uses the CompilationEngine, SymbolTable, and VMWriter modules 
 * to write the output file.
 * 
 * */


public class JackCompiler {
	
	private static final String TAG = "Jack Compiler";
	private static CompilationEngine engine;
	
	public static void main(String[] args) {
		
		//get parameter
		String arg = args[0];
		Path path = Paths.get(arg);
		
		//get path and fileNames;
		if(Files.isDirectory(path)){
			try{
				DirectoryStream<Path> stream = Files.newDirectoryStream(path, "*.jack");
				for(Path entry : stream){
					parseJackFile(entry);
				}
			}catch (IOException e){
				System.out.println(TAG + "/Error reading directory");
			}
			
		} else {
			try{
				parseJackFile(path);
			}catch (IOException e){
				System.out.println(TAG + "/Error reading file");
			}
			
		}
		
	}

	private static void parseJackFile(Path path) throws IOException{
		String trimmedProgram;
		List<String> lines = Files.readAllLines(path);
		StringBuilder sb = new StringBuilder();
		for(String line : lines){
			line = line.replaceAll("(//.*)", "").trim();
			if(line.length() > 0){
				sb.append(line);
			}
		}

		trimmedProgram = sb.toString();
		trimmedProgram = trimmedProgram.replaceAll("(/\\*\\*.*?\\*/)", "");

		VMWriter writer = new VMWriter(path);
		engine = new CompilationEngine(trimmedProgram, writer);
		engine.compileClass();
	}
	
}
