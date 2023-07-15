

public class ParserException extends Exception {
    public String message;
    private String eToken;
    private String eLexema;

    public ParserException(String sToken){
        this.message = "Error! Se esperaba el token " + sToken + " y no se ha encontrado nada.";
    }

    public ParserException(String token, String lexema, String sToken){
        this.eToken = token;
        this.eLexema = lexema;
        this.message = "Error! Se esperaba el token " + sToken + " y se ha encontrado el token " + token + " y lexema " + lexema;
    }

    public ParserException(String token, String lexema, String token1, String token2){
        this.eToken = token;
        this.eLexema = lexema;
        this.message = "Error! Se esperaba el token " + token1 + " o el token " + token2 + " y se ha encontrado el token " + token + " y lexema " + lexema;
    }


    public ParserException(String token, String lexema, String token1, String token2, String token3){
        this.eToken = token;
        this.eLexema = lexema;
        this.message = "Error! Se esperaba el token " + token1 + " o el token " + token2 + "o el token" + token3 +  " y se ha encontrado el token " + token + " y lexema " + lexema;
    }

    public String getErrorToken() {
        return eToken;
    }

    public String getErrorLexema() {
        return eLexema;
    }

    @Override
    public String getMessage(){
        return message;
    }
}

