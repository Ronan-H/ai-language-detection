package ie.gmit.sw;


import static java.lang.System.out;

import java.util.Scanner;

public class Menu {
    private Scanner console;

    public Menu() {
        console = new Scanner(System.in);
    }

    public void go() {
        // print the program header
        out.println();
        out.println(" =================================");
        out.println(" |     AI Language Detection     |");
        out.println(" |        By Ronan Hanley        |");
        out.println(" =================================\n");

        out.println("The flow of this application is as follows:");
        out.println("  1. Creation of the training data");
        out.println("  2. Network topology and training");
        out.println("  3. Accuracy, sensitivity, and specificity stats");
        out.println("  4. Test your own input (from a file)\n");

        out.println("Creation of the training data");
    }

    public int getUserOption(String...options) {
        int i;

        out.println("\nSelect an option:");
        // print the options
        for (i = 0; i < options.length; ++i) {
            out.printf("[%d]: %s\n", (i + 1), options[i]);
        }

        out.print("\n");

        // return the user's validated choice
        return getValidatedInt(1, options.length);
    }

    public int getValidatedInt(int min, int max) {
        int input = 0;
        boolean valid;

        do {
            out.print("> ");
            try {
                input = Integer.parseInt(console.nextLine());
                valid = (input >= min && input <= max);
            } catch(NumberFormatException e) {
                // input wasn't a number
                valid = false;
            }

            out.println();

            if (!valid) {
                out.printf("Invalid input; must be a number between %d and %d (inclusive).\n", min, max);
                out.println("Please try again.\n");
            }
        } while(!valid);

        return input;
    }
}
