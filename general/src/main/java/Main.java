
public class Main {
    public static void main(String[] args) {
        Thread thread = new Thread(
                () -> {
                    System.out.println("We are now in thread: " + Thread.currentThread().getName());
                    System.out.println("Current thread priority: " + Thread.currentThread().getPriority());
                }
        );

        thread.setName("New worker thread");

        thread.setPriority(Thread.MAX_PRIORITY);

        System.out.println("We are now in thread: " + Thread.currentThread().getName());
        System.out.println("Starting thread: " + thread.getName());
        thread.start();
        try {
            thread.join(); // Wait for the thread to finish
        } catch (InterruptedException e) {
            System.out.println(e.getMessage());
        }
    }
}