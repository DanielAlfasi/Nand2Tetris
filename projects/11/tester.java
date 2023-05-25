import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;

public class tester {
    public static void main(String[] args) {
        // testSymbolTable();
        testCompiler();
    }

    public static void testSymbolTable() {
        SymbolTable table = new SymbolTable();
        table.define("line 1", "boolean", "STATIC");
        table.define("line 2", "boolean", "VAR");
        table.define("line 3", "boolean", "VAR");
        table.define("line 4", "boolean", "FIELD");
        table.define("line 4", "boolean", "ARG");

        System.out.println(table.KindOf("line 1") + ": expected STATIC " + table.IndexOf("line 3") + " : Expected 1");
    }

    public static void testCompiler() {
        try {
            File in = new File("sample.txt");
            File out = new File("out.txt");

            BufferedReader reader = new BufferedReader(new FileReader(in));
            BufferedWriter writer = new BufferedWriter(new FileWriter(out));

            CompilationEngine c = new CompilationEngine(reader, writer);
            reader.close();
            writer.close();

        } catch (Exception e) {
            System.out.println("kaka");
        }
    }
}
