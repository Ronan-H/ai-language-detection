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

        Map<Lang, Integer> sampleCounts = new HashMap<>();
        Arrays.stream(Lang.values()).forEach(l -> sampleCounts.put(l, 0));

        // -- using a hash vector for every sample --
        BufferedReader in = new BufferedReader(new FileReader(samplesPath));
        List<PartitionedHashedLangDist> dists = new ArrayList<>();
        String line;
        // read file line by line
        while ((line = in.readLine()) != null) {
            line = line.trim();
            int atPos = line.lastIndexOf("@");
            Lang lang = Lang.valueOf(line.substring(atPos + 1));

            if (sampleCounts.get(lang) >= sampleLimit) {
                continue;
            }

            String sample = line.substring(0, atPos).toLowerCase();

            PartitionedHashedLangDist dist = new PartitionedHashedLangDist(
                    lang,
                    vectorSize,
                    ngramLength
            );
            dist.recordSample(sample);
            dists.add(dist);

            sampleCounts.put(lang, sampleCounts.get(lang) + 1);
        }
        in.close();

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
