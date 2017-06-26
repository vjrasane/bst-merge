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

public final class CommandLineTool {

    public static void main(String[] args) {
        CommonUtils.printTitle("Running " + MergeConstants.MERGE_PROGRAM_NAME);

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
            CommonUtils.printError(MergeUtils.getConflictMessage(e.getConflicts()));
            System.exit(1);
        } catch (UserAbortException e) {
            System.exit(1);
        } catch (ParameterException e) {
            CommonUtils.printError(e.getMessage());
            jc.usage();
            System.exit(1);
        } catch (Exception e) {
            CommonUtils.printError(e.getMessage());
            System.exit(1);
        }
    }

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

    public interface Operation {
        public void execute() throws IOException;
    }

}
