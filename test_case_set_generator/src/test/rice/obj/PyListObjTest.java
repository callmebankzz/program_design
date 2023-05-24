package test.rice.obj;

import main.rice.obj.*;
import org.junit.jupiter.api.*;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Test cases for the PyListObj class.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class PyListObjTest extends AIterablePyObjTest {

    /**
     * An empty PyListObj designed to contain floats.
     */
    private static PyListObj<PyFloatObj> emptyFloatList;

    /**
     * An empty PyListObj designed to contain lists of ints.
     */
    private static PyListObj<PyListObj<PyIntObj>> emptyNestedList;

    /**
     * Two identical non-empty PyListObjs containing floats.
     */
    private static PyListObj<PyFloatObj> nonEmptyFloatList;
    private static PyListObj<PyFloatObj> nonEmptyFloatList2;

    /**
     * A non-empty PyListObj containing floats whose values are different from the other
     * two nonEmptyFloatLists.
     */
    private static PyListObj<PyFloatObj> nonEmptyFloatList3;

    /**
     * A non-empty PyListObj containing ints.
     */
    private static PyListObj<PyIntObj> nonEmptyIntList;

    /**
     * A PyListObj containing a subset of the elements in nonEmptyIntList.
     */
    private static PyListObj<PyIntObj> nonEmptyIntSubsetList;

    /**
     * Two identical nested PyListObjs.
     */
    private static PyListObj<PyListObj<PyBoolObj>> nestedList;
    private static PyListObj<PyListObj<PyBoolObj>> nestedList2;
    private static List<PyListObj<PyBoolObj>> nestedListVal;

    /**
     * Sets up all static fields for use in the test cases.
     */
    @BeforeAll
    static void setUp() {
        AIterablePyObjTest.setUp();

        // Create two empty lists of different types
        emptyFloatList = new PyListObj<>(emptyFloatVal);
        emptyNestedList = new PyListObj<>(Collections.emptyList());

        // Create two identical non-empty lists of floats, and a third distinct
        // non-empty List of floats.
        nonEmptyFloatList = new PyListObj<>(nonEmptyFloatVal);
        nonEmptyFloatList2 = new PyListObj<>(nonEmptyFloatVal2);
        nonEmptyFloatList3 = new PyListObj<>(nonEmptyFloatVal3);

        // Create two non-empty lists of ints, one of which is a subset of the other
        nonEmptyIntList = new PyListObj<>(nonEmptyIntVal);
        nonEmptyIntSubsetList = new PyListObj<>(nonEmptyIntSubsetVal);

        // Create two identical nested lists
        nestedListVal = new ArrayList<>();
        List<PyListObj<PyBoolObj>> nestedListVal2 = new ArrayList<>();
        for (PyTupleObj<PyBoolObj> tup : nestedVal) {
            nestedListVal.add(new PyListObj<>(new ArrayList<>(tup.getValue())));
            nestedListVal2.add(new PyListObj<>(new ArrayList<>(tup.getValue())));
        }
        nestedList = new PyListObj<>(nestedListVal);
        nestedList2 = new PyListObj<>(nestedListVal2);
    }

    /**
     * Tests getValue() on a non-empty nested list.
     */
    @Test
    @Tag("0.25")
    @Order(1)
    void testGetValueNested() {
        assertEquals(nestedListVal, nestedList2.getValue());
    }

    /**
     * Tests toString() on an empty list.
     */
    @Test
    @Tag("0.25")
    @Order(2)
    void testToStringEmpty() {
        assertEquals("[]", emptyFloatList.toString());
    }

    /**
     * Tests toString() on a list containing a single element.
     */
    @Test
    @Tag("0.25")
    @Order(3)
    void testToStringSimpleSingle() {
        assertEquals("[1]", nonEmptyIntSubsetList.toString());
    }

    /**
     * Tests toString() on a list containing multiple elements.
     */
    @Test
    @Tag("0.25")
    @Order(4)
    void testToStringSimpleMultiple() {
        assertEquals("[3.1, 2.3, 6.0]", nonEmptyFloatList.toString());
    }

    /**
     * Tests toString() on an empty nested list.
     */
    @Test
    @Tag("0.25")
    @Order(5)
    void testToStringNestedEmpty() {
        assertEquals("[]", emptyNestedList.toString());
    }

    /**
     * Tests toString() on a non-empty nested list.
     */
    @Test
    @Tag("0.25")
    @Order(6)
    void testToStringNestedMultiple() {
        assertEquals("[[True, True], [True, False], [False, False, True, False]]",
            nestedList.toString());
    }

    /**
     * Tests equals() on two empty lists of different declared inner types (should still
     * be considered equal).
     */
    @Test
    @Tag("0.5")
    @Order(7)
    void testEqualsEmptyDifferent() {
        assertEquals(emptyFloatList, emptyNestedList);
    }

    /**
     * Tests equals() on two non-empty lists containing identical elements (but whose
     * underlying lists are unique objects).
     */
    @Test
    @Tag("0.25")
    @Order(8)
    void testEqualsSimple() {
        assertEquals(nonEmptyFloatList, nonEmptyFloatList2);
    }

    /**
     * Tests equals() on two non-empty lists containing different elements.
     */
    @Test
    @Tag("0.25")
    @Order(9)
    void testNotEqual() {
        assertNotEquals(nonEmptyFloatList3, nonEmptyFloatList);
    }

    /**
     * Tests equals() on two non-empty lists containing different elements, where one is a
     * subset of the other.
     */
    @Test
    @Tag("0.25")
    @Order(10)
    void testNotEqualSubset1() {
        assertNotEquals(nonEmptyIntSubsetList, nonEmptyIntList);
    }

    /**
     * Tests equals() on two non-empty lists containing different elements, where one is a
     * subset of the other (in the opposite direction of testNotEqualSubset1).
     */
    @Test
    @Tag("0.25")
    @Order(11)
    void testNotEqualSubset2() {
        assertNotEquals(nonEmptyIntList, nonEmptyIntSubsetList);
    }

    /**
     * Tests equals() on two non-empty nested lists.
     */
    @Test
    @Tag("0.5")
    @Order(12)
    void testEqualsNested() {
        assertEquals(nestedList, nestedList2);
    }

    /**
     * Tests that hashCode() returns the same value for two nested lists containing
     * identical elements.
     */
    @Test
    @Tag("0.5")
    @Order(13)
    void testHashCodeNested() {
        assertEquals(nestedList.hashCode(), nestedList2.hashCode());
    }
}