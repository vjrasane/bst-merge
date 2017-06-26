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
import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.parser.entity.jaxb.JAXBObject;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "", propOrder = { "exec" })
@XmlRootElement(name = "body")
public class JAXBMethodBody implements JAXBObject {

    protected List<JAXBExec> exec;

    public JAXBMethodBody() {}

    public JAXBMethodBody(Element e) {
        this.convert(e);
    }

    @Override
    public void convert(Element elem) {
        List<Element> nodes = elem.getElements(Node.TAG);
        nodes.sort((n1, n2) -> getPos(n1).compareTo(getPos(n2)));
        nodes.forEach(n -> {
            convertIds(n, nodes);
            this.getExec().add(new JAXBExec(n));
        });
    }

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

    private void convertLinkId(Element source, List<Element> nodes, String jumpTargetId, Consumer<String> setter) {
        if (jumpTargetId != null) {
            int sourceIndex = nodes.indexOf(source);
            Element target = CommonUtils.findFirst(nodes, n -> n.getId().equals(jumpTargetId));

            if (target == null)
                System.out.println("o ou");

            int targetIndex = nodes.indexOf(target);

            int jump = targetIndex - sourceIndex;
            setter.accept(Integer.toString(jump));
        }
    }

    private void convertIds(Element source, List<Element> nodes) {
        Element links = source.findUniqueElement(Links.TAG);
        if (links != null) {
            Element primaryLink = links.findUniqueElement(Links.PRIMARY_LINK_TAG);
            Element secondaryLink = links.findUniqueElement(Links.SECONDARY_LINK_TAG);

            String firstLinkId = CommonUtils.nullSafeApply(primaryLink, e -> e.getValue());
            String secondLinkId = CommonUtils.nullSafeApply(secondaryLink, e -> e.getValue());

            convertLinkId(source, nodes, firstLinkId, s -> primaryLink.setValue(s));
            convertLinkId(source, nodes, secondLinkId, s -> secondaryLink.setValue(s));

            links.getElements(Jump.TAG).forEach(l -> convertLinkId(source, nodes, l.getValue(), s -> l.setValue(s)));
        }
    }

}
