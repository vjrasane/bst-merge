package com.comptel.bst.tools.diff.parser.entity.bst.method;

import com.comptel.bst.tools.diff.parser.entity.bst.InputParameters;
import com.comptel.bst.tools.diff.parser.entity.bst.OutputParameters;
import com.comptel.bst.tools.diff.parser.entity.bst.Parameter;
import com.comptel.bst.tools.diff.parser.entity.generic.Conversible;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.parser.entity.generic.Reference;
import com.comptel.bst.tools.diff.parser.entity.generic.References;
import com.comptel.bst.tools.diff.parser.entity.generic.Tag;
import com.comptel.bst.tools.diff.parser.entity.jaxb.method.JAXBExec;
import com.comptel.bst.tools.diff.parser.entity.jaxb.method.JAXBNodeOutputParameter;
import com.comptel.bst.tools.diff.utils.DiffConstants;
import com.comptel.bst.tools.diff.utils.DiffUtils;


public class Node extends Element implements Conversible<JAXBExec> {

    public static final String ID_ATTR = "id";
    public static final String POS_DATA = DiffConstants.DATA_PREFIX + ":position";

    public static final String NAME_ATTR = "name";
    public static final Tag TAG = Tag.identifiable("exec", ID_ATTR);
    public static final String FIRST_JUMP_ATTR = "jump0";
    public static final String SECOND_JUMP_ATTR = "jump1";
    public static final Tag INSTANCE_DESCRIPTION_TAG = Tag.generic("instance_description");

    private static final long serialVersionUID = 1L;

    public Node() {
        super(TAG);
    }

    public Node(JAXBExec ex) {
        this();
        this.convert(ex);
    }

    public Node(String id, String name) {
        this();
        this.setId(id);
        this.setName(name);

        this.addElement(new Links());
        this.addElement(new References());
    }

    @Override
    public void convert(JAXBExec obj) {
        this.setId(obj.getId());
        this.setName(obj.getName());
        this.setPosition(obj.getPosition());

        this.addElement(new Links(
                obj.getPrimaryJumpId(),
                obj.getPrimaryTargetName(),
                obj.getSecondaryJumpId(),
                obj.getSecondaryTargetName(),
                obj.getJump()));

        this.addElement(new References(obj.getReferences()));

        this.addElement(new OutputParameters<JAXBNodeOutputParameter>(obj.getOutParameter(), p -> new NodeOutputParameter(p)));
        this.addElement(new InputParameters(obj.getParameter(), p -> new Parameter(p)));

        obj.getInstanceDescription().stream().forEach(d -> this.addElement(Element.value(INSTANCE_DESCRIPTION_TAG, d.getvalue())));
    }

    private void setPosition(String position) {
        this.setData(POS_DATA, position);
    }

    public String getFirstLink() {
        return this.getAttr(FIRST_JUMP_ATTR);
    }

    @Override
    public String getId() {
        return DiffUtils.getAttributeId(this, ID_ATTR);
    }

    public String getName() {
        return this.getAttr(NAME_ATTR);
    }

    @Override
    public String toSimpleString() {
        return "step " + this.getAttr(Node.NAME_ATTR);
    }

    public String getSecondLink() {
        return this.getAttr(SECOND_JUMP_ATTR);
    }

    @Override
    public void setId(String id) {
        this.setAttr(ID_ATTR, id);
    }

    public void setName(String name) {
        this.addAttr(NAME_ATTR, name);
    }

    public Element addLink(Tag tag, String id, Node node) {
        Element link = Element.value(tag, node.getId()).id(id);
        return addLink(link, node);
    }

    public Element addLink(Tag tag, Node node) {
        Element link = Element.value(tag, node.getId());
        return addLink(link, node);
    }

    public Element addLink(Element link, Node node) {
        Element links = this.findUniqueElement(Links.TAG);
        if(links == null) {
            links = new Links();
            this.addElement(links);
        }
        links.addElement(link);
        return node.addReference(this.getId(), this.getName());
    }

    public Element setPrimaryLink(Node node) {
        return addLink(Links.PRIMARY_LINK_TAG, node);
    }

    public Element setSecondaryLink(Node node) {
        return addLink(Links.SECONDARY_LINK_TAG, node);
    }

    public Element addAdditionalLink(String linkId, Node node) {
        return addLink(Jump.TAG, linkId, node);
    }

    public Element addReference(String nodeId, String sourceName) {
        Element refs = this.findUniqueElement(References.TAG);
        if(refs == null) {
            refs = new References();
            this.addElement(refs);
        }
        Reference ref = new Reference(nodeId, sourceName);
        refs.addElement(ref);
        return ref;
    }

    public Element getReferences() {
        return this.findUniqueElement(References.TAG);
    }

    public Element getLinks() {
        return this.findUniqueElement(Links.TAG);
    }

    public Element getPrimaryLink() {
        return this.getLinks().findUniqueElement(Links.PRIMARY_LINK_TAG);
    }

    public Element getSecondaryLink() {
        return this.getLinks().findUniqueElement(Links.SECONDARY_LINK_TAG);
    }


}
