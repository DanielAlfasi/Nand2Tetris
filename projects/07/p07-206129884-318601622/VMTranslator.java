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
            asmFile = new File(file + ".asm");

        }

        // Construct a new Parser and a new CodeWriter
        Parser parser = new Parser(file);
        CodeWriter cd = new CodeWriter(asmFile);

        for (File f : files) {
            cd.setFileName(f);
        }
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
            }
        }

        cd.close();

    }

    public static ArrayList<File> getAllFiles(File allFiles) {
        File[] files = allFiles.listFiles();
        ArrayList<File> arrayfiles = new ArrayList<>();
        if (files != null) {
            for (File file : files) {
                if (file.getName().endsWith(".vm"))
                    arrayfiles.add(file);
            }
        }
        return arrayfiles;

    }
}
