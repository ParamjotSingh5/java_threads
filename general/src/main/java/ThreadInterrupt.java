public class ThreadInterrupt {

    public static void main(String[] args) {

        Thread blockingThread = new Thread(new BlockingThread());
        blockingThread.start();

        blockingThread.interrupt();

    }

    private static class BlockingThread implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(5000000); // Simulate a long-running task
            } catch (InterruptedException e) {
                System.out.println("Blocking thread interrupted: " + e.getMessage());
            }
        }
    }
}
