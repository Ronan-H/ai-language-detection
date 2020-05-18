package ie.gmit.sw;


import java.io.IOException;

import static java.lang.System.out;

public class AILanguageDetection {
    public void go() throws IOException {
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
        out.println(networkSelection.toString());

        NetworkManager networkManager = new NetworkManager(
                networkSelection,
                "./wili-2018-Small-11750-Edited.txt",
                "./training-data.csv",
                "./neural-network.nn"
        );

        // create data set using chosen parameters
        networkManager.createTrainingData();

        // train network using user parameters
        networkManager.trainNetwork();

        // test accuracy/sensitivity/specificity
        networkManager.runValidationTests();

        // allow the user to specify their own language sample as input, from a file
        networkManager.allowUserInputPredictions();

        System.out.println("\nFinished. Exiting...\n");
    }
}
