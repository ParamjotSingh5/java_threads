package atomic;

import java.util.concurrent.atomic.AtomicInteger;

public class Main {
    public static void main(String[] args) {
        InventoryCounter inventoryCounter = new InventoryCounter();
        ProducerThread producerThread =  new ProducerThread(inventoryCounter);
        ConsumerThread consumerThread = new ConsumerThread(inventoryCounter);

        producerThread.start();
        consumerThread.start();

        try {
            producerThread.join();
            consumerThread.join();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        System.out.println("Final inventory count: " + inventoryCounter.getItems());
    }

    public static class ConsumerThread extends Thread {

        private final InventoryCounter inventoryCounter;

        public ConsumerThread(InventoryCounter inventoryCounter) {
            this.inventoryCounter = inventoryCounter;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                inventoryCounter.decrement();
            }
        }
    }

    public static class ProducerThread extends Thread {

        private final InventoryCounter inventoryCounter;

        public ProducerThread(InventoryCounter inventoryCounter) {
            this.inventoryCounter = inventoryCounter;
        }

        @Override
        public void run() {
            for (int i = 0; i < 10000; i++) {
                inventoryCounter.increment();
            }
        }
    }

    public static class InventoryCounter {
        private  int items =0 ;

        public void increment() {
            items++;
            System.out.printf("Incremented, current items: %d%n", items);
        }

        public void decrement() {
            items--;
            System.out.printf("Decremented, current items: %d%n", items);
        }

        public int getItems() {
            return items;
        }
    }


}
