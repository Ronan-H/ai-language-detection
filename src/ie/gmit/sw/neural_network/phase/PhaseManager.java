package ie.gmit.sw.neural_network.phase;

import ie.gmit.sw.neural_network.config.NetworkSelection;

import java.io.IOException;

public class PhaseManager {
    private InputVectorCreationPhase trainingDataCreator;
    private TrainingPhase networkTrainer;
    private ValidationPhase networkValidation;
    private PredictionPhase networkPrediction;

    public PhaseManager(NetworkSelection selection, String samplesPath, String trainingDataPath, String nnPath) {
        trainingDataCreator = new InputVectorCreationPhase(selection, samplesPath, trainingDataPath);
        networkTrainer = new TrainingPhase(selection, nnPath);
        networkValidation = new ValidationPhase(selection, samplesPath, nnPath);
        networkPrediction = new PredictionPhase(selection, nnPath);
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
