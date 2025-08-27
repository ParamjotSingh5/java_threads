package semaphore;

public class ObjectLock {
    private boolean isCompleted = false;

    public static void main(String[] args) {
        ObjectLock objectLock = new ObjectLock();

        Thread workerThread = new Thread(() -> {
            try {
                // Simulate some work
                Thread.sleep(2000);
                System.out.println("Worker thread completed work.");
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // Restore interrupted status
            }
            objectLock.complete();
        });

        workerThread.start();

        System.out.println("Main thread waiting for completion...");
        objectLock.waitForCompletion();
        System.out.println("Main thread resumed after completion.");
    }

    public void waitForCompletion() {
        synchronized (this) {
            while (!isCompleted) {
                try {
                    this.wait();// Using Java Object's wait method to block the thread. Every Java Object have wait and notify methods.
                    // That means every Java Object can be used as a lock.
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt(); // Restore interrupted status
                }
            }
        }
    }

    public void complete() {
        synchronized (this) {
            isCompleted = true;
            this.notifyAll(); // Notify all waiting threads
        }
    }
}
