package com.comptel.bst.tools.diff.parser.entity.bst.method;

import com.comptel.bst.tools.diff.parser.entity.bst.Parameter;
import com.comptel.bst.tools.diff.parser.entity.generic.Tag;
import com.comptel.bst.tools.diff.parser.entity.jaxb.JAXBParameter;


public class NodeOutputParameter extends Parameter {

    private static final long serialVersionUID = 1L;

    public static final Tag TAG = Tag.identifiable("out_parameter", ID_ATTR);

    public NodeOutputParameter(JAXBParameter p) {
        super(TAG, p);
    }

    public NodeOutputParameter(String id, String value) {
        super(TAG, id, value);
    }

}
