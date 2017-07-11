package com.comptel.bst.tools.diff.parser.entity.generic;

import java.io.Serializable;

/*
 * A convenience class for representing an attribute.
 * This isn't strictly necessary, but can be useful for conveying
 * both the name and value in conjunction.
 */
public class Attribute implements Serializable {

    private static final long serialVersionUID = 1L;

    private String name;
    private String value;

    public Attribute(String name, String value) {
        super();
        this.name = name;
        this.value = value;
    }

    public String getName() {
        return name;
    }

    public String getValue() {
        return value;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setValue(String value) {
        this.value = value;
    }

}
