package com.comptel.bst.tools.diff.parser.entity.bst.method;

import java.util.List;

import com.comptel.bst.tools.diff.parser.entity.bst.Activity;
import com.comptel.bst.tools.diff.parser.entity.bst.InputParameters;
import com.comptel.bst.tools.diff.parser.entity.bst.OutputParameters;
import com.comptel.bst.tools.diff.parser.entity.generic.Conversible;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.parser.entity.generic.Tag;
import com.comptel.bst.tools.diff.parser.entity.jaxb.JAXBParameter;
import com.comptel.bst.tools.diff.parser.entity.jaxb.method.JAXBMethod;

/*
 * BST method element containing the entire flowchart representation
 */
public class Method extends Activity implements Conversible<JAXBMethod> {

    private static final long serialVersionUID = 1L;

    public static final String TAG_NAME = "method"; // Referenced when the method is parsed back into XML
    public static final Tag TAG = Tag.identifiable(TAG_NAME, "fileName"); // The method is identified by its filename

    // Some unique data elements
    public static final Tag MAINLINE_ENABLED = Tag.unique("mainline_enabled");
    public static final Tag CATALOG_DRIVEN = Tag.unique("catalog_driven");

    // Method element attributes
    public static final String PARALLEL_ATTR = "parallel";
    public static final String PARENT_ID_ATTR = "parentId";

    public Method() {
        super(TAG);
    }

    public Method(JAXBMethod method) {
        this();
        this.convert(method);
    }

    public Method(String fileName, JAXBMethod method) {
        this(method);
        this.setId(fileName);
    }

    public void addNode(Node current) {
        /*
         *  Make sure the method has a body before adding the node.
         *  This should never occur in real-world situations, but some tests
         *  do not add method bodies automatically
         */
        if (this.getBody() == null)
            this.addElement(new MethodBody());
        this.getBody().addElement(current);
    }

    // Does the conversion from XML object to internal element object
    @Override
    public void convert(JAXBMethod obj) {
        /*
         *  Set the unique data elements
         *  Note that many of these are defined in the Activity super
         *  class since they are common for methods and steps
         */
        this.addElement(Element.value(ACTIVITY_DURATION_TAG, obj.getActivityDuration()));
        this.addElement(Element.value(ACTIVITY_NAME_TAG, obj.getActivityName()));
        this.addElement(Element.value(CLASS_TAG, obj.getClazz()));
        this.addElement(Element.value(DESCRIPTION_TAG, obj.getDescription()));
        this.addElement(Element.value(MAINLINE_ENABLED, obj.getMainlineEnabled()));
        this.addElement(Element.value(CATALOG_DRIVEN, obj.getCatalogDriven()));

        //  Add output and input parameter groups, recursively converting the parameters as well.
        this.addElement(new OutputParameters<JAXBParameter>(obj.getOutput()));
        this.addElement(new InputParameters(obj.getInput()));

        // Add the method body. The nodes of the flowchart are recursively converted at the same time.
        this.addElement(new MethodBody(obj.getBody()));

        // Set the element attributes
        this.addAttr(PARALLEL_ATTR, obj.getParallel());
        this.addAttr(PARENT_ID_ATTR, obj.getParentId());
    }

    // Convenience method for retrieving the method body
    public Element getBody() {
        return this.findUniqueElement(MethodBody.TAG);
    }

    // Convenience method for retrieving the list of contained nodes
    public List<Element> getNodes() {
        return this.getBody().getElementsByTag().get(Node.TAG);
    }

}
