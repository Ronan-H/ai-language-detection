package ie.gmit.sw;


import ie.gmit.sw.neural_network.phase.PhaseManager;
import ie.gmit.sw.neural_network.config.NetworkSelection;
import ie.gmit.sw.neural_network.config.NetworkSelectionFactory;

import java.io.IOException;

import static java.lang.System.out;

public class AILanguageDetection {
    public void go() throws IOException {
        // print the program header
        printHeader();

        NetworkSelection networkSelection = NetworkSelectionFactory.getInstance().getStandardSelections();
        if (networkSelection.shouldUseOptimizedDefaults()) {
            networkSelection.loadOptimizedDefaults();
        }
        else {
            // allow the user to select all parameters
            networkSelection.getUserSelectionForAll();
        }

        // print network configuration
        out.println(networkSelection.toString());

        PhaseManager networkManager = new PhaseManager(
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

    private void printHeader() {
        out.println();
        out.println(" =================================");
        out.println(" |     AI Language Detection     |");
        out.println(" |        By Ronan Hanley        |");
        out.println(" =================================\n");

        out.println("The flow of this application is as follows:");
        out.println("1. Network configuration by the user");
        out.println("2. Training data creation phase");
        out.println("3. Training phase");
        out.println("4. Validation phase");
        out.println("5. Prediction phase (of user input)\n");

        out.println("Network topology used:");
        out.println("  (CLI selectable options are written in square brackets, everything else is fixed):");
        out.println("Input format: [vectorSize] * [ngramLength] input vector of FP numbers in the range 0..1 (adds to 1),");
        out.println("              followed by a 235 length one-hot encoding to specify the sample's language,");
        out.println("                limiting language samples to [sampleLimit] samples per language");
        out.println("Input layer:  null (linear) activation, has bias, neurons matching the input format 1:1,                [dropout] applied");
        out.println("Hidden layer: tanh() activation,        has bias, [hiddenSize] neurons (based on the selected formula), [dropout] applied");
        out.println("Output layer: SoftMax() activation,      no bias, 235 neurons,                                          [dropout] applied");
        out.println("Training:     using 5-fold cross validation, for [numEpochs]\n");
    }
}
