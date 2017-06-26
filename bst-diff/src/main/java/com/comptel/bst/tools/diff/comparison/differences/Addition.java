package com.comptel.bst.tools.diff.comparison.differences;

import java.util.Collection;

import com.comptel.bst.tools.diff.comparison.differences.changes.Change;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;

public final class Addition extends SingleContext {

    protected Element added;

    protected Addition(Element context, Element added) {
        super(context);
        this.added = added;
    }

    @Override
    public boolean conflicts(Difference other) {
        if (other instanceof Addition) {
            Addition otherAdd = (Addition) other;
            if (this.added.matches(otherAdd.added))
                return !isMergePossible(this.context, this.added, otherAdd.added);
        } else if (other instanceof Removal) {
            Removal otherRem = (Removal) other;
            if (this.added.matches(otherRem.removed) || otherRem.removed.isAncestor(this.context))
                return true;
        } else if (other instanceof Change)
            return other.conflicts(this);
        return false;
    }

    public Element getAdded() {
        return added;
    }

    public void setAdded(Element added) {
        this.added = added;
    }

    @Override
    public String getMessage() {
        return "added " + this.added.toExpandedString();
    }

    @Override
    public void apply() {
        this.context.addElement(this.added);
    }

    @Override
    public String toString() {
        return "add:[" + this.added.toExpandedString() + "]";
    }

    @Override
    public Element getTargetElement() {
        return added;
    }

    /*
     * Checks if two matching elements can be merged
     */
    protected boolean isMergePossible(Element context, Element first, Element second) {
        // Use comparison to find differences between elements
        Collection<Difference> diffs = first.compare(context, second);
        // If any of the differences are changes to the value or attributes, there is a conflict and merge is not possible
        return diffs.stream().noneMatch(d -> d instanceof Change);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((added == null) ? 0 : added.hashCode());
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
        Addition other = (Addition) obj;
        if (added == null) {
            if (other.added != null)
                return false;
        } else if (!added.equals(other.added))
            return false;
        return true;
    }
}
