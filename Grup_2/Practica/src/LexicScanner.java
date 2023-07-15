import Model.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LexicScanner {
    private String code;
    private ArrayList<Token> tokenList;
    private ArrayList<String> codeList;
    private int codeIndex;

    /**
     * Constructor that checks for invalid characters
     * @param code String with the code read from the tests file
     */
    public LexicScanner(String code) {
        codeIndex = 0;
        tokenList = new ArrayList<>();
        codeList = new ArrayList<>();

        //Check if there are any invalid characters
        String pattern = "[^A-Za-z0-9;{}()=><+*/\\-\\s]+";

        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(code);
        //If an invalid character is found, shows an error message
        if (m.find()) {
            System.out.println("Lexical error in char: " + m.group() + "\nTerminating compilation.");
            System.exit(0);
        }else {
            this.code = code;
        }

        generateCodeArray();
    }

    /**
     * Creates an array with each cell containing one word or character from the code
     */

    private void generateCodeArray() {
        //Separate the code in lines (the line delimiter is '\n')
        String[] splitted = code.split("[\n]");
        codeList.addAll(Arrays.asList(splitted));
        createTokens();
    }

    private void createTokens() {
        Token t = null;
        int i;

        for (String line : codeList) {
            i = 0;

            //Parse every line in order to obtain the tokens
            while (i < line.length()) {
                StringBuilder word = new StringBuilder();

                //Split the line by spaces or semicolon

                while (i < line.length() && line.charAt(i) != ' ' && line.charAt(i) != ';' && line.charAt(i) != '{' && line.charAt(i) != '}') {
                    word.append(line.charAt(i));
                    i++;
                }

                //We compare the word with our tokens
                try {
                    Integer.parseInt(word.toString());
                    t = new Token(word.toString(), "cte_entera");
                } catch (NumberFormatException e) {
                    switch (word.toString()){
                        case "int":
                        case "boolean":
                            t = new Token(word.toString(), "type");
                            break;
                        case "true":
                        case "false" :
                            t = new Token(word.toString(),"cte_booleana");
                            break;
                        case "if":
                            t = new Token(word.toString(), "conditional");
                            break;
                        case "while":
                            t = new Token(word.toString(), "loop");
                            break;
                        case "==":
                        case ">":
                        case "<":
                            t = new Token(word.toString(), "op_logic");
                            break;
                        case "=":
                            t = new Token(word.toString(), "=");
                            break;
                        case "+":
                        case "-":
                        case "*":
                        case "/":
                            t = new Token(word.toString(), "op");
                            break;
                        case "(":
                            t = new Token(word.toString(), "opening_conditioner");
                            break;
                        case ")":
                            t = new Token(word.toString(), "closing_conditioner");
                            break;
                        case "{":
                            t = new Token(word.toString(), "opening_body");
                            break;
                        case "}":
                            t = new Token(word.toString(), "closing_body");
                            break;

                        case "void":
                            t = new Token(word.toString(), "void");
                            break;

                        case "main":
                            t = new Token(word.toString(), "main");
                            break;
                        default:
                            if (word.toString().equals(" ") || word.toString().equals("")){
                                t = new Token("EMPTY", "EMPTY");
                                break;
                            }

                            if (word.toString().length() > 0 && word.toString().matches("[A-Za-z]+")) {
                                t = new Token(word.toString(), "ID");
                            } else {
                                System.out.println("Token not recognized! Please check spelling in token " + word.toString());
                                System.exit(-1);
                            }
                            break;
                    }
                }

                if (!t.getLexeme().equals("EMPTY")){
                    tokenList.add(t);
                }

                if (i < line.length()) {
                    //If there is a semicolon, we create its token
                    if (line.charAt(i) == ';') {
                        tokenList.add(new Token(";", ";"));
                    } else if (line.charAt(i) == '{') {
                        tokenList.add(new Token("{", "opening_body"));
                    } else if (line.charAt(i) == '}') {
                        tokenList.add(new Token("}", "closing_body"));
                    }
                }

                i++;
            }
        }
        System.out.println("Lexic OK");
    }

    /**
     * Reads from codeList to generate a token if possible
     * @return Token generated with lexeme and type
     * @param i position of the token in the array of tokens
     */
    public Token getToken(int i) {
        return this.tokenList.get(i);
    }

    public int tokenListSize() {
        return this.tokenList.size();
    }

    public ArrayList<Token> getTokenList() {
        return tokenList;
    }
}
