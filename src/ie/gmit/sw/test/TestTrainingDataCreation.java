package ie.gmit.sw.test;

import ie.gmit.sw.neural_network.config.NetworkSelection;
import ie.gmit.sw.neural_network.config.NetworkSelectionFactory;
import ie.gmit.sw.neural_network.TrainingDataCreator;

import java.io.*;

public class TestTrainingDataCreation {
    public static void main(String[] args) throws IOException {
        String samplesPath = "./wili-2018-Small-11750-Edited.txt";
        String outPath = "./training-data.csv";

        NetworkSelection networkSelection = NetworkSelectionFactory.getInstance().getStandardSelections();
        networkSelection.loadOptimizedDefaults();
        System.out.println(networkSelection.toString());

        TrainingDataCreator trainingDataCreator = new TrainingDataCreator(networkSelection, samplesPath, outPath);
        trainingDataCreator.create();
    }
}
