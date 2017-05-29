package hse.marinosyan;

public class Main {

    public static void main(String[] args) {

        if (args.length != 4) {
            System.err.println("Number of parameters must be equal to 3:\n" +
                    "[N, M] - range, C - the last digit, P – path to the output file.");
        }
        else if (args[0].charAt(0) == '-' || args[1].charAt(0) == '-' || args[2].length() != 1) {
            System.err.println("Incorrect values of parameters!");
        }
        else {
            PrimeNumbersCalculator calculator = new PrimeNumbersCalculator(args[0], args[1], args[2]);

            calculator.calculateToFile(args[3]);
        }
    }
}
