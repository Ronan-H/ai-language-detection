package ie.gmit.sw.neural_network.phase;

import ie.gmit.sw.language.Lang;
import ie.gmit.sw.UserInput;
import ie.gmit.sw.language.PartitionedLangDist;
import ie.gmit.sw.neural_network.config.NetworkSelection;
import org.encog.ml.data.MLData;
import org.encog.ml.data.basic.BasicMLData;
import org.encog.neural.networks.BasicNetwork;
import org.encog.persist.EncogDirectoryPersistence;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Scanner;

import static java.lang.System.out;


// the phase of predicting the language of a user specified input file
public class PredictionPhase extends NetworkPhase {
    private File nnPath;
    private Scanner console;

    public PredictionPhase(NetworkSelection networkSelection, String nnPath) {
        super(networkSelection);
        this.nnPath = new File(nnPath);
        // get a handle on the shared Scanner object
        console = UserInput.getScanner();
    }

    public void allowUserInputPredictions() throws IOException {
        executePhase();
    }

    @Override
    public void executePhase() throws IOException {
        out.println("== Prediction phase (of user input) ==");
        out.println("You will now have the opportunity to predict \"live\" language data from a file.\n");

        out.println("Loading parameters...");
        int vectorSize = (Integer) getSelectionChoice("vectorSize");
        int ngramLength = (Integer) getSelectionChoice("ngramLength");

        out.printf("Loading the nerual network from file: %s%n", nnPath.getName());
        BasicNetwork network = (BasicNetwork) EncogDirectoryPersistence.loadObject(nnPath);

        String input;
        File inputFile;
        while (true) {
            out.println("Enter the path of a file containing sample language data to predict (relative or absolute)");
            out.println("(or enter \"exit\" to exit)\n");
            out.print("> ");
            input = console.nextLine();

            if (input.trim().equalsIgnoreCase("exit")) {
                // returning from this method naturally exits the program
                return;
            }
            else {
                // make sure the specified file is valid
                inputFile = new File(input);
                if (!inputFile.exists() || inputFile.isDirectory()) { // must exist and not be a directory...
                    out.println("Invalid file, please try again.\n");
                    continue;
                }
            }

            // at this point the user has specified a valid input file
            out.println("\nReading file and predicting...");

            // process each line of the text file into a hashed feature vector of n-grams
            List<String> samples = new SampleFileReader(inputFile).getUnknownSamples();
            PartitionedLangDist dist = new PartitionedLangDist(
                    Lang.Unidentified,
                    vectorSize,
                    ngramLength
            );

            for (String sample : samples) {
                dist.recordSample(sample);
            }

            // make a language prediction and display it to the user
            MLData sample = new BasicMLData(dist.getFrequencies());
            Lang classification = Lang.values()[network.classify(sample)];
            out.printf("Predicted classification: %s%n%n", classification.getLanguageName());
        }
    }
}
