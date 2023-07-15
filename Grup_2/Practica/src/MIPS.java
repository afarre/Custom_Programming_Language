import Model.HashTableContent;
import Model.ThreeAddressCode;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class MIPS {
    private BufferedWriter bw;
    private String operation;
    private int operationFlag;

    private final static int EQUAL = 0;
    private final static int GREATER_THAN = 1;
    private final static int LESSER_THAN = 2;

    /**
     * Constructor that creates the .asm file
     * @param testName name of the test file containing the code
     * @param intermediateCode ArrayList containing the intermediate code information for MIPS generation
     * @param symbolTable Hashmap containing the symbol table of our language
     */
    public MIPS(String testName, ArrayList<ThreeAddressCode> intermediateCode, HashMap<String, HashTableContent> symbolTable) {
        try {
            bw = new BufferedWriter(new FileWriter("data/asm/" + testName + ".asm"));
            bw.write("\t.data\t#declare storage for variables\n");
            for (int i = 0; i < intermediateCode.size(); i++){
                //bw.write("var" + (i + 1) + ":\t.word 0\n");
                if (symbolTable.containsKey(intermediateCode.get(i).getResult()) && !symbolTable.get(intermediateCode.get(i).getResult()).isLoaded()){
                    bw.write("var" + intermediateCode.get(i).getResult() + ":\t.word 0\n");
                    symbolTable.get(intermediateCode.get(i).getResult()).setLoaded(true);
                }
            }
            bw.write("\n\t.text\n");
            //bw.write("main:\n");
            for (ThreeAddressCode intermediate: intermediateCode) {
                switch (intermediate.getOperator()){
                    case "=":
                        setVariable(intermediate.getResult(), intermediate.getArg1());
                        break;

                    case "+":
                        sumVariable(intermediate.getResult(), intermediate.getArg1(), intermediate.getArg2());
                        break;

                    case "-":
                        subtractVariable(intermediate.getResult(), intermediate.getArg1(), intermediate.getArg2());
                        break;

                    case "*":
                        multiplyVariable(intermediate.getResult(), intermediate.getArg1(), intermediate.getArg2());
                        break;

                    case "/":
                        divideVariable(intermediate.getResult(), intermediate.getArg1(), intermediate.getArg2());
                        break;

                    case "end_if":
                        finishIf(intermediate.getResult());
                        break;

                    case "if":
                        buildIf(intermediate.getResult());
                        break;

                    case "goto":
                        bw.write("\tb " + intermediate.getResult() + "\n\n");
                        break;

                    case "init_while_label":
                        bw.write("\t" + intermediate.getResult() + ":\n\n");
                        break;

                    case "end_while":
                        bw.write("\tFin" + intermediate.getResult() + ":\n\n");
                        break;

                    case "==":
                        generateComparatorConditionalBody(intermediate.getArg1(), intermediate.getArg2());
                        operationFlag = EQUAL;
                        break;

                    case ">":
                        generateComparatorConditionalBody(intermediate.getArg1(), intermediate.getArg2());
                        operationFlag = GREATER_THAN;
                        break;

                    case "<":
                        generateComparatorConditionalBody(intermediate.getArg1(), intermediate.getArg2());
                        operationFlag = LESSER_THAN;
                        break;
                }
            }
            closeFile();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Introduces finalization token for flag
     * @param lexeme Name of the flag used to decide if finalisation token has already been added or not
     */
    private void finishIf(String lexeme) {
        try {
            bw.write("\tFin" + lexeme + ":\n\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Builds "beq" instruction for MIPS assembly
     * @param lexeme name of tag used in MIPS code for this conditional
     */
    private void buildIf(String lexeme) {
        try {
            if (operationFlag == EQUAL){
                bw.write("\tbeq " + operation + lexeme + "\t\t#branch if first parameter == second parameter\n\n");
            }else  if (operationFlag == GREATER_THAN){
                bw.write("\tbgt " + operation + lexeme + "\t\t#branch if first parameter > second parameter\n\n");
            }else if (operationFlag == LESSER_THAN){
                bw.write("\tblt " + operation + lexeme + "\t\t#branch if first parameter < second parameter\n\n");
            }
            bw.write("\t#Space for else body (non-usable for while statements)\n\n");
            bw.write("\tb Fin" + lexeme + "\t\t#go to flag Fin" + lexeme + " if condition is not met\n\n");
            bw.write("\t" + lexeme + ":\n\n");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Generates comparison body to be placed in the "beq" instruction
     * @param value1 First value
     * @param value2 Second value
     */
    private void generateComparatorConditionalBody(String value1, String value2) {
        try {
            if (value1.matches("[0-9]+") && value2.matches("[0-9]+")) {
                bw.write("\tli $t0," + Integer.parseInt(value1) + "\t\t#load integer: loads the an integer to the specified register\n");
                bw.write("\tli $t1," + Integer.parseInt(value2) + "\t\t#load integer: loads the an integer to the specified register\n");
            } else if (value1.matches("[0-9]+")) {
                bw.write("\tli $t0," + Integer.parseInt(value1) + "\t\t#load integer: loads the an integer to the specified register\n");
                bw.write("\tsw $t1,var" + value2 + "\t\t#store word: store word from source register into RAM destination\n");
            } else if (value2.matches("[0-9]+")) {
                bw.write("\tsw $t0,var" + value1 + "\t\t#store word: store word from source register into RAM destination\n");
                bw.write("\tli $t1," + Integer.parseInt(value2) + "\t\t#load integer: loads the an integer to the specified register\n");
            }
            operation = "$t0,$t1,";
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    /**
     * Method that subtracts two values into the lexeme
     * @param lexeme lexeme to which the final value will be stored
     * @param value1 First value
     * @param value2 Second value
     */
    private void subtractVariable(String lexeme, String value1, String value2) {
        try {
            if (value1.matches("[0-9]+") && value2.matches("[0-9]+")){
                bw.write("\tli $t0," + Integer.parseInt(value1) + "\t\t#load integer: loads the an integer to the specified register\n");
                bw.write("\tli $t1," + Integer.parseInt(value2) + "\t\t#load integer: loads the an integer to the specified register\n");
                bw.write("\tsub $t2,$t0,$t1\t\t#substracts two registers and stores the result in the first register\n");
                bw.write("\tsw $t2,vart2\t\t#store word: store word from source register into RAM destination\n\n");
            }else if (value1.matches("[0-9]+")){
                bw.write("\tli $t0," + Integer.parseInt(value1) + "\t\t#load integer: loads the an integer to the specified register\n");
                bw.write("\tlw $t1,var" + value2 + "\t\t#load address: loads the memory address from RAM destination to the specified variable\n");
                bw.write("\tsub $t2,$t0,$t1\t\t#substracts two registers and stores the result in the first register\n");
                bw.write("\tsw $t2,vart2\t\t#store word: store word from source register into RAM destination\n\n");
            }else if (value2.matches("[0-9]+")){
                bw.write("\tli $t0," + Integer.parseInt(value2) + "\t\t#load integer: loads the an integer to the specified register\n");
                bw.write("\tlw $t1,var" + value1 + "\t\t#load address: loads the memory address from RAM destination to the specified variable\n");
                bw.write("\tsub $t2,$t0,$t1" + "\t\t#substracts two registers and stores the result in the first register\n");
                bw.write("\tsw $t2,vart2\t\t#store word: store word from source register into RAM destination\n\n");
            }else {
                bw.write("\tlw $t0,var" + value1 + "\t\t#load address: loads the memory address from RAM destination to the specified variable\n");
                bw.write("\tlw $t1,var" + value2 + "\t\t#load address: loads the memory address from RAM destination to the specified variable\n");
                bw.write("\tsub $t2,$t0,$t1\t\t#substracts two registers and stores the result in the first register\n");
                bw.write("\tsw $t2,vart2\t\t#store word: store word from source register into RAM destination\n\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that sums two values into the lexeme
     * @param lexeme lexeme to which the final value will be stored
     * @param value1 First value
     * @param value2 Second value
     */
    private void sumVariable(String lexeme, String value1, String value2) {
        try {
            if (value1.matches("[0-9]+") && value2.matches("[0-9]+")){
                bw.write("\tli $t0," + Integer.parseInt(value1) + "\t\t#load integer: loads the an integer to the specified register\n");
                bw.write("\tli $t1," + Integer.parseInt(value2) + "\t\t#load integer: loads the an integer to the specified register\n");
                bw.write("\tadd $t2,$t0,$t1\t\t#adds two registers and stores the result in the first register\n");
                bw.write("\tsw $t2,vart2\t\t#store word: store word from source register into RAM destination\n\n");
            }else if (value1.matches("[0-9]+")){
                bw.write("\tli $t0," + Integer.parseInt(value1) + "\t\t#load integer: loads the an integer to the specified register\n");
                bw.write("\tlw $t1,var" + value2 + "\t\t#load address: loads the memory address from RAM destination to the specified variable\n");
                bw.write("\tadd $t2,$t0,$t1\t\t#adds two registers and stores the result in the first register\n");
                bw.write("\tsw $t2,vart2\t\t#store word: store word from source register into RAM destination\n\n");
            }else if (value2.matches("[0-9]+")){
                bw.write("\tli $t0," + Integer.parseInt(value2) + "\t\t#load integer: loads the an integer to the specified register\n");
                bw.write("\tlw $t1,var" + value1 + "\t\t#load address: loads the memory address from RAM destination to the specified variable\n");
                bw.write("\tadd $t2,$t0,$t1" + "\t\t#adds two registers and stores the result in the first register\n");
                bw.write("\tsw $t2,vart2\t\t#store word: store word from source register into RAM destination\n\n");
            }else {
                bw.write("\tlw $t0,var" + value1 + "\t\t#load address: loads the memory address from RAM destination to the specified variable\n");
                bw.write("\tlw $t1,var" + value2 + "\t\t#load address: loads the memory address from RAM destination to the specified variable\n");
                bw.write("\tadd $t2,$t0,$t1\t\t#adds two registers and stores the result in the first register\n");
                bw.write("\tsw $t2,vart2\t\t#store word: store word from source register into RAM destination\n\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that multiplies two values into the lexeme
     * @param lexeme lexeme to which the final value will be stored
     * @param value1 First value
     * @param value2 Second value
     */
    private void multiplyVariable(String lexeme, String value1, String value2) {
        try {
            if (value1.matches("[0-9]+") && value2.matches("[0-9]+")){
                bw.write("\tli $t0," + Integer.parseInt(value1) + "\t\t#load integer: loads the an integer to the specified register\n");
                bw.write("\tli $t1," + Integer.parseInt(value2) + "\t\t#load integer: loads the an integer to the specified register\n");
                bw.write("\tmult $t2,$t0,$t1\t\t#multiplies two registers and stores the result in the first register\n");
                bw.write("\tsw $t2,vart2\t\t#store word: store word from source register into RAM destination\n\n");
            }else if (value1.matches("[0-9]+")){
                bw.write("\tli $t0," + Integer.parseInt(value1) + "\t\t#load integer: loads the an integer to the specified register\n");
                bw.write("\tlw $t1,var" + value2 + "\t\t#load address: loads the memory address from RAM destination to the specified variable\n");
                bw.write("\tmult $t2,$t0,$t1\t\t#multiplies two registers and stores the result in the first register\n");
                bw.write("\tsw $t2,vart2\t\t#store word: store word from source register into RAM destination\n\n");
            }else if (value2.matches("[0-9]+")){
                bw.write("\tli $t0," + Integer.parseInt(value2) + "\t\t#load integer: loads the an integer to the specified register\n");
                bw.write("\tlw $t1,var" + value1 + "\t\t#load address: loads the memory address from RAM destination to the specified variable\n");
                bw.write("\tmult $t2,$t0,$t1" + "\t\t#multiplies two registers and stores the result in the first register\n");
                bw.write("\tsw $t2,vart2\t\t#store word: store word from source register into RAM destination\n\n");
            }else {
                bw.write("\tlw $t0,var" + value1 + "\t\t#load address: loads the memory address from RAM destination to the specified variable\n");
                bw.write("\tlw $t1,var" + value2 + "\t\t#load address: loads the memory address from RAM destination to the specified variable\n");
                bw.write("\tmult $t2,$t0,$t1\t\t#multiplies two registers and stores the result in the first register\n");
                bw.write("\tsw $t2,vart2\t\t#store word: store word from source register into RAM destination\n\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that divides two values into the lexeme
     * @param lexeme lexeme to which the final value will be stored
     * @param value1 First value
     * @param value2 Second value
     */
    private void divideVariable(String lexeme, String value1, String value2) {
        try {
            if (value1.matches("[0-9]+") && value2.matches("[0-9]+")){
                bw.write("\tli $t0," + Integer.parseInt(value1) + "\t\t#load integer: loads the an integer to the specified register\n");
                bw.write("\tli $t1," + Integer.parseInt(value2) + "\t\t#load integer: loads the an integer to the specified register\n");
                bw.write("\tdiv $t2,$t0,$t1\t\t#divides two registers and stores the result in the first register\n");
                bw.write("\tsw $t2,vart2\t\t#store word: store word from source register into RAM destination\n\n");
            }else if (value1.matches("[0-9]+")){
                bw.write("\tli $t0," + Integer.parseInt(value1) + "\t\t#load integer: loads the an integer to the specified register\n");
                bw.write("\tlw $t1,var" + value2 + "\t\t#load address: loads the memory address from RAM destination to the specified variable\n");
                bw.write("\tdiv $t2,$t0,$t1\t\t#divides two registers and stores the result in the first register\n");
                bw.write("\tsw $t2,vart2\t\t#store word: store word from source register into RAM destination\n\n");
            }else if (value2.matches("[0-9]+")){
                bw.write("\tli $t0," + Integer.parseInt(value2) + "\t\t#load integer: loads the an integer to the specified register\n");
                bw.write("\tlw $t1,var" + value1 + "\t\t#load address: loads the memory address from RAM destination to the specified variable\n");
                bw.write("\tdiv $t2,$t0,$t1" + "\t\t#divides two registers and stores the result in the first register\n");
                bw.write("\tsw $t2,vart2\t\t#store word: store word from source register into RAM destination\n\n");
            }else {
                bw.write("\tlw $t0,var" + value1 + "\t\t#load address: loads the memory address from RAM destination to the specified variable\n");
                bw.write("\tlw $t1,var" + value2 + "\t\t#load address: loads the memory address from RAM destination to the specified variable\n");
                bw.write("\tdiv $t2,$t0,$t1\t\t#divides two registers and stores the result in the first register\n");
                bw.write("\tsw $t2,vart2\t\t#store word: store word from source register into RAM destination\n\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that initializes token with the value passed by parameter
     * @param lexeme token that is being updated
     * @param value Value assigned to the token
     */
    private void setVariable(String lexeme, String value) {
        try {
            if (value.matches("[0-9]+")){
                bw.write("\tli $t0," + value + "\t\t#load integer: loads the an integer to the specified register" + "\n");
                bw.write("\tsw $t0,var" + lexeme + "\t\t#store word: store word from source register into RAM destination\n\n");
            }else if (!value.equals("true") && !value.equals("false")){
                bw.write("\tlw $t0,var" + value + "\t\t#load address: loads the memory address from RAM destination to the specified variable\n");
                bw.write("\tsw $t0,var" + lexeme + "\t\t#store word: store word from source register into RAM destination\n\n");
            }else {
                switch (value){
                    case "false":
                        bw.write("\tli $t0,0\t\t#load integer: loads the an integer to the specified register" + "\n");
                        bw.write("\tsw $t0,var" + lexeme + "\t\t#store word: store word from source register into RAM destination\n\n");
                        break;

                    case "true":
                        bw.write("\tli $t0,1\t\t#load integer: loads the an integer to the specified register" + "\n");
                        bw.write("\tsw $t0,var" + lexeme + "\t\t#store word: store word from source register into RAM destination\n\n");
                        break;
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * Method that closes the .asm file
     */
    private void closeFile() {
        try {
            bw.write("end:");
            bw.close();
            System.out.println("Assembler file generated successfully.");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
