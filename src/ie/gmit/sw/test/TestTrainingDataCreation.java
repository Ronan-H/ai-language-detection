package ie.gmit.sw.test;

import ie.gmit.sw.Lang;
import ie.gmit.sw.language_detector.CosineDistanceStrategy;
import ie.gmit.sw.language_detector.FreqDistanceStrategy;
import ie.gmit.sw.language_distribution.*;
import ie.gmit.sw.sample_parser.FileSampleParser;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class TestTrainingDataCreation {
    public static void main(String[] args) throws IOException {
        File wili = new File("./wili-2018-Small-11750-Edited.txt");

        System.out.println("Creating hash vectors from input file data...");

        LangDistStore distStore = new LangDistStoreBuilder()
                .withMappedStore(TestAIClassification.HASH_RANGE, TestAIClassification.K)
                .registerParser(
                        new FileSampleParser(wili)
                )
                .build();

        FreqDistanceStrategy strategy = new CosineDistanceStrategy();

        // -- using a hash vector for every sample --
        BufferedReader in = new BufferedReader(new FileReader(wili));
        List<NetworkInput> networkInputs = new ArrayList<>();
        String line;
        // read file line by line
        while ((line = in.readLine()) != null) {
            String[] parts = line.trim().split("@");
            if (parts.length == 2) {
                NetworkInput networkInput = new NetworkInput(Lang.valueOf(parts[1]));

                HashedLangDist hashedLangDist = new HashedLangDist(Lang.valueOf(parts[1]), TestAIClassification.HASH_RANGE);
                hashedLangDist.recordSample(parts[0].toLowerCase(), TestAIClassification.K);
                double[] langSims = new double[235];
                int lIndex = 0;
                for (Lang lang : Lang.values()) {
                    if (lang == Lang.Unidentified) break;
                    langSims[lIndex++] = strategy.getDistance(
                            distStore.getDistribution(lang).getFrequencies(),
                            hashedLangDist.getFrequencies()
                    );
                }

                networkInput.addInput(langSims);
                //networkInput.addInput(hashedLangDist.getFrequencies());
                networkInputs.add(networkInput);
            }
        }
        in.close();


        System.out.println("Writing training data to a file...");

        try {
            writeToFile("./training-data.csv", networkInputs);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Finished. Exiting...");
    }

    public static void writeToFile(String filePath, List<NetworkInput> networkInputs) throws IOException {
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filePath)));

        for (NetworkInput networkInput : networkInputs) {
            double[] normalizedFreqs = networkInput.asArray();

            // write hash vector values
            for (int i = 0; i < normalizedFreqs.length; i++) {
                // truncate to 5 decimal places to save disk space
                out.printf("%.5f,", normalizedFreqs[i]);
            }

            // write language vector
            Lang[] langs = Lang.values();
            for (int i = 0; i < langs.length - 1; i++) {
                Lang l = langs[i];

                out.print(l == networkInput.getLang() ? "1" : "0");
                out.print(i == langs.length - 2 ? "\n" : ",");
            }
        }

        // close the file
        out.close();
    }
}
