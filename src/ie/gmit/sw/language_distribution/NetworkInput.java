package ie.gmit.sw.language_distribution;

import ie.gmit.sw.Lang;
import ie.gmit.sw.code_stubs.Utilities;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NetworkInput {
    private Lang lang;
    private List<double[]> inputs;

    public NetworkInput(Lang lang) {
        this.lang = lang;
        inputs = new ArrayList<>();
    }

    public void addInput(double[] input) {
        inputs.add(input);
    }

    public double[] asArray() {
        int inputSize = inputs.stream().mapToInt(d -> d.length).sum();
        double[] arr = new double[inputSize];
        int index = 0;

        for (double[] input : inputs) {
            for (double d : input) {
                arr[index++] = d;
            }
        }

        double sum = Arrays.stream(arr).sum();
        for (int i = 0; i < arr.length; i++) {
            arr[i] = arr[i] / sum;
        }

        arr = Utilities.normalize(arr, 0, 1);

        return arr;
    }

    public Lang getLang() {
        return lang;
    }
}
