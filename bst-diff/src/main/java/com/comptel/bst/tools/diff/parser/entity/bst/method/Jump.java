package com.comptel.bst.tools.diff.parser.entity.bst.method;

import com.comptel.bst.tools.diff.parser.entity.generic.Conversible;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.parser.entity.generic.Tag;
import com.comptel.bst.tools.diff.parser.entity.jaxb.method.JAXBJump;
import com.comptel.bst.tools.diff.utils.DiffConstants;


public class Jump extends Element implements Conversible<JAXBJump> {

    public static final String INDEX_ATTR = DiffConstants.ATTR_PREFIX +":index";

    public static final String TARGET_NAME_DATA = DiffConstants.DATA_PREFIX + ":target-name";

    public static final Tag TAG = Tag.identifiable("jump", INDEX_ATTR);

    private static final long serialVersionUID = 1L;

    public Jump() {
        super(Element.Type.VALUE, TAG);
    }

    public Jump(JAXBJump obj) {
        this();
        this.convert(obj);
    }

    @Override
    public void convert(JAXBJump obj) {
        this.setId(obj.getJumpPos());
        this.setValue(obj.getJumpId());

        this.setTargetName(obj.getTargetName());
    }

    private void setTargetName(String targetName) {
        this.setData(TARGET_NAME_DATA, targetName);
    }

    @Override
    public String getId() {
        return this.getAttributes().get(INDEX_ATTR);
    }

    @Override
    public void setId(String id) {
        this.setAttr(INDEX_ATTR, id);
    }

    @Override
    public String getOutputValue() {
        return this.getTargetName();
    }

    private String getTargetName() {
        return this.getData(TARGET_NAME_DATA);
    }

}
