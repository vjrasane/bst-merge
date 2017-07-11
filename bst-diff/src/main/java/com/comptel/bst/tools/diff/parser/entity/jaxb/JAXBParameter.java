package com.comptel.bst.tools.diff.parser.entity.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.comptel.bst.tools.diff.parser.entity.bst.Parameter;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;

/*
 * General XML representation of the node, method and step parameters.
 * This could otherwise be used everywhere, except the node output parameters
 * have a different tag name despite being otherwise exactly equal.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "value" })
public class JAXBParameter implements JAXBObject {

    // Parameter attributes
    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String name; // Identifier
    @XmlAttribute(name = "overwrite")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String overwrite;

    // Parameter value
    @XmlValue
    protected String value;

    public JAXBParameter() {}

    public JAXBParameter(Element elem) {
        this.convert(elem);
    }

    // Does the conversion from internal element object into XML object
    @Override
    public void convert(Element elem) {
        this.name = elem.getId(); // Set the name from the element ID
        this.overwrite = elem.getAttr(Parameter.OVERWRITE_ATTR);
        this.value = elem.getValue(); // Set value
    }

    /*
     * Getters and setters
     */
    public String getName() {
        return name;
    }

    public String getOverwrite() {
        return overwrite;
    }

    public String getvalue() {
        return value;
    }

    public void setName(String value) {
        this.name = value;
    }

    public void setOverwrite(String value) {
        this.overwrite = value;
    }

    public void setvalue(String value) {
        this.value = value;
    }

}
