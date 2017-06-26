package com.comptel.bst.tools.diff.parser.entity.jaxb;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlType;

import com.comptel.bst.tools.diff.parser.entity.bst.Parameter;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "parameter" })
public class JAXBParameterGroup implements JAXBObject {

    protected List<JAXBParameter> parameter;

    public JAXBParameterGroup() {}

    public JAXBParameterGroup(Element elem) {
        this.convert(elem);
    }

    @Override
    public void convert(Element elem) {
        elem.getElements(Parameter.TAG).forEach(p -> this.getParameter().add(new JAXBParameter(p)));
    }

    public List<JAXBParameter> getParameter() {
        if (parameter == null) {
            parameter = new ArrayList<JAXBParameter>();
        }
        return this.parameter;
    }

}
