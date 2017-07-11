package com.comptel.bst.tools.diff.comparison.differences;

import com.comptel.bst.tools.diff.parser.entity.generic.Element;

/*
 * Common superclass that holds the context elements. As of yet ther are no multi-context
 * diffs supported, but in the future copy and uncopy could be added.
 */
public abstract class SingleContext extends Difference {

    protected Element context;

    public SingleContext(Element context) {
        this.context = context;
    }

    public Element getContext() {
        return context;
    }

    public void setContext(Element context) {
        this.context = context;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((context == null) ? 0 : context.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (getClass() != obj.getClass())
            return false;
        SingleContext other = (SingleContext) obj;
        if (context == null) {
            if (other.context != null)
                return false;
        } else if (!context.equals(other.context))
            return false;
        return true;
    }

}
