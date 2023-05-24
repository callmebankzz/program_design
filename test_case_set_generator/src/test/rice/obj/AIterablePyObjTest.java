package test.rice.obj;

import main.rice.obj.PyBoolObj;
import main.rice.obj.PyFloatObj;
import main.rice.obj.PyIntObj;
import main.rice.obj.PyTupleObj;
import org.junit.jupiter.api.BeforeAll;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * A helper class encapsulating tests that will be shared by the various classes for
 * testing AIterablePyObj classes (PyListObjTest, PyTupleObjTest, etc.).
 */
public abstract class AIterablePyObjTest {

    /**
     * An empty list designed to contain floats.
     */
    protected static List<PyFloatObj> emptyFloatVal;

    /**
     * An empty list designed to contain ints.
     */
    protected static List<PyIntObj> emptyIntVal;

    /**
     * Two identical non-empty lists containing floats.
     */
    protected static List<PyFloatObj> nonEmptyFloatVal;
    protected static List<PyFloatObj> nonEmptyFloatVal2;

    /**
     * A non-empty list containing floats whose values are different from the other
     * two nonEmptyFloatTups.
     */
    protected static List<PyFloatObj> nonEmptyFloatVal3;

    /**
     * A non-empty list containing ints.
     */
    protected static List<PyIntObj> nonEmptyIntVal;

    /**
     * A list containing a subset of the elements in nonEmptyIntTup.
     */
    protected static List<PyIntObj> nonEmptyIntSubsetVal;

    /**
     * Two identical nested lists.
     */
    protected static List<PyTupleObj<PyBoolObj>> nestedVal;
    protected static List<PyTupleObj<PyBoolObj>> nestedVal2;

    /**
     * Sets up all static fields for use in the test cases in the subclasses.
     */
    @BeforeAll
    static void setUp() {
        // Create two empty lists of different sizes
        emptyFloatVal = new ArrayList<>();
        emptyIntVal = new ArrayList<>();

        // Create two identical non-empty lists of floats, and a third distinct
        // non-empty tuple of floats.
        nonEmptyFloatVal = Arrays.asList(new PyFloatObj(3.1),
            new PyFloatObj(2.3), new PyFloatObj(6.0));
        nonEmptyFloatVal2 = Arrays.asList(new PyFloatObj(3.1),
            new PyFloatObj(2.3), new PyFloatObj(6.0));
        nonEmptyFloatVal3 = Arrays.asList(new PyFloatObj(-11.11),
            new PyFloatObj(11.11), new PyFloatObj(-1.111));

        // Create two non-empty lists of ints, one of which is a subset of the other
        nonEmptyIntVal = Arrays.asList(new PyIntObj(1), new PyIntObj(2));
        nonEmptyIntSubsetVal = Collections.singletonList(new PyIntObj(1));

        // Create two identical nested lists
        nestedVal = getNestedVal();
        nestedVal2 = getNestedVal();
    }

    /**
     * Sets up and returns value for nestedVal and nestedVal2.
     *
     * @return value for nestedVal and nestedVal2
     */
    private static List<PyTupleObj<PyBoolObj>> getNestedVal() {
        List<PyBoolObj> nestedSubval1 = Arrays.asList(new PyBoolObj(true),
            new PyBoolObj(true));
        List<PyBoolObj> nestedSubval2 = Arrays.asList(new PyBoolObj(true),
            new PyBoolObj(false));
        List<PyBoolObj> nestedSubval3 = Arrays.asList(new PyBoolObj(false),
            new PyBoolObj(false), new PyBoolObj(true), new PyBoolObj(false));
        return Arrays.asList(new PyTupleObj<>(nestedSubval1),
            new PyTupleObj<>(nestedSubval2), new PyTupleObj<>(nestedSubval3));
    }
}