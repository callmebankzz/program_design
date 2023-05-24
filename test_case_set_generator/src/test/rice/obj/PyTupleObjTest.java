package test.rice.obj;

import main.rice.obj.PyBoolObj;
import main.rice.obj.PyFloatObj;
import main.rice.obj.PyIntObj;
import main.rice.obj.PyTupleObj;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Test cases for the PyTupleObj class.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PyTupleObjTest extends AIterablePyObjTest {

    /**
     * An empty PyTupleObj designed to contain floats.
     */
    private static PyTupleObj<PyFloatObj> emptyFloatTup;

    /**
     * An empty PyTupleObj designed to contain ints.
     */
    private static PyTupleObj<PyIntObj> emptyIntTup;

    /**
     * Two identical non-empty PyTupleObjs containing floats.
     */
    private static PyTupleObj<PyFloatObj> nonEmptyFloatTup;
    private static PyTupleObj<PyFloatObj> nonEmptyFloatTup2;

    /**
     * A non-empty PyTupleObj containing floats whose values are different from the other
     * two nonEmptyFloatTups.
     */
    private static PyTupleObj<PyFloatObj> nonEmptyFloatTup3;

    /**
     * A non-empty PyTupleObj containing ints.
     */
    private static PyTupleObj<PyIntObj> nonEmptyIntTup;

    /**
     * A PyTupleObj containing a subset of the elements in nonEmptyIntTup.
     */
    private static PyTupleObj<PyIntObj> nonEmptyIntSubsetTup;

    /**
     * Two identical nested PyTupleObjs.
     */
    private static PyTupleObj<PyTupleObj<PyBoolObj>> nestedTup;
    private static PyTupleObj<PyTupleObj<PyBoolObj>> nestedTup2;

    /**
     * Sets up all static fields for use in the test cases.
     */
    @BeforeAll
    static void setUp() {
        AIterablePyObjTest.setUp();

        // Create two empty tuples of different types
        emptyFloatTup = new PyTupleObj<>(emptyFloatVal);
        emptyIntTup = new PyTupleObj<>(emptyIntVal);

        // Create two identical non-empty tuples of floats, and a third distinct
        // non-empty tuple of floats.
        nonEmptyFloatTup = new PyTupleObj<>(nonEmptyFloatVal);
        nonEmptyFloatTup2 = new PyTupleObj<>(nonEmptyFloatVal2);
        nonEmptyFloatTup3 = new PyTupleObj<>(nonEmptyFloatVal3);

        // Create two non-empty tuples of ints, one of which is a subset of the other
        nonEmptyIntTup = new PyTupleObj<>(nonEmptyIntVal);
        nonEmptyIntSubsetTup = new PyTupleObj<>(nonEmptyIntSubsetVal);

        // Create two identical nested tuples
        nestedTup = new PyTupleObj<>(nestedVal);
        nestedTup2 = new PyTupleObj<>(nestedVal2);
    }

    /**
     * Tests getValue() on a nested tuple.
     */
    @Test
    @Tag("0.25")
    @Order(1)
    void testGetValueNested() {
        assertEquals(nestedVal, nestedTup.getValue());
    }

    /**
     * Tests toString() on an empty tuple.
     */
    @Test
    @Tag("0.5")
    @Order(2)
    void testToStringEmpty() {
        assertEquals("()", emptyFloatTup.toString());
    }

    /**
     * Tests toString() on a tuple containing a single element; must have a trailing
     * comma.
     */
    @Test
    @Tag("0.5")
    @Order(3)
    void testToStringSingle() {
        assertEquals("(1,)", nonEmptyIntSubsetTup.toString());
    }

    /**
     * Tests toString() on a tuple containing multiple elements.
     */
    @Test
    @Tag("0.25")
    @Order(4)
    void testToStringMultiple1() {
        assertEquals("(3.1, 2.3, 6.0)", nonEmptyFloatTup.toString());
    }

    /**
     * Tests toString() on a nested tuple.
     */
    @Test
    @Tag("0.25")
    @Order(5)
    void testToStringNested() {
        assertEquals("((True, True), (True, False), (False, False, True, False))",
            nestedTup.toString());
    }

    /**
     * Tests equals() on two empty tuples of different declared inner types (should still
     * be considered equal).
     */
    @Test
    @Tag("0.25")
    @Order(6)
    void testEqualsEmpty() {
        assertEquals(emptyFloatTup, emptyIntTup);
    }

    /**
     * Tests equals() on two non-empty tuples containing identical elements (but whose
     * underlying lists are unique objects).
     */
    @Test
    @Tag("0.25")
    @Order(7)
    void testEqualsMultiple() {
        assertEquals(nonEmptyFloatTup, nonEmptyFloatTup2);
    }

    /**
     * Tests equals() on two tuples with different elements.
     */
    @Test
    @Tag("0.25")
    @Order(8)
    void testNotEqual() {
        assertNotEquals(nonEmptyFloatTup2, nonEmptyFloatTup3);
    }

    /**
     * Tests equals() on two tuples with different elements, where one is a subset of the
     * other.
     */
    @Test
    @Tag("0.25")
    @Order(9)
    void testNotEqualSubset1() {
        assertNotEquals(nonEmptyIntTup, nonEmptyIntSubsetTup);
    }

    /**
     * Tests equals() on two tuples with different elements, where one is a subset of the
     * other (in the opposite direction of testNotEqualSubset1).
     */
    @Test
    @Tag("0.25")
    @Order(10)
    void testNotEqualSubset2() {
        assertNotEquals(nonEmptyIntSubsetTup, nonEmptyIntTup);
    }

    /**
     * Tests equals() on two identical nested tuples.
     */
    @Test
    @Tag("0.5")
    @Order(11)
    void testEqualsNested() {
        assertEquals(nestedTup, nestedTup2);
    }

    /**
     * Tests that hashCode() returns the same value for two nested tuples containing
     * identical elements.
     */
    @Test
    @Tag("0.5")
    @Order(12)
    void testHashCodeNested() {
        assertEquals(nestedTup.hashCode(), nestedTup2.hashCode());
    }
}