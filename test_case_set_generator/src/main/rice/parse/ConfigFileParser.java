package main.rice.parse;

import main.rice.node.*;
import main.rice.obj.*;
import org.json.*;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

/**
 * Class used to read and parse the contents of a file, presumed to be in the
 * format of a JSON object.
 */
public class ConfigFileParser {

    /**
     * Returns the entire contents of the file at the given path into a single String.
     *
     * @param filepath the path of the file to be read
     * @return the contents of the file in a single String
     * @throws IOException thrown when file at given path doesn't exist or can't be read
     */
    public String readFile(String filepath) throws IOException {
        return Files.readString(Paths.get(filepath));
    }

    /**
     * Parses the input string, which should be the contents of a JSON file formatted
     * according to the config file specifications. When parsing the contents, it builds
     * a tree of Python nodes for each parameter each according to its type, exhaustive
     * domain, and random domain. It also parses/interprets the name of the function
     * under test and the amount of random test cases to generate. The function name,
     * quantity of random test cases, and generation tree are arguments for the config
     * file returned.
     *
     * @param contents the contents of configuration file
     * @return configuration file containing the information for the function under test
     * @throws InvalidConfigException if there's a syntax error in the file contents
     */
    public ConfigFile parse(String contents) throws InvalidConfigException {
        //Ensure that contents are a properly formatted JSON object
        JSONObject jObj;
        try {
            jObj = new JSONObject(contents);
        } catch (JSONException e) {
            throw new InvalidConfigException(e.getMessage());
        }

        //Check that all necessary fields are in file
        String[] fields =
                {"fname", "types", "exhaustive domain", "random domain", "num random"};
        for (String field : fields) {
            if (!jObj.has(field)) {
                throw new InvalidConfigException("Missing " + field + " field");
            }
        }

        //Check that the parameters, exhaustive, and random domains are in arrays
        for (int i = 1; i <= 3; i++) {
            if (!(jObj.get(fields[i]) instanceof org.json.JSONArray)) {
                throw new InvalidConfigException(fields[i] + " value isn't an array");
            }
        }

        //parse the parameter types and domains, creating the generator nodes
        List<APyNode<? extends APyObj>> nodes =
                this.parseTypes((JSONArray) jObj.get("types"));
        this.parseDomains((JSONArray) jObj.get("exhaustive domain"), nodes, true);
        this.parseDomains((JSONArray) jObj.get("random domain"), nodes, false);

        //Check that the function name
        if (!(jObj.get("fname") instanceof java.lang.String)) {
            throw new InvalidConfigException("fname value isn't a string");
        }
        //check that the number of random test cases is an int
        if (!(jObj.get("num random") instanceof java.lang.Integer)) {
            throw new InvalidConfigException("num random value isn't an integer");
        }

        String funcName = (String) jObj.get("fname");
        int numRand = (int) jObj.get("num random");

        return new ConfigFile(funcName, nodes, numRand);
    }

    /**
     * Parses the values of the given JSON Array by calling a helper function that parses
     * each value individually. The helper generates an APyNode root for each generation
     * tree, and this method compiles the roots into a list of APyNodes.
     *
     * @param obj JSONArray whose values are to be parsed
     * @return a list of APyNodes corresponding to the parameter types
     * @throws InvalidConfigException if parameter types aren't written in proper syntax
     */
    private List<APyNode<? extends APyObj>> parseTypes(JSONArray obj)
            throws InvalidConfigException {
        List<APyNode<? extends APyObj>> types = new ArrayList<>();
        for (Object type : obj) {
            types.add(this.parseType((String) type));
        }
        return types;
    }

    /**
     * Parses the individual value passed in that is a parameter type to create the root
     * of a generation tree of APyNodes along with any APyNode children specified.
     *
     * @param t the parameter type
     * @return APyNode of the parameter type specifies.
     * @throws InvalidConfigException if the parameter type isn't written in proper syntax
     */
    private APyNode<? extends APyObj> parseType(String t)
            throws InvalidConfigException {

        String type = t.trim();

        //parse simple types
        if (type.matches("int")) {
            return new PyIntNode();
        } else if (type.matches("float")) {
            return new PyFloatNode();
        } else if (type.matches("bool")) {
            return new PyBoolNode();
        }

        int parenIdx = type.indexOf("(");
        String value = type.substring(parenIdx + 1).trim();
        int colonIdx = value.indexOf(":");

        //parse a string, list, tuple, set, or dict
        if (type.startsWith("str") || this.isIterableType(type) || type
                .startsWith("dict")) {
            //Must have parenthesis followed by another value
            if (parenIdx == -1) {
                throw new InvalidConfigException("Missing parenthesis in " + type);
            }
            if (value.length() == 0) {
                //strings need to be followed by a char domain
                if (type.startsWith("str")) {
                    throw new InvalidConfigException("No char domain for " + type);
                }
                //list, tuple, set and dict need to be followed by more types
                else {
                    throw new InvalidConfigException(
                            "No further types specified in " + type);
                }
            }

            //parse string
            if (type.startsWith("str")) {
                return new PyStringNode(value);
            }

            //parse a list, tuple, or set
            else if (this.isIterableType(type)) {
                //Recursive call to parse inner type
                APyNode<? extends APyObj> innerType = parseType(value);

                //Generating the coordinating list, tuple, or set node
                if (type.startsWith("list")) {
                    return new PyListNode<>(innerType);
                } else if (type.startsWith("tuple")) {
                    return new PyTupleNode<>(innerType);
                } else {
                    return new PySetNode<>(innerType);
                }

            }

            //parse a dict
            if (colonIdx == -1) {
                throw new InvalidConfigException("Missing colon in " + type);
            }

            String[] keyValPair = new String[2];
            keyValPair[0] = value.substring(0, colonIdx).trim();
            keyValPair[1] = value.substring(colonIdx + 1).trim();

            APyNode<? extends APyObj> key = this.parseType(keyValPair[0]);
            APyNode<? extends APyObj> val = this.parseType(keyValPair[1]);
            return new PyDictNode<>(key, val);

        }

        //Otherwise, no syntactically correct type specified
        throw new InvalidConfigException("Invalid syntax for " + type);
    }

    //if dom = 0, then parse then set exDomains; if dom = 1, then parse ranDomains

    /**
     * Parses the values of the given JSON array by calling a helper function that parses
     * each value individually. The helper assigns the exhaustive or random domain to the
     * coordinating APyNode as specified. If exDom is true, the exhaustive domain is set.
     * If
     * exDom is false, the random domain is set.
     *
     * @param arr the JSON array whose values are to be parsed
     * @param nodes list of APyNodes whose exhaustive or random domains will be set
     * @param exDom true if exhaustive domain is set, false if random domain
     * @throws InvalidConfigException if a domain isn't written in proper syntax
     */
    private void parseDomains(JSONArray arr, List<APyNode<? extends APyObj>> nodes,
                              boolean exDom)
            throws InvalidConfigException {
        //Incorrect amount of domains
        if (arr.length() != nodes.size()) {
            throw new InvalidConfigException(
                    "Need " + nodes.size() + " domains but found " + arr.length());
        }

        //Parse each domain individually
        for (int i = 0; i < arr.length(); i++) {
            this.parseDomain((String) arr.get(i), nodes.get(i), exDom);
        }
    }

    private void parseDomain(String domain, APyNode<? extends APyObj> node, boolean exDom)
            throws InvalidConfigException {
        int parenIdx = domain.indexOf("(");
        int tildeIdx = domain.indexOf("~");
        int bracketIdx = domain.indexOf("[");
        //parse list, tuple, set, or dict
        if (this.isIterableType(node) || node instanceof PyDictNode) {
            //Must have parenthesis followed by another value
            if (parenIdx == -1) {
                throw new InvalidConfigException("Missing parenthesis in " + domain);
            }
            //A dictionary must have a colon in the domain
            if (node instanceof PyDictNode && !domain.contains(":")) {
                throw new InvalidConfigException("Missing colon in " + domain);
            }
        }

        //parse an int, float, bool, or string
        else if (this.isSimpleType(node) || node instanceof PyStringNode) {
            //Must not contain
            if (parenIdx > -1) {
                throw new InvalidConfigException("\"(\" unexpected in " + domain);
            }
            if (domain.contains(":")) {
                throw new InvalidConfigException("\":\" unexpected in " + domain);
            }
        }

        //Must have tilde or bracket
        if ((tildeIdx == -1 && bracketIdx == -1)) {
            throw new InvalidConfigException("Missing \"~\" or \"[\" at " + domain);
        }

        //Must not have tilde and bracket in same domain specification
        if ((tildeIdx > -1 && bracketIdx > -1)) {
            throw new InvalidConfigException(
                    "Improper syntax for \"~\" and \"[\" at " + domain);
        }

        List<Number> domainRange;

        //Parse domain with tilde notation
        if (tildeIdx > -1) {
            domainRange = this.parseTildeNotation(domain, tildeIdx, node, exDom);
        }

        //Parse domain with bracket notation
        else {
            domainRange = this.parseBracketNotation(domain, bracketIdx, node, exDom);
        }

        //Set the nodes exhaustive or random domain
        if (domainRange.size() != 0) {
            if (this.domainRangeIsValid(domainRange, node)) {
                //set the exhaustive domain of the node
                if (exDom) {
                    node.setExDomain(domainRange);
                }
                //set the rand domain of the node
                else {
                    node.setRanDomain(domainRange);
                }
            } else {
                throw new InvalidConfigException(
                        "Domain range" + domain + " is invalid");
            }
        }
    }

    /**
     * Enumerates the whole numbers from start to stop, inclusively, into a List of
     * Numbers. If areInts is true, then integers are put in the list of Numbers. If
     * areInts is false, then floats are put in the list of Numbers.
     *
     * @param start the beginning number to enumerate from
     * @param stop the last number to enumerate to
     * @param areInts Weather or not the List of numbers should be integers
     * @return a List of numbers that is the enumeration from start to stop
     * @throws InvalidConfigException if start is greater than stop
     */
    private List<Number> enumerateNumbers(int start, int stop, boolean areInts)
            throws InvalidConfigException {
        List<Number> nums = new ArrayList<>();

        //check that lower bound doesn't exceed upper bound
        if (start > stop) {
            throw new InvalidConfigException(
                    "Lower bound " + start + "exceeds upper bound " + stop);
        }

        //Populate the list of numbers with ints or floats accordingly
        for (int i = start; i <= stop; i++) {
            if (areInts) {
                nums.add(i);
            } else {
                nums.add((double) i);
            }
        }
        return nums;
    }

    /**
     * Determines if the given domain range is valid for the given node. The limits of
     * the domains by node type are as follows: PyBoolNode can only have the intergers 1
     * and 0. PyIntNode can only have integers. PyFloatNode can have floats (or
     * integers). PyDictNode, PyListNode, PySetNode, and PyStringNode can only have
     * positive integers. Assumes that domainRange is not empty. Assumes that all of the
     * numbers in domainRange are of the same type.
     *
     * @param domainRange the domain range that may or may not be valid for node
     * @param node a APyNode which can only have certain domain values
     * @return true if the domain is valid, false otherwise
     */
    private boolean domainRangeIsValid(List<Number> domainRange,
                                       APyNode<? extends APyObj> node) {

        //Only float types can have decimals
        if (!(node instanceof PyFloatNode) && this.isListOfDecimals(domainRange)) {
            return false;
        }

        //booleans cannot have values outside of ints 1 and 0
        if (node instanceof PyBoolNode) {
            for (Number num : domainRange) {
                if (num.intValue() != 0 && num.intValue() != 1) {
                    return false;
                }
            }
        }

        //Only ints and float can have negative domain values
        else if (!(node instanceof PyIntNode || node instanceof PyFloatNode)) {
            return !hasNegativeVals(domainRange);
        }
        return true;
    }

    /**
     * Determines if the given node is a PyBoolNode, PyIntNode, or PyFloatNode.
     *
     * @param node the node to be evaluated
     * @return true if node is a generator for python bools, ints, or floats
     */
    private boolean isSimpleType(APyNode<? extends APyObj> node) {
        return node instanceof PyBoolNode || node instanceof PyFloatNode
                || node instanceof PyIntNode;
    }

    /**
     * Determines if the given node is a PyListNode, PyTypleNode, or PySetNode.
     *
     * @param node the node to be evaluated
     * @return true if node is a generator for python lists, tuples, or sets
     */
    private boolean isIterableType(APyNode<? extends APyObj> node) {
        return node instanceof PyListNode || node instanceof PyTupleNode
                || node instanceof PySetNode;
    }

    //returns true if the given string starts with "list", "tuple" or "set"

    /**
     * Determines if the given string type is for a list, tuple, or set.
     *
     * @param t the string to be evaluated
     * @return true if string starts with list, tuple, or set
     */
    private boolean isIterableType(String t) {
        return t.startsWith("list") || t.startsWith("tuple") || t.startsWith("set");
    }

    /**
     * Determines if the given list of numbers is a list of decimals. Assumes that all of
     * the numbers in the list are decimal, or none are. Assumes that the list is not
     * empty.
     *
     * @param nums the list of numbers
     * @return true if list of numbers has decimals, false otherwise
     */
    private boolean isListOfDecimals(List<Number> nums) {
        //try to get teh int value of a list number
        try {
            nums.get(0).intValue();
            return false;
            //if couldn't retrieve int value list number, then the list must have
            // decimals!
        } catch (NumberFormatException e) {
            return true;
        }
    }

    /**
     * Determines if the given list of numbers has any negative values.
     *
     * @param nums the list of numbers
     * @return true if list has negative numbers, false otherwise
     */
    private boolean hasNegativeVals(List<Number> nums) {
        //Return false if a negative number is found while iterating through the list
        for (Number num : nums) {
            if (num.floatValue() < 0) {
                return true;
            }
        }
        return false;
    }

    /**
     * Parses the given domain. Assumes that the domain contains an open bracket. Removes
     * duplicates from the specified domain, if any. Checks if all the numbers in the
     * domain are of the right type (i.e. decimal or int) with their node.
     *
     * @param domain the domain to be parsed
     * @param bracketIn the index of the opening bracket
     * @param node the node coordinating with the given domain
     * @param exDom whether or not the given domain is exhaustive for node
     * @return a list of numbers spanning the domain given
     * @throws InvalidConfigException if the domain is syntactically incorrect
     */
    private List<Number> parseBracketNotation(String domain, int bracketIn,
                                              APyNode<? extends APyObj> node, boolean exDom)
            throws InvalidConfigException {
        //Must contain close bracket
        if (!domain.contains("]")) {
            throw new InvalidConfigException("Missing ] in " + domain);
        }

        //If there is an inner domain, parse that
        if (domain.contains("(")) {
            //parse a list, tuple, or set inner domain
            if (this.isIterableType(node)) {
                this.parseDomain(this.getInnerDomain(domain), node.getLeftChild(), exDom);
            }
            //parse a dict inner domain
            else if (node instanceof PyDictNode) {
                this.parseDictChildren(domain, node, exDom);
            }
        }

        //Return the domain for this node, as a list of numbers
        String range = domain.substring(bracketIn + 1, domain.indexOf("]"));
        return this.stringArrayToNumList(range.split(","));
    }

    /**
     * Parses the given domain. Assumes that the domain contains a tilde. Checks the
     * given domain is a range of integers. Assumes that if domain contains an open
     * parentheses, then the domain is for a list, set, tuple or dict
     *
     * @param domain the domain to be parsed
     * @param tildeIdx the index of the tilde
     * @param node the node coordinating with the given domain
     * @param exDom whether or not the given domain is exhaustive for node
     * @return a list of numbers spanning the range given
     * @throws InvalidConfigException if the domain is syntactically incorrect
     */
    private List<Number> parseTildeNotation(String domain, int tildeIdx,
                                            APyNode<? extends APyObj> node, boolean exDom)
            throws InvalidConfigException {
        String leftField = domain.substring(0, tildeIdx).trim(), rightField;

        //parse domain for list, set, tuple or dict
        if (domain.contains("(")) {
            rightField = domain.substring(tildeIdx + 1, domain.indexOf("(")).trim();
            //parse domain for list, set or tuple
            if (this.isIterableType(node)) {
                this.parseDomain(this.getInnerDomain(domain), node.getLeftChild(), exDom);
            }
            //Otherwise, parse the children for a dict
            else {
                this.parseDictChildren(domain, node, exDom);
            }

        }
        //Otherwise, everything to the right of the tilde should be a number
        else {
            rightField = domain.substring(tildeIdx + 1).trim();
        }

        int lower, upper;

        //The upper and lower bounds of the range must be integers
        try {
            lower = Integer.parseInt(leftField);
            try {
                upper = Integer.parseInt(rightField);
            } catch (NumberFormatException numE) {
                throw new InvalidConfigException(
                        rightField + " in " + domain + " is not an int");
            }
        } catch (NumberFormatException intE) {
            throw new InvalidConfigException(
                    leftField + " in " + domain + " is not an int");
        }

        //Enumerate the range between the upper and lower bound
        try {
            return this.enumerateNumbers(lower, upper, !(node instanceof PyFloatNode));
        } catch (InvalidConfigException e) {
            throw new InvalidConfigException(e.getMessage() + "in " + domain);
        }
    }

    /**
     * Parses the given string array into a list of numbers. Removes duplicates if
     * present. All of the numbers are decimals or none are decimals.
     *
     * @param array the string array of supposed numbers
     * @return the parsed list of numbers
     * @throws InvalidConfigException the array elements aren't numbers
     */
    private List<Number> stringArrayToNumList(String[] array)
            throws InvalidConfigException {

        //store numbers in a set to avoid duplicates
        Set<Number> nums = new HashSet<>();
        boolean isInt = false, isFloat = false;

        for (String num : array) {
            String n = num.trim();
            //if the number type hasn't been determined yet, set it
            if (!(isInt || isFloat)) {
                try {
                    nums.add(Integer.parseInt(n));
                    isInt = true;
                } catch (NumberFormatException intE) {
                    try {
                        nums.add(Double.parseDouble(n));
                        isFloat = true;
                    } catch (NumberFormatException doubleE) {
                        throw new InvalidConfigException(
                                "Domain range " + Arrays.toString(array)
                                        + " doesn't contain all numbers");
                    }
                }
            }

            //if the number type is int
            else if (isInt) {
                try {
                    nums.add(Integer.parseInt(n));
                } catch (NumberFormatException intE) {
                    throw new InvalidConfigException(
                            n + " is not a valid int in " + Arrays.toString(array));
                }
            }

            //Otherwise, the number type must be float
            else {
                try {
                    nums.add(Double.parseDouble(n));
                } catch (NumberFormatException doubleE) {
                    throw new InvalidConfigException(
                            n + " is not a valid float in" + Arrays.toString(array));
                }
            }
        }

        return new ArrayList<>(nums);
    }

    /**
     * Returns the portion of the given domain (as a string) to the right of the open
     * parentheses to get the the domain of the "inner" type. Assumes that the given
     * string contains an open parentheses.
     *
     * @param domain the string representation
     * @return a string representation of the inner domain
     */
    private String getInnerDomain(String domain) {
        return domain.substring(domain.indexOf("(") + 1).trim();
    }

    /**
     * Parses the domains for the children of the given node. Assumes that the given node
     * is a PyDictNode.
     *
     * @param domain the domain of the children to be parsed
     * @param node the PyDictNode whose children's domains will be parsed
     * @param exDom whether or not the given domain is exhaustive for node
     * @throws InvalidConfigException if a domain isn't written in proper syntax
     */
    private void parseDictChildren(String domain, APyNode<? extends APyObj> node,
                                   boolean exDom) throws InvalidConfigException {
        String[] keyValDomains = new String[2];
        String innerDomain = this.getInnerDomain(domain);
        int colonIdx = innerDomain.indexOf(":");

        //parse the domains of the children
        keyValDomains[0] = innerDomain.substring(0, colonIdx).trim();
        keyValDomains[1] = innerDomain.substring(colonIdx + 1).trim();
        this.parseDomain(keyValDomains[0], node.getLeftChild(), exDom);
        this.parseDomain(keyValDomains[1], node.getRightChild(), exDom);
    }
}
