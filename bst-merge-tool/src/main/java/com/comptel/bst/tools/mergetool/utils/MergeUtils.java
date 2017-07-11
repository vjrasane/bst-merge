package com.comptel.bst.tools.mergetool.utils;

import java.io.File;
import java.io.IOException;
import java.util.List;

import com.comptel.bst.tools.common.CommonUtils;
import com.comptel.bst.tools.common.CommonUtils.TriConsumer;
import com.comptel.bst.tools.diff.comparison.OutputTree;
import com.comptel.bst.tools.diff.parser.BSTReader;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.parser.entity.generic.Yaml;
import com.comptel.bst.tools.diff.utils.DiffConstants;
import com.comptel.bst.tools.mergetool.merger.Conflict;
import com.comptel.bst.tools.mergetool.parser.BSTWriter;

/*
 * Contains utility methods that are used in several classes or
 * do not functionally fit into any single class
 */
public final class MergeUtils {

    /*
     *  Constructs a conflict message including the output
     *  tree based on the given list of conflicts
     */
    public static String getConflictMessage(List<Conflict> conflicts) {
        StringBuilder b = new StringBuilder();
        b.append("Unresolvable conflicts:\n\n");

        b.append(new OutputTree(conflicts).getMessage());

        return b.toString();
    }

    /*
     * Reads a triad of files into interal YAML elements
     * and passes them on to the given function
     */
    public static void parseFiles(File base, File local, File remote, TriConsumer<Yaml, Yaml, Yaml> action) throws IOException {
        Timer timer = new Timer(); // Start the timer for file parsing

        CommonUtils.printPhase(DiffConstants.PARSE_PHASE); // Print phase message

        // Parse each file into a separate object
        Yaml baseYaml = BSTReader.readYaml(base);
        Yaml localYaml = BSTReader.readYaml(local);
        Yaml remoteYaml = BSTReader.readYaml(remote);

        timer.printDuration("Parsed files"); // Print duration if output is on

        action.accept(baseYaml, localYaml, remoteYaml); // Pass the elements to the expecting function
    }

    // Writes the given element into the output file
    public static void writeResult(Element result, File output) throws IOException {
        CommonUtils.printPhase(MergeConstants.COMPOSE_PHASE);
        Timer timer = new Timer();
        BSTWriter.writeYaml(output, result);
        timer.printDuration("Composed YAML file");
    }
}
