package ie.gmit.sw.neural_network.phase;

import ie.gmit.sw.language.Lang;
import ie.gmit.sw.language.LangDist;
import ie.gmit.sw.language.PartitionedLangDist;
import ie.gmit.sw.neural_network.config.NetworkSelection;

import java.io.*;
import java.util.*;

import static java.lang.System.out;

// the phase of creating the training data vectors, based on input samples
public class InputVectorCreationPhase extends NetworkPhase {
    private File samplesPath;
    private File outPath;

    public InputVectorCreationPhase(NetworkSelection networkSelection, String samplesPath, String outPath) {
        super(networkSelection);
        this.samplesPath = new File(samplesPath);
        this.outPath = new File(outPath);
    }

    public void create() throws IOException {
        executePhase();
    }

    @Override
    public void executePhase() throws IOException {
        out.println("== Training data creation phase ==");
        out.println("Loading parameters...");
        int vectorSize = (Integer) getSelectionChoice("vectorSize");
        int ngramLength = (Integer) getSelectionChoice("ngramLength");
        int sampleLimit = (Integer) getSelectionChoice("sampleLimit");

        out.printf("Creating hash vectors from samples in input file: %s%n", samplesPath.getName());

        List<String[]> samples = new SampleFileReader(samplesPath).getSamples(sampleLimit);
        List<LangDist> dists = new ArrayList<>();
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

        out.printf("Writing vectorized training data to file: %s%n", outPath.getName());
        try {
            writeDistsToFile(dists);
        } catch (IOException e) {
            e.printStackTrace();
        }

        out.println("Finished creating the training data.\n");
    }

    // writes vectorized training data to a file
    private void writeDistsToFile(List<LangDist> dists) throws IOException {
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(outPath)));

        for (LangDist dist : dists) {
            // write hash vector values
            for (double normalizedFreq : dist.getFrequencies()) {
                // truncate to 5 decimal places to save disk space
                out.printf("%.5f,", normalizedFreq);
            }

            // write language "one-hot encoding" vector
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
