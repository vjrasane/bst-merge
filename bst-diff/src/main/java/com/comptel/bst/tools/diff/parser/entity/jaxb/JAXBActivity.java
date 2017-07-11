package com.comptel.bst.tools.diff.parser.entity.jaxb;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlType;

import com.comptel.bst.tools.diff.parser.entity.bst.method.Method;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.utils.DiffUtils;

/*
 * Common abstract superclass for the JAXBMethod and JAXBStep classes, as
 * they share several elements.
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "clazz", "description", "activityDuration", "activityName" })
public abstract class JAXBActivity implements JAXBObject {

    // Common unique configuration elements
    @XmlElement(name = "class", required = false)
    protected String clazz;
    @XmlElement(required = true)
    protected String description;
    @XmlElement(name = "activity-duration")
    protected String activityDuration;
    @XmlElement(name = "activity-name")
    protected String activityName;

    public JAXBActivity() {}

    public JAXBActivity(Element elem) {
        this.convert(elem);
    }

    // Does the conversion from internal element object into XML object
    @Override
    public void convert(Element elem) {
        // Set the common unique elements
        this.clazz = DiffUtils.nullSafeValue(elem.findUniqueElement(Method.CLASS_TAG));
        this.activityName = DiffUtils.nullSafeValue(elem.findUniqueElement(Method.ACTIVITY_NAME_TAG));
        this.activityDuration = DiffUtils.nullSafeValue(elem.findUniqueElement(Method.ACTIVITY_DURATION_TAG));
        this.description = DiffUtils.nullSafeValue(elem.findUniqueElement(Method.DESCRIPTION_TAG));
    }

    /*
     * Getters and setters
     */

    public String getActivityDuration() {
        return activityDuration;
    }

    public String getActivityName() {
        return activityName;
    }

    public String getClazz() {
        return clazz;
    }

    public String getDescription() {
        return description;
    }

    public void setActivityDuration(String value) {
        this.activityDuration = value;
    }

    public void setActivityName(String value) {
        this.activityName = value;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public void setDescription(String value) {
        this.description = value;
    }

}
