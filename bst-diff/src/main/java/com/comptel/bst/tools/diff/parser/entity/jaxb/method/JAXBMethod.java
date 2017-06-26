package com.comptel.bst.tools.diff.parser.entity.jaxb.method;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.comptel.bst.tools.common.CommonUtils;
import com.comptel.bst.tools.diff.parser.entity.bst.InputParameters;
import com.comptel.bst.tools.diff.parser.entity.bst.OutputParameters;
import com.comptel.bst.tools.diff.parser.entity.bst.method.Method;
import com.comptel.bst.tools.diff.parser.entity.bst.method.MethodBody;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.parser.entity.jaxb.JAXBActivity;
import com.comptel.bst.tools.diff.parser.entity.jaxb.JAXBOutputParameters;
import com.comptel.bst.tools.diff.parser.entity.jaxb.JAXBParameterGroup;
import com.comptel.bst.tools.diff.utils.DiffUtils;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "mainlineEnabled", "catalogDriven", "input", "output", "body" })
@XmlRootElement(name = "method")
public class JAXBMethod extends JAXBActivity {

    @XmlAttribute(name = "parallel")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String parallel;
    @XmlAttribute(name = "parentId")
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String parentId;

    @XmlElement(name = "mainline-enabled")
    protected String mainlineEnabled;
    @XmlElement(required = true)
    protected JAXBParameterGroup input;
    @XmlElement(required = true)
    protected JAXBOutputParameters output;
    @XmlElement(required = true)
    protected JAXBMethodBody body;
    @XmlElement(name = "catalog-driven")
    protected String catalogDriven;

    public JAXBMethod() {
        super();
    }

    public JAXBMethod(Element elem) {
        super(elem);
    }

    @Override
    public void convert(Element elem) {
        super.convert(elem);

        this.mainlineEnabled = DiffUtils.nullSafeValue(elem.findUniqueElement(Method.MAINLINE_ENABLED));
        this.catalogDriven = DiffUtils.nullSafeValue(elem.findUniqueElement(Method.CATALOG_DRIVEN));

        this.output = CommonUtils.nullSafeApply(elem.findUniqueElement(OutputParameters.TAG), o -> new JAXBOutputParameters(o));
        this.input = CommonUtils.nullSafeApply(elem.findUniqueElement(InputParameters.TAG), o -> new JAXBParameterGroup(o));
        this.body = CommonUtils.nullSafeApply(elem.findUniqueElement(MethodBody.TAG), o -> new JAXBMethodBody(o));

        this.parallel = elem.getAttr(Method.PARALLEL_ATTR);
        this.parentId = elem.getAttr(Method.PARENT_ID_ATTR);
    }

    public JAXBMethodBody getBody() {
        return body;
    }

    public String getCatalogDriven() {
        return catalogDriven;
    }

    public JAXBParameterGroup getInput() {
        return input;
    }

    public String getMainlineEnabled() {
        return mainlineEnabled;
    }

    public JAXBOutputParameters getOutput() {
        return output;
    }

    public String getParallel() {
        if (parallel == null) {
            return "no";
        } else {
            return parallel;
        }
    }

    public String getParentId() {
        return parentId;
    }

    public void setBody(JAXBMethodBody value) {
        this.body = value;
    }

    public void setCatalogDriven(String catalogDriven) {
        this.catalogDriven = catalogDriven;
    }

    public void setInput(JAXBParameterGroup value) {
        this.input = value;
    }

    public void setMainlineEnabled(String value) {
        this.mainlineEnabled = value;
    }

    public void setOutput(JAXBOutputParameters value) {
        this.output = value;
    }

    public void setParallel(String value) {
        this.parallel = value;
    }

    public void setParentId(String value) {
        this.parentId = value;
    }

    @Override
    public String toString() {
        return "Method: [ " + this.body + " ]";
    }

}
