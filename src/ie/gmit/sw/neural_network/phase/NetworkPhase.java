package ie.gmit.sw.neural_network.phase;

import ie.gmit.sw.UserInput;
import ie.gmit.sw.neural_network.config.NetworkSelection;

import java.io.IOException;

// represents a "phase" of neural network creation, testing, or use
public abstract class NetworkPhase {
    private NetworkSelection networkSelection;

    public NetworkPhase(NetworkSelection networkSelection) {
        this.networkSelection = networkSelection;
    }

    public abstract void executePhase() throws IOException;

    // can be called after the phase has ended
    protected void onPhaseFinished() {
        // wait for the user to press enter, so that they can read the printed details
        // of this phase before continuing on to the next
        System.out.print("<< Press enter to continue to the next phase >>");
        UserInput.getScanner().nextLine();
        System.out.println();
    }

    // convenience method for a phase to access the user's network configuration options
    protected Object getSelectionChoice(String selectionKey) {
        return networkSelection.getSelectionChoice(selectionKey);
    }
}
