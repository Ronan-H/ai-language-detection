package ie.gmit.sw.sample_parser;

import ie.gmit.sw.language_distribution.LangDistStore;

import java.io.*;

/**
 * Allows the parsing of language sample lines from a file into a language distribution store.
 */
public class FileSampleParser extends SampleParser {
    private File file;

    /**
     * Creates a new file parser at the given path.
     *
     * @param file File to parse.
     */
    public FileSampleParser(File file) {
        this.file = file;
    }

    /**
     * Parses all lines of the file into a given language distribution store.
     * Expects each line to have a language sample, followed by an "@" symbol,
     * followed by the language of the sample.
     *
     * @param store Store to record the resulting k-mers into.
     */
    @Override
    public void parseAll(LangDistStore store) {
        try {
            BufferedReader in = new BufferedReader(new FileReader(file));
            String line;

            // read file line by line
            while ((line = in.readLine()) != null) {
                // split language sample and name
                String[] parts = line.trim().split("@");
                if (parts.length == 2) {
                    parseSample(parts[1], parts[0], store);
                }
            }

            in.close();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }
}
