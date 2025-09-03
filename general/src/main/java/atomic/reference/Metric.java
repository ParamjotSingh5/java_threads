package atomic.reference;

import java.util.concurrent.atomic.AtomicReference;

public class Metric {

    public static void main(String[] args) {
        Metric metric = new Metric();

        Thread t1 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                metric.addSample(10); // Adding sample value 10 1000 times, total sum = 10000.
            }
        });

        Thread t2 = new Thread(() -> {
            for (int i = 0; i < 1000; i++) {
                metric.addSample(20);
                // Adding sample value 20 1000 times, total sum = 20000.
                // Overall, we have 2000 samples with a total sum of 30000.
                // So the average should be 30000 / 2000 = 15.0
            }
        });

        t1.start();
        t2.start();

        try {
            t1.join();
            t2.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Average: " + metric.getAverage()); // Should print 15.0
    }

    private static class InternalMetric{
        public long count;
        public long sum;
    }

    private final AtomicReference<InternalMetric> internalMetric = new AtomicReference<>(new InternalMetric());

    public void addSample(long sample) {
        InternalMetric currentState;
        InternalMetric newState;
        do {
            currentState = internalMetric.get();
            newState = new InternalMetric();
            newState.sum = currentState.sum + sample;
            newState.count = currentState.count + 1;
        } while (!internalMetric.compareAndSet(currentState, newState));
    }

    public double getAverage() {
        InternalMetric newResetState = new InternalMetric();
        InternalMetric currentState;
        double average;
        do {
            currentState = internalMetric.get();
            average = (double)currentState.sum / currentState.count;
        } while (!internalMetric.compareAndSet(currentState,  newResetState));

        return average;
    }
}
