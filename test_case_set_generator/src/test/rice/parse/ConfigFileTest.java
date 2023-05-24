package test.rice.parse;

import main.rice.node.APyNode;
import main.rice.node.PyBoolNode;
import main.rice.node.PyIntNode;
import main.rice.parse.ConfigFile;
import org.junit.jupiter.api.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * Test cases for the ConfigFile class.
 */
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
class ConfigFileTest {

    /**
     * Tests the getFuncName() method of the ConfigFile class.
     */
    @Test
    @Tag("0.5")
    @Order(1)
    void testGetFuncName() {
        assertEquals("test", new ConfigFile("test", null, 0).getFuncName());
    }

    /**
     * Tests the getNumRand() method of the ConfigFile class.
     */
    @Test
    @Tag("0.5")
    @Order(2)
    void testGetNumRand() {
        assertEquals(17, new ConfigFile(null, null, 17).getNumRand());
    }

    /**
     * Tests the getNodes() method of the ConfigFile class.
     */
    @Test
    @Tag("0.5")
    @Order(3)
    void testGetNodes() {
        // First node: a boolean
        PyBoolNode node1 = new PyBoolNode();
        node1.setExDomain(Arrays.asList(0, 1));
        node1.setRanDomain(Arrays.asList(0, 1));

        // Second node: an integer
        PyIntNode node2 = new PyIntNode();
        node2.setExDomain(Arrays.asList(0, 1, 2));
        node2.setRanDomain(Arrays.asList(3, 4, 5));

        List<APyNode<?>> nodes = Arrays.asList(node1, node2);
        assertEquals(new ArrayList<>(nodes), new ConfigFile(null, nodes, 0).getNodes());
    }
}