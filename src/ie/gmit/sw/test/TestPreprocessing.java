package ie.gmit.sw.test;

import java.util.regex.Pattern;

public class TestPreprocessing {
    public static void main(String[] args) {
        String pattern = "\\((.*)\\)\\s*|[0-9]\\s*";
        Pattern toRemove = Pattern.compile(pattern);
        String sample = "This is a sample (this part should be removed) and these numbers: 123 4 5 6";
        String cleaned = toRemove.matcher(sample).replaceAll("");

        System.out.printf("Pattern: %s%n", pattern);
        System.out.printf("Input: %s%n", sample);
        System.out.printf("Output: %s%n", cleaned);
    }
}
