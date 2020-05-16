package ie.gmit.sw;

import ie.gmit.sw.code_stubs.Utilities;
import ie.gmit.sw.language_distribution.PartitionedHashedLangDist;
import ie.gmit.sw.test.TestAIClassification;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.networks.BasicNetwork;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class NetworkValidation {
    private File samplesPath;
    private File nnPath;

    public NetworkValidation(String samplesPath, String nnPath) {
        this.samplesPath = new File(samplesPath);
        this.nnPath = new File(nnPath);
    }

    public void testAccuracy() throws IOException {
        System.out.println("== Accuracy Test ==");
        System.out.printf("Loading the nerual network from file: %s%n", nnPath.getName());
        BasicNetwork network = Utilities.loadNeuralNetwork("neural-network.nn");
        System.out.println("Testing accuracy...\n");

        BufferedReader in = new BufferedReader(new FileReader(samplesPath));
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
        System.out.println("Finished");
        System.out.printf("\tCorrect predictions: %d%n", correct);
        System.out.printf("\tTotal samples tested: %d%n", total);
        System.out.printf("\tAccuracy: %.2f%%%n%n", accuracy);
    }
}
