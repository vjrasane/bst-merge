package com.comptel.bst.tools.diff.parser.entity.bst.method;

import com.comptel.bst.tools.diff.parser.entity.bst.Parameter;
import com.comptel.bst.tools.diff.parser.entity.generic.Tag;
import com.comptel.bst.tools.diff.parser.entity.jaxb.JAXBParameter;

/*
 * Subclass of the Parameter element specifically for nodes. For some reason the output
 * and input parameters are separated by their tag names within nodes, rather than with an
 * input/output parameter container like in method
 * 
 * It might be possible to use the parameter class with a different tag, but having this as
 * a subclass allows some nice tricks with Java generics.
 */
public class NodeOutputParameter extends Parameter {

    private static final long serialVersionUID = 1L;

    // Identifiable based on the parameter 'nameä, defined in the superclass
    public static final Tag TAG = Tag.identifiable("out_parameter", ID_ATTR);

    public NodeOutputParameter(JAXBParameter p) {
        super(TAG, p);
    }

    public NodeOutputParameter(String id, String value) {
        super(TAG, id, value);
    }

}
