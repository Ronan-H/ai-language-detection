package ie.gmit.sw;

import java.util.ArrayList;
import java.util.List;

public class NetworkSelectionFactory {
    private NetworkSelectionFactory instance;

    private NetworkSelectionFactory() {}

    public NetworkSelectionFactory getInstance() {
        if (instance == null) {
            instance = new NetworkSelectionFactory();
        }

        return instance;
    }

    public NetworkSelection getStandardSelections() {
        NetworkSelection networkSelection = new NetworkSelection();

        networkSelection.addSelection("vectorSize",
                new Selection<>(
                "Choose the size of the vector to use for feature hashing. A value too low will introduce too many collisions, and a value too high will overwhelm the network with input data.",
                new Integer[] {128, 256, 512},
                256
                )
        );

        networkSelection.addSelection("ngramLength",
                new Selection<>(
                        "Choose the max length of n-gram to use for feature hashing. All n-gram lengths up to n will be used as input.",
                        new Integer[] {1, 2, 3},
                        2
                )
        );

        networkSelection.addSelection("sampleLimit",
                new Selection<>(
                        "Choose a limit on the number of samples to use for each language. Limiting samples may help give each language a more equal representation, since some languages have more samples than others.",
                        new Integer[] {30, 40, 50, 999},
                        999
                )
        );

        networkSelection.addSelection("numEpochs",
                new Selection<>(
                        "Choose the number of epochs to train for. Too few epochs won't give the neural net enough training, while too many will cause overfitting.",
                        new Integer[] {10, 15, 20, 25},
                        15
                )
        );

        networkSelection.addSelection("dropout",
                new Selection<>(
                        "Choose the amount of dropout to use between each layer. A higher dropout value could help prevent overfitting.",
                        new Double[] {0.0, 0.25, 0.5, 0.75, 0.9},
                        0.9
                )
        );

        networkSelection.addSelection("hiddenSize",
                new Selection<>(
                        "Choose a formula to use to compute the number of neurons to use in the hidden layer. Too few neurons may not provide the network with enough resources to make good predictions, while using too many might now allow the network to be trained fully with the limited data.",
                        new String[] {
                                "input + output",
                                "(input + output) / 2",
                                "(input * 0.66) + out",
                                "sqrt(input * output)"
                        },
                        "input + output"
                )
        );

        return networkSelection;
    }
}
