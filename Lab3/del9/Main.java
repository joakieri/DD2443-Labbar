import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.Arrays;
import java.util.ArrayList;

public class Main {
    public static void main(String[] args) {
        int exponent = 7;
        int listSize = (int)Math.pow(10, exponent);
        Populate pop = new Populate();
        pop.generateBoth(listSize);
        pop.printMeanStd();
	ArrayList<LogEntry>[] log;
        LockfreeConcurrentSkipListSet skiplistUniform = new LockfreeConcurrentSkipListSet(25, log);
        LockfreeConcurrentSkipListSet skiplistNormal = new LockfreeConcurrentSkipListSet(25, log);
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


        double[][] ratioArr = {{0.5, 0.5, 0.0}, {0.25, 0.25, 0.50}, {0.05, 0.05, 0.90}};
        int[] threadsArr = {2,12,30,48};

        // int threadOption = 1;
        // int ratioOption = 0;
        
        int runs = 10;

        for (int ratioOption = 0; ratioOption < ratioArr.length; ratioOption++) {
            System.out.println("Started test for ratios " + ratioArr[ratioOption][0]*100 + "% add, " + ratioArr[ratioOption][1]*100 + "% remove, " + ratioArr[ratioOption][2]*100 + "% contains!");
            for (int threadOption = 0; threadOption < threadsArr.length; threadOption++) {
		log = new ArrayList[threadsArr[threadOption]];
		for (int a = 0; a <  threadsArr[threadOption]; a++)
			log[a] = new ArrayList();
                for (int run = 0; run < runs; run++){
                    
                    System.out.println("--Started test " + run + " for " + threadsArr[threadOption] + " threads!");

                    // System.out.println("--Started populating skiplists!");
                    // skiplistUniform = new LockfreeConcurrentSkipListSet(25);
                    // skiplistNormal = new LockfreeConcurrentSkipListSet(25);
                    // for (int i : uniformRandom) {
                    //     skiplistUniform.add(i);
                    // }
            
                    // for (int i : normalRandom) {
                    //     skiplistNormal.add(i);
                    // }
                    // System.out.println("--Done populating skiplists!");

                    int testSize = (int)Math.pow(10, exponent-1);
                    int[] testArrUniform = GenerateRandomArr.generateUniform(testSize);
                    int[] testArrNormal = GenerateRandomArr.generateNormal(testSize);
                    int[] instructionsArr = GenerateRandomArr.generateInstructions(testSize, ratioArr[ratioOption][0], ratioArr[ratioOption][1], ratioArr[ratioOption][2]);
                    ExecutorService pool = Executors.newFixedThreadPool(threadsArr[threadOption]);

                    

                    int[] threadsIndexes = new int[threadsArr[threadOption] + 1];
                    for (int i=0; i < threadsArr[threadOption]; i++) {
                        threadsIndexes[i] = i*((testSize)/threadsArr[threadOption]);
                    }

                    threadsIndexes[threadsArr[threadOption]] = testSize-1;
                
                
                    long startUniform = System.nanoTime();
                    for (int i=0; i < threadsArr[threadOption]; i++) {
                        int[] subArrUniform = Arrays.copyOfRange(testArrUniform, threadsIndexes[i], threadsIndexes[i + 1]);
                        int[] subInstructions = Arrays.copyOfRange(instructionsArr, threadsIndexes[i], threadsIndexes[i + 1]);
                        pool.execute(new ThreadTest(skiplistUniform, subArrUniform, subInstructions));
                    }
                    
                    int[] temp = {0};
                    ThreadTest threadValidator = new ThreadTest(skiplistUniform, temp, temp);
                    while(threadValidator.activeThreads.get() != 0) {}

                    long totalUniform = (System.nanoTime() - startUniform);
		    boolean logCheck = LogEntry.checkLog(log);
		    log.clear();
                    System.out.println("--Execution time uniform distribution for " + threadsArr[threadOption] + " threads: " + totalUniform + ", logCheck: " + logCheck);

                    long startNormal = System.nanoTime();
                    for (int i=0; i < threadsArr[threadOption]; i++) {
                        int[] subArrNormal = Arrays.copyOfRange(testArrNormal, threadsIndexes[i], threadsIndexes[i + 1]);
                        int[] subInstructions = Arrays.copyOfRange(instructionsArr, threadsIndexes[i], threadsIndexes[i + 1]);
                        pool.execute(new ThreadTest(skiplistUniform, subArrNormal, subInstructions));
                    }
                    
                    while(threadValidator.activeThreads.get() != 0) {}
                    pool.shutdown();

                    long totalNormal = (System.nanoTime() - startNormal);
		    logCheck = LogEntry.checkLog(log);
		    log.clear();
                    System.out.println("--Execution time normal distribution for " + threadsArr[threadOption] + " threads: " + totalNormal + ", logCheck: " + logCheck);
                    System.out.println("");
                }
                System.out.println("-----");
            }
            System.out.println("********");
        }
            
    }
        
}


class ThreadTest implements Runnable {
    static AtomicInteger activeThreads = new AtomicInteger();
    int[] instructions, randArr;
    LockfreeConcurrentSkipListSet skiplist;


    public ThreadTest(LockfreeConcurrentSkipListSet skiplist, int[] randArr, int[] instructions) {
        this.instructions = instructions;
        this.skiplist = skiplist;
        this.randArr = randArr;
    }

    public void run() {
        activeThreads.incrementAndGet();
        // System.out.println("Thread: " + Thread.currentThread().getId() + " has started!");
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
        // System.out.println("Thread: " + Thread.currentThread().getId() + " is done!");
        activeThreads.decrementAndGet();
    }

}
