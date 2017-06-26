package com.comptel.bst.tools.mergetool;

import static org.junit.Assert.assertEquals;

import java.io.IOException;

import org.junit.Test;

import com.comptel.bst.tools.diff.DifferenceTest;
import com.comptel.bst.tools.diff.parser.entity.bst.method.Method;
import com.comptel.bst.tools.diff.parser.entity.bst.method.Node;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.mergetool.merger.BSTMerger;
import com.comptel.bst.tools.mergetool.merger.MergeConflictException;

public class MergeCompleteTest extends DifferenceTest {

    @Test
    public void add_contents_simple___agree() throws IOException, MergeConflictException {
        Node[] baseNodes = createNodes(1);
        Node[] localNodes = createNodes(1);
        Node[] remoteNodes = createNodes(1);
        Node[] expectedNodes = createNodes(1);

        Method base = createMethod(baseNodes, 0);
        Method local = createMethod(localNodes, 0);
        localNodes[0].addElement(param(0, 0));
        Method remote = createMethod(remoteNodes, 0);
        remoteNodes[0].addElement(param(1, 1));
        Method expected = createMethod(expectedNodes, 0);
        expectedNodes[0].addElement(param(0, 0));
        expectedNodes[0].addElement(param(1, 1));

        Element result = BSTMerger.merge(base, local, remote);
        assertEquals(expected, result);
    }

    @Test(expected = MergeConflictException.class)
    public void add_contents_simple___conflict() throws IOException, MergeConflictException {
        Node[] baseNodes = createNodes(1);
        Node[] localNodes = createNodes(1);
        Node[] remoteNodes = createNodes(1);

        Method base = createMethod(baseNodes, 0);
        Method local = createMethod(localNodes, 0);
        localNodes[0].addElement(param(0, 0));
        Method remote = createMethod(remoteNodes, 0);
        remoteNodes[0].addElement(param(0, 1));

        BSTMerger.merge(base, local, remote);
    }

    @Test
    public void add_contents_simple___overlap() throws IOException, MergeConflictException {
        Node[] baseNodes = createNodes(1);
        Node[] localNodes = createNodes(1);
        Node[] remoteNodes = createNodes(1);
        Node[] expectedNodes = createNodes(1);

        Method base = createMethod(baseNodes, 0);
        Method local = createMethod(localNodes, 0);
        localNodes[0].addElement(param(0, 0));
        Method remote = createMethod(remoteNodes, 0);
        remoteNodes[0].addElement(param(0, 0));
        Method expected = createMethod(expectedNodes, 0);
        expectedNodes[0].addElement(param(0, 0));

        Element result = BSTMerger.merge(base, local, remote);
        assertEquals(expected, result);
    }

    @Test
    public void add_parallel_complex___agree() throws IOException, MergeConflictException {
        Node[] baseNodes = createNodes(4);
        Node[] localNodes = createNodes(5);
        Node[] remoteNodes = createNodes(6);
        Node[] expectedNodes = createNodes(6);

        Method base = createMethod(baseNodes, 0, 1, 2);
        addParallelNodes(base, baseNodes, 0, 2, 3);

        Method local = createMethod(localNodes, 0, 1, 4, 2);
        addParallelNodes(local, localNodes, 0, 2, 3);

        Method remote = createMethod(remoteNodes, 0, 1, 2);
        addParallelNodes(remote, remoteNodes, 0, 2, 3, 5);

        Method expected = createMethod(expectedNodes, 0, 1, 4, 2);
        addParallelNodes(expected, expectedNodes, 0, 2, 3, 5);

        Element result = BSTMerger.merge(base, local, remote);
        assertEquals(expected, result);
    }

    @Test(expected = MergeConflictException.class)
    public void add_parallel_complex___conflict() throws IOException, MergeConflictException {
        Node[] baseNodes = createNodes(4);
        Node[] localNodes = createNodes(5);
        Node[] remoteNodes = createNodes(6);

        Method base = createMethod(baseNodes, 0, 1, 2);
        addParallelNodes(base, baseNodes, 0, 2, 3);

        Method local = createMethod(localNodes, 0, 1, 2);
        addParallelNodes(local, localNodes, 0, 2, 3, 4);

        Method remote = createMethod(remoteNodes, 0, 1, 2);
        addParallelNodes(remote, remoteNodes, 0, 2, 3, 5);

        BSTMerger.merge(base, local, remote);
    }

    @Test
    public void add_parallel_complex___overlap() throws IOException, MergeConflictException {
        Node[] baseNodes = createNodes(4);
        Node[] localNodes = createNodes(5);
        Node[] remoteNodes = createNodes(5);
        Node[] expectedNodes = createNodes(5);

        Method base = createMethod(baseNodes, 0, 1, 2);
        addParallelNodes(base, baseNodes, 0, 2, 3);

        Method local = createMethod(localNodes, 0, 1, 2);
        addParallelNodes(local, localNodes, 0, 2, 3, 4);

        Method remote = createMethod(remoteNodes, 0, 1, 2);
        addParallelNodes(remote, remoteNodes, 0, 2, 3, 4);

        Method expected = createMethod(expectedNodes, 0, 1, 2);
        addParallelNodes(expected, expectedNodes, 0, 2, 3, 4);

        Element result = BSTMerger.merge(base, local, remote);
        assertEquals(expected, result);
    }

    @Test
    public void add_parallel_simple___agree() throws IOException, MergeConflictException {
        Node[] baseNodes = createNodes(1);
        Node[] localNodes = createNodes(2);
        Node[] remoteNodes = createNodes(3);
        Node[] expectedNodes = createNodes(3);

        Method base = createMethod(baseNodes, 0);
        Method local = createMethod(localNodes, 0, 1);
        Method remote = createMethod(remoteNodes, 0);
        addParallelNodes(remote, remoteNodes, 0, -1, 2);
        Method expected = createMethod(expectedNodes, 0, 1);
        addParallelNodes(expected, expectedNodes, 0, -1, 2);

        Element result = BSTMerger.merge(base, local, remote);
        assertEquals(expected, result);
    }

    @Test(expected = MergeConflictException.class)
    public void add_parallel_simple___conflict() throws IOException, MergeConflictException {
        Node[] baseNodes = createNodes(1);
        Node[] localNodes = createNodes(4);
        Node[] remoteNodes = createNodes(3);

        Method base = createMethod(baseNodes, 0);
        Method local = createMethod(localNodes, 0, 1);
        addParallelNodes(local, localNodes, 0, -1, 3);
        Method remote = createMethod(remoteNodes, 0);
        addParallelNodes(remote, remoteNodes, 0, -1, 2);

        BSTMerger.merge(base, local, remote);
    }

    @Test
    public void add_parallel_simple___overlap() throws IOException, MergeConflictException {
        Node[] baseNodes = createNodes(1);
        Node[] localNodes = createNodes(3);
        Node[] remoteNodes = createNodes(3);
        Node[] expectedNodes = createNodes(3);

        Method base = createMethod(baseNodes, 0);
        Method local = createMethod(localNodes, 0, 1);
        addParallelNodes(local, localNodes, 0, -1, 2);
        Method remote = createMethod(remoteNodes, 0, 1);
        addParallelNodes(remote, remoteNodes, 0, -1, 2);
        Method expected = createMethod(expectedNodes, 0, 1);
        addParallelNodes(expected, expectedNodes, 0, -1, 2);

        Element result = BSTMerger.merge(base, local, remote);
        assertEquals(expected, result);
    }

    @Test
    public void add_sequence_complex___agree() throws IOException, MergeConflictException {
        Node[] baseNodes = createNodes(3);
        Node[] localNodes = createNodes(5);
        Node[] remoteNodes = createNodes(7);
        Node[] expectedNodes = createNodes(7);

        Method base = createMethod(baseNodes, 0, 1, 2);
        Method local = createMethod(localNodes, 0, 1, 2, 3, 4);
        Method remote = createMethod(remoteNodes, 6, 5, 0, 1, 2);
        Method expected = createMethod(expectedNodes, 6, 5, 0, 1, 2, 3, 4);

        Element result = BSTMerger.merge(base, local, remote);
        assertEquals(expected, result);
    }

    @Test(expected = MergeConflictException.class)
    public void add_sequence_complex___conflict() throws IOException, MergeConflictException {
        Node[] baseNodes = createNodes(3);
        Node[] localNodes = createNodes(5);
        Node[] remoteNodes = createNodes(6);

        Method base = createMethod(baseNodes, 0, 1, 2);
        Method local = createMethod(localNodes, 0, 1, 2, 3, 4);
        Method remote = createMethod(remoteNodes, 0, 1, 2, 5, 3);

        BSTMerger.merge(base, local, remote);
    }

    @Test
    public void add_sequence_complex___overlap() throws IOException, MergeConflictException {
        Node[] baseNodes = createNodes(3);
        Node[] localNodes = createNodes(5);
        Node[] remoteNodes = createNodes(5);
        Node[] expectedNodes = createNodes(5);

        Method base = createMethod(baseNodes, 0, 1, 2);
        Method local = createMethod(localNodes, 0, 1, 2, 3, 4);
        Method remote = createMethod(remoteNodes, 0, 1, 2, 3, 4);
        Method expected = createMethod(expectedNodes, 0, 1, 2, 3, 4);

        Element result = BSTMerger.merge(base, local, remote);
        assertEquals(expected, result);
    }

    @Test
    public void add_sequence_simple___agree() throws IOException, MergeConflictException {
        Node[] baseNodes = createNodes(1);
        Node[] localNodes = createNodes(2);
        Node[] remoteNodes = createNodes(3);
        Node[] expectedNodes = createNodes(3);

        Method base = createMethod(baseNodes, 0);
        Method local = createMethod(localNodes, 0, 1);
        Method remote = createMethod(remoteNodes, 2, 0);
        Method expected = createMethod(expectedNodes, 2, 0, 1);

        Element result = BSTMerger.merge(base, local, remote);
        assertEquals(expected, result);
    }

    @Test(expected = MergeConflictException.class)
    public void add_sequence_simple___conflict() throws IOException, MergeConflictException {
        Node[] baseNodes = createNodes(1);
        Node[] localNodes = createNodes(2);
        Node[] remoteNodes = createNodes(3);

        Method base = createMethod(baseNodes, 0);
        Method local = createMethod(localNodes, 0, 1);
        Method remote = createMethod(remoteNodes, 0, 2);

        BSTMerger.merge(base, local, remote);
    }

    @Test
    public void add_sequence_simple___overlap() throws IOException, MergeConflictException {
        Node[] baseNodes = createNodes(1);
        Node[] localNodes = createNodes(2);
        Node[] remoteNodes = createNodes(2);
        Node[] expectedNodes = createNodes(2);

        Method base = createMethod(baseNodes, 0);
        Method local = createMethod(localNodes, 0, 1);
        Method remote = createMethod(remoteNodes, 0, 1);
        Method expected = createMethod(expectedNodes, 0, 1);

        Element result = BSTMerger.merge(base, local, remote);
        assertEquals(expected, result);
    }

    @Test
    public void change_contents_simple___agree() throws IOException, MergeConflictException {
        Node[] baseNodes = createNodes(1);
        Node[] localNodes = createNodes(1);
        Node[] remoteNodes = createNodes(1);
        Node[] expectedNodes = createNodes(1);

        Method base = createMethod(baseNodes, 0);
        baseNodes[0].addElement(param(0, 0));
        baseNodes[0].addElement(param(1, 1));
        Method local = createMethod(localNodes, 0);
        localNodes[0].addElement(param(0, 2));
        localNodes[0].addElement(param(1, 1));
        Method remote = createMethod(remoteNodes, 0);
        remoteNodes[0].addElement(param(0, 0));
        remoteNodes[0].addElement(param(1, 2));
        Method expected = createMethod(expectedNodes, 0);
        expectedNodes[0].addElement(param(0, 2));
        expectedNodes[0].addElement(param(1, 2));

        Element result = BSTMerger.merge(base, local, remote);
        assertEquals(expected, result);
    }

    @Test (expected = MergeConflictException.class)
    public void change_contents_simple___conflict() throws IOException, MergeConflictException {
        Node[] baseNodes = createNodes(1);
        Node[] localNodes = createNodes(1);
        Node[] remoteNodes = createNodes(1);

        Method base = createMethod(baseNodes, 0);
        baseNodes[0].addElement(param(0, 0));
        baseNodes[0].addElement(param(1, 1));
        Method local = createMethod(localNodes, 0);
        localNodes[0].addElement(param(0, 2));
        localNodes[0].addElement(param(1, 2));
        Method remote = createMethod(remoteNodes, 0);
        remoteNodes[0].addElement(param(0, 3));
        remoteNodes[0].addElement(param(1, 3));

        BSTMerger.merge(base, local, remote);
    }

    @Test
    public void change_contents_simple___overlap() throws IOException, MergeConflictException {
        Node[] baseNodes = createNodes(1);
        Node[] localNodes = createNodes(1);
        Node[] remoteNodes = createNodes(1);
        Node[] expectedNodes = createNodes(1);

        Method base = createMethod(baseNodes, 0);
        baseNodes[0].addElement(param(0, 0));
        baseNodes[0].addElement(param(1, 1));
        Method local = createMethod(localNodes, 0);
        localNodes[0].addElement(param(0, 2));
        localNodes[0].addElement(param(1, 2));
        Method remote = createMethod(remoteNodes, 0);
        remoteNodes[0].addElement(param(0, 2));
        remoteNodes[0].addElement(param(1, 2));
        Method expected = createMethod(expectedNodes, 0);
        expectedNodes[0].addElement(param(0, 2));
        expectedNodes[0].addElement(param(1, 2));

        Element result = BSTMerger.merge(base, local, remote);
        assertEquals(expected, result);
    }

    @Test
    public void remove_contents_simple___agree() throws IOException, MergeConflictException {
        Node[] baseNodes = createNodes(1);
        Node[] localNodes = createNodes(1);
        Node[] remoteNodes = createNodes(1);
        Node[] expectedNodes = createNodes(1);

        Method base = createMethod(baseNodes, 0);
        baseNodes[0].addElement(param(0, 0));
        baseNodes[0].addElement(param(1, 1));
        Method local = createMethod(localNodes, 0);
        localNodes[0].addElement(param(0, 0));
        Method remote = createMethod(remoteNodes, 0);
        remoteNodes[0].addElement(param(1, 1));
        Method expected = createMethod(expectedNodes, 0);

        Element result = BSTMerger.merge(base, local, remote);
        assertEquals(expected, result);
    }

    @Test (expected = MergeConflictException.class)
    public void remove_contents_simple___conflict() throws IOException, MergeConflictException {
        Node[] baseNodes = createNodes(1);
        Node[] localNodes = createNodes(1);
        Node[] remoteNodes = createNodes(1);

        Method base = createMethod(baseNodes, 0);
        baseNodes[0].addElement(param(0, 0));
        baseNodes[0].addElement(param(1, 1));
        Method local = createMethod(localNodes, 0);
        localNodes[0].addElement(param(0, 2));
        Method remote = createMethod(remoteNodes, 0);
        remoteNodes[0].addElement(param(1, 2));

        BSTMerger.merge(base, local, remote);
    }

    @Test
    public void remove_contents_simple___overlap() throws IOException, MergeConflictException {
        Node[] baseNodes = createNodes(1);
        Node[] localNodes = createNodes(1);
        Node[] remoteNodes = createNodes(1);
        Node[] expectedNodes = createNodes(1);

        Method base = createMethod(baseNodes, 0);
        baseNodes[0].addElement(param(0, 0));
        baseNodes[0].addElement(param(1, 1));
        Method local = createMethod(localNodes, 0);
        Method remote = createMethod(remoteNodes, 0);
        Method expected = createMethod(expectedNodes, 0);

        Element result = BSTMerger.merge(base, local, remote);
        assertEquals(expected, result);
    }
}
