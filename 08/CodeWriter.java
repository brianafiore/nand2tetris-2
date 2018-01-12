import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.LinkOption;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;

public class CodeWriter {
	
	private int ifIndex = 0;

	private String fileName;
	private BufferedWriter writer;
	private String functionName; 
	private Integer callCounter = 0;
	
	public CodeWriter(Path filePath, Path mainFilePath) throws IOException{
		
		this.functionName = null;
		this.callCounter = 0;
		File file = filePath.toFile();
		
		//get fileName from current .vm file;
		if(Files.isDirectory(filePath)){
			this.fileName = file.getName();
		} else {
			this.fileName = file.getName().substring(0, file.getName().lastIndexOf('.'));
		}		

		//open buffer to append lines to mainFilePath.asm file
		this.writer = Files.newBufferedWriter(mainFilePath, StandardCharsets.UTF_8, StandardOpenOption.APPEND);
	}
	
	public void writeArithmetic(String command) throws IOException{
		StringBuilder asmCommand = new StringBuilder();
		switch(command){
			case "add":
				asmCommand.append("@SP\nAM=M-1\nD=M\nA=A-1\n");
				asmCommand.append("M=D+M\n");
				break;
			case "sub":
				asmCommand.append("@SP\nAM=M-1\nD=M\nA=A-1\n");
				asmCommand.append("M=M-D\n");
				break;
			case "neg":
				asmCommand.append("@SP\nA=M-1\n");
				asmCommand.append("M=-M\n");
				break;
			case "eq":
				asmCommand.append("@SP\nAM=M-1\nD=M\nA=A-1\nD=D-M\n");
				asmCommand.append("@IF_");
				asmCommand.append(ifIndex);
				asmCommand.append("\nD;JEQ\nD=0\n@ENDIF_");
				asmCommand.append(ifIndex);
				asmCommand.append("\n0;JMP\n(IF_");
				asmCommand.append(ifIndex);
				asmCommand.append(")\nD=-1\n(ENDIF_");
				asmCommand.append(ifIndex);
				asmCommand.append(")\n@SP\nA=M-1\nM=D\n");
				ifIndex++;
				break;
			case "gt":
				asmCommand.append("@SP\nAM=M-1\nD=M\nA=A-1\nD=D-M\n");
				asmCommand.append("@IF_");
				asmCommand.append(ifIndex);
				asmCommand.append("\nD;JLT\nD=0\n@ENDIF_");
				asmCommand.append(ifIndex);
				asmCommand.append("\n0;JMP\n(IF_");
				asmCommand.append(ifIndex);
				asmCommand.append(")\nD=-1\n(ENDIF_");
				asmCommand.append(ifIndex);
				asmCommand.append(")\n@SP\nA=M-1\nM=D\n");
				ifIndex++;
				break;
			case "lt":
				asmCommand.append("@SP\nAM=M-1\nD=M\nA=A-1\nD=D-M\n");
				asmCommand.append("@IF_");
				asmCommand.append(ifIndex);
				asmCommand.append("\nD;JGT\nD=0\n@ENDIF_");
				asmCommand.append(ifIndex);
				asmCommand.append("\n0;JMP\n(IF_");
				asmCommand.append(ifIndex);
				asmCommand.append(")\nD=-1\n(ENDIF_");
				asmCommand.append(ifIndex);
				asmCommand.append(")\n@SP\nA=M-1\nM=D\n");
				ifIndex++;
				break;
			case "and":
				asmCommand.append("@SP\nAM=M-1\nD=M\nA=A-1\n");
				asmCommand.append("M=D&M\n");
				break;
			case "or":
				asmCommand.append("@SP\nAM=M-1\nD=M\nA=A-1\n");
				asmCommand.append("M=D|M\n");
				break;
			case "not":
				asmCommand.append("@SP\nA=M-1\n");
				asmCommand.append("M=!M\n");
				break;
		}
		writer.write(asmCommand.toString());
		writer.flush();
	}
	
	public void writePushPop(String command, String segment, Integer index) throws IOException{
		StringBuilder asmCommand = new StringBuilder();
		
		if(command.equals("C_PUSH")){
			//get the address and value
			switch(segment){
			case "argument":
				asmCommand.append("@ARG\nD=M\n");
				asmCommand.append("@");
				asmCommand.append(index);
				asmCommand.append("\nA=D+A\nD=M\n");
				break;
			case "local":
				asmCommand.append("@LCL\nD=M\n");
				asmCommand.append("@");
				asmCommand.append(index);
				asmCommand.append("\nA=D+A\nD=M\n");
				break;
			case "static":
				asmCommand.append("@");
				asmCommand.append(fileName);
				asmCommand.append(".");
				asmCommand.append(index);
				asmCommand.append("\nD=M\n");
				break;
			case "constant":
				asmCommand.append("@");
				asmCommand.append(index);
				asmCommand.append("\nD=A\n");
				//no more code
				break;
			case "this":
				asmCommand.append("@THIS\nD=M\n");
				asmCommand.append("@");
				asmCommand.append(index);
				asmCommand.append("\nA=D+A\nD=M\n");
				break;
			case "that":
				asmCommand.append("@THAT\nD=M\n");
				asmCommand.append("@");
				asmCommand.append(index);
				asmCommand.append("\nA=D+A\nD=M\n");
				break;
			case "pointer":
				asmCommand.append("@");
				asmCommand.append(index+3);
				asmCommand.append("\nD=M\n");
				break;
			case "temp":
				asmCommand.append("@");
				asmCommand.append(index+5);
				asmCommand.append("\nD=M\n");
				break;
		}
			
			//set the stack pointer and push the value
			asmCommand.append("@SP\nAM=M+1\nA=A-1\nM=D\n");
			
		 } else {
			//C_POP
			//get the value
			switch(segment){
			case "argument":
				asmCommand.append("@ARG\nD=M\n");
				asmCommand.append("@");
				asmCommand.append(index);
				asmCommand.append("\nD=D+A\n@R13\nM=D\n");
				break;
			case "local":
				asmCommand.append("@LCL\nD=M\n");
				asmCommand.append("@");
				asmCommand.append(index);
				asmCommand.append("\nD=D+A\n@R13\nM=D\n");
				break;
			case "static":
				asmCommand.append("@");
				asmCommand.append(fileName);
				asmCommand.append(".");
				asmCommand.append(index);
				asmCommand.append("\nD=A\n@R13\nM=D\n");
				break;
			case "constant":
				asmCommand.append("@");
				asmCommand.append(index);
				asmCommand.append("\nD=A\n");
				asmCommand.append("@R13\nM=D\n");
				break;
			case "this":
				asmCommand.append("@THIS\nD=M\n");
				asmCommand.append("@");
				asmCommand.append(index);
				asmCommand.append("\nD=D+A\n@R13\nM=D\n");
				break;
			case "that":
				asmCommand.append("@THAT\nD=M\n");
				asmCommand.append("@");
				asmCommand.append(index);
				asmCommand.append("\nD=D+A\n@R13\nM=D\n");
				break;
			case "pointer":
				asmCommand.append("@");
				asmCommand.append(index+3);
				asmCommand.append("\nD=A\n@R13\nM=D\n");
				break;
			case "temp":
				asmCommand.append("@");
				asmCommand.append(index+5);
				asmCommand.append("\nD=A\n@R13\nM=D\n");
				break;
		}
			
			//set the stack pointer and pop the value
			asmCommand.append("@SP\nAM=M-1\nD=M\n");
			asmCommand.append("@R13\nA=M\nM=D\n");
		 }
		writer.write(asmCommand.toString());
		writer.flush();
		
	}
	
	public void writeInit() throws IOException{
		StringBuilder asmCommand = new StringBuilder();
		//SP=256
		asmCommand.append("@256\nD=A\n@SP\nM=D\n");
		writer.write(asmCommand.toString());
		writer.flush();
		//call Sys.init
		writeCall("Sys.init", 0);
		
		
	}
	
	public void writeLabel(String label) throws IOException{
		StringBuilder asmCommand = new StringBuilder();
		asmCommand.append("(");
		if(functionName == null){
			asmCommand.append("null");
		} else{
			asmCommand.append(functionName);
		}
		asmCommand.append("$");
		asmCommand.append(label);
		asmCommand.append(")\n");
		writer.write(asmCommand.toString());
		writer.flush();
	}
	
	public void writeGoto(String label) throws IOException{
		StringBuilder asmCommand = new StringBuilder();
		asmCommand.append("@");
		if(functionName == null){
			asmCommand.append("null");
		} else{
			asmCommand.append(functionName);
		}
		asmCommand.append("$");
		asmCommand.append(label);
		asmCommand.append("\n0;JMP\n");
		writer.write(asmCommand.toString());
		writer.flush();
	}
	
	public void writeIf(String label) throws IOException{
		StringBuilder asmCommand = new StringBuilder();
		asmCommand.append("@SP\nAM=M-1\nD=M\n@"); //pop
		if(functionName == null){
			asmCommand.append("null");
		} else{
			asmCommand.append(functionName);
		}
		asmCommand.append("$");
		asmCommand.append(label);
		asmCommand.append("\nD;JNE\n");
		writer.write(asmCommand.toString());
		writer.flush();
	}
	
	public void writeCall(String functionName, Integer numArgs) throws IOException{
		String retAddress = ".ret"+callCounter.toString();
		StringBuilder asmCommand = new StringBuilder();
		String basicPushCommand = "@SP\nAM=M+1\nA=A-1\nM=D\n";
		
		//save caller state
		
		//push return address
		asmCommand.append("@");
		asmCommand.append(this.functionName+"$"+functionName+retAddress);
		asmCommand.append("\nD=A\n");
		asmCommand.append(basicPushCommand);
		//push other values
		asmCommand.append("@LCL\nD=M\n");
		asmCommand.append(basicPushCommand);
		asmCommand.append("@ARG\nD=M\n");
		asmCommand.append(basicPushCommand);
		asmCommand.append("@THIS\nD=M\n");
		asmCommand.append(basicPushCommand);
		asmCommand.append("@THAT\nD=M\n");
		asmCommand.append(basicPushCommand);
		
		//reposition ags pointer
		asmCommand.append("@5\nD=A\n@");
		asmCommand.append(numArgs);
		asmCommand.append("\nD=D+A\n@SP\nD=M-D\n@ARG\nM=D\n");
		
		//reposition local pointer
		asmCommand.append("@SP\nD=M\n@LCL\nM=D\n");
		
		//goto function
		asmCommand.append("@");
		asmCommand.append(functionName);
		asmCommand.append("\n0;JMP\n");
		
		//declare return address
		asmCommand.append("(");
		asmCommand.append(this.functionName+"$"+functionName+retAddress);
		asmCommand.append(")\n");
		
		
		writer.write(asmCommand.toString());
		writer.flush();
		
		callCounter++;
				
	}
	
	public void writeReturn() throws IOException{
		StringBuilder asmCommand = new StringBuilder();
		
		//FRAME = LCL
		asmCommand.append("@LCL\nD=M\n@R15\nM=D\n");
		
		//RET = *(FRAME-5)
		asmCommand.append("@5\nD=A\n@R15\nA=M-D\nD=M\nM=0\n@R14\nM=D\n");
		
		//ARG=pop() pops last pushed value onto arg 0
		asmCommand.append("@SP\nA=M-1\nD=M\n@ARG\nA=M\nM=D\n");
		asmCommand.append("D=A+1\n@SP\nM=D\n");
		
		//restore saved state and clear stack
		asmCommand.append("@R15\nA=M-1\nD=M\nM=0\n@THAT\nM=D\n");
		asmCommand.append("@2\nD=A\n@R15\nA=M-D\nD=M\nM=0\n@THIS\nM=D\n");
		asmCommand.append("@3\nD=A\n@R15\nA=M-D\nD=M\nM=0\n@ARG\nM=D\n");
		asmCommand.append("@4\nD=A\n@R15\nA=M-D\nD=M\nM=0\n@LCL\nM=D\n");
		
		//goto return address
		asmCommand.append("@R14\nA=M\n0;JMP\n");
		
		writer.write(asmCommand.toString());
		writer.flush();

	}
	
	public void writeFunction(String functionName, Integer numLocals) throws IOException{
		StringBuilder asmCommand = new StringBuilder();
		asmCommand.append("(");
		asmCommand.append(functionName);
		asmCommand.append(")\n");
		writer.write(asmCommand.toString());
		writer.flush();
		for(int i = 0; i < numLocals; i++){
			writePushPop("C_PUSH", "constant", 0);
		}
		this.functionName = functionName;
	}
	
	public void close() throws IOException{
		this.writer.close();
	}
	
}
