package com.comptel.bst.tools.diff.parser.entity.jaxb.method;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.XmlType;

import com.comptel.bst.tools.common.CommonUtils;
import com.comptel.bst.tools.diff.parser.entity.bst.method.Jump;
import com.comptel.bst.tools.diff.parser.entity.bst.method.Links;
import com.comptel.bst.tools.diff.parser.entity.bst.method.Node;
import com.comptel.bst.tools.diff.parser.entity.bst.method.StandardJump;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.parser.entity.jaxb.JAXBObject;

/*
 * XML representation of a method body
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "exec" })
@XmlRootElement(name = "body")
public class JAXBMethodBody implements JAXBObject {

    // List of the BST method nodes
    protected List<JAXBExec> exec;

    public JAXBMethodBody() {}

    public JAXBMethodBody(Element e) {
        this.convert(e);
    }

    // Does the conversion from internal element object into XML object
    @Override
    public void convert(Element elem) {
        List<Element> nodes = elem.getElements(Node.TAG);
        // Sort the nodes based on their original position in the lists
        nodes.sort((n1, n2) -> getPos(n1).compareTo(getPos(n2)));
        // Post-process the node references back to relative values
        nodes.forEach(n -> {
            convertIds(n, nodes);
            // Convert the nodes as they are added
            this.getExec().add(new JAXBExec(n));
        });
    }

    /*
     *  Converts a single link identifier to a relative value and sets it with the given setter.
     *  (Would need two separate methods if they were hard coded)
     */
    private void convertLinkId(Element source, List<Element> nodes, String jumpTargetId, Consumer<String> setter) {
        /*
         *  Check that the identifier is not null. This should not occur in
         *  practice but some tests do not set it explicitly
         */
        if (jumpTargetId != null) {
            /*
             *  Get index of the source node in the node list.
             *  Note that it is important to sort the list BEFORE this
             */
            int sourceIndex = nodes.indexOf(source);
            // Find the node with the target identifier
            Element target = CommonUtils.findFirst(nodes, n -> n.getId().equals(jumpTargetId));

            if (target == null)
                // This should absolutely positively never happen
                throw new IllegalStateException("Target node with identifier '"+ jumpTargetId + "' was not found.");

            int targetIndex = nodes.indexOf(target); // Get the new index of the target node

            int jump = targetIndex - sourceIndex; // Get the relative connector value
            setter.accept(Integer.toString(jump)); // Set the value to source
        }
    }

    /*
     * Convert all connectors of a node to the relative values
     * (we can ignore all the transient stuff since its not added to the XML anyway)
     */
    private void convertIds(Element source, List<Element> nodes) {
        Element links = source.findUniqueElement(Links.TAG); // Get the connector container
        if (links != null) { // Should be always present, but some tests dont add it explicitly
            // Primary and secondary connectors
            Element primaryLink = links.findUniqueElement(StandardJump.PRIMARY_LINK_TAG);
            Element secondaryLink = links.findUniqueElement(StandardJump.SECONDARY_LINK_TAG);

            // Get the referenced node identigier
            String firstLinkId = CommonUtils.nullSafeApply(primaryLink, e -> e.getValue());
            String secondLinkId = CommonUtils.nullSafeApply(secondaryLink, e -> e.getValue());

            // Convert the values
            convertLinkId(source, nodes, firstLinkId, s -> primaryLink.setValue(s));
            convertLinkId(source, nodes, secondLinkId, s -> secondaryLink.setValue(s));

            // Do the same for each additional link
            links.getElements(Jump.TAG).forEach(l -> convertLinkId(source, nodes, l.getValue(), s -> l.setValue(s)));
        }
    }

    /*
     * Convenience methods and getters
     */

    private Integer getPos(Element node) {
        return Integer.parseInt(node.getData(Node.POS_DATA));
    }

    public List<JAXBExec> getExec() {
        if (exec == null) {
            exec = new ArrayList<JAXBExec>();
        }
        return this.exec;
    }

    @Override
    public String toString() {
        return exec.stream().map(Object::toString).collect(Collectors.joining(", "));
    }
}
