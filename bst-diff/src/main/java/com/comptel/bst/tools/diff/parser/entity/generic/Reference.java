package com.comptel.bst.tools.diff.parser.entity.generic;

import com.comptel.bst.tools.diff.utils.DiffConstants;

public class Reference extends Element {

    private static final long serialVersionUID = 1L;

    public static final Tag TAG = Tag.generic("reference").hide();

    public static final String SOURCE_NAME_DATA = DiffConstants.DATA_PREFIX + ":source-name";

    public Reference(String value, String source) {
        super(TAG, value);
        this.setSourceName(source);
    }

    public void setSourceName(String name) {
        this.setData(SOURCE_NAME_DATA, name);
    }

    public String getSourceName() {
        return this.getData(SOURCE_NAME_DATA);
    }

    @Override
    public String toSimpleString() {
        return "reference from " + this.getOutputValue();
    }

    @Override
    public String getOutputValue() {
        return this.getSourceName();
    }

}
