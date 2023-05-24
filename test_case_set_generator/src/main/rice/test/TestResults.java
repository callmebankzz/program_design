package main.rice.test;

import java.util.List;
import java.util.Set;

/**
 * A representation of the results of running a series of tests on a series of files.
 */
public class TestResults {

    /**
     * All test cases that were executed; each one is a wrapper around a series of
     * PyObjs.
     */
    private List<TestCase> allCases;

    /**
     * The results of testing, in the form of a list where the i-th element is the set of
     * integers representing the indices of the files that were caught by that i-th test
     * case in allCases.
     */
    private List<Set<Integer>> caseToFiles;

    /**
     * A set of integers representing the indices of the files that failed one or more
     * tests in allCases
     */
    private Set<Integer> wrongSet;

    /**
     * Constructor for a TestResults object; initializes all fields.
     *
     * @param allCases    all test cases that were executed
     * @param caseToFiles a list where the i-th element is a set of integers representing
     *                    the files that were caught by the i-th test case in allCases
     * @param wrongSet    the set of all files that failed one or more tests in allCases
     */
    public TestResults(List<TestCase> allCases, List<Set<Integer>> caseToFiles,
        Set<Integer> wrongSet) {
        this.allCases = allCases;
        this.caseToFiles = caseToFiles;
        this.wrongSet = wrongSet;
    }

    /**
     * Returns the index-th test case in allCases, if index is within the bounds of
     * allCases; null otherwise.
     *
     * @param index the index of the test case to be returned
     * @return the index-th test case in allTests, if index is valid; null otherwise
     */
    public TestCase getTestCase(int index) {
        // Make sure the index is in bounds; if it's out of bounds, return null
        if (index >= this.allCases.size() || (this.allCases.size() == 0) || (index < 0)) {
            return null;
        }
        return this.allCases.get(index);
    }

    /**
     * @return the set of files that failed one or more test cases (represented as integer
     * indices)
     */
    public Set<Integer> getWrongSet() {
        return this.wrongSet;
    }

    /**
     * @return the per-case list of files that they caught (represented by indices)
     */
    public List<Set<Integer>> getCaseToFiles() {
        return this.caseToFiles;
    }
}