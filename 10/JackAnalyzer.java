import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.List;

/**
 * The analyzer program operates on a given source, where source is either a file name
 * of the form Xxx.jack or a directory name containing one or more such files. For
 * each source Xxx.jack file, the analyzer goes through the following logic:
 * 
 * 1. Create a JackTokenizer from the Xxx.jack input file.
 * 2. Create an output file called Xxx.xml and prepare it for writing.
 * 3. Use the CompilationEngine to compile the input JackTokenizer into the output file.
 * 
 * */


public class JackAnalyzer {
	
	private static final String TAG = "Jack Analyzer";
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
	
	/**
	 * Creates an xml file with the same name as the input file and returns
	 * the path to the output file. 
	 * */
	private static Path createXmlFile(Path path) throws IOException{
		File file = path.toFile(); //create file from path object
		String dir = path.getParent().toString(); //get the directory to write the xml file in
		String fileName = file.getName().substring(0, file.getName().lastIndexOf('.')); //get filename without extension
		Path filePath = Paths.get(dir, fileName +".xml"); //get the path object of the new xml file
		
		//delete old file and create new
		Files.deleteIfExists(filePath);
		if(Files.notExists(filePath, LinkOption.NOFOLLOW_LINKS)){
			Files.createFile(filePath);
		}
		return filePath;
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
		Path outputFilePath = createXmlFile(path);
		BufferedWriter writer = Files.newBufferedWriter(outputFilePath, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
		
		engine = new CompilationEngine(trimmedProgram, writer);
		engine.compileClass();
	}
	
}
