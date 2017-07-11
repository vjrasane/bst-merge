package com.comptel.bst.tools.diff.parser.entity.jaxb;

import com.comptel.bst.tools.diff.parser.entity.generic.Element;

// Interface for making sure JAXB classes implement the convert method
public interface JAXBObject {
    public void convert(Element elem);
}
