// This file is part of www.nand2tetris.org
// and the book "The Elements of Computing Systems"
// by Nisan and Schocken, MIT Press.
// File name: projects/04/Fill.asm

// Runs an infinite loop that listens to the keyboard input.
// When a key is pressed (any key), the program blackens the screen,
// i.e. writes "black" in every pixel;
// the screen should remain fully black as long as the key is pressed. 
// When no key is pressed, the program clears the screen, i.e. writes
// "white" in every pixel;
// the screen should remain fully clear as long as no key is pressed.

// Put your code here.

//PSEUDOCODE:

//global pointer = -1;
//global n = 8k //SCREEN.length();
//
//if(keyPressed){
//	while(pointer+1 < n){
//		//fill row
//		SCREEN[pointer] = -1; //1111111111111111
//		pointer++;
//	}
//
//} else {
//	while(pointer >= 0){
//			screen[pointer] = 0; //0000000000000000
//			pointer--;
//	}
//}

			@i
			M=-1 //set the pointer to -1
			@8192
			D=A //set D to 0x2000
			@n
			M=D //set n to 0x2000 (screen size)

(LISTEN)
			@KBD
			D=M //set D register to key pressed
			@FILL
			D;JGT //if key is pressed go to fill
			@ERASE
			0;JMP //else jump to beginning of erase

(FILL)
			@n
			D=M-1 //set D register to n-1
			@i
			D=D-M //set D register to n-1-i
			@LISTEN
			D;JLE //jump to listen if screen is full (n-1-i = 0)
			@i
			D=M+1 //set D register to i+1
			@SCREEN
			A=D+A //set A register to Screen[0] + i+1
			M=-1 //fill row of screen at Screen[i+1]
			@i
			M=M+1 //increment i
			@LISTEN
			0;JMP //jump to listen

(ERASE)
			@i
			D=M //set D register to i
			@LISTEN
			D;JLT //jump to listen if i<0
			@SCREEN
			A=D+A //set A register to Screen[0] + i
			M=0 //erase row of screen at Screen[i]
			@i
			M=M-1 //decrement i
			@LISTEN
			0;JMP //jump to listen