package ie.gmit.sw.language_detector;

import ie.gmit.sw.Lang;
import ie.gmit.sw.language_distribution.LangDist;
import ie.gmit.sw.language_distribution.LangDistStore;

import java.util.Set;

/**
 * A language detector strategy which compares language distributions using some distance value metric.
 */
public abstract class FreqDistanceStrategy implements LangDetectorStrategy {
    /**
     * Performs this strategy on an unidentified language distribution and a store of known language k-mers.
     *
     * @param unidentifiedLang K-mer distribution of the unidentified language sample.
     * @param store            K-mer distribution store for all known languages
     * @return Closest known language using this strategy.
     */
    @Override
    public Lang findClosestLanguage(LangDist unidentifiedLang, LangDistStore store) {
        double[] unidentifiedDist = unidentifiedLang.getFrequencies();
        double lowestDist = Double.MAX_VALUE;
        Lang bestFitLang = Lang.Unidentified;

        Set<Lang> keys = store.getKeySet();
        for (Lang key : keys) {
            LangDist ref = store.getDistribution(key);
            double[] refDist = ref.getFrequencies();
            double d = getDistance(unidentifiedDist, refDist);

            if (d < lowestDist) {
                lowestDist = d;
                bestFitLang = ref.getLang();
            }
        }

        return bestFitLang;
    }

    /**
     * Distance between two frequency distributions, where smaller values = more similar languages.
     *
     * @param distA Distribution frequency from a language distribution.
     * @param distB Distribution frequency from a language distribution.
     * @return Floating point value between 0 and 1, where smaller values indicate more similarity.
     */
    public abstract double getDistance(double[] distA, double[] distB);
}
