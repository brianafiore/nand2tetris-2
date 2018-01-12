import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;

public class CodeWriter {
	
	private int ifIndex = 0;

	private String fileName = "stub";
	private BufferedWriter writer;
	
	public CodeWriter(String filePath) throws IOException{
		this.writer = Files.newBufferedWriter(Paths.get(filePath.substring(0, filePath.lastIndexOf('.')) + ".asm"), StandardCharsets.UTF_8);
		setFileName(filePath);
	}
	
	private void setFileName(String filePath){
		File file = new File(filePath);
		String fileName = file.getName();
		this.fileName = fileName.substring(0, fileName.lastIndexOf('.'));
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
				asmCommand.append("\nA=A+D\nD=M\n");
				break;
			case "local":
				asmCommand.append("@LCL\nD=M\n");
				asmCommand.append("@");
				asmCommand.append(index);
				asmCommand.append("\nA=A+D\nD=M\n");
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
				asmCommand.append("\nA=A+D\nD=M\n");
				break;
			case "that":
				asmCommand.append("@THAT\nD=M\n");
				asmCommand.append("@");
				asmCommand.append(index);
				asmCommand.append("\nA=A+D\nD=M\n");
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
	
	public void close() throws IOException{
		this.writer.close();
	}
	
}
