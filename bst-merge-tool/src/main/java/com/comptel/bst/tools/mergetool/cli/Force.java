package com.comptel.bst.tools.mergetool.cli;

import java.util.List;

import com.beust.jcommander.IStringConverter;
import com.beust.jcommander.ParameterException;
import com.comptel.bst.tools.diff.comparison.differences.Difference;
import com.comptel.bst.tools.mergetool.merger.Conflict;

/*
 * This enumeral represents the two conflict resolution methods of taking either the local or remote change.
 */
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

    // Used for string representation
    public final String flag;

    private Force(String value) {
        this.flag = value;
    }

    // Output message to the console when using either force methods
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

    // This abstract method can be used to resolve a conflict regardless of which force is in use
    public abstract boolean resolve(List<Difference> localDiffs, List<Difference> remoteDiffs, Conflict conflict);

    /*
     * JCommander uses this class to convert the string input to enum.
     * Additionally it can show the available options in the usage message.
     */
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
