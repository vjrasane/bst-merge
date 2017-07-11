package com.comptel.bst.tools.diff.parser.entity.bst.method;

import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.parser.entity.generic.Tag;

/*
 * Represents the primary/secondary connectors of a node
 */
public class StandardJump extends Element {

    private static final long serialVersionUID = 1L;

    // The tag names are referenced in places that retrieve them from the node
    public static final String PRIMARY_LINK_TAG_NAME = "primary link";
    public static final String SECONDARY_LINK_TAG_NAME = "secondary link";

    /*
     *  Unlike the general jump elements, the primary and secondary links are
     *  unique i.e. there can be only one of each per node
     */
    public static final Tag PRIMARY_LINK_TAG = Tag.unique(PRIMARY_LINK_TAG_NAME);
    public static final Tag SECONDARY_LINK_TAG = Tag.unique(SECONDARY_LINK_TAG_NAME);

    /*
     *  This element is not conversible from any XML object since it
     *  does not have an XML equivalent (it is an attribute).
     */
    private StandardJump(Tag tag, String id, String name) {
        super(tag, id);
        this.setTargetName(name); // Set the target node name that is used in output messages
    }

    // The output messages should show the name of the target node rather than the ID
    @Override
    public String getOutputValue() {
        return this.getTargetName();
    }

    /*
     *  Convenience methods for creating the primary and secondary jumps
     */

    public static StandardJump primary(String id, String name) {
        return new StandardJump(PRIMARY_LINK_TAG, id, name);
    }

    public static StandardJump secondary(String id, String name) {
        return new StandardJump(SECONDARY_LINK_TAG, id, name);
    }

    private void setTargetName(String name) {
        this.setData(Jump.TARGET_NAME_DATA, name);
    }

    private String getTargetName() {
        return this.getData(Jump.TARGET_NAME_DATA);
    }



}
