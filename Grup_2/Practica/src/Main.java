import java.io.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Main {

    public static void main(String[] args) {
        /*File dir = new File("data/");

        for (File file : dir.listFiles()) {
            if (file.getName().endsWith(".txt")) {
                executeTest(file.getName());
            }
        }*/

        //executeTest("test_ok.txt");
        executeTest("test_final.txt");
        //executeTest("test_operacio_de_tres.txt");
        //executeTest("test_operacio_incompleta.txt");
        //executeTest("test_pre_asignacio.txt");
        //executeTest("test_doble_declaracio.txt");
        //executeTest("test_caracter_raro.txt");
        //executeTest("test_doble_equ.txt");
        //executeTest("test_doble_tipus.txt");
        //executeTest("test_sense_punt_i_coma.txt");
        //executeTest("test_tipus_erroni.txt");
        //executeTest("test_boolean.txt");
        //executeTest("test_while_if.txt");
        //executeTest("test_while.txt");
        //executeTest("test_boolean_int.txt");
        //executeTest("test_int_boolean.txt");
        //executeTest("test_main_erroneo.txt");
        //executeTest("test_if_boolean.txt");
        //executeTest("test_if_comparacio_single.txt");
        //executeTest("test_if_comparacio_double.txt");
    }

    /**
     * Method that executes an specific test file
     * @param testName String with the name of the test that is being executed
     */
    private static void executeTest(String testName) {
        System.out.println("\nEXECUTING TEST " + testName);
        System.out.println("---------------------------\n");
        try{
            InputStream is = new FileInputStream("data/tests/" + testName);
            BufferedReader buf = new BufferedReader(new InputStreamReader(is));

            String line = buf.readLine();
            StringBuilder sb = new StringBuilder();
            //Reads every line and looks for a semicolon at the end of each, then stores it adding a \n
            while(line != null){
                sb.append(line).append("\n");
                line = buf.readLine();
            }
            //Sends test code to lexic scanner and test name to Parser, removing its file name directory and format
            LexicScanner lexicScanner = new LexicScanner(sb.toString());
            testName = testName.replace("tests/", "");
            testName = testName.replace(".txt", "");
            new Parser(lexicScanner, testName);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
