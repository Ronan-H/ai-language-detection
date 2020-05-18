package ie.gmit.sw.test;

import ie.gmit.sw.neural_network.config.NetworkSelection;
import ie.gmit.sw.neural_network.config.NetworkSelectionFactory;
import ie.gmit.sw.neural_network.phase.TrainingPhase;

public class TestTrainNetwork {
    public static void main(String[] args) {
        String savePath = "./neural-network.nn";

        NetworkSelection networkSelection = NetworkSelectionFactory.getInstance().getStandardSelections();
        networkSelection.loadOptimizedDefaults();
        System.out.println(networkSelection.toString());

        new TrainingPhase(networkSelection, savePath).train();
    }
}
