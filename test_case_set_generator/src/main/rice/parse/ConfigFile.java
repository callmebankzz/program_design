package main.rice.parse;

import main.rice.node.APyNode;
import main.rice.obj.APyObj;

import java.util.List;

/**
 * This class bundles together the data from a config file. This includes the name of the
 * function to be tested, the python object generators, and the number of random test
 * cases that should be generated.
 */
public class ConfigFile {

    /**
     * The name of the function to be tested
     */
    private String funcName;

    /**
     * The list of python object generators
     */
    private List<APyNode<?>> nodes;

    /**
     * The number of random test cases to be generated
     */
    private int numRand;

    /**
     * Constructor for a ConfigFile object taking in the name of the function to be
     * tested, the python object generators, and the amount of random test cases to be
     * generated.
     *
     * @param funcName the name of the function to be tested
     * @param nodes list of python nodes that serve as object generators
     * @param numRand the number of random test cases to be generated
     */
    public ConfigFile(String funcName, List<APyNode<? extends APyObj>> nodes, int numRand) {
        this.funcName = funcName;
        this.nodes = nodes;
        this.numRand = numRand;
    }

    /**
     * Returns the name of the function to be tested.
     *
     * @return the name of the tested function
     */
    public String getFuncName() {
        return funcName;
    }

    /**
     * Returns the list of Python nodes serving as generators for the function to be
     * tested
     *
     * @return list of Python object generators
     */
    public List<APyNode<?>> getNodes() {
        return nodes;
    }

    /**
     * Returns the number of random test cases passed in to the constructor.
     *
     * @return the number of random test cases to be generated
     */
    public int getNumRand() {
        return numRand;
    }
}
