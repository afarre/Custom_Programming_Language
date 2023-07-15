import Model.HashTableContent;
import Model.Token;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;

public class Semantic {

    private ArrayList<Token> tokenList;
    private HashMap<String, HashTableContent> symbolTable;
    private String testName;

    public Semantic(String testName) {
        this.tokenList = new ArrayList<>();
        this.symbolTable = new HashMap<>();
        this.testName = testName;
    }

    /**
     * Main method that checks the semantic of the code received
     */
    public void checkSemantic(){

        //We go through all the tokens
        int size = this.tokenList.size();
        int i = 0;

        while (i < size) {
            //We read until the semicolon or bracket token to identify what type of sentence is
            ArrayList<Token> sentence = new ArrayList<>();

            while (!tokenList.get(i).getType().equals(";") && !tokenList.get(i).getType().equals("opening_body") && !tokenList.get(i).getType().equals("closing_body")) {
                sentence.add(tokenList.get(i));
                i++;
            }

            i++;

            try {
                if (sentence.size() > 0) checkSentence(sentence);
            } catch (SemanticException e) {
                System.out.println(e.getMessage());
                System.exit(-1);
            }
        }

        System.out.println("Semantic OK");

        //Generate the intermediate code
        (new IntermediateCodeGenerator(this.tokenList, this.symbolTable)).generate(testName);

    }

    public void setTokenList(ArrayList<Token> tokenList) {
        this.tokenList = tokenList;
    }

    /**
     * Checks the first token of the sentence
     * @param sentence list of tokens from an specific line of the code
     * @throws SemanticException Exception thrown when Semantic scanner detects an error
     */
    private void checkSentence(ArrayList<Token> sentence) throws SemanticException {
        HashTableContent hashTableContent = new HashTableContent();
        if (sentence.get(0).getType().equals("type")) {
            checkDeclaration(sentence, hashTableContent);
        } else if (sentence.get(0).getType().equals("ID")) {
            checkAssignation(sentence);
        } else if (sentence.get(0).getType().equals("conditional")|| sentence.get(0).getType().equals("loop")) {
            checkCondition(sentence);
        }
    }

    /**
     * Adds a token to the symbol table in case it hasn't been declared before
     * @param sentence list of tokens from an specific line of the code
     * @param hashTableContent hash table containing
     * @throws SemanticException Exception thrown when Semantic scanner detects an error
     */
    private void checkDeclaration(ArrayList<Token> sentence, HashTableContent hashTableContent) throws SemanticException {
        //Check if the id has already been declared
        if (this.symbolTable.containsKey(sentence.get(1).getLexeme())) {
            //It is already declared, we throw an exception
            throw new SemanticException(sentence.get(1).getType(), sentence.get(1).getLexeme(), SemanticException.VAR_DECLARED);
        } else {
            //If not, we add it to the symbol table
            hashTableContent.setType(sentence.get(0).getLexeme());
            hashTableContent.setLoaded(false);
            this.symbolTable.put(sentence.get(1).getLexeme(), hashTableContent);
        }

        //Check if it is just a declaration or both declaration and assignation
        if (sentence.size() > 2 && sentence.get(2).getType().equals("=")) {
            checkVariable(sentence.get(1), sentence.get(3));
        }

        //Check if its an operation
        if (sentence.size() > 4 && sentence.get(4).getType().equals("op")) {
            checkVariable(sentence.get(1), sentence.get(5));
        }
    }

    private void checkAssignation(ArrayList<Token> sentence) throws SemanticException {
        //Check the first value
        checkVariable(sentence.get(0), sentence.get(2));

        //Check if its an operation
        if (sentence.size() > 3 && sentence.get(3).getType().equals("op")) {
            checkVariable(sentence.get(0), sentence.get(4));
        }
    }

    private void checkCondition(ArrayList<Token> sentence) throws SemanticException {
        //Check if the first value is a constant or a variable
        if (sentence.get(2).getType().equals("ID")) {
            //Check if the variable has been declared
            if (!this.symbolTable.containsKey(sentence.get(2).getLexeme())) {
                //If it is not declared, we throw an exception
                throw new SemanticException(sentence.get(2).getType(), sentence.get(2).getLexeme(), SemanticException.VAR_NOT_DECLARED);
            }
        }

        //Check the second value
        if (sentence.get(4).getType().equals("ID")) {
            //Check if the variable has been declared
            if (!this.symbolTable.containsKey(sentence.get(4).getLexeme())) {
                //If it is not declared, we throw an exception
                throw new SemanticException(sentence.get(4).getType(), sentence.get(4).getLexeme(), SemanticException.VAR_NOT_DECLARED);
            }
        }

        checkVariable(sentence.get(2), sentence.get(4));
    }

    private void checkVariable(Token origin, Token var) throws SemanticException {
        //Check if the new value is a constant or another variable
        if (!var.getType().equals("cte_entera") && !var.getType().equals("cte_booleana")) {
            //Check if the variable to assign has been declared
            if (!this.symbolTable.containsKey(var.getLexeme())) {
                //It is not declared, we throw an exception
                throw new SemanticException(var.getType(), var.getLexeme(), SemanticException.VAR_NOT_DECLARED);
            } else {
                //Check that both variables are the same type
                if (!this.symbolTable.get(origin.getLexeme()).getType().equals(this.symbolTable.get(var.getLexeme()).getType())) {
                    //It they have different types, we throw an exception
                    throw new SemanticException(this.symbolTable.get(origin.getLexeme()).getType(), origin.getLexeme(), SemanticException.INCOMPAT_TYPES);
                }
            }
        } else {
            //Check if is an int or a boolean
            if (this.symbolTable.get(origin.getLexeme()).getType().equals("int")) {
                if (var.getType().equals("cte_booleana")) {
                    //It they have different types, we throw an exception
                    throw new SemanticException(this.symbolTable.get(origin.getLexeme()).getType() , origin.getLexeme(), SemanticException.INCOMPAT_TYPES);
                }
            } else if (this.symbolTable.get(origin.getLexeme()).getType().equals("boolean")) {
                if (var.getType().equals("cte_entera")) {
                    //It they have different types, we throw an exception
                    throw new SemanticException(this.symbolTable.get(origin.getLexeme()).getType() , origin.getLexeme(), SemanticException.INCOMPAT_TYPES);
                }
            }
        }
    }
}
