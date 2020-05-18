package ie.gmit.sw;

import java.io.IOException;

public class NetworkManager {
    private TrainingDataCreator trainingDataCreator;
    private NetworkTrainer networkTrainer;
    private NetworkValidation networkValidation;
    private NetworkPrediction networkPrediction;

    public NetworkManager(NetworkSelection selection, String samplesPath, String trainingDataPath, String nnPath) {
        trainingDataCreator = new TrainingDataCreator(selection, samplesPath, trainingDataPath);
        networkTrainer = new NetworkTrainer(selection, nnPath);
        networkValidation = new NetworkValidation(selection, samplesPath, nnPath);
        networkPrediction = new NetworkPrediction(selection, nnPath);
    }

    public void createTrainingData() throws IOException {
        trainingDataCreator.create();
    }

    public void trainNetwork() {
        networkTrainer.train();
    }

    public void runValidationTests() throws IOException {
        networkValidation.runTests();
    }

    public void allowUserInputPredictions() throws IOException {
        networkPrediction.allowUserInputPredictions();
    }
}
