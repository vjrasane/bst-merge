package com.comptel.bst.tools.diff.parser.entity.bst.method;

import java.util.List;

import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.parser.entity.generic.Tag;
import com.comptel.bst.tools.diff.parser.entity.jaxb.method.JAXBJump;

/*
 * A container element that holds all the connectors of a node.
 * This is mostly for easier manipulation of the connectors and for some output sugar
 */
public class Links extends Element {

    private static final long serialVersionUID = 1L;

    // The link container is hidden in the output as it has no representation in the XML
    public static final Tag TAG = Tag.unique("links").hide();

    protected Links() {
        super(TAG);
    }

    /*
     *  Takes the identifier and name of the primary and secondary links as the first four parameters and the list of additional links last.
     *  In practice no node should have both primary/secondary links AND the additional links, but it is possible in the XML
     */
    public Links(String primaryId, String primaryName, String secondaryId, String secondaryName, List<JAXBJump> additionalLinks) {
        this();

        if(primaryId != null)
            this.addElement(StandardJump.primary(primaryId, primaryName));
        if(secondaryId != null)
            this.addElement(StandardJump.secondary(secondaryId, secondaryName));

        // Convert the XML jump representations to internal elements as they are added
        additionalLinks.forEach(l -> {
            this.addElement(new Jump(l));
        });
    }
}
