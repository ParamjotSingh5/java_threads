package atomic.reference;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.LockSupport;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Stack<Integer> stack = new LockFreeStack<>();
        Random random = new Random();

        for (Thread thread : getThreads(stack, random)) {
            thread.start();
        }

        Thread.sleep(10000);

        System.out.printf("%,d operations were performed in 10 seconds %n", stack.getCounter());
    }

    private static List<Thread> getThreads(Stack<Integer> stack, Random random) {
        List<Thread> threads = new ArrayList<>();

        for (int i = 0; i < 2; i++) {
            Thread producerThread = new Thread(() -> {
                while (true) {
                    stack.push(random.nextInt());
                }
            });
            producerThread.setDaemon(true);
            threads.add(producerThread);

            Thread consumerThread = new Thread(() -> {
                while (true) {
                    stack.pop();
                }
            });
            consumerThread.setDaemon(true);
            threads.add(consumerThread);
        }
        return threads;
    }

    // Classic Treiber stack, also called optimistic concurrency.
    public static class LockFreeStack<T> implements Stack<T> {
        private final AtomicReference<StackNode<T>> head = new AtomicReference<>();
        private final AtomicLong stepCounter = new AtomicLong(0);

        public void push(T value) {
            StackNode<T> newHeadNode = new StackNode<>(value);

            while (true) {
                StackNode<T> currentHeadNode = head.get();
                newHeadNode.next = currentHeadNode;
                if (head.compareAndSet(currentHeadNode, newHeadNode)) {
                    break;
                } else {
                    LockSupport.parkNanos(1);
                }
            }
            stepCounter.incrementAndGet();
        }

        public T pop() {
            StackNode<T> currentHeadNode = head.get();
            StackNode<T> newHeadNode;

            while (currentHeadNode != null) {
                newHeadNode = currentHeadNode.next;
                if (head.compareAndSet(currentHeadNode, newHeadNode)) {
                    break;
                } else {
                    LockSupport.parkNanos(1);
                    currentHeadNode = head.get();
                }
            }
            stepCounter.incrementAndGet();
            return currentHeadNode != null ? currentHeadNode.value : null;
        }

        public Long getCounter() {
            return stepCounter.get();
        }


        private static int backoff(int i) {
            if (i < 10) {
                Thread.onSpinWait(); // JDK 9+
                return i + 1;
            } else {
                // cap the backoff; tune for your workload
                LockSupport.parkNanos(50);
                return 0;
            }
        }
    }

    public static class StandardStack<T> implements Stack<T> {
        private StackNode<T> head;
        private Long stepCounter = 0L;

        public synchronized void push(T t){
            StackNode<T> node = new StackNode<>(t);
            node.next = head;
            head = node;
            stepCounter++;
        }

        public synchronized T pop(){
            if(head == null){
                return null;
            }

            T value = head.value;
            head = head.next;
            stepCounter++;
            return value;
        }

        public Long getCounter(){
            return stepCounter;
        }
    }

    public interface Stack<T>{
        void push(T value);
        T pop();
        Long getCounter();
    }

    private static class StackNode<T>{
        public T value;
        public StackNode<T> next;

        public StackNode(T value) {
            this.value = value;
        }
    }
}
