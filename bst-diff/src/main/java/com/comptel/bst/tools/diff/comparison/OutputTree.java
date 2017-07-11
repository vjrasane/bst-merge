package com.comptel.bst.tools.diff.comparison;

import java.util.ArrayList;
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

/*
 * Class for constructing the string representation of differences and conflicts
 * as a descriptive tree structure. Classes that should be shown as output tree
 * have to implement the OutputTree.OutputElement interface
 */
public class OutputTree {

    // The global charset of the program output
    private static OutputCharset OUTPUT_CHARSET = OutputCharset.UTF8;

    private OutputTreeNode root; // Root node

    public OutputTree() {}

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

    // Adds the given message in the position of the target element, with the given branch prefix
    private void addElement(Branch branch, Element target, String msg) {
        // Keep track of which node we are at and which one was the previous node
        OutputTreeNode current = null;
        OutputTreeNode prev = null;

        // Traverse the path of the target node starting from the root node
        List<Element> path = DiffUtils.getElementPath(target);
        for (Element e : path) {
            OutputTreeNode node = new OutputTreeNode(e); // Create new output tree node
            if (current == null) {
                // This is the first node we are looking at
                if (this.root == null)
                    // The root of the tree has not been initialized yet
                    this.root = node;

                current = this.root;
            } else {
                if (!node.elem.isHidden()) {
                    // If the element is not hidden, get the next matching node in the tree
                    OutputTreeNode next = current.children.stream().filter(n -> n.elem.matches(e)).findFirst().orElse(null);
                    if (next == null) {
                        // There is no matching node, so we add the current node to the tree
                        next = node;
                        current.children.add(next);
                    }
                    // Update the current and next nodes
                    prev = current;
                    current = next;
                }
            }
        }

        /*
         * After we have traversed the tree, the current element points to the
         * leaf of the output tree corresponding to target. Previous element
         * points to the last non-hidden ancestor element
         */
        if (!current.elem.isHidden())
            // If current is not hidden we can add the message to it
            current.add(branch, msg);
        else if (prev != null)
            // Otherwise we add it to the last non-hidden ancestor element
            prev.add(branch, msg);
    }

    // Gets the output tree message indented with the given string
    public String getMessage(String indent) {
        StringBuilder b = new StringBuilder();
        // The root is the YAML file which is not shown
        for (OutputTreeNode child : this.root.children) {
            // Traverse the output tree
            traverse(indent, indent, child, b);
            b.append("\n");
        }
        return b.toString();
    }

    /*
     * Traverses the output tree and adds the appropriate messages to the string builder.
     * The subMarker and indent parameters are changed accordingly at each part of the recursion call tree.
     * The sub marker is used at each child node while the indent is used at the messages.
     * Hence, the submarker changes so that:
     *  1. It is correctly indented at each position in the tree
     *  2. It has the correct output marker placed before the element description
     * 
     * The indentation only keeps track of the correct amount of space before the message
     */
    private void traverse(String subMarker, String indent, OutputTreeNode node, StringBuilder b) {
        if (!node.elem.isHidden())
            /*
             *  If the element is not hidden, add a description to the tree with the current sub marker.
             *  The sub marker begins with the original indentation and is changed according to the
             *  position in the output tree
             */
            b.append(subMarker + node.elem.toSimpleString() + "\n");

        // Construct the message indentation, depending on whether the node has children
        String msgIndent = indent
                + (node.children.isEmpty() ? OUTPUT_CHARSET.indent : OUTPUT_CHARSET.extension);

        // Loop over the branches and display any messages the node has from them
        for (Branch branch : Branch.values()) {
            Set<String> msgs = node.messages.get(branch);
            if (msgs != null) {
                /*
                 *  Construct the indents for both the first message in each branch and each subsequent message
                 *  For those we do not repeat the branch label but instead substitute an empty indentation
                 */
                String label = msgIndent + DiffConstants.OUTPUT_ELEMENT_MARKER + branch.label;
                String empty = msgIndent + StringUtils.repeat(' ', DiffConstants.OUTPUT_ELEMENT_MARKER.length() + branch.label.length());
                for (String msg : msgs) {
                    b.append(label + msg + "\n");
                    label = empty;
                }
            }
        }

        // Loop over the node children and recursively call this method
        for (int i = 0; i < node.children.size(); i++) {
            OutputTreeNode child = node.children.get(i);
            boolean hidden = child.elem.isHidden();

            // Set indentation and submarker if the child is not hiddenr
            String marker = hidden ? "" : OUTPUT_CHARSET.subElement;
            String indentMarker = hidden ? "" : OUTPUT_CHARSET.indent;

            // Check whether the child is the last in its list and set the marker and indentation accordingly
            if (!hidden && node.children.indexOf(child) == node.children.size() - 1)
                marker = hidden ? "" : OUTPUT_CHARSET.lastElement;
            else
                indentMarker = hidden ? "" : OUTPUT_CHARSET.extension;

            // Recurse, adding the indentation to the determined marker and indentation
            traverse(indent + marker, indent + indentMarker, child, b);
        }
    }

    /*
     * Output node that contains the corresponding element,
     * children and possible messages for each branch
     */
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

    /*
     *  The interface that a class must implement in order
     *  to be viewed as output tree. See conflict, difference or choice
     *  as examples
     */
    public interface OutputElement {
        public void addTo(OutputTree tree);
    }

    /*
     * Some convenience methods
     */

    private void addElement(Branch branch, Difference diff) {
        addElement(branch, diff.getTargetElement(), diff.getMessage());
    }

    public String getMessage() {
        return getMessage("");
    }

    public static void setOutputCharset(OutputCharset charset) {
        OUTPUT_CHARSET = charset;
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

    @Override
    public String toString() {
        return this.getMessage();
    }
}
