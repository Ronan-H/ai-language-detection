package ie.gmit.sw.test;

import org.encog.engine.network.activation.ActivationFunction;
import org.encog.neural.error.ErrorFunction;

public class TestErrorFunction implements ErrorFunction {
    /**
     * {@inheritDoc}
     */
    @Override
    public void calculateError(ActivationFunction af, double[] b, double[] a,
                               double[] ideal, double[] actual, double[] error, double derivShift,
                               double significance) {

        for(int i=0;i<actual.length;i++) {
            if (ideal[i] == 1) {
                error[i] = (ideal[i] - actual[i]) *significance;
            }
        }
    }
}
