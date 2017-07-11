package com.comptel.bst.tools.diff.parser.entity.jaxb.step;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.comptel.bst.tools.common.CommonUtils;
import com.comptel.bst.tools.diff.parser.entity.bst.InputParameters;
import com.comptel.bst.tools.diff.parser.entity.bst.OutputParameters;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.parser.entity.jaxb.JAXBActivity;
import com.comptel.bst.tools.diff.parser.entity.jaxb.JAXBOutputParameters;
import com.comptel.bst.tools.diff.parser.entity.jaxb.JAXBParameterGroup;

/*
 * XML representation of the 'step' node blueprints
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "input", "output" })
@XmlRootElement(name = "step")
public class JAXBStep extends JAXBActivity {

    // Input and output parameter groups
    @XmlElement(required = true)
    protected JAXBParameterGroup input;
    @XmlElement(required = true)
    protected JAXBOutputParameters output;

    public JAXBStep() {
        super();
    }

    public JAXBStep(Element elem) {
        super(elem);
    }

    // Does the conversion from internal element object into XML object
    @Override
    public void convert(Element elem) {
        // Do superclass conversion to set the common elements
        super.convert(elem);

        // Add input and output parameters, recursively converting the parameters as well
        this.output = CommonUtils.nullSafeApply(elem.findUniqueElement(OutputParameters.TAG), o -> new JAXBOutputParameters(o));
        this.input = CommonUtils.nullSafeApply(elem.findUniqueElement(InputParameters.TAG), o -> new JAXBParameterGroup(o));
    }

    /*
     * Getters and setters
     */

    public JAXBParameterGroup getInput() {
        return input;
    }

    public JAXBOutputParameters getOutput() {
        return output;
    }

    public void setInput(JAXBParameterGroup value) {
        this.input = value;
    }

    public void setOutput(JAXBOutputParameters value) {
        this.output = value;
    }

}
