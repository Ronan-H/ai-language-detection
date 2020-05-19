package ie.gmit.sw.neural_network.phase;

import ie.gmit.sw.language.Lang;

import java.io.*;
import java.util.*;
import java.util.regex.Pattern;

// reads sample files and performs some simple pre-processing
public class SampleFileReader {
    private File inputFile;
    private Pattern toRemove;

    public SampleFileReader(File inputFile) {
        this.inputFile = inputFile;
        // matches text in rounded brackets (like this)
        // and also reference numbers, like this: [123]
        // These are common on wikipedia and are better off being ignored
        toRemove = Pattern.compile("\\s*\\((.*)\\)|\\[[0-9]*\\]\\s*");
    }

    // some simple pre-processing techniques
    private String preprocessSample(String sample) {
        // trim out leading/trailing whitespace
        String s = sample.trim();
        // convert to lowercase, so that all samples are in the same case
        s = s.toLowerCase();
        // remove unhelpful patterns of wikipedia text
        s = toRemove.matcher(s).replaceAll("");
        return s;
    }

    // reads a file of language sample data, where each line is of the format sample@LanguageHere
    // each element in the list returned is a String array of length 2, containing the above fields
    public List<String[]> getSamples(int sampleLimit) throws IOException {
        List<String[]> samples = new ArrayList<>();

        // count how many occurrences of each language there are
        // (in case the user specified a limit)
        Map<Lang, Integer> sampleCounts = new HashMap<>();
        Arrays.stream(Lang.values()).forEach(l -> sampleCounts.put(l, 0));

        BufferedReader in = new BufferedReader(new FileReader(inputFile));
        String line;
        // read file line by line
        while ((line = in.readLine()) != null) {
            // trim off any whitespace
            line = line.trim();
            // locate the last @ character (some samples contain an @ character in the sample text!)
            int atPos = line.lastIndexOf("@");
            Lang lang = Lang.valueOf(line.substring(atPos + 1));

            // take the sample from the line and pre-process it
            String sample = preprocessSample(line.substring(0, atPos));
            if (sampleCounts.get(lang) >= sampleLimit // stop recording samples if the limit is hit for that lang
             || sample.length() < 125) { // ignore small samples (may be small because of the pre-processing...)
                continue;
            }

            samples.add(new String[] {sample, lang.name()});
            sampleCounts.put(lang, sampleCounts.get(lang) + 1);
        }
        in.close();

        return samples;
    }

    // reads a file containing samples (lines) of text from an unknown language
    public List<String> getUnknownSamples() throws IOException {
        List<String> samples = new ArrayList<>();

        BufferedReader in = new BufferedReader(new FileReader(inputFile));
        String line;
        // read file line by line
        while ((line = in.readLine()) != null) {
            // pre-process the line so it matches the format of the training data
            samples.add(preprocessSample(line));
        }
        in.close();

        return samples;
    }
}
