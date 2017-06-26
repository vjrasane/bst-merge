package com.comptel.bst.tools.diff.comparison.differences;

import com.comptel.bst.tools.diff.comparison.differences.changes.AttributeChange;
import com.comptel.bst.tools.diff.comparison.differences.changes.ValueChange;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;

public abstract class Difference {

    public static enum Branch {
        LOCAL,
        REMOTE,
        UNDEFINED("");

        public final String label;

        private Branch() {
            this.label = this.name().toLowerCase() + ": ";
        }

        private Branch(String label) {
            this.label = label;
        }
    }

    public static Difference added(Element context, Element added) {
        return new Addition(context, added);
    }

    public static Difference attrChanged(Element original, Element changed, String attr) {
        return new AttributeChange(attr, original, changed);
    }

    public static Difference removed(Element context, Element removed) {
        return new Removal(context, removed);
    }

    public static Difference valueChanged(Element original, Element changed) {
        return new ValueChange(original, changed);
    }

    public abstract boolean conflicts(Difference other);

    public abstract Element getTargetElement();

    public abstract String getMessage();

    public abstract void apply();



}
