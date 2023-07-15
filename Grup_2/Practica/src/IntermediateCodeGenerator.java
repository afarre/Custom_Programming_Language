import Model.HashTableContent;
import Model.StackContent;
import Model.ThreeAddressCode;
import Model.Token;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Stack;

public class IntermediateCodeGenerator {

    private ArrayList<ThreeAddressCode> intermediateCode;
    private ArrayList<Token> tokenList;
    private HashMap<String, HashTableContent> symbolTable;
    private int labelCount;
    private Stack<StackContent> labelStack;

    public IntermediateCodeGenerator(ArrayList<Token> tokenList, HashMap<String, HashTableContent> symbolTable) {
        this.tokenList = tokenList;
        this.intermediateCode = new ArrayList<>();
        this.symbolTable = symbolTable;
        this.labelCount = 0;
        this.labelStack = new Stack<>();
    }

    public void generate(String testName) {
        int i = 0, size;

        ArrayList<Token> line = new ArrayList<>();

        size = tokenList.size();
        while (i < size) {
            while (!tokenList.get(i).getType().equals(";") && !tokenList.get(i).getType().equals("opening_body") && !tokenList.get(i).getType().equals("closing_body")) {
                //Read until the end of a line (semicolon)
                line.add(tokenList.get(i++));
            }
            line.add(tokenList.get(i));

            //Generate the three address codes from the line
            generateTACs(line);

            //Clear the line
            line.clear();
            i++;
        }

        for (ThreeAddressCode tac:
             this.intermediateCode) {
        }
        new MIPS(testName, intermediateCode, symbolTable);
    }

    private void generateTACs(ArrayList<Token> line) {
        //Identify if it is a conditional, a loop or an operation
        switch (line.get(0).getType()) {
            case "loop":
                generateLoop(line);
                break;
            case "conditional":
                generateConditional(line);
                break;
            case "closing_body":
                //Create the ending label TAC of the last conditional/loop opened
                if (!this.labelStack.empty()) {
                    StackContent content = this.labelStack.pop();

                    //Check if it is the label of a conditional or a loop
                    if (content.getType().equals("conditional")) {
                        ThreeAddressCode tac = new ThreeAddressCode();
                        tac.setOperator("end_if");
                        tac.setResult(content.getClosingLabel());
                        this.intermediateCode.add(tac);
                    } else {
                        //Create the goto too
                        ThreeAddressCode tacGoto = new ThreeAddressCode();
                        tacGoto.setOperator("goto");
                        tacGoto.setResult(content.getStartingLabel());

                        ThreeAddressCode tac = new ThreeAddressCode();
                        tac.setOperator("end_while");
                        tac.setResult(content.getClosingLabel());

                        this.intermediateCode.add(tacGoto);
                        this.intermediateCode.add(tac);
                    }

                }
                break;
            default:
                generateOperation(line);
        }
    }

    private void generateOperation(ArrayList<Token> line) {
        int i = 0, size;

        size = line.size();

        //Move to the equal or the end
        while (i < size && !line.get(i).getType().equals("=")) i++;

        //If we found an equal, we create the three address code
        if (i < size) {
            ThreeAddressCode tac = new ThreeAddressCode();

            //Check if it is an assignation or an operation
            if (line.get(i+2).getType().equals(";")) {
                //Create the three address code of the assignation
                tac.setOperator("=");
                tac.setArg1(line.get(i+1).getLexeme());
                tac.setResult(line.get(i-1).getLexeme());

                this.intermediateCode.add(tac);
            } else if (line.get(i+2).getType().equals("op")){
                //Create as many three address code as needed
                tac.setOperator(line.get(i+2).getLexeme());
                tac.setArg1(line.get(i+1).getLexeme());
                tac.setArg2(line.get(i+3).getLexeme());

                //Create the temp
                //String temp = "t" + this.tempCount;
                String temp = "t2";
                tac.setResult(temp);

                //Add the operation
                this.intermediateCode.add(tac);

                ThreeAddressCode tac2 = new ThreeAddressCode("=", temp, null,line.get(i-1).getLexeme());

                //Add the assignation
                this.intermediateCode.add(tac2);
            }

        }
    }

    private void generateConditional(ArrayList<Token> line) {
        //Create the label and add it to the stack
        String label = "I" + this.labelCount++;

        StackContent content = new StackContent("conditional", label, null);
        this.labelStack.push(content);

        //Create the TAC of the condition
        ThreeAddressCode tac = new ThreeAddressCode();
        tac.setOperator(line.get(3).getLexeme());
        tac.setArg1(line.get(2).getLexeme());
        tac.setArg2(line.get(4).getLexeme());

        //Create the temp
        String temp = "t2";
        tac.setResult(temp);

        //Create the TAC of the conditional
        ThreeAddressCode tac2 = new ThreeAddressCode();
        tac2.setOperator("if");
        tac2.setResult(label);

        //Add the TACs
        this.intermediateCode.add(tac);
        this.intermediateCode.add(tac2);
    }

    private void generateLoop(ArrayList<Token> line) {
        //Create the three labels needed
        String startingLabel = "L" + ++this.labelCount;
        String conditionLabel = "L" + ++this.labelCount;
        String closingLabel = "L" + this.labelCount;

        //Add the labels to the stack
        StackContent content = new StackContent("loop", closingLabel, startingLabel);
        this.labelStack.push(content);

        //Create the TAC of the condition
        ThreeAddressCode tacCondition = new ThreeAddressCode();
        tacCondition.setOperator(line.get(3).getLexeme());
        tacCondition.setArg1(line.get(2).getLexeme());
        tacCondition.setArg2(line.get(4).getLexeme());

        //Create the TAC of the starting label
        ThreeAddressCode tacStartingLabel = new ThreeAddressCode();
        tacStartingLabel.setOperator("init_while_label");
        tacStartingLabel.setResult(startingLabel);


        //Create the temp
        String temp = "t2";
        tacCondition.setResult(temp);

        //Create the TAC of the conditional tr (to repeat the loop if needed)
        ThreeAddressCode tacConditional = new ThreeAddressCode();
        tacConditional.setOperator("if");
        tacConditional.setArg1(temp);
        tacConditional.setResult(conditionLabel);

        ThreeAddressCode tacExit = new ThreeAddressCode();
        tacExit.setOperator("end_while");
        tacExit.setResult(closingLabel);
/*
        //Create the TAC for the label used when the condition is true
        ThreeAddressCode tacCondTrue = new ThreeAddressCode();
        tacCondTrue.setOperator("label");
        tacCondTrue.setResult(conditionLabel);

         */

        //Add the TACs
        this.intermediateCode.add(tacStartingLabel);
        this.intermediateCode.add(tacCondition);
        this.intermediateCode.add(tacConditional);

        /*

        this.intermediateCode.add(tacCondTrue);

         */
    }
}
