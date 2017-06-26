package com.comptel.bst.tools.diff.parser.entity.bst;

import java.util.List;
import java.util.function.Function;

import com.comptel.bst.tools.diff.parser.entity.generic.Tag;
import com.comptel.bst.tools.diff.parser.entity.jaxb.JAXBParameter;
import com.comptel.bst.tools.diff.parser.entity.jaxb.JAXBParameterGroup;

public class InputParameters extends ParameterGroup<JAXBParameter, JAXBParameterGroup> {

    private static final long serialVersionUID = 1L;

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

    @Override
    public String toSimpleString() {
        return "input parameters";
    }
}
