import java.math.BigInteger;
import java.util.ArrayList;
import java.util.List;

public class JoinThread {

    public static void main(String[] args) {
        List<Long> inputNumbers = List.of(5L, 10L, 20L, 30L, 50L);

        List<FactorialThread> threads = new ArrayList<>();

        for (Long inputNumber : inputNumbers) {
            FactorialThread factorialThread = new FactorialThread(inputNumber.intValue());
            threads.add(factorialThread);
        }

        for (FactorialThread factorialThread : threads) {
            factorialThread.start();
        }

        for (FactorialThread factorialThread : threads) {
            if(factorialThread.isFinished()) {
                System.out.println("Factorial of " + factorialThread.getInputNumber() + " is: " + factorialThread.getResult());
            } else {
                System.out.println("The calculation for input: " + factorialThread.getInputNumber() + " is in progress.");
            }
        }
    }


    private static class FactorialThread extends Thread {
        private final long inputNumber;
        private BigInteger result = BigInteger.ZERO;
        private boolean isFinished = false;

        public FactorialThread(int inputNumber) {
            this.inputNumber = inputNumber;
        }

        @Override
        public void run() {
            result = factorial(inputNumber);
            isFinished = true;
        }

        public long getInputNumber() {
            return inputNumber;
        }

        private BigInteger factorial(long n) {
            BigInteger tempResult = BigInteger.ONE;

            for (long i = n; i > 0; i--) {
                tempResult = tempResult.multiply(BigInteger.valueOf(i));
            }

            return tempResult;
        }

        public boolean isFinished() {
            return isFinished;
        }

        public BigInteger getResult() {
            return result;
        }

    }
}
