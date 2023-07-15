package Model;

public class Token {
    private String lexeme;
    private String type;

    public Token(String lexeme, String type) {
        this.lexeme = lexeme;
        this.type = type;

    }

    public String getLexeme() {
        return lexeme;
    }

    public void setLexeme(String lexeme) {
        this.lexeme = lexeme;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }
}
