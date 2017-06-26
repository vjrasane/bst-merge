package com.comptel.bst.tools.mergetool;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FileUtils;

import com.comptel.bst.tools.common.CommonConstants;
import com.comptel.bst.tools.mergetool.utils.MergeConstants;

public abstract class ToolTest {

    protected File getCopy(String fileName) throws IOException {
        File f = new File(fileName);
        File copy = new File(f.getAbsolutePath() + "_copy");
        FileUtils.copyFile(f, copy);
        return copy;
    }

    protected String[] buildArgs(String operation, String... params) { // base, String local, String remote, String output) {

        List<String> argList = new ArrayList<String>();
        argList.add(operation);
        argList.addAll(Arrays.asList(params));

        return argList.toArray(new String[]{});
    }

    protected String[] buildMergeArgs(String base, String local, String remote, String output) {
        return buildArgs(MergeConstants.MERGE_OPERATION, CommonConstants.BASE_FILE_ARG, base, MergeConstants.LOCAL_FILE_ARG, local, MergeConstants.REMOTE_FILE_ARG , remote, MergeConstants.OUTPUT_FILE_ARG, output);
    }

    protected String[] buildMergeArgs(String base, String local, String remote, String output, String... additional) {
        List<String> argList = new ArrayList<String>(Arrays.asList(CommonConstants.BASE_FILE_ARG, base, MergeConstants.LOCAL_FILE_ARG, local, MergeConstants.REMOTE_FILE_ARG , remote, MergeConstants.OUTPUT_FILE_ARG, output));
        argList.addAll(Arrays.asList(additional));
        return buildArgs(MergeConstants.MERGE_OPERATION, argList.toArray(new String[]{}));
    }

    protected String[] buildMergeArgs(File base, File local, File remote, File output, String... additional) {
        return buildMergeArgs(base.getAbsolutePath(), local.getAbsolutePath(), remote.getAbsolutePath(), output.getAbsolutePath(), additional);
    }

    protected String[] buildDiffArgs(String base, String local, String remote, String... additional) {
        List<String> argList = new ArrayList<String>(Arrays.asList(CommonConstants.BASE_FILE_ARG, base, MergeConstants.LOCAL_FILE_ARG, local, MergeConstants.REMOTE_FILE_ARG , remote));
        argList.addAll(Arrays.asList(additional));
        return buildArgs(MergeConstants.DIFF_OPERATION, argList.toArray(new String[]{}));
    }

    protected String[] buildDiffArgs(File base, File local, File remote, String... additional) {
        return buildDiffArgs(base.getAbsolutePath(), local.getAbsolutePath(), remote.getAbsolutePath(), additional);
    }

}
