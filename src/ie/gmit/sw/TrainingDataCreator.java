package ie.gmit.sw;

import ie.gmit.sw.language_distribution.HashedLangDist;
import ie.gmit.sw.language_distribution.PartitionedHashedLangDist;
import ie.gmit.sw.test.TestAIClassification;

import java.io.*;
import java.util.*;

public class TrainingDataCreator {
    private NetworkSelection networkSelection;
    private File samplesPath;
    private File outPath;

    public TrainingDataCreator(NetworkSelection networkSelection, String samplesPath, String outPath) {
        this.networkSelection = networkSelection;
        this.samplesPath = new File(samplesPath);
        this.outPath = new File(outPath);
    }

    public void create() throws IOException {
        System.out.println("== Training data set creation ==");
        System.out.println("Loading parameters...");
        int vectorSize = (Integer) networkSelection.getSelectionChoice("vectorSize");
        int ngramLength = (Integer) networkSelection.getSelectionChoice("ngramLength");
        int sampleLimit = (Integer) networkSelection.getSelectionChoice("sampleLimit");

        System.out.printf("Creating hash vectors from samples in input file: %s%n", samplesPath.getName());

        List<String[]> samples = new TrainingDataProcessor(samplesPath).getSamples(sampleLimit);
        List<PartitionedHashedLangDist> dists = new ArrayList<>();
        for (String[] sample : samples) {
            String sampleText = sample[0];
            Lang lang = Lang.valueOf(sample[1]);

            PartitionedHashedLangDist dist = new PartitionedHashedLangDist(
                    lang,
                    vectorSize,
                    ngramLength
            );
            dist.recordSample(sampleText);
            dists.add(dist);
        }

        System.out.printf("Writing vectorized training data to file: %s%n", outPath.getName());

        try {
            writeToFile("./training-data.csv", dists, TestAIClassification.HASH_RANGE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Finished creating training data.\n");
    }

    public static void writeToFile(String filePath, List<PartitionedHashedLangDist> dists, int hashRange) throws IOException {
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filePath)));

        for (HashedLangDist dist : dists) {
            double[] normalizedFreqs = dist.getFrequencies();

            // write hash vector values
            for (double normalizedFreq : normalizedFreqs) {
                // truncate to 5 decimal places to save disk space
                out.printf("%.5f,", normalizedFreq);
            }

            // write language vector
            Lang[] langs = Lang.values();
            for (int i = 0; i < langs.length - 1; i++) {
                Lang l = langs[i];

                out.print(l == dist.getLang() ? "1" : "0");
                out.print(i == langs.length - 2 ? "\n" : ",");
            }
        }

        // close the file
        out.close();
    }
}
