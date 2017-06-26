package com.comptel.bst.tools.diff.comparison.differences.changes;

import com.comptel.bst.tools.common.CommonUtils;
import com.comptel.bst.tools.diff.comparison.differences.Difference;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;

public final class ValueChange extends Change {

    public ValueChange(Element original, Element modified) {
        super(original, modified);
    }

    @Override
    public boolean conflicts(Difference other) {
        if (other instanceof ValueChange) {
            ValueChange otherChg = (ValueChange) other;
            if(this.modified.matches(otherChg.modified))
                return !CommonUtils.nullSafeEquals(this.modified.getValue(), otherChg.modified.getValue());
        }
        return super.conflicts(other);
    }

    @Override
    public String getMessage() {
        return super.getMessage() + "value '" + this.original.getOutputValue() + "' -> '" + this.modified.getOutputValue() + "'";
    }

    @Override
    public void apply() {
        this.original.setValue(modified.getValue());
    }

    @Override
    public String getChange(Element elem) {
        return elem.getValue();
    }

    @Override
    public String toString() {
        return super.toString() + "<val>"+ super.getString();
    }

}
