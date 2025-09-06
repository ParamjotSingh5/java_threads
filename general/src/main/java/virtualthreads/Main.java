package virtualthreads;

public class Main {
    public static void main(String[] args) throws InterruptedException {
        Runnable runnable = () -> System.out.println("Hello from " + Thread.currentThread());

        Thread thread = Thread.ofPlatform().unstarted(runnable);

        thread.start();
        thread.join();
    }
}
