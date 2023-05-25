import java.util.HashMap;
import java.util.Map;

public class SymbolTable {
    Map<String, RowInTable> table = new HashMap<String, RowInTable>();
    int nStatic, nField, nArg, nVar;

    public SymbolTable() {
        this.table = new HashMap<String, RowInTable>();
        this.nStatic = 0;
        this.nField = 0;
        this.nArg = 0;
        this.nVar = 0;
    }

    public void define(String name, String type, String kind) {
        // TODO implement, defiens a new identifier and adds it to the symbol table with
        // the right index.
        int index = VarCount(kind);
        RowInTable symbol = new RowInTable(type, kind, index);
        table.put(name, symbol);
    }

    // Returns the number of var's of the given kind in the current scope.
    public int VarCount(String kind) {
        switch (kind) {
            case "STATIC":
                return nStatic++;
            case "FIELD":
                return nField++;
            case "ARG":
                return nArg++;
            default:
                return nVar++;
        }

    }

    public String KindOf(String name) {

        String kindof;

        if (table.containsKey(name)) {
            kindof = table.get(name).getKind();
        } else {
            kindof = null;
        }

        if (kindof != null) {
            return kindof;
        }
        return "NONE";
    }

    public String TypeOf(String name) {
        return table.get(name).getType();
    }

    public int IndexOf(String name) {
        return table.get(name).getIndex();
    }

    public boolean exists(String name) {
        return table.containsKey(name);
    }

    public void reset() {
        this.table.clear();
        this.nArg = 0;
        this.nField = 0;
        this.nStatic = 0;
        this.nVar = 0;
    }

}