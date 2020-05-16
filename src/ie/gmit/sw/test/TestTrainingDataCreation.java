package ie.gmit.sw.test;

import ie.gmit.sw.NetworkSelection;
import ie.gmit.sw.NetworkSelectionFactory;
import ie.gmit.sw.TrainingDataCreator;

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
