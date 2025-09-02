package atomic.reference;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.atomic.LongAdder;
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
        private final LongAdder stepCounter = new LongAdder();

        public void push(T value) {
            for (int spins = 0; ; spins = backoff(spins)) {
                StackNode<T> currentHeadNode = head.get();
                new StackNode<>(value).next = currentHeadNode;
                if (head.compareAndSet(currentHeadNode, new StackNode<T>(value))) {
                    stepCounter.increment();
                    return;
                }
                stepCounter.increment();
            }
        }

        public T pop() {
            for (int spins = 0; ; spins = backoff(spins)) {
                StackNode<T> currentHeadNode = head.get();
                if (currentHeadNode == null) {
                    stepCounter.increment();
                    return null;
                }
                if (head.compareAndSet(currentHeadNode, currentHeadNode.next)) {
                    stepCounter.increment();
                    return currentHeadNode.value;
                }
            }
        }

        public Long getCounter() {
            return stepCounter.longValue();
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
