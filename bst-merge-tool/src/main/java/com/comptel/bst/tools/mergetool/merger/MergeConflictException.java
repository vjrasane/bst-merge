package com.comptel.bst.tools.mergetool.merger;

import java.util.List;

/*
 * Exception thrown when unresolvable merge conflicts are encountered.
 */
public class MergeConflictException extends RuntimeException {

    private static final long serialVersionUID = 1L;

    // Contains the conflicts that should be shown as output
    private final List<Conflict> conflicts;

    public MergeConflictException(List<Conflict> conflicts) {
        super();
        this.conflicts = conflicts;
    }

    public List<Conflict> getConflicts() {
        return conflicts;
    }

}
