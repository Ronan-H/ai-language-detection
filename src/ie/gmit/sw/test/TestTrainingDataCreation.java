package ie.gmit.sw.test;

import ie.gmit.sw.Lang;
import ie.gmit.sw.language_distribution.HashedLangDist;
import ie.gmit.sw.language_distribution.PartitionedHashedLangDist;

import java.io.*;
import java.util.*;

public class TestTrainingDataCreation {
    public static void main(String[] args) throws IOException {
        File wili = new File("./wili-2018-Small-11750-Edited.txt");

        System.out.println("Creating hash vectors from input file data...");

        int limit = Integer.MAX_VALUE;
        Map<Lang, Integer> sampleCounts = new HashMap<>();
        Arrays.stream(Lang.values()).forEach(l -> sampleCounts.put(l, 0));

        // -- using a hash vector for every sample --
        BufferedReader in = new BufferedReader(new FileReader(wili));
        List<PartitionedHashedLangDist> dists = new ArrayList<>();
        String line;
        // read file line by line
        while ((line = in.readLine()) != null) {
            line = line.trim();
            int atPos = line.lastIndexOf("@");
            Lang lang = Lang.valueOf(line.substring(atPos + 1));

            if (sampleCounts.get(lang) >= limit) {
                continue;
            }

            String sample = line.substring(0, atPos).toLowerCase();

            PartitionedHashedLangDist dist = new PartitionedHashedLangDist(
                    lang,
                    TestAIClassification.HASH_RANGE,
                    TestAIClassification.K
            );
            dist.recordSample(sample);
            dists.add(dist);

            sampleCounts.put(lang, sampleCounts.get(lang) + 1);
        }
        in.close();


        System.out.println("Writing training data to a file...");

        try {
            writeToFile("./training-data.csv", dists, TestAIClassification.HASH_RANGE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        // -- using a hash vector for all samples combined --
        /*
        // build k-mer distribution for all languages from language dataset
        LangDistStore distStore = new LangDistStoreBuilder()
                .withMappedStore(TestAIClassification.HASH_RANGE, TestAIClassification.K)
                .registerParser(
                        new FileSampleParser(wili)
                )
                .build();
        distStore.writeToFile("./training-data.csv");
        */

        System.out.println("Finished. Exiting...");
    }

    public static void writeToFile(String filePath, List<PartitionedHashedLangDist> dists, int hashRange) throws IOException {
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filePath)));

        for (HashedLangDist dist : dists) {
            double[] normalizedFreqs = dist.getFrequencies();

            // write hash vector values
            for (int i = 0; i < normalizedFreqs.length; i++) {
                // truncate to 5 decimal places to save disk space
                out.printf("%.5f,", normalizedFreqs[i]);
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
