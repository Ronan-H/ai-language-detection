package ie.gmit.sw.language;

public class PartitionedLangDist extends LangDist {
    private int numPartitions;
    private LangDist[] partitions;

    public PartitionedLangDist(Lang lang, int hashRange, int numPartitions) {
        super(lang, hashRange);
        this.numPartitions = numPartitions;

        partitions = new LangDist[numPartitions];
        for (int i = 0; i < numPartitions; i++) {
            partitions[i] = new LangDist(lang, hashRange);
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
