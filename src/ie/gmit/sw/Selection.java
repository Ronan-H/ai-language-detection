package ie.gmit.sw;

public class Selection<T> {
    private String prompt;
    private T[] options;
    private String[] optionLabels;
    private T best;

    public Selection(String prompt, T[] options, String[] optionLabels, T best) {
        this.prompt = prompt;
        this.options = options;
        this.optionLabels = optionLabels;
        this.best = best;
    }

    public Selection(String prompt, T[] options, T best) {
        this.prompt = prompt;
        this.options = options;
        this.best = best;

        for (int i = 0; i < options.length; i++) {
            optionLabels[i] = options[i].toString();
        }
    }

    public T select(int index) {
        return options[index];
    }
}
