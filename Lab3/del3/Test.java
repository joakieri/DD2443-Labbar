import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Arrays;
import java.util.ArrayList;


public class Test {
    public static void main(String[] args) {
        int exponent = 2; //7
        int listSize = (int)Math.pow(10, exponent);
        Populate pop = new Populate();
        pop.generateBoth(listSize);
        pop.printMeanStd();
	LCSLS3 skiplistUniform = new LCSLS3(25);
        LCSLS3 skiplistNormal = new LCSLS3(25);
        int[] uniformRandom = pop.getUniformRandom();
        int[] normalRandom = pop.getNormalRandom();

        System.out.println("Started populating skiplists!");
        for (int i : uniformRandom) {
            skiplistUniform.add(i);
        }

        for (int i : normalRandom) {
            skiplistNormal.add(i);
        }
        System.out.println("Done populating skiplists!");

        int threadOption = 1;
        int ratioOption = 0;
        int testSize = (int)Math.pow(10, exponent-1);
        double[][] ratioArr = {{0.5, 0.5, 0.0}, {0.25, 0.25, 0.50}, {0.05, 0.05, 0.90}};
        int[] testArrUniform = GenerateRandomArr.generateUniform(testSize);
        int[] testArrNormal = GenerateRandomArr.generateNormal(testSize);
        int[] instructionsArr = GenerateRandomArr.generateInstructions(testSize, ratioArr[ratioOption][0], ratioArr[ratioOption][1], ratioArr[ratioOption][2]);
        int[] threadsArr = {2,12,30,48};
        ExecutorService pool = Executors.newFixedThreadPool(threadsArr[threadOption]);


        int[] threadsIndexes = new int[threadsArr[threadOption] + 1];
        for (int i=0; i < threadsArr[threadOption]; i++) {
            threadsIndexes[i] = i*((testSize)/threadsArr[threadOption]);
        }

        threadsIndexes[threadsArr[threadOption]] = testSize-1;
    
    
        long start = System.nanoTime();
        for (int i=0; i < threadsArr[threadOption]; i++) {
            int[] subArrUniform = Arrays.copyOfRange(testArrUniform, threadsIndexes[i], threadsIndexes[i + 1]);
            int[] subInstructions = Arrays.copyOfRange(instructionsArr, threadsIndexes[i], threadsIndexes[i + 1]);
            pool.execute(new ThreadTest(skiplistUniform, subArrUniform, subInstructions));
        }
        
        int[] temp = {0};
        ThreadTest threadValidator = new ThreadTest(skiplistUniform, temp, temp);
        while(threadValidator.activeThreads.get() != 0) {}
        pool.shutdown();

        long total = (System.nanoTime() - start)/1000000; 
        System.out.println("Execution time: " + total);
    }
}


class ThreadTest implements Runnable {
    static AtomicInteger activeThreads = new AtomicInteger();
    int[] instructions, randArr;
    LCSLS3 skiplist;


    public ThreadTest(LCSLS3 skiplist, int[] randArr, int[] instructions) {
        this.instructions = instructions;
        this.skiplist = skiplist;
        this.randArr = randArr;
    }

    public void run() {
        activeThreads.incrementAndGet();
        System.out.println("Thread: " + Thread.currentThread().getId() + " has started!");
        for (int i = 0; i < instructions.length; i++) {
            if (instructions[i] == 0) {
                this.skiplist.add(this.randArr[i]);
            }
            else if (instructions[i] == 1) {
                this.skiplist.remove(this.randArr[i]);
            }
            else if (instructions[i] == 2) {
                this.skiplist.contains(this.randArr[i]);
            }
        }
        System.out.println("Thread: " + Thread.currentThread().getId() + " is done!");
        activeThreads.decrementAndGet();
    }

}
