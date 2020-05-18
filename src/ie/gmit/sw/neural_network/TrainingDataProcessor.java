package ie.gmit.sw.neural_network;

import ie.gmit.sw.language.Lang;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

public class TrainingDataProcessor {
    private File inputFile;
    private Pattern toRemove;

    public TrainingDataProcessor(File inputFile) {
        this.inputFile = inputFile;
        toRemove = Pattern.compile("\\s*\\((.*)\\)|\\[[0-9]*\\]\\s*");
    }

    public String preprocessSample(String sample) {
        String s = sample.trim();
        s = s.toLowerCase();
        s = toRemove.matcher(s).replaceAll("");
        return s;
    }

    public List<String[]> getSamples(int sampleLimit) throws IOException {
        Map<Lang, Integer> sampleCounts = new HashMap<>();
        Arrays.stream(Lang.values()).forEach(l -> sampleCounts.put(l, 0));

        BufferedReader in = new BufferedReader(new FileReader(inputFile));
        String line;
        List<String[]> samples = new ArrayList<>();
        // read file line by line
        while ((line = in.readLine()) != null) {
            line = line.trim();
            int atPos = line.lastIndexOf("@");
            Lang lang = Lang.valueOf(line.substring(atPos + 1));

            String sample = preprocessSample(line.substring(0, atPos));
            if (sampleCounts.get(lang) >= sampleLimit
             || sample.length() < 100) {
                continue;
            }

            samples.add(new String[] {sample, lang.name()});
            sampleCounts.put(lang, sampleCounts.get(lang) + 1);
        }
        in.close();

        return samples;
    }

    public List<String> getUnknownSamples() throws IOException {
        BufferedReader in = new BufferedReader(new FileReader(inputFile));
        String line;
        List<String> samples = new ArrayList<>();
        // read file line by line
        while ((line = in.readLine()) != null) {
            samples.add(preprocessSample(line));
        }
        in.close();

        return samples;
    }
}
