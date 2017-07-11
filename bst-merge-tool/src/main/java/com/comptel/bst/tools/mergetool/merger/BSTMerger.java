package com.comptel.bst.tools.mergetool.merger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.comptel.bst.tools.common.CommonUtils;
import com.comptel.bst.tools.diff.comparison.differences.Difference;
import com.comptel.bst.tools.diff.comparison.differences.Difference.Branch;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.utils.DiffConstants;
import com.comptel.bst.tools.diff.utils.DiffUtils;
import com.comptel.bst.tools.mergetool.cli.Options;
import com.comptel.bst.tools.mergetool.utils.Timer;

/*
 * Class that executes the actual merge of the files converted into tree objects
 */
public class BSTMerger {

    /*
     * The core functionality of the program. Takes three generic 'Element' objects that are
     * essentially the converted YAML files and executes the merge on them.
     */
    public static Element merge(Element base, Element local, Element remote, Options opts) throws MergeConflictException {
        /*
         *  Just in case copy the base tree so that the original is never modified.
         *  The base file is never written to so this should not be an issue.
         *  Note that we can still compare the derived files to the copy as it is identical.
         *  During the differencing we add pointers to the copy so that the changes can be applied in the correct places.
         */
        Element result = CommonUtils.deepCopy(base);

        // Get the differences mapped based on their branch (LOCAL/REMOTE)
        Map<Branch, List<Difference>> diffs = getDifferences(result, local, remote);

        Timer timer = new Timer(); // Start the timer for conflict checking

        CommonUtils.printPhase("Checking for conflicts"); // Print phase message

        List<Difference> localDiffs = diffs.get(Branch.LOCAL);
        List<Difference> remoteDiffs = diffs.get(Branch.REMOTE);

        List<Conflict> conflicts = checkConflicts(localDiffs, remoteDiffs); // Compare the two lists of differences and check for conflicts

        timer.printDuration("Checked conflicts"); // If the duration output is on, print the duration of conflict check

        timer.start(); // Restart the timer to measure conflict resolution

        /*
         * Resolve conflicts using the conflict resolution strategy.
         * The strategy object is always present even though it might not contain any resolvers.
         */
        opts.getStrat().resolveConflicts(localDiffs, remoteDiffs, conflicts);

        timer.printDuration("Resolved conflicts"); // Print resolution duration if output is on

        timer.start(); // Restart the timer for measuring merge

        CommonUtils.printPhase("Merging changes");

        /*
         * The diffs can be applied separately since they are independent
         * and at this point non-conflicting. Moreover, their order does not matter here.
         */
        applyDiffs(localDiffs);
        applyDiffs(remoteDiffs);

        timer.printDuration("Resolved differences"); // Print merge duration if output is on

        return result; // Output a single result tree that represents the output YAML file
    }

    // Convenience method that is used in tests that don't define any additional options
    public static Element merge(Element base, Element local, Element remote) throws MergeConflictException {
        return merge(base, local, remote, new Options(false, null));
    }

    // Checks the two lists of differences for conflicts
    public static List<Conflict> checkConflicts(Collection<Difference> localDiffs, Collection<Difference> remoteDiffs) {
        List<Conflict> conflicts = new ArrayList<Conflict>();
        // Two nested loops are required since all possible pairings have to be checked
        for (Difference localDiff : localDiffs) {
            for (Difference remoteDiff : remoteDiffs) {
                // The difference objects have the functionality for checking if they conflict with one another
                if (localDiff.conflicts(remoteDiff))
                    conflicts.add(new Conflict(localDiff, remoteDiff));
            }
        }
        return conflicts;
    }

    public static Map<Branch, List<Difference>> getDifferences(Element base, Element local, Element remote) {
        /*
         * Timer for calculating the differences. This should be placed
         * here rather than the calling method since this is used in both diff and merge operations
         */
        Timer timer = new Timer();

        CommonUtils.printPhase(DiffConstants.DIFF_COMP_PHASE); // Print phase message

        Map<Branch, List<Difference>> diffs = new HashMap<Branch, List<Difference>>();

        // We can use the diff functionality of the diff tool
        diffs.put(Branch.LOCAL, DiffUtils.getDifferences(base, local));
        diffs.put(Branch.REMOTE, DiffUtils.getDifferences(base, remote));

        timer.printDuration("Calculated differences"); // Print diff duration if output is on

        return diffs;
    }

    // Convenience method for looping over the diffs and applying them
    public static void applyDiffs(Collection<Difference> diffs) {
        /*
         * The difference objects have all required information about
         * where they should be applied and how
         */
        diffs.forEach(d -> d.apply());
    }

}
