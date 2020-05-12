package ie.gmit.sw.language_distribution;

import ie.gmit.sw.Lang;

import java.io.IOException;
import java.util.Set;

/**
 * Represents a store of language distributions (LangDist objects) which are accessible
 * through a set of Lang keys.
 */
public interface LangDistStore {
    /**
     * Finds the k-mer distribution belonging to a given language
     */
    LangDist getDistribution(Lang lang);

    /**
     * Retrieves the set of language keys
     */
    Set<Lang> getKeySet();

    /**
     * Get the length of k-mer that this store uses.
     */
    int getKmerLength();

    /**
     * Creates a new (empty) language distribution of the same type that this
     * store uses.
     */
    LangDist getNewDistOfSameType(Lang distLang);

    /**
     * Convenience method, assuming new language distributions created after the store are for
     * user queries.
     */
    default LangDist getNewDistOfSameType() {
        return getNewDistOfSameType(Lang.Unidentified);
    }
}
