package com.comptel.bst.tools.diff;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.junit.Test;

import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.parser.entity.generic.Tag;


public class ElementTest {

    public static String ID_ATTR = "id";
    private static Tag UNIQUE_TAG = Tag.unique("unique");
    public static Tag ID_TAG = Tag.identifiable("id", ID_ATTR);
    private static Tag VALUE_TAG = Tag.generic("value");

    public static Element UNIQUE_ELEM = Element.group(UNIQUE_TAG);

    public static MockElement ID_ELEM = createIdElement("id");
    public static MockElement VALUE_ELEM = createValueElement("value");

    static {
        ID_ELEM.setId(ID_ATTR);
    }

    private static Element createContainer() {
        return Element.group(Tag.unique("container"));
    }

    private static MockElement createGroupElement(Tag tag) {
        return new MockElement(tag);
    }

    private static MockElement createIdElement(String id) {
        MockElement elem = new MockElement(ID_TAG);
        elem.setId(id);
        return elem;
    }

    private static MockElement createValueElement(String value) {
        return new MockElement(VALUE_TAG, value);
    }

    @Test(expected = IllegalStateException.class)
    public void add_element___null() {
        Element container = createContainer();
        container.addElement(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void add_element___to___value() {
        Element value = createValueElement("value");
        value.addElement(createValueElement("value"));
        ;
    }

    @Test
    public void add_generic_element___duplicate() {
        Element container = createContainer();
        assertEquals(0, getSize(container, VALUE_TAG));

        container.addElement(VALUE_ELEM);
        container.addElement(VALUE_ELEM);
        assertEquals(1, getSize(container, VALUE_TAG));
    }

    @Test
    public void add_generic_element___multiple() {
        Element container = createContainer();
        assertEquals(0, getSize(container, VALUE_TAG));
        container.addElement(createValueElement("value1"));
        container.addElement(createValueElement("value2"));
        assertEquals(2, getSize(container, VALUE_TAG));
    }

    @Test
    public void add_generic_element___single() {
        Element container = createContainer();
        assertEquals(0, getSize(container, VALUE_TAG));
        container.addElement(VALUE_ELEM);
        assertEquals(1, getSize(container, VALUE_TAG));
    }

    @Test
    public void add_id_element___duplicate() {
        Element container = createContainer();
        assertEquals(0, getSize(container, ID_TAG));
        container.addElement(ID_ELEM);
        container.addElement(ID_ELEM);
        assertEquals(1, getSize(container, ID_TAG));
    }

    @Test
    public void add_id_element___multiple() {
        Element container = createContainer();
        assertEquals(0, getSize(container, ID_TAG));
        container.addElement(createIdElement("id1"));
        container.addElement(createIdElement("id2"));
        assertEquals(2, getSize(container, ID_TAG));
    }

    @Test
    public void add_id_element___single() {
        Element container = createContainer();
        assertEquals(0, getSize(container, ID_TAG));
        container.addElement(ID_ELEM);
        assertEquals(1, getSize(container, ID_TAG));
    }

    @Test
    public void add_unique_element___duplicate() {
        Element container = createContainer();
        assertEquals(0, getSize(container, UNIQUE_TAG));
        container.addElement(UNIQUE_ELEM);
        container.addElement(UNIQUE_ELEM);
        assertEquals(1, getSize(container, UNIQUE_TAG));
    }

    @Test
    public void add_unique_element___single() {
        Element container = createContainer();
        assertEquals(0, getSize(container, UNIQUE_TAG));
        container.addElement(UNIQUE_ELEM);
        assertEquals(1, getSize(container, UNIQUE_TAG));
    }

    @Test
    public void add_value___empty() {
        Element value = createValueElement("value");
        value.setValue("");
    }

    @Test
    public void add_value___null() {
        Element value = createValueElement("value");
        value.setValue(null);
    }

    @Test(expected = UnsupportedOperationException.class)
    public void add_value___to___group() {
        Element container = createContainer();
        container.setValue("value");
    }

    @Test
    public void attribute_identifier___exists() {
        Element id_elem = createGroupElement(ID_TAG);
        id_elem.setId(ID_ATTR);
        String id = id_elem.getId();
        assertNotNull(id);
        assertEquals(ID_ATTR, id);
    }

    @Test(expected = IllegalStateException.class)
    public void attribute_identifier___missing() {
        Element id_elem = createGroupElement(ID_TAG);
        id_elem.getId();
    }

    @Test(expected = IllegalStateException.class)
    public void compare___tag_mismatch() {
        ID_ELEM.compare(null, UNIQUE_ELEM);
    }

    @Test(expected = IllegalStateException.class)
    public void compare___type_mismatch() {
        ID_ELEM.compare(null, VALUE_ELEM);
    }

    @Test(expected = IllegalStateException.class)
    public void find_id_element___duplicate() {
        Element container = createContainer();
        MockElement elem = createIdElement(ID_ATTR);
        container.getElementsByTag().put(ID_TAG, Arrays.asList(elem, elem));
        container.findIdElement(ID_TAG, ID_ATTR);
    }

    @Test
    public void find_id_element___empty() {
        Element container = createContainer();
        container.getElementsByTag().put(ID_TAG, Collections.emptyList());
        Element elem = container.findIdElement(ID_TAG, ID_ATTR);
        assertEquals(null, elem);
    }

    @Test
    public void find_id_element___missing() {
        Element container = createContainer();
        Element elem = container.findIdElement(ID_TAG, ID_ATTR);
        assertEquals(null, elem);
    }

    @Test
    public void find_id_element___multiple() {
        Element container = createContainer();
        MockElement elem = createIdElement(ID_ATTR);
        container.getElementsByTag().put(ID_TAG, Arrays.asList(elem, createIdElement("id1")));
        Element found = container.findIdElement(ID_TAG, ID_ATTR);
        assertNotNull(found);
        assertEquals(elem, found);
    }

    @Test
    public void find_id_element___single() {
        Element container = createContainer();
        MockElement elem = createIdElement(ID_ATTR);
        container.getElementsByTag().put(ID_TAG, Arrays.asList(elem));
        Element found = container.findIdElement(ID_TAG, ID_ATTR);
        assertNotNull(found);
        assertEquals(elem, found);
    }

    @Test
    public void find_unique_element___empty() {
        Element container = createContainer();
        container.getElementsByTag().put(UNIQUE_TAG, Collections.emptyList());
        Element elem = container.findUniqueElement(UNIQUE_TAG);
        assertEquals(null, elem);
    }

    @Test
    public void find_unique_element___missing() {
        Element container = createContainer();
        Element elem = container.findUniqueElement(UNIQUE_TAG);
        assertEquals(null, elem);
    }

    @Test(expected = IllegalStateException.class)
    public void find_unique_element___multiple() {
        Element container = createContainer();
        container.getElementsByTag().put(UNIQUE_TAG, Arrays.asList(UNIQUE_ELEM, UNIQUE_ELEM));
        container.findUniqueElement(UNIQUE_TAG);
    }

    @Test
    public void find_unique_element___single() {
        Element container = createContainer();
        container.getElementsByTag().put(UNIQUE_TAG, Arrays.asList(UNIQUE_ELEM));
        Element elem = container.findUniqueElement(UNIQUE_TAG);
        assertNotNull(elem);
        assertEquals(UNIQUE_ELEM, elem);
    }

    public void get_value() {
        Element container = createContainer();
        container.getValue();
    }

    @Test(expected = UnsupportedOperationException.class)
    public void get_value___from___group() {
        Element container = createContainer();
        container.getValue();
    }

    private int getSize(Element container, Tag tag) {
        List<Element> elems = container.getElementsByTag().get(tag);
        return elems != null ? elems.size() : 0;
    }

    @SuppressWarnings("serial")
    private static class MockElement extends Element {

        public MockElement(Tag tag) {
            super(tag);
        }

        public MockElement(Tag tag, String value) {
            super(tag, value);
        }

        @Override
        public String getId() {
            return this.getAttributeId(ID_ATTR);
        }

        @Override
        public void setId(String id) {
            this.setAttr(ID_ATTR, id);
        }
    }

}
