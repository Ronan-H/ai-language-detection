package ie.gmit.sw;

import ie.gmit.sw.code_stubs.Utilities;
import ie.gmit.sw.language_distribution.PartitionedHashedLangDist;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.networks.BasicNetwork;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;
import java.util.regex.Pattern;

public class NetworkValidation {
    private NetworkSelection networkSelection;
    private File samplesPath;
    private File nnPath;

    public NetworkValidation(NetworkSelection networkSelection, String samplesPath, String nnPath) {
        this.networkSelection = networkSelection;
        this.samplesPath = new File(samplesPath);
        this.nnPath = new File(nnPath);
    }

    public void testAccuracy() throws IOException {
        System.out.println("== Validation ==");
        System.out.println("Loading parameters...");
        int vectorSize = (Integer) networkSelection.getSelectionChoice("vectorSize");
        int ngramLength = (Integer) networkSelection.getSelectionChoice("ngramLength");
        int sampleLimit = (Integer) networkSelection.getSelectionChoice("sampleLimit");

        System.out.printf("Loading the nerual network from file: %s%n", nnPath.getName());
        BasicNetwork network = Utilities.loadNeuralNetwork("neural-network.nn");
        System.out.println("Generating validation statistics...\n");

        BufferedReader in = new BufferedReader(new FileReader(samplesPath));
        String line;
        int total = 0;
        int correct = 0;

        Map<Lang, LangStats> langStats = new HashMap<>();
        for (int i = 0; i < Lang.values().length - 1; i++) {
            Lang l = Lang.values()[i];
            langStats.put(l, new LangStats(l));
        }

        List<String[]> samples = new TrainingDataProcessor(samplesPath).getSamples(sampleLimit);
        for (String[] sample : samples) {
            String sampleText = sample[0];
            Lang lang = Lang.valueOf(sample[1]);

            PartitionedHashedLangDist dist = new PartitionedHashedLangDist(
                    Lang.Unidentified,
                    vectorSize,
                    ngramLength
            );

            dist.recordSample(sampleText);
            MLData probs = new BasicMLData(dist.getFrequencies());
            Lang classification = Lang.values()[network.classify(probs)];

            if (classification == lang) {
                correct++;
                langStats.get(lang).recordTruePositive();
            }
            else {
                langStats.get(lang).recordFalePositive();
            }

            total++;
        }

        double accuracy = (correct / (double) total) * 100.0;

        LangStats[] sortedStats = langStats.values().toArray(new LangStats[0]);
        Arrays.sort(sortedStats);
        System.out.println("Prediction precision breakdown:");
        for (LangStats stats : sortedStats) {
            System.out.println(stats);
        }

        System.out.printf("\n\tCorrect predictions: %d%n", correct);
        System.out.printf("\tTotal samples tested: %d%n", total);
        System.out.printf("\tAccuracy: %.2f%%%n%n", accuracy);
    }
}
