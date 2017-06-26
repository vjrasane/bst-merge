package com.comptel.bst.tools.diff.parser.entity.bst.method;

import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.parser.entity.generic.Tag;


public class StandardJump extends Element {

    private static final long serialVersionUID = 1L;

    public static final String PRIMARY_LINK_TAG_NAME = "primary link";
    public static final String SECONDARY_LINK_TAG_NAME = "secondary link";

    public static final Tag PRIMARY_LINK_TAG = Tag.unique(PRIMARY_LINK_TAG_NAME);
    public static final Tag SECONDARY_LINK_TAG = Tag.unique(SECONDARY_LINK_TAG_NAME);

    private StandardJump(Tag tag, String id, String name) {
        super(tag, id);
        this.setTargetName(name);
    }

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

    @Override
    public String getOutputValue() {
        return this.getTargetName();
    }

}
