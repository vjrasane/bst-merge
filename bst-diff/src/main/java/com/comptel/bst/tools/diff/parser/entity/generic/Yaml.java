package com.comptel.bst.tools.diff.parser.entity.generic;

import java.util.List;

import com.comptel.bst.tools.diff.comparison.differences.Difference;

/*
 * Container class for the XML trees. By placing them under one
 * parent element, we can run the entire merge algorithm once
 * starting from this element
 */
public class Yaml extends Element {

    private static final long serialVersionUID = 1L;

    // The Yaml object is hidden in the output messages
    public static final Tag TAG = Tag.unique("yaml").hide();

    public Yaml() {
        super(TAG);
    }

    // Small convenience, no need to pass context as this is the topmost element
    public List<Difference> compare(Yaml other) {
        return this.compare(null, other);
    }

}
