package ie.gmit.sw.language_distribution;

import ie.gmit.sw.Lang;

public class PartitionedHashedLangDist extends HashedLangDist {
    private int numPartitions;
    private HashedLangDist[] partitions;

    public PartitionedHashedLangDist(Lang lang, int hashRange, int numPartitions) {
        super(lang, hashRange);
        this.numPartitions = numPartitions;

        partitions = new HashedLangDist[numPartitions];
        for (int i = 0; i < numPartitions; i++) {
            partitions[i] = new HashedLangDist(lang, hashRange);
        }
    }

    public void recordSample(String line) {
        for (int i = 0; i < numPartitions; i++) {
            recordSample(line, i);
        }
    }


    @Override
    public void recordSample(String line, int k) {
        partitions[k].recordSample(line, k + 1);
    }

    @Override
    public double[] getFrequencies() {
        double[] combined = new double[getHashRange() * numPartitions];

        for (int i = 0; i < numPartitions; i++) {
            double[] pFreqs = partitions[i].getFrequencies();

            System.arraycopy(pFreqs, 0, combined, i * getHashRange(), pFreqs.length);
        }

        return combined;
    }
}
