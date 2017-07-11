package com.comptel.bst.tools.diff.utils;

import com.beust.jcommander.IStringConverter;

/*
 * Contains the available output message charsets. The UTF8 symbols for the output tree
 * are not displayed correctly in all text representations so there was a need to
 * enable diplaying them as simple ascii characters
 */
public enum OutputCharset {

    ASCII(DiffConstants.SUB_ELEMENT_MARKER_ASCII,
            DiffConstants.SUB_ELEMENT_MARKER_LAST_ASCII),
            UTF8(DiffConstants.SUB_ELEMENT_MARKER,
                    DiffConstants.SUB_ELEMENT_MARKER_LAST);

    // The markers that differ between the charsets
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

    /*
     * Used by JCommander to parse the string unput directly to an enum. Could also
     * display the available options, but the charset parameter is currently hidden
     */
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
