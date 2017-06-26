package com.comptel.bst.tools.mergetool.cli;

import java.util.List;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;
import com.comptel.bst.tools.diff.comparison.differences.Difference;
import com.comptel.bst.tools.mergetool.merger.Conflict;

public enum Force {

    LOCAL("local") {
        @Override
        public boolean resolve(List<Difference> localDiffs, List<Difference> remoteDiffs, Conflict conflict) {
            remoteDiffs.remove(conflict.getRemote());
            return true;
        }
    },
    REMOTE("remote") {
        @Override
        public boolean resolve(List<Difference> localDiffs, List<Difference> remoteDiffs, Conflict conflict) {
            localDiffs.remove(conflict.getLocal());
            return true;
        }
    };

    public final String flag;

    private Force(String value) {
        this.flag = value;
    }

    public String getMessage(){
        return "Resolving by forcing " + this.flag + " changes";
    }

    @Override
    public String toString() {
        return this.flag;
    }

    public static Force parseString(String s) {
        for(Force f : Force.values()) {
            if(f.flag.equalsIgnoreCase(s)) {
                return f;
            }
        }
        return null;
    }

    public abstract boolean resolve(List<Difference> localDiffs, List<Difference> remoteDiffs, Conflict conflict);

    public static class ForceConverter implements IStringConverter<Force>{
        @Override
        public Force convert(String s) {
            Force convertedValue = Force.parseString(s);

            if(convertedValue == null) {
                throw new ParameterException("Value '" + s + "' can not be converted to a merge strategy.");
            }
            return convertedValue;
        }
    }
}
