package ie.gmit.sw;

import java.util.Scanner;

// singleton access to a Scanner object, so it can be easily shared across classes
public class UserInput {
    private static Scanner console;

    private UserInput() {}

    public static Scanner getScanner() {
        if (console == null) {
            console = new Scanner(System.in);
        }

        return console;
    }
}
