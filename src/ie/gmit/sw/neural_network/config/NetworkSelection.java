package ie.gmit.sw.neural_network.config;

import ie.gmit.sw.UserInput;

import java.util.*;

import static java.lang.System.out;

// facilitates the overall network configuration process, performed by the user
public class NetworkSelection {
    // list of selections that have to be made to fully configure the network
    private List<Selection> selections;
    // map representation of the above list, for easy constant time access
    private Map<String, Selection> selectionMap;
    private Scanner console;

    protected NetworkSelection() {
        selections = new ArrayList<>();
        selectionMap = new HashMap<>();
        // get a handle on the shared Scanner object
        console = UserInput.getScanner();
    }

    // adds a new type of selection
    public void addSelection(String key, Selection selection) {
        selections.add(selection);
        selectionMap.put(key, selection);
    }

    // gets the user's choice for a particular selection option
    public Object getSelectionChoice(String selectionKey) {
        return selectionMap.get(selectionKey).getChoice();
    }

    // gets the users choice for all options, using the command line menu system
    public void getUserSelectionForAll() {
        for (Selection selection : selections) {
            getUserSelection(selection);
        }
    }

    // displays the details of the selection to the user, and allows them to pick an option
    private void getUserSelection(Selection selection) {
        out.printf("OPTION: %s%n", selection.getPrompt());
        out.printf("EXPLANATION: %s%n", selection.getExplanation());
        out.printf("GUIDANCE: %s%n", selection.getGuidance());
        out.printf("RECOMMENDED: %s%n", selection.getBest());

        int choiceIndex = getUserOption(selection.getOptionLabels()) - 1;
        selection.choose(choiceIndex);
    }

    // picks the "best" option for each selection
    public void loadOptimizedDefaults() {
        for (Selection selection : selections) {
            selection.chooseBest();
        }
    }

    // asks the user if they want to use the optimized default values, and returns the result
    public boolean shouldUseOptimizedDefaults() {
        String[] options = {
                "Let me pick what parameters to use",
                "Use the optimal default parameters for all options"
        };

        out.println("Would you like to choose all the parameters for the neural network, or use the optimized defaults?");
        int chosen = getUserOption(options);

        // returns true if they picked option 2
        return chosen == 2;
    }

    // presents a simple list of options to the user and lets them pick one based on a numbering system
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

    // repeatably asks the user for a number between min and max until they give one
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

    // "pretty" representation of the chosen neural network configuration
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        // list every selection in turn, with it's selected value
        sb.append("Neural network configuration:\n");
        for (Selection selection : selections) {
            sb.append(selection.toString());
        }

        return sb.toString();
    }
}
