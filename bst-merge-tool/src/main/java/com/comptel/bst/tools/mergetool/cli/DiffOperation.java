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

@Parameters(commandDescription = "Print differences between the base, local and remote BST logics")
public class DiffOperation implements Operation {

    @ParametersDelegate
    private CommonParams params = new CommonParams();

    @Override
    public void execute() throws IOException {
        Timer.PRINT = params.printDuration;

        OutputTree.setOutputCharset(params.charset);

        MergeUtils.parseFiles(params.base, params.local, params.remote, (b, l, r) -> {
            Map<Branch, List<Difference>> diffs = BSTMerger.getDifferences(b, l, r);
            OutputTree tree = new OutputTree();
            diffs.forEach((br, d) -> tree.add(br, d));

            CommonUtils.printPhase(DiffConstants.DIFF_DETECT_PHASE);
            System.out.println(tree);
        });
    }
}