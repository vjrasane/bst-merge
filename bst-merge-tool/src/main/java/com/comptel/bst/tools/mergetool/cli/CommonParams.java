package com.comptel.bst.tools.mergetool.cli;

import java.io.File;

import com.beust.jcommander.Parameter;
import com.comptel.bst.tools.common.CommonConstants;
import com.comptel.bst.tools.common.FileExistsValidator;
import com.comptel.bst.tools.diff.utils.OutputCharset;
import com.comptel.bst.tools.diff.utils.OutputCharset.OutputCharsetConverter;
import com.comptel.bst.tools.mergetool.utils.MergeConstants;

/*
 * These parameters are common for all of the allowed operations of the merge tool. They can be added as delegates into the appropriate classes.
 */
public class CommonParams {

    /*
     * Three way merge file parameters.
     */

    @Parameter(names = { CommonConstants.BASE_FILE_ARG, CommonConstants.BASE_FILE_ARG_ABBR }, arity = 1, description = "Path to the base file of the two files to be merged.", required = true, validateValueWith = FileExistsValidator.class)
    protected File base;

    @Parameter(names = { MergeConstants.LOCAL_FILE_ARG, MergeConstants.LOCAL_FILE_ARG_ABBR }, arity = 1, description = "Path to the local file to be merged.", required = true, validateValueWith = FileExistsValidator.class)
    protected File local;

    @Parameter(names = { MergeConstants.REMOTE_FILE_ARG, MergeConstants.REMOTE_FILE_ARG_ABBR }, arity = 1, description = "Path to the remote file to be merged.", required = true, validateValueWith = FileExistsValidator.class)
    protected File remote;

    // This is a hidden parameter mostly used for benchmarking the duration of each part of the merge
    @Parameter(names = MergeConstants.PRINT_DURATION_ARG, hidden = true, required = false)
    protected boolean printDuration = false;

    /*
     *  Another hidden parameter that can be used to switch to either UTF8 or ASCII charset.
     *  This is mostly for showing the output in ASCII in case some applications do not support UTF8.
     */
    @Parameter(names = CommonConstants.OUTPUT_CHARSET_ARG, hidden = true, required = false, converter=OutputCharsetConverter.class)
    protected OutputCharset charset = OutputCharset.UTF8;

    public File getBase() {
        return base;
    }

    public void setBase(File base) {
        this.base = base;
    }

    public File getLocal() {
        return local;
    }

    public void setLocal(File local) {
        this.local = local;
    }

    public File getRemote() {
        return remote;
    }

    public void setRemote(File remote) {
        this.remote = remote;
    }

    public boolean isPrintDuration() {
        return printDuration;
    }

    public void setPrintDuration(boolean printDuration) {
        this.printDuration = printDuration;
    }

}
