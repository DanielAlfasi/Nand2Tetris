import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class CodeWriter {
    // In this project we used R13 and R14 in order to save some address and data
    // during the run
    private FileWriter writer;
    private BufferedWriter bwriter;
    private static String filename;
    private int loopcounter = 0;
    private int labelcounter = 0;

    // Constructor which gets a file and opens a new file.asm with a BufferWriter to
    // it
    public CodeWriter(File file) {
        try {
            bwriter = new BufferedWriter(new FileWriter(file));
            setFileName(file);
        } catch (IOException e) {

        }
    }

    public void setFileName(File file) {
        filename = file.getName();

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
                    writeC("@" + index);
                    writeC("D=A");
                    onePush();
                    break;
                case "static":
                    writeC("@" + filename + index);
                    writeC("D=M");
                    onePush();
                    break;
                case "local":
                    advancedPush("LCL", index);
                    break;
                case "argument":
                    advancedPush("ARG", index);
                    break;
                case "temp":
                    writeC("@" + (5 + index));
                    writeC("D=M");
                    onePush();
                    break;
                case "this":
                    advancedPush("THIS", index);
                    break;
                case "that":
                    advancedPush("THAT", index);
                    break;
                case "pointer":
                    if (index == 0) {
                        writeC("@THIS");
                        writeC("D=M");
                        onePush();
                    } else if (index == 1) {
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
                    onePop();
                    writeC("@" + filename + index);
                    writeC("M=D");
                    break;
                case "local":
                    advancedPop("LCL", index);
                    break;
                case "argument":
                    advancedPop("ARG", index);
                    break;
                case "temp":
                    onePop();
                    writeC("@" + (5 + index));
                    writeC("M=D");
                    break;
                case "this":
                    advancedPop("THIS", index);
                    break;
                case "that":
                    advancedPop("THAT", index);
                    break;

                case "pointer":
                    if (index == 0) {
                        writeC("@THIS");
                        writeC("D=A");
                        writeC("@R13");
                        writeC("M=D");
                        onePop();
                        writeC("@R13");
                        writeC("A=M");
                        writeC("M=D");
                    } else if (index == 1) {
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
        writer.close();
    }

    public void writeLabel(String label) throws IOException {
        writeC("(" + label + ")");
    }

    public void writeGoto(String label) throws IOException {
        writeC("@" + label);
        writeC("0;JMP");
    }

    public void writeIf(String label) throws IOException {
        doublePop();
        writeC("@" + label);
        writeC("D;JNE");
    }

    public void writeFunction(String functionname, int nVars) throws IOException {
        writeC("(" + functionname + ")");
        for (int i = 0; i < nVars; i++) {
            WritePushPop("C_PUSH", "constant", 0);
        }
    }

    public void writeCall(String functionname, int nVars) throws IOException {
        writeC("@RETURN_LABEL" + labelcounter);
        writeC("D=A");
        onePush();
        advancedPush("LCL", -1);
        advancedPush("ARG", -1);
        advancedPush("THAT", -1);
        advancedPush("THIS", -1);
        writeC("@SP");
        writeC("D=M");
        writeC("@5");
        writeC("@D=D-A");
        writeC("@" + nVars);
        writeC("D=D-A");
        writeC("@ARG");
        writeC("M=D");
        writeC("@SP");
        writeC("D=M");
        writeC("LCL");
        writeC("M=D");
        writeGoto(functionname);
        writeC("(RETURN_LABEL" + labelcounter + ")");
    }

    // @5
    public void writeReturn() throws IOException {
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
        cleanerBeforeReturn("THAT");
        cleanerBeforeReturn("THIS");
        cleanerBeforeReturn("ARG");
        cleanerBeforeReturn("LCL");
        writeC("@RET");
        writeC("A=M");
        writeC("0;JMP");
    }

    public void cleanerBeforeReturn(String segment) throws IOException {
        writeC("@FRAME");
        writeC("D=M-1");
        writeC("AM=D");
        writeC("D=M");
        writeC("@" + segment);
        writeC("M=D");
    }

}
