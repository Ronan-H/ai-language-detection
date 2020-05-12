package ie.gmit.sw.test;

import ie.gmit.sw.Lang;
import ie.gmit.sw.code_stubs.Utilities;
import ie.gmit.sw.language_distribution.HashedLangDist;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.networks.BasicNetwork;

public class TestAIClassification {
    public static void main(String[] args) {
        String sampleString = "This is an English sentence.";

        System.out.printf("Creating a vector hash for \"%s\"...%n", sampleString);
        HashedLangDist dist = new HashedLangDist(Lang.Unidentified, 512);
        dist.recordKmer(sampleString.toCharArray());
        double[] hashVector = Utilities.normalize(dist.getFrequencies(), -1, 1);

        System.out.println("Loading the neural network...");
        BasicNetwork network = Utilities.loadNeuralNetwork("neural-network.nn");
        MLData sample = new BasicMLData(hashVector);
        System.out.println("Classifying...\n");
        Lang classification = Lang.values()[network.classify(sample)];

        System.out.printf("Classification: %s%n", classification.getLanguageName());
    }
}
