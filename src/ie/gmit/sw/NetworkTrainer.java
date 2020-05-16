package ie.gmit.sw;

import ie.gmit.sw.code_stubs.Utilities;
import ie.gmit.sw.test.TestAIClassification;
import org.encog.Encog;
import org.encog.engine.network.activation.ActivationReLU;
import org.encog.engine.network.activation.ActivationSoftMax;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.buffer.MemoryDataLoader;
import org.encog.ml.data.buffer.codec.CSVDataCODEC;
import org.encog.ml.data.buffer.codec.DataSetCODEC;
import org.encog.ml.data.folded.FoldedDataSet;
import org.encog.neural.error.CrossEntropyErrorFunction;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.cross.CrossValidationKFold;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.csv.CSVFormat;

import java.io.File;

public class NetworkTrainer {
    private NetworkSelection networkSelection;
    private File savePath;
    private int numInputs;
    private int numOutputs;

    public NetworkTrainer(NetworkSelection networkSelection, String savePath) {
        this.networkSelection = networkSelection;
        this.savePath = new File(savePath);

        int vectorSize = (Integer) networkSelection.getSelectionChoice("vectorSize");
        int ngramLength = (Integer) networkSelection.getSelectionChoice("ngramLength");

        numInputs = vectorSize * ngramLength;
        numOutputs = Lang.values().length - 1;
    }

    public void train() {
        System.out.println("== Training ==");
        System.out.println("Loading parameters...");
        int numEpochs = (Integer) networkSelection.getSelectionChoice("numEpochs");
        double dropout = (Double) networkSelection.getSelectionChoice("dropout");

        System.out.println("Computing hidden layer size:");
        int hiddenSize = computeHiddenLayerSize();

        System.out.println("Building the neural network topology...\n");

        //Configure the neural network topology.
        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(null, true, numInputs, dropout));
        network.addLayer(new BasicLayer(new ActivationReLU(), true, hiddenSize, dropout));
        network.addLayer(new BasicLayer(new ActivationSoftMax(), false, numOutputs));
        network.getStructure().finalizeStructure();
        network.reset();

        System.out.println("Loading the training data...\n");
        //Read the CSV file "data.csv" into memory. Encog expects your CSV file to have input + output number of columns.
        DataSetCODEC dsc = new CSVDataCODEC(
                new File("training-data.csv"),
                CSVFormat.ENGLISH,
                false,
                numInputs,
                numOutputs,
                false
        );
        MemoryDataLoader mdl = new MemoryDataLoader(dsc);
        MLDataSet trainingSet = mdl.external2Memory();

        FoldedDataSet folded = new FoldedDataSet(trainingSet);
        ResilientPropagation train = new ResilientPropagation(network, folded);
        train.setDroupoutRate(dropout);
        train.setErrorFunction(new CrossEntropyErrorFunction());

        CrossValidationKFold cv = new CrossValidationKFold(train, 5);

        System.out.println("Training...");
        //Train the neural network
        for (int epoch = 1; epoch <= numEpochs; epoch++) {
            System.out.printf("\tEpoch %2d ... ", epoch);
            cv.iteration();
            System.out.println(" Error: " + cv.getError());
        }

        cv.finishTraining();
        System.out.println("\nFinished training.");
        Encog.getInstance().shutdown();
        System.out.printf("Saving the model to file: %s%n", savePath.getName());
        Utilities.saveNeuralNetwork(network, savePath.getPath());
        System.out.println("Finished training the model.");
    }

    private int computeHiddenLayerSize() {
        String expression = (String) networkSelection.getSelectionChoice("hiddenSize");
        String subbedExpression = expression.replace("input", Integer.toString(numInputs))
                                            .replace("output", Integer.toString(numOutputs));
        System.out.printf("\t%s = %s = ", expression, subbedExpression);
        int result;

        switch (expression) {
            case "input + output":
                result = numInputs + numOutputs;
                break;
            case "(input + output) / 2":
                result =  (numInputs + numOutputs) / 2;
                break;
            case "(input * 0.66) + output":
                result =  (int) ((numInputs * 0.66) + numOutputs);
                break;
            case "sqrt(input * output)":
                result =  (int) Math.sqrt(numInputs * numOutputs);
                break;
            default:
                // selected option is invalid...this indicated a compile time bug
                result = -1;
        }

        System.out.println(result);
        return result;
    }
}
