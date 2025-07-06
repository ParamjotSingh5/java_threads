import java.math.BigInteger;

public class InterruptLongRunningTask {

    public static void main(String[] args) {
        BigInteger base = new BigInteger("2");
        BigInteger exponent = new BigInteger("1000000");

        Thread longComputationThread = new Thread(new LongComputationThread(base, exponent));
        longComputationThread.start();

        try {
            // Let the thread run for a short time before interrupting
            Thread.sleep(100);
            longComputationThread.interrupt(); // Interrupt the long computation thread
        } catch (InterruptedException e) {
            System.out.println("Main thread interrupted: " + e.getMessage());
        }
    }


    private record LongComputationThread(BigInteger base, BigInteger exponent) implements Runnable {

        @Override
            public void run() {
                System.out.println(base + " raised to the power of " + exponent + " is: " + computePower(base, exponent));
            }

            private BigInteger computePower(BigInteger base, BigInteger exponent) {
                BigInteger result = BigInteger.ONE;


                for(BigInteger i= BigInteger.ZERO; i.compareTo(exponent) != 0; i=i.add(BigInteger.ONE)) {
                    if (Thread.currentThread().isInterrupted()) {
                        System.out.println("Thread interrupted, stopping computation.");
                        return BigInteger.ZERO;
                    }
                    result = result.multiply(base);
                }

                return result;
            }
        }
}
