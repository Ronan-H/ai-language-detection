package ie.gmit.sw.neural_network.phase;

import ie.gmit.sw.UserInput;
import ie.gmit.sw.neural_network.config.NetworkSelection;

import java.io.IOException;

public abstract class NetworkPhase {
    private NetworkSelection networkSelection;

    public NetworkPhase(NetworkSelection networkSelection) {
        this.networkSelection = networkSelection;
    }

    public abstract void executePhase() throws IOException;

    protected void onPhaseFinished() {
        // TODO This doesn't work properly
        //System.out.println("Press enter to continue...");
        //UserInput.getScanner().nextLine();
    }

    public Object getSelectionChoice(String selectionKey) {
        return networkSelection.getSelectionChoice(selectionKey);
    }
}
