import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class VMTranslator {
    public static void main(String[] args) throws IOException {
        // Receives the file from the arg[0]
        File file = new File(args[0]);
        File asmFile;
        ArrayList<File> files = new ArrayList<>();

        if (file.isFile() && args[0].endsWith(".vm")) {

            files.add(file);
            String firstPart = args[0].substring(0, args[0].length() - 3);
            asmFile = new File(firstPart + ".asm");
        } else // fileName is a directory - access all files in the directory
        {
            files = getAllFiles(file);
            asmFile = new File(file + "/" + file.getName() + ".asm");

        }

        // Construct a new Parser and a new CodeWriter
        CodeWriter cd = new CodeWriter(asmFile);
        // If we received a dir with files then goes by convention
        if (files.size() > 1) {
            cd.writeInit();
        }
        for (File f : files) {
            cd.setFileName(f.getName());
            Parser parser = new Parser(f);
            // Runs on the file using the advance command
            while (parser.advance()) {
                // For each Command sends to Code Writer the command type and segment
                if (parser.commandType() == Parser.commands.C_POP || parser.commandType() == Parser.commands.C_PUSH) {
                    switch (parser.commandType()) {
                        case C_POP:
                            cd.WritePushPop("pop", parser.arg1(), Integer.parseInt(parser.arg2()));
                            break;
                        case C_PUSH:
                            cd.WritePushPop("push", parser.arg1(), Integer.parseInt(parser.arg2()));
                            break;
                        default:
                            break;
                    }
                } else if (parser.commandType() == Parser.commands.C_ARITHMETIC) {
                    cd.writeArithmetic(parser.arg1());
                } else if (parser.commandType() == Parser.commands.C_IF) {
                    cd.writeIf(parser.arg1());
                } else if (parser.commandType() == Parser.commands.C_GOTO) {
                    cd.writeGoto(parser.arg1());
                } else if (parser.commandType() == Parser.commands.C_FUNCTION) {
                    cd.writeFunction(parser.arg1(), Integer.parseInt(parser.arg2()));
                } else if (parser.commandType() == Parser.commands.C_LABEL) {
                    cd.writeLabel(parser.arg1());
                } else if (parser.commandType() == Parser.commands.C_RETURN) {
                    cd.writeReturn();
                } else if (parser.commandType() == Parser.commands.C_CALL) {
                    cd.writeCall(parser.arg1(), Integer.parseInt(parser.arg2()));
                }
            }
        }
        cd.close();

    }

    // This method goes over all the files in the directory and adds the .vms files
    // to the array list.
    public static ArrayList<File> getAllFiles(File allFiles) {

        File[] files = allFiles.listFiles();
        ArrayList<File> arrayfiles = new ArrayList<>();
        if (files != null) {

            for (File file : files) {

                if (file.getName().endsWith(".vm")) {
                    arrayfiles.add(file);
                }

            }
        }
        return arrayfiles;

    }
}
