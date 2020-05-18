package ie.gmit.sw.neural_network;

import ie.gmit.sw.language.Lang;
import ie.gmit.sw.language.LangDist;
import ie.gmit.sw.language.PartitionedLangDist;
import ie.gmit.sw.neural_network.config.NetworkSelection;

import java.io.*;
import java.util.*;

public class TrainingDataCreator extends NetworkStep {
    private File samplesPath;
    private File outPath;

    public TrainingDataCreator(NetworkSelection networkSelection, String samplesPath, String outPath) {
        super(networkSelection);
        this.samplesPath = new File(samplesPath);
        this.outPath = new File(outPath);
    }

    @Override
    public void executeStep() throws IOException {
        System.out.println("== Training data set creation ==");
        System.out.println("Loading parameters...");
        int vectorSize = (Integer) getSelectionChoice("vectorSize");
        int ngramLength = (Integer) getSelectionChoice("ngramLength");
        int sampleLimit = (Integer) getSelectionChoice("sampleLimit");

        System.out.printf("Creating hash vectors from samples in input file: %s%n", samplesPath.getName());

        List<String[]> samples = new TrainingDataProcessor(samplesPath).getSamples(sampleLimit);
        List<PartitionedLangDist> dists = new ArrayList<>();
        for (String[] sample : samples) {
            String sampleText = sample[0];
            Lang lang = Lang.valueOf(sample[1]);

            PartitionedLangDist dist = new PartitionedLangDist(
                    lang,
                    vectorSize,
                    ngramLength
            );
            dist.recordSample(sampleText);
            dists.add(dist);
        }

        System.out.printf("Writing vectorized training data to file: %s%n", outPath.getName());

        try {
            writeToFile(dists);
        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Finished creating training data.\n");
    }

    public void create() throws IOException {
        executeStep();
    }

    private void writeToFile(List<PartitionedLangDist> dists) throws IOException {
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outPath)));

        for (LangDist dist : dists) {
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
