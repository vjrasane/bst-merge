package com.comptel.bst.tools.diff.parser.entity.jaxb.method;

import javax.xml.bind.annotation.XmlRootElement;

import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.parser.entity.jaxb.JAXBParameter;
/*
 * Subclass of the general JAXBParameter which is used in the place
 * of most parameters, except here. This is because the tag of the
 * node output parameters differs
 */
@XmlRootElement(name = "out_parameter")
public class JAXBNodeOutputParameter extends JAXBParameter {

    public JAXBNodeOutputParameter() {
        super();
    }

    public JAXBNodeOutputParameter(Element elem) {
        super(elem);
    }
}
