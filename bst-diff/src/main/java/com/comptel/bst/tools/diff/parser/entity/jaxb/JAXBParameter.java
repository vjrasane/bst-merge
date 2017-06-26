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

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "value" })
public class JAXBParameter implements JAXBObject {

    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String name;
    @XmlAttribute(name = "overwrite")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String overwrite;
    @XmlValue
    protected String value;

    public JAXBParameter() {}

    public JAXBParameter(Element elem) {
        this.convert(elem);
    }

    @Override
    public void convert(Element elem) {
        this.name = elem.getId();
        this.overwrite = elem.getAttr(Parameter.OVERWRITE_ATTR);
        this.value = elem.getValue();
    }

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
