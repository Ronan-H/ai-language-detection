package ie.gmit.sw.language_distribution;

import ie.gmit.sw.Lang;

import java.util.Arrays;

/**
 * Hashed language distribution; a language distribution where each k-mer is hashed into
 * an array (there WILL be collisions, but since other language distributions of the same
 * language would have the same collisions and so record frequencies for the same index,
 * it should still work OK and be much more efficient than using Maps).
 */
public class HashedLangDist extends LangDist {
    private int hashRange;
    private int[] freqs;

    /**
     * Creates a new hashed language distribution
     *
     * @param lang Language of this distribution
     * @param hashRange Range of the hash function
     */
    public HashedLangDist(Lang lang, int hashRange) {
        super(lang);
        this.hashRange = hashRange;
        freqs = new int[hashRange];
    }

    /**
     * Records a single k-mer to this language distribution, by hashing the k-mer in order
     * to record it into an array instead of a map.
     *
     * @param kmer k-mer to record one usage of
     */
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
        // record the 1st character of the k-mer too, to help detect languages with large alphabets (eg. Chinese)
        freqs[kmer[0] % hashRange]++;
    }

    /**
     * Generates a double array of frequency values for this distribution, where each double represents
     * a frequency value between 0 and 1, where 0 means no occurances and 1 means all k-mers were at this
     * index.
     *
     * @return frequency values
     */
    @Override
    public double[] getFrequencies() {
        /* Different type of normalization (same as Utilities.normalize(...))
        double lower = 0;
        double upper = 1;

        double[] dist = new double[hashRange];
        double max = Arrays.stream(freqs).max().getAsInt();
        double min = Arrays.stream(freqs).min().getAsInt();

        for(int i=0; i<dist.length; i++) {
            dist[i] = (freqs[i] - min)*(upper - lower)/(max - min) + lower;
        }
        return dist;
        */


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
