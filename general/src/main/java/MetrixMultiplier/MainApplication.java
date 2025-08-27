package MetrixMultiplier;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Scanner;
import java.util.StringJoiner;

public class MainApplication {

    private static final String INPUT_FILE = "./out/matricesMultiplier/matrices.txt";
    private static final String OUTPUT_FILE = "./out/matricesMultiplier/matrices_results.txt";
    private static final int N = 10;

    public static void main(String[] args) throws IOException {
        ThreadSafeQueue threadSafeQueue = new ThreadSafeQueue();
        File inputFile = new File(INPUT_FILE);
        File outputFile = new File(OUTPUT_FILE);

        MatricesReaderProducer matricesReader = new MatricesReaderProducer(new FileReader(inputFile), threadSafeQueue);
        MatricesMultiplierConsumer matricesConsumer = new MatricesMultiplierConsumer(new FileWriter(outputFile), threadSafeQueue);

        matricesConsumer.start();
        matricesReader.start();
    }

    private static class MatricesMultiplierConsumer extends Thread {
        private final ThreadSafeQueue queue;
        private final FileWriter fileWriter;

        public MatricesMultiplierConsumer(FileWriter fileWriter, ThreadSafeQueue queue) {
            this.fileWriter = fileWriter;
            this.queue = queue;
        }

        @Override
        public void run()  {
            while(true){
                MatricesPair pair = queue.remove();
                if(pair == null) {
                    System.out.println("No more matrices to read from the queue, consumer is terminating");
                    break;
                }

                float[][] multiplicationResult = multiplyMatrices(pair.matrix1, pair.matrix2);

                try{
                    saveMatrixToFile(fileWriter, multiplicationResult);
                } catch (Exception e){
                    e.printStackTrace();
                }
            }

            try {
                fileWriter.flush();
                fileWriter.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void saveMatrixToFile(FileWriter fileWriter, float[][] matrix) throws IOException {
            MatricesGenerator.writeToFile(fileWriter, matrix, N);
        }

        private float[][] multiplyMatrices(float[][] m1, float[][] m2){
            float[][] result = new float[N][N];

            for(int r = 0; r < N; r++){
                for(int c = 0; c < N; c++){
                    for(int adderPointer = 0; adderPointer < N; adderPointer++){
                        result[r][c] += m1[r][adderPointer] * m2[adderPointer][c];
                    }
                }
            }

            return result;
        }

    }

    private static class MatricesReaderProducer extends Thread {
        private final Scanner scanner;
        private final ThreadSafeQueue queue;

        public MatricesReaderProducer(FileReader reader, ThreadSafeQueue queue) {
            this.scanner = new Scanner(reader);
            this.queue = queue;
        }

        @Override
        public void run() {
            while(true){
                float[][] matrix1 = readMatrix();
                float[][] matrix2 = readMatrix();

                if(matrix1 == null || matrix2 == null){
                    queue.terminate();
                    System.out.println("No more matrices to read. Producer Thread is terminating");
                    return;
                }

                MatricesPair matricesPair = new MatricesPair();
                matricesPair.matrix1 = matrix1;
                matricesPair.matrix2 = matrix2;

                queue.add(matricesPair);
            }
        }

        private float[][] readMatrix(){
            float[][] result = new float[N][N];
            for(int r = 0; r < N; r++){
                if(!scanner.hasNext()){
                    return null;
                }

                String[] line = scanner.nextLine().split(",");
                for(int c = 0; c < N; c++){
                    result[r][c] = Float.parseFloat(line[c]);
                }
            }

            scanner.nextLine();
            return result;
        }
    }

    private static class ThreadSafeQueue{
        private final Queue<MatricesPair> queue = new LinkedList<>();
        private boolean isEmpty = true;
        private boolean isTerminate = false;

        public synchronized void add(MatricesPair matricesPair) {
            queue.add(matricesPair);
            isEmpty = false;
            this.notifyAll(); // notify the waiting threads to recheck the condition
        }

        public synchronized MatricesPair remove(){
            MatricesPair matricesPair;
            while(isEmpty && !isTerminate) {
                try {
                    this.wait();
                } catch (InterruptedException ignored) {
                }
            }

            if (queue.size() == 1) {
                isEmpty = true;
            }

            if (queue.isEmpty() && isTerminate) {
                return null;
            }

            System.out.println("queue size " + queue.size());

            matricesPair = queue.remove();

            return matricesPair;
        }

        public synchronized void terminate() {
            isTerminate = true;
            this.notifyAll();
        }
    }

    private static class MatricesPair {
        public float[][] matrix1;
        public float[][] matrix2;
    }
}
