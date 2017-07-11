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
import com.comptel.bst.tools.diff.parser.entity.bst.method.StandardJump;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.parser.entity.jaxb.JAXBObject;
import com.comptel.bst.tools.diff.parser.entity.jaxb.JAXBParameter;
import com.comptel.bst.tools.diff.utils.DiffConstants;

/*
 * XML representation of a flowchart node
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "jump", "instanceDescription", "parameter", "outParameter" })
@XmlRootElement(name = "exec")
public class JAXBExec implements JAXBObject {

    // Node attributes including connectors
    @XmlAttribute(name = "id", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String id; // Identifier (quite self-evident)
    @XmlAttribute(name = "jump0")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String jump0;
    @XmlAttribute(name = "jump1")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String jump1;
    @XmlAttribute(name = "name", required = true)
    @XmlJavaTypeAdapter(NormalizedStringAdapter.class)
    protected String name;

    // List of additional connectors present in branching nodes
    protected List<JAXBJump> jump;

    // List of description elements
    @XmlElement(name = "instance_description")
    protected List<JAXBInstanceDescription> instanceDescription;

    // Input parameters, confusingly not tagged as such
    protected List<JAXBParameter> parameter;
    // Output parameters
    @XmlElement(name = "out_parameter")
    protected List<JAXBNodeOutputParameter> outParameter;

    /*
     * Transient data about the node, that is not present in the XML
     * and is populated once it is pre-processed
     */
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

    // Does the conversion from internal element object into XML object
    @Override
    public void convert(Element elem) {
        // Set the non-connector attributes
        this.id = elem.getId();
        this.name = elem.getAttr(Node.NAME_ATTR);

        // Position data is used later to sort the nodes
        this.position = elem.getData(Node.POS_DATA);

        /*
         *  Handle the connector attributes and elements.
         *  At this point they should be already post-processed
         *  so that the values are again relative
         */
        Element links = elem.findUniqueElement(Links.TAG);
        if (links != null) {
            // Primary and secondary links
            this.jump0 = getLinkValue(links.findUniqueElement(StandardJump.SECONDARY_LINK_TAG));
            this.jump1 = getLinkValue(links.findUniqueElement(StandardJump.PRIMARY_LINK_TAG));

            // Convert the additional links as they are added
            List<Element> additionalLinks = links.getElements(Jump.TAG);
            additionalLinks.forEach(l -> this.getJump().add(new JAXBJump(l)));
        }

        /*
         *  Add input and output parameters if present
         *  (internally they are contained within a parameter group
         *  but in the XML they are direct child elements of the node)
         */

        Element input = elem.findUniqueElement(InputParameters.TAG);
        if (input != null)
            input.getElements(Parameter.TAG).forEach(p -> this.getParameter().add(new JAXBParameter(p)));

        Element output = elem.findUniqueElement(OutputParameters.TAG);
        if (output != null)
            output.getElements(NodeOutputParameter.TAG).forEach(p -> this.getOutParameter().add(new JAXBNodeOutputParameter(p)));

        // Add the input descriptions
        elem.getElements(Node.INSTANCE_DESCRIPTION_TAG).forEach(d -> this.getInstanceDescription().add(new JAXBInstanceDescription(d)));
    }

    /*
     * Convenience methods, getters and setters
     */

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
