package ie.gmit.sw.language_detector;

import ie.gmit.sw.Lang;
import ie.gmit.sw.language_distribution.LangDist;
import ie.gmit.sw.language_distribution.LangDistStore;

public interface LangDetectorStrategy {
    /**
     * Find the closest language based on a k-mer distribution and a LangDistStore.
     *
     * @param unidentifiedLang K-mer distribution of unidentified language sample.
     * @param store Store of known language k-mer distributions.
     * @return Closest known language to the unknown language distribution, based on this strategy.
     */
    Lang findClosestLanguage(LangDist unidentifiedLang, LangDistStore store);
}
