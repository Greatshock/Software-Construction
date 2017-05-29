package hse.marinosyan;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.math.BigInteger;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;
import java.util.stream.LongStream;

class PrimeNumbersCalculator {

    private BigInteger from_big, to_big;
    private long from_long, to_long;
    private byte digit;
    private boolean fitsLong = true;

    PrimeNumbersCalculator(String from, String to, String digit) {

        // Initialize the BigInteger range
        from_big = new BigInteger(from);
        to_big = new BigInteger(to);
        this.digit = Byte.valueOf(digit);

        // Check if the range fits in long
        try {
            from_long = from_big.longValueExact();
            to_long = to_big.longValueExact();
        }
        catch (ArithmeticException e) {

            // If not, indicate it
            fitsLong = false;
        }
    }

    private List<Long> calcLongSequentially() {

        return LongStream.rangeClosed(from_long, to_long).boxed()
                .collect(Collectors.toList()).stream()
                .filter(x -> isPrime(x) && x % 10 == digit)
                .collect(Collectors.toList());
    }

    private List<Long> calcLongParallel() {

        return LongStream.rangeClosed(from_long, to_long).boxed()
                .collect(Collectors.toList()).parallelStream()
                .filter(x -> isPrime(x) && x % 10 == digit)
                .collect(Collectors.toList());
    }

    private List<BigInteger> calcBigintSequentially() {

        BigInteger from = new BigInteger(from_big.toString());
        BigInteger to = new BigInteger(to_big.toString());

        // Get the list of all numbers
        // between the user-defined range
        List<BigInteger> numbersList = new LinkedList<>();
        while (from.compareTo(to) != 0){
            numbersList.add(from);
            from = from.add(BigInteger.ONE);
        }

        return numbersList.stream().
                filter(x -> isPrime(x) && x.mod(BigInteger.TEN)
                        .equals(new BigInteger(String.valueOf(digit)))).
                collect(Collectors.toList());
    }

    private List<BigInteger> calcBigintParallel() {

        BigInteger from = new BigInteger(from_big.toString());
        BigInteger to = new BigInteger(to_big.toString());

        // Get the list of all numbers
        // between the user-defined range
        List<BigInteger> numbersList = new LinkedList<>();
        while (from.compareTo(to) != 0){
            numbersList.add(from);
            from = from.add(BigInteger.ONE);
        }

        return numbersList.parallelStream().
                filter(x -> isPrime(x) && x.mod(BigInteger.TEN)
                        .equals(new BigInteger(String.valueOf(digit)))).
                collect(Collectors.toList());
    }

    void calculateToFile(String path) {

        // Check if the range is correct
        if (from_big.compareTo(to_big) == 1) {

            System.err.println("Incorrect range!");
            return;
        }

        // Check whether we can calculate
        // within the 'long' type range
        if (fitsLong) {

            System.out.println("Calculating using Long...");

            List<Long> primeNumbers;

            long startTime = System.nanoTime();
            primeNumbers = calcLongSequentially();
            long seqTime = System.nanoTime() - startTime;
            System.out.println("Sequential computation duration: " +
                    TimeUnit.NANOSECONDS.toMillis(seqTime));

            startTime = System.nanoTime();
            primeNumbers = calcLongParallel();
            long parTime = System.nanoTime() - startTime;
            System.out.println("Parallel computation duration: " +
                    TimeUnit.NANOSECONDS.toMillis(parTime));

            // Write results to the file
            writeToFile(primeNumbers, path);
        }
        else {

            // Calculate within
            // the BigInteger range
            System.out.println("The range does not fit in Long!\n" +
            "Calculating using BigInteger...");

            List<BigInteger> primeNumbers;

            long startTime = System.nanoTime();
            primeNumbers = calcBigintSequentially();
            long seqTime = System.nanoTime() - startTime;
            System.out.println("Sequential computation duration: " +
                    TimeUnit.NANOSECONDS.toMillis(seqTime));

            startTime = System.nanoTime();
            primeNumbers = calcBigintParallel();
            long parTime = System.nanoTime() - startTime;
            System.out.println("Parallel computation duration: " +
                    TimeUnit.NANOSECONDS.toMillis(parTime));

            // Write results to the file
            writeToFile(primeNumbers, path);
        }
    }

    private <T extends Number> void writeToFile(List<T> primeNumbers,  String path) {

        BufferedWriter outputWriter = null;
        try {
            File file = new File(path);
            outputWriter = new BufferedWriter(new FileWriter(file));
            String output = primeNumbers.size() + ": <";

            if (primeNumbers.size() > 0) {
                for (T prime : primeNumbers) {
                    output += prime.toString() + ", ";
                }

                output = output.substring(0, output.length() - 2);
            }

            output+=">";
            outputWriter.write(output);

        } catch ( IOException e ) {
            System.err.println("Unable to write to the file!");;
        } finally {
            if (outputWriter != null) {
                try {
                    outputWriter.close();
                } catch (IOException e) { /* Omitted */}
            }
        }
    }

    private boolean isPrime(long candidate) {

        return LongStream.rangeClosed(2, (long)(Math.sqrt(candidate)))
                .allMatch(n -> candidate % n != 0) && candidate > 1;
    }

    private boolean isPrime(BigInteger candidate) {

        if (!candidate.isProbablePrime(5))
            return false;

        BigInteger two = new BigInteger("2");
        if (!two.equals(candidate) && BigInteger.ZERO.equals(candidate.mod(two)))
            return false;

        for (BigInteger i = new BigInteger("3"); i.multiply(i).compareTo(candidate) < 1; i = i.add(two)) {
            if (BigInteger.ZERO.equals(candidate.mod(i)))
                return false;
        }

        return true;
    }
}
