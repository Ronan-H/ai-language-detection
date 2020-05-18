package ie.gmit.sw.language;

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

    public double getPrecision() {
        // TP / (TP + NP)
        return (double) tp / (fp + tp);
    }

    @Override
    public int compareTo(LangStats o) {
        return -Double.compare(getPrecision(), o.getPrecision());
    }

    @Override
    public String toString() {
        return String.format("%25s precision: %7.2f%% - TP: %3d, FP: %3d",
                lang.getLanguageName(),
                getPrecision() * 100,
                tp, fp);
    }
}
