package ie.gmit.sw;

import ie.gmit.sw.code_stubs.Utilities;
import ie.gmit.sw.language_distribution.PartitionedHashedLangDist;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.networks.BasicNetwork;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Scanner;

public class NetworkPrediction {
    private NetworkSelection networkSelection;
    private File nnPath;
    private Scanner console;

    public NetworkPrediction(NetworkSelection networkSelection, String nnPath) {
        this.networkSelection = networkSelection;
        this.nnPath = new File(nnPath);
        console = UserInput.getScanner();
    }

    public void allowUserInputPredictions() throws IOException {
        System.out.println("== Live Data Prediction ==");
        System.out.println("You will now have the opportunity to predict \"live\" language data from a file.\n");

        System.out.println("Loading parameters...");
        int vectorSize = (Integer) networkSelection.getSelectionChoice("vectorSize");
        int ngramLength = (Integer) networkSelection.getSelectionChoice("ngramLength");

        System.out.printf("Loading the nerual network from file: %s%n", nnPath.getName());
        BasicNetwork network = Utilities.loadNeuralNetwork("neural-network.nn");

        String input;
        File inputFile;
        while (true) {
            System.out.println("Enter the path of a file containing sample language data to predict.");
            System.out.println("(or enter an empty string to exit)\n");
            System.out.print("> ");
            input = console.nextLine();

            if (input.length() == 0) {
                return;
            }
            else {
                inputFile = new File(input);
                if (!inputFile.exists() || inputFile.isDirectory()) {
                    System.out.println("Invalid file, please try again.\n");
                    continue;
                }
            }

            System.out.println("\nReading file and predicting...");
            PartitionedHashedLangDist dist = new PartitionedHashedLangDist(
                    Lang.Unidentified,
                    vectorSize,
                    ngramLength
            );

            BufferedReader fileIn = new BufferedReader(new FileReader(inputFile));
            String line;
            while ((line = fileIn.readLine()) != null) {
                dist.recordSample(line);
            }

            MLData sample = new BasicMLData(dist.getFrequencies());
            Lang classification = Lang.values()[network.classify(sample)];
            System.out.println("Predicted classification: " + classification.getLanguageName());
        }
    }
}
