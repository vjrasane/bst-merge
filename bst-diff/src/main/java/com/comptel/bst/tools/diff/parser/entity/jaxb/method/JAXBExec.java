package com.comptel.bst.tools.diff.parser.entity.jaxb.method;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.NormalizedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.comptel.bst.tools.common.CommonUtils;
import com.comptel.bst.tools.diff.parser.entity.bst.InputParameters;
import com.comptel.bst.tools.diff.parser.entity.bst.OutputParameters;
import com.comptel.bst.tools.diff.parser.entity.bst.Parameter;
import com.comptel.bst.tools.diff.parser.entity.bst.method.Jump;
import com.comptel.bst.tools.diff.parser.entity.bst.method.Links;
import com.comptel.bst.tools.diff.parser.entity.bst.method.Node;
import com.comptel.bst.tools.diff.parser.entity.bst.method.NodeOutputParameter;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.parser.entity.jaxb.JAXBObject;
import com.comptel.bst.tools.diff.parser.entity.jaxb.JAXBParameter;
import com.comptel.bst.tools.diff.utils.DiffConstants;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "jump", "instanceDescription", "parameter", "outParameter" })
@XmlRootElement(name = "exec")
public class JAXBExec implements JAXBObject {

    @XmlAttribute(name = "id", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String id;
    @XmlAttribute(name = "jump0")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String jump0;
    @XmlAttribute(name = "jump1")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String jump1;
    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String name;

    protected List<JAXBJump> jump;
    @XmlElement(name = "instance_description")
    protected List<JAXBInstanceDescription> instanceDescription;
    protected List<JAXBParameter> parameter;
    @XmlElement(name = "out_parameter")
    protected List<JAXBNodeOutputParameter> outParameter;

    @XmlTransient
    private String primaryJumpId;
    @XmlTransient
    private String secondaryJumpId;

    @XmlTransient
    private String primaryTargetName;
    @XmlTransient
    private String secondaryTargetName;

    @XmlTransient
    private String position;

    @XmlTransient
    private Map<String, String> references = new HashMap<String, String>();

    public JAXBExec() {}

    public JAXBExec(Element e) {
        this.convert(e);
    }

    @Override
    public void convert(Element elem) {
        this.id = elem.getId();
        this.name = elem.getAttr(Node.NAME_ATTR);

        this.position = elem.getData(Node.POS_DATA);

        Element links = elem.findUniqueElement(Links.TAG);
        if (links != null) {
            this.jump0 = getLinkValue(links.findUniqueElement(Links.SECONDARY_LINK_TAG));
            this.jump1 = getLinkValue(links.findUniqueElement(Links.PRIMARY_LINK_TAG));

            List<Element> additionalLinks = links.getElements(Jump.TAG);
            additionalLinks.forEach(l -> this.getJump().add(new JAXBJump(l)));
        }

        Element input = elem.findUniqueElement(InputParameters.TAG);
        if (input != null)
            input.getElements(Parameter.TAG).forEach(p -> this.getParameter().add(new JAXBParameter(p)));

        Element output = elem.findUniqueElement(OutputParameters.TAG);
        if (output != null)
            output.getElements(NodeOutputParameter.TAG).forEach(p -> this.getOutParameter().add(new JAXBNodeOutputParameter(p)));

        elem.getElements(Node.INSTANCE_DESCRIPTION_TAG).forEach(d -> this.getInstanceDescription().add(new JAXBInstanceDescription(d)));
    }

    private String getLinkValue(Element link) {
        return CommonUtils.nullSafeApply(link, e -> e.getValue() != null ? e.getValue() : DiffConstants.DEFAULT_LINK_VALUE);
    }

    public String getFirstJumpId() {
        return primaryJumpId;
    }

    public String getId() {
        return id;
    }

    public List<JAXBInstanceDescription> getInstanceDescription() {
        if (instanceDescription == null) {
            instanceDescription = new ArrayList<JAXBInstanceDescription>();
        }
        return this.instanceDescription;
    }

    public List<JAXBJump> getJump() {
        if (jump == null) {
            jump = new ArrayList<JAXBJump>();
        }
        return this.jump;
    }

    public String getJump0() {
        if (jump0 == null)
            return "0";
        return jump0;
    }

    public String getJump1() {
        if (jump1 == null)
            return "0";
        return jump1;
    }

    public String getName() {
        return name;
    }

    public List<JAXBNodeOutputParameter> getOutParameter() {
        if (outParameter == null) {
            outParameter = new ArrayList<JAXBNodeOutputParameter>();
        }
        return this.outParameter;
    }

    public List<JAXBParameter> getParameter() {
        if (parameter == null) {
            parameter = new ArrayList<JAXBParameter>();
        }
        return this.parameter;
    }

    public void setId(String value) {
        this.id = value;
    }

    public void setJump0(String value) {
        if (value == null)
            this.jump0 = "0";
        this.jump0 = value;
    }

    public void setJump1(String value) {
        if (value == null)
            this.jump1 = "0";
        this.jump1 = value;
    }

    public void setName(String value) {
        this.name = value;
    }

    public Map<String, String> getReferences() {
        return references;
    }

    public void setReferences(Map<String, String> references) {
        this.references = references;
    }

    public String getPosition() {
        return position;
    }

    public void setPosition(String position) {
        this.position = position;
    }

    public String getPrimaryJumpId() {
        return primaryJumpId;
    }

    public void setPrimaryJumpId(String primaryJumpId) {
        this.primaryJumpId = primaryJumpId;
    }

    public String getSecondaryJumpId() {
        return secondaryJumpId;
    }

    public void setSecondaryJumpId(String secondaryJumpId) {
        this.secondaryJumpId = secondaryJumpId;
    }

    public String getPrimaryTargetName() {
        return primaryTargetName;
    }

    public void setPrimaryTargetName(String primaryTargetName) {
        this.primaryTargetName = primaryTargetName;
    }

    public String getSecondaryTargetName() {
        return secondaryTargetName;
    }

    public void setSecondaryTargetName(String secondaryTargetName) {
        this.secondaryTargetName = secondaryTargetName;
    }

    @Override
    public String toString() {
        return name;
    }

}
