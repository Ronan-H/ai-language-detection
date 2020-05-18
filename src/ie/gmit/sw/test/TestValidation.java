package ie.gmit.sw.test;

import ie.gmit.sw.neural_network.config.NetworkSelection;
import ie.gmit.sw.neural_network.config.NetworkSelectionFactory;
import ie.gmit.sw.neural_network.phase.ValidationPhase;
import java.io.IOException;

public class TestValidation {
    public static void main(String[] args) throws IOException {
        String samplesPath = "wili-2018-Small-11750-Edited.txt";
        String nnPath = "neural-network.nn";

        NetworkSelection networkSelection = NetworkSelectionFactory.getInstance().getStandardSelections();
        networkSelection.loadOptimizedDefaults();
        System.out.println(networkSelection.toString());

        new ValidationPhase(networkSelection, samplesPath, nnPath).runTests();
    }
}
