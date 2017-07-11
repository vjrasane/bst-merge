package com.comptel.bst.tools.diff.parser.entity.bst;

import com.comptel.bst.tools.diff.parser.entity.generic.Attribute;
import com.comptel.bst.tools.diff.parser.entity.generic.Conversible;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.parser.entity.generic.Tag;
import com.comptel.bst.tools.diff.parser.entity.jaxb.JAXBParameter;

/*
 * Generic parameter object that can be used in the place of most parameters
 * in the BST logics except the node output parameters due to their different tag.
 */
public class Parameter extends Element implements Conversible<JAXBParameter> {

    private static final long serialVersionUID = 1L;

    // Parameters are identified based on their name
    public static final String ID_ATTR = "name";

    public static final Tag TAG = Tag.identifiable("parameter", ID_ATTR);
    public static final String OVERWRITE_ATTR = "overwrite"; // The only other attribute in parameters

    public Parameter(JAXBParameter p) {
        this(TAG, p);
    }

    public Parameter(Tag tag, JAXBParameter p) {
        this(tag);
        this.convert(p);
    }

    public Parameter(Tag tag) {
        super(Element.Type.VALUE, tag);
    }

    public Parameter(Tag tag, String id, String value) {
        super(Element.Type.VALUE, tag, value);
        this.setId(id);
    }

    public Parameter(Tag tag, String id, String value, Attribute... attributes) {
        super(Element.Type.VALUE, tag, value, attributes);
        this.setId(id);
    }

    // Does the conversion from XML object to internal element object
    @Override
    public void convert(JAXBParameter obj) {
        this.setId(obj.getName());
        this.setValue(obj.getvalue());
        this.addAttr(OVERWRITE_ATTR, obj.getOverwrite());
    }

    @Override
    public String getId() {
        return this.getAttributeId(ID_ATTR);
    }

    @Override
    public void setId(String id) {
        this.setAttr(ID_ATTR, id);
    }

    // Used in some output messages that require a more detailed string with both the name and value
    @Override
    public String toExpandedString() {
        return super.toSimpleString() + "='" + this.getValue() + "'";
    }

}
