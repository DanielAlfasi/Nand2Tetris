import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;

public class CompilationEngine {
    private BufferedReader reader;
    private BufferedWriter writer;
    private JackTokenizer tokenizer;
    private int recurresiondepth;

    // Creates a new instance of a compilation engine, uses the JackTokenizer, and
    // compiles it to xml file
    public CompilationEngine(BufferedReader reader, BufferedWriter writer) throws IOException {
        this.reader = reader;
        this.writer = writer;
        this.tokenizer = new JackTokenizer(reader);
        this.recurresiondepth = 0;
        compileClass();
        // testerXML();

    }

    public void testerXML() throws IOException {
        while (tokenizer.hasMoreTokens()) {
            tokenizer.advance();
            codeWriter();
        }
    }

    // Drives the proccess in recursion
    public void compileClass() throws IOException {
        // Writes the class tag
        otagWriter("class");
        recurresiondepth++;
        engineAdvance();
        if (tokenizer.keyWord() != JackTokenizer.KEYWORD.CLASS) {
            throw new IllegalArgumentException("Missing class ");
        }
        codeWriter(); // class

        engineAdvance();
        if (tokenizer.tokenType() != JackTokenizer.TOKENTYPE.IDENTIFIER) {
            throw new IllegalArgumentException("Missing identifier ");
        }
        codeWriter(); // class name

        engineAdvance();

        if (!(tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.symbol().equals("{"))) {
            throw new IllegalArgumentException("Missing { ");
        }
        codeWriter(); // {

        engineAdvance();
        // Check if the class is empty
        if ((tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.symbol().equals("}"))) {
            codeWriter();
            ctagWriter("class");
            engineAdvance();
            return;
        } else if ((tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL)) {
            throw new IllegalArgumentException("missing }");
        } else if (tokenizer.tokenType() != JackTokenizer.TOKENTYPE.KEYWORD) {
            throw new IllegalArgumentException("missing varDec or subroutine");
        }

        // Compile the class Variables
        while (tokenizer.classVarDecTypes()) {
            recurresiondepth++;
            compileClassVarDec();
            recurresiondepth--;
        }

        // compile the subroutine methods within the class
        while (tokenizer.subRoutineTypes()) {
            recurresiondepth++;
            compileSubroutine();
            recurresiondepth--;
        }
        codeWriter(); // }
        recurresiondepth--;
        ctagWriter("class"); // Closes the class tag
    }

    // Compiles the class variable's
    public void compileClassVarDec() throws IOException {
        otagWriter("classVarDec");
        recurresiondepth++;
        genericVarDec();
        recurresiondepth--;
        ctagWriter("classVarDec");

    }

    // a generic function that compiles both varDecClass and subRoutine var's
    public void genericVarDec() throws IOException {
        // field,static var
        codeWriter();

        // Identifier / keyword
        engineAdvance();
        codeWriter();

        engineAdvance();
        codeWriter();

        engineAdvance();
        while (!(tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.symbol().equals(";"))) {
            if (!tokenizer.symbol().equals(",")) {
                throw new IllegalArgumentException("missing ,");
            }
            // symbol ,
            codeWriter();

            // identifier
            engineAdvance();
            codeWriter();
            // advances to the next ,
            engineAdvance();
        }

        codeWriter();
        engineAdvance();
    }

    // Compile subroutine -> compiles the paramterlist -> compiles the
    // subroutinebody
    public void compileSubroutine() throws IOException {
        while (tokenizer.subRoutineTypes()) {
            otagWriter("subroutineDec");

            // subroutine identifier
            codeWriter();

            engineAdvance();
            if (!(tokenizer.tokenType() == JackTokenizer.TOKENTYPE.IDENTIFIER || tokenizer.primitiveTypes()
                    || tokenizer.keyWord() == JackTokenizer.KEYWORD.VOID)) {
                throw new IllegalArgumentException("missing return type");
            }

            // return type
            codeWriter();

            // method name
            engineAdvance();
            codeWriter();

            engineAdvance();
            if (!tokenizer.tokenValue().equals("(")) {
                throw new IllegalArgumentException("missing ( after function/method name");
            }
            // ( symbol
            codeWriter();

            engineAdvance();
            if (tokenizer.primitiveTypes()) {
                recurresiondepth++;
                compileParameterList();
                recurresiondepth--;
            } else if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.tokenValue().equals(")")) {
                recurresiondepth++;
                otagWriter("parameterList");
                ctagWriter("parameterList");
                recurresiondepth--;
            } else {
                throw new IllegalArgumentException("missing close of parameterlist");
            }

            // ) symbol
            codeWriter();

            engineAdvance();
            recurresiondepth++;
            compileSubroutineBody();
            recurresiondepth--;
            ctagWriter("subroutineDec");
        }
    }

    // Compiles parameterlist
    public void compileParameterList() throws IOException {
        otagWriter("parameterList");
        // Parameter type
        codeWriter();

        engineAdvance();
        // Parameter name
        codeWriter();

        engineAdvance();
        while (!(tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.tokenValue().equals(")"))) {
            // ,
            codeWriter();

            // type
            engineAdvance();
            codeWriter();

            // name/identifier
            engineAdvance();
            codeWriter();

            engineAdvance();
        }
        ctagWriter("parameterList");
    }

    // compiles the subroutinebody -> compiles varDec -> compiles statements
    public void compileSubroutineBody() throws IOException {
        otagWriter("subroutineBody");
        // First checks whether there is a '{' symbol
        if (!(tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.tokenValue().equals("{"))) {
            throw new IllegalStateException("missing {");
        }
        codeWriter();

        engineAdvance();
        // Checks whether the next symbol closes the subroutine body
        if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL) {
            if (tokenizer.symbol().equals("}")) {
                codeWriter();
                ctagWriter("subroutineBody");
                engineAdvance();
                return;
            } else {
                throw new IllegalStateException("missing }");
            }

        }

        // validates that the next token is a keyword type.

        if (tokenizer.tokenType() != JackTokenizer.TOKENTYPE.KEYWORD) {
            throw new IllegalStateException("Missing statements or varDec in the subroutine");
        }

        if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.KEYWORD) {
            if (tokenizer.keyWord() == JackTokenizer.KEYWORD.VAR) {
                while (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.KEYWORD
                        && tokenizer.keyWord() == JackTokenizer.KEYWORD.VAR) {
                    recurresiondepth++;
                    compileVarDec();
                    recurresiondepth--;
                }
            }
        }

        if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.KEYWORD && tokenizer.statementsTypes()) {
            recurresiondepth++;
            compileStatements();
            recurresiondepth--;
        }

        if (!(tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.tokenValue().equals("}"))) {
            throw new IllegalStateException("missing }");
        }

        codeWriter();

        ctagWriter("subroutineBody");
        engineAdvance();

    }

    // Compiles the varDec's using genericVarDec
    public void compileVarDec() throws IOException {
        otagWriter("varDec");
        recurresiondepth++;
        genericVarDec();
        recurresiondepth--;
        ctagWriter("varDec");
    }

    // compiles statements, each one for the matching keyword
    public void compileStatements() throws IOException {
        otagWriter("statements");

        while (!(tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.tokenValue().equals("}"))) {
            if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.KEYWORD) {
                switch (tokenizer.keyWord()) {
                    case LET:
                        recurresiondepth++;
                        compileLet();
                        recurresiondepth--;
                        break;
                    case RETURN:
                        recurresiondepth++;
                        compileReturn();
                        recurresiondepth--;
                        break;
                    case IF:
                        recurresiondepth++;
                        compileIf();
                        recurresiondepth--;
                        break;
                    case DO:
                        recurresiondepth++;
                        compileDo();
                        recurresiondepth--;
                        break;
                    case WHILE:
                        recurresiondepth++;
                        compileWhile();
                        recurresiondepth--;
                        break;
                }
            } else {
            }
        }
        ctagWriter("statements");
    }

    // Compiles the let statement using compile expression
    public void compileLet() throws IOException {
        otagWriter("letStatement");

        // let
        codeWriter();
        // varName

        engineAdvance();
        codeWriter();

        engineAdvance();

        if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.symbol().equals("[")) {
            codeWriter();
            engineAdvance();
            recurresiondepth++;
            compileExpression();
            recurresiondepth--;
            codeWriter();
            engineAdvance();
        } else if ((tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && !tokenizer.symbol().equals("="))
                || tokenizer.tokenType() != JackTokenizer.TOKENTYPE.SYMBOL) {
            throw new IllegalStateException("missing = for let statment after varname");
        }

        // print =
        codeWriter();
        engineAdvance();
        recurresiondepth++;
        compileExpression();
        recurresiondepth--;
        codeWriter();
        ctagWriter("letStatement");
        engineAdvance();
    }

    // Compiles the if statement, using compile expression and handles the else
    // aswell
    public void compileIf() throws IOException {

        otagWriter("ifStatement");
        codeWriter();

        engineAdvance();
        codeWriter();

        engineAdvance();
        recurresiondepth++;
        compileExpression();
        recurresiondepth--;

        codeWriter();

        engineAdvance();
        codeWriter();

        engineAdvance();
        if (tokenizer.statementsTypes()) {
            recurresiondepth++;
            compileStatements();
            recurresiondepth--;
        }

        codeWriter();

        engineAdvance();
        if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.KEYWORD
                && tokenizer.keyWord().equals(JackTokenizer.KEYWORD.ELSE)) {
            codeWriter();

            engineAdvance();
            codeWriter();

            engineAdvance();
            recurresiondepth++;
            compileStatements();
            recurresiondepth--;

            codeWriter();
            engineAdvance();
        }

        ctagWriter("ifStatement");

    }

    // Compiles the while using compile expression
    public void compileWhile() throws IOException {
        otagWriter("whileStatement");
        codeWriter();

        engineAdvance();
        codeWriter();

        engineAdvance();
        recurresiondepth++;
        compileExpression();
        recurresiondepth--;
        codeWriter();

        engineAdvance();
        codeWriter();

        engineAdvance();
        if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.KEYWORD && tokenizer.statementsTypes()) {
            recurresiondepth++;
            compileStatements();
            recurresiondepth--;
        }
        codeWriter();
        ctagWriter("whileStatement");
        engineAdvance();
    }

    // compiles the do statement, using compile expressionlist.
    public void compileDo() throws IOException {
        otagWriter("doStatement");
        // keyword
        codeWriter();

        // identifier
        engineAdvance();
        codeWriter();

        engineAdvance();
        codeWriter();
        if (tokenizer.symbol().equals("(")) {
            engineAdvance();

            if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.symbol().equals(")")) {
                recurresiondepth++;
                otagWriter("expressionList");
                ctagWriter("expressionList");
                recurresiondepth--;
            } else {
                recurresiondepth++;
                compileExpressionList();
                recurresiondepth--;
            }
        } else if (tokenizer.symbol().equals(".")) {
            engineAdvance();
            codeWriter();

            engineAdvance();
            codeWriter();

            engineAdvance();
            if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.symbol().equals(")")) {
                recurresiondepth++;
                otagWriter("expressionList");
                ctagWriter("expressionList");
                recurresiondepth--;
            } else {
                recurresiondepth++;
                compileExpressionList();
                recurresiondepth--;
            }
        } else {
            throw new IllegalStateException("do statement isnt done properly");
        }
        codeWriter();
        engineAdvance();
        codeWriter();
        ctagWriter("doStatement");
        engineAdvance();
    }

    // compiles the return statement, using expression if needed
    public void compileReturn() throws IOException {
        otagWriter("returnStatement");
        codeWriter();

        engineAdvance();
        if (!(tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.symbol().equals(";"))) {
            recurresiondepth++;
            compileExpression();
            recurresiondepth--;
        }

        codeWriter();
        ctagWriter("returnStatement");
        engineAdvance();
    }

    // Complies the expression, in the case of a complex expression compiles the
    // inner terms other wise, complies the term regularly
    public void compileExpression() throws IOException {
        otagWriter("expression");
        recurresiondepth++;
        compileTerm();
        recurresiondepth--;

        while (!(tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && (tokenizer.symbol().equals("]")
                || tokenizer.symbol().equals(";") || tokenizer.symbol().equals(")")
                || tokenizer.symbol().equals(",")))) {
            codeWriter();

            engineAdvance();
            recurresiondepth++;
            compileTerm();
            recurresiondepth--;
        }

        ctagWriter("expression");
    }

    // complies the term
    public void compileTerm() throws IOException {
        otagWriter("term");
        switch (tokenizer.tokenType()) {
            case KEYWORD:
                codeWriter();
                engineAdvance();
                break;

            case INT_CONST:
                codeWriter();
                engineAdvance();
                break;

            case STRING_CONST:
                codeWriter();
                engineAdvance();
                break;

            case SYMBOL:
                if (tokenizer.unaryOp()) {
                    codeWriter();
                    engineAdvance();
                    recurresiondepth++;
                    compileTerm();
                    recurresiondepth--;
                } else {
                    codeWriter();
                    engineAdvance();
                    recurresiondepth++;
                    compileExpression();
                    recurresiondepth--;
                    codeWriter();
                    engineAdvance();
                }
                break;

            case IDENTIFIER:
                codeWriter();
                engineAdvance();
                if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && (tokenizer.symbol().equals(".")
                        || tokenizer.symbol().equals("("))) {
                    codeWriter();

                    if (tokenizer.symbol().equals("(")) {
                        engineAdvance();
                        if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.symbol().equals(")")) {
                            recurresiondepth++;
                            otagWriter("expressionList");
                            ctagWriter("expressionList");
                            recurresiondepth--;
                            engineAdvance();
                        } else {
                            recurresiondepth++;
                            compileExpressionList();
                            recurresiondepth--;
                        }
                    } else if (tokenizer.symbol().equals(".")) {
                        engineAdvance();
                        codeWriter();

                        engineAdvance();
                        codeWriter();

                        engineAdvance();
                        if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.symbol().equals(")")) {
                            recurresiondepth++;
                            otagWriter("expressionList");
                            ctagWriter("expressionList");
                            recurresiondepth--;
                        } else {
                            recurresiondepth++;
                            compileExpressionList();
                            recurresiondepth--;
                        }

                        codeWriter();
                        engineAdvance();
                    }
                } else if (tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.symbol().equals("[")) {
                    codeWriter();
                    engineAdvance();
                    recurresiondepth++;
                    compileExpression();
                    recurresiondepth--;
                    codeWriter();
                    engineAdvance();
                }
                break;
        }
        ctagWriter("term");
    }

    // Complies expression list
    public void compileExpressionList() throws IOException {
        otagWriter("expressionList");
        recurresiondepth++;
        compileExpression();
        recurresiondepth--;

        while (!(tokenizer.tokenType() == JackTokenizer.TOKENTYPE.SYMBOL && tokenizer.symbol().equals(")"))) {
            codeWriter();
            engineAdvance();
            recurresiondepth++;
            compileExpression();
            recurresiondepth--;
        }
        ctagWriter("expressionList");
    }

    // Open tag writer
    public void otagWriter(String tag) throws IOException {
        for (int i = 1; i < recurresiondepth; i++) {
            writer.write("\t");
        }
        writer.write("<" + tag + ">");
        writer.newLine();
    }

    // Close tag writer
    public void ctagWriter(String tag) throws IOException {
        for (int i = 1; i < recurresiondepth; i++) {
            writer.write("\t");
        }
        writer.write("</" + tag + "> ");
        writer.newLine();
    }

    // Writes the proper xml for the current token.
    public void codeWriter() throws IOException {
        String tag = "";
        String token = "";
        switch (tokenizer.tokenType()) {

            case SYMBOL:
                tag = "symbol";
                token = tokenizer.symbol();
                break;

            case IDENTIFIER:
                tag = "identifier";
                token = tokenizer.identifier();
                break;

            case KEYWORD:
                tag = "keyword";
                token = tokenizer.tokenValue();
                break;

            case INT_CONST:
                tag = "integerConstant";
                token = tokenizer.tokenValue();
                break;

            case STRING_CONST:
                tag = "stringConstant";
                token = tokenizer.stringVal();
                break;

            default:
                break;
        }
        for (int i = 0; i < recurresiondepth; i++) {
            writer.write("\t");
        }
        writer.write("<" + tag + "> " + token + " </" + tag + ">");
        writer.newLine();
    }

    // Advances to the next token.
    public void engineAdvance() throws IOException {
        if (tokenizer.hasMoreTokens()) {
            tokenizer.advance();
        } else {
            throw new IllegalArgumentException("No more tokens");
        }
    }
}
