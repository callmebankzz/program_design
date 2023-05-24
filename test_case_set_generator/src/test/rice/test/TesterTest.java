package test.rice.test;

import main.rice.obj.*;
import main.rice.test.TestCase;
import main.rice.test.TestResults;
import main.rice.test.Tester;
import org.junit.jupiter.api.*;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;

/**
 * Test cases for the Tester class.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class TesterTest {

    /**
     * The absolute path to this project directory, which we'll use to find the provided
     * pyfiles.
     */
    private static String userDir = System.getProperty("user.dir");

    /**
     * Lists of test cases for each of the functions under test (function definitions
     * themselves can be found in the test.rice.test.pyfiles package).
     */
    private static List<TestCase> f0Tests = new ArrayList<>();
    private static List<TestCase> f1Tests = new ArrayList<>();
    private static List<TestCase> f2Tests = new ArrayList<>();
    private static List<TestCase> f3Tests = new ArrayList<>();

    /**
     * Expected contents of expected.py for f3 tests.
     */
    private static String f3resultStr;

    /**
     * Sets up all test cases for all functions under test.
     */
    @BeforeAll
    static void setUp() {
        setUpF0Tests();
        setUpF1Tests();
        setUpF2Tests();
        setUpF3Tests();
    }

    /**
     * Tests that an IOException is thrown when the solution path is invalid.
     */
    @Test
    @Tag("0.5")
    @Order(1)
    void testInvalidSolPath() {
        // Create a tester with an invalid solPath
        Tester tester = new Tester("func0", "/a/b/c/d/e",
            userDir + "/src/test/rice/test/pyfiles/f0oneRight",
            Collections.emptyList());

        try {
            tester.computeExpectedResults();
        } catch (IOException e) {
            return;
        } catch (InterruptedException e) {
            fail();
        }
        fail();
    }

    /**
     * Tests that an IOException is thrown when the impl dir path is invalid.
     */
    @Test
    @Tag("0.5")
    @Order(2)
    void testInvalidImplDirPath() {
        // Create a tester with an invalid implDirPath
        Tester tester = new Tester("func0",
            userDir + "/src/test/rice/test/pyfiles/sols/func0sol.py",
            "/a/b/c/d/e",
            Collections.emptyList());

        try {
            tester.computeExpectedResults();
            tester.runTests();
        } catch (IOException e) {
            return;
        } catch (InterruptedException e) {
            fail();
        }
        fail();
    }

    /**
     * Tests that the wrapper file doesn't keep growing longer (gets overwritten, not
     * appended to, on subsequent runs).
     */
    @Test
    @Tag("1.5")
    @Order(3)
    void testWrapperNotGrowing() {
        String implDirPath = userDir + "/src/test/rice/test/pyfiles/f0oneRight";
        Tester tester = new Tester("func0",
            userDir + "/src/test/rice/test/pyfiles/sols/func0sol.py",
            implDirPath,
            Collections.singletonList(f0Tests.get(0)));

        try {
            tester.computeExpectedResults();

            // Run runTests() twice, grabbing the contents of the wrapper after each
            tester.runTests();
            String oldContents = Files.readString(
                Paths.get(implDirPath + "/wrapper.py"));
            tester.runTests();
            String newContents = Files.readString(
                Paths.get(implDirPath + "/wrapper.py"));

            // Make sure the contents didn't change
            assertEquals(oldContents, newContents);

        } catch (Exception e) {
            fail();
        }
    }

    /**
     * Tests that the solution file doesn't keep growing longer (footer gets overwritten,
     * not appended to, on subsequent runs).
     */
    @Test
    @Tag("2.0")
    @Order(4)
    void testSolutionNotGrowing() {
        String solPath = userDir + "/src/test/rice/test/pyfiles/sols/func0sol.py";
        Tester tester = new Tester("func0", solPath,
            userDir + "/src/test/rice/test/pyfiles/f0oneRight",
            Collections.singletonList(f0Tests.get(0)));

        try {
            // Run computeExpectedResults() twice, grabbing the contents of the solution
            // after each
            tester.computeExpectedResults();
            String oldContents = Files.readString(
                Paths.get(solPath));
            tester.computeExpectedResults();
            String newContents = Files.readString(
                Paths.get(solPath));

            // Make sure the contents didn't change
            assertEquals(oldContents, newContents);

        } catch (Exception e) {
            fail();
        }
    }

    /**
     * Tests that expected.py doesn't keep growing longer(gets overwritten, not appended
     * to, on subsequent runs).
     */
    @Test
    @Tag("1.5")
    @Order(5)
    void testExpectedNotGrowing() {
        String implDirPath = userDir + "/src/test/rice/test/pyfiles/f0oneRight";
        Tester tester = new Tester("func0",
            userDir + "/src/test/rice/test/pyfiles/sols/func0sol.py",
            implDirPath, Collections.singletonList(f0Tests.get(0)));

        try {
            // Run computeExpectedResults() twice, grabbing the contents of expected.py
            // after each
            tester.computeExpectedResults();
            String oldContents = Files.readString(
                Paths.get(implDirPath + "/expected.py"));
            tester.computeExpectedResults();
            String newContents = Files.readString(
                Paths.get(implDirPath + "/expected.py"));

            // Make sure the contents didn't change
            assertEquals(oldContents, newContents);

        } catch (Exception e) {
            fail();
        }
    }

    /**
     * Tests computeExpectedResults() using a single test on a function that takes one
     * simple argument.
     */
    @Test
    @Tag("1.5")
    @Order(6)
    void testGetExpectedResultsOneTest() {
        expectedHelper("func0", Collections.singletonList(f0Tests.get(3)),
            "func0sol.py", List.of("3"));
    }

    /**
     * Tests computeExpectedResults() using multiple tests on a function that takes one
     * simple argument.
     */
    @Test
    @Tag("1.5")
    @Order(7)
    void testGetExpectedResultsOneArgSimple() {
        List<String> expected = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            expected.add(String.valueOf(i));
        }
        expectedHelper("func0", f0Tests, "func0sol.py", expected);
    }

    /**
     * Tests computeExpectedResults() using multiple tests on a function that takes
     * multiple simple arguments.
     */
    @Test
    @Tag("3.0")
    @Order(8)
    void testGetExpectedResultsMultipleArgsSimple() {
        // Generate expected (expected) results
        List<String> expected = new ArrayList<>();
        for (TestCase test : f1Tests) {
            if ((boolean) test.getArgs().get(0).getValue()) {
                expected.add(String.valueOf((int) test.getArgs().get(1).getValue()
                    * (double) test.getArgs().get(2).getValue()));
            } else {
                expected.add(String.valueOf((int) test.getArgs().get(1).getValue()
                    + (double) test.getArgs().get(2).getValue()));
            }
        }

        // Run tests and compare expected (expected) results to actual (expected) results
        expectedHelper("func1", f1Tests, "func1sol.py", expected);
    }

    /**
     * Tests computeExpectedResults() using multiple tests on a function that takes one
     * nested argument.
     */
    @Test
    @Tag("2.0")
    @Order(9)
    @SuppressWarnings("unchecked")
    void testGetExpectedResultsOneArgNested() {
        // Generate expected (expected) results
        List<String> expected = new ArrayList<>();
        for (TestCase test : f2Tests) {
            List<String> vals = new ArrayList<>();
            Map<PyStringObj, PyIntObj> map = (Map<PyStringObj, PyIntObj>)
                test.getArgs().get(0).getValue();

            for (Map.Entry<PyStringObj, PyIntObj> entry : map.entrySet()) {
                vals.add(entry.getKey().toString().substring(1, 5) + entry.getValue()
                    .getValue());
            }

            Collections.sort(vals);
            List<PyStringObj> pyVals = new ArrayList<>();
            for (String val : vals) {
                pyVals.add(new PyStringObj(val));
            }
            expected.add(new PyListObj<>(pyVals).toString());
        }

        // Run tests and compare expected (expected) results to actual (expected) results
        expectedHelper("func2", f2Tests, "func2sol.py", expected);
    }

    /**
     * Tests computeExpectedResults() using multiple tests on a function that takes
     * multiple nested arguments.
     */
    @Test
    @Tag("3.0")
    @Order(10)
    @SuppressWarnings("unchecked")
    void testGetExpectedResultsMultipleArgsNested() {
        // Generate expected (expected) results
        List<String> expected = new ArrayList<>();

        for (TestCase test : f3Tests) {
            if (((Set<PyIntObj>) test.getArgs().get(0).getValue()).size() > 0) {
                expected.add("('3', '4')");
            } else if (((List<PyIntObj>) test.getArgs().get(1).getValue()).size()
                > ((List<PyIntObj>) test.getArgs().get(2).getValue()).size()) {
                expected.add("('4', '5')");
            } else {
                expected.add("('5', '6')");
            }
        }

        // Run tests and compare expected (expected) results to actual (expected) results
        expectedHelper("func3", f3Tests, "func3sol.py", expected);
    }

    /**
     * Tests running a single failing test on a single implementation of a function that
     * takes one simple argument; checks wrongSet.
     */
    @Test
    @Tag("1.0")
    @Order(11)
    void testRunTestsOneFileOneTestFails() {
        runTestsHelper("func0", Collections.singletonList(f0Tests.get(0)), "f0oneWrong",
            "results = [0]", Set.of(0), List.of(Set.of(0)), 0);
    }

    /**
     * Tests running a single failing test on a single implementation of a function that
     * takes one simple argument; checks caseToFiles.
     */
    @Test
    @Tag("1.0")
    @Order(12)
    void testRunTestsOneFileOneTestFails2() {
        runTestsHelper("func0", Collections.singletonList(f0Tests.get(0)), "f0oneWrong",
            "results = [0]", Set.of(0), List.of(Set.of(0)), 1);
    }

    /**
     * Tests running a single failing test on a single implementation of a function that
     * takes one simple argument; checks wrongSet.
     */
    @Test
    @Tag("2.0")
    @Order(13)
    void testRunTestsReturnsTests() {
        runTestsHelper("func0", Collections.singletonList(f0Tests.get(0)), "f0oneWrong",
            "results = [0]", Set.of(0), List.of(Set.of(0)), 2);
    }

    /**
     * Tests running a single passing test on a single implementation of a function that
     * takes one simple argument; checks wrongSet.
     */
    @Test
    @Tag("1.0")
    @Order(14)
    void testRunTestsOneFileOneTestPasses() {
        runTestsHelper("func0", Collections.singletonList(f0Tests.get(4)), "f0oneRight",
            "results = [4]", Set.of(), List.of(Set.of()), 0);
    }

    /**
     * Tests running a single passing test on a single implementation of a function that
     * takes one simple argument; checks the wrongSet; checks caseToFiles.
     */
    @Test
    @Tag("1.0")
    @Order(15)
    void testRunTestsOneFileOneTestPasses2() {
        runTestsHelper("func0", Collections.singletonList(f0Tests.get(4)), "f0oneRight",
            "results = [4]", Set.of(), List.of(Set.of()), 1);
    }

    /**
     * Tests running multiple failing tests on a single implementation of a function that
     * takes one simple argument; checks wrongSet.
     */
    @Test
    @Tag("1.0")
    @Order(16)
    void testRunTestsOneFileFailsAll() {
        runTestsHelper("func0", f0Tests, "f0oneWrong",
            "results = [0, 1, 2, 3, 4]", Set.of(0), List.of(Set.of(0), Set.of(0),
                Set.of(0), Set.of(0), Set.of(0)), 0);
    }

    /**
     * Tests running multiple failing tests on a single implementation of a function that
     * takes one simple argument; checks caseToFiles.
     */
    @Test
    @Tag("1.0")
    @Order(17)
    void testRunTestsOneFileFailsAll2() {
        runTestsHelper("func0", f0Tests, "f0oneWrong",
            "results = [0, 1, 2, 3, 4]", Set.of(0), List.of(Set.of(0), Set.of(0),
                Set.of(0), Set.of(0), Set.of(0)), 1);
    }

    /**
     * Tests running multiple passing tests on a single implementation of a function that
     * takes one simple argument; checks wrongSet.
     */
    @Test
    @Tag("1.0")
    @Order(18)
    void testRunTestsOneFilePassesAll() {
        runTestsHelper("func0", f0Tests, "f0oneRight",
            "results = [0, 1, 2, 3, 4]", Set.of(), List.of(Set.of(), Set.of(),
                Set.of(), Set.of(), Set.of()), 0);
    }

    /**
     * Tests running multiple passing tests on a single implementation of a function that
     * takes one simple argument; checks caseToFiles.
     */
    @Test
    @Tag("1.0")
    @Order(19)
    void testRunTestsOneFilePassesAll2() {
        runTestsHelper("func0", f0Tests, "f0oneRight",
            "results = [0, 1, 2, 3, 4]", Set.of(), List.of(Set.of(), Set.of(),
                Set.of(), Set.of(), Set.of()), 1);
    }

    /**
     * Tests running a mix of passing and failing tests on a single implementation of a
     * function that takes one simple argument; checks wrongSet
     */
    @Test
    @Tag("1.0")
    @Order(20)
    void testRunTestsOneFileMixed() {
        runTestsHelper("func0", f0Tests, "f0oneMixed",
            "results = [0, 1, 2, 3, 4]", Set.of(0), List.of(Set.of(0), Set.of(),
                Set.of(0), Set.of(), Set.of(0)), 0);
    }

    /**
     * Tests running a mix of passing and failing tests on a single implementation of a
     * function that takes one simple argument.
     */
    @Test
    @Tag("1.0")
    @Order(21)
    void testRunTestsOneFileMixed2() {
        runTestsHelper("func0", f0Tests, "f0oneMixed",
            "results = [0, 1, 2, 3, 4]", Set.of(0), List.of(Set.of(0), Set.of(),
                Set.of(0), Set.of(), Set.of(0)), 1);
    }

    /**
     * Tests running multiple failing tests on multiple implementations of a function that
     * takes one simple argument; checks wrongSet.
     */
    @Test
    @Tag("1.0")
    @Order(22)
    void testRunTestsMultipleFilesFailAll() {
        runTestsHelper("func0", f0Tests, "f0multipleWrong",
            "results = [0, 1, 2, 3, 4]", Set.of(0, 1), List.of(Set.of(0, 1),
                Set.of(0, 1), Set.of(0, 1), Set.of(0, 1), Set.of(0, 1)), 0);
    }

    /**
     * Tests running multiple failing tests on multiple implementations of a function that
     * takes one simple argument; checks caseToFiles.
     */
    @Test
    @Tag("1.0")
    @Order(23)
    void testRunTestsMultipleFilesFailAll2() {
        runTestsHelper("func0", f0Tests, "f0multipleWrong",
            "results = [0, 1, 2, 3, 4]", Set.of(0, 1), List.of(Set.of(0, 1),
                Set.of(0, 1), Set.of(0, 1), Set.of(0, 1), Set.of(0, 1)), 1);
    }

    /**
     * Tests running multiple passing tests on multiple implementations of a function that
     * takes one simple argument; checks wrongSet.
     */
    @Test
    @Tag("1.5")
    @Order(24)
    void testRunTestsMultipleFilesPassAll() {
        runTestsHelper("func0", f0Tests, "f0multipleRight",
            "results = [0, 1, 2, 3, 4]", Set.of(),
            List.of(Set.of(), Set.of(), Set.of(), Set.of(), Set.of()), 0);
    }

    /**
     * Tests running multiple passing tests on multiple implementations of a function that
     * takes one simple argument; checks caseToFiles.
     */
    @Test
    @Tag("1.5")
    @Order(25)
    void testRunTestsMultipleFilesPassAll2() {
        runTestsHelper("func0", f0Tests, "f0multipleRight",
            "results = [0, 1, 2, 3, 4]", Set.of(),
            List.of(Set.of(), Set.of(), Set.of(), Set.of(), Set.of()), 1);
    }

    /**
     * Tests running a mix of passing and failing tests on multiple implementations of a
     * function that takes one simple argument; checks wrongSet.
     */
    @Test
    @Tag("1.0")
    @Order(26)
    void testRunTestsMultipleFilesMixed() {
        runTestsHelper("func0", f0Tests, "f0multipleMixed",
            "results = [0, 1, 2, 3, 4]", Set.of(0, 1),
            List.of(Set.of(0), Set.of(1), Set.of(0), Set.of(1), Set.of(0)), 0);
    }

    /**
     * Tests running a mix of passing and failing tests on multiple implementations of a
     * function that takes one simple argument; checks caseToFiles.
     */
    @Test
    @Tag("2.0")
    @Order(27)
    void testRunTestsMultipleFilesMixed2() {
        runTestsHelper("func0", f0Tests, "f0multipleMixed",
            "results = [0, 1, 2, 3, 4]", Set.of(0, 1),
            List.of(Set.of(0), Set.of(1), Set.of(0), Set.of(1), Set.of(0)), 1);
    }

    /**
     * Tests running a mix of tests on multiple implementations of a function that takes
     * one simple argument, where one implementation is buggy and the other is not;
     * checks wrongSet.
     */
    @Test
    @Tag("3.0")
    @Order(28)
    void testRunTestsMultipleFilesMixedCorrectness() {
        runTestsHelper("func0", f0Tests, "f0multipleMixed2",
            "results = [0, 1, 2, 3, 4]", Set.of(0),
            List.of(Set.of(0), Collections.emptySet(), Set.of(0), Collections.emptySet(),
                Set.of(0)), 0);
    }

    /**
     * Tests running multiple failing tests on multiple implementations of a function that
     * takes multiple nested arguments; checks wrongSet.
     */
    @Test
    @Tag("1.0")
    @Order(29)
    void testRunTestsMultipleFilesFailAllComplex() {
        // Generate expected results
        List<Set<Integer>> expected = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            expected.add(Set.of(0, 1, 2));
        }

        // Run tests and compare expected results to actual results
        runTestsHelper("func3", f3Tests, "f3multipleWrong",
            f3resultStr, Set.of(0, 1, 2), expected, 0);
    }

    /**
     * Tests running multiple failing tests on multiple implementations of a function that
     * takes multiple nested arguments; checks caseToFiles.
     */
    @Test
    @Tag("1.0")
    @Order(30)
    void testRunTestsMultipleFilesFailAllComplex2() {
        // Generate expected results
        List<Set<Integer>> expected = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            expected.add(Set.of(0, 1, 2));
        }

        // Run tests and compare expected results to actual results
        runTestsHelper("func3", f3Tests, "f3multipleWrong",
            f3resultStr, Set.of(0, 1, 2), expected, 1);
    }

    /**
     * Tests running multiple passing tests on multiple implementations of a function that
     * takes multiple nested arguments; checks wrongSet.
     */
    @Test
    @Tag("2.0")
    @Order(31)
    void testRunTestsMultipleFilesPassAllComplex() {
        // Generate expected results
        List<Set<Integer>> expected = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            expected.add(Set.of());
        }

        // Run tests and compare expected results to actual results
        runTestsHelper("func3", f3Tests, "f3multipleRight",
            f3resultStr, Set.of(), expected, 0);
    }

    /**
     * Tests running multiple passing tests on multiple implementations of a function that
     * takes multiple nested arguments; checks caseToFiles.
     */
    @Test
    @Tag("2.0")
    @Order(32)
    void testRunTestsMultipleFilesPassAllComplex2() {
        // Generate expected results
        List<Set<Integer>> expected = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            expected.add(Set.of());
        }

        // Run tests and compare expected results to actual results
        runTestsHelper("func3", f3Tests, "f3multipleRight",
            f3resultStr, Set.of(),
            expected, 1);
    }

    /**
     * Tests running a mix of passing and failing tests on multiple implementations of a
     * function that takes multiple nested arguments; checks wrongSet.
     */
    @Test
    @Tag("2.0")
    @Order(33)
    @SuppressWarnings("unchecked")
    void testRunTestsMultipleFilesMixedComplex() {
        // Generate expected results
        List<Set<Integer>> expected = new ArrayList<>();
        int i = 0;
        for (TestCase test : f3Tests) {
            if (((Set<PyIntObj>) test.getArgs().get(0).getValue()).size() != 0) {
                expected.add(Set.of(2));
            } else {
                Set<Integer> wrongSet = new HashSet<>();
                wrongSet.add(1);
                if (((List<PyIntObj>) test.getArgs().get(2).getValue()).size()
                    >= ((List<PyIntObj>) test.getArgs().get(1).getValue()).size()) {
                    wrongSet.add(0);
                }
                expected.add(wrongSet);
            }
            i++;
        }

        // Run tests and compare expected results to actual results
        runTestsHelper("func3", f3Tests, "f3multipleMixed",
            f3resultStr,
            Set.of(0, 1, 2), expected, 0);
    }

    /**
     * Tests running a mix of passing and failing tests on multiple implementations of a
     * function that takes multiple nested arguments; checks caseToFiles.
     */
    @Test
    @Tag("4.0")
    @Order(34)
    @SuppressWarnings("unchecked")
    void testRunTestsMultipleFilesMixedComplex2() {
        // Generate expected results
        List<Set<Integer>> expected = new ArrayList<>();
        int i = 0;
        for (TestCase test : f3Tests) {
            if (((Set<PyIntObj>) test.getArgs().get(0).getValue()).size() != 0) {
                expected.add(Set.of(2));
            } else {
                Set<Integer> wrongSet = new HashSet<>();
                wrongSet.add(1);
                if (((List<PyIntObj>) test.getArgs().get(2).getValue()).size()
                    >= ((List<PyIntObj>) test.getArgs().get(1).getValue()).size()) {
                    wrongSet.add(0);
                }
                expected.add(wrongSet);
            }
            i++;
        }

        // Run tests and compare expected results to actual results
        runTestsHelper("func3", f3Tests, "f3multipleMixed",
            f3resultStr,
            Set.of(0, 1, 2), expected, 1);
    }

    /**
     * Tests running multiple failing tests on multiple implementations of a function that
     * takes multiple nested arguments; checks wrongSet.
     */
    @Test
    @Tag("1.0")
    @Order(35)
    void testRunTestsMalformedFilesFailAll() {
        // Generate expected results
        List<Set<Integer>> expected = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            expected.add(Set.of(0, 1, 2));
        }

        // Run tests and compare expected results to actual results
        runTestsHelper("func3", f3Tests, "f3malformed",
            f3resultStr, Set.of(0, 1, 2), expected, 0);
    }

    /**
     * Tests running multiple failing tests on multiple implementations of a function that
     * takes multiple nested arguments; checks caseToFiles.
     */
    @Test
    @Tag("1.0")
    @Order(36)
    void testRunTestsMalformedFilesFailAll2() {
        // Generate expected results
        List<Set<Integer>> expected = new ArrayList<>();
        for (int i = 0; i < 8; i++) {
            expected.add(Set.of(0, 1, 2));
        }

        // Run tests and compare expected results to actual results
        runTestsHelper("func3", f3Tests, "f3malformed",
            f3resultStr, Set.of(0, 1, 2), expected, 1);
    }

    /**
     * Sets up the test cases for function f0, which takes one simple argument.
     */
    private static void setUpF0Tests() {
        // Create five test cases with one argument that's an integer
        for (int i = 0; i < 5; i++) {
            f0Tests.add(new TestCase(Collections.singletonList(new PyIntObj(i))));
        }
    }

    /**
     * Sets up the test cases for function f1, which takes multiple simple arguments.
     */
    private static void setUpF1Tests() {
        // Create eight test cases with (bool, int, float) arguments
        for (boolean b : new boolean[]{true, false}) {
            for (int i = 0; i < 2; i++) {
                for (double f : new double[]{4.4, -6.07}) {
                    f1Tests.add(new TestCase(Arrays
                        .asList(new PyBoolObj(b), new PyIntObj(i), new PyFloatObj(f))));
                }
            }
        }
    }

    /**
     * Sets up the test cases for function f2, which takes one nested argument.
     */
    private static void setUpF2Tests() {
        // Create nine possible test cases with (dict(string:int)) arguments
        for (int val1 = -3; val1 < 0; val1++) {
            for (int val2 = -1; val2 < 2; val2++) {
                f2Tests.add(new TestCase(Collections.singletonList(new PyDictObj<>(
                    Map.of(new PyStringObj("key1"), new PyIntObj(val1),
                        new PyStringObj("key2"), new PyIntObj(val2))))));
            }
        }
    }

    /**
     * Sets up the test cases for function f3, which takes multiple nested arguments.
     */
    private static void setUpF3Tests() {
        // Create two possible set(int)s
        PySetObj<PyIntObj> set1 = new PySetObj<>(Set.of());
        PySetObj<PyIntObj> set2 = new PySetObj<>(Set.of(new PyIntObj(3)));

        // Create two possible list(int)s
        PyListObj<PyIntObj> list1 =
            new PyListObj<>(Collections.singletonList(new PyIntObj(4)));
        PyListObj<PyIntObj> list2 =
            new PyListObj<>(Arrays.asList(new PyIntObj(4), new PyIntObj(4)));

        // Create two possible tuple(int)s
        PyTupleObj<PyIntObj> tup1 =
            new PyTupleObj<>(Collections.singletonList(new PyIntObj(5)));
        PyTupleObj<PyIntObj> tup2 =
            new PyTupleObj<>(Arrays.asList(new PyIntObj(5), new PyIntObj(5)));

        // Create eight test cases with (set(int), list(int), tuple(int)) arguments
        for (PySetObj<?> set : new PySetObj[]{set1, set2}) {
            for (PyListObj<?> list : new PyListObj[]{list1, list2}) {
                for (PyTupleObj<?> tup : new PyTupleObj[]{tup1, tup2}) {
                    f3Tests.add(new TestCase(List.of(set, list, tup)));
                }
            }
        }

        f3resultStr =
            "results = [('5', '6'), ('5', '6'), ('4', '5'), ('5', '6'), ('3', '4'), "
                + "('3', '4'), ('3', '4'), ('3', '4')]";
    }

    /**
     * Helper function for testing the runTests() function; instantiates a Tester object,
     * fakes computation of the expected results, gets the actual results, and compares
     * both the wrongSet and caseToFile mappings between the actual and expected results.
     *
     * @param funcName    name of the function under test
     * @param tests       the set of tests to be run
     * @param implDir     the path to the directory containing the buggy implementations
     * @param solResults  the expected contents of expected.py, assuming
     *                    computeExpectedResults() is correct
     * @param expWrongSet the expected wrongSet
     * @param expResults  the expected caseToFile list
     */
    private void runTestsHelper(String funcName, List<TestCase> tests, String implDir,
        String solResults, Set<Integer> expWrongSet, List<Set<Integer>> expResults,
        int outputToCheck) {
        Tester tester = new Tester(funcName, null,
            userDir + "/src/test/rice/test/pyfiles/" + implDir, tests);
        try {
            // Generate the expected.py file (to fake computing the expected results
            // without creating a dependency on computeExpectedResults())
            FileWriter writer = new FileWriter(userDir +
                "/src/test/rice/test/pyfiles/" + implDir + "/expected.py");
            writer.write(solResults);
            writer.close();

            // Run the tester
            TestResults results = tester.runTests();
            if (outputToCheck == 0) {
                assertEquals(expWrongSet, results.getWrongSet());
            } else if (outputToCheck == 1) {
                assertEquals(expResults, results.getCaseToFiles());
            } else {
                for (int i = 0; i < tests.size(); i++) {
                    assertEquals(tests.get(i), results.getTestCase(i));
                }
            }
        } catch (Exception e) {
            fail();
        } finally {
            // Clean up, so that we don't artificially make it look like
            // computeExpectedResults() works
            deletedExpected(implDir);
        }
    }

    /**
     * Helper function for testing the computeExpectedResults() function; instantiates a
     * Tester object, computes the expected results, and compares those to the
     * manually-created expected results.
     *
     * @param funcName the name of the function under test
     * @param tests    the set of tests to be run
     * @param solName  the filename of the reference solution, which can be found in * the
     *                 test.rice.test.pyfiles.sols package
     * @param expected the expected (expected) results
     */
    private void expectedHelper(String funcName, List<TestCase> tests, String solName,
        List<String> expected) {
        Tester tester = new Tester(funcName, userDir +
            "/src/test/rice/test/pyfiles/sols/" + solName, userDir +
            "/src/test/rice/test/pyfiles/f0oneRight", tests);
        try {
            // Compute the actual results and compare to the expected
            List<String> actual = tester.computeExpectedResults();
            assertEquals(expected, actual);
        } catch (Exception e) {
            fail();
        }
    }

    /**
     * Deletes the file containing the expected results.
     *
     * @param implDir the path to the directory containing the expected results
     */
    private void deletedExpected(String implDir) {
        File expFile = new File(userDir + "/src/test/rice/test/pyfiles/" +
                implDir + "/expected.py");
        expFile.delete();
    }
}