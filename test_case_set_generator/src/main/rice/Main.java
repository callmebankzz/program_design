package main.rice;

import main.rice.basegen.BaseSetGenerator;
import main.rice.concisegen.ConciseSetGenerator;
import main.rice.parse.ConfigFile;
import main.rice.parse.ConfigFileParser;
import main.rice.parse.InvalidConfigException;
import main.rice.test.TestCase;
import main.rice.test.Tester;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public class Main {

    /**
     * Prints the concise test set along with a message specifying that it is the concise
     * test set.
     *
     * @param args the paths to the configuration file, directory of buggy files, and
     * solution file.
     * @throws IOException if a file couldn't be found at the given path
     * @throws InvalidConfigException if the configuration file is syntactically incorrect
     */
    public static void main(String[] args) throws IOException, InvalidConfigException, InterruptedException{
        System.out.println(
                "Generating the concise test set from the specifications in the "
                        + "configuration file");
        System.out.println(generateTests(args));
    }

    /**
     * Generates the concise test set to be printed in main.
     *
     * @param args the paths to the configuration file, directory of buggy files, and
     * * solution file.
     * @return a set of TestCases that are the concise test set for the specifications
     * @throws IOException if a file couldn't be found at the given path
     * @throws InvalidConfigException if the configuration file is syntactically incorrect
     */
    public static Set<TestCase> generateTests(String[] args)
            throws IOException, InvalidConfigException, InterruptedException {
        // path of the configuration file
        String conPath = args[0];
        // path of the directory
        String dirPath = args[1];
        // path to solutions
        String solPath = args[2];
        //Parse the configuration file and specifications
        ConfigFileParser parser = new ConfigFileParser();
        ConfigFile file = parser.parse(parser.readFile(conPath));

        //Generate the base test set
        BaseSetGenerator baseGen = new BaseSetGenerator(file.getNodes(), file.getNumRand());
        List<TestCase> baseSet = baseGen.genBaseSet();

        //Use the ConciseSetGenerator the base test set
        Tester tester = new Tester(file.getFuncName(), solPath, dirPath, baseSet);
        tester.computeExpectedResults();
        ConciseSetGenerator conciseGen = new ConciseSetGenerator();
        return conciseGen.setCover(tester.runTests());
    }
}