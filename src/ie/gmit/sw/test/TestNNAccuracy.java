package ie.gmit.sw.test;

import ie.gmit.sw.Lang;
import ie.gmit.sw.NetworkSelection;
import ie.gmit.sw.NetworkSelectionFactory;
import ie.gmit.sw.NetworkValidation;
import ie.gmit.sw.code_stubs.Utilities;
import ie.gmit.sw.language_distribution.PartitionedHashedLangDist;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.networks.BasicNetwork;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class TestNNAccuracy {
    public static void main(String[] args) throws IOException {
        NetworkSelection networkSelection = NetworkSelectionFactory.getInstance().getStandardSelections();
        networkSelection.loadOptimizedDefaults();
        System.out.println(networkSelection.toString());

        new NetworkValidation(networkSelection, "wili-2018-Small-11750-Edited.txt", "neural-network.nn").testAccuracy();
    }
}
