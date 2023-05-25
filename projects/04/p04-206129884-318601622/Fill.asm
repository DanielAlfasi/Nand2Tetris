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

(BLACK)
@KBD 
D = M // Saves the Keyboard memory value


@WHITE
D;JEQ // If keyboard value is zero goto white loop

@24575 // Checkes if we colored the last pixel of the screen in black
D = M
@WHITE // If we colored the last pixel we prevent the action of BLACK to prevent us reaching memory addresses that are not connected to the screen
D;JLT

@i 
D = M // Sets D to be i counter
@SCREEN
D = A + D // Sets D to be in the interval of the screen pixels
A = D // Sets the Address to the current value after we calculated it
M = -1 // Change the color of the pixel to black
@i
M = M + 1 // Inc's the counter
@BLACK
0;JMP

(WHITE)
@KBD
D = M 

@BLACK
D;JGT // If keyboard is pressed goto BLACK loop

@i // Gets the i pixel in the screen interval
D = M
@SCREEN
D = A + D
A = D
M = 0 // Color it white
@i
M = M - 1 // Decrease the counter by one until we get zero and then we will color the first pixel of the screen in white over and over again
@WHITE
0;JMP

