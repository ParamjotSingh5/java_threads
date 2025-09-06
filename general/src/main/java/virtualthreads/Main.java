package virtualthreads;

import java.util.ArrayList;
import java.util.List;

public class Main {

    private final int NUMBER_OF_VIRTUAL_THREADS = 1000;

    public static void main(String[] args) throws InterruptedException {
        Runnable runnable = () -> System.out.println("Hello from " + Thread.currentThread());


        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < 1000; i++) {
            Thread thread = Thread.ofVirtual().unstarted(runnable);
            threads.add(thread);
        }

        for (Thread thread : threads) {
            thread.start();
        }

        for (Thread thread : threads) {
            thread.join();
        }
    }
}
