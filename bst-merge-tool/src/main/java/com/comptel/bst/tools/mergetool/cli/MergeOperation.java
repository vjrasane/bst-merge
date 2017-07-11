package com.comptel.bst.tools.mergetool.cli;

import java.io.File;
import java.io.IOException;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;
import com.comptel.bst.tools.common.CommonUtils;
import com.comptel.bst.tools.diff.comparison.OutputTree;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.mergetool.cli.CommandLineTool.Operation;
import com.comptel.bst.tools.mergetool.merger.BSTMerger;
import com.comptel.bst.tools.mergetool.utils.MergeConstants;
import com.comptel.bst.tools.mergetool.utils.MergeUtils;
import com.comptel.bst.tools.mergetool.utils.Timer;

/*
 * Executes a three way merge of the given files. Additionally can emply some conflict resolution techniques.
 */
@Parameters(commandDescription = "Three way merge of the given base, local and remote BST logics")
public class MergeOperation implements Operation {

    // Common merge tool CLI parameters
    @ParametersDelegate
    private CommonParams params = new CommonParams();

    /*
     * The output file has to be specified so that Mercurial (or some other revision control)
     * can pass the correct output file location to the merge tool
     */
    @Parameter(names = { MergeConstants.OUTPUT_FILE_ARG, "-o" }, arity = 1, description = "Path to the output file.", required = true)
    private File output;

    // Conflict resolution by forcing one of the two branches
    @Parameter(names = MergeConstants.FORCE_ARG, arity = 1, description = "Forcefully apply changes from one of the branches in case of a conflict.", required = false, converter = Force.ForceConverter.class)
    private Force force = null;

    /*
     *  Interactive conflict resolution, i.e. ask the user which change should be used.
     *  Overrides force if it is set as well.
     */
    @Parameter(names = MergeConstants.INTERACTIVE_ARG, description = "Use interactive prompt for resolving conflicts. Overrides the force option.", required = false)
    private boolean interactive = false;

    @Override
    public void execute() throws IOException {
        Timer.PRINT = params.printDuration; // Set duration output on/off

        OutputTree.setOutputCharset(params.charset);

        Options opts = new Options(interactive, force); // Non-mandatory parameters are wrapped into options

        /*
         * Here we call the common utility method in MergeUtils that parses the files and
         * expects a function that takes those files as parameters
         */
        MergeUtils.parseFiles(params.base, params.local, params.remote,
                // An anonymous function that takes the (b)ase, (l)ocal and (r)emote files and executes a merge on them
                (b, l, r) -> {
                    try {
                        Element result = BSTMerger.merge(b, l, r, opts);
                        MergeUtils.writeResult(result, output);
                    } catch (IOException e) {
                        throw new IllegalStateException("Merge failed with an exception: " + e.getMessage(), e);
                    }
                });

        CommonUtils.printPhase("Merge finished");
    }
}
