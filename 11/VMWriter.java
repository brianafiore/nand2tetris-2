import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class VMWriter {
	
	private BufferedWriter writer;
	
	public VMWriter(Path path) throws IOException{
		File file = path.toFile(); //create file from path object
		String dir = path.getParent().toString(); //get the directory to write the xml file in
		String fileName = file.getName().substring(0, file.getName().lastIndexOf('.')); //get filename without extension
		Path filePath = Paths.get(dir, fileName +".vm"); //get the path object of the new vm file
		
		//delete old file and create new
		Files.deleteIfExists(filePath);
		if(Files.notExists(filePath, LinkOption.NOFOLLOW_LINKS)){
			Files.createFile(filePath);
		}
		
		this.writer = Files.newBufferedWriter(filePath, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
	}
	
	public void writePush(String segment, int index){
		try{
			if(segment.equals("field")) segment = "this";
			writer.write("push " + segment + " " + String.valueOf(index));
			writer.newLine();
			writer.flush();
		} catch (IOException e){}
	}
	
	public void writepop(String segment, int index){
		try{
			if(segment.equals("field")) segment = "this";
			writer.write("pop " + segment + " " + String.valueOf(index));
			writer.newLine();
			writer.flush();
		} catch (IOException e){}
	}
	
	public void writeArithmetic(String command){
		try{
			writer.write(command);
			writer.newLine();
			writer.flush();
		} catch (IOException e){}
	}
	
	public void writeLabel(String label){
		try{
			writer.write("label " + label);
			writer.newLine();
			writer.flush();
		} catch (IOException e){}
	}
	
	public void writeGoto(String label){
		try{
			writer.write("goto " + label);
			writer.newLine();
			writer.flush();
		} catch (IOException e){}
	}
	
	public void writeIf(String label){
		try{
			writer.write("if-goto " + label);
			writer.newLine();
			writer.flush();
		} catch (IOException e){}
	}
	
	public void writeCall(String name, int nArgs){
		try{
			writer.write("call " + name + " " + String.valueOf(nArgs));
			writer.newLine();
			writer.flush();
		} catch (IOException e){}
	}
	
	public void writeFunction(String name, int nLocals){
		try{
			writer.write("function " + name + " " + String.valueOf(nLocals));
			writer.newLine();
			writer.flush();
		} catch (IOException e){}
	}
	
	public void writeReturn(){
		try{
			writer.write("return");
			writer.newLine();
			writer.flush();
		} catch (IOException e){}
	}
	
	public void close(){
		try{
			writer.close();
		} catch (IOException e){}
		
	}
	
}

