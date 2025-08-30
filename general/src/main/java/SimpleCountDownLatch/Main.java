package SimpleCountDownLatch;

public class Main {
    public static void main(String[] args) {
        SimpleCountDownLatchObjectLock latch = new SimpleCountDownLatchObjectLock(3);

        Runnable task = () -> {
            try {
                System.out.println(Thread.currentThread().getName() + " is waiting on the latch.");
                latch.await();
                System.out.println(Thread.currentThread().getName() + " has been released from the latch.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        };

        Thread t1 = new Thread(task, "Thread-1");
        Thread t2 = new Thread(task, "Thread-2");
        Thread t3 = new Thread(task, "Thread-3");

        t1.start();
        t2.start();
        t3.start();

        try {
            Thread.sleep(1000); // Simulate some work with sleep
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        System.out.println("Main thread is counting down the latch.");
        latch.countDown();
        System.out.println("Current count: " + latch.getCount());
        latch.countDown();
        System.out.println("Current count: " + latch.getCount());
        latch.countDown();
        System.out.println("Current count: " + latch.getCount());
    }
}
