package ie.gmit.sw.language_detector;

/**
 * A simple strategy for detecting an unknown language: finds the language in a LangDistStore which has
 * the smallest total difference for each k-mer frequency value.
 */
public class SimpleDistanceStrategy extends FreqDistanceStrategy {

    /**
     * Total distance between two frequency distributions.
     *
     * @param dist1 Distribution frequency from a language distribution.
     * @param dist2 Distribution frequency from a language distribution.
     * @return Sum of the differences between values at each index of these arrays.
     */
    public double getDistance(double[] dist1, double[] dist2) {
        double totalDist = 0;

        for (int i = 0; i < dist1.length; i++) {
            totalDist += Math.abs(dist1[i] - dist2[i]);
        }

        return totalDist;
    }
}
