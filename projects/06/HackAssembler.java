import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class HackAssembler {
    private static int addressHolder = 16; // Initiliaze the addressHolder to the next position after the predefined's
    private static Parser parser;
    private static SymbolTable symboltable;
    private static File file;

    public static void main(String[] args) {
        // Receives the file from the args[0]
        file = new File(args[0]);

        // Creates a new symboltable
        symboltable = new SymbolTable();

        // Initliaze the proccess
        try {
            firstSwipe();
            secondSwipe();
        } catch (IOException e) {
            System.out.println("VAMOS ARGENTINA");
        }

    }

    // First swipe goes over the file and scans for L.INSTRUCTIONS , if finds them
    // saves them in the symboltable with the address of their linecount
    public static void firstSwipe() throws IOException {
        int countline = 0;
        parser = new Parser(file);
        String temp = "";

        // Iterate over all the valid lines of the file
        while (parser.advance()) {
            if (parser.instructionType().equals(Parser.commands.L_INSTRUCTION)) {
                temp = parser.line.substring(parser.line.indexOf("(") + 1, parser.line.indexOf(")"));
                if (!symboltable.contains(temp)) {
                    symboltable.addEntry(temp, countline);
                }

            } else {
                countline++; // Advances only if this line isnt an L_INSTRUCTION since we are not translating
                             // them to the binary file
            }

        }
    }

    // This method creates a new file.hack, scans the input file and uses the
    // symboltable , parser and code to convert it to binary code which is written
    // to file.hack
    public static void secondSwipe() throws IOException {
        parser = new Parser(file);
        File ofile = new File(file.getPath().replace("asm", "hack")); // Creates the new file
        FileWriter writer = new FileWriter(ofile);
        BufferedWriter bwriter = new BufferedWriter(writer);
        String temp = "";

        // Scans all the valid lines in the file
        while (parser.advance()) {

            // Checks if the current line is a A_INSTRUCTION
            if (parser.instructionType().equals(Parser.commands.A_INSTRUCTION)) {
                temp = parser.line.substring(parser.line.indexOf("@") + 1);

                // Checks if this symbol is already being caught in the first swipe or in the
                // previous reads in the second swipe , if so get it Address from symbol table
                // and converts it to binary.
                if (symboltable.contains(temp)) {
                    temp = Integer.toBinaryString(symboltable.getAddress(temp));
                } else {
                    // Checks if the A_INSTRUCTION is a specific number to the memory (like @2).
                    boolean numA = temp.matches("[0-9]+");
                    // If it isnt it means that its a variable and adds it to the next available
                    // position in the memory using the symbol table, and translates it to binary
                    if (!numA) {
                        symboltable.addEntry(temp, addressHolder);
                        addressHolder++;
                        temp = Integer.toBinaryString(symboltable.getAddress(temp));
                    } else {
                        // If the A_INSTRUCTION is a specific number, we parse it as is and converts it
                        // to binary
                        Integer c = Integer.parseInt(temp);
                        temp = Integer.toBinaryString(c);
                    }

                }

                // Fills the missing 0's (since Integer.toBinaryString isnt makes by default a
                // 16 char binary code)
                while (temp.length() < 16) {
                    temp = "0" + temp;
                }

                // Writes the binary string to the new file.hack and skip to the next line
                bwriter.write(temp);
                bwriter.newLine();

            } else if (parser.instructionType().equals(Parser.commands.L_INSTRUCTION)) {
                // L_INSTRUCTIONS are ignored in the second swipe.
            } else {
                // Checks if the line is a C_INSTRUCTION, if so using parser's method creates
                // the String according to the convention, writes it to the file and skips to
                // the next line
                if (parser.instructionType().equals(Parser.commands.C_INSTRUCTION)) {
                    temp = "111" + parser.comp() + parser.dest() + parser.jump();
                    bwriter.write(temp);
                    bwriter.newLine();
                }
            }

        }
        bwriter.close();
    }

}
