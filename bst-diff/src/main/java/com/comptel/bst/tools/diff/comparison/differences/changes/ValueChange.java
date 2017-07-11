package com.comptel.bst.tools.diff.comparison.differences.changes;

import com.comptel.bst.tools.common.CommonUtils;
import com.comptel.bst.tools.diff.comparison.differences.Difference;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;

/*
 * Represents a modification of the value of an element
 */
public final class ValueChange extends Change {

    public ValueChange(Element original, Element modified) {
        super(original, modified);
    }

    @Override
    public boolean conflicts(Difference other) {
        if (other instanceof ValueChange) {
            // Check if the value was changed to some other value, if so, this is a conflict
            ValueChange otherChg = (ValueChange) other;
            if(this.modified.matches(otherChg.modified))
                return !CommonUtils.nullSafeEquals(this.modified.getValue(), otherChg.modified.getValue());
        }
        return super.conflicts(other); // Check general change conflicts
    }

    @Override
    public String getMessage() {
        return super.getMessage() + "value '" + this.original.getOutputValue() + "' -> '" + this.modified.getOutputValue() + "'";
    }

    // Set the value of the original element to the modified value
    @Override
    public void apply() {
        this.original.setValue(modified.getValue());
    }

    // Return the value of the element
    @Override
    public String getChange(Element elem) {
        return elem.getValue();
    }

    @Override
    public String toString() {
        return super.toString() + "<val>"+ super.getString();
    }

}
