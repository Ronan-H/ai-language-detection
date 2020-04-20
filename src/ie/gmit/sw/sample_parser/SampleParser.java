package ie.gmit.sw.sample_parser;

import ie.gmit.sw.Lang;
import ie.gmit.sw.language_distribution.LangDistStore;

/**
 * Represents some way of parsing sample language data (may be from a file,
 * web page, socket, etc).
 */
public abstract class SampleParser {
    /**
     * Parse all languages samples associated with this parser.
     *
     * @param store Store to record the resulting k-mers into.
     */
    public abstract void parseAll(LangDistStore store);

    /**
     * Parse a single piece of text into a given language distribution store.
     *
     * @param lang Language of the associated text (may be Unidentified).
     * @param text Text sample.
     * @param store Store to record the resulting k-mers into.
     */
    public void parseSample(String lang, String text, LangDistStore store) {
        Lang language = Lang.valueOf(lang);
        store.getDistribution(language).recordSample(text, store.getKmerLength());
    }
}
