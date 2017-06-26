package com.comptel.bst.tools.diff.cli;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.beust.jcommander.JCommander;
import com.beust.jcommander.Parameter;
import com.beust.jcommander.ParameterException;
import com.comptel.bst.tools.common.CommonConstants;
import com.comptel.bst.tools.common.CommonUtils;
import com.comptel.bst.tools.common.FileExistsValidator;
import com.comptel.bst.tools.diff.comparison.OutputTree;
import com.comptel.bst.tools.diff.comparison.differences.Difference;
import com.comptel.bst.tools.diff.comparison.differences.Difference.Branch;
import com.comptel.bst.tools.diff.parser.BSTReader;
import com.comptel.bst.tools.diff.parser.entity.Yaml;
import com.comptel.bst.tools.diff.utils.DiffConstants;
import com.comptel.bst.tools.diff.utils.OutputCharset;

public class CommandLineTool {

    @Parameter(names = { CommonConstants.BASE_FILE_ARG, CommonConstants.BASE_FILE_ARG_ABBR }, arity = 1, description = "Path to the base file", required = true, validateValueWith = FileExistsValidator.class)
    private File base;

    @Parameter(names = { DiffConstants.DERIVED_FILE_ARG, DiffConstants.DERIVED_FILE_ARG }, arity = 1, description = "Path to the derived file", required = true, validateValueWith = FileExistsValidator.class)
    private File derived;

    @Parameter(names = CommonConstants.OUTPUT_CHARSET_ARG, hidden = true, required = false, converter=OutputCharset.OutputCharsetConverter.class)
    protected OutputCharset charset = OutputCharset.UTF8;

    public static void main(String[] args) {
        CommonUtils.printStartupMsg(DiffConstants.DIFF_PROGRAM_NAME);
        CommandLineTool diffTool = new CommandLineTool();
        JCommander jc = new JCommander(diffTool);

        try {
            jc.parse(args);

            diffTool.diff();
        } catch (ParameterException e) {
            CommonUtils.printError(e.getMessage());
            jc.setProgramName(DiffConstants.DIFF_PROGRAM_NAME);
            jc.usage();
            System.exit(1);
        } catch (IOException e) {
            CommonUtils.printError(e.getMessage());
            System.exit(1);
        }
    }

    public void diff() throws IOException {
        OutputTree.setOutputCharset(this.charset);

        Yaml baseYaml = BSTReader.readYaml(base);
        Yaml derviedYaml = BSTReader.readYaml(derived);

        CommonUtils.printPhase(DiffConstants.DIFF_COMP_PHASE);

        List<Difference> diffs = derviedYaml.compare(baseYaml);

        CommonUtils.printPhase(DiffConstants.DIFF_DETECT_PHASE);
        System.out.println(new OutputTree(Branch.UNDEFINED, diffs));
    }
}
