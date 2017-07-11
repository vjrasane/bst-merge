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

/*
 * Internal representation of the flowchart nodes
 */
public class Node extends Element implements Conversible<JAXBExec> {

    public static final String ID_ATTR = "id"; // Identifier attribute

    // Position data attribute that is used by the algorithm to preserve the node positions as far as possible
    public static final String POS_DATA = DiffConstants.DATA_PREFIX + ":position";

    public static final String NAME_ATTR = "name"; // Name attribute. This is what users see in the UI

    public static final Tag TAG = Tag.identifiable("exec", ID_ATTR); // Identifiable element based on 'id' attribute

    // Generic element containing a string representation. Unrestricted occurrences.
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

    // Does the conversion from XML object to internal element object
    @Override
    public void convert(JAXBExec obj) {
        // Set the non-connector attributes
        this.setId(obj.getId());
        this.setName(obj.getName());
        this.setPosition(obj.getPosition());

        /*
         *  Create the container for connectors.
         *  Note that the primary and secondary connector attributes are also
         *  stored as elements rather than attributes. This makes no difference
         *  for the user, but is more convenient for the processing of the links.
         */
        this.addElement(new Links(
                obj.getPrimaryJumpId(),
                obj.getPrimaryTargetName(),
                obj.getSecondaryJumpId(),
                obj.getSecondaryTargetName(),
                obj.getJump()));

        // Take the node references that were set in method body conversion
        this.addElement(new References(obj.getReferences()));

        // Add output and input parameters
        this.addElement(new OutputParameters<JAXBNodeOutputParameter>(obj.getOutParameter(), p -> new NodeOutputParameter(p)));
        this.addElement(new InputParameters(obj.getParameter(), p -> new Parameter(p)));

        // Instance descriptions can just be dumped here, they do not need specific handling
        obj.getInstanceDescription().stream().forEach(d -> this.addElement(Element.value(INSTANCE_DESCRIPTION_TAG, d.getvalue())));
    }

    private void setPosition(String position) {
        this.setData(POS_DATA, position);
    }

    @Override
    public String getId() {
        return DiffUtils.getAttributeId(this, ID_ATTR);
    }

    public String getName() {
        return this.getAttr(NAME_ATTR);
    }

    // Override the default simple string so that the output displays nodes as 'steps'. Go figure.
    @Override
    public String toSimpleString() {
        return "step " + this.getAttr(Node.NAME_ATTR);
    }

    @Override
    public void setId(String id) {
        this.setAttr(ID_ATTR, id);
    }

    public void setName(String name) {
        this.addAttr(NAME_ATTR, name);
    }

    /*
     * A selection of convenience methods
     */

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
        return addLink(StandardJump.PRIMARY_LINK_TAG, node);
    }

    public Element setSecondaryLink(Node node) {
        return addLink(StandardJump.SECONDARY_LINK_TAG, node);
    }

    public Element addAdditionalLink(String linkId, Node node) {
        return addLink(Jump.TAG, linkId, node);
    }

    public Element addReference(String nodeId, String sourceName) {
        Element refs = this.findUniqueElement(References.TAG);
        // Make sure that the reference container exists
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
        return this.getLinks().findUniqueElement(StandardJump.PRIMARY_LINK_TAG);
    }

    public Element getSecondaryLink() {
        return this.getLinks().findUniqueElement(StandardJump.SECONDARY_LINK_TAG);
    }

}
