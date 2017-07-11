package com.comptel.bst.tools.diff.parser.entity.jaxb;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.comptel.bst.tools.diff.parser.entity.bst.OutputParameters;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;

/*
 * Subclass of the general JAXBParameterGroup. Otherwise we could
 * use that instead, but the output parameter group can have
 * an attribute unlike the others.
 */
public class JAXBOutputParameters extends JAXBParameterGroup {

    @XmlAttribute(name = "all")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String all; // Sole attribute

    public JAXBOutputParameters() {
        super();
    }

    public JAXBOutputParameters(Element e) {
        super(e);
    }

    // Does the conversion from internal element object into XML object
    @Override
    public void convert(Element elem) {
        super.convert(elem);
        this.all = elem.getAttr(OutputParameters.ALL_ATTR);
    }

    /*
     * Getters and setters
     */

    public String getAll() {
        return all;
    }

    public void setAll(String value) {
        this.all = value;
    }

}
