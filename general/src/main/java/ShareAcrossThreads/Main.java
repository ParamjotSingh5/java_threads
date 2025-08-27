package ShareAcrossThreads;

import java.util.Objects;

public class Main {
    public static void main(String[] args) throws InterruptedException {

        InventoryCounter inventoryCounter = new InventoryCounter(0);


        Thread incrementingThread = new IncrementingThread(inventoryCounter);
        Thread decrementingThread = new DecrementingThread(inventoryCounter);

        incrementingThread.start();
        decrementingThread.start();

        incrementingThread.join();
        decrementingThread.join();

        System.out.println("Final inventory count: " + inventoryCounter.getCount());
    }

    public static  class DecrementingThread extends Thread{
        private final InventoryCounter inventoryCounter;

        public DecrementingThread(InventoryCounter inventoryCounter) {
            this.inventoryCounter = inventoryCounter;
        }

        @Override
        public void run(){
            for (int i = 0; i < 10000; i++) {
                inventoryCounter.decrement();
            }
        }
    }

    public static class IncrementingThread extends Thread {

        private final InventoryCounter inventoryCounter;

        public IncrementingThread(InventoryCounter inventoryCounter) {
            this.inventoryCounter = inventoryCounter;
        }

        @Override
        public void run(){
            for(int i = 0; i < 10000; i++){
                inventoryCounter.increment();
            }
        }
    }

    public static class InventoryCounter {
        private int count;

        public InventoryCounter(int initialCount) {
            this.count = initialCount;
        }

        final Object lock = new Object();

        public void increment() {
            synchronized (lock) { // achieving atomicity for a number of operations by using synchronized block.
                count++; // This is not an atomic operation, it performs 3 separate operations a read, increment, and write.
                // If another threads access this resource in between these operations, it can lead data discrepancies.

                // Atomic operation: An operation or a set of operations that are completed at once, not intermediate steps, all or nothing.
            }
        }

        public void decrement() {
            synchronized (lock) {
                count--; // Similar to increment, this is not atomic and can lead
            }
        }

        public int getCount() {
            return count;
        }
    }

    public static class MinMaxMetrics {

        private volatile long minValue; // make these variables volatile to prevent from instruction reordering.
        private volatile long maxValue;

        /**
         * Initializes all member variables
         */
        public MinMaxMetrics() {
            this.maxValue = Long.MIN_VALUE;
            this.minValue = Long.MAX_VALUE;
        }

        /**
         * Adds a new sample to our metrics.
         */
        public void addSample(long newSample) {
            synchronized (this) { // make this operation atomic in nature.
                this.minValue = Math.min(newSample, this.minValue);
                this.maxValue = Math.max(newSample, this.maxValue);
            }
        }

        /**
         * Returns the smallest sample we've seen so far.
         */
        public long getMin() {
            return this.minValue;
        }

        /**
         * Returns the biggest sample we've seen so far.
         */
        public long getMax() {
            return this.maxValue;
        }
    }
}
