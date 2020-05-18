package ie.gmit.sw;

public class Selection<T> {
    private String prompt;
    private String explanation;
    private String guidance;
    private T[] options;
    private String[] optionLabels;
    private T best;
    private T chosen;

    public Selection(String prompt, String explanation, String guidance, T[] options, String[] optionLabels, T best) {
        this.prompt = prompt;
        this.explanation = explanation;
        this.guidance = guidance;
        this.options = options;
        this.optionLabels = optionLabels;
        this.best = best;
    }

    public Selection(String prompt, String explanation, String guidance, T[] options, T best) {
        this.prompt = prompt;
        this.explanation = explanation;
        this.guidance = guidance;
        this.options = options;
        this.best = best;

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

    @Override
    public String toString() {
        return String.format("\t%s: %s%n", prompt, chosen);
    }
}