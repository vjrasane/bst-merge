package com.comptel.bst.tools.mergetool;

import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.commons.io.FileUtils;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.contrib.java.lang.system.ExpectedSystemExit;

import com.comptel.bst.tools.common.CommonConstants;
import com.comptel.bst.tools.diff.comparison.differences.Difference;
import com.comptel.bst.tools.diff.parser.BSTReader;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.utils.DiffUtils;
import com.comptel.bst.tools.diff.utils.OutputCharset;
import com.comptel.bst.tools.mergetool.cli.CommandLineTool;
import com.comptel.bst.tools.mergetool.cli.Force;
import com.comptel.bst.tools.mergetool.utils.MergeConstants;

public class BSTMergeToolTest extends ToolTest {

    @Rule
    public final ExpectedSystemExit exit = ExpectedSystemExit.none();

    @Before
    public void setup() throws IOException {
        FileUtils.copyFile(new File(BST_EXAMPLE_YAML), new File(BST_EXAMPLE_YAML_COPY));
    }

    @Test
    public void command_line___inexistent_all() {
        exit.expectSystemExitWithStatus(1);
        CommandLineTool.main(buildMergeArgs("this file doesn't exist", "this file doesn't exist", "this file doesn't exist",
                "this file does not exist"));
    }

    @Test
    public void command_line___inexistent_base() throws IOException {
        exit.expectSystemExitWithStatus(1);
        CommandLineTool
        .main(buildMergeArgs("this file doesn't exist", BST_EXAMPLE_YAML_COPY, BST_EXAMPLE_YAML_COPY, BST_EXAMPLE_YAML_COPY));
    }

    @Test
    public void command_line___inexistent_local() throws IOException {
        exit.expectSystemExitWithStatus(1);
        CommandLineTool
        .main(buildMergeArgs(BST_EXAMPLE_YAML_COPY, "this file doesn't exist", BST_EXAMPLE_YAML_COPY, BST_EXAMPLE_YAML_COPY));
    }

    @Test
    public void command_line___inexistent_remote() throws IOException {
        exit.expectSystemExitWithStatus(1);
        CommandLineTool
        .main(buildMergeArgs(BST_EXAMPLE_YAML_COPY, BST_EXAMPLE_YAML_COPY, "this file doesn't exist", BST_EXAMPLE_YAML_COPY));
    }

    @Test
    public void test_different_files() throws IOException {
        CommandLineTool.main(buildMergeArgs(getCopy(BST_BASE_YAML), getCopy(BST_LOCAL_YAML), getCopy(BST_REMOTE_YAML),
                new File(OUTPUT_FILE), CommonConstants.OUTPUT_CHARSET_ARG, OutputCharset.ASCII.name()));
        assertTrue(filesEqual(OUTPUT_FILE, BST_EXPECTED_YAML));
    }

    @Test
    public void test_different_files___local_conflict() throws IOException {
        exit.expectSystemExitWithStatus(1);
        CommandLineTool.main(buildMergeArgs(getCopy(BST_BASE_YAML), getCopy(BST_LOCAL_CONFLICT_YAML), getCopy(BST_REMOTE_YAML), new File(
                OUTPUT_FILE), CommonConstants.OUTPUT_CHARSET_ARG, OutputCharset.ASCII.name()));
    }

    @Test
    public void test_strategy_args___incorrect() throws IOException {
        exit.expectSystemExitWithStatus(1);
        CommandLineTool.main(buildMergeArgs(getCopy(BST_BASE_YAML), getCopy(BST_BASE_YAML), getCopy(BST_BASE_YAML), new File(OUTPUT_FILE),
                MergeConstants.FORCE_ARG, "incorrect"));
    }

    @Ignore
    @Test
    public void test_parameter_moved___no_conflict() throws IOException {
        CommandLineTool.main(buildMergeArgs(getCopy(BST_BASE_YAML), getCopy(BST_LOCAL_YAML), getCopy(PARAMETER_MOVED),
                new File(OUTPUT_FILE)));
        assertTrue(filesEqual(OUTPUT_FILE, EXPECTED_PARAMETER_MOVED));
    }

    @Ignore
    @Test
    public void test_parameter_moved___reverse() throws IOException {
        CommandLineTool.main(buildMergeArgs(getCopy(BST_BASE_YAML), getCopy(PARAMETER_CHANGED), getCopy(PARAMETER_MOVED_REVERSE), new File(
                OUTPUT_FILE)));
        assertTrue(filesEqual(OUTPUT_FILE, EXPECTED_PARAMETER_MOVED_REVERSE));
    }

    private boolean filesEqual(String output, String expected) {
        Element outputYaml = BSTReader.readYaml(new File(output));
        Element expectedYaml = BSTReader.readYaml(new File(expected));

        List<Difference> diffs = DiffUtils.getDifferences(expectedYaml, outputYaml);
        return diffs.isEmpty() && outputYaml.equals(expectedYaml);
    }

    @Ignore
    @Test
    public void test_parameter_moved___conflict() throws IOException {
        exit.expectSystemExitWithStatus(1);
        CommandLineTool.main(buildMergeArgs(getCopy(BST_BASE_YAML), getCopy(BST_LOCAL_YAML), getCopy(PARAMETER_MOVED_CHANGED), new File(
                OUTPUT_FILE)));
    }

    @Ignore
    @Test
    public void test_parameter_moved___twice() throws IOException {
        CommandLineTool.main(buildMergeArgs(getCopy(BST_BASE_YAML), getCopy(PARAMETER_CHANGED_TWICE),
                getCopy(PARAMETER_MOVED_REVERSE_TWICE), new File(OUTPUT_FILE)));
    }

    @Test
    public void test_parameter_moved___mismatched_step() throws IOException {
        exit.expectSystemExitWithStatus(1);
        CommandLineTool.main(buildMergeArgs(getCopy(BST_BASE_YAML), getCopy(BST_LOCAL_YAML), getCopy(PARAMETER_MOVED_MISMATCHED_STEP),
                new File(OUTPUT_FILE)));
    }

    @Test
    public void test_equal_files() throws IOException {
        CommandLineTool.main(buildMergeArgs(BST_EXAMPLE_YAML_COPY, BST_EXAMPLE_YAML_COPY, BST_EXAMPLE_YAML_COPY, OUTPUT_FILE));
        assertTrue(filesEqual(OUTPUT_FILE, BST_EXAMPLE_FORMATED_YAML));
    }

    @Test
    public void test_irl_files() throws IOException {
        exit.expectSystemExitWithStatus(1);
        CommandLineTool.main(buildMergeArgs(getCopy(IRL_BASE), getCopy(IRL_LOCAL), getCopy(IRL_REMOTE), new File(OUTPUT_FILE)));
    }

    @Test
    public void test_irl_files___keep_local() throws IOException {
        CommandLineTool.main(buildMergeArgs(getCopy(IRL_BASE), getCopy(IRL_LOCAL), getCopy(IRL_REMOTE), new File(OUTPUT_FILE),
                MergeConstants.FORCE_ARG, Force.LOCAL.flag));
    }

    @Ignore
    @Test
    public void test_irl_reduced_files___keep_local() throws IOException {
        CommandLineTool.main(buildMergeArgs(getCopy(IRL_REDUCED_BASE), getCopy(IRL_REDUCED_LOCAL), getCopy(IRL_REDUCED_REMOTE), new File(
                OUTPUT_FILE), MergeConstants.FORCE_ARG, Force.LOCAL.flag));
        assertTrue(filesEqual(OUTPUT_FILE, IRL_REDUCED_EXPECTED));
    }

    @Test
    public void test_missing_options() throws IOException {
        exit.expectSystemExitWithStatus(1);
        CommandLineTool
        .main(new String[] { BST_EXAMPLE_YAML_COPY + " -base " + BST_EXAMPLE_YAML_COPY + " -remote " + BST_EXAMPLE_YAML_COPY });
    }

    @Test
    public void test_missing_params() {
        exit.expectSystemExitWithStatus(1);
        CommandLineTool.main(new String[] { "-local -base -remote -output" });
    }

    @Test
    public void test_no_params() {
        exit.expectSystemExitWithStatus(1);
        CommandLineTool.main(new String[] {});
    }

    @Test
    public void test_invalid_operation() {
        exit.expectSystemExitWithStatus(1);
        CommandLineTool.main(new String[] { "invalid" });
    }

    public static final String YAML_DIR = MergeConstants.TEST_RESOURCE_DIR + "yaml/";

    public static final String SINGLE_NODE = MergeConstants.TEST_RESOURCE_DIR + "SingleNode.jar";
    public static final String SINGLE_NODE_AFTER = MergeConstants.TEST_RESOURCE_DIR + "SingleNodeAddedAfter.jar";
    public static final String SINGLE_NODE_BEFORE = MergeConstants.TEST_RESOURCE_DIR + "SingleNodeAddedBefore.jar";
    public static final String SINGLE_NODE_DESC = MergeConstants.TEST_RESOURCE_DIR + "SingleNodeAddedDesc.jar";

    public static final String BST_EXAMPLE_YAML_COPY = MergeConstants.TEST_RESOURCE_DIR + "bst-example_copy.yaxml";

    public static final String OUTPUT_DIR = MergeConstants.OUTPUT_DIR + "yaml/";
    public static final String OUTPUT_FILE = OUTPUT_DIR + "output.yaxml";

    public static final String BST_EXAMPLE_YAML = YAML_DIR + "bst-example.yaxml";
    public static final String BST_EXAMPLE_FORMATED_YAML = YAML_DIR + "bst-example-formated.yaxml";
    public static final String BST_BASE_YAML = YAML_DIR + "bst-base.yaxml";
    public static final String BST_REMOTE_YAML = YAML_DIR + "bst-remote.yaxml";
    public static final String BST_LOCAL_YAML = YAML_DIR + "bst-local.yaxml";
    public static final String BST_EXPECTED_YAML = YAML_DIR + "bst-expected.yaxml";

    public static final String IRL_BASE = YAML_DIR + "irl-base.yaxml";
    public static final String IRL_REMOTE = YAML_DIR + "irl-remote.yaxml";
    public static final String IRL_LOCAL = YAML_DIR + "irl-local.yaxml";

    public static final String IRL_REDUCED_BASE = YAML_DIR + "irl-reduced-base.yaxml";
    public static final String IRL_REDUCED_REMOTE = YAML_DIR + "irl-reduced-remote.yaxml";
    public static final String IRL_REDUCED_LOCAL = YAML_DIR + "irl-reduced-local.yaxml";
    public static final String IRL_REDUCED_EXPECTED = YAML_DIR + "irl-reduced-local.yaxml";

    public static final String IRL_RESULT_FAILING = YAML_DIR + "irl-result-failing.yaxml";
    public static final String IRL_RESULT = YAML_DIR + "irl-result.yaxml";

    public static final String PARAMETER_MOVED = YAML_DIR + "parameter_moved.yaxml";

    public static final String PARAMETER_MOVED_CHANGED = YAML_DIR + "parameter_moved_changed.yaxml";
    public static final String PARAMETER_MOVED_MISMATCHED_STEP = YAML_DIR + "parameter_moved_mismatched_step.yaxml";
    public static final String PARAMETER_MOVED_REVERSE = YAML_DIR + "parameter_moved_reverse.yaxml";
    public static final String PARAMETER_MOVED_REVERSE_TWICE = YAML_DIR + "parameter_moved_reverse_twice.yaxml";

    public static final String PARAMETER_CHANGED = YAML_DIR + "param_changed.yaxml";
    public static final String PARAMETER_CHANGED_TWICE = YAML_DIR + "param_changed_twice.yaxml";

    public static final String EXPECTED_PARAMETER_MOVED = YAML_DIR + "expected_parameter_moved.yaxml";
    public static final String EXPECTED_PARAMETER_MOVED_REVERSE = YAML_DIR + "expected_parameter_moved_reverse.yaxml";

    public static final String BST_LOCAL_CONFLICT_YAML = YAML_DIR + "bst-local-conflict.yaxml";

}
