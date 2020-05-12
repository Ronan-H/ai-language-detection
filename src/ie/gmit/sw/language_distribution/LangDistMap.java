package ie.gmit.sw.language_distribution;

import ie.gmit.sw.Lang;
import ie.gmit.sw.code_stubs.Utilities;

import javax.rmi.CORBA.Util;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

/**
 * Represents a store of language distribution objects using a Map.
 */
public class LangDistMap implements LangDistStore {
    private int hashRange;
    private Map<Lang, LangDist> map;
    private int kmerLength;

    /**
     * Constructs a new language distribution map with a given hash range.
     * (the hash range is needed for the created language distribution objects, not this store)
     *
     * @param hashRange Hash range to use for this mapped store.
     */
    public LangDistMap(int hashRange, int kmerLength) {
        this.hashRange = hashRange;
        this.kmerLength = kmerLength;
        map = new HashMap<>();

        Lang[] allLangs = Lang.values();
        for (Lang lang : allLangs) {
            // skip Unidentified language (used in LanguageDistribution when the language isn't specified)
            if (lang != Lang.Unidentified) {
                map.put(lang, new HashedLangDist(lang, hashRange));
            }
        }
    }

    @Override
    public LangDist getDistribution(Lang lang) {
        return map.get(lang);
    }

    @Override
    public Set<Lang> getKeySet() {
        return map.keySet();
    }

    @Override
    public int getKmerLength() {
        return kmerLength;
    }

    @Override
    public LangDist getNewDistOfSameType(Lang distLang) {
        return new HashedLangDist(distLang, hashRange);
    }

    @Override
    public void writeToFile(String filePath) throws IOException {
        PrintWriter out = new PrintWriter(new BufferedWriter(new FileWriter(filePath)));

        // loop over all languages (this may not be in order, but that doesn't affect the training)
        for (Lang lang : getKeySet()) {
            LangDist dist = getDistribution(lang);
            // TODO dist.getFrequences() already normalizes between 0 and 1. Would that also work?
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
                out.print(l == lang ? "1" : "0");
                out.print(i == langs.length - 1 ? "\n" : ",");
            }
        }

        // close the file
        out.close();
    }
}
