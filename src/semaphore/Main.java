package semaphore;

import java.util.concurrent.Semaphore;

public class Main {
    Semaphore full = new Semaphore(0);
    Semaphore empty = new Semaphore(1);
    int buffer = 1;

    public static void main(String[] args) throws InterruptedException {
        Main main = new Main();
        Thread producer = new Thread(main::produce);
        Thread consumer = new Thread(main::consume);

        consumer.start();
        producer.start();
    }

    private void produce() {
        while (true) {
            try{
                System.out.println("Acquiring full semaphore");
                full.acquire();// In first iteration, this will block until the consumer has consumed an item
                // This ensures that we do not produce more items than the buffer can hold
                // In subsequent iterations, it will block until the consumer has consumed an item
                // and released the full semaphore
                // This allows the producer to produce an item only when there is space in the buffer
                // This is a classic producer-consumer problem where the producer waits for the consumer to consume
                // an item before producing a new one
                System.out.println("Producing item, current buffer size: " + buffer);
                buffer++;
                System.out.println("Releasing empty semaphore");
                empty.release();
                System.out.println("Item produced, new buffer size: " + buffer);
            }
            catch (InterruptedException e){
                System.out.println("Producer interrupted: " + e.getMessage());
            }
        }
    }

    private void consume()  {
        try{
            while (true) {
                System.out.println("Acquiring empty semaphore");
                empty.acquire();
                System.out.println("Consuming item, current buffer size: " + buffer);
                buffer--;
                System.out.println("Releasing full semaphore");
                full.release();
                System.out.println("Item consumed, new buffer size: " + buffer);
            }
        }
        catch (InterruptedException e){
            System.out.println("Consumer interrupted: " + e.getMessage());
        }

    }
}
