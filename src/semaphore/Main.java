package semaphore;

import java.util.concurrent.Semaphore;

public class Main {
    Semaphore full = new Semaphore(0);
    Semaphore empty = new Semaphore(1);
    int buffer = 0;

    public static void main(String[] args) throws InterruptedException {
        Main main = new Main();
        Thread producer = new Thread(main::produce);
        Thread consumer = new Thread(main::consume);

        producer.start();
        consumer.start();
    }

    private void produce() {
        while (true) {
            try{
                System.out.println("Acquiring full semaphore");
                full.acquire();
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
