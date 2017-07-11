package com.comptel.bst.tools.diff.parser.entity.generic;

import com.comptel.bst.tools.diff.utils.DiffConstants;

/*
 * Representation of an incoming reference to a node. There is no XML representation for this
 * and it is only used by the merge algorithm to determine if removing a node would leave behind
 * null references.
 */
public class Reference extends Element {

    private static final long serialVersionUID = 1L;

    /*
     *  Reference object is hidden in the output.
     *  Note that this does not mean that conflicts arising from
     *  the references are not shown, but rather that they are shown
     *  under the first non-hidden parent element instead of this element
     */
    public static final Tag TAG = Tag.generic("reference").hide();

    // Name of the source node, shown in output messages instead of the identifier
    public static final String SOURCE_NAME_DATA = DiffConstants.DATA_PREFIX + ":source-name";

    public Reference(String value, String source) {
        super(TAG, value);
        this.setSourceName(source); // Set the name of the source node
    }

    public void setSourceName(String name) {
        /*
         *  The name is added to transient data rather than attributes
         *  so that the algorithm does not use them in the merge
         */
        this.setData(SOURCE_NAME_DATA, name);
    }

    public String getSourceName() {
        return this.getData(SOURCE_NAME_DATA);
    }

    // Display a user friendly message about the reference in the output message
    @Override
    public String toSimpleString() {
        return "reference from " + this.getOutputValue();
    }

    // The output value should not be the identifier but instead the name of the node
    @Override
    public String getOutputValue() {
        return this.getSourceName();
    }

}
