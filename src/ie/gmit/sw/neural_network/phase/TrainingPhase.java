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
public class TrainingPhase extends NetworkPhase {
    private File savePath;
    private int inputs;
    private int outputs;

    public TrainingPhase(NetworkSelection networkSelection, String savePath) {
        super(networkSelection);
        this.savePath = new File(savePath);

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
        System.out.println("== Training phase ==");
        System.out.println("Loading parameters...");
        int numEpochs = (Integer) getSelectionChoice("numEpochs");
        double dropout = (Double) getSelectionChoice("dropout");

        System.out.println("Computing hidden layer size:");
        int hiddenSize = computeHiddenLayerSize();

        System.out.println("Building the neural network topology...\n");
        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(null, true, inputs, dropout));
        network.addLayer(new BasicLayer(new ActivationTANH(), true, hiddenSize, dropout));
        network.addLayer(new BasicLayer(new ActivationSoftMax(), false, outputs, dropout));
        network.getStructure().finalizeStructure();
        network.reset();

        System.out.println("Loading the training data...\n");
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

        System.out.println("Training...");
        long startTime = System.currentTimeMillis();
        //Train the neural network
        for (int epoch = 1; epoch <= numEpochs; epoch++) {
            System.out.printf("\tEpoch %2d ... ", epoch);
            cv.iteration();
            System.out.println(" Error: " + cv.getError());
        }

        cv.finishTraining();

        // timer calculations
        long timeTaken = System.currentTimeMillis() - startTime;
        double seconds = (double) timeTaken / 1000;
        int minutes = (int) Math.floor(seconds / 60);
        double remSeconds = seconds - (minutes * 60);

        System.out.printf("\nFinished training. Time taken: %dm %.2fs%n%n", minutes, remSeconds);
        Encog.getInstance().shutdown();

        System.out.printf("Saving the model to file: %s%n", savePath.getName());
        EncogDirectoryPersistence.saveObject(savePath, network);

        System.out.println("Finished training the model.\n");
    }

    private int computeHiddenLayerSize() {
        String expression = (String) getSelectionChoice("hiddenSize");
        String subbedExpression = expression.replace("input", Integer.toString(inputs))
                                            .replace("output", Integer.toString(outputs));
        System.out.printf("\t%s = %s = ", expression, subbedExpression);
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
                // selected option is invalid...this indicated a compile time bug
                result = -1;
        }

        System.out.println(result);
        return result;
    }
}
