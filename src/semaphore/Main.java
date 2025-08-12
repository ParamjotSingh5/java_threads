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
                full.acquire();
                buffer++;
                empty.release();
            }
            catch (InterruptedException e){
                System.out.println("Producer interrupted: " + e.getMessage());
            }
        }
    }

    private void consume()  {
        try{
            while (true) {
                empty.acquire();
                buffer--;
                full.release();
            }
        }
        catch (InterruptedException e){
            System.out.println("Consumer interrupted: " + e.getMessage());
        }

    }
}
