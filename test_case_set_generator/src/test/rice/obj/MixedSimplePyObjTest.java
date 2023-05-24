package test.rice.obj;

import main.rice.obj.PyBoolObj;
import main.rice.obj.PyFloatObj;
import main.rice.obj.PyIntObj;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertNotEquals;

/**
 * Test cases using a combination of PyBoolObjs, PyIntObjs, and PyFloatObjs.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class MixedSimplePyObjTest {

    /**
     * A PyBoolObj, PyIntObj, and PyFloatObj that all have the "same" internal value (1).
     */
    private static PyFloatObj float1 = new PyFloatObj(1.0);
    private static PyIntObj int1 = new PyIntObj(1);
    private static PyBoolObj bool1 = new PyBoolObj(true);

    /**
     * Tests that a PyIntObj and PyBoolObj with the "same" internal value don't count as
     * being equal (according to the PyIntObj .equals() method).
     */
    @Test
    @Tag("0.5")
    @Order(1)
    void testIntNotEqualsBool() {
        assertNotEquals(int1, bool1);
    }

    /**
     * Tests that a PyIntObj and PyBoolObj with the "same" internal value don't count as
     * being equal (according to the PyBoolObj .equals() method).
     */
    @Test
    @Tag("0.5")
    @Order(2)
    void testBoolNotEqualsInt() {
        assertNotEquals(bool1, int1);
    }

    /**
     * Tests that a PyIntObj and PyFloatObj with the "same" internal value don't count as
     * being equal (according to the PyIntObj .equals() method).
     */
    @Test
    @Tag("1.25")
    @Order(3)
    void testIntNotEqualsFloat() {
        assertNotEquals(int1, float1);
    }

    /**
     * Tests that a PyIntObj and PyFloatObj with the "same" internal value don't count as
     * being equal (according to the PyFloatObj .equals() method).
     */
    @Test
    @Tag("1.25")
    @Order(4)
    void testFloatNotEqualsInt() {
        assertNotEquals(float1, int1);
    }

    /**
     * Tests that a PyFloatObj and PyBoolObj with the "same" internal value don't count as
     * being equal (according to the PyBoolObj .equals() method).
     */
    @Test
    @Tag("0.5")
    @Order(5)
    void testBoolNotEqualsFloat() {
        assertNotEquals(bool1, float1);
    }

    /**
     * Tests that a PyFloatObj and PyBoolObj with the "same" internal value don't count as
     * being equal (according to the PyFloatObj .equals() method).
     */
    @Test
    @Tag("0.5")
    @Order(6)
    void testFloatNotEqualsBool() {
        assertNotEquals(float1, bool1);
    }
}