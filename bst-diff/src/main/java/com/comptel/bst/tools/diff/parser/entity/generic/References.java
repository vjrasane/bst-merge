package com.comptel.bst.tools.diff.parser.entity.generic;

import java.util.Map;

/*
 * Container element for the incoming references to a node.  There is no XML representation for this
 * and it is only used by the merge algorithm to determine if removing a node would leave behind
 * null references.
 */
public class References extends Element {

    private static final long serialVersionUID = 1L;

    // The reference container object is not shown in the output
    public static final Tag TAG = Tag.unique("references").hide();

    // The constructor receives the map of references directly
    public References(Map<String, String> refs) {
        this();
        refs.forEach((r,s) -> this.addReference(r,s));
    }

    public References() {
        super(TAG);
    }

    public void addReference(String nodeId, String sourceName) {
        this.addElement(new Reference(nodeId, sourceName));
    }
}
