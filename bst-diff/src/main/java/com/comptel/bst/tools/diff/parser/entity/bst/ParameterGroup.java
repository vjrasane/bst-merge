package com.comptel.bst.tools.diff.parser.entity.bst;

import java.util.List;
import java.util.function.Function;

import com.comptel.bst.tools.diff.parser.entity.generic.Conversible;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.parser.entity.generic.Tag;
import com.comptel.bst.tools.diff.parser.entity.jaxb.JAXBParameter;
import com.comptel.bst.tools.diff.parser.entity.jaxb.JAXBParameterGroup;
/*
 * General class for the parameter containers. Most of the
 * functionality is shared between them so it is placed here, but
 * due to some minor details the actual groups have to be represented
 * as their own subclasses.
 */
public class ParameterGroup<P extends JAXBParameter, G extends JAXBParameterGroup> extends Element implements Conversible<G> {

    private static final long serialVersionUID = 1L;

    public ParameterGroup(Tag tag) {
        super(Element.Type.GROUP, tag);
    }

    public ParameterGroup(Tag tag, List<P> params, Function<P, Parameter> converter) {
        super(Element.Type.GROUP, tag);
        params.forEach(p -> this.addElement(converter.apply(p)));
    }

    public ParameterGroup(Tag tag, G params) {
        this(tag);
        this.convert(params);
    }

    // Converts the given XML object to internal element object
    @Override
    public void convert(G obj) {
        // Convert each parameter as they are added
        obj.getParameter().forEach(p -> this.addElement(new Parameter(p)));
    }

}
