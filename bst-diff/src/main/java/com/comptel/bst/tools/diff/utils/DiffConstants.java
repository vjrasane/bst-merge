package com.comptel.bst.tools.diff.utils;

public class DiffConstants {

    // COMMAND LINE PARAMETER CONSTANTS

    // full
    public static final String DERIVED_FILE_ARG = "-derived";

    // abbreviated
    public static final String DERIVED_FILE_ARG_ABBR = "-d";

    // ATTRIBUTE & DATA PREFIXES

    public static final String BST_PREFIX = "bst-mergetool";
    public static final String ATTR_PREFIX = BST_PREFIX + ":attr";
    public static final String DATA_PREFIX = BST_PREFIX + ":data";

    // DEFAULT VALUES

    public static final String DEFAULT_LINK_VALUE = "0";

    // OUTPUT CONSTANTS

    public static final String DIFF_PROGRAM_NAME = "BST Diff Tool";

    // phase descriptions
    public static final String DIFF_COMP_PHASE = "Calculating differences";;
    public static final String DIFF_DETECT_PHASE = "Detected differences:\n";
    public static final String PARSE_PHASE = "Parsing files to internal objects";

    // diff tree markers
    protected static final String SUB_ELEMENT_MARKER_EXTEND =  "|  ";
    protected static final String SUB_ELEMENT_MARKER =         "├──";
    protected static final String SUB_ELEMENT_MARKER_LAST =    "└──";
    protected static final String SUB_ELEMENT_MARKER_INDENT =  "   ";

    protected static final String SUB_ELEMENT_MARKER_ASCII =         "+--";
    protected static final String SUB_ELEMENT_MARKER_LAST_ASCII =    "\\--";

    public static final String OUTPUT_ELEMENT_MARKER = "* ";

    public static final String CONFLICT_MSG_INDENT = "    ";

}
