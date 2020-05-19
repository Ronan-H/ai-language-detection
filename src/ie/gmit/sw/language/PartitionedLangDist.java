package ie.gmit.sw.language;

// partitioned language distribution; stores and maintains a LangDist for each n-gram from 1 to k
public class PartitionedLangDist extends LangDist {
    private int numPartitions;
    private LangDist[] partitions;

    public PartitionedLangDist(Lang lang, int hashRange, int maxK) {
        super(lang, hashRange);
        this.numPartitions = maxK;

        // create LangDist partitions
        partitions = new LangDist[maxK];
        for (int i = 0; i < maxK; i++) {
            partitions[i] = new LangDist(lang, hashRange);
        }
    }

    // records a language sample for every n-gram partition
    public void recordSample(String line) {
        for (int i = 0; i < numPartitions; i++) {
            recordSample(line, i + 1);
        }
    }

    // uses the underlying LangDist partitions to record the sample for every n-gram partition
    @Override
    public void recordSample(String line, int k) {
        partitions[k - 1].recordSample(line, k);
    }

    // returns the combined frequency distribution of every underlying LangDist partition
    @Override
    public double[] getFrequencies() {
        double[] combined = new double[getHashRange() * numPartitions];

        // copy every frequency distribution into the combined array
        for (int i = 0; i < numPartitions; i++) {
            double[] dist = partitions[i].getFrequencies();
            System.arraycopy(dist, 0, combined, i * getHashRange(), dist.length);
        }

        return combined;
    }
}
