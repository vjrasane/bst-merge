package com.comptel.bst.tools.diff.utils;

import com.beust.jcommander.IStringConverter;

public enum OutputCharset {

    ASCII(DiffConstants.SUB_ELEMENT_MARKER_ASCII, DiffConstants.SUB_ELEMENT_MARKER_LAST_ASCII), UTF8(DiffConstants.SUB_ELEMENT_MARKER,
            DiffConstants.SUB_ELEMENT_MARKER_LAST);

    public final String subElement;
    public final String lastElement;
    public final String extension;
    public final String indent;

    private OutputCharset(String subElement, String lastElement) {
        this.subElement = subElement;
        this.lastElement = lastElement;
        this.extension = DiffConstants.SUB_ELEMENT_MARKER_EXTEND;
        this.indent = DiffConstants.SUB_ELEMENT_MARKER_INDENT;
    }

    public static class OutputCharsetConverter implements IStringConverter<OutputCharset> {
        @Override
        public OutputCharset convert(String s) {
            for (OutputCharset charset : OutputCharset.values()) {
                if (charset.name().equalsIgnoreCase(s))
                    return charset;
            }
            return null;
        }
    }
}
