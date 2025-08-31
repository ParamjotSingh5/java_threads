package SimpleCountDownLatch;

public class CountDownLatchExample {
    public static void main(String[] args) throws InterruptedException {
        int workerCount = 3;
        SimpleCountDownLatchConditionLock latch = new SimpleCountDownLatchConditionLock(workerCount);

        for (int i = 1; i <= workerCount; i++) {
            Thread worker = new Thread(new Worker(latch, i));
            worker.start();
        }

        System.out.println("Main thread waiting for workers to finish...");
        latch.await();  // Blocks until count reaches 0
        System.out.println("All workers finished. Main thread continues!");
    }

    record Worker(SimpleCountDownLatchConditionLock latch, int id) implements Runnable {

        @Override
            public void run() {
                try {
                    System.out.println("Worker " + id + " is doing work...");
                    Thread.sleep(1000 + id * 500L); // Simulate work
                    System.out.println("Worker " + id + " finished.");
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                } finally {
                    latch.countDown(); // Decrement the count
                }
            }
        }
}
