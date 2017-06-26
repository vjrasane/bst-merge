package com.comptel.bst.tools.mergetool.utils;

public interface MergeConstants {

    // FILES AND DIRECTORIES

    // resource and output directories
    public static final String TEST_RESOURCE_DIR = "src/test/resources/";
    public static final String MAIN_RESOURCE_DIR = "src/main/resources/";
    public static final String OUTPUT_DIR = "src/test/output/";

    // COMMAND LINE PARAMETER CONSTANTS

    // mandatory file parameters
    public static final String BASE_FILE_ARG = "-base";
    public static final String LOCAL_FILE_ARG = "-local";
    public static final String REMOTE_FILE_ARG = "-remote";
    public static final String OUTPUT_FILE_ARG = "-output";

    // operations
    public static final String MERGE_OPERATION = "merge";
    public static final String DIFF_OPERATION = "diff";

    // additional parameters
    public static final String FORCE_ARG = "-force";
    public static final String INTERACTIVE_ARG = "-interactive";
    public static final String DRY_RUN_ARG = "-dry";

    // misc
    public static final String PRINT_DURATION_ARG = "-duration";

    // abbreviated
    public static final String LOCAL_FILE_ARG_ABBR = "-l";
    public static final String REMOTE_FILE_ARG_ABBR = "-r";

    // OUTPUT MESSAGE CONSTANTS

    // phases
    public static final String COMPOSE_PHASE = "Composing result YAML";

    // interactive prompt line markers
    public static final String REPLY_MARKER = ">> ";

    public static final String MERGE_PROGRAM_NAME = "BST Merge Tool";

}
