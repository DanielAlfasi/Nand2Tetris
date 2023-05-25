import java.io.BufferedWriter;
import java.io.IOException;

public class VMWriter {
    BufferedWriter writer;

    public VMWriter(BufferedWriter writer) throws IOException {
        this.writer = writer;
    }

    public void writePush(String segment, int index) throws IOException { // Writes push
        writer.write("push " + segment + " " + index + "\n");
    }

    public void writePop(String segment, int index) throws IOException { // Writes pop
        writer.write("pop " + segment + " " + index + "\n");
    }

    public void writeArithmetic(String command) throws IOException { // Writes arithmetic
        writer.write(command + "\n");
    }

    public void writeLabel(String label) throws IOException { // Writes the label
        writer.write("label " + label + "\n");
    }

    public void writeGoto(String label) throws IOException { // Writes goto
        writer.write("goto " + label + "\n");
    }

    public void writeIf(String label) throws IOException { // Writes if
        writer.write("if-goto " + label + "\n");
    }

    public void writeCall(String label, int nArgs) throws IOException { // Writes the call
        writer.write("call " + label + " " + nArgs + "\n");
    }

    public void writeFunction(String name, String className, int nVars) throws IOException { // Writes the function
        writer.write("function " + className + "." + name + " " + nVars + "\n");
    }

    public void writeReturn() throws IOException { // writes return
        writer.write("return" + "\n");
    }

    public void close() throws IOException { // Closes the writer
        writer.close();
    }
}
