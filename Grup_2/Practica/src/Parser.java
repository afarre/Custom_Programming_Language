import Model.Token;

import java.util.ArrayList;


public class Parser {

    private static LexicScanner lexicScanner;
    private static int tokenIndex;
    private static Semantic semantic;

    public Parser(LexicScanner lexicScanner, String testName) {
        Parser.lexicScanner = lexicScanner;
        Parser.semantic = new Semantic(testName);
        mainParser();

    }

    /**
     * Method that executes the main function which parses the code read from the test file
     */
    private void mainParser() {
        for (tokenIndex = 0; tokenIndex < lexicScanner.tokenListSize(); tokenIndex++){

            try {
                Accept("void");
                Accept("main");
                Accept("opening_conditioner");
                Accept("closing_conditioner");
                Accept("opening_body");
                Dec_Cte_Var();
                Accept("closing_body");
            } catch (ParserException e) {
                System.out.println("Error al declarar el main");
                System.exit(-1);
            }

        }

        System.out.println("Parser OK");
        semantic.setTokenList(lexicScanner.getTokenList());
        semantic.checkSemantic();
    }

    /**
     * Method that expects to read a constant declaration with/without initialization
     * @throws ParserException Exception thrown when parser detects a parse error
     */
    private static void Dec_Cte_Var() throws ParserException {
        try {
            Accept("type");
            Accept("ID");

            switch(lexicScanner.getToken(tokenIndex).getType()) {
                //Variable declaration
                case ";":
                    Accept(";");
                    checkNextToken();
                    break;
                //Variable declaration and assigment
                case "=":
                    Accept("=");
                    checkDeclaration();
                    Accept(";");
                    checkNextToken();
                    break;
                default:
                    break;
            }
        } catch(ParserException e) {
            String message = e.getMessage();
            System.out.println(message);
            System.exit(-1);
        }
    }

    /**
     * Method that expects to read a constant assignment or initialization
     * @throws ParserException Exception thrown when parser detects a parse error
     */
    private static void Equ_Cte_Var() throws ParserException {
        try {
            Accept("ID");
            Accept("=");
            checkDeclaration();

            Accept(";");

            checkNextToken();

        } catch(ParserException e) {
            String message = e.getMessage();
            System.out.println(message);
            System.exit(-1);
        }
    }

    /**
     * Method that checks if an ID, boolean or integer is being assigned
     * @throws ParserException Exception thrown when parser detects a parse error
     */
    private static void checkDeclaration() throws ParserException {
        switch (lexicScanner.getToken(tokenIndex).getType()) {

            //Integer assignment
            case "cte_entera":
                Accept("cte_entera");
                checkOperand();
                break;
            //ID assignment
            case "ID":
                Accept("ID");
                checkOperand();
                break;
            case "cte_booleana":
                Accept("cte_booleana");
                break;
            /*
            case "closing_body":
                Accept("}");
                break;
            */

            default:
                Token t = lexicScanner.getToken(tokenIndex);
                throw new ParserException(t.getType(), t.getLexeme(), "cte_entera", "ID");
        }
    }

    /**
     * Method that checks whether there's an operation
     * If there's an operation, such as + or - , check's that it's done with a valid type
     * @throws ParserException Exception thrown when parser detects a parse error
     */
    private static void checkOperand() throws ParserException {
        if (lexicScanner.getToken(tokenIndex).getType().equals("op")) {
            Accept("op");
            switch (lexicScanner.getToken(tokenIndex).getType()) {
                case "cte_entera":
                    Accept("cte_entera");
                    break;

                case "ID":
                    Accept("ID");
                    break;
                default:
                    Token t = lexicScanner.getToken(tokenIndex);
                    throw new ParserException(t.getType(), t.getLexeme(), "cte_entera", "ID");
            }
        }
    }

    private static void conditional() {
        try {
            Accept("conditional");
            checkBody();
        } catch (ParserException e) {
            System.out.println(e.message);
            System.exit(-1);
        }

    }

    private static void Bucle() {
        try {
            Accept("loop");
            checkBody();
        } catch (ParserException e) {
            System.out.println(e.message);
            System.exit(-1);
        }
    }

    private static void checkBody() throws ParserException {
        Accept("opening_conditioner");

        if (lexicScanner.getToken(tokenIndex).getType().equals("ID")){
            Accept("ID");
        } else if (lexicScanner.getToken(tokenIndex).getType().equals("cte_entera")){
            Accept("cte_entera");
        }
        Accept("op_logic");
        if (lexicScanner.getToken(tokenIndex).getType().equals("ID")){
            Accept("ID");
        }else if (lexicScanner.getToken(tokenIndex).getType().equals("cte_entera")){
            Accept("cte_entera");
        }
        Accept("closing_conditioner");
        Accept("opening_body");

        //tractar contingut del bucle
        checkNextToken();

        Accept("closing_body");

        checkNextToken();
    }

    /**
     * Method that checks if the next token read is an ID or a type token
     * @throws ParserException Exception thrown when parser detects a parse error
     */
    private static void checkNextToken() throws ParserException {
        if ((tokenIndex < lexicScanner.tokenListSize())){
            switch(lexicScanner.getToken(tokenIndex).getType()) {
                case "type":
                    Dec_Cte_Var();
                    break;

                case "ID":
                    Equ_Cte_Var();
                    break;

                case "conditional":
                    conditional();
                    break;

                case "loop":
                    Bucle();
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * Method that checks if the token type received equals the token type expected
     * @param sToken Token that is being scanned
     * @throws ParserException Exception thrown when parser detects a parse error
     */
    private static void Accept(String sToken) throws ParserException {
        if (tokenIndex < lexicScanner.tokenListSize()) {
            Token t = lexicScanner.getToken(tokenIndex);
            tokenIndex++;

            if (!t.getType().equals(sToken)) throw new ParserException(t.getType(), t.getLexeme(), sToken);

        }else {
            throw new ParserException(sToken);
        }
    }
}
