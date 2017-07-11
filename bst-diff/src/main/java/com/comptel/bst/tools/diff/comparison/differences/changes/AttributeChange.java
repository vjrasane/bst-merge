package com.comptel.bst.tools.diff.comparison.differences.changes;

import com.comptel.bst.tools.common.CommonUtils;
import com.comptel.bst.tools.diff.comparison.differences.Difference;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;

/*
 * Represents a modification of an attribute of an element
 */
public final class AttributeChange extends Change {

    // The name of the modified attribute
    private String attribute;

    public AttributeChange(String attribute, Element original, Element modified) {
        super(original, modified);
        this.attribute = attribute;
    }

    @Override
    public boolean conflicts(Difference other) {
        if (other instanceof AttributeChange) {
            // If the same attribute is modified to a different value, this is a conflict
            AttributeChange otherChg = (AttributeChange) other;
            if(this.modified.matches(otherChg.modified) && this.attribute.equals(otherChg.attribute))
                return !CommonUtils.nullSafeEquals(this.modified.getAttr(this.attribute), otherChg.modified.getAttr(this.attribute));
        }
        return super.conflicts(other); // Check the general change conflicts
    }

    @Override
    public String getMessage() {
        return super.getMessage() + this.attribute + " '" + this.original.getAttr(this.attribute) + "' -> '"
                + this.modified.getAttr(this.attribute) + "'";
    }

    // Apply the change by setting the attribute in the original element to the modified value
    @Override
    public void apply() {
        this.original.setAttr(this.attribute, modified.getAttr(this.attribute));
    }

    @Override
    public String toString() {
        return super.toString() + "<" + this.attribute +">"+ super.getString();
    }

    // Return the value of the changed attribute
    @Override
    public String getChange(Element elem){
        return elem.getAttr(this.attribute);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((attribute == null) ? 0 : attribute.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        AttributeChange other = (AttributeChange) obj;
        if (attribute == null) {
            if (other.attribute != null)
                return false;
        } else if (!attribute.equals(other.attribute))
            return false;
        return true;
    }
}