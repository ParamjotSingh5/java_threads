package deadlock;

import java.util.Random;

public class Main {

    public static void main(String[] args) {
        Intersection intersection = new Intersection(); // Shared resource representing the intersection of two roads. Both

        Thread trainA = new Thread(new TrainA(intersection), "Train A");
        Thread trainB = new Thread(new TrainB(intersection), "Train B");

        trainA.start();
        trainB.start();
    }

    public static class TrainB implements Runnable {
        private final Intersection intersection;
        private final Random random = new Random();

        public TrainB(Intersection intersection) {
            this.intersection = intersection;
        }

        @Override
        public void run() {
            while (true) {
                long sleepTime = random.nextInt(5);
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                }

                intersection.takeRoadB();
            }
        }
    }

    public static class TrainA implements Runnable {
        private final Intersection intersection;
        private final Random random = new Random();

        public TrainA(Intersection intersection) {
            this.intersection = intersection;
        }

        @Override
        public void run() {
            while (true) {
                long sleepTime = random.nextInt(5);
                try {
                    Thread.sleep(sleepTime);
                } catch (InterruptedException e) {
                    throw new RuntimeException(e);
                }

                intersection.takeRoadA();
            }
        }
    }

    public static class Intersection{
        private final Object roadA = new Object();
        private final Object roadB = new Object();

        public void takeRoadA(){
            synchronized (roadB){
                System.out.printf("Road A is locked by %s\n", Thread.currentThread().getName());

                synchronized (roadA){ // Acquiring lock on road B after acquiring lock on road A, so that Train-A can pass through intersection.
                    // If another thread "Train-B" has already acquired lock on road B, then Train-A thread will wait indefinitely for road B to be released, leading to a deadlock.
                    // This is a deadlock scenario because both trains are waiting for each other to release the locks on their respective roads.
                    System.out.println("Train is passing through road A");
                    try{
                        Thread.sleep(1); // Simulating the time taken to pass through the road
                    }
                    catch (InterruptedException e){
                    }
                }
            }
        }

        public void takeRoadB(){
            synchronized (roadB){
                System.out.printf("Road B is locked by %s\n", Thread.currentThread().getName());

                synchronized (roadA){ // If another thread "Train-A" has already acquired lock on road A, then Train-B thread will wait indefinitely for road A to be released, leading to a deadlock.
                    System.out.println("Train is passing through road B");
                    try{
                        Thread.sleep(1);
                    }
                    catch (InterruptedException e){
                    }
                }
            }
        }
    }
}
