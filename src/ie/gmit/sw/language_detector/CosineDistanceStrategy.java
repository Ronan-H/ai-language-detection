package ie.gmit.sw.language_detector;

/**
 * Language detector strategy using the cosine similarity metric to compare language distributions.
 */
public class CosineDistanceStrategy extends FreqDistanceStrategy {

    /**
     * Inverse of cosine similarity between two frequency distributions.
     * (inverted so that smaller values = more simimlar languages)
     *
     * @param distA Distribution frequency from a language distribution.
     * @param distB Distribution frequency from a language distribution.
     * @return Inverse cosine similarity metric value.
     */
    public double getDistance(double[] distA, double[] distB) {
        double aSum = 0, bSum = 0, prodSum = 0;
        double aVal, bVal;

        for (int i = 0; i < distA.length; i++) {
            aVal = distA[i];
            bVal = distB[i];

            aSum += aVal * aVal;
            bSum += bVal * bVal;

            prodSum += aVal * bVal;
        }

        double cosineSimilarity = (prodSum / (Math.sqrt(aSum) * Math.sqrt(bSum)));
        return cosineSimilarity;
    }
}