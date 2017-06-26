package com.comptel.bst.tools.diff.parser.entity.jaxb.method;

import javax.xml.bind.annotation.XmlRootElement;

import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.parser.entity.jaxb.JAXBParameter;

@XmlRootElement(name = "out_parameter")
public class JAXBNodeOutputParameter extends JAXBParameter {

    public JAXBNodeOutputParameter() {
        super();
    }

    public JAXBNodeOutputParameter(Element elem) {
        super(elem);
    }
}
