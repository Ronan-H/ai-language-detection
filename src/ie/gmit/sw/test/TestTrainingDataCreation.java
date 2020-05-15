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

        Map<Lang, PartitionedHashedLangDist> distMap = new HashMap<>();
        Arrays.stream(Lang.values()).forEach(
                l -> distMap.put(l, new PartitionedHashedLangDist(
                        l,
                        TestAIClassification.HASH_RANGE,
                        TestAIClassification.K
                ))
        );

        // -- using a hash vector for every sample --
        BufferedReader in = new BufferedReader(new FileReader(wili));
        List<double[]> dists = new ArrayList<>();
        String line;
        // read file line by line
        while ((line = in.readLine()) != null) {
            line = line.trim();
            int atPos = line.lastIndexOf("@");
            Lang lang = Lang.valueOf(line.substring(atPos + 1));

            PartitionedHashedLangDist dist = distMap.get(lang);

            if (sampleCounts.get(lang) >= limit) {
                continue;
            }

            String sample = line.substring(0, atPos).toLowerCase();
            dist.recordSample(sample);
            dists.add(dist.getFrequencies());

            sampleCounts.put(lang, sampleCounts.get(lang) + 1);
        }
        in.close();


        System.out.println("Writing training data to a file...");

        try {
            writeToFile("./training-data.csv", dists, TestAIClassification.HASH_RANGE);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Finished. Exiting...");
    }

    public static void writeToFile(String filePath, List<double[]> dists, int hashRange) throws IOException {
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filePath)));

        for (double[] values : dists) {
            // write hash vector values
            for (int i = 0; i < values.length; i++) {
                // truncate to 5 decimal places to save disk space
                out.printf("%.5f", values[i]);
                if (i != values.length - 1) out.print(",");
            }
            out.println();
        }

        // close the file
        out.close();
    }
}
