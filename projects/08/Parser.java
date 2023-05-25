
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Parser {
    private File file;
    private FileReader reader;
    private BufferedReader breader;
    public String line;

    // Opens a new stream from the given file
    public Parser(File file) {
        this.file = file;
        try {
            this.reader = new FileReader(this.file);
            this.breader = new BufferedReader(reader);

        } catch (IOException e) {

        }
    }

    // This method advances until gets to the last line every time it goes to the
    // next valid line
    public boolean advance() throws IOException {

        this.line = breader.readLine();
        if (line == null) {
            breader.close();
            reader.close();
            return false;
        } else {
            this.line = line.trim();
            while (isCommentorisEmpty()) {
                advance();
            }

            // If there is a valid line it cleans it from comments and etc.
            if (line.contains("//")) {
                line = line.substring(0, line.indexOf("//"));
                line = line.trim();
            }
        }

        return true;
    }

    // Returns the Command Type
    public commands commandType() {
        if (line.startsWith("push"))
            return commands.C_PUSH;
        else if (line.startsWith("pop"))
            return commands.C_POP;
        else if (line.startsWith("label"))
            return commands.C_LABEL;
        else if (line.startsWith("goto"))
            return commands.C_GOTO;
        else if (line.startsWith("if-goto"))
            return commands.C_IF;
        else if (line.startsWith("return"))
            return commands.C_RETURN;
        else if (line.startsWith("call"))
            return commands.C_CALL;
        else if (line.startsWith("function"))
            return commands.C_FUNCTION;
        return commands.C_ARITHMETIC;
    }

    // Returns the arg1 from each command, if its a Arithmetic command returns the
    // all line
    public String arg1() {
        if (this.commandType() == commands.C_ARITHMETIC) {
            return line;
        } else if (!(this.commandType() == commands.C_RETURN)) {
            String[] args = this.line.split(" ");
            return args[1];
        }
        return null;
    }

    // Returns the arg2 from each command
    public String arg2() {
        if (this.commandType() == commands.C_PUSH || this.commandType() == commands.C_POP
                || this.commandType() == commands.C_FUNCTION || this.commandType() == commands.C_CALL) {
            String[] args = this.line.split(" ");
            return args[2];
        }
        return "Im from arg2";
    }

    // Checks if the line we are looking at is a valid line to parse
    public boolean isCommentorisEmpty() {
        return this.line.startsWith("//") || this.line.isEmpty();
    }

    // Type of valid commands
    enum commands {
        C_ARITHMETIC,
        C_PUSH,
        C_POP,
        C_LABEL,
        C_GOTO,
        C_IF,
        C_FUNCTION,
        C_RETURN,
        C_CALL
    }
}
