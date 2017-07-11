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

/*
 * General element class. Once the conversion of XML into internal objects is complete, all data
 * is stored as these element objects and knowledge about any subclasses is lost. Therefore, the
 * merge algorithm operates entirely by processing these elements and does so based on the information
 * that is stored in them. This information consists of:
 *          - Tag name
 *          - Element type (value, container, hybrid, flag)
 *          - Identifier
 *          - Uniqueness
 *          - Contained elements
 *          - Element attributes
 *          - Element value
 *          - Transient data
 */
public class Element implements Serializable, Comparable<Element> {

    private static final long serialVersionUID = 1L;

    /*
     *  Tag dictates what the name of the element in the XML is and
     *  its occurrence restrictions, which could make it unique, identifiable
     *  or unrestricted
     */
    private final Tag tag;

    // Element type dictates what type of content it can hold: other elements, only string data, both or neither.
    private final Type type;

    private String value; // The possible string data value of the element

    /*
     *  Elements mapped by their tag. This speeds up the matching process as
     *  elements of the same type are retrieved in constant time. This means that unique
     *  elements are matched in O(1) time and identifiable in O(D), where D is the branching factor
     */
    private Map<Tag, List<Element>> elementsByTag = new HashMap<Tag, List<Element>>();

    // The attributes stored as key-value pairs
    private Map<String, String> attributes = new HashMap<String, String>();

    // Transient data similar to the attributes but is not present in the actual XMLs
    private Map<String, String> transientData = new HashMap<String, String>();

    // Parent is stored for convenience in determining ancestry
    private Element parent;


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

    /*
     * Adds an element to the map of child elements. Checks whether the added element is unique, or if the same element already exists
     * (based on ID or equality). If the element exists, assume the merge can be done (since its checked when difference conflicts are
     * detected) and merge the two.
     */
    public void addElement(Element elem) {
        if (!this.type.container)
            // If this element somehow isn't a container, nothing can be added to it. This should not ever happen though.
            throw new UnsupportedOperationException("Cannot add an element to an element of type " + this.type);
        if (elem == null)
            // Should never occur but lets be sure
            throw new IllegalStateException("Attempting to add a null element to " + this);

        List<Element> elems = elementsByTag.get(elem.getTag());
        Element existing = null;
        if (elems == null) {
            // No elements with such tag exist so we can freely add it
            elems = new ArrayList<Element>();
            elementsByTag.put(elem.getTag(), elems);
        } else if (elems.size() > 0) {
            /*
             * Elements exist, check if they conflict with the added element i.e.
             * check if there is a unique element or one with the same identifier
             */
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

    /*
     * Merges the contents of two elements, assuming there are no differences in the values or attributes between the two
     */
    private void merge(Element elem) {
        for (Tag tag : elem.getElementsByTag().keySet()) {
            elem.elementsByTag.get(tag).forEach(e -> this.addElement(e));
        }
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

    /*
     * Compares this element to the other. The context references the parent element in the
     * original tree. The comparison works recursively so that the original tree is traversed
     * in its entirety. Returns the complete set of detected changes.
     */
    public List<Difference> compare(Element context, Element other) {
        // The types and tags should always match when comparing
        if (!this.type.equals(other.type))
            throw new IllegalStateException("Cannot compare elements of type" + this.type + " and " + other.type);
        if (!CommonUtils.nullSafeEquals(this.tag, other.tag))
            throw new IllegalStateException("Cannot compare elements " + this.tag + " and " + other.tag);

        List<Difference> diffs = new ArrayList<Difference>();
        if (this.type.container)
            // If this element is a container, compare the child elements recursively
            diffs.addAll(DiffUtils.compareChildElements(this, this.getElementsByTag(), other.getElementsByTag()));
        if (this.type.valued)
            // If this element has a value, compare it to the other elements value
            diffs.addAll(DiffUtils.compareValues(this, other));
        // Always compare the attributes (although they might be empty)
        diffs.addAll(DiffUtils.compareAttributes(this, other));
        return diffs;
    }

    // Used for sorting the element lists
    @Override
    public int compareTo(Element o) {
        if (!this.tag.equals(o.tag))
            // If different tags, compare the string values (alphabetical order)
            return this.tag.getName().compareTo(o.getTag().getName());
        if (this.tag.isIdentifiable())
            // If the tags are same and have an identifier, compare the IDs of the elements (alpha/numerical order)
            return this.getId().compareTo(o.getId());
        if (this.type.valued && this.value != null)
            // If the elements are valued, compare the values
            return this.value.compareTo(o.getValue());
        /*
         *  If none of these apply, consider them to be equal
         *  (this would mean non-unique, non-identifiable and non-valued element)
         */
        return 0;
    }



    /*
     * Finds a child element with the given tag and identifier.
     * Doesn't actually check that the element is identifiable.
     */
    public Element findIdElement(Tag tag, String id) {
        Collection<Element> elems = this.elementsByTag.get(tag);
        if (elems != null) {
            List<Element> matching = elems.stream().filter(e -> e.getId().equals(id)).collect(Collectors.toList());
            if (matching.size() > 1)
                // Somehow there were several elements with the same ID which should not happen
                throw new IllegalStateException("More than one instance of an element with ID '" + id + "' found in " + this);
            return matching.size() > 0 ? matching.get(0) : null;
        }
        return null;
    }

    /*
     * Finds a unique child element with the given tag
     * Doesn't actually check that the element is unique
     * (but throws and exception if there are several elements with the given tag)
     */
    public Element findUniqueElement(Tag tag) {
        Collection<Element> elems = this.elementsByTag.get(tag);
        return DiffUtils.getUniqueElem(tag, elems);
    }

    // Gets all child elements with the given tag
    public List<Element> getElements(Tag tag) {
        List<Element> elems = this.elementsByTag.get(tag);
        return elems != null ? elems : Collections.emptyList();
    }

    public String getValue() {
        if (!this.type.valued)
            // Cannot return a value of an element that is not valued. This should not happen.
            throw new UnsupportedOperationException("Cannot read value from an elment of type " + this.type);
        return value;
    }

    /*
     * Matches the elements including the entire ancestry
     * (so two parameters in different nodes are not matched, for example)
     */
    public boolean matches(Element o) {
        if (!this.tag.equals(o.tag))
            return false;
        if (DiffUtils.nullSafeMatches(this.parent, o.parent)) {
            return this.isSame(o);
        }
        return false;
    }

    /**
     * Checks whether the element is the same as the other, based on the tag
     * identifiers, uniqueness and finally equality (without considering ancestry).
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

    // If the element is hidden, it is not displayed in the output message
    public boolean isHidden() {
        return this.tag.isHidden();
    }

    /*
     *  Output value by default is the actual value of the element.
     *  Note that this won't be used for any elements that actually
     *  don't have a value
     */
    public String getOutputValue() {
        return this.value;
    }

    public void removeAttr(String id) {
        this.attributes.remove(id);
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

    public void setElementsByTag(Map<Tag, List<Element>> elementsByTag) {
        this.elementsByTag = elementsByTag;
    }

    public void setId(String id) {
        this.tag.setId(id, this);
    }

    public void setParent(Element parent) {
        this.parent = parent;
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

    public Element id(String id) {
        this.setId(id);
        return this;
    }

    public String getAttr(String id) {
        return this.attributes.get(id);
    }

    public Map<String, String> getAttributes() {
        return attributes;
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

}
