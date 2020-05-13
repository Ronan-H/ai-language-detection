package ie.gmit.sw.test;

import ie.gmit.sw.Lang;
import ie.gmit.sw.code_stubs.Utilities;
import org.encog.Encog;
import org.encog.engine.network.activation.ActivationReLU;
import org.encog.engine.network.activation.ActivationSigmoid;
import org.encog.engine.network.activation.ActivationSoftMax;
import org.encog.ml.data.MLDataSet;
import org.encog.ml.data.buffer.MemoryDataLoader;
import org.encog.ml.data.buffer.codec.CSVDataCODEC;
import org.encog.ml.data.buffer.codec.DataSetCODEC;
import org.encog.ml.data.folded.FoldedDataSet;
import org.encog.ml.train.MLTrain;
import org.encog.neural.error.CrossEntropyErrorFunction;
import org.encog.neural.error.ErrorFunction;
import org.encog.neural.error.LinearErrorFunction;
import org.encog.neural.networks.BasicNetwork;
import org.encog.neural.networks.layers.BasicLayer;
import org.encog.neural.networks.training.cross.CrossValidationKFold;
import org.encog.neural.networks.training.propagation.back.Backpropagation;
import org.encog.neural.networks.training.propagation.resilient.ResilientPropagation;
import org.encog.util.csv.CSVFormat;

import java.io.File;

public class TestTrainNetwork {
    public static void main(String[] args) {
        int epochs = 10;

        int inputs = TestAIClassification.HASH_RANGE * TestAIClassification.K;
        int outputs = Lang.values().length - 1;

        System.out.println("Building the neural network...\n");

        //Configure the neural network topology.
        BasicNetwork network = new BasicNetwork();
        network.addLayer(new BasicLayer(null, true, inputs));
        //network.addLayer(new BasicLayer(new ActivationSigmoid(), true, inputs + outputs));
        network.addLayer(new BasicLayer(new ActivationSigmoid(), true, inputs));
        //network.addLayer(new BasicLayer(new ActivationSigmoid(), true, 64));
        network.addLayer(new BasicLayer(new ActivationSoftMax(), false, outputs));

        network.getStructure().finalizeStructure();
        network.reset();

        System.out.println("== Training ==");
        //Read the CSV file "data.csv" into memory. Encog expects your CSV file to have input + output number of columns.
        DataSetCODEC dsc = new CSVDataCODEC(
                new File("training-data.csv"),
                CSVFormat.DECIMAL_POINT,
                false,
                inputs,
                outputs,
                false
        );
        MemoryDataLoader mdl = new MemoryDataLoader(dsc);
        MLDataSet trainingSet = mdl.external2Memory();

        FoldedDataSet folded = new FoldedDataSet(trainingSet);
        MLTrain train = new ResilientPropagation(network, folded);
        CrossValidationKFold cv = new CrossValidationKFold(train, 5);
        ((ResilientPropagation) train).setDroupoutRate(0.2);
        ((ResilientPropagation) train).setErrorFunction(new CrossEntropyErrorFunction());
        //((ResilientPropagation) train).setErrorFunction(new CrossEntropyErrorFunction());


        //Train the neural network
        for (int epoch = 1; epoch <= epochs; epoch++) {
            System.out.printf("Epoch %2d ... ", epoch);
            cv.iteration();
            System.out.println(" Error: " + cv.getError());
        }

        cv.finishTraining();

        System.out.println("\nFinished training.");

        Encog.getInstance().shutdown();

        System.out.println("Saving the model to a file...");
        Utilities.saveNeuralNetwork(network, "./neural-network.nn");

        System.out.println("Finished.");
    }
}
