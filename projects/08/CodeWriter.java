import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CodeWriter {
    // In this project we used R13 and R14 in order to save some address and data
    // during the run
    private BufferedWriter bwriter;
    private static String filename;
    private int loopcounter = 0;
    private int labelcounter = 0;

    // Constructor which gets a file and opens a new file.asm with a BufferWriter to
    // it
    public CodeWriter(File file) {
        try {
            bwriter = new BufferedWriter(new FileWriter(file));
            setFileName(file.getName());
        } catch (IOException e) {

        }
    }

    public void setFileName(String nameoffile) throws IOException {
        filename = nameoffile;

    }

    // Write the Arithmetic options for each arithmetic method {add, subb, and, or,
    // not, neg, eq, lt, gt}
    public void writeArithmetic(String command) throws IOException {
        switch (command) {
            // Pops two values from the Stack and adds them and keeps the score at the head
            // of the stack
            case "add":
                doublePop();
                writeC("M=M+D");
                break;
            // Pops two values from the Stack and sub them and keeps the score at the head
            // of the stack
            case "sub":
                doublePop();
                writeC("M=M-D");
                break;
            // Pops two values from the Stack and perfom AND operation on them and keeps the
            // score at the head of the stack
            case "and":
                doublePop();
                writeC("M=M&D");
                break;
            // Pops two values from the Stack and perfom OR operation on them and keeps the
            // score at the head of the stack
            case "or":
                doublePop();
                writeC("M=M|D");
                break;
            // Performs NOT operation on the value that is at the head of the Stack
            case "not":
                writeC("@SP");
                writeC("A=M-1");
                writeC("M=!M");
                break;
            // Performs NEG operation on the value that is at the head of the Stack
            case "neg":
                writeC("D=0");
                writeC("@SP");
                writeC("A=M-1");
                writeC("M=D-M");
                break;
        }
        // For eq, lt, gt we are going to Double pop the values from the Stack and then
        // use the LooperWriter in order to Write in asm the logical command of perform
        // those operations
        if (command.equals("eq") || command.equals("lt") || command.equals("gt")) {
            String propercommand = "J" + command.toUpperCase();
            doublePop();
            looperWriter(propercommand);
            loopcounter++;

        }

    }

    // This method handle the Push/Pop commands
    public void WritePushPop(String command, String segment, int index) throws IOException {
        // Checks the type of command
        if (command == "push") {
            // Checks the type of segment and peforms the right operation for each segment
            // and each index accordinly
            switch (segment) {
                case "constant":
                    writeC("//Push constant " + index);
                    writeC("@" + index);
                    writeC("D=A");
                    onePush();
                    break;
                case "static":
                    writeC("//Push Static " + filename + index);
                    writeC("@" + filename + index);
                    writeC("D=M");
                    onePush();
                    break;
                case "local":
                    writeC("//Push Local " + index);
                    advancedPush("LCL", index);
                    break;
                case "argument":
                    writeC("//Push ARG " + index);
                    advancedPush("ARG", index);
                    break;
                case "temp":
                    writeC("//Push TEMP " + index);
                    writeC("@" + (5 + index));
                    writeC("D=M");
                    onePush();
                    break;
                case "this":
                    writeC("//Push THIS " + index);
                    advancedPush("THIS", index);
                    break;
                case "that":
                    writeC("//Push THAT " + index);
                    advancedPush("THAT", index);
                    break;
                case "pointer":
                    if (index == 0) {
                        writeC("//Push pointer " + index);
                        writeC("@THIS");
                        writeC("D=M");
                        onePush();
                    } else if (index == 1) {
                        writeC("//Push pointer " + index);
                        writeC("@THAT");
                        writeC("D=M");
                        onePush();
                    }
                    break;
                default:
                    break;
            }

        } else if (command == "pop") {
            // Checks the type of segment and peforms the right operation for each segment
            // and each index accordinly
            switch (segment) {
                case "static":
                    writeC("//pop static ");
                    onePop();
                    writeC("@" + filename + index);
                    writeC("M=D");
                    break;
                case "local":
                    writeC("//pop local " + index);
                    advancedPop("LCL", index);
                    break;
                case "argument":
                    writeC("//pop arg " + index);
                    advancedPop("ARG", index);
                    break;
                case "temp":
                    writeC("//pop temp " + index);
                    onePop();
                    writeC("@" + (5 + index));
                    writeC("M=D");
                    break;
                case "this":
                    writeC("//pop this " + index);
                    advancedPop("THIS", index);
                    break;
                case "that":
                    writeC("//pop that " + index);
                    advancedPop("THAT", index);
                    break;

                case "pointer":
                    if (index == 0) {
                        writeC("//pop pointer " + index);
                        writeC("@THIS");
                        writeC("D=A");
                        writeC("@R13");
                        writeC("M=D");
                        onePop();
                        writeC("@R13");
                        writeC("A=M");
                        writeC("M=D");
                    } else if (index == 1) {
                        writeC("//pop pointer " + index);
                        writeC("@THAT");
                        writeC("D=A");
                        writeC("@R13");
                        writeC("M=D");
                        onePop();
                        writeC("@R13");
                        writeC("A=M");
                        writeC("M=D");
                    }
                    break;
                default:
            }

        }
    }

    // The logical asm code for EQ, LT, GT operations
    public void looperWriter(String command) throws IOException {
        writeC("D=M-D");
        writeC("@FALSE" + loopcounter);
        writeC("D;" + command);
        writeC("@SP");
        writeC("A=M-1");
        writeC("M=0");
        writeC("@CONTINUE" + loopcounter);
        writeC("0;JMP");
        writeC("(FALSE" + loopcounter + ")");
        writeC("@SP");
        writeC("A=M-1");
        writeC("M=-1");
        writeC("(CONTINUE" + loopcounter + ")");
    }

    // Pushes a number to the head of the stack and inc the SP
    public void onePush() throws IOException {
        writeC("@SP");
        writeC("A=M");
        writeC("M=D");
        writeC("@SP");
        writeC("M=M+1");
    }

    // The logical push for segments - local, this, that, temp, arg
    public void advancedPush(String segment, int index) throws IOException {
        writeC("@" + segment);
        if (index != -1) {
            writeC("D=M");
            writeC("@" + index);
            writeC("A=D+A");
        }
        writeC("D=M");
        onePush();
    }

    // Pops two numbers from the stack {at the end of the operation one number is at
    // D and the other is at M}
    public void doublePop() throws IOException {
        writeC("@SP");
        writeC("AM=M-1");
        writeC("D=M");
        writeC("A=A-1");
    }

    // Pops a number and saves it in the Data register
    public void onePop() throws IOException {
        writeC("@SP");
        writeC("AM=M-1");
        writeC("D=M");
    }

    // The logical pop for segments - local, this, that, temp, arg
    public void advancedPop(String segment, int index) throws IOException {
        writeC("@" + segment);
        writeC("D=M");
        writeC("@" + index);
        writeC("D=D+A");
        writeC("@R13");
        writeC("M=D");
        onePop();
        writeC("@R13");
        writeC("A=M");
        writeC("M=D");
    }

    // Write a new command via the str arg and then proceed to the next line
    public void writeC(String str) throws IOException {
        bwriter.write(str);
        bwriter.newLine();
    }

    // Closes the file
    public void close() throws IOException {
        bwriter.close();
    }

    public void writeLabel(String label) throws IOException {
        writeC("//writeLabel");
        writeC("(" + label + ")");
    }

    // Writes an assembly language command that jumps to the specified label
    public void writeGoto(String label) throws IOException {
        writeC("//writeGoTO");
        writeC("@" + label);
        writeC("0;JMP");
    }

    public void writeIf(String label) throws IOException {
        writeC("//writeIF");
        doublePop();
        writeC("@" + label);
        writeC("D;JNE");
    }

    // Writes an assembly language function definition, and reset nVars position's
    // using pushpop with constant 0
    public void writeFunction(String functionname, int nVars) throws IOException {
        writeC("//writeFunction");
        writeC("(" + functionname + ")");
        for (int i = 0; i < nVars; i++) {
            WritePushPop("push", "constant", 0);
        }
    }

    // Writes an assembly language command that calls the specified function
    public void writeCall(String functionname, int nVars) throws IOException {
        writeC("//writeCall");
        writeC("@RETURN_LABEL" + labelcounter);
        writeC("D=A");
        onePush();
        advancedPush("LCL", -1);
        advancedPush("ARG", -1);
        advancedPush("THIS", -1);
        advancedPush("THAT", -1);
        writeC("@SP");
        writeC("D=M");
        writeC("@5");
        writeC("D=D-A");
        writeC("@" + nVars);
        writeC("D=D-A");
        writeC("@ARG");
        writeC("M=D");
        writeC("@SP");
        writeC("D=M");
        writeC("@LCL");
        writeC("M=D");
        writeGoto(functionname);
        writeC("(RETURN_LABEL" + labelcounter + ")");
        labelcounter++;

    }

    // Writes an assembly language command that returns from a function
    public void writeReturn() throws IOException {
        writeC("//writeReturn");
        writeC("@LCL");
        writeC("D=M");
        writeC("@FRAME");
        writeC("M=D");
        writeC("@5");
        writeC("A=D-A");
        writeC("D=M");
        writeC("@RET");
        writeC("M=D");
        advancedPop("ARG", 0);
        writeC("@ARG");
        writeC("D=M");
        writeC("@SP");
        writeC("M=D+1");
        beforeReturn("THAT");
        beforeReturn("THIS");
        beforeReturn("ARG");
        beforeReturn("LCL");
        writeC("@RET");
        writeC("A=M");
        writeC("0;JMP");
    }

    public void beforeReturn(String segment) throws IOException {
        writeC("@FRAME");
        writeC("D=M-1");
        writeC("AM=D");
        writeC("D=M");
        writeC("@" + segment);
        writeC("M=D");
    }

    // This method follows the convention discusses in class, and initillizes the
    // stack and the first call for Sys.int
    public void writeInit() throws IOException {
        writeC("//Init func");
        writeC("@256");
        writeC("D=A");
        writeC("@SP");
        writeC("M=D");
        writeCall("Sys.init", 0);
    }

}
