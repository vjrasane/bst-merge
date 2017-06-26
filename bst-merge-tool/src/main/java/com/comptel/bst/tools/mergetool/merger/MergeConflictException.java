package com.comptel.bst.tools.mergetool.merger;

import java.util.List;


public class MergeConflictException extends RuntimeException {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private final List<Conflict> conflicts;

    public MergeConflictException(List<Conflict> conflicts) {
        super();
        this.conflicts = conflicts;
    }

    public List<Conflict> getConflicts() {
        return conflicts;
    }

}
