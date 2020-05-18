package ie.gmit.sw.language;

public class LangDist {
    private Lang lang;
    private int numRecords;
    private int hashRange;
    private int[] freqs;

    public LangDist(Lang lang, int hashRange) {
        this.lang = lang;
        this.hashRange = hashRange;
        freqs = new int[hashRange];
    }

    public Lang getLang() {
        return lang;
    }

    public int getNumRecords() {
        return numRecords;
    }

    public void recordSample(String line, int k) {
        char[] sample = line.toCharArray();
        char[] kmer = new char[k];

        for (int i = 0; i <= sample.length - k; i++) {
            // build k-mer array
            for (int j = 0; j < k; j++) {
                kmer[j] = sample[i + j];
            }

            recordKmer(kmer);
            numRecords++;
        }
    }

    public void recordKmer(char[] kmer) {
        // hash implementation based on String.hashcode()
        int hash = 17;

        for (int i = 0; i < kmer.length; i++) {
            hash = 31 * hash + kmer[i];
        }

        // compute array index (ignoring sign bit)
        int index = (hash & 0x7FFFFFFF) % hashRange;
        // record k-mer
        freqs[index]++;
    }

    public double[] getFrequencies() {
        double[] dist = new double[hashRange];
        for (int i = 0; i < hashRange; i++) {
            dist[i] = (double) freqs[i] / getNumRecords();
        }

        return dist;

    }

    public int getHashRange() {
        return hashRange;
    }
}
