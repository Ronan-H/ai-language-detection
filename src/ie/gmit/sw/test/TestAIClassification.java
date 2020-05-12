package ie.gmit.sw.test;

import ie.gmit.sw.Lang;
import ie.gmit.sw.code_stubs.Utilities;
import ie.gmit.sw.language_distribution.HashedLangDist;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.networks.BasicNetwork;

public class TestAIClassification {
    public static int HASH_RANGE = 512;
    public static int K = 3;

    public static void main(String[] args) {
        String sampleString = "this is a classification test for the network";

        System.out.printf("Creating a vector hash for \"%s\"...%n", sampleString);
        HashedLangDist dist = new HashedLangDist(Lang.Unidentified, HASH_RANGE);
        dist.recordSample(sampleString, K);
        double[] hashVector = dist.getFrequencies();

        System.out.println("Loading the neural network...");
        BasicNetwork network = Utilities.loadNeuralNetwork("neural-network.nn");
        MLData sample = new BasicMLData(hashVector);
        System.out.println("Classifying...\n");
        Lang classification = Lang.values()[network.classify(sample)];

        System.out.println("Probabilities:");
        double[] outputVector = network.compute(sample).getData();
        for (int i = 0; i < outputVector.length; i++) {
            System.out.printf("\t%-27s -- %.3f%n", Lang.values()[i].getLanguageName() + ": ", outputVector[i]);
        }

        System.out.printf("%n%nClassification: %s%n", classification.getLanguageName());
    }
}
