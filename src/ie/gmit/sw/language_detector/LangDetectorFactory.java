package ie.gmit.sw.language_detector;

import java.util.HashMap;
import java.util.Map;

/**
 * Responsible for creating LangDetectors. Hides the complexity of creating a LangDetector.
 */
public class LangDetectorFactory {
    private static LangDetectorFactory instance;

    private LangDetectorFactory() {}

    public static LangDetectorFactory getInstance() {
        if (instance == null) {
            instance = new LangDetectorFactory();
        }

        return instance;
    }

    /**
     * Builds a language detector with all the available detecting strategies.
     *
     * @param defaultAlgorithm Name of default algorithm to use (e.g. "Out-of-place")
     * @return Language detector with all the available detecting strategies.
     */
    public LangDetector getLanguageDetector(String defaultAlgorithm) {
        Map<String, LangDetectorStrategy> strategies = new HashMap<>();

        strategies.put("Out-of-place", new OutOfPlaceStrategy());
        strategies.put("Simple distance", new SimpleDistanceStrategy());
        strategies.put("Cosine distance", new CosineDistanceStrategy());

        return new LangDetector(strategies, defaultAlgorithm);
    }
}
