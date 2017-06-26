package com.comptel.bst.tools.diff.parser.entity.bst;

import org.apache.commons.io.FilenameUtils;

import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.parser.entity.generic.Tag;
import com.comptel.bst.tools.diff.parser.entity.jaxb.JAXBParameter;


@SuppressWarnings("serial") // Serial UID not needed for abstract class
public abstract class Activity extends Element {

    public static final Tag CLASS_TAG =  Tag.unique("class");
    public static final Tag DESCRIPTION_TAG =  Tag.unique("description");
    public static final Tag ACTIVITY_DURATION_TAG =  Tag.unique("activity-duration");
    public static final Tag ACTIVITY_NAME_TAG =  Tag.unique("activity-name");

    public Activity(Tag tag) {
        super(tag);
    }

    public void addInputParam(Parameter param) {
        Element input = this.getInput();
        if (input == null) {
            input = new InputParameters();
            this.addElement(input);
        }
        input.addElement(param);
    }

    public void addOutputParam(Parameter param) {
        Element output = this.getOutput();
        if (output == null) {
            output = new OutputParameters<JAXBParameter>();
            this.addElement(output);
        }
        output.addElement(param);
    }

    public Element getInput() {
        return this.findUniqueElement(InputParameters.TAG);
    }

    public Element getOutput() {
        return this.findUniqueElement(OutputParameters.TAG);
    }

    @Override
    public String toSimpleString() {
        return FilenameUtils.removeExtension(this.getId());
    }

}
