package com.comptel.bst.tools.diff.parser.entity.bst.method;

import java.util.List;

import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.parser.entity.generic.Tag;
import com.comptel.bst.tools.diff.parser.entity.jaxb.method.JAXBJump;

public class Links extends Element {

    private static final long serialVersionUID = 1L;

    public static final Tag TAG = Tag.unique("links").hide();

    public static final String PRIMARY_LINK_TAG_NAME = "primary link";
    public static final String SECONDARY_LINK_TAG_NAME = "secondary link";
    public static final String ADDITIONAL_LINK_TAG_NAME = "additional link";

    public static final Tag PRIMARY_LINK_TAG = Tag.unique(PRIMARY_LINK_TAG_NAME);
    public static final Tag SECONDARY_LINK_TAG = Tag.unique(SECONDARY_LINK_TAG_NAME);

    protected Links() {
        super(TAG);
    }

    public Links(String primaryId, String primaryName, String secondaryId, String secondaryName, List<JAXBJump> additionalLinks) {
        this();

        if(primaryId != null)
            this.addElement(StandardJump.primary(primaryId, primaryName));
        if(secondaryId != null)
            this.addElement(StandardJump.secondary(secondaryId, secondaryName));

        additionalLinks.forEach(l -> {
            this.addElement(new Jump(l));
        });
    }
}
