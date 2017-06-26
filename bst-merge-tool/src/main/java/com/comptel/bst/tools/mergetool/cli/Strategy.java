package com.comptel.bst.tools.mergetool.cli;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Scanner;

import org.apache.commons.lang3.StringUtils;

import com.comptel.bst.tools.common.CommonUtils;
import com.comptel.bst.tools.common.CommonUtils.TriFunc;
import com.comptel.bst.tools.diff.comparison.OutputTree;
import com.comptel.bst.tools.diff.comparison.differences.Difference;
import com.comptel.bst.tools.diff.comparison.differences.Difference.Branch;
import com.comptel.bst.tools.mergetool.merger.Conflict;
import com.comptel.bst.tools.mergetool.merger.MergeConflictException;
import com.comptel.bst.tools.mergetool.utils.MergeConstants;

public class Strategy {

    private List<Resolver> pipeline = new ArrayList<Resolver>();

    @FunctionalInterface
    private interface Resolver extends TriFunc<List<Difference>, List<Difference>, List<Conflict>, List<Conflict>> {
    }

    public Strategy(boolean interactive, Force force) {
        if (interactive)
            this.pipeline.add((l, r, c) -> interactiveResolve(l, r, c));
        if (force != null)
            this.pipeline.add((l, r, c) -> forceResolve(force, l, r, c));
    }

    public void resolveConflicts(List<Difference> localDiffs, List<Difference> remoteDiffs, List<Conflict> conflicts)
            throws MergeConflictException {
        List<Conflict> unresolved = new ArrayList<Conflict>(conflicts);
        if (!unresolved.isEmpty()) {
            if (!pipeline.isEmpty()) {
                CommonUtils.printPhase("Merge conflicts detected:\n");

                System.out.println(new OutputTree(unresolved).getMessage());

                for (Resolver resolver : pipeline) {
                    if (unresolved.isEmpty())
                        break;
                    else
                        unresolved = resolver.apply(localDiffs, remoteDiffs, unresolved);
                }
            }
            if (!unresolved.isEmpty())
                throw new MergeConflictException(unresolved);
        }
    }

    private List<Conflict> interactiveResolve(List<Difference> localDiffs, List<Difference> remoteDiffs, List<Conflict> conflicts) {
        CommonUtils.printPhase("Resolving with interactive prompt\n");
        Scanner scanner = new Scanner(System.in);

        List<Choice> choices = new ArrayList<Choice>();

        try {
            int counter = 0;
            while (counter <= conflicts.size()) {
                Conflict current = null;

                ChoiceType[] allowed = ChoiceType.NORMAL;
                if (choices.size() == conflicts.size()) {
                    allowed = ChoiceType.FINALS;
                    System.out.println(CommonUtils.addPadding("All conflicts resolved", '=') + "\n");
                } else {
                    current = conflicts.get(counter);

                    System.out.println(CommonUtils.addPadding("Conflict " + (counter + 1) + "/" + conflicts.size(), '=') + "\n");
                    System.out.println("\n" + new OutputTree(current).getMessage());

                    if (choices.isEmpty()) {
                        allowed = ChoiceType.NO_UNDO;
                    }
                }

                String prompt = createPrompt(allowed);
                ChoiceType choiceType = awaitInput(prompt, allowed, scanner);

                switch (choiceType) {
                case ABORT:
                    System.out.println(MergeConstants.REPLY_MARKER + "Aborting");
                    throw new UserAbortException();
                case UNDO:
                    int prevIndex = --counter;
                    Choice prev = choices.remove(prevIndex);
                    System.out.println(MergeConstants.REPLY_MARKER + "Undoing: \"" + prev.getMessage() + "\"\n");
                    break;
                case CLEAR:
                    choices.clear();
                    System.out.println(MergeConstants.REPLY_MARKER + "Cleared all previous choices\n");
                    counter = 0;
                    break;
                case DONE:
                    System.out.println(MergeConstants.REPLY_MARKER + "Done\n");
                    counter++;
                    break;
                case PRINT:
                    System.out.println(MergeConstants.REPLY_MARKER + "Printing chosen resolutions\n");
                    System.out.println(new OutputTree(choices).getMessage());
                    break;
                default:
                    Choice c = new Choice(choiceType, current);
                    choices.add(c);
                    System.out.println(MergeConstants.REPLY_MARKER + c.getMessage() + "\n");
                    counter++;
                    break;
                }
            }
        } finally {
            scanner.close();
        }

        List<Conflict> unresolved = new ArrayList<Conflict>(conflicts);
        choices.forEach(c -> {
            if (c.apply(localDiffs, remoteDiffs))
                unresolved.remove(c.conflict);
        });

        return unresolved;
    }

    private String createPrompt(ChoiceType[] allowed) {
        List<String> prompts = CommonUtils.mapList(Arrays.asList(allowed), c -> c.prompt);
        String last = prompts.remove(prompts.size() - 1);
        String s = StringUtils.join(prompts, ", ");
        return "Choose: " + s + " or " + last;
    }

    private ChoiceType awaitInput(String prompt, ChoiceType[] allowed, Scanner scanner) {
        ChoiceType choice = null;
        while (choice == null) {
            System.out.println(CommonUtils.decorateOutput(prompt, '*'));
            choice = ChoiceType.parseString(allowed, scanner.nextLine());
        }
        return choice;
    }

    private List<Conflict> forceResolve(Force force, List<Difference> localDiffs, List<Difference> remoteDiffs,
            Collection<Conflict> conflicts) {
        List<Conflict> unresolved = new ArrayList<Conflict>(conflicts);
        CommonUtils.printPhase(force.getMessage());
        for (Conflict conflict : conflicts) {
            if (force.resolve(localDiffs, remoteDiffs, conflict))
                unresolved.remove(conflict);
        }
        return unresolved;
    }

    private static class Choice implements OutputTree.OutputElement {
        private ChoiceType type;
        private Conflict conflict;

        public Choice(ChoiceType type, Conflict conflict) {
            this.type = type;
            this.conflict = conflict;
        }

        public boolean apply(List<Difference> localDiffs, List<Difference> remoteDiffs) {
            return type.apply(localDiffs, remoteDiffs, conflict);
        }

        public String getMessage() {
            return "Taking " + this.type.getMessage(conflict);
        }

        @Override
        public void addTo(OutputTree tree) {
            tree.add(type.branch, conflict.get(type.branch));
        }
    }

    private static enum ChoiceType {
        LOCAL(Branch.LOCAL) {
            @Override
            public boolean apply(List<Difference> localDiffs, List<Difference> remoteDiffs, Conflict conflict) {
                return Force.LOCAL.resolve(localDiffs, remoteDiffs, conflict);
            }

            @Override
            public String getMessage(Conflict c) {
                return Branch.LOCAL.label + c.getLocal().getMessage();
            }
        },
        REMOTE(Branch.REMOTE) {
            @Override
            public boolean apply(List<Difference> localDiffs, List<Difference> remoteDiffs, Conflict conflict) {
                return Force.REMOTE.resolve(localDiffs, remoteDiffs, conflict);
            }

            @Override
            public String getMessage(Conflict c) {
                return Branch.REMOTE.label + c.getRemote().getMessage();
            }
        },
        UNDO(Branch.UNDEFINED), DONE(Branch.UNDEFINED), CLEAR(Branch.UNDEFINED), ABORT(Branch.UNDEFINED), PRINT(Branch.UNDEFINED);

        public static final ChoiceType[] NORMAL = new ChoiceType[] { ChoiceType.LOCAL, ChoiceType.REMOTE, ChoiceType.UNDO,
            ChoiceType.CLEAR, ChoiceType.ABORT };
        public static final ChoiceType[] FINALS = new ChoiceType[] { ChoiceType.DONE, ChoiceType.PRINT, ChoiceType.CLEAR, ChoiceType.UNDO, ChoiceType.ABORT };
        public static final ChoiceType[] NO_UNDO = new ChoiceType[] { ChoiceType.LOCAL, ChoiceType.REMOTE, ChoiceType.ABORT };

        private final String letter;
        private final String prompt;
        private final Branch branch;

        private ChoiceType(Branch branch, String prompt) {
            letter = Character.toString(this.name().charAt(0));
            this.prompt = prompt;
            this.branch = branch;
        }

        private ChoiceType(Branch branch) {
            letter = Character.toString(this.name().charAt(0));
            this.prompt = "[" + this.letter.toUpperCase() + "]" + this.name().substring(1).toLowerCase();
            this.branch = branch;
        }


        public static ChoiceType parseString(ChoiceType[] allowed, String s) {
            for (ChoiceType c : allowed) {
                if (c.name().equalsIgnoreCase(s) || c.letter.equalsIgnoreCase(s))
                    return c;
            }
            return null;
        }

        public boolean apply(List<Difference> localDiffs, List<Difference> remoteDiffs, Conflict conflict) {
            return false;
        }

        public String getMessage(Conflict c) {
            return null;
        }
    }
}
