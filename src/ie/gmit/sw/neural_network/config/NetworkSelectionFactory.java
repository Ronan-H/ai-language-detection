package ie.gmit.sw.neural_network.config;

public class NetworkSelectionFactory {
    private static NetworkSelectionFactory instance;

    private NetworkSelectionFactory() {}

    public static NetworkSelectionFactory getInstance() {
        if (instance == null) {
            instance = new NetworkSelectionFactory();
        }

        return instance;
    }

    public NetworkSelection getStandardSelections() {
        NetworkSelection networkSelection = new NetworkSelection();

        networkSelection.addSelection(
                "vectorSize",
                new Selection<>(
                "N-gram feature vector size",
                        "This decides the fixed space that n-gram frequency data will have to be \"squashed\" into.",
                        "A value too low could introduce too many collisions, while a value too high could overwhelm the network with input data.",
                        new Integer[] {128, 256, 512},
                        256
                )
        );

        networkSelection.addSelection(
                "ngramLength",
                new Selection<>(
                        "N-gram max length",
                        "This is the size of the \"window\" that is passed over the input data, in characters.\n\tAll n-gram lengths up to n will be used as input (I.e. selecting 2 will store n-grams of size 1 AND 2 as input).",
                        "A value too low might not provide the network with enough information, while a value too high could overwhelm the network with input data.",
                        new Integer[] {1, 2, 3},
                        2
                )
        );

        networkSelection.addSelection(
                "sampleLimit",
                new Selection<>(
                        "Language data sample limit",
                        "Since some languages have more samples than others, limiting samples may help give each language a more equal representation.",
                        "Limiting the samples too much may not provide the network with enough data to make good predictions.",
                        new Integer[] {30, 40, 50, 999},
                        999
                )
        );

        networkSelection.addSelection(
                "numEpochs",
                new Selection<>(
                        "Number of epochs to run",
                        "One \"epoch\" means on full pass over the data during training.",
                        "Running for too few epochs won't give the neural net enough training, while running for too many could cause overfitting.",
                        new Integer[] {10, 13, 15, 20},
                        13
                )
        );

        networkSelection.addSelection(
                "dropout",
                new Selection<>(
                        "Dropout proportion",
                        "\"Dropout\" causes the network to ignore random neurons during training.",
                        "A higher dropout value could help prevent overfitting. Too much dropout, however, could deprive the network of enough data to make good predictions.",
                        new Double[] {0.0, 0.25, 0.5, 0.75, 0.9},
                        0.9
                )
        );

        networkSelection.addSelection(
                "hiddenSize",
                new Selection<>(
                        "Hidden layer size formula",
                        "Hidden layers are the layers between the input and output layer in a feedforward neural network.\n\tFor this network, I have found that using more than one layer is not practical, so only one hidden layer will be used.",
                        "Too few neurons may not provide the network with enough resources to make good predictions, while using too many might now allow the network to be trained fully with the limited data.",
                        new String[] {
                                "input + output",
                                "(input + output) / 2",
                                "(input + output) / 4",
                                "(input * 0.66) + output",
                                "sqrt(input * output)"
                        },
                        "(input + output) / 4"
                )
        );

        return networkSelection;
    }
}
