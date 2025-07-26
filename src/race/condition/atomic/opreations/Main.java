package race.condition.atomic.opreations;

import java.util.Random;

public class Main {

    public static void main(String[] args) {
        Metrics metrics = new Metrics();

        BusinessLogic businessLogicThread1 = new BusinessLogic(metrics);
        BusinessLogic businessLogicThread2 = new BusinessLogic(metrics);

        MetricsPrinter metricsPrinter = new MetricsPrinter(metrics);

        businessLogicThread1.start();
        businessLogicThread2.start();
        metricsPrinter.start();

    }

    public static class MetricsPrinter extends Thread {

        private Metrics metrics;

        public MetricsPrinter(Metrics metrics) {
            this.metrics = metrics;
        }


        @Override
        public void run() {
            try {
                Thread.sleep(100);
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            double currentAverage = metrics.getAverage();

            System.out.printf("Current average is %f\n", currentAverage);
        }
    }


    public static class BusinessLogic extends Thread {
        private Metrics metrics;
        private Random random = new Random();

        public BusinessLogic(Metrics metrics) {
            this.metrics = metrics;
        }

        @Override
        public void run() {
            long startTime = System.currentTimeMillis();

            try{
                Thread.sleep(random.nextInt(10));
            }
            catch (InterruptedException e) {
                e.printStackTrace();
            }

            long endTime = System.currentTimeMillis();

            metrics.addSample(endTime - startTime);
        }
    }

    public static class Metrics{
        private long count = 0;
        private volatile double average = 0.0;

        public synchronized void addSample(long sample){
            double currentSum = average * count;
            count++;
            average = (currentSum + sample)/count;
        }

        public double getAverage(){
            return average;
        }
    }

}
