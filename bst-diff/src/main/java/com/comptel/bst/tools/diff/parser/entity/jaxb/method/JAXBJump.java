package com.comptel.bst.tools.diff.parser.entity.jaxb.method;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlTransient;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.parser.entity.jaxb.JAXBObject;
import com.comptel.bst.tools.diff.utils.DiffConstants;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "value" })
@XmlRootElement(name = "jump")
public class JAXBJump implements JAXBObject {

    @XmlAttribute(name = "index")
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    protected String index;
    @XmlValue
    protected String value;

    @XmlTransient
    private String jumpId;

    @XmlTransient
    private String targetName;

    @XmlTransient
    private String position;

    public JAXBJump() {}

    public JAXBJump(Element j) {
        this.convert(j);
    }

    @Override
    public void convert(Element j) {
        String target = j.getValue();
        this.index = target != null ? target : DiffConstants.DEFAULT_LINK_VALUE;
    }

    public String getIndex() {
        return index;
    }

    public String getJumpId() {
        return jumpId;
    }

    public String getvalue() {
        return value;
    }

    public void setIndex(String value) {
        this.index = value;
    }

    public void setJumpId(String jumpId) {
        this.jumpId = jumpId;
    }

    public void setvalue(String value) {
        this.value = value;
    }

    public String getJumpPos() {
        return position;
    }

    public void setJumpPos(String jumpPos) {
        this.position = jumpPos;
    }

    public String getTargetName() {
        return targetName;
    }

    public void setTargetName(String targetName) {
        this.targetName = targetName;
    }

}
