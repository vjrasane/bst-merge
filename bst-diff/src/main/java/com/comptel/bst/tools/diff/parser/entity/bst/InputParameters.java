package com.comptel.bst.tools.diff.parser.entity.bst;

import java.util.List;
import java.util.function.Function;

import com.comptel.bst.tools.diff.parser.entity.generic.Tag;
import com.comptel.bst.tools.diff.parser.entity.jaxb.JAXBParameter;
import com.comptel.bst.tools.diff.parser.entity.jaxb.JAXBParameterGroup;

/*
 * Input parameter container. It might be possible
 * to simply use a parameter group object, but having this as
 * a separate subclass allows customizing the output
 */
public class InputParameters extends ParameterGroup<JAXBParameter, JAXBParameterGroup> {

    private static final long serialVersionUID = 1L;

    // Parameter groups are unique in whichever context they appear in.
    public static final Tag TAG = Tag.unique("input");

    public InputParameters() {
        super(TAG);
    }

    public InputParameters(JAXBParameterGroup input) {
        super(TAG, input);
    }

    public InputParameters(List<JAXBParameter> params, Function<JAXBParameter, Parameter> converter) {
        super(TAG, params, converter);
    }

    // Show a little more user friendly name in the output message
    @Override
    public String toSimpleString() {
        return "input parameters";
    }
}
