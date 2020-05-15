package ie.gmit.sw.test;

import ie.gmit.sw.Lang;
import ie.gmit.sw.code_stubs.Utilities;
import ie.gmit.sw.language_distribution.PartitionedHashedLangDist;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.networks.BasicNetwork;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class TestNNAccuracy {
    public static void main(String[] args) throws IOException {
        File wili = new File("./wili-2018-Small-11750-Edited.txt");

        System.out.println("Loading the neural network...");
        BasicNetwork network = Utilities.loadNeuralNetwork("neural-network.nn");
        System.out.println("Testing accuracy...");

        BufferedReader in = new BufferedReader(new FileReader(wili));
        String line;
        int total = 0;
        int correct = 0;
        // read file line by line
        while ((line = in.readLine()) != null) {
            line = line.trim();
            int atPos = line.lastIndexOf("@");
            Lang lang = Lang.valueOf(line.substring(atPos + 1));

            PartitionedHashedLangDist dist = new PartitionedHashedLangDist(
                    Lang.Unidentified,
                    TestAIClassification.HASH_RANGE,
                    TestAIClassification.K
            );
            dist.recordSample(line);
            MLData sample = new BasicMLData(dist.getFrequencies());
            Lang classification = Lang.values()[network.classify(sample)];

            if (classification == lang) {
                correct++;
            }

            total++;
        }
        in.close();

        double accuracy = (correct / (double) total) * 100.0;
        System.out.printf("Finished. Accuracy: %.2f%%%n", accuracy);
    }
}
