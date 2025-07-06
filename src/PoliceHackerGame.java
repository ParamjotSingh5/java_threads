import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class PoliceHackerGame {

    public static final int MAX_PASSWORD = 9999;


    public static void main(String[] args) {

        Random rand = new Random();

        Vault vault = new Vault(rand.nextInt(MAX_PASSWORD));

        List<Thread> threads = new ArrayList<>();

        threads.add(new AscendingHackerThread(vault));
        threads.add(new DescendingHackerThread(vault));
        threads.add(new PoliceThread());

        for (Thread thread : threads) {
            thread.start();
        }


    }


    private static class Vault {
        private final int password;

        public Vault(int password) {
            this.password = password;
        }

        public boolean tryPassword(int guess) {
            return guess == password;
        }
    }

    private static abstract class HackerThread extends Thread {
        protected Vault vault;

        public HackerThread(Vault vault) {
            this.vault = vault;
            this.setName("HackerThread-" + this.getClass().getName());
            this.setPriority(Thread.MAX_PRIORITY);
        }

        @Override
        public void start(){
            System.out.println("Starting hacker thread: " + this.getClass().getName());
            super.start();
        }
    }

    private static class AscendingHackerThread extends HackerThread {
        public AscendingHackerThread(Vault vault) {
            super(vault);
        }

        @Override
        public void run() {
            for (int i = 0; i <= MAX_PASSWORD; i++) {
                if (vault.tryPassword(i)) {
                    System.out.println("Hacker " + this.getName() + " cracked the vault with password: " + i);
                    System.exit(0);
                }
            }
        }
    }

    private static class DescendingHackerThread extends HackerThread {
        public DescendingHackerThread(Vault vault) {
            super(vault);
        }

        @Override
        public void run() {
            for (int i = MAX_PASSWORD; i >= 0; i--) {
                if (vault.tryPassword(i)) {
                    System.out.println("Hacker " + this.getName() + " cracked the vault with password: " + i);
                    System.exit(0);
                }
            }
        }
    }

    private static class PoliceThread extends Thread {


        @Override
        public void run() {

            for (int i =10; i > 0; i--) {
                try {
                    Thread.sleep(1000); // Simulate monitoring for 1 second
                } catch (InterruptedException e) {
                    System.out.println("PoliceThread- " + this.getName() + " interrupted.");
                }

                System.out.println("Police " + this.getName() + " is monitoring the hackers... " + i + " seconds left.");
            }

            System.out.println("Police " + this.getName() + " has caught the hackers!");
            System.exit(0); // End the program when police catches the hackers
        }
    }

}
