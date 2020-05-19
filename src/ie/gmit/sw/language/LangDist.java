package ie.gmit.sw.language;

// language distribution; records a language sample's n-grams into a hashed feature vector.
// reworked from the previous language detection assignment.
//   (surprisingly, this is the only part that I managed to reuse from that...)
public class LangDist {
    private Lang lang;
    private int numRecords;
    private int hashRange;
    private int[] vector;

    public LangDist(Lang lang, int hashRange) {
        this.lang = lang;
        this.hashRange = hashRange;
        vector = new int[hashRange];
    }

    public Lang getLang() {
        return lang;
    }

    public int getNumRecords() {
        return numRecords;
    }

    public int getHashRange() {
        return hashRange;
    }

    // splits a sample line into n-grams of size k and records them into the feature vector
    public void recordSample(String line, int k) {
        char[] sample = line.toCharArray();
        char[] ngram = new char[k];

        for (int i = 0; i <= sample.length - k; i++) {
            // build n-gram array
            for (int j = 0; j < k; j++) {
                ngram[j] = sample[i + j];
            }

            recordNgram(ngram);
            numRecords++;
        }
    }

    // hashes an n-gram and records the "hit" in the feature vector
    public void recordNgram(char[] ngram) {
        // hash implementation based on String.hashcode()
        int hash = 17;

        for (int i = 0; i < ngram.length; i++) {
            hash = 31 * hash + ngram[i];
        }

        // compute array index (ignoring sign bit)
        int index = (hash & 0x7FFFFFFF) % hashRange;
        // record n-gram hit
        vector[index]++;
    }

    // get the relative frequencies of the n-gram distribution from the feature vector
    // (meaning the frequencies sum to 1)
    public double[] getFrequencies() {
        double[] dist = new double[hashRange];
        for (int i = 0; i < hashRange; i++) {
            dist[i] = (double) vector[i] / getNumRecords();
        }

        return dist;

    }
}
