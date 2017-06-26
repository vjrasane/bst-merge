package com.comptel.bst.tools.diff.comparison;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.StringUtils;

import com.comptel.bst.tools.diff.comparison.differences.Difference;
import com.comptel.bst.tools.diff.comparison.differences.Difference.Branch;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.utils.DiffConstants;
import com.comptel.bst.tools.diff.utils.DiffUtils;
import com.comptel.bst.tools.diff.utils.OutputCharset;

public class OutputTree {

    private static OutputCharset OUTPUT_CHARSET = OutputCharset.UTF8;
    private OutputTreeNode root;

    public OutputTree() {}

    public OutputTree(Branch branch, Difference... diffs) {
        this(branch, Arrays.asList(diffs));
    }

    public <E extends OutputElement> OutputTree(E elem) {
        elem.addTo(this);
    }

    public <E extends OutputElement> OutputTree(List<E> elems) {
        elems.forEach(e -> e.addTo(this));
    }

    public OutputTree(Branch branch, List<Difference> diffs) {
        for (Difference e : diffs) {
            addElement(branch, e);
        }
    }

    public OutputTree add(Branch branch, List<Difference> diffs) {
        for (Difference e : diffs) {
            addElement(branch, e);
        }
        return this;
    }

    public OutputTree add(Branch branch, Difference diff) {
        this.addElement(branch, diff);
        return this;
    }

    public OutputTree add(Branch branch, Element target, Difference diff) {
        this.addElement(branch, target, diff.getMessage());
        return this;
    }

    private void addElement(Branch branch, Element target, String msg) {
        OutputTreeNode current = null;
        OutputTreeNode prev = null;
        List<Element> path = DiffUtils.getElementPath(target);
        for (Element e : path) {
            OutputTreeNode node = new OutputTreeNode(e);
            if (current == null) {
                if (this.root == null)
                    this.root = node;
                current = this.root;
            } else {
                if (!node.elem.isHidden()) {
                    OutputTreeNode next = current.children.stream().filter(n -> n.elem.matches(e)).findFirst().orElse(null);
                    if (next == null) {
                        next = node;
                        current.children.add(next);
                    }
                    prev = current;
                    current = next;
                }
            }
        }
        if (!current.elem.isHidden())
            current.add(branch, msg);
        else if (prev != null)
            prev.add(branch, msg);
    }

    private void addElement(Branch branch, Difference diff) {
        addElement(branch, diff.getTargetElement(), diff.getMessage());
    }

    public String getMessage() {
        return getMessage("");
    }

    public String getMessage(String indent) {
        StringBuilder b = new StringBuilder();
        for (OutputTreeNode child : this.root.children) {
            traverse(indent, indent, child, b);
            b.append("\n");
        }
        return b.toString();
    }

    // I honestly don't really understand how this works anymore, so better not touch it as long as it works...
    private void traverse(String subMarker, String indent, OutputTreeNode node, StringBuilder b) {
        if (!node.elem.isHidden())
            b.append(subMarker + node.elem.toSimpleString() + "\n");

        String msgIndent = indent
                + (node.children.isEmpty() ? OUTPUT_CHARSET.indent : OUTPUT_CHARSET.extension);

        for (Branch branch : Branch.values()) {
            Set<String> msgs = node.messages.get(branch);
            if (msgs != null) {
                String label = msgIndent + DiffConstants.OUTPUT_ELEMENT_MARKER + branch.label;
                String empty = msgIndent + StringUtils.repeat(' ', DiffConstants.OUTPUT_ELEMENT_MARKER.length() + branch.label.length());
                for (String msg : msgs) {
                    b.append(label + msg + "\n");
                    label = empty;
                }
            }
        }

        for (int i = 0; i < node.children.size(); i++) {
            OutputTreeNode child = node.children.get(i);
            boolean hidden = child.elem.isHidden();
            String marker = hidden ? "" : OUTPUT_CHARSET.subElement;
            String indentMarker = hidden ? "" : OUTPUT_CHARSET.indent;

            if (!hidden && node.children.indexOf(child) == node.children.size() - 1)
                marker = hidden ? "" : OUTPUT_CHARSET.lastElement;
            else
                indentMarker = hidden ? "" : OUTPUT_CHARSET.extension;

            traverse(indent + marker, indent + indentMarker, child, b);
        }
    }

    @Override
    public String toString() {
        return this.getMessage();
    }

    public static class OutputTreeNode {
        public OutputTreeNode(Element elem) {
            this.elem = elem;
        }

        private Element elem;
        private List<OutputTreeNode> children = new ArrayList<OutputTreeNode>();

        private Map<Branch, Set<String>> messages = new HashMap<Difference.Branch, Set<String>>();

        public void add(Branch branch, String message) {
            Set<String> msgList = messages.get(branch);
            if (msgList == null) {
                msgList = new HashSet<String>();
                messages.put(branch, msgList);
            }
            msgList.add(message);
        }
    }

    public static void setOutputCharset(OutputCharset charset) {
        OUTPUT_CHARSET = charset;
    }

    public interface OutputElement {
        public void addTo(OutputTree tree);
    }

}
