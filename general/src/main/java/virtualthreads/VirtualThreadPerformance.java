package virtualthreads;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class VirtualThreadPerformance {

    private static final int NUMBER_OF_TASKS = 10000;

    public static void main(String[] args) {
        System.out.println("Starting IO-bound tasks...");
        long startTime = System.currentTimeMillis();
        performTasks();
        long endTime = System.currentTimeMillis();
        System.out.println("All tasks completed in " + (endTime - startTime) + " ms");
    }

    private static void performTasks(){
        try(ExecutorService executorService = Executors.newVirtualThreadPerTaskExecutor()){
            for (int i = 0; i < NUMBER_OF_TASKS; i++) {
                executorService.submit(VirtualThreadPerformance::blockingIOOperation);
            }
        }
    }

    private static void blockingIOOperation(){
        System.out.println("Executing a blocking IO task from thread: " + Thread.currentThread().getName());
        try {
            for(int i = 0; i < 100; i++){
                Thread.sleep(10);
            }
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
