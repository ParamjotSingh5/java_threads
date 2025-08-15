package semaphore;

import java.util.*;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Semaphore;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

public class Main {
    static int CAPACITY = 10;
    Semaphore producerPermits = new Semaphore(0);
    Semaphore consumerPermits = new Semaphore(CAPACITY);
    Queue<Integer> queue = new ArrayBlockingQueue<>(CAPACITY); // We have limited buffer of in stock items, but the producer does not have any loop breaker,
    // The producer run is permitted only when, a consumer have acquired and consumed an items from queue. In this way queue is not overwhelmed with tasks,
    // and we are blocking the producers to block more task as of now.
    Lock lock = new ReentrantLock();

    public static void main(String[] args) throws InterruptedException {
        Main main = new Main();

        System.out.println("Starting...");

        List<Thread> producerThreads = new ArrayList<>();
        for (int i = 0; i < CAPACITY; i++) {
            producerThreads.add(new Thread(main::produce));
        }

        List<Thread> consumerThreads = new ArrayList<>();
        for (int i = 0; i< CAPACITY; i++){
            consumerThreads.add( new Thread(main::consume));
        }

        consumerThreads.forEach(Thread::start);
        producerThreads.forEach(Thread::start);
    }

    private void produce() {
        while (true) {
            try{
                producerPermits.acquire();// In first iteration, this will block until the consumer has consumed an item
                // This ensures that we do not produce more items than the buffer can hold
                // In subsequent iterations, it will block until the consumer has consumed an item
                // and released the full semaphore
                // This allows the producer to produce an item only when there is space in the buffer
                // This is a classic producer-consumer problem where the producer waits for the consumer to consume
                // an item before producing a new one
                lock.lock();
                queue.offer(new Random().nextInt(CAPACITY));
                System.out.println("Produced new item, current queue: " + queue);
                lock.unlock();
                consumerPermits.release();
            }
            catch (InterruptedException e){
                System.out.println("Producer interrupted: " + e.getMessage());
            }
        }
    }

    private void consume()  {
        try{
            while (true) {
                consumerPermits.acquire();
                lock.lock();
                System.out.println("Pooled item: " + queue.poll());
                System.out.println("Queue: " + queue);
                lock.unlock();
                producerPermits.release();
            }
        }
        catch (InterruptedException e){
            System.out.println("Consumer interrupted: " + e.getMessage());
        }

    }
}
