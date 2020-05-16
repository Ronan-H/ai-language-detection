package ie.gmit.sw.test;

import ie.gmit.sw.*;

public class TestTrainNetwork {
    public static void main(String[] args) {
        String savePath = "./neural-network.nn";

        NetworkSelection networkSelection = NetworkSelectionFactory.getInstance().getStandardSelections();
        networkSelection.loadOptimizedDefaults();
        System.out.println(networkSelection.toString());

        NetworkTrainer networkTrainer = new NetworkTrainer(networkSelection, savePath);
        networkTrainer.train();
    }
}
