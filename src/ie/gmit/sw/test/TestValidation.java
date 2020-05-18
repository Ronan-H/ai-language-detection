package ie.gmit.sw.test;

import ie.gmit.sw.NetworkSelection;
import ie.gmit.sw.NetworkSelectionFactory;
import ie.gmit.sw.NetworkValidation;
import java.io.IOException;

public class TestValidation {
    public static void main(String[] args) throws IOException {
        NetworkSelection networkSelection = NetworkSelectionFactory.getInstance().getStandardSelections();
        networkSelection.loadOptimizedDefaults();
        System.out.println(networkSelection.toString());

        new NetworkValidation(networkSelection, "wili-2018-Small-11750-Edited.txt", "neural-network.nn").testAccuracy();
    }
}
