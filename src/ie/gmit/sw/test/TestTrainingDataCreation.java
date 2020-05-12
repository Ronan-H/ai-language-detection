package ie.gmit.sw.test;

import ie.gmit.sw.Lang;
import ie.gmit.sw.code_stubs.Utilities;
import ie.gmit.sw.language_distribution.HashedLangDist;
import ie.gmit.sw.language_distribution.LangDistStore;
import ie.gmit.sw.language_distribution.LangDistStoreBuilder;
import ie.gmit.sw.sample_parser.FileSampleParser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TestTrainingDataCreation {
    public static void main(String[] args) throws IOException {
        File wili = new File("./wili-2018-Small-11750-Edited.txt");


        List<HashedLangDist> dists = new ArrayList<>();
        BufferedReader in = new BufferedReader(new FileReader(wili));
        String line;

        // read file line by line
        System.out.println("Creating hash vectors from input file data...");
        while ((line = in.readLine()) != null) {
            // split language sample and name
            String[] parts = line.trim().split("@");
            if (parts.length == 2) {
                HashedLangDist dist = new HashedLangDist(Lang.valueOf(parts[1]), TestAIClassification.HASH_RANGE);
                dist.recordSample(parts[0], TestAIClassification.K);
                dists.add(dist);
            }
        }


        System.out.println("Writing training data to a file...");

        try {
            writeToFile("./training-data.csv", dists, TestAIClassification.HASH_RANGE);
        } catch (IOException e) {
            e.printStackTrace();
        }

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

    public static void writeToFile(String filePath, List<HashedLangDist> dists, int hashRange) throws IOException {
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filePath)));


        for (HashedLangDist dist : dists) {
            // TODO dist.getFrequences() already normalizes between 0 and 1. Would that also work?
            //double[] normalizedFreqs = Utilities.normalize(dist.getFrequencies(), 0, 1);
            double[] normalizedFreqs = dist.getFrequencies();

            // write hash vector values
            for (int i = 0; i < hashRange; i++) {
                // truncate to 5 decimal places to save disk space
                out.printf("%.5f,", normalizedFreqs[i]);
            }

            // write language vector
            Lang[] langs = Lang.values();
            for (int i = 0; i < langs.length; i++) {
                Lang l = langs[i];
                out.print(l == dist.getLang() ? "1" : "0");
                out.print(i == langs.length - 1 ? "\n" : ",");
            }
        }

        // close the file
        out.close();
    }
}
