package ie.gmit.sw.language;

// language prediction stats, including TP rate, NP rate, and precision
public class LangStats implements Comparable<LangStats> {
    private Lang lang;
    // true positive count
    private int tp;
    // false positive count
    private int fp;

    public LangStats(Lang lang) {
        this.lang = lang;
    }

    public void recordTruePositive() {
        tp++;
    }

    public void recordFalePositive() {
        fp++;
    }

    // precision = TP / (TP + NP)
    public double getPrecision() {
        return (double) tp / (fp + tp);
    }

    // compare language stats based on precision
    // (used for sorting the language stats based on performance)
    @Override
    public int compareTo(LangStats o) {
        return -Double.compare(getPrecision(), o.getPrecision());
    }

    // "pretty" string representation of the stats
    @Override
    public String toString() {
        return String.format("%25s precision: %7.2f%% - TP: %3d, FP: %3d",
                lang.getLanguageName(),
                getPrecision() * 100,
                tp, fp);
    }
}
