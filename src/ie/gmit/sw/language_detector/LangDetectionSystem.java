package ie.gmit.sw.language_detector;

import ie.gmit.sw.Lang;
import ie.gmit.sw.language_detector.LangDetector;
import ie.gmit.sw.language_distribution.LangDist;
import ie.gmit.sw.language_distribution.LangDistStore;

/**
 * Calculates the closest language for a given sample, using the supplied
 * LangDetector and LangDistStore.
 */
public class LangDetectionSystem {
    private LangDistStore distStore;
    private LangDetector langDetector;

    /**
     * Constructs a LangDetectionSystem based on a LangDetector (algorithm) and LangDistStore (data structure).
     *
     * @param distStore Store of language distributions.
     * @param langDetector Language detector to use (contains an underlying strategy for detecting languages).
     */
    public LangDetectionSystem(LangDistStore distStore, LangDetector langDetector) {
        this.distStore = distStore;
        this.langDetector = langDetector;
    }

    public Lang findClosestLanguage(String id, String sampleText) {
        // create language distribution for the user's query and record the k-mer values to it
        LangDist testDist = distStore.getNewDistOfSameType();
        testDist.recordSample(sampleText, distStore.getKmerLength());

        // find the closest language from the store of known language distributions, using whichever
        // LangDetectorStrategy was given to this worker through the LangDetector
        return langDetector.findClosestLanguage(testDist, distStore);
    }
}
