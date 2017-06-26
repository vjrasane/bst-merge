package com.comptel.bst.tools.mergetool.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.comptel.bst.tools.common.CommonUtils;
import com.comptel.bst.tools.common.CommonUtils.TriConsumer;
import com.comptel.bst.tools.diff.comparison.OutputTree;
import com.comptel.bst.tools.diff.parser.BSTReader;
import com.comptel.bst.tools.diff.parser.entity.Yaml;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.utils.DiffConstants;
import com.comptel.bst.tools.mergetool.merger.Conflict;
import com.comptel.bst.tools.mergetool.parser.BSTWriter;


public final class MergeUtils {

    public static String getConflictMessage(List<Conflict> conflicts) {
        StringBuilder b = new StringBuilder();
        b.append("Unresolvable conflicts:\n\n");

        b.append(new OutputTree(conflicts).getMessage());

        return b.toString();
    }

    public static void parseFiles(File base, File local, File remote, TriConsumer<Yaml, Yaml, Yaml> action) throws IOException {
        Timer timer = Timer.start();

        CommonUtils.printPhase(DiffConstants.PARSE_PHASE);

        Yaml baseYaml = BSTReader.readYaml(base);
        Yaml localYaml = BSTReader.readYaml(local);
        Yaml remoteYaml = BSTReader.readYaml(remote);

        timer.printDuration("Parsed files");

        action.accept(baseYaml, localYaml, remoteYaml);
    }

    public static void writeResult(Element result, File output) throws IOException {
        CommonUtils.printPhase(MergeConstants.COMPOSE_PHASE);
        Timer timer = Timer.start();
        BSTWriter.writeYaml(output, result);
        timer.printDuration("Composed YAML file");
    }
}
