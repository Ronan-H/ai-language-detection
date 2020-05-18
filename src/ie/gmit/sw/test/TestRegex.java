package ie.gmit.sw.test;

import java.util.regex.Pattern;

public class TestRegex {
    public static void main(String[] args) {
        String pattern = "\\((.*)\\)\\s*|\\[[0-9]*\\]\\s*";
        Pattern toRemove = Pattern.compile(pattern);
        String sample = "This part should stay (and this part should be removed), also removes reference numbers[123]";
        String cleaned = toRemove.matcher(sample).replaceAll("");

        System.out.printf("Pattern: %s%n", pattern);
        System.out.printf("Input: %s%n", sample);
        System.out.printf("Output: %s%n", cleaned);
    }
}
