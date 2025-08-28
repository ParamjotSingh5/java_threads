package overview;

import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.ReentrantLock;

public class Main {

    public static void main(String[] args) {
        ObjectLocking objectLocking = new ObjectLocking();
        ExplicitLocking explicitLocking = new ExplicitLocking();

        Thread t1 = new Thread(() -> {
            try {
                objectLocking.declareSuccess();
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        });

        Thread t2 = new Thread(() -> {
            try {
                explicitLocking.declareSuccess();
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        });

        t1.start();
        t2.start();


        try {
            Thread.sleep(2000); // Simulate some work with sleep
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        objectLocking.finishWork();
        explicitLocking.finishWork();
    }

    public static class ObjectLocking{
        boolean isCompleted = false;

        public synchronized void declareSuccess () throws InterruptedException {
            while (!isCompleted){
                wait();
            }
            System.out.println("ObjectLocking: Task is completed");
        }

        public synchronized void finishWork(){
            isCompleted = true;
            notifyAll();
        }
    }


    public static class ExplicitLocking{
        ReentrantLock lock = new ReentrantLock();
        Condition condition = lock.newCondition();
        private boolean isCompleted = false;

        public void declareSuccess(){
            lock.lock();
            try{
                while(!isCompleted){
                    condition.await();
                }

                System.out.println("ExplicitLocking: Task is completed");
            }
            catch (InterruptedException e){
                Thread.currentThread().interrupt();
            }
            finally {
                lock.unlock();
            }
        }

        public void finishWork(){
            lock.lock();
            try {
                isCompleted = true;
                condition.signalAll();
            }
            finally {
                lock.unlock();
            }
        }
    }
}
