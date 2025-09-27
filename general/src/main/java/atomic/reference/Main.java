package atomic.reference;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        StandardStack<Integer> stack = new StandardStack<>();
        Random random = new Random();

        List<Thread> threads = getThreads(stack, random);

        for (Thread thread : threads) {
            thread.start();
        }

        Thread.sleep(10000);

        System.out.printf("%,d operations were performed in 10 seconds %n", stack.getCounter());
    }

    private static List<Thread> getThreads(StandardStack<Integer> stack, Random random) {
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

    public static class StandardStack<T>{
        private StackNode<T> head;
        private int stepCounter = 0;

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

        public int getCounter(){
            return stepCounter;
        }
    }

    private static class StackNode<T>{
        public T value;
        public StackNode<T> next;

        public StackNode(T value) {
            this.value = value;
        }
    }
}
