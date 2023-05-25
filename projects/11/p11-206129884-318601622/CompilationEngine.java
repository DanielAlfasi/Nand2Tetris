import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class CompilationEngine {

    private JackTokenizer tokenizer;
    private SymbolTable classTable, subroutineTable;
    private int labelCounter;
    private String functionName;
    private String functionType;
    private String className;
    private VMWriter vmWriter;

    public CompilationEngine(BufferedReader reader, BufferedWriter writer) throws IOException {

        this.tokenizer = new JackTokenizer(reader);
        this.classTable = new SymbolTable();
        this.subroutineTable = new SymbolTable();
        this.vmWriter = new VMWriter(writer);

        compileClass(); // complies the class

    }

    public void compileClass() throws IOException {
        engineAdvance(); // class
        engineAdvance(); // className

        this.className = tokenizer.identifier();

        engineAdvance(); // {

        engineAdvance();
        // Checks if the class is empty

        // Checks if varDec need to be proccessed
        while (tokenizer.classVarDecTypes()) {
            compileClassVarDec();
            engineAdvance();

        }

        // Checks if subroutineDec need to be proccessed
        while (tokenizer.subRoutineTypes()) {
            compileSubroutine();
            engineAdvance();
        }

    }

    // Complies the class var declartions and add's them to the classTable
    public void compileClassVarDec() throws IOException {
        String name, type, kind;
        kind = tokenizer.tokenValue().toUpperCase();

        engineAdvance();
        type = tokenizer.tokenValue();

        engineAdvance();
        name = tokenizer.identifier();

        classTable.define(name, type, kind);

        engineAdvance();
        while (!(tokenizer.tokenType() == TOKENTYPE.SYMBOL && tokenizer.symbol() == ';')) {
            engineAdvance();
            name = tokenizer.identifier();

            classTable.define(name, type, kind);

            engineAdvance();
        }

    }

    // Complies the subroutine -> compiles the parameterlist -> compiles
    // subroutinebody
    public void compileSubroutine() throws IOException {
        subroutineTable.reset();
        this.functionType = tokenizer.tokenValue();

        engineAdvance();
        engineAdvance();
        this.functionName = tokenizer.identifier();

        engineAdvance();

        engineAdvance();

        compileParameterList();

        compileSubroutineBody();

    }

    // Complies the parameterlist and adds them to the subroutine table
    public void compileParameterList() throws IOException {
        String name, type, kind;
        if (tokenizer.tokenType() != TOKENTYPE.SYMBOL) {
            kind = "ARG";
            // Parameter type
            type = tokenizer.tokenValue();

            engineAdvance();
            // Parameter name
            name = tokenizer.tokenValue();
            subroutineTable.define(name, type, kind);

            engineAdvance();
            while (!(tokenizer.tokenType() == TOKENTYPE.SYMBOL && tokenizer.symbol() == ')')) {
                // type
                engineAdvance();
                type = tokenizer.tokenValue();

                // name/identifier
                engineAdvance();
                name = tokenizer.identifier();
                subroutineTable.define(name, type, kind);

                engineAdvance();
            }
        }
    }

    // complies subroutinebody -> complies the vardec -> writes the function in VM
    // -> complies the statements
    public void compileSubroutineBody() throws IOException {

        engineAdvance();

        engineAdvance();

        while (tokenizer.tokenType() == TOKENTYPE.KEYWORD
                && tokenizer.keyWord() == KEYWORD.VAR) {
            compileVarDec();
            engineAdvance();
        }

        vmWriter.writeFunction(functionName, className, subroutineTable.VarCount("LOCAL"));
        if (functionType.equals("method")) {
            subroutineTable.define("this", className, "ARG");
            vmWriter.writePush("argument", 0);
            vmWriter.writePop("pointer", 0);
        } else if (functionType.equals("constructor")) {
            vmWriter.writePush("constant", classTable.VarCount("FIELD"));
            vmWriter.writeCall("Memory.alloc", 1);
            vmWriter.writePop("pointer", 0);
        }
        compileStatements();

    }

    // Complies the subroutine var declartions and adds them to the subroutineTable
    public void compileVarDec() throws IOException {
        String name, type, kind;
        kind = "VAR";

        engineAdvance();

        type = tokenizer.tokenValue();

        engineAdvance();
        name = tokenizer.identifier();

        subroutineTable.define(name, type, kind);

        engineAdvance();
        while (!(tokenizer.tokenType() == TOKENTYPE.SYMBOL && tokenizer.symbol() == ';')) {
            engineAdvance();
            name = tokenizer.identifier();

            subroutineTable.define(name, type, kind);

            engineAdvance();
        }

    }

    // Maps the statement to the right one that matches its compliation
    public void compileStatements() throws IOException {
        while (tokenizer.tokenType() == TOKENTYPE.KEYWORD && (tokenizer.keyWord() == KEYWORD.DO
                || tokenizer.keyWord() == KEYWORD.IF || tokenizer.keyWord() == KEYWORD.WHILE
                || tokenizer.keyWord() == KEYWORD.LET || tokenizer.keyWord() == KEYWORD.RETURN)) {
            switch (tokenizer.keyWord()) {
                case LET:
                    compileLet();
                    break;
                case RETURN:
                    compileReturn();
                    break;
                case IF:
                    compileIf();
                    break;
                case DO:
                    compileDo();
                    break;
                case WHILE:
                    compileWhile();
                    break;
            }
        }
    }

    // Complies let statement, checks if the statement using an array, and if the
    // statement identifier is also defined and then decides the vm output
    public void compileLet() throws IOException {
        boolean isArray = false;
        int i = 0;
        String seg = "";

        engineAdvance();
        String name = tokenizer.identifier();

        if (subroutineTable.exists(name)) {
            seg = subRoutineSeg(name);
            i = subroutineTable.IndexOf(name);
        } else if (classTable.exists(name)) {
            seg = classSeg(name);
            i = classTable.IndexOf(name);
        }

        engineAdvance();
        if (tokenizer.tokenType() == TOKENTYPE.SYMBOL && tokenizer.symbol() == '[') {
            isArray = true;
            vmWriter.writePush(seg, i);

            engineAdvance();
            compileExpression();

            vmWriter.writeArithmetic("add");

            engineAdvance();
        }

        engineAdvance();

        compileExpression();

        engineAdvance();
        if (isArray) {
            arrayHandler();
        } else {
            vmWriter.writePop(seg, i);
        }

    }

    // compiles if statement, using label counter and increments it by two in case
    // there is an else label,
    public void compileIf() throws IOException {
        int cLabel = labelCounter;
        labelCounter = labelCounter + 2;
        engineAdvance();

        engineAdvance();

        compileExpression();

        vmWriter.writeArithmetic("not");

        vmWriter.writeIf("IFLABEL_" + cLabel);

        engineAdvance();

        engineAdvance();

        compileStatements();

        engineAdvance();

        if (tokenizer.tokenType() == TOKENTYPE.KEYWORD
                && tokenizer.keyWord() == KEYWORD.ELSE) {

            vmWriter.writeGoto("IFLABEL_" + (cLabel + 1));
            vmWriter.writeLabel("IFLABEL_" + cLabel++);

            engineAdvance();

            engineAdvance();

            compileStatements();

            engineAdvance();
        }

        vmWriter.writeLabel("IFLABEL_" + cLabel);

    }

    // complies while statment
    public void compileWhile() throws IOException {
        int cLabel = labelCounter;
        labelCounter = labelCounter + 2;

        vmWriter.writeLabel("WHILE_" + cLabel);

        engineAdvance();

        engineAdvance();
        compileExpression();

        vmWriter.writeArithmetic("not");

        vmWriter.writeIf("WHILE_" + (cLabel + 1));

        engineAdvance();
        engineAdvance();
        compileStatements();

        engineAdvance();
        vmWriter.writeGoto("WHILE_" + cLabel++);
        vmWriter.writeLabel("WHILE_" + cLabel);

    }

    // Complies the do the statement
    public void compileDo() throws IOException {
        String caller = "";
        String name = "";
        int numofVars = 0;

        engineAdvance();
        name = tokenizer.identifier(); // Saves the identifier
        engineAdvance();

        if (tokenizer.tokenType() == TOKENTYPE.SYMBOL && tokenizer.symbol() == '.') { // Checks if the name exists in
                                                                                      // the subroutine/class table in
                                                                                      // order to push its location to
                                                                                      // the stack
            if (subroutineTable.exists(name)) {
                caller = subroutineTable.TypeOf(name) + ".";
                numofVars++;

                vmWriter.writePush(subRoutineSeg(name), subroutineTable.IndexOf(name));
            } else if (classTable.exists(name)) {
                caller = classTable.TypeOf(name) + ".";
                numofVars++;

                vmWriter.writePush(classSeg(name), classTable.IndexOf(name));
            } else
                caller = name + ".";

            engineAdvance();

            caller += tokenizer.identifier();

            engineAdvance();
        } else {
            caller = className + "." + name;
            numofVars++;

            vmWriter.writePush("pointer", 0);
        }
        engineAdvance();
        numofVars += compileExpressionList();

        vmWriter.writeCall(caller, numofVars);

        engineAdvance();
        engineAdvance();

        vmWriter.writePop("temp", 0);
    }

    // Complies the return statement, in case of a void function that ends with
    // return; uses the ShimonShoken convention for void
    public void compileReturn() throws IOException {
        engineAdvance();
        if (!(tokenizer.tokenType() == TOKENTYPE.SYMBOL && tokenizer.symbol() == ';')) {
            compileExpression();
        } else {
            vmWriter.writePush("constant", 0);
        }
        engineAdvance();
        vmWriter.writeReturn();

    }

    // Complies expression
    public void compileExpression() throws IOException {

        // complies the expression term
        compileTerm();

        // Checks if the expression uses a OP symbol, if it does, complies the term and
        // provides the proper vm for it
        while (tokenizer.tokenType() == TOKENTYPE.SYMBOL
                && (tokenizer.symbol() == '>' || tokenizer.symbol() == '*'
                        || tokenizer.symbol() == '/' || tokenizer.symbol() == '<'
                        || tokenizer.symbol() == '|' || tokenizer.symbol() == '&'
                        || tokenizer.symbol() == '+' || tokenizer.symbol() == '-'
                        || tokenizer.symbol() == '=')) {
            char op = tokenizer.symbol();

            engineAdvance();
            compileTerm();

            switch (op) {
                case '*':
                    vmWriter.writeCall("Math.multiply", 2);
                    break;
                case '>':
                    vmWriter.writeArithmetic("gt");
                    break;
                case '/':
                    vmWriter.writeCall("Math.divide", 2);
                    break;
                case '<':
                    vmWriter.writeArithmetic("lt");
                    break;
                case '&':
                    vmWriter.writeArithmetic("and");
                    break;
                case '|':
                    vmWriter.writeArithmetic("or");
                    break;
                case '+':
                    vmWriter.writeArithmetic("add");
                    break;
                case '-':
                    vmWriter.writeArithmetic("sub");
                    break;
                case '=':
                    vmWriter.writeArithmetic("eq");
                    break;

            }
        }
    }

    // complies Term
    public void compileTerm() throws IOException {
        String seg = "";
        int index = 0;

        if (tokenizer.tokenType() == TOKENTYPE.SYMBOL) { // Checks if the current token is a symbol
            if (tokenizer.symbol() == '(') {
                engineAdvance();
                compileExpression();
                engineAdvance();
            } else {
                char symbol = tokenizer.symbol();
                engineAdvance();
                compileTerm();
                if (symbol == '-') {
                    vmWriter.writeArithmetic("neg");
                } else
                    vmWriter.writeArithmetic("not");
            }
        } else if (tokenizer.tokenType() == TOKENTYPE.KEYWORD) { // Checks if its true|null|false|this
            boolean neg = false;
            switch (tokenizer.tokenValue().trim()) {
                case "true":
                    seg = "constant";
                    index = 1;
                    vmWriter.writePush("constant", 1);
                    neg = true;
                    break;
                case "null":
                    seg = "constant";
                    index = 0;
                    vmWriter.writePush("constant", 0);
                    break;
                case "false":
                    seg = "constant";
                    index = 0;
                    vmWriter.writePush("constant", 0);
                    break;
                case "this":
                    seg = "pointer";
                    index = 0;
                    vmWriter.writePush("pointer", 0);
                    break;
            }

            if (neg) { // fixes the true to be -1
                vmWriter.writeArithmetic("neg");
            }
            engineAdvance();

        } else {

            String name = tokenizer.tokenValue();

            // Checks if its a INT_CONST
            if (name.matches("[0-9]+")) {
                vmWriter.writePush("constant", Integer.parseInt(name));

                engineAdvance();
            } else if (tokenizer.tokenType() == TOKENTYPE.STRING_CONST) { // Checks if its a STRING_CONST and Writes the
                                                                          // string by convention
                name = tokenizer.stringVal();
                vmWriter.writePush("constant", name.length());
                vmWriter.writeCall("String.new", 1);
                for (int i = 0; i < name.length(); i++) {
                    vmWriter.writePush("constant", name.charAt(i));
                    vmWriter.writeCall("String.appendChar", 2);
                }
                engineAdvance();

            } else {
                engineAdvance(); // Advances to the next token and checks for identifier.|(|[
                if (tokenizer.tokenType() == TOKENTYPE.SYMBOL) {
                    if (tokenizer.symbol() == '(' || tokenizer.symbol() == '.') {
                        String caller = "";
                        int numofVars = 0;

                        if (tokenizer.tokenType() == TOKENTYPE.SYMBOL && tokenizer.symbol() == '.') {
                            if (subroutineTable.exists(name)) {
                                caller = subroutineTable.TypeOf(name) + ".";
                                numofVars++;

                                vmWriter.writePush(subRoutineSeg(name), subroutineTable.IndexOf(name));
                            } else if (classTable.exists(name)) {
                                caller = classTable.TypeOf(name) + ".";
                                numofVars++;

                                vmWriter.writePush(classSeg(name), classTable.IndexOf(name));
                            } else {
                                caller = name + ".";
                            }

                            engineAdvance();

                            caller += tokenizer.identifier();

                            engineAdvance();
                        } else {
                            caller = className + "." + name;
                            numofVars++;

                            vmWriter.writePush("pointer", 0);
                        }
                        engineAdvance();

                        numofVars += compileExpressionList();
                        vmWriter.writeCall(caller, numofVars);

                        engineAdvance();
                    } else {
                        if (subroutineTable.exists(name)) {
                            seg = subRoutineSeg(name);
                            index = subroutineTable.IndexOf(name);
                        } else if (classTable.exists(name)) {
                            seg = classSeg(name);
                            index = classTable.IndexOf(name);
                        }

                        vmWriter.writePush(seg, index);

                        if (tokenizer.symbol() == '[') {
                            engineAdvance();
                            compileExpression();
                            // Using the array convention for the proper address without leaking "Address
                            // location"
                            vmWriter.writeArithmetic("add");
                            vmWriter.writePop("pointer", 1);
                            vmWriter.writePush("that", 0);

                            engineAdvance();
                        }
                    }
                }
            }
        }

    }

    // complies expression list, returns the number of expressions as an integer
    public int compileExpressionList() throws IOException {
        int numberofArgs = 0;

        while (!(tokenizer.tokenType() == TOKENTYPE.SYMBOL && tokenizer.symbol() == ')')) {
            if (tokenizer.tokenType() == TOKENTYPE.SYMBOL && tokenizer.symbol() == ',') {
                engineAdvance();
            } else {
                numberofArgs++;
                compileExpression();

            }
        }

        return numberofArgs;
    }

    // Advances to the next token
    public void engineAdvance() throws IOException {
        if (tokenizer.hasMoreTokens()) {
            tokenizer.advance();
        } else {
            throw new IllegalArgumentException("ssdas");
        }
    }

    // Handles the array address
    public void arrayHandler() throws IOException {
        vmWriter.writePop("temp", 0);
        vmWriter.writePop("pointer", 1);
        vmWriter.writePush("temp", 0);
        vmWriter.writePop("that", 0);
    }

    // Returns arg if the name is kind of ARG in the subroutine table else returns
    // local
    public String subRoutineSeg(String name) {
        if (subroutineTable.KindOf(name).equals("ARG")) {
            return "argument";
        }
        return "local";
    }

    // Returns this if the name is a field kind else static
    public String classSeg(String name) {
        if (classTable.KindOf(name).equals("FIELD")) {
            return "this";
        }
        return "static";
    }
}
