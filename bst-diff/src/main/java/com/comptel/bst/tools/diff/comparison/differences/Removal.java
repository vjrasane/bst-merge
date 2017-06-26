package com.comptel.bst.tools.diff.comparison.differences;

import com.comptel.bst.tools.diff.parser.entity.generic.Element;


public final class Removal extends SingleContext {

    protected Element removed;

    public Removal(Element context, Element removed) {
        super(context);
        this.removed = removed;
    }

    @Override
    public boolean conflicts(Difference other) {
        if(other instanceof Removal)
            return false;
        else
            return other.conflicts(this);
    }

    public Element getRemoved() {
        return removed;
    }

    public void setRemoved(Element removed) {
        this.removed = removed;
    }

    @Override
    public String getMessage() {
        return "removed " + this.removed.toSimpleString();
    }

    @Override
    public void apply() {
        this.context.removeElement(this.removed);
    }

    @Override
    public String toString() {
        return "rem:[" + this.removed.toSimpleString() + "]";
    }

    @Override
    public Element getTargetElement() {
        return this.removed;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((removed == null) ? 0 : removed.hashCode());
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
        Removal other = (Removal) obj;
        if (removed == null) {
            if (other.removed != null)
                return false;
        } else if (!removed.equals(other.removed))
            return false;
        return true;
    }

}