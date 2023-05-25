import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;

public class JackTokenizer {

    private BufferedReader reader;
    public String cToken, line;
    private TOKENTYPE cTokenType;
    private boolean spancomment;
    public static HashMap<String, KEYWORD> keywordMap = new HashMap<String, KEYWORD>();
    private static HashSet<Character> symbolSet = new HashSet<Character>();

    public static enum TOKENTYPE {
        KEYWORD,
        SYMBOL,
        IDENTIFIER,
        INT_CONST,
        STRING_CONST
    }

    public static enum KEYWORD {
        CLASS,
        METHOD,
        FUNCTION,
        CONSTRUCTOR,
        INT,
        BOOLEAN,
        CHAR,
        VOID,
        VAR,
        STATIC,
        FIELD,
        LET,
        DO,
        IF,
        ELSE,
        WHILE,
        RETURN,
        TRUE,
        FALSE,
        NULL,
        THIS
    }

    // Initliaze's the keywordMap and the symbolSet
    static {
        keywordMap.put("this", KEYWORD.THIS);
        keywordMap.put("null", KEYWORD.NULL);
        keywordMap.put("false", KEYWORD.FALSE);
        keywordMap.put("true", KEYWORD.TRUE);
        keywordMap.put("return", KEYWORD.RETURN);
        keywordMap.put("while", KEYWORD.WHILE);
        keywordMap.put("else", KEYWORD.ELSE);
        keywordMap.put("if", KEYWORD.IF);
        keywordMap.put("do", KEYWORD.DO);
        keywordMap.put("let", KEYWORD.LET);
        keywordMap.put("field", KEYWORD.FIELD);
        keywordMap.put("static", KEYWORD.STATIC);
        keywordMap.put("var", KEYWORD.VAR);
        keywordMap.put("void", KEYWORD.VOID);
        keywordMap.put("char", KEYWORD.CHAR);
        keywordMap.put("boolean", KEYWORD.BOOLEAN);
        keywordMap.put("int", KEYWORD.INT);
        keywordMap.put("constructor", KEYWORD.CONSTRUCTOR);
        keywordMap.put("function", KEYWORD.FUNCTION);
        keywordMap.put("method", KEYWORD.METHOD);
        keywordMap.put("class", KEYWORD.CLASS);

        symbolSet.add('+');
        symbolSet.add('-');
        symbolSet.add('*');
        symbolSet.add('/');
        symbolSet.add('&');
        symbolSet.add('|');
        symbolSet.add('<');
        symbolSet.add('>');
        symbolSet.add('=');
        symbolSet.add('{');
        symbolSet.add('}');
        symbolSet.add('(');
        symbolSet.add(')');
        symbolSet.add('[');
        symbolSet.add(']');
        symbolSet.add('.');
        symbolSet.add(',');
        symbolSet.add(';');
        symbolSet.add('~');

    }

    // constructor, initliaze's the line and reader for the current instance
    public JackTokenizer(BufferedReader reader) {
        this.reader = reader;
        this.line = "";
    }

    // Checks if the current line is BLANK
    public boolean isBlank1() {
        for (int i = 0; i < line.length(); i++) {
            if (!Character.isWhitespace(line.charAt(i))) {
                return false;
            }
        }
        return true;
    }

    // Checks if there is more tokens to proccess
    public boolean hasMoreTokens() throws IOException {
        if (line != null && isBlank1()) {
            if (reader.ready()) {
                line = reader.readLine();
                while (!validLine() || isBlank1()) {
                    line = reader.readLine();
                    if (line == null) {
                        return false;
                    }
                }
                return true;

            }
        } else {

            return true;
        }
        return false;
    }

    // Advances to the next token
    public void advance() throws IOException {
        line = line.trim();
        cToken = "";
        for (int i = 0; i < line.length(); i++) {
            cToken += line.charAt(i);

            if (symbolSet.contains(cToken.charAt(0))) {
                cTokenType = TOKENTYPE.SYMBOL;
                if (line.length() == 1) {
                    line = "";
                } else if (i + 1 < line.length()) {
                    line = line.substring(i + 1, line.length());
                }
                break;
            } else if (keywordMap.containsKey(cToken) && (line.charAt(i + 1) == ' ' || line.charAt(i + 1) == ')' || line.charAt(i + 1) == ';')) {
                cTokenType = TOKENTYPE.KEYWORD;
                if (i + 1 < line.length())
                    line = line.substring(i + 1, line.length());
                break;
            } else if (cToken.matches("[0-9]+")) {
                cTokenType = TOKENTYPE.INT_CONST;
                int j = i + 1;
                while (j < line.length() && line.charAt(j) >= '0' && line.charAt(j) <= '9') {
                    cToken += line.charAt(j);
                    j++;
                }
                if (j < line.length())
                    line = line.substring(j, line.length());

                break;
            } else if (cToken.matches("[a-zA-Z_]*")) {
                if (symbolSet.contains(line.charAt(i + 1)) || line.charAt(i + 1) == ' ') {
                    cTokenType = TOKENTYPE.IDENTIFIER;

                    if (i + 1 < line.length())
                        line = line.substring(i + 1, line.length());

                    break;
                }

            } else if (cToken.matches("\"[^\"\n]*\"")) {
                cTokenType = TOKENTYPE.STRING_CONST;

                if (i + 1 < line.length())
                    line = line.substring(i + 1, line.length());

                break;
            }

        }

    }

    // Returns the TOKETYPE of the current token
    public TOKENTYPE tokenType() {
        return this.cTokenType;
    }

    // Returns the KEYWORD of the current token
    public KEYWORD keyWord() {
        if (cTokenType == TOKENTYPE.KEYWORD)
            return keywordMap.get(cToken);
        else {
            throw new IllegalStateException("Current token isnt a keyWord");
        }
    }

    // Return the string of the current token
    public String tokenValue() {
        return cToken;
    }

    // Returns the symbol
    public String symbol() {
        if (cTokenType == TOKENTYPE.SYMBOL) {
            switch (cToken) {
                case "<":
                    return "&lt;";
                case ">":
                    return "&gt;";
                case "&":
                    return "&amp;";
                case "\"":
                    return "&quot;";
                default:
                    return cToken;
            }
        } else {
            throw new IllegalStateException("Current token isnt a symbol");
        }
    }

    // Returns the identifier of the current token
    public String identifier() {
        if (cTokenType == TOKENTYPE.IDENTIFIER) {
            return cToken;
        } else {
            throw new IllegalStateException("Current token isnt a identifier");
        }
    }

    // Returns the int value of the current token
    public int intVal() {
        if (cTokenType == TOKENTYPE.INT_CONST) {
            return Integer.parseInt(cToken);
        } else {
            throw new IllegalStateException("Current token isnt a INT_CONST");
        }
    }

    // Return the string value of the current token
    public String stringVal() {
        if (cTokenType == TOKENTYPE.STRING_CONST) {
            return cToken.substring(1, cToken.length() - 1);
        } else {
            throw new IllegalStateException("Current token isnt a STRING_CONST");
        }
    }

    // Boolean function that returns true if the line is valid and false otherwise
    public boolean validLine() throws IOException {
        line = line.trim();
        if (line.equals("") || isBlank1()) {
            return false;
        }

        if (line.contains("/*") && line.contains("*/")) {
            return false;
        }

        if (line.contains("/*")) {
            spancomment = true;
            return false;
        }

        if (line.contains("*/") && spancomment) {
            spancomment = false;
            return false;
        }

        if (spancomment) {
            return false;
        }

        if (line.startsWith("//")) {
            return false;
        } else if (line.contains("//")) {
            line = line.substring(0, line.indexOf("//"));
            return true;
        }

        return true;
    }

    // Returns true if the token is a classVarDecType
    public boolean classVarDecTypes() {
        if (tokenType() == TOKENTYPE.KEYWORD) {
            if (keyWord() == KEYWORD.FIELD || keyWord() == KEYWORD.STATIC)
                return true;
        }
        return false;
    }

    // Returns true if the token is a subroutine type
    public boolean subRoutineTypes() {
        if (tokenType() == TOKENTYPE.KEYWORD) {
            if (keyWord() == KEYWORD.CONSTRUCTOR || keyWord() == KEYWORD.METHOD || keyWord() == KEYWORD.FUNCTION)
                return true;
        }
        return false;
    }

    // Returns true if the token is a statement type
    public boolean statementsTypes() {
        if (tokenType() == TOKENTYPE.KEYWORD) {
            if (keyWord() == KEYWORD.RETURN || keyWord() == KEYWORD.LET || keyWord() == KEYWORD.DO
                    || keyWord() == KEYWORD.IF || keyWord() == KEYWORD.WHILE) {
                return true;
            }
        }
        return false;
    }

    // Returns true if the token is a primitive variable type
    public boolean primitiveTypes() {
        if (tokenType() == TOKENTYPE.KEYWORD) {
            if (keyWord() == KEYWORD.INT || keyWord() == KEYWORD.CHAR || keyWord() == KEYWORD.BOOLEAN) {
                return true;
            }
        }
        return false;
    }

    // Returns true if the token is a unaryOp
    public boolean unaryOp() {
        if (tokenType() == TOKENTYPE.SYMBOL) {
            if (cToken.equals("-") || cToken.equals("~")) {
                return true;
            }
        }
        return false;
    }
}