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


public class Method extends Activity implements Conversible<JAXBMethod> {

    private static final long serialVersionUID = 1L;

    public static final String TAG_NAME = "method";

    public static final Tag TAG = Tag.identifiable(TAG_NAME, "fileName");

    public static final Tag MAINLINE_ENABLED = Tag.unique("mainline_enabled");
    public static final Tag CATALOG_DRIVEN = Tag.unique("catalog_driven");

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
        if (this.getBody() == null)
            this.addElement(new MethodBody());
        this.getBody().addElement(current);
    }

    @Override
    public void convert(JAXBMethod obj) {
        this.addElement(Element.value(ACTIVITY_DURATION_TAG, obj.getActivityDuration()));
        this.addElement(Element.value(ACTIVITY_NAME_TAG, obj.getActivityName()));
        this.addElement(Element.value(CLASS_TAG, obj.getClazz()));
        this.addElement(Element.value(DESCRIPTION_TAG, obj.getDescription()));
        this.addElement(Element.value(MAINLINE_ENABLED, obj.getMainlineEnabled()));
        this.addElement(Element.value(CATALOG_DRIVEN, obj.getCatalogDriven()));

        this.addElement(new OutputParameters<JAXBParameter>(obj.getOutput()));
        this.addElement(new InputParameters(obj.getInput()));
        this.addElement(new MethodBody(obj.getBody()));

        this.addAttr(PARALLEL_ATTR, obj.getParallel());
        this.addAttr(PARENT_ID_ATTR, obj.getParentId());
    }

    public Element getBody() {
        return this.findUniqueElement(MethodBody.TAG);
    }

    public List<Element> getNodes() {
        return this.getBody().getElementsByTag().get(Node.TAG);
    }

}
