package com.comptel.bst.tools.mergetool.cli;

import java.io.IOException;
import java.util.List;
import java.util.Map;

import com.beust.jcommander.Parameters;
import com.beust.jcommander.ParametersDelegate;
import com.comptel.bst.tools.common.CommonUtils;
import com.comptel.bst.tools.diff.comparison.OutputTree;
import com.comptel.bst.tools.diff.comparison.differences.Difference;
import com.comptel.bst.tools.diff.comparison.differences.Difference.Branch;
import com.comptel.bst.tools.diff.utils.DiffConstants;
import com.comptel.bst.tools.mergetool.cli.CommandLineTool.Operation;
import com.comptel.bst.tools.mergetool.merger.BSTMerger;
import com.comptel.bst.tools.mergetool.utils.MergeUtils;
import com.comptel.bst.tools.mergetool.utils.Timer;

/*
 * This operation displays the differences of the two derived files to their base file as an output tree.
 * It doesn't perform any merge operations.
 */
@Parameters(commandDescription = "Print differences between the base, local and remote BST logics")
public class DiffOperation implements Operation {

    // Common merge tool CLI parameters
    @ParametersDelegate
    private CommonParams params = new CommonParams();

    @Override
    public void execute() throws IOException {
        Timer.PRINT = params.printDuration; // Set duration output on/off

        OutputTree.setOutputCharset(params.charset);

        /*
         * Here we call the common utility method in MergeUtils that parses the files and
         * expects a function that takes those files as parameters
         */
        MergeUtils.parseFiles(params.base, params.local, params.remote,
                // An anonymous function that takes the (b)ase, (l)ocal and (r)emote files and calculates their differences
                (b, l, r) -> {
                    Map<Branch, List<Difference>> diffs = BSTMerger.getDifferences(b, l, r);

                    OutputTree tree = new OutputTree();
                    diffs.forEach((br, d) -> tree.add(br, d)); // Add each diff to the output tree and mark the correct branch

                    CommonUtils.printPhase(DiffConstants.DIFF_DETECT_PHASE); // Print the phase message
                    System.out.println(tree); // Output the difference tree
                });
    }
}
