// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Mult.asm

// Multiplies R0 and R1 and stores the result in R2.
// (R0, R1, R2 refer to RAM[0], RAM[1], and RAM[2], respectively.)

// Put your code here.

//PSEUDOCODE:

//R2 = 0;
//while(R1 > 0){
//	R2 += R0;
//	R1--;
//}

//Setup
			@R2
			M=0		//R2 = 0

(LOOP)
			@R1
			D=M //store the value of RAM[1] in the D register
			@END
			D;JLE //if RAM[1]<=0 goto END
			@R0
			D=M //Store the value of RAM[0] in the D register
			@R2
			M=M+D //increment the value of RAM[2] by the value of RAM[0]
			@R1
			M=M-1 //decrease the value of RAM[1] by 1
			@LOOP
			0;JMP //goto beginning of the loop

(END)			
			@END
			0;JMP