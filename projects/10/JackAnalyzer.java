import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

public class JackAnalyzer {
    private static ArrayList<File> inputfiles = new ArrayList<>();
    private static ArrayList<File> outputfiles = new ArrayList<>();

    public static void main(String[] args) throws IOException {
        File file = new File(args[0]);
        getAllFiles(file);

        Iterator<File> inputiter = inputfiles.iterator();
        Iterator<File> outputiter = outputfiles.iterator();
        // Iterates over all the files and creates the proper xml for each one.
        while (inputiter.hasNext() && outputiter.hasNext()) {
            BufferedReader reader = new BufferedReader(new FileReader(inputiter.next()));
            BufferedWriter writer = new BufferedWriter(new FileWriter(outputiter.next()));
            CompilationEngine cEngine = new CompilationEngine(reader, writer);
            reader.close();
            writer.close();
        }
    }

    // This method prepares the inputfiles and creates the output files
    public static void getAllFiles(File filepath) {
        File xmlfile;
        int length;
        if (filepath.isFile() && filepath.getAbsolutePath().endsWith(".jack")) {
            inputfiles.add(filepath);
            length = filepath.getAbsolutePath().length();
            xmlfile = new File(filepath.getAbsolutePath().substring(0, length - 5) + ".xml");
            outputfiles.add(xmlfile);
        } else {
            File[] files = filepath.listFiles();
            if (files != null) {
                for (File file : files) {
                    if (file.getAbsolutePath().endsWith(".jack")) {
                        inputfiles.add(file);
                        length = file.getAbsolutePath().length();
                        xmlfile = new File(file.getAbsolutePath().substring(0, length - 5) + ".xml");
                        outputfiles.add(xmlfile);
                    }
                }
            }
        }

    }
}
