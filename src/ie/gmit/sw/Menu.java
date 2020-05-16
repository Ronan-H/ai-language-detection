package ie.gmit.sw;


import java.io.IOException;

import static java.lang.System.out;

public class Menu {
    public void go() throws IOException {
        String samplesPath = "./wili-2018-Small-11750-Edited.txt";
        String trainingDataPath = "./training-data.csv";
        String nnPath = "./neural-network.nn";

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

        // create data set using chosen parameters
        new TrainingDataCreator(
                networkSelection,
                samplesPath,
                trainingDataPath
        ).create();

        // train network using user parameters
        new NetworkTrainer(
                networkSelection,
                "./neural-network.nn"
        ).train();

        // test accuracy/sensitivity/specificity
        new NetworkValidation(networkSelection, samplesPath, nnPath).testAccuracy();

        // allow the user to specify their own language sample as input, from a file
        new NetworkPrediction(networkSelection, nnPath).allowUserInputPredictions();

        System.out.println("\nFinished. Exiting...\n");
    }
}
