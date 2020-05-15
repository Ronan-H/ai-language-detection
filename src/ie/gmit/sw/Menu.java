package ie.gmit.sw;


import static java.lang.System.out;

public class Menu {
    public void go() {
        // print the program header
        out.println();
        out.println(" =================================");
        out.println(" |     AI Language Detection     |");
        out.println(" |        By Ronan Hanley        |");
        out.println(" =================================\n");

        out.println("The flow of this application is as follows:");
        out.println("  1. Creation of the training data");
        out.println("  2. Network topology and training");
        out.println("  3. Accuracy, sensitivity, and specificity stats");
        out.println("  4. Test your own input (from a file)\n");

        NetworkSelection networkSelection = NetworkSelectionFactory.getInstance().getStandardSelections();

        if (networkSelection.shouldUseOptimizedDefaults()) {
            networkSelection.loadOptimizedDefaults();
        }
        else {
            // allow the user to select all parameters
            networkSelection.getUserSelectionForAll();
        }

        out.println("Neural network configuration:");
        out.println(networkSelection.toString());
    }
}
