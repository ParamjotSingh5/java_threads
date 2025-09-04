import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;

public class Main {

    private static final int NUMBER_OF_TASKS = 20000;

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
            Thread.sleep(1000); // Simulate blocking IO with sleep
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}