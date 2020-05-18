package ie.gmit.sw;

import ie.gmit.sw.language_distribution.PartitionedHashedLangDist;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogDirectoryPersistence;

import java.io.File;
import java.io.IOException;
import java.util.List;
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
        BasicNetwork network = (BasicNetwork) EncogDirectoryPersistence.loadObject(nnPath);

        String input;
        File inputFile;
        while (true) {
            System.out.println("Enter the path of a file containing sample language data to predict.");
            System.out.println("(or enter \"exit\" to exit)\n");
            System.out.print("> ");
            input = console.nextLine();

            if (input.trim().equalsIgnoreCase("exit")) {
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

            List<String> samples = new TrainingDataProcessor(inputFile).getUnknownSamples();
            PartitionedHashedLangDist dist = new PartitionedHashedLangDist(
                    Lang.Unidentified,
                    vectorSize,
                    ngramLength
            );

            for (String sample : samples) {
                dist.recordSample(sample);
            }

            MLData sample = new BasicMLData(dist.getFrequencies());
            Lang classification = Lang.values()[network.classify(sample)];
            System.out.printf("Predicted classification: %s%n%n", classification.getLanguageName());
        }
    }
}
