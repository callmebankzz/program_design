package main.rice.obj;

import java.util.Map;

/**
 * A representation of Python objects of type dict.
 *
 * @param <KeyType> the type of each key in the dictionary
 * @param <ValType> the type of each value in the dictionary
 */
public class PyDictObj<KeyType extends APyObj, ValType extends APyObj> extends APyObj {

    /**
     * The contents of this PyDictObj.
     */
    private final Map<KeyType, ValType> value;

    /**
     * Constructor for a PyDictObj; initializes its value to the input.
     *
     * @param value the value of this PyDictObj
     */
    public PyDictObj(Map<KeyType, ValType> value) {
        this.value = value;
    }

    /**
     * @return the underlying (Java Map) representation of this PyDictObj
     */
    @Override
    public Map<KeyType, ValType> getValue() {
        return this.value;
    }

    /**
     * Builds and returns a string representation of this object that mirrors the Python
     * string representation (i.e., {key1: val1, key2: val2, ...}).
     *
     * @return a string representation of this object
     */
    @Override
    public String toString() {
        // Note: this also could have been done by manually constructing the string using a
        // StringBuilder, as in PyTupleObj. I wasn't expecting you to use replace, but since
        // I directed you to the String API in my announcement I figured I'd switch to using
        // it, since it's a more elegant solution.
        return this.value.toString().replace("=", ": ");
    }
}
