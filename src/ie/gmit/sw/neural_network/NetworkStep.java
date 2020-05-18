package ie.gmit.sw.neural_network;

import ie.gmit.sw.neural_network.config.NetworkSelection;

import java.io.IOException;

public abstract class NetworkStep {
    private NetworkSelection networkSelection;

    public NetworkStep(NetworkSelection networkSelection) {
        this.networkSelection = networkSelection;
    }

    public abstract void executeStep() throws IOException;

    public Object getSelectionChoice(String selectionKey) {
        return networkSelection.getSelectionChoice(selectionKey);
    }
}
