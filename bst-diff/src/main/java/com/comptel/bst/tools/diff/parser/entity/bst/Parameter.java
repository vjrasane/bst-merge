package com.comptel.bst.tools.diff.parser.entity.bst;

import java.util.List;

import org.apache.commons.io.FilenameUtils;

import com.comptel.bst.tools.common.CommonUtils;
import com.comptel.bst.tools.diff.parser.entity.bst.method.Node;
import com.comptel.bst.tools.diff.parser.entity.bst.step.Step;
import com.comptel.bst.tools.diff.parser.entity.generic.Attribute;
import com.comptel.bst.tools.diff.parser.entity.generic.Conversible;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.parser.entity.generic.Tag;
import com.comptel.bst.tools.diff.parser.entity.jaxb.JAXBParameter;
import com.comptel.bst.tools.diff.utils.DiffUtils;

public class Parameter extends Element implements Conversible<JAXBParameter> {

    private static final long serialVersionUID = 1L;

    public static final String ID_ATTR = "name";

    public static final Tag TAG = Tag.identifiable("parameter", ID_ATTR);
    public static final String OVERWRITE_ATTR = "overwrite";

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

    @Override
    public void convert(JAXBParameter obj) {
        this.setId(obj.getName());
        this.setValue(obj.getvalue());
        this.addAttr(OVERWRITE_ATTR, obj.getOverwrite());
    }

    @Override
    public String getId() {
        return DiffUtils.getAttributeId(this, ID_ATTR);
    }

    @Override
    public void setId(String id) {
        this.setAttr(ID_ATTR, id);
    }

    @Override
    public boolean isMovable(Element target) {
        List<Element> targetPath = target.getPath(false);
        List<Element> currentPath = this.getPath(false);

        // Both paths must have at least two container elements (currentPath contains this element as well)
        if (currentPath.size() < 3 || targetPath.size() < 2)
            return false;

        Tag parentTag = currentPath.get(1).getTag();
        Tag targetTag = targetPath.get(0).getTag();
        // Output parameters can only be moved to output parameter groups and inputs to input group
        if (!parentTag.equals(targetTag))
            return false;

        Element currentContainer = currentPath.get(2);
        Element targetContainer = targetPath.get(1);

        Element node = CommonUtils.getFirstMatch(e -> e.getTag().equals(Node.TAG), currentContainer, targetContainer);
        Element step = CommonUtils.getFirstMatch(e -> e.getTag().equals(Step.TAG), currentContainer, targetContainer);
        if (node == null || step == null)
            return false;
        if (node.getAttr(Node.NAME_ATTR).equals(FilenameUtils.removeExtension(step.getId())))
            return true;

        return false;
    }

    @Override
    public String toExpandedString() {
        return super.toSimpleString() + "='" + this.getValue() + "'";
    }

}
