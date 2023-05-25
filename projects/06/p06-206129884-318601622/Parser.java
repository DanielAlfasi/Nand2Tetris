
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class Parser {
    private File file;
    private FileReader reader;
    private BufferedReader breader;
    public String line;

    // Parser constructor, Creates a filereader and a bufferreader for the instance
    // of parser to use
    public Parser(File file) {
        this.file = file;
        try {
            this.reader = new FileReader(this.file);
            this.breader = new BufferedReader(reader);

        } catch (IOException e) {

        }
    }

    // This method is returns true/false whether there is another line to read and
    // if there is it advances the pointer of buffered reader to it
    // It also uses the method that skips the Comment lines and the Empty lines

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
            if (line.contains(" ") || line.contains("//")) {
                line = line.substring(0, line.indexOf(" "));
            }
        }

        return true;
    }

    // This method returns a commands type which is enum and specify the commandtype
    // of the line we are looking at
    public commands instructionType() {
        if (line.startsWith("@"))
            return commands.A_INSTRUCTION;
        else if (line.startsWith("("))
            return commands.L_INSTRUCTION;
        return commands.C_INSTRUCTION;
    }

    // This method returns a String in a binary form uses the Code class(dest part)
    public String dest() {
        if (line.contains("="))
            return Code.dest(line.substring(0, line.indexOf("=")));
        return Code.dest("");
    }

    // This method returns a String in a binary form uses the Code class(jump part)
    public String jump() {
        if (line.contains(";"))
            return Code.jump(line.substring(line.indexOf(";") + 1));
        return Code.jump("");
    }

    // This method returns a String in a binary form uses the Code class(comp part)
    // There is two cases in this part and we handle them seperately
    public String comp() {
        if (line.contains("=")) {
            return Code.comp(line.substring(line.indexOf("=") + 1));
        } else if (line.contains(";"))
            return Code.comp(line.substring(0, line.indexOf(";")));
        return Code.comp("");
    }

    // We made an Enum commands for each type of instruction
    enum commands {
        A_INSTRUCTION,
        C_INSTRUCTION,
        L_INSTRUCTION
    }

    // This method returns true/false whether the line is empty or its comment line/
    // false otherwise
    public boolean isCommentorisEmpty() {
        return this.line.startsWith("//") || this.line.isEmpty();
    }

}
