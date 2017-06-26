package com.comptel.bst.tools.diff;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.io.IOException;
import java.util.Collection;

import org.junit.Test;

import com.comptel.bst.tools.diff.comparison.differences.Difference;
import com.comptel.bst.tools.diff.parser.entity.bst.InputParameters;
import com.comptel.bst.tools.diff.parser.entity.bst.OutputParameters;
import com.comptel.bst.tools.diff.parser.entity.bst.Parameter;
import com.comptel.bst.tools.diff.parser.entity.bst.method.Method;
import com.comptel.bst.tools.diff.parser.entity.bst.method.MethodBody;
import com.comptel.bst.tools.diff.parser.entity.bst.method.Node;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.parser.entity.jaxb.JAXBParameter;


public class DiffComparisonTest extends DifferenceTest {

    @Test
    public void method_params___add() throws IOException {
        Method m1 = new Method();
        Method m2 = new Method();
        m2.addInputParam(param(0,0));
        m2.addOutputParam(param(0,0));

        Collection<Difference> diffs = m1.compare(null, m2);
        assertEquals(2, diffs.size());
        assertTrue(added(m1, m2.getOutput(), diffs));
        assertTrue(added(m1, m2.getInput(), diffs));
    }

    @Test
    public void method_params___add___pre_exist() throws IOException {
        Method m1 = new Method();
        m1.addElement(new InputParameters());
        m1.addElement(new OutputParameters<JAXBParameter>());
        Method m2 = new Method();
        m2.addInputParam(param(0,0));
        m2.addOutputParam(param(0,0));

        Collection<Difference> diffs = m1.compare(null, m2);
        assertEquals(2, diffs.size());
        assertTrue(added(m1.getInput(), param(0,0), diffs));
        assertTrue(added(m1.getOutput(), param(0,0), diffs));
    }

    @Test
    public void method_params___change() throws IOException {
        Method m1 = new Method();
        m1.addInputParam(param(0,0));
        m1.addOutputParam(param(0,0));
        Method m2 = new Method();
        m2.addInputParam(param(0,1));
        m2.addOutputParam(param(0,1));

        Collection<Difference> diffs = m1.compare(null, m2);
        assertEquals(2, diffs.size());
        assertTrue(valueChanged(param(0,0), param(0,1), diffs));
        assertTrue(valueChanged(param(0,0), param(0,1), diffs));
    }

    @Test
    public void method_params___remove() throws IOException {
        Method m1 = new Method();
        m1.addInputParam(param(0,0));
        m1.addOutputParam(param(0,0));
        Method m2 = new Method();

        Collection<Difference> diffs = m1.compare(null, m2);
        assertEquals(2, diffs.size());
        assertTrue(removed(m1, m1.getInput(), diffs));
        assertTrue(removed(m1, m1.getOutput(), diffs));
    }

    @Test
    public void method_params___remove___post_exist() throws IOException {
        Method m1 = new Method();
        m1.addInputParam(param(0,0));
        m1.addOutputParam(param(0,0));
        Method m2 = new Method();
        m2.addElement(new OutputParameters<>());
        m2.addElement(new InputParameters());

        Collection<Difference> diffs = m1.compare(null, m2);
        assertEquals(2, diffs.size());
        assertTrue(removed(m1.getInput(), param(0,0), diffs));
        assertTrue(removed(m1.getOutput(), param(0,0), diffs));
    }

    @Test
    public void node_after___add() throws IOException {
        Node[] nodes1 = createNodes(2);
        Node[] nodes2 = createNodes(3);

        Method m1 = createMethod(nodes1, 0, 1);
        Method m2 = createMethod(nodes2, 0, 1, 2);

        Collection<Difference> diffs = m1.compare(null, m2);
        assertEquals(2, diffs.size());
        assertTrue(added(m1.getBody(), nodes2[2], diffs));
        assertTrue(added(nodes1[1].getLinks(), nodes2[1].getPrimaryLink(), diffs));
    }

    @Test
    public void node_after___remove() throws IOException {
        Node[] nodes1 = createNodes(3);
        Node[] nodes2 = createNodes(2);

        Method m1 = createMethod(nodes1, 0, 1, 2);
        Method m2 = createMethod(nodes2, 0, 1);

        Collection<Difference> diffs = m1.compare(null, m2);
        assertEquals(2, diffs.size());
        assertTrue(removed(m1.getBody(), nodes1[2], diffs));
        assertTrue(removed(nodes1[1].getLinks(), nodes1[1].getPrimaryLink(), diffs));
    }

    @Test
    public void node_attr___both_null() throws IOException {
        Node node1 = node(0);
        Node node2 = node(0);

        node1.addAttr("attr", null);
        node2.addAttr("attr", null);

        Collection<Difference> diffs = node1.compare(null, node2);
        assertEquals(0, diffs.size());
    }

    @Test
    public void node_attr___changed___from_null() throws IOException {
        Node[] nodes1 = createNodes(1);
        Node[] nodes2 = createNodes(1);

        Method m1 = createMethod(nodes1, 0);
        Method m2 = createMethod(nodes2, 0);

        nodes1[0].addAttr("attr", null);
        nodes2[0].addAttr("attr", "new");

        Collection<Difference> diffs = m1.compare(null, m2);
        assertEquals(1, diffs.size());
        assertTrue(attrChanged(nodes1[0], nodes2[0], "attr", diffs));
    }

    @Test
    public void node_before___add() throws IOException {
        Node[] nodes1 = createNodes(2);
        Node[] nodes2 = createNodes(3);

        Method m1 = createMethod(nodes1, 0, 1);
        Method m2 = createMethod(nodes2, 2, 0, 1);

        Collection<Difference> diffs = m1.compare(null, m2);
        assertEquals(2, diffs.size());
        assertTrue(added(m1.getBody(), nodes2[2], diffs));
        assertTrue(added(nodes1[0].getReferences(), ref(2), diffs));
    }

    @Test
    public void node_before___remove() throws IOException {
        Node[] nodes1 = createNodes(3);
        Node[] nodes2 = createNodes(2);

        Method m1 = createMethod(nodes1, 2, 0, 1);
        Method m2 = createMethod(nodes2, 0, 1);

        Collection<Difference> diffs = m1.compare(null, m2);
        assertEquals(2, diffs.size());
        assertTrue(removed(m1.getBody(), nodes1[2], diffs));
    }

    @Test
    public void node_between___add() throws IOException {
        Node[] nodes1 = createNodes(2);
        Node[] nodes2 = createNodes(3);

        Method m1 = createMethod(nodes1, 0, 1);
        Method m2 = createMethod(nodes2, 0, 2, 1);

        Collection<Difference> diffs = m1.compare(null, m2);
        assertEquals(4, diffs.size());
        assertTrue(added(m1.getBody(), nodes2[2], diffs));
        assertTrue(added(nodes1[1].getReferences(), ref(2), diffs));
        assertTrue(removed(nodes1[1].getReferences(), ref(0), diffs));
        assertTrue(valueChanged(nodes1[0].getPrimaryLink(), nodes2[0].getPrimaryLink(), diffs));
    }

    @Test
    public void node_between___remove() throws IOException {
        Node[] nodes1 = createNodes(3);
        Node[] nodes2 = createNodes(2);

        Method m1 = createMethod(nodes1, 0, 2, 1);
        Method m2 = createMethod(nodes2, 0, 1);

        Collection<Difference> diffs = m1.compare(null, m2);
        assertEquals(4, diffs.size());
        assertTrue(removed(m1.getBody(), nodes1[2], diffs));
        assertTrue(removed(nodes1[1].getReferences(), ref(2), diffs));

        assertTrue(added(nodes1[1].getReferences(), ref(0), diffs));
        assertTrue(valueChanged(nodes1[0].getPrimaryLink(), nodes2[0].getPrimaryLink(), diffs));
    }

    @Test
    public void node_branching___add() throws IOException {
        Node[] nodes1 = createNodes(3);
        Node[] nodes2 = createNodes(4);

        Method m1 = createMethod(nodes1, 0, 1, 2);
        Method m2 = createMethod(nodes2, 0, 1, 2);
        addParallelNodes(m2, nodes2, 0, -1, 3);

        Collection<Difference> diffs = m1.compare(null, m2);
        assertEquals(2, diffs.size());
        assertTrue(added(m1.getBody(), nodes2[3], diffs));
        assertTrue(added(nodes1[0].getLinks(), nodes2[0].getSecondaryLink(), diffs));
    }


    @Test
    public void node_branching___remove() throws IOException {
        Node[] nodes1 = createNodes(4);
        Node[] nodes2 = createNodes(3);

        Method m1 = createMethod(nodes1, 0, 1, 2);
        addParallelNodes(m1, nodes1, 0, -1, 3);
        Method m2 = createMethod(nodes2, 0, 1, 2);

        Collection<Difference> diffs = m1.compare(null, m2);
        assertEquals(2, diffs.size());
        assertTrue(removed(m1.getBody(), nodes1[3], diffs));
        assertTrue(removed(nodes1[0].getLinks(), nodes1[0].getSecondaryLink(), diffs));
    }

    @Test
    public void node_name___change() throws IOException {
        Node[] nodes1 = createNodes(1);
        Node[] nodes2 = createNodes(1);
        Method m1 = createMethod(nodes1, 0);
        Method m2 = createMethod(nodes2, 0);
        nodes2[0].setAttr(Node.NAME_ATTR, "changed");

        Collection<Difference> diffs = m1.compare(null, m2);
        assertEquals(1, diffs.size());
        assertTrue(attrChanged(nodes1[0], nodes2[0], Node.NAME_ATTR, diffs));
    }

    @Test
    public void node_out_param___add() throws IOException {
        Node[] nodes1 = createNodes(1);
        Node[] nodes2 = createNodes(1);
        Method m1 = createMethod(nodes1, 0);
        Method m2 = createMethod(nodes2, 0);
        nodes2[0].addElement(outParam(0, 0));

        Collection<Difference> diffs = m1.compare(null, m2);
        assertEquals(1, diffs.size());
        assertTrue(added(nodes1[0], outParam(0, 0), diffs));
    }

    @Test
    public void node_out_param___change() throws IOException {
        Node[] nodes1 = createNodes(1);
        Node[] nodes2 = createNodes(1);
        Method m1 = createMethod(nodes1, 0);
        nodes1[0].addElement(outParam(0, 0));
        Method m2 = createMethod(nodes2, 0);
        nodes2[0].addElement(outParam(0, 1));

        Collection<Difference> diffs = m1.compare(null, m2);
        assertEquals(1, diffs.size());
        assertTrue(valueChanged(outParam(0, 0), outParam(0, 1), diffs));
    }

    @Test
    public void node_out_param___remove() throws IOException {
        Node[] nodes1 = createNodes(1);
        Node[] nodes2 = createNodes(1);
        Method m1 = createMethod(nodes1, 0);
        nodes1[0].addElement(param(0, 0));
        Method m2 = createMethod(nodes2, 0);

        Collection<Difference> diffs = m1.compare(null, m2);
        assertEquals(1, diffs.size());
        assertTrue(removed(nodes1[0], param(0, 0), diffs));
    }

    @Test
    public void node_out_param_attr___add() throws IOException {
        Node[] nodes1 = createNodes(1);
        Node[] nodes2 = createNodes(1);
        Method m1 = createMethod(nodes1, 0);
        Element param1 = outParam(0, 0);
        nodes1[0].addElement(param1);

        Method m2 = createMethod(nodes2, 0);
        Element param2 = outParam(0, 0);
        param2.addAttr(Parameter.OVERWRITE_ATTR, "yes");
        nodes2[0].addElement(param2);

        Collection<Difference> diffs = m1.compare(null, m2);
        assertEquals(1, diffs.size());
        assertTrue(attrChanged(param1, param2, Parameter.OVERWRITE_ATTR, diffs));
    }

    @Test
    public void node_out_param_attr___change() throws IOException {
        Node[] nodes1 = createNodes(1);
        Node[] nodes2 = createNodes(1);
        Method m1 = createMethod(nodes1, 0);
        Method m2 = createMethod(nodes2, 0);

        Element param1 = outParam(0, 0);
        param1.addAttr(Parameter.OVERWRITE_ATTR, "yes");
        nodes1[0].addElement(param1);

        Element param2 = outParam(0, 0);
        param2.addAttr(Parameter.OVERWRITE_ATTR, "no");
        nodes2[0].addElement(param2);

        Collection<Difference> diffs = m1.compare(null, m2);
        assertEquals(1, diffs.size());
        assertTrue(attrChanged(param1, param2, Parameter.OVERWRITE_ATTR, diffs));
    }

    @Test
    public void node_out_param_attr___remove() throws IOException {
        Node[] nodes1 = createNodes(1);
        Node[] nodes2 = createNodes(1);
        Method m1 = createMethod(nodes1, 0);
        Element param1 = param(0, 0);
        param1.addAttr(Parameter.OVERWRITE_ATTR, "yes");
        nodes1[0].addElement(param1);

        Method m2 = createMethod(nodes2, 0);
        nodes2[0].addElement(param(0, 0));

        Collection<Difference> diffs = m1.compare(null, m2);
        assertEquals(1, diffs.size());
        assertTrue(attrChanged(param1, param(0, 0), Parameter.OVERWRITE_ATTR, diffs));
    }

    @Test
    public void node_parallel___add() throws IOException {
        Node[] nodes1 = createNodes(3);
        Node[] nodes2 = createNodes(4);

        Method m1 = createMethod(nodes1, 0, 1, 2);
        Method m2 = createMethod(nodes2, 0, 1, 2);
        addParallelNodes(m2, nodes2, 0, 2, 3);

        Collection<Difference> diffs = m1.compare(null, m2);
        assertEquals(3, diffs.size());
        assertTrue(added(m1.getBody(), nodes2[3], diffs));
        assertTrue(added(nodes1[0].getLinks(), nodes2[0].getSecondaryLink(), diffs));
        assertTrue(added(nodes1[2].getReferences(), ref(3), diffs));
    }

    @Test
    public void node_parallel___remove() throws IOException {
        Node[] nodes1 = createNodes(4);
        Node[] nodes2 = createNodes(3);

        Method m1 = createMethod(nodes1, 0, 1, 2);
        addParallelNodes(m1, nodes1, 0, 2, 3);
        Method m2 = createMethod(nodes2, 0, 1, 2);

        Collection<Difference> diffs = m1.compare(null, m2);
        assertEquals(3, diffs.size());
        assertTrue(removed(m1.getBody(), nodes1[3], diffs));
        assertTrue(removed(nodes1[0].getLinks(), nodes1[0].getSecondaryLink(), diffs));
        assertTrue(removed(nodes1[2].getReferences(), ref(3), diffs));
    }



    @Test
    public void node_param___add() throws IOException {
        Node[] nodes1 = createNodes(1);
        Node[] nodes2 = createNodes(1);
        Method m1 = createMethod(nodes1, 0);
        Method m2 = createMethod(nodes2, 0);
        nodes2[0].addElement(param(0, 0));

        Collection<Difference> diffs = m1.compare(null, m2);
        assertEquals(1, diffs.size());
        assertTrue(added(nodes1[0], param(0, 0), diffs));
    }

    @Test
    public void node_param___move() throws IOException {
        Node[] nodes1 = createNodes(2);
        Node[] nodes2 = createNodes(2);
        Method m1 = createMethod(nodes1, 0, 1);
        nodes1[0].addElement(param(0, 0));
        Method m2 = createMethod(nodes2, 0, 1);
        nodes2[1].addElement(param(0, 0));

        Collection<Difference> diffs = m1.compare(null, m2);
        assertEquals(2, diffs.size());
        assertTrue(removed(nodes1[0], param(0, 0), diffs));
        assertTrue(added(nodes1[1], param( 0, 0), diffs));
    }

    @Test
    public void node_param___remove() throws IOException {
        Node[] nodes1 = createNodes(1);
        Node[] nodes2 = createNodes(1);
        Method m1 = createMethod(nodes1, 0);
        nodes1[0].addElement(param(0, 0));
        Method m2 = createMethod(nodes2, 0);

        Collection<Difference> diffs = m1.compare(null, m2);
        assertEquals(1, diffs.size());
        assertTrue(removed(nodes1[0], param(0, 0), diffs));
    }

    @Test
    public void node_single___add() throws IOException {
        Node[] nodes = createNodes(1);
        Method m1 = new Method();
        Method m2 = createMethod(nodes, 0);

        Collection<Difference> diffs = m1.compare(null, m2);
        assertEquals(1, diffs.size());
        assertTrue(added(m1, m2.getBody(), diffs));
    }


    @Test
    public void node_single___add___pre_exist() throws IOException {
        Node[] nodes = createNodes(1);
        Method m1 = new Method();
        m1.addElement(new MethodBody());
        Method m2 = createMethod(nodes, 0);

        Collection<Difference> diffs = m1.compare(null, m2);
        assertEquals(1, diffs.size());
        assertTrue(added(m1.getBody(), nodes[0], diffs));
    }

    @Test
    public void node_single___remove() throws IOException {
        Node[] nodes = createNodes(1);
        Method m1 = createMethod(nodes, 0);
        Method m2 = new Method();

        Collection<Difference> diffs = m1.compare(null, m2);
        assertEquals(1, diffs.size());
        assertTrue(removed(m1, m1.getBody(), diffs));
    }

    @Test
    public void node_single___remove___post_exist() throws IOException {
        Node[] nodes = createNodes(1);
        Method m1 = createMethod(nodes, 0);
        Method m2 = new Method();
        m2.addElement(new MethodBody());

        Collection<Difference> diffs = m1.compare(null, m2);
        assertEquals(1, diffs.size());
        assertTrue(removed(m1.getBody(), node(0), diffs));
    }

}

