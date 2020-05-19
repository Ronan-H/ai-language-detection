package ie.gmit.sw.neural_network.phase;

import ie.gmit.sw.language.Lang;
import ie.gmit.sw.neural_network.config.NetworkSelection;
import org.encog.Encog;
import org.encog.engine.network.activation.ActivationSoftMax;
import org.encog.engine.network.activation.ActivationTANH;
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
import org.encog.persist.EncogDirectoryPersistence;
import org.encog.util.csv.CSVFormat;

import java.io.File;

import static java.lang.System.out;

// the phase of training a neural network
public class TrainingPhase extends NetworkPhase {
    private File savePath;
    private int inputs;
    private int outputs;

    public TrainingPhase(NetworkSelection networkSelection, String savePath) {
        super(networkSelection);
        this.savePath = new File(savePath);

        // compute number of inputs and outputs for use in the hidden layer formula
        int vectorSize = (Integer) getSelectionChoice("vectorSize");
        int ngramLength = (Integer) getSelectionChoice("ngramLength");

        inputs = vectorSize * ngramLength;
        outputs = Lang.values().length - 1;
    }

    public void train() {
        executePhase();
        onPhaseFinished();
    }

    @Override
    public void executePhase()  {
        out.println("== Training phase ==");
        out.println("Loading parameters...");
        int numEpochs = (Integer) getSelectionChoice("numEpochs");
        double dropout = (Double) getSelectionChoice("dropout");

        out.println("Computing hidden layer size:");
        int hiddenSize = computeHiddenLayerSize();

        out.println("Building the neural network topology...\n");
        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(null, true, inputs, dropout));
        network.addLayer(new BasicLayer(new ActivationTANH(), true, hiddenSize, dropout));
        network.addLayer(new BasicLayer(new ActivationSoftMax(), false, outputs, dropout));
        network.getStructure().finalizeStructure();
        network.reset();

        out.println("Loading the training data...\n");
        DataSetCODEC dsc = new CSVDataCODEC(
                new File("training-data.csv"),
                CSVFormat.ENGLISH,
                false,
                inputs,
                outputs,
                false
        );
        MemoryDataLoader mdl = new MemoryDataLoader(dsc);
        MLDataSet trainingSet = mdl.external2Memory();
        FoldedDataSet folded = new FoldedDataSet(trainingSet);

        ResilientPropagation train = new ResilientPropagation(network, folded);
        train.setDroupoutRate(dropout);
        train.setErrorFunction(new CrossEntropyErrorFunction());

        CrossValidationKFold cv = new CrossValidationKFold(train, 5);

        out.println("Training...");
        // record the start time
        long startTime = System.currentTimeMillis();
        // train the neural network
        for (int epoch = 1; epoch <= numEpochs; epoch++) {
            out.printf("\tEpoch %2d ... ", epoch);
            cv.iteration();
            out.println(" Error: " + cv.getError());
        }

        cv.finishTraining();

        // timer calculations (timer stops here)
        long timeTaken = System.currentTimeMillis() - startTime;
        double seconds = (double) timeTaken / 1000;
        int minutes = (int) Math.floor(seconds / 60);
        double remSeconds = seconds - (minutes * 60);

        // print the training time
        out.printf("\nFinished training. Time taken: %dm %.2fs%n%n", minutes, remSeconds);
        Encog.getInstance().shutdown();

        out.printf("Saving the model to file: %s%n", savePath.getName());
        EncogDirectoryPersistence.saveObject(savePath, network);

        out.println("Finished training the model.\n");
    }

    // compute the size of the hidden layer for the network, based on the formula that the user chose
    private int computeHiddenLayerSize() {
        // show the expression that the user chose, along with the subbed-in values
        String expression = (String) getSelectionChoice("hiddenSize");
        String subbedExpression = expression.replace("input", Integer.toString(inputs))
                                            .replace("output", Integer.toString(outputs));
        out.printf("\t%s = %s = ", expression, subbedExpression);

        // translate the String expression to a programmatical expression
        int result;
        switch (expression) {
            case "input + output":
                result = inputs + outputs;
                break;
            case "(input + output) / 2":
                result =  (inputs + outputs) / 2;
                break;
            case "(input + output) / 4":
                result =  (inputs + outputs) / 4;
                break;
            case "(input * 0.66) + output":
                result =  (int) ((inputs * 0.66) + outputs);
                break;
            case "sqrt(input * output)":
                result =  (int) Math.sqrt(inputs * outputs);
                break;
            default:
                // selected option is invalid...this indicates a compile time bug, this should never happen
                result = -1;
        }

        out.println(result);
        return result;
    }
}
