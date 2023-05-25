public class RowInTable {
    private String type, kind;
    private int index;

    // This class represent a entry in the Symbol table
    public RowInTable(String type, String kind, int index) {
        this.type = type;
        this.kind = kind;
        this.index = index;
    }

    public String getKind() {
        return this.kind;
    }

    public String getType() {
        return this.type;
    }

    public int getIndex() {
        return this.index;
    }
}
