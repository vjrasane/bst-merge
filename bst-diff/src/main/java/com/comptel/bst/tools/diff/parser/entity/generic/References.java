package com.comptel.bst.tools.diff.parser.entity.generic;

import java.util.Map;

public class References extends Element {

    private static final long serialVersionUID = 1L;

    public static final Tag TAG = Tag.unique("references").hide();

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
