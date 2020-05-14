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
            partitions[i].recordSample(line, numPartitions);
        }
    }


    @Override
    public void recordSample(String line, int k) {
        for (int i = 0; i < numPartitions; i++) {
            partitions[i].recordSample(line, k - i);
        }
    }

    @Override
    public double[] getFrequencies() {
        double[] combined = new double[getHashRange() * numPartitions];

        for (int i = 0; i < numPartitions; i++) {
            double[] pFreqs = partitions[i].getFrequencies();

            for (int j = 0; j < pFreqs.length; j++) {
                combined[(i * getHashRange()) + j] = pFreqs[j];
            }
        }

        return combined;
    }
}
