package com.comptel.bst.tools.mergetool;

import java.io.File;
import java.io.IOException;

import org.junit.Test;

import com.comptel.bst.tools.diff.parser.BSTReader;
import com.comptel.bst.tools.diff.parser.entity.generic.Yaml;
import com.comptel.bst.tools.diff.utils.DiffUtils;


public class BSTParserTest {

    @Test
    public void test_simple_yaml() throws IOException {
        BSTReader.readYaml(new File(BSTMergeToolTest.BST_EXAMPLE_YAML));
    }

    @Test
    public void test_failing_yaml() throws IOException {
        Yaml result = BSTReader.readYaml(new File(BSTMergeToolTest.IRL_RESULT_FAILING));
        Yaml expected = BSTReader.readYaml(new File(BSTMergeToolTest.IRL_RESULT));

        DiffUtils.getDifferences(expected, result).forEach(System.out::println);
    }

}
