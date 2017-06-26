package com.comptel.bst.tools.diff.parser.entity.bst.step;

import com.comptel.bst.tools.diff.parser.entity.bst.Activity;
import com.comptel.bst.tools.diff.parser.entity.bst.InputParameters;
import com.comptel.bst.tools.diff.parser.entity.bst.OutputParameters;
import com.comptel.bst.tools.diff.parser.entity.generic.Conversible;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.parser.entity.generic.Tag;
import com.comptel.bst.tools.diff.parser.entity.jaxb.JAXBParameter;
import com.comptel.bst.tools.diff.parser.entity.jaxb.step.JAXBStep;

public class Step extends Activity implements Conversible<JAXBStep> {

    private static final long serialVersionUID = 1L;

    public static final String TAG_NAME = "step";

    public static final Tag TAG = Tag.identifiable(TAG_NAME, "fileName");

    public Step() {
        super(TAG);
    }

    public Step(JAXBStep step) {
        this();
        this.convert(step);
    }

    public Step(String fileName, JAXBStep step) {
        this(step);
        this.setId(fileName);
    }

    @Override
    public void convert(JAXBStep obj) {
        this.addElement(Element.value(CLASS_TAG, obj.getClazz()));
        this.addElement(Element.value(ACTIVITY_DURATION_TAG, obj.getActivityDuration()));
        this.addElement(Element.value(ACTIVITY_NAME_TAG, obj.getActivityName()));
        this.addElement(Element.value(DESCRIPTION_TAG, obj.getDescription()));

        this.addElement(new OutputParameters<JAXBParameter>(obj.getOutput()));
        this.addElement(new InputParameters(obj.getInput()));
    }

}
