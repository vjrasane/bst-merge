package com.comptel.bst.tools.diff.parser.entity.jaxb;

import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.comptel.bst.tools.diff.parser.entity.bst.OutputParameters;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;

public class JAXBOutputParameters extends JAXBParameterGroup {

    @XmlAttribute(name = "all")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String all;

    public JAXBOutputParameters() {
        super();
    }

    public JAXBOutputParameters(Element e) {
        super(e);
    }

    @Override
    public void convert(Element elem) {
        super.convert(elem);
        this.all = elem.getAttr(OutputParameters.ALL_ATTR);
    }

    public String getAll() {
        return all;
    }

    public void setAll(String value) {
        this.all = value;
    }

}
