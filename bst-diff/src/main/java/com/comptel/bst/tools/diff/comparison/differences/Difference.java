package com.comptel.bst.tools.diff.comparison.differences;

import com.comptel.bst.tools.diff.comparison.differences.changes.AttributeChange;
import com.comptel.bst.tools.diff.comparison.differences.changes.ValueChange;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;
/*
 * Abstract parent class for all differences. Contains the definitions
 * of the required abstract methods along with some convenience methods.
 */
public abstract class Difference {

    // Used to define the branch the difference is from
    public static enum Branch {
        LOCAL,
        REMOTE,
        UNDEFINED(""); // Required for cases where the branch does not matter or is not known

        public final String label;

        private Branch() {
            this.label = this.name().toLowerCase() + ": ";
        }

        private Branch(String label) {
            this.label = label;
        }
    }

    // Creates a new addition diff (insertion)
    public static Difference added(Element context, Element added) {
        return new Addition(context, added);
    }

    // Creates a new attribute change diff
    public static Difference attrChanged(Element original, Element changed, String attr) {
        return new AttributeChange(attr, original, changed);
    }

    // Creates a new removal diff (delete)
    public static Difference removed(Element context, Element removed) {
        return new Removal(context, removed);
    }

    // Creates a new value change diff
    public static Difference valueChanged(Element original, Element changed) {
        return new ValueChange(original, changed);
    }

    // Method for checking whether a difference conflicts with another
    public abstract boolean conflicts(Difference other);

    // Returns the element that the difference will be applied to
    public abstract Element getTargetElement();

    // Output message
    public abstract String getMessage();

    // Applies the difference to the target element
    public abstract void apply();



}
