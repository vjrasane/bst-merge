package com.comptel.bst.tools.diff.parser.entity.bst.method;

import com.comptel.bst.tools.diff.parser.entity.generic.Conversible;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.parser.entity.generic.Tag;
import com.comptel.bst.tools.diff.parser.entity.jaxb.method.JAXBJump;
import com.comptel.bst.tools.diff.utils.DiffConstants;

/*
 * Connector element present in branching nodes
 */
public class Jump extends Element implements Conversible<JAXBJump> {

    // Uses the index of the connector in the list of jump elements as identifier (not present in the BST logic)
    public static final String INDEX_ATTR = DiffConstants.ATTR_PREFIX +":index";

    // Name of the referenced node. Used for the output messages
    public static final String TARGET_NAME_DATA = DiffConstants.DATA_PREFIX + ":target-name";

    // Jumps are considered identifiable because they are stored in an ordered list
    public static final Tag TAG = Tag.identifiable("jump", INDEX_ATTR);

    private static final long serialVersionUID = 1L;

    public Jump() {
        super(Element.Type.VALUE, TAG);
    }

    public Jump(JAXBJump obj) {
        this();
        this.convert(obj);
    }

    // Takes the XML representation of the connector element and converts it to the internal element
    @Override
    public void convert(JAXBJump obj) {
        this.setId(obj.getJumpPos());
        // The value is set as the identifier of the referenced node rather than the relative index like within the BST logics
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

    // In the output messages the value should be shown as the name of the target element rather than its ID
    @Override
    public String getOutputValue() {
        return this.getTargetName();
    }

    private String getTargetName() {
        return this.getData(TARGET_NAME_DATA);
    }

}
