package semaphore;

import java.util.Random;
import java.util.concurrent.locks.Condition;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class LockCondition {

    static Lock lock = new ReentrantLock();
    static Condition condition = lock.newCondition();
    static boolean flag = false;
    static String username = null, password = null;

    public static void main(String[] args) {

        Thread uiThread = new Thread(UIThread(), "UI Thread");
        Thread dbThread = new Thread(fetchUserFromDatabase(), "DB Thread");

        uiThread.start();
        dbThread.start();

    }

    private static Runnable UIThread() {
        return new Runnable() {

            final Random random = new Random();

            @Override
            public void run() {
                while(!flag) {
                    boolean usernameFlag = random.nextInt(100) > 50;
                    boolean passwordFlag = random.nextInt(100) > 50;

                    lock.lock();

                    try {

                        if (usernameFlag) {
                            username = "user" + random.nextInt(1000);
                            System.out.println("Username set: " + username);
                        }

                        if (passwordFlag) {
                            password = "pass" + random.nextInt(1000);
                            System.out.println(" Password set: " + password);
                        }

                        System.out.println("UI Thread: Signaling DB thread that username and password are set.");
                        condition.signal();
                        // Signal the DB thread that the username and password are set
                        // This will wake up the DB thread if it is waiting on the condition variable

                    } finally {
                        lock.unlock();
                    }
                }
            }
        };
    }

    private static Runnable fetchUserFromDatabase()  {

        return  () -> {
            lock.lock();
            try {
                while (username == null || password == null) {
                    try {
                        System.out.println("Waiting for username and password to be set...");
                        // Wait until the UI thread sets the username and password
                        // This is where the condition variable comes into play
                        // The DB thread will wait until the UI thread signals that the data is ready
                        // This is a blocking call, it will release the lock and wait until signaled
                        // by the UI thread.
                        // Once signaled, it will re-acquire the lock and continue execution.
                        // This is a way to achieve synchronization between threads.
                        condition.await();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                }
            } finally {
                flag = true;
                lock.unlock();
            }

            try {
                Thread.sleep(4000);
                System.out.printf("Fetched user from database: Username: %s, Password: %s\n", username, password);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            // Simulate database IO access call
        };
    }


}
