package ie.gmit.sw.test;

import ie.gmit.sw.language_detector.LangDetector;
import ie.gmit.sw.language_detector.LangDetectorFactory;
import ie.gmit.sw.language_detector.LangDetectionSystem;
import ie.gmit.sw.language_distribution.LangDistStore;
import ie.gmit.sw.language_distribution.LangDistStoreBuilder;
import ie.gmit.sw.sample_parser.FileSampleParser;

import java.io.File;
import java.io.IOException;

public class TestLangDetector {
    public static void main(String[] args) throws InterruptedException {
        File wili = new File("./wili-2018-Small-11750-Edited.txt");

        // build k-mer distribution for all languages from language dataset
        LangDistStore distStore = new LangDistStoreBuilder()
                .withMappedStore(512, 3)
                .registerParser(
                        new FileSampleParser(wili)
                )
                .build();

        // create language detection system
        LangDetector langDetector = LangDetectorFactory.getInstance().getLanguageDetector("Out-of-place");
        LangDetectionSystem langDetectionSystem = new LangDetectionSystem(distStore, langDetector);

        // switch strategy
        langDetector.switchToStrategy("Cosine distance");

        try {
            distStore.writeToFile("./training-data.csv");
        } catch (IOException e) {
            e.printStackTrace();
        }

        String result = langDetectionSystem.findClosestLanguage(
                "0",
                "One of the limiting features of multi-layer perceptron is that the number of input neurons is fixed, causing issues when dealing with variable-length input sources such as text."
        ).getLanguageName();
        System.out.println("Result: " + result);
    }
}
