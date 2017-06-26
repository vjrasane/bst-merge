package com.comptel.bst.tools.diff;

import java.util.Collection;

import com.comptel.bst.tools.diff.comparison.differences.Difference;
import com.comptel.bst.tools.diff.parser.entity.bst.Parameter;
import com.comptel.bst.tools.diff.parser.entity.bst.method.Method;
import com.comptel.bst.tools.diff.parser.entity.bst.method.Node;
import com.comptel.bst.tools.diff.parser.entity.bst.method.NodeOutputParameter;
import com.comptel.bst.tools.diff.parser.entity.generic.Attribute;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.parser.entity.generic.Reference;


public class DifferenceTest {

    protected static Node node(int id) {
        return new Node("n" + id, "Node");
    }

    protected static Parameter param(int name, int value) {
        return new Parameter(Parameter.TAG, "p" + name, "v" + value);
    }

    protected boolean added(Element context, Element elem, Collection<Difference> diffs) {
        return diffs.contains(Difference.added(context, elem));
    }

    public static Element ref(int i) {
        return new Reference("n" + i, "n" + i);
    }

    protected void addParallelNodes(Method method, Node[] nodes, int sourceIndex, int endIndex, int... nodeIndexes) {
        Node source = nodes[sourceIndex];

        Node prev = source;
        for (int i : nodeIndexes) {
            Node current = nodes[i];
            method.addNode(current);
            prev.setSecondaryLink(current);
            prev = current;
        }
        if (endIndex > 0) {
            Node end = nodes[endIndex];
            prev.setSecondaryLink(end);
        }
    }

    protected Attribute attr(int name, int value) {
        return new Attribute("a" + name, "v" + value);
    }

    protected boolean attrChanged(Element orig, Element changed, String attr, Collection<Difference> diffs) {
        return diffs.contains(Difference.attrChanged(orig, changed, attr));
    }

    protected Method createMethod(Node[] nodes, int... indexes) {
        Method method = new Method();
        Node prev = null;
        for (int i : indexes) {
            Node current = nodes[i];
            method.addNode(nodes[i]);
            if (prev != null)
                prev.setPrimaryLink(current);
            prev = current;
        }
        return method;
    }

    protected Node[] createNodes(int amount) {
        Node[] nodes = new Node[amount];
        for (int i = 0; i < nodes.length; i++) {
            nodes[i] = node(i);
        }
        return nodes;
    }

    protected Attribute jump(int index, int id) {
        return new Attribute(Integer.toString(index), "n" + id);
    }

    protected NodeOutputParameter outParam(int name, int value) {
        return new NodeOutputParameter("p" + name, "v" + value);
    }

    protected boolean removed(Element context, Element param, Collection<Difference> diffs) {
        return diffs.contains(Difference.removed(context, param));
    }

    protected boolean valueChanged(Element orig, Element changed, Collection<Difference> diffs) {
        return diffs.contains(Difference.valueChanged(orig, changed));
    }

}
