package com.comptel.bst.tools.mergetool.cli;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.JCommander.Builder;
import com.beust.jcommander.ParameterException;
import com.comptel.bst.tools.common.CommonUtils;
import com.comptel.bst.tools.mergetool.merger.MergeConflictException;
import com.comptel.bst.tools.mergetool.utils.MergeConstants;
import com.comptel.bst.tools.mergetool.utils.MergeUtils;

/*
 * The main class for running the merge tool from command line
 */
public final class CommandLineTool {

    public static void main(String[] args) {
        CommonUtils.printTitle("Running " + MergeConstants.MERGE_PROGRAM_NAME); // Print the header message

        // Both merge and diff operations are supported
        Map<String, Operation> operations = getOperations();
        CommandLineTool tool = new CommandLineTool();
        JCommander jc = buildCommander(tool, operations);
        jc.setProgramName(MergeConstants.MERGE_PROGRAM_NAME);

        try {
            jc.parse(args);
            Operation operation = operations.get(jc.getParsedCommand());
            if (operation == null)
                throw new ParameterException("Expected a command, got '" + jc.getParsedCommand() + "'");
            operation.execute();
        } catch (MergeConflictException e) {
            /*
             * This occurs when merge conflicts are detected during a merge. It makes sense to
             * throw an exception since we want to end execution immediately without modifying any files.
             */
            CommonUtils.printError(MergeUtils.getConflictMessage(e.getConflicts())); // Print the output conflict tree
            System.exit(1);
        } catch (UserAbortException e) {
            // This is thrown when the user aborts the interactive conflict resolution
            System.exit(1);
        } catch (ParameterException e) {
            // Thrown by JCommander if the command line parameters are incorrect
            CommonUtils.printError(e.getMessage());
            jc.usage();
            System.exit(1);
        } catch (Exception e) {
            // Safeguard
            CommonUtils.printError(e.getMessage());
            System.exit(1);
        }
    }

    // Operations implement this interface so that we can call the same method regardless of which one is used
    public interface Operation {
        public void execute() throws IOException;
    }

    /*
     * Some private utility methods
     */

    private static Map<String, Operation> getOperations() {
        Map<String, Operation> ops = new HashMap<String, Operation>();
        ops.put(MergeConstants.MERGE_OPERATION, new MergeOperation());
        ops.put(MergeConstants.DIFF_OPERATION, new DiffOperation());
        return ops;
    }

    private static JCommander buildCommander(CommandLineTool tool, Map<String, Operation> operations) {
        Builder b = JCommander.newBuilder().addObject(tool);
        for (String name : operations.keySet()) {
            b.addCommand(name, operations.get(name));
        }
        return b.build();
    }

}
