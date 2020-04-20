package ie.gmit.sw.language_detector;

import ie.gmit.sw.Lang;
import ie.gmit.sw.language_distribution.LangDist;
import ie.gmit.sw.language_distribution.LangDistStore;

import java.util.Map;

/**
 * Used to detect an unknown language based on a LangDistStore. Encapsulates a map of LangDetectorStrategys, which
 * define ways that languages can be detected. The algorithm can be switched on the fly with a call to switchToStrategy().
 */
public class LangDetector {
    private Map<String, LangDetectorStrategy> strategies;
    private String strategy;

    /**
     * Creates a LangDetector with a map of strategies which it can use.
     *
     * @param strategies Map of strategies which this language detector can use.
     * @param defaultStrategy Default strategy to use for this language detector.
     */
    public LangDetector(Map<String, LangDetectorStrategy> strategies, String defaultStrategy) {
        this.strategies = strategies;
        switchToStrategy(defaultStrategy);
    }

    /**
     * Finds the closest language based on a k-mer distribution and a LangDistStore.
     *
     * @param unidentifiedLang K-mer distribution of unidentified language.
     * @param store Store of language distributions to compare to.
     */
    public Lang findClosestLanguage(LangDist unidentifiedLang, LangDistStore store) {
        return strategies.get(strategy).findClosestLanguage(unidentifiedLang, store);
    }

    /**
     * Switches this language detector's strategy to a different algorithm.
     *
     * @param newStrategy Name of the new strategy to use.
     */
    public void switchToStrategy(String newStrategy) {
        strategy = newStrategy;
    }
}
