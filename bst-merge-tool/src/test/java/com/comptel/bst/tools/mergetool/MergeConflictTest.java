package com.comptel.bst.tools.mergetool;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;

import com.comptel.bst.tools.diff.DifferenceTest;
import com.comptel.bst.tools.diff.comparison.OutputTree;
import com.comptel.bst.tools.diff.comparison.differences.Difference;
import com.comptel.bst.tools.diff.parser.entity.bst.InputParameters;
import com.comptel.bst.tools.diff.parser.entity.bst.OutputParameters;
import com.comptel.bst.tools.diff.parser.entity.bst.Parameter;
import com.comptel.bst.tools.diff.parser.entity.bst.method.Method;
import com.comptel.bst.tools.diff.parser.entity.bst.method.MethodBody;
import com.comptel.bst.tools.diff.parser.entity.bst.method.Node;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.parser.entity.generic.References;
import com.comptel.bst.tools.diff.parser.entity.jaxb.JAXBParameter;
import com.comptel.bst.tools.mergetool.merger.BSTMerger;
import com.comptel.bst.tools.mergetool.merger.Conflict;

public class MergeConflictTest extends DifferenceTest {

    private static Method method = new Method();
    private static Method method_mod = new Method();

    private static MethodBody body = new MethodBody();
    private static MethodBody body_mod_second = new MethodBody();
    private static MethodBody body_mod_first = new MethodBody();

    private static InputParameters input = new InputParameters();
    private static InputParameters input_mod = new InputParameters();

    private static OutputParameters<JAXBParameter> output = new OutputParameters<JAXBParameter>();
    private static OutputParameters<JAXBParameter> output_mod = new OutputParameters<JAXBParameter>();

    private static Node node = node(0);
    private static Node node_mod_first = node(0);
    private static Node node_mod_second= node(0);

    private static Parameter param = param(0,0);
    private static Parameter param_mod_first = param(0,1);
    private static Parameter param_mod_second = param(0,2);

    static {
        param_mod_first.setAttr(Parameter.OVERWRITE_ATTR, "no");
        param_mod_second.setAttr(Parameter.OVERWRITE_ATTR, "yes");

        node.addElement(param);

        node_mod_first.setAttr(Node.NAME_ATTR, "Modified");
        node_mod_first.addElement(param_mod_first);

        node_mod_second.setAttr(Node.NAME_ATTR, "Changed");
        node_mod_second.addElement(param_mod_second);

        output.addElement(param);

        output_mod.setAttr(OutputParameters.ALL_ATTR, "yes");
        output_mod.addElement(param_mod_first);

        body.addElement(node);
        body_mod_first.addElement(node_mod_first);
        body_mod_second.addElement(node_mod_second);

        input.addElement(param);
        input_mod.addElement(param_mod_first);
    }

    public static Collection<Difference> diffs(Difference... diffs) {
        return Arrays.asList(diffs);
    }

    private static void doTestDiffConflict(
            Collection<Difference> firstDiffs,
            Collection<Difference> secondDiffs,
            Conflict... expectedConflicts) {
        Collection<Conflict> conflicts =
                BSTMerger.checkConflicts(
                        firstDiffs,
                        secondDiffs);
        assertEquals(expectedConflicts.length, conflicts.size());
        for (Conflict conflict : expectedConflicts) {
            assertTrue(conflicts.contains(conflict));
        }
    }

    private static void testDiffConflict(
            Collection<Difference> firstDiffs,
            Collection<Difference> secondDiffs,
            Conflict... expectedConflicts) {
        doTestDiffConflict(firstDiffs, secondDiffs, expectedConflicts);
        doTestDiffConflict(secondDiffs, firstDiffs, expectedConflicts);
    }

    @Test
    public void added_method_body___with___added_method_body___differs() {
        Difference added1 = Difference.added(method, body_mod_first);
        Difference added2 = Difference.added(method, body_mod_second);
        testDiffConflict(diffs(added1), diffs(added2), conf(added1, added2));
    }

    @Test
    public void added_method_body___with___added_method_body___equals() {
        Difference added1 = Difference.added(method, body_mod_first);
        Difference added2 = Difference.added(method, body_mod_first);
        testDiffConflict(diffs(added1), diffs(added2));
    }

    @Test
    public void added_method_input___with___added_method_input___differs() {
        Difference added1 = Difference.added(method, input);
        Difference added2 = Difference.added(method, input_mod);
        testDiffConflict(diffs(added1), diffs(added2), conf(added1, added2));
    }

    @Test
    public void added_method_input___with___added_method_input___equals() {
        Difference added1 = Difference.added(method, input);
        Difference added2 = Difference.added(method, input);
        testDiffConflict(diffs(added1), diffs(added2));
    }

    @Test
    public void added_method_output___with___added_method_output___differs() {
        Difference added1 = Difference.added(method, output);
        Difference added2 = Difference.added(method, output_mod);
        testDiffConflict(diffs(added1), diffs(added2), conf(added1, added2));
    }

    @Test
    public void added_method_output___with___added_method_output___equals() {
        Difference added1 = Difference.added(method, output);
        Difference added2 = Difference.added(method, output);
        testDiffConflict(diffs(added1), diffs(added2));
    }

    @Test
    public void added_node___with___added_node___differs() {
        Difference added1 = Difference.added(body_mod_first, node);
        Difference added2 = Difference.added(body_mod_first, node_mod_first);
        testDiffConflict(diffs(added1), diffs(added2), conf(added1, added2));
    }

    @Test
    public void added_node___with___added_node___equals() {
        Difference added1 = Difference.added(body_mod_first, node);
        Difference added2 = Difference.added(body_mod_first, node);
        testDiffConflict(diffs(added1), diffs(added2));
    }

    @Test
    public void added_node___with___changed_node_attr() {
        Difference added = Difference.added(body_mod_first, node);
        Difference changed = Difference.attrChanged(node, node, Node.NAME_ATTR);
        testDiffConflict(diffs(added), diffs(changed), new Conflict(added, changed));
    }

    @Test
    public void added_node_link___with___removed_node() {
        Node refNode = node(1);
        Element ref = refNode.setPrimaryLink(node);
        Difference added = Difference.added(node.findUniqueElement(References.TAG), ref);
        Difference removed = Difference.removed(body_mod_first, node);

        Conflict conflict = new Conflict(added, removed);
        testDiffConflict(diffs(added), diffs(removed), conflict);
        System.out.println(new OutputTree(conflict).getMessage(""));
    }

    @Test
    public void added_node___with___removed_node() {
        Difference added = Difference.added(body_mod_first, node);
        Difference removed = Difference.removed(body_mod_first, node);
        testDiffConflict(diffs(added), diffs(removed), new Conflict(added, removed));
    }

    @Test
    public void added_node_param___with___changed_node_param_value() {
        Difference added = Difference.added(node, param);
        Difference changed = Difference.valueChanged(param, param);
        testDiffConflict(diffs(added), diffs(changed), new Conflict(added, changed));
    }

    @Test
    public void changed_node_attr___with___changed_node_attr___differs() {
        Difference changed1 = Difference.attrChanged(node, node_mod_first, Node.NAME_ATTR);
        Difference changed2 = Difference.attrChanged(node, node_mod_second, Node.NAME_ATTR);
        testDiffConflict(diffs(changed1), diffs(changed2), conf(changed1, changed2));
    }

    @Test
    public void changed_node_attr___with___changed_node_attr___equals() {
        Difference changed1 = Difference.attrChanged(node, node_mod_first, Node.NAME_ATTR);
        Difference changed2 = Difference.attrChanged(node, node_mod_first, Node.NAME_ATTR);
        testDiffConflict(diffs(changed1), diffs(changed2));
    }

    @Test
    public void changed_node_param___with___changed_node_param___separate() {
        Node firstNode = node(0);
        Node secondNode = node(1);

        Parameter firstParam = param(0, 0);
        Parameter secondParam = param(0, 1);

        firstNode.addElement(firstParam);
        secondNode.addElement(secondParam);

        Difference changed1 = Difference.valueChanged(firstParam, firstParam);
        Difference changed2 = Difference.valueChanged(secondParam, secondParam);
        testDiffConflict(diffs(changed1), diffs(changed2));
    }

    @Test
    public void changed_node_param___with___changed_node_param___equals() {
        Node node = node(0);

        Parameter firstParam = param(0, 0);
        Parameter secondParam = param(0, 0);

        node.addElement(firstParam);
        node.addElement(secondParam);

        Difference changed1 = Difference.valueChanged(firstParam, firstParam);
        Difference changed2 = Difference.valueChanged(secondParam, secondParam);
        testDiffConflict(diffs(changed1), diffs(changed2));
    }

    @Test
    public void changed_node_param___with___changed_node_param___differs() {
        Node node = node(0);

        Parameter firstParam = param(0, 0);
        Parameter secondParam = param(0, 1);

        node.addElement(firstParam);
        node.addElement(secondParam);

        Difference changed1 = Difference.valueChanged(firstParam, firstParam);
        Difference changed2 = Difference.valueChanged(secondParam, secondParam);
        testDiffConflict(diffs(changed1), diffs(changed2), new Conflict(changed1, changed2));
    }

    @Test
    public void changed_node_attr___with___changed_param_attr() {
        Difference changed1 = Difference.attrChanged(node, node_mod_first, Node.NAME_ATTR);
        Difference changed2 = Difference.attrChanged(param, param_mod_first, Parameter.OVERWRITE_ATTR);
        testDiffConflict(diffs(changed1), diffs(changed2));
    }

    @Test
    public void changed_node_param_value___with___changed_node_param_value() {
        Difference added = Difference.added(node, param);
        Difference changed = Difference.valueChanged(param, param);
        testDiffConflict(diffs(added), diffs(changed), new Conflict(added, changed));
    }

    @Test
    public void changed_param_attr___with___changed_param_value() {
        Difference changed1 = Difference.attrChanged(param, param_mod_first, Parameter.OVERWRITE_ATTR);
        Difference changed2 = Difference.valueChanged(param, param_mod_first);
        testDiffConflict(diffs(changed1), diffs(changed2));
    }

    @Test
    public void removed_node___with___removed_node() {
        Difference removed1 = Difference.removed(body_mod_first, node);
        Difference removed2 = Difference.removed(body_mod_first, node);
        testDiffConflict(diffs(removed1), diffs(removed2));
    }

    @Test
    public void changed_node_param___with___removed_node() {
        Node node = node(0);

        Parameter firstParam = param(0, 0);
        Parameter secondParam = param(0, 1);

        node.addElement(firstParam);
        node.addElement(secondParam);

        MethodBody body = new MethodBody();
        body.addElement(node);

        Difference changed = Difference.valueChanged(firstParam, firstParam);
        Difference removed = Difference.removed(body, node);
        testDiffConflict(diffs(removed), diffs(changed), new Conflict(removed, changed));
    }

    @Test
    public void added_node_param___with___removed_node() {
        Node node = node(0);

        Parameter param = param(0, 0);

        MethodBody body = new MethodBody();
        body.addElement(node);

        Difference added = Difference.added(node, param);
        Difference removed = Difference.removed(body, node);
        testDiffConflict(diffs(removed), diffs(added), new Conflict(removed, added));
    }

    @Test
    public void removed_node_param___with___changed_node_param_value() {
        Difference added = Difference.added(body_mod_first, param);
        Difference changed = Difference.valueChanged(param, param);
        testDiffConflict(diffs(added), diffs(changed), new Conflict(added, changed));
    }

    private Conflict conf(Difference first, Difference second) {
        return new Conflict(first, second);
    }


}
