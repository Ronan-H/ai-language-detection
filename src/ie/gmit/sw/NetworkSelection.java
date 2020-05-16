package ie.gmit.sw;

import java.util.*;

import static java.lang.System.out;

public class NetworkSelection {
    private List<Selection> selections;
    private Map<String, Selection> selectionMap;
    private Scanner console;

    protected NetworkSelection() {
        selections = new ArrayList<>();
        selectionMap = new HashMap<>();
        console = UserInput.getScanner();
    }

    public void getUserSelectionForAll() {
        for (Selection selection : selections) {
            getUserSelection(selection);
        }
    }

    private void getUserSelection(Selection selection) {
        out.printf("OPTION: %s%n", selection.getPrompt());
        out.printf("EXPLANATION: %s%n", selection.getExplanation());
        out.printf("GUIDANCE: %s%n", selection.getGuidance());
        out.printf("RECOMMENDED: %s%n", selection.getBest());
        int choiceIndex = getUserOption(selection.getOptionLabels()) - 1;
        selection.choose(choiceIndex);
    }

    public void loadOptimizedDefaults() {
        for (Selection selection : selections) {
            selection.chooseBest();
        }
    }

    public Object getSelectionChoice(String selectionKey) {
        return selectionMap.get(selectionKey).getChoice();
    }

    public void addSelection(String key, Selection selection) {
        selections.add(selection);
        selectionMap.put(key, selection);
    }

    public boolean shouldUseOptimizedDefaults() {
        String[] options = {"Let me pick what parameters to use", "Use the \"optimal\" default parameters for all options"};

        out.println("Would you like to choose all parameters for the neural network, or use the optimized default parameters?");
        int chosen = getUserOption(options);

        return chosen == 2;
    }

    private int getUserOption(String...options) {
        int i;

        out.println("\nSelect an option:");
        // print the options
        for (i = 0; i < options.length; ++i) {
            out.printf("[%d]: %s\n", (i + 1), options[i]);
        }

        out.print("\n");

        // return the user's validated choice
        return getValidatedInt(1, options.length);
    }

    private int getValidatedInt(int min, int max) {
        int input = 0;
        boolean valid;

        do {
            out.print("> ");
            try {
                input = Integer.parseInt(console.nextLine());
                valid = (input >= min && input <= max);
            } catch(NumberFormatException e) {
                // input wasn't a number
                valid = false;
            }

            out.println();

            if (!valid) {
                out.printf("Invalid input; must be a number between %d and %d (inclusive).\n", min, max);
                out.println("Please try again.\n");
            }
        } while(!valid);

        return input;
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        sb.append("Neural network configuration:\n");
        for (Selection selection : selections) {
            sb.append(selection.toString());
        }

        return sb.toString();
    }
}
