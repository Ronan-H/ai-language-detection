package ie.gmit.sw.neural_network.config;

// represents a single selection option that can be made to configure a neural network
public class Selection<T> {
    private String prompt;
    private String explanation;
    private String guidance;
    private T[] options;
    private String[] optionLabels;
    private T best;
    private T chosen;

    public Selection(String prompt, String explanation, String guidance, T[] options, T best) {
        this.prompt = prompt;
        this.explanation = explanation;
        this.guidance = guidance;
        this.options = options;
        this.best = best;

        // create option labels using the options provided (which may not be strings)
        optionLabels = new String[options.length];
        for (int i = 0; i < options.length; i++) {
            optionLabels[i] = options[i].toString();
        }
    }

    public void choose(int index) {
        chosen = options[index];
    }

    public void chooseBest() {
        chosen = best;
    }

    public T getChoice() {
        return chosen;
    }

    public String getPrompt() {
        return prompt;
    }

    public String[] getOptionLabels() {
        return optionLabels;
    }

    public T getBest() {
        return best;
    }

    public String getExplanation() {
        return explanation;
    }

    public String getGuidance() {
        return guidance;
    }

    // prints this options description along with the chosen value
    @Override
    public String toString() {
        return String.format("\t%s: %s%n", prompt, chosen);
    }
}
