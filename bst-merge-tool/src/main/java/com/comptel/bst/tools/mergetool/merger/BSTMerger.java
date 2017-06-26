package com.comptel.bst.tools.mergetool.merger;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.BiConsumer;

import com.comptel.bst.tools.common.CommonUtils;
import com.comptel.bst.tools.diff.comparison.differences.Difference;
import com.comptel.bst.tools.diff.comparison.differences.Difference.Branch;
import com.comptel.bst.tools.diff.parser.entity.generic.Element;
import com.comptel.bst.tools.diff.utils.DiffConstants;
import com.comptel.bst.tools.diff.utils.DiffUtils;
import com.comptel.bst.tools.mergetool.cli.Options;
import com.comptel.bst.tools.mergetool.utils.Timer;

public class BSTMerger {

    public static List<Conflict> checkConflicts(Collection<Difference> localDiffs, Collection<Difference> remoteDiffs) {
        List<Conflict> conflicts = new ArrayList<Conflict>();
        for (Difference localDiff : localDiffs) {
            for (Difference remoteDiff : remoteDiffs) {
                if (localDiff.conflicts(remoteDiff))
                    conflicts.add(new Conflict(localDiff, remoteDiff));
            }
        }
        return conflicts;
    }

    private static <K extends Difference, V> void convertMoves(Map<K, V> map, BiConsumer<K, V> remover, BiConsumer<K, V> converter) {
        map.forEach((k, v) -> {
            remover.accept(k, v);
            converter.accept(k, v);
        });
    }

    public static Map<Branch, List<Difference>> getDifferences(Element base, Element local, Element remote) {
        Timer timer = Timer.start();

        CommonUtils.printPhase(DiffConstants.DIFF_COMP_PHASE);

        Map<Branch, List<Difference>> diffs = new HashMap<Branch, List<Difference>>();

        diffs.put(Branch.LOCAL, DiffUtils.getDifferences(base, local));
        diffs.put(Branch.REMOTE, DiffUtils.getDifferences(base, remote));

        timer.printDuration("Calculated differences");

        return diffs;
    }

    public static Element merge(Element base, Element local, Element remote, Options opts) throws MergeConflictException {
        Element result = CommonUtils.deepCopy(base);

        Map<Branch, List<Difference>> diffs = getDifferences(result, local, remote);

        Timer timer = Timer.start();

        CommonUtils.printPhase("Checking for conflicts");

        List<Difference> localDiffs = diffs.get(Branch.LOCAL);
        List<Difference> remoteDiffs = diffs.get(Branch.REMOTE);

        List<Conflict> conflicts = checkConflicts(localDiffs, remoteDiffs);

        timer.printDuration("Checked conflicts");

        timer = Timer.start();

        opts.getStrat().resolveConflicts(localDiffs, remoteDiffs, conflicts);

        timer.printDuration("Resolved conflicts");

        timer = Timer.start();

        CommonUtils.printPhase("Merging changes");

        applyDiffs(localDiffs);
        applyDiffs(remoteDiffs);

        timer.printDuration("Resolved differences");

        return result;
    }

    public static Element merge(Element base, Element local, Element remote) throws MergeConflictException {
        return merge(base, local, remote, new Options(false, null));
    }

    public static void applyDiffs(Collection<Difference> diffs) {
        diffs.forEach(d -> d.apply());
    }

}
