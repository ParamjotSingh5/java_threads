import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class Main {

    private static final int NUMBER_OF_TASKS = 1000;

    public static void main(String[] args) {
        System.out.println("Starting IO-bound tasks...");
        long startTime = System.currentTimeMillis();
        performTasks();
        long endTime = System.currentTimeMillis();
        System.out.println("All tasks completed in " + (endTime - startTime) + " ms");
    }

    private static void performTasks(){
        try(ExecutorService executorService = Executors.newFixedThreadPool(1000)){
            for (int i = 0; i < NUMBER_OF_TASKS; i++) {
                executorService.submit(Main::blockingIOOperation);
            }
        }
    }

    private static void blockingIOOperation(){
        System.out.println("Executing a blocking IO task from thread: " + Thread.currentThread().getName());
        try {
           for(int i = 0; i < 100; i++){
               Thread.sleep(10);
               // Problem with Thread-Per-Task model for IO-bound tasks:
               // Price of Context switches:
               //  - OS is trying to fully utilize CPU
               //  - As soon a there is a blocking operation, OS unschedules the thread
               //  - Too many threads and frequent blocking calls lead to CPU being busy running OS code that manages threads
               //  - Context switch is expensive, it takes time to save and restore thread state
               // Threadshing: A situation where the CPU is busy switching between threads instead of executing actual tasks.
           }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}