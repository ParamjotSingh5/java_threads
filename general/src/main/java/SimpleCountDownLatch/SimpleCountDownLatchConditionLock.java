package SimpleCountDownLatch;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class SimpleCountDownLatchConditionLock {
    private int count;
    private final Lock lock = new ReentrantLock();
    private final Condition condition = lock.newCondition();

    public SimpleCountDownLatchConditionLock(int count) {
        this.count = count;
        if (count < 0) {
            throw new IllegalArgumentException("count cannot be negative");
        }
    }

    /**
     * Causes the current thread to wait until the latch has counted down to zero.
     * If the current count is already zero then this method returns immediately.
     */
    public void await() throws InterruptedException {
        lock.lock();
            while (count > 0) {
                this.condition.await();
            }
        lock.unlock();
    }

    /**
     *  Decrements the count of the latch, releasing all waiting threads when the count reaches zero.
     *  If the current count already equals zero then nothing happens.
     */
    public void countDown() {
        lock.lock();
            if (count > 0) {
                count--;

                if (count == 0) {
                    this.condition.signalAll();
                }
            }
        lock.unlock();
    }

    /**
     * Returns the current count.
     */
    public int getCount() {
        try{
            lock.lock();
            // just to ensure visibility of count variable
            return this.count;
        }
        finally {
            lock.unlock();
        }

    }
}
