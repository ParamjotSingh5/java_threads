package ShareAcrossThreads;

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

        public void increment() {
            count++;
        }

        public void decrement() {
            count--;
        }

        public int getCount() {
            return count;
        }
    }
}
