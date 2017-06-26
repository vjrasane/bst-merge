package com.comptel.bst.tools.diff.parser.entity.generic;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import com.comptel.bst.tools.common.CommonUtils;
import com.comptel.bst.tools.diff.comparison.differences.Difference;
import com.comptel.bst.tools.diff.utils.DiffUtils;

public class Element implements Serializable, Comparable<Element> {

    private static final long serialVersionUID = 1L;

    private final Tag tag;

    private final Type type;
    private String value;

    private Map<Tag, List<Element>> elementsByTag = new HashMap<Tag, List<Element>>();
    private Map<String, String> attributes = new HashMap<String, String>();
    private Map<String, String> transientData = new HashMap<String, String>();

    private Element parent;

    public static Element emptyCopy(Element elem) {
        return new Element(elem.type, elem.tag);
    }

    public static Element group(Tag tag) {
        return new Element(tag);
    }

    public static Element value(Tag tag, String value) {
        return new Element(tag, value);
    }

    protected Element(Tag tag) {
        this(Element.Type.GROUP, tag);
    }

    protected Element(Tag tag, String value) {
        this(Element.Type.VALUE, tag);
        this.value = value;
    }

    protected Element(Type type, Tag tag) {
        this.tag = tag;
        this.type = type;
    }

    protected Element(Type type, Tag tag, Attribute... attributes) {
        this(type, tag);
        Arrays.stream(attributes).forEach(a -> this.addAttr(a));
    }

    protected Element(Type type, Tag tag, String value, Attribute... attributes) {
        this(type, tag, attributes);
        this.value = value;
    }

    public Element attr(String key, String value) {
        this.addAttr(key, value);
        return this;
    }

    public void addAttr(Attribute attr) {
        this.addAttr(attr.getName(), attr.getValue());
    }

    public void addAttr(String name, String value) {
        this.attributes.put(name, value);
    }

    public void setData(String name, String value) {
        this.transientData.put(name, value);
    }

    public String getData(String name) {
        return this.transientData.get(name);
    }

    /*
     * Adds an element to the map of child elements. Checks whether the added element is unique, or if the same element already exists
     * (based on ID or equality). If the element exists, assume the merge can be done (since its checked when difference conflicts are
     * detected) and merge the two.
     */
    public void addElement(Element elem) {
        if (!this.type.container)
            throw new UnsupportedOperationException("Cannot add an element to an element of type " + this.type);
        if (elem == null)
            throw new IllegalStateException("Attempting to add a null element to " + this);

        List<Element> elems = elementsByTag.get(elem.getTag());
        Element existing = null;
        if (elems == null) {
            // No elements with such tag exist
            elems = new ArrayList<Element>();
            elementsByTag.put(elem.getTag(), elems);
        } else if (elems.size() > 0) {
            // Elements exist, check if they conflict with the added element
            existing = CommonUtils.findFirst(elems, e -> e.matches(elem));
        }

        // If a matching element exists, attempt to merge it with the given element
        if (existing != null)
            existing.merge(elem);
        else {
            elems.add(elem);
            Collections.sort(elems);
        }
        elem.setParent(this);
    }

    public List<Difference> compare(Element context, Element other) {
        if (!this.type.equals(other.type))
            throw new IllegalStateException("Cannot compare elements of type" + this.type + " and " + other.type);
        if (!CommonUtils.nullSafeEquals(this.tag, other.tag))
            throw new IllegalStateException("Cannot compare elements " + this.tag + " and " + other.tag);

        List<Difference> diffs = new ArrayList<Difference>();

        if (this.type.container)
            diffs.addAll(DiffUtils.compareChildElements(this, this.getElementsByTag(), other.getElementsByTag()));
        if (this.type.valued)
            diffs.addAll(DiffUtils.compareValues(this, other));
        diffs.addAll(DiffUtils.compareAttributes(this, other));
        return diffs;
    }

    @Override
    public int compareTo(Element o) {
        if (!this.tag.equals(o.tag))
            return this.tag.getName().compareTo(o.getTag().getName());
        if (this.tag.isIdentifiable())
            return this.getId().compareTo(o.getId());
        if (this.type.valued && this.value != null)
            return this.value.compareTo(o.getValue());
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Element other = (Element) obj;
        if (attributes == null) {
            if (other.attributes != null)
                return false;
        } else if (!attributes.equals(other.attributes))
            return false;
        if (elementsByTag == null) {
            if (other.elementsByTag != null)
                return false;
        } else if (!elementsByTag.equals(other.elementsByTag))
            return false;
        if (tag == null) {
            if (other.tag != null)
                return false;
        } else if (!tag.equals(other.tag))
            return false;
        if (type != other.type)
            return false;
        if (value == null) {
            if (other.value != null)
                return false;
        } else if (!value.equals(other.value))
            return false;
        return true;
    }

    public Element findIdElement(Tag tag, String id) {
        Collection<Element> elems = this.elementsByTag.get(tag);
        if (elems != null) {
            List<Element> matching = elems.stream().filter(e -> e.getId().equals(id)).collect(Collectors.toList());
            if (matching.size() > 1)
                throw new IllegalStateException("More than one instance of an element with ID '" + id + "' found in " + this);
            return matching.size() > 0 ? matching.get(0) : null;
        }
        return null;
    }

    public Element findUniqueElement(Tag tag) {
        Collection<Element> elems = this.elementsByTag.get(tag);
        return DiffUtils.getUniqueElem(tag, elems);
    }

    public String getAttr(String id) {
        return this.attributes.get(id);
    }

    public Map<String, String> getAttributes() {
        return attributes;
    }

    public List<Element> getElements(Tag tag) {
        List<Element> elems = this.elementsByTag.get(tag);
        return elems != null ? elems : Collections.emptyList();
    }

    public Map<Tag, List<Element>> getElementsByTag() {
        return elementsByTag;
    }

    public String getId() {
        return this.tag.getId(this);
    }

    public Element getParent() {
        return parent;
    }

    public Tag getTag() {
        return tag;
    }

    public Type getType() {
        return type;
    }

    public String getValue() {
        if (!this.type.valued)
            throw new UnsupportedOperationException("Cannot read value from an elment of type " + this.type);
        return value;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((attributes == null) ? 0 : attributes.hashCode());
        result = prime * result + ((elementsByTag == null) ? 0 : elementsByTag.hashCode());
        result = prime * result + ((tag == null) ? 0 : tag.hashCode());
        result = prime * result + ((type == null) ? 0 : type.hashCode());
        result = prime * result + ((value == null) ? 0 : value.hashCode());
        return result;
    }

    /**
     * Checks whether the element is the same as the other, based on the tag identifiers, uniqueness and finally equality.
     * 
     * @param other
     * @return boolean
     */
    public boolean isSame(Element other) {
        if (!this.tag.equals(other.tag))
            return false;
        if (this.tag.isIdentifiable())
            return CommonUtils.nullSafeEquals(this.getId(), other.getId());
        if (this.tag.isUnique())
            return true;
        return this.equals(other);
    }

    public boolean isMovable(Element target) {
        return false;
    }

    public boolean matches(Element o) {
        if (!this.tag.equals(o.tag))
            return false;
        if (DiffUtils.nullSafeMatches(this.parent, o.parent)) {
            return this.isSame(o);
        }
        return false;
    }

    public void removeAttr(Attribute attr) {
        this.removeAttr(attr.getName());
    }

    public void removeAttr(String id) {
        this.attributes.remove(id);
    }

    public boolean isHidden() {
        return this.tag.isHidden();
    }

    public void removeElement(Element elem) {
        if (!this.type.container)
            throw new UnsupportedOperationException("Cannot remove an element from an element of type " + this.type);
        Collection<Element> coll = this.elementsByTag.get(elem.getTag());
        Element toBeRemoved = CommonUtils.findFirst(coll, e -> e.matches(elem));
        if (toBeRemoved != null) {
            coll.remove(toBeRemoved);
            if (coll.isEmpty())
                this.elementsByTag.remove(elem.getTag());
            elem.setParent(null);
        }
    }

    public void setAttr(Attribute attr) {
        this.setAttr(attr.getName(), attr.getValue());
    }

    public void setAttr(String id, String value) {
        if (value == null)
            this.attributes.remove(id);
        else
            this.attributes.put(id, value);
    }

    public void setAttributes(Map<String, String> attributes) {
        this.attributes = attributes;
    }

    public void setElementsByTag(Map<Tag, List<Element>> elementsByTag) {
        this.elementsByTag = elementsByTag;
    }

    public void setId(String id) {
        this.tag.setId(id, this);
    }

    public void setParent(Element parent) {
        this.parent = parent;
    }

    public String getOutputValue() {
        return this.value;
    }

    public void setValue(String value) {
        if (!this.type.valued)
            throw new UnsupportedOperationException("Cannot set value to an elment of type " + this.type);
        this.value = value;
    }

    public String toSimpleString() {
        return this.tag.toSimpleString(this);
    }

    public String toExpandedString() {
        return this.toSimpleString();
    }

    @Override
    public String toString() {
        return this.tag.toString() + "={ " + CommonUtils.mapToString(this.attributes) + this.type.toString(this) + " }"; // "<" + this.getTag()
        // + Utils.mapToString(this.attributes) + (this.value != null ? ">" + this.value + "</>" : "/>");
    }

    /*
     * Merges the contents of two elements, assuming there are no differences in the values or attributes between the two
     */
    private void merge(Element elem) {
        for (Tag tag : elem.getElementsByTag().keySet()) {
            elem.elementsByTag.get(tag).forEach(e -> this.addElement(e));
        }
    }

    public static enum Type {
        GROUP(false, true) {
            @Override
            public String toString(Element e) {
                return CommonUtils.collectionToString(CommonUtils.combineCategories(e.elementsByTag), true);
            }
        },
        VALUE(true, false) {
            @Override
            public String toString(Element e) {
                return "value=" + e.value;
            }
        },
        FLAG(false, false) {
            @Override
            public String toString(Element element) {
                return "";
            }
        };

        public final boolean valued;
        public final boolean container;

        private Type(boolean valued, boolean container) {
            this.valued = valued;
            this.container = container;
        }

        public abstract String toString(Element element);
    }

    public Element findGenericElement(Element elem) {
        return this.elementsByTag.get(elem.tag).stream().filter(e -> e.equals(elem)).findFirst().orElse(null);
    }

    public boolean isIdentifiable() {
        return this.tag.isIdentifiable();
    }

    public boolean isUnique() {
        return this.tag.isUnique();
    }

    public boolean isAncestor(Element other) {
        return other.hasAncestor(this);
    }

    public List<Element> getPath(boolean reverse) {
        return DiffUtils.getElementPath(this, reverse);
    }

    public boolean hasAncestor(Element other) {
        Element current = this;
        do {
            if (current.matches(other))
                return true;
        } while ((current = current.getParent()) != null);
        return false;
    }

    public static Element flag(Tag tag) {
        return new Element(Type.FLAG, tag);
    }

    public Element id(String id) {
        this.setId(id);
        return this;
    }

    public List<Tag> getTagPath() {
        return DiffUtils.getTagPath(this);
    }

    public List<Tag> getTagPath(boolean reverse) {
        return DiffUtils.getTagPath(this, reverse);
    }

    public Map<String, String> getTransientData() {
        return transientData;
    }

    public void setTransientData(Map<String, String> transientData) {
        this.transientData = transientData;
    }

    public void removeSelf() {
        this.parent.removeElement(this);
    }

}
