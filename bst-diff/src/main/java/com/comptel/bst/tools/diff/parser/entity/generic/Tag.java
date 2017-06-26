package com.comptel.bst.tools.diff.parser.entity.generic;

import java.io.Serializable;

public final class Tag implements Serializable {

    private static final long serialVersionUID = 1L;

    private final String name;
    private boolean unique = false;

    private String idAttr = null;
    private boolean hidden = false;

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

    public String getId(Element elem) {
        if(!this.isIdentifiable())
            throw new IllegalStateException("Attempting to read identifier of non-identifiable element '" + elem + "'");
        return elem.getAttr(this.idAttr);
    }

    public String getName() {
        return name;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        return result;
    }

    public boolean isIdentifiable() {
        return this.idAttr != null && !this.idAttr.isEmpty();
    }

    public boolean isUnique() {
        return this.unique;
    }

    public void setId(String id, Element elem) {
        if(!this.isIdentifiable())
            throw new IllegalStateException("Attempting to set identifier of non-identifiable element '" + elem + "'");
        elem.setAttr(this.idAttr, id);
    }

    public boolean isHidden() {
        return hidden;
    }

    public String toSimpleString(Element elem) {
        return this.name + (this.isIdentifiable() ? " '" + this.getId(elem) + "'" : "");
    }

    @Override
    public String toString() {
        return this.name;
    }

}
