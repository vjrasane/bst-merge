package com.comptel.bst.tools.diff.parser.entity.generic;

import java.io.Serializable;

/*
 * Tag contains the static information about each element in the tree.
 * Essentially it corresponds to the XML tag, but also includes information
 * about the restrictions of that element, such as uniqueness and identifiers.
 */
public final class Tag implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String name; // The name of the tag
    private boolean unique = false; // Whether the tag is unique, false by default

    private String idAttr = null; // The possible identifier attribute

    /*
     * Whether the elements with this tag should be visible in the output
     *  Note that setting this to 'true's does not mean that conflicts arising
     *  from the references are not shown, but rather that they are shown
     *  under the first non-hidden parent element instead of this element
     */
    private boolean hidden = false;

    private Tag(String name) {
        this.name = name;
    }

    private Tag(String name, boolean unique) {
        this(name);
        this.unique = unique;
    }

    private Tag(String name, String idAttr) {
        this(name);
        this.idAttr = idAttr;
    }

    // For general elements the output string can be generated based on the tag
    public String toSimpleString(Element elem) {
        return this.name + (this.isIdentifiable() ? " '" + this.getId(elem) + "'" : "");
    }

    @Override
    public String toString() {
        return this.name;
    }

    /*
     * Some convenience methods, getters and setters
     */

    public static Tag generic(String name) {
        return new Tag(name);
    }

    public static Tag identifiable(String name, String idAttr) {
        return new Tag(name, idAttr);
    }

    public static Tag unique(String name) {
        return new Tag(name, true);
    }

    public Tag hide() {
        this.hidden = true;
        return this;
    }

    public String getId(Element elem) {
        if(!this.isIdentifiable())
            // Cannot retrieve an ID of a non-identifiable element. Should never happen
            throw new IllegalStateException("Attempting to read identifier of non-identifiable element '" + elem + "'");
        return elem.getAttr(this.idAttr);
    }

    public void setId(String id, Element elem) {
        if(!this.isIdentifiable())
            // Cannot set the ID of a non-identifiable element. Should never happen
            throw new IllegalStateException("Attempting to set identifier of non-identifiable element '" + elem + "'");
        elem.setAttr(this.idAttr, id);
    }

    public String getName() {
        return name;
    }

    public boolean isIdentifiable() {
        return this.idAttr != null && !this.idAttr.isEmpty();
    }

    public boolean isUnique() {
        return this.unique;
    }

    public boolean isHidden() {
        return hidden;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Tag other = (Tag) obj;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        return true;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

}
