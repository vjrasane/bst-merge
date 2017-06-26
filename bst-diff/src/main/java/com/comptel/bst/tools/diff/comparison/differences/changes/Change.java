package com.comptel.bst.tools.diff.comparison.differences.changes;

import com.comptel.bst.tools.diff.comparison.differences.Addition;
import com.comptel.bst.tools.diff.comparison.differences.Difference;
import com.comptel.bst.tools.diff.comparison.differences.Removal;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;

public abstract class Change extends Difference {

    protected Element original;
    protected Element modified;

    protected Change(Element original, Element modified) {
        this.original = original;
        this.modified = modified;
    }

    @Override
    public boolean conflicts(Difference other) {
        // In practice there should never be a case where an element is both added and modified
        if (other instanceof Addition) {
            Addition otherAdd = (Addition) other;
            if (this.original.matches(otherAdd.getAdded()))
                return true;
        } else if (other instanceof Removal) {
            Removal otherRem = (Removal) other;
            if (otherRem.getRemoved().isAncestor(this.original))
                return true;
        }
        return false;
    }

    @Override
    public String getMessage() {
        return "modified ";
    }

    @Override
    public String toString() {
        return "chg:";
    }

    public String getString() {
        return "[" + this.getChange(this.original) + "==>" + this.getChange(this.modified) + "]";
    }

    @Override
    public Element getTargetElement() {
        return original;
    }

    public abstract String getChange(Element elem);

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((modified == null) ? 0 : modified.hashCode());
        result = prime * result + ((original == null) ? 0 : original.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        Change other = (Change) obj;
        if (modified == null) {
            if (other.modified != null)
                return false;
        } else if (!modified.equals(other.modified))
            return false;
        if (original == null) {
            if (other.original != null)
                return false;
        } else if (!original.equals(other.original))
            return false;
        return true;
    }
}
