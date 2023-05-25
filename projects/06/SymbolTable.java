import java.util.TreeMap;

public class SymbolTable extends TreeMap<String, Integer> {

    public SymbolTable() {
        // Creates a Instance of a TREEMAP for the symbol table
        super();
        // Creates the predefined symbols
        this.put("R0", 0);
        this.put("R1", 1);
        this.put("R2", 2);
        this.put("R3", 3);
        this.put("R4", 4);
        this.put("R5", 5);
        this.put("R6", 6);
        this.put("R7", 7);
        this.put("R8", 8);
        this.put("R9", 9);
        this.put("R10", 10);
        this.put("R11", 11);
        this.put("R12", 12);
        this.put("R13", 13);
        this.put("R14", 14);
        this.put("R15", 15);
        this.put("SCREEN", 16384);
        this.put("KBD", 24576);
        this.put("SP", 0);
        this.put("LCL", 1);
        this.put("ARG", 2);
        this.put("THIS", 3);
        this.put("THAT", 4);
    }

    // This method adds a Symbol and his Address to the symbol table
    public void addEntry(String symbol, int address) {
        this.put(symbol, address);
    }

    // This method returns true/false if the symbol exist's/doesnt exist in the
    // symbol table
    public boolean contains(String symbol) {
        return super.containsKey(symbol);
    }

    // This method returns the value/address of the symbol
    public int getAddress(String symbol) {
        return super.get(symbol);
    }
}
