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
        System.out.print("<< Press enter to continue to the next phase >>");
        UserInput.getScanner().nextLine();
        System.out.println();
    }

    public Object getSelectionChoice(String selectionKey) {
        return networkSelection.getSelectionChoice(selectionKey);
    }
}
