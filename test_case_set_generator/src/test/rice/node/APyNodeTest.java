package test.rice.node;

import main.rice.node.APyNode;
import main.rice.obj.APyObj;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Helper functions for testing random generation, to be shared by all of the Py*NodeTest
 * classes.
 */
public class APyNodeTest {

    /**
     * Helper function for testing genRanVal() that runs the input number of trials and
     * builds a distribution of the results.
     *
     * @param node      the node being used to generate random values
     * @param numTrials the number of trials to be run
     * @return the distribution of results, in the form of a mapping of each generated
     * object to its relative frequency of generation
     */
    public static <T extends APyObj> Map<T, Double> buildDistribution(APyNode<T> node,
        int numTrials) {
        // Build raw frequency counts
        Map<T, Double> actual = new HashMap<>();
        for (int i = 0; i < numTrials; i++) {
            T obj = node.genRandVal();
            if (!actual.containsKey(obj)) {
                actual.put(obj, 1.0);
            } else {
                actual.put(obj, actual.get(obj) + 1);
            }
        }

        // Normalize frequency counts
        for (Map.Entry<T, Double> entry : actual.entrySet()) {
            actual.put(entry.getKey(), entry.getValue() / numTrials);
        }

        // Return the distribution
        return actual;
    }

    /**
     * Helper function for testing genRanVal() that takes in two distributions -- expected
     * and actual -- as well as the acceptable marginOfError. Returns true if every (key in
     * expected)'s value in actual is within the marginOfError of the its value in
     * expected, and every (key in actual) that is *not* in expected has a value that is less
     * than marginOfError; false otherwise.
     *
     * @param actual        the actual distribution
     * @param expected      the expected distribution
     * @param marginOfError the upper bound on acceptable error
     * @return true if all differences between the actual and expected distributions are
     * within the marginOfError; false otherwise
     */
    public static <T> boolean compareDistribution(Map<T, Double> expected,
        Map<T, Double> actual, Double marginOfError) {

        // Make a copy of actual
        Map<T, Double> actualCopy = new HashMap<>(actual);

        // Check every entry in expected to see if its key's corresponding value in
        // actual is within the acceptable marign of error
        for (Map.Entry<T, Double> entry : expected.entrySet()) {

            // Look up the value associated with this entry's key in actual, setting it
            // to 0 if this key is not found in actual
            Double actualVal;
            actualVal = actual.getOrDefault(entry.getKey(), 0.0);
            actualCopy.remove(entry.getKey());

            // Check that difference is within the margin of error
            if (Math.abs(actualVal - entry.getValue()) > marginOfError) {
                // Difference is too large
                return false;
            }
        }
        
        // It would be reasonable to do a parallel check on actualCopy (comparing its
        // values directly to marginOfError), but we can actually be stricter and say
        // that any spurious values in actual represent failure.
        return actualCopy.size() <= 0;
    }

    /**
     * Converts the expected results of exhaustive generation to the expected results of
     * random generation. Only works for simple PyNodes and compound PyNodes whose domains
     * consist of a single size (and therefore result in an equal distribution of
     * values).
     *
     * @param expEx the expected results of exhaustive generation
     * @param prob  the probability of each object being generated
     * @return the expected results of random generation (a probability distribution)
     */
    public static <T> Map<T, Double> convertExpExToRandEqual(Set<T> expEx,
        Double prob) {
        Map<T, Double> expRand = new HashMap<>();
        for (T obj : expEx) {
            expRand.put(obj, prob);
        }
        return expRand;
    }
}
