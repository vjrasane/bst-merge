package com.comptel.bst.tools.diff.parser.entity.bst;

import java.util.List;
import java.util.function.Function;

import com.comptel.bst.tools.diff.parser.entity.generic.Tag;
import com.comptel.bst.tools.diff.parser.entity.jaxb.JAXBOutputParameters;
import com.comptel.bst.tools.diff.parser.entity.jaxb.JAXBParameter;

/*
 * Output parameter container. Otherwise could be replaced by the generic ParameterGroup,
 * but it can have 'all' attribute which would need to be handled separately. Also allows
 * some output message manipulation.
 */
public class OutputParameters<P extends JAXBParameter> extends ParameterGroup<P, JAXBOutputParameters> {

    private static final long serialVersionUID = 1L;

    // Parameter groups are unique in the context they appear in
    public static final Tag TAG = Tag.unique("output");
    public static final String ALL_ATTR = "all"; // Sole attribute that can only occur in output parameters

    public OutputParameters() {
        super(TAG);
    }

    public OutputParameters(JAXBOutputParameters output) {
        super(TAG, output);
    }

    public OutputParameters(List<P> params, Function<P, Parameter> converter) {
        super(TAG, params, converter);
    }

    @Override
    public void convert(JAXBOutputParameters obj) {
        super.convert(obj);
        this.addAttr(ALL_ATTR, obj.getAll());
    }

    // Show a more user friendly name in output messages
    @Override
    public String toSimpleString() {
        return "output parameters";
    }
}
