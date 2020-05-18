package ie.gmit.sw.neural_network.phase;

import ie.gmit.sw.language.Lang;
import ie.gmit.sw.language.LangStats;
import ie.gmit.sw.language.PartitionedLangDist;
import ie.gmit.sw.neural_network.config.NetworkSelection;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogDirectoryPersistence;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class ValidationPhase extends NetworkPhase {
    private File samplesPath;
    private File nnPath;

    public ValidationPhase(NetworkSelection networkSelection, String samplesPath, String nnPath) {
        super(networkSelection);
        this.samplesPath = new File(samplesPath);
        this.nnPath = new File(nnPath);
    }

    public void runTests() throws IOException {
        executePhase();
        onPhaseFinished();
    }

    @Override
    public void executePhase() throws IOException {
        System.out.println("== Validation ==");
        System.out.println("Loading parameters...");
        int vectorSize = (Integer) getSelectionChoice("vectorSize");
        int ngramLength = (Integer) getSelectionChoice("ngramLength");
        int sampleLimit = (Integer) getSelectionChoice("sampleLimit");

        System.out.printf("Loading the neural network from a file: %s%n", nnPath.getName());
        BasicNetwork network = (BasicNetwork) EncogDirectoryPersistence.loadObject(nnPath);

        System.out.println("Generating validation statistics...\n");
        int total = 0;
        int correct = 0;
        Map<Lang, LangStats> langStats = new HashMap<>();
        for (int i = 0; i < Lang.values().length - 1; i++) {
            Lang l = Lang.values()[i];
            langStats.put(l, new LangStats(l));
        }

        List<String[]> samples = new SampleFileReader(samplesPath).getSamples(sampleLimit);
        for (String[] sample : samples) {
            String sampleText = sample[0];
            Lang lang = Lang.valueOf(sample[1]);

            PartitionedLangDist dist = new PartitionedLangDist(
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
