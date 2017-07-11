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

/*
 * Conflict resolution strategy class. Contains the pipeline that the set of conflicts is run through in order.
 */
public class Strategy {

    // The ordered list of resolvers
    private List<Resolver> pipeline = new ArrayList<Resolver>();

    /*
     * A named functional interface for convenience. Takes the local and remote differences and
     * the list of conflicts, then outputs the unresolved conflicts.
     */
    @FunctionalInterface
    private interface Resolver extends TriFunc<List<Difference>, List<Difference>, List<Conflict>, List<Conflict>> {
    }

    public Strategy(boolean interactive, Force force) {
        if (interactive)
            this.pipeline.add((l, r, c) -> interactiveResolve(l, r, c)); // Add an interactive resolver to the pipeline
        if (force != null)
            this.pipeline.add((l, r, c) -> forceResolve(force, l, r, c)); // Add a force resolver to the pipeline
    }

    // Runs the list of conflicts through the pipeline in an attempt to resolve it
    public void resolveConflicts(List<Difference> localDiffs, List<Difference> remoteDiffs, List<Conflict> conflicts)
            throws MergeConflictException {
        List<Conflict> unresolved = new ArrayList<Conflict>(conflicts);
        if (!unresolved.isEmpty()) {
            if (!pipeline.isEmpty()) {
                CommonUtils.printPhase("Merge conflicts detected:\n"); // Print the phase message

                System.out.println(new OutputTree(unresolved).getMessage()); // Output the conflicts

                for (Resolver resolver : pipeline) { // Loop over each resolver
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

    // Force resolution
    private List<Conflict> forceResolve(Force force, List<Difference> localDiffs, List<Difference> remoteDiffs,
            Collection<Conflict> conflicts) {
        List<Conflict> unresolved = new ArrayList<Conflict>(conflicts);
        CommonUtils.printPhase(force.getMessage()); // Print phase message
        // Loop over the conflicts and apply the chosen force method
        for (Conflict conflict : conflicts) {
            // If the conflict was resolved, remove it from the unresolved
            if (force.resolve(localDiffs, remoteDiffs, conflict))
                unresolved.remove(conflict);
        }
        return unresolved;
    }

    // Interactive resolution
    private List<Conflict> interactiveResolve(List<Difference> localDiffs, List<Difference> remoteDiffs, List<Conflict> conflicts) {
        CommonUtils.printPhase("Resolving with interactive prompt\n"); // Print the phase message
        Scanner scanner = new Scanner(System.in);

        List<Choice> choices = new ArrayList<Choice>(); // Stores the choices the user has made

        try {
            int counter = 0; // Keeps track of the index of the conflict that is currently being resolved
            while (counter <= conflicts.size()) { // Loop as long as there are conflicts remaining
                Conflict current = null;

                ChoiceType[] allowed = ChoiceType.NORMAL; // Set the default choices LOCAL, REMOTE, UNDO, CLEAR, ABORT
                if (choices.size() == conflicts.size()) {
                    // All choices have been made so no other output
                    allowed = ChoiceType.FINALS; // allow only UNDO, CLEAR, ABORT, DONE, PRINT
                    System.out.println(CommonUtils.addPadding("All conflicts resolved", '=') + "\n");
                } else {
                    // Conflicts remain
                    current = conflicts.get(counter); // Get the current conflict

                    // Output the conflict tree
                    System.out.println(CommonUtils.addPadding("Conflict " + (counter + 1) + "/" + conflicts.size(), '=') + "\n");
                    System.out.println("\n" + new OutputTree(current).getMessage());

                    if (choices.isEmpty()) {
                        // If there are no previous choices, do not allow UNDO
                        allowed = ChoiceType.NO_UNDO;
                    }
                }

                String prompt = createPrompt(allowed); // Create the prompt message based on the allowed options
                ChoiceType choiceType = awaitInput(prompt, allowed, scanner); // Await user input

                // Execute an operation based on the choice
                switch (choiceType) {
                case ABORT:
                    System.out.println(MergeConstants.REPLY_MARKER + "Aborting");
                    throw new UserAbortException(); // End execution immediately (without error message)
                case UNDO:
                    int prevIndex = --counter; // Step back in the list of conflicts
                    Choice prev = choices.remove(prevIndex); // Remove previous choice
                    System.out.println(MergeConstants.REPLY_MARKER + "Undoing: \"" + prev.getMessage() + "\"\n");
                    break;
                case CLEAR:
                    choices.clear(); // Clear all choices
                    System.out.println(MergeConstants.REPLY_MARKER + "Cleared all previous choices\n");
                    counter = 0; // Start over from the beginning
                    break;
                case DONE:
                    counter++; // Step forward so the while loop exits naturally
                    System.out.println(MergeConstants.REPLY_MARKER + "Done\n");
                    break;
                case PRINT:
                    System.out.println(MergeConstants.REPLY_MARKER + "Printing chosen resolutions\n");
                    System.out.println(new OutputTree(choices).getMessage()); // Print the choices in the output tree
                    break;
                default:
                    // Otherwise the choice should be placed in the list of choices (i.e. it is either REMOTE or LOCAL)
                    Choice c = new Choice(choiceType, current);
                    choices.add(c);
                    System.out.println(MergeConstants.REPLY_MARKER + c.getMessage() + "\n");
                    counter++; // Step forward
                    break;
                }
            } // End of the interactive loop
        } finally {
            scanner.close();
        }

        /*
         * At this point the choices about each conflict have been made,
         * but none of them are really resolved yet. We loop over the choices
         * and apply them.
         */
        List<Conflict> unresolved = new ArrayList<Conflict>(conflicts);
        choices.forEach(c -> {
            if (c.apply(localDiffs, remoteDiffs))
                // If the conflict was resolved, remove it from the unresolved
                unresolved.remove(c.conflict);
        });

        return unresolved;
    }

    // Class for storing the user choices in the interactive mode
    private static class Choice implements OutputTree.OutputElement {
        private ChoiceType type; // Essentially this will always be one of the branches
        private Conflict conflict; // The corresponding conflict

        public Choice(ChoiceType type, Conflict conflict) {
            this.type = type;
            this.conflict = conflict;
        }

        // Applies the choice based on type
        public boolean apply(List<Difference> localDiffs, List<Difference> remoteDiffs) {
            return type.apply(localDiffs, remoteDiffs, conflict);
        }

        public String getMessage() {
            return "Taking " + this.type.getMessage(conflict);
        }

        // This allows the choices to be displayed as an output tree
        @Override
        public void addTo(OutputTree tree) {
            tree.add(type.branch, conflict.get(type.branch));
        }
    }

    // Enum for the different types of choices
    private static enum ChoiceType {
        LOCAL(Branch.LOCAL) {
            // Only the local and remote choices can really be applied so they override the default method that does nothing
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
            // Override the default method that does nothing
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

        /*
         * The sets of allowed choices remain the same so we store
         * them beforehand to clean up the code
         */
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

        // Parse a given string into a choice (essentially matches the user input to a choice)
        public static ChoiceType parseString(ChoiceType[] allowed, String s) {
            for (ChoiceType c : allowed) {
                if (c.name().equalsIgnoreCase(s) || c.letter.equalsIgnoreCase(s))
                    return c;
            }
            return null;
        }

        /*
         *  By default the apply does nothing to the conflicts (with choices like CLEAR and ABORT).
         *  If this method is called for anything other than LOCAL or REMOTE, something has gone wrong.
         */
        public boolean apply(List<Difference> localDiffs, List<Difference> remoteDiffs, Conflict conflict) {
            return false;
        }

        public String getMessage(Conflict c) {
            return null;
        }
    }

    /*
     * Private utility methods
     */

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

}
