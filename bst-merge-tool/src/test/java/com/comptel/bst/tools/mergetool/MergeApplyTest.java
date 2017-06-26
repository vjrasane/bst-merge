package com.comptel.bst.tools.mergetool;

import static org.junit.Assert.assertEquals;

import java.io.IOException;
import java.util.Collection;

import org.junit.Test;

import com.comptel.bst.tools.diff.DifferenceTest;
import com.comptel.bst.tools.diff.comparison.differences.Difference;
import com.comptel.bst.tools.diff.parser.entity.bst.method.Method;
import com.comptel.bst.tools.diff.parser.entity.bst.method.Node;
import com.comptel.bst.tools.diff.utils.DiffUtils;
import com.comptel.bst.tools.mergetool.merger.BSTMerger;

public class MergeApplyTest extends DifferenceTest {

    @Test
    public void test_add_branching_node() throws IOException {
        Node[] nodes1 = createNodes(3);
        Node[] nodes2 = createNodes(4);

        Method s1 = createMethod(nodes1, 0, 1, 2);
        Method s2 = createMethod(nodes2, 0, 1, 2);
        addParallelNodes(s2, nodes2, 0, -1, 3);

        Collection<Difference> diffs = DiffUtils.getDifferences(s1,s2);
        BSTMerger.applyDiffs(diffs);
        assertEquals(s1, s2);
    }

    @Test
    public void test_add_node_after() throws IOException {
        Node[] nodes1 = createNodes(2);
        Node[] nodes2 = createNodes(3);

        Method s1 = createMethod(nodes1, 0, 1);
        Method s2 = createMethod(nodes2, 0, 1, 2);

        Collection<Difference> diffs = DiffUtils.getDifferences(s1,s2);
        BSTMerger.applyDiffs(diffs);
        assertEquals(s1, s2);
    }

    @Test
    public void test_add_node_before() throws IOException {
        Node[] nodes1 = createNodes(2);
        Node[] nodes2 = createNodes(3);

        Method s1 = createMethod(nodes1, 0, 1);
        Method s2 = createMethod(nodes2, 2, 0, 1);

        Collection<Difference> diffs = DiffUtils.getDifferences(s1,s2);
        BSTMerger.applyDiffs(diffs);
        assertEquals(s1, s2);
    }

    @Test
    public void test_add_node_between() throws IOException {
        Node[] nodes1 = createNodes(2);
        Node[] nodes2 = createNodes(3);

        Method s1 = createMethod(nodes1, 0, 1);
        Method s2 = createMethod(nodes2, 0, 2, 1);

        Collection<Difference> diffs = DiffUtils.getDifferences(s1,s2);
        BSTMerger.applyDiffs(diffs);
        assertEquals(s1, s2);
    }

    @Test
    public void test_add_node_parallel() throws IOException {
        Node[] nodes1 = createNodes(3);
        Node[] nodes2 = createNodes(4);

        Method s1 = createMethod(nodes1, 0, 1, 2);
        Method s2 = createMethod(nodes2, 0, 1, 2);
        addParallelNodes(s2, nodes2, 0, 2, 3);

        Collection<Difference> diffs = DiffUtils.getDifferences(s1,s2);
        BSTMerger.applyDiffs(diffs);
        assertEquals(s1, s2);
    }

    @Test
    public void test_add_output_parameter() throws IOException {
        Node[] nodes1 = createNodes(1);
        Node[] nodes2 = createNodes(1);

        Method s1 = createMethod(nodes1, 0);
        Method s2 = createMethod(nodes2, 0);
        nodes2[0].addElement(outParam(0, 0));

        Collection<Difference> diffs = DiffUtils.getDifferences(s1,s2);
        BSTMerger.applyDiffs(diffs);
        assertEquals(s1, s2);
    }

    @Test
    public void test_add_parameter() throws IOException {
        Node[] nodes1 = createNodes(1);
        Node[] nodes2 = createNodes(1);

        Method s1 = createMethod(nodes1, 0);
        Method s2 = createMethod(nodes2, 0);
        nodes2[0].addElement(param(0, 0));

        Collection<Difference> diffs = DiffUtils.getDifferences(s1,s2);
        BSTMerger.applyDiffs(diffs);
        assertEquals(s1, s2);
    }

    @Test
    public void test_add_single_node() throws IOException {
        Node[] nodes = createNodes(1);
        Method s1 = new Method();
        Method s2 = createMethod(nodes, 0);

        Collection<Difference> diffs = DiffUtils.getDifferences(s1,s2);
        BSTMerger.applyDiffs(diffs);
        assertEquals(s1, s2);
    }

    @Test
    public void test_change_output_parameter() throws IOException {
        Node[] nodes1 = createNodes(1);
        Node[] nodes2 = createNodes(1);

        Method s1 = createMethod(nodes1, 0);
        nodes1[0].addElement(outParam(0, 0));
        Method s2 = createMethod(nodes2, 0);
        nodes2[0].addElement(outParam(0, 1));

        Collection<Difference> diffs = DiffUtils.getDifferences(s1,s2);
        BSTMerger.applyDiffs(diffs);
        assertEquals(s1, s2);
    }

    @Test
    public void test_change_parameter() throws IOException {
        Node[] nodes1 = createNodes(1);
        Node[] nodes2 = createNodes(1);

        Method s1 = createMethod(nodes1, 0);
        nodes1[0].addElement(outParam(0, 0));
        Method s2 = createMethod(nodes2, 0);
        nodes2[0].addElement(outParam(0, 1));

        Collection<Difference> diffs = DiffUtils.getDifferences(s1,s2);
        BSTMerger.applyDiffs(diffs);
        assertEquals(s1, s2);
    }

    @Test
    public void test_remove_branching_node() throws IOException {
        Node[] nodes1 = createNodes(4);
        Node[] nodes2 = createNodes(3);

        Method s1 = createMethod(nodes1, 0, 1, 2);
        addParallelNodes(s1, nodes1, 0, -1, 3);
        Method s2 = createMethod(nodes2, 0, 1, 2);

        Collection<Difference> diffs = DiffUtils.getDifferences(s1,s2);
        BSTMerger.applyDiffs(diffs);
        assertEquals(s1, s2);
    }

    @Test
    public void test_remove_node_after() throws IOException {
        Node[] nodes1 = createNodes(3);
        Node[] nodes2 = createNodes(2);

        Method s1 = createMethod(nodes1, 0, 1, 2);
        Method s2 = createMethod(nodes2, 0, 1);

        Collection<Difference> diffs = DiffUtils.getDifferences(s1,s2);
        BSTMerger.applyDiffs(diffs);
        assertEquals(s1, s2);
    }

    @Test
    public void test_remove_node_before() throws IOException {
        Node[] nodes1 = createNodes(3);
        Node[] nodes2 = createNodes(2);

        Method s1 = createMethod(nodes1, 2, 0, 1);
        Method s2 = createMethod(nodes2, 0, 1);

        Collection<Difference> diffs = DiffUtils.getDifferences(s1,s2);
        BSTMerger.applyDiffs(diffs);
        assertEquals(s1, s2);
    }

    @Test
    public void test_remove_node_between() throws IOException {
        Node[] nodes1 = createNodes(3);
        Node[] nodes2 = createNodes(2);

        Method s1 = createMethod(nodes1, 0, 2, 1);
        Method s2 = createMethod(nodes2, 0, 1);

        Collection<Difference> diffs = DiffUtils.getDifferences(s1,s2);
        BSTMerger.applyDiffs(diffs);
        assertEquals(s1, s2);
    }

    @Test
    public void test_remove_node_parallel() throws IOException {
        Node[] nodes1 = createNodes(4);
        Node[] nodes2 = createNodes(3);

        Method s1 = createMethod(nodes1, 0, 1, 2);
        addParallelNodes(s1, nodes1, 0, 2, 3);
        Method s2 = createMethod(nodes2, 0, 1, 2);

        Collection<Difference> diffs = DiffUtils.getDifferences(s1,s2);
        BSTMerger.applyDiffs(diffs);
        assertEquals(s1, s2);
    }

    @Test
    public void test_remove_output_parameter() throws IOException {
        Node[] nodes1 = createNodes(1);
        Node[] nodes2 = createNodes(1);

        Method s1 = createMethod(nodes1, 0);
        nodes1[0].addElement(outParam(0, 0));
        Method s2 = createMethod(nodes2, 0);

        Collection<Difference> diffs = DiffUtils.getDifferences(s1,s2);
        BSTMerger.applyDiffs(diffs);
        assertEquals(s1, s2);
    }

    @Test
    public void test_remove_parameter() throws IOException {
        Node[] nodes1 = createNodes(1);
        Node[] nodes2 = createNodes(1);

        Method s1 = createMethod(nodes1, 0);
        nodes1[0].addElement(param(0, 0));
        Method s2 = createMethod(nodes2, 0);

        Collection<Difference> diffs = DiffUtils.getDifferences(s1,s2);
        BSTMerger.applyDiffs(diffs);
        assertEquals(s1, s2);
    }

    @Test
    public void test_remove_single_node() throws IOException {
        Node[] nodes = createNodes(1);
        Method s1 = createMethod(nodes, 0);
        Method s2 = new Method();

        Collection<Difference> diffs = DiffUtils.getDifferences(s1,s2);
        BSTMerger.applyDiffs(diffs);
        assertEquals(s1, s2);
    }

}
