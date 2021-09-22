import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicInteger;

class QuickSort2 implements Runnable {
	static int[] arr;
	static ExecutorService pool;
	static AtomicInteger activeCount;
	int left, right;

	QuickSort2(int l, int r) {
		left = l;
		right = r;
	}

	public void run() {
		int pivot = arr[right-1];
		int i = left;
		int j = right-1;
		while (i < j) {
			if (arr[i] > pivot) {
				arr[j] = arr[i];
				j--;
				arr[i] = arr[j]; 
			}
			else
				i++;
		}
		arr[j] = pivot;
		
		if (left < (j-1)) {
			activeCount.incrementAndGet();
			pool.execute(new QuickSort2(left, j));
		}
		if ((j+1) < (right-1)) {
			activeCount.incrementAndGet();
			pool.execute(new QuickSort2(j+1, right));
		}
		activeCount.decrementAndGet();
	}
	
	public static void main(String[] args) {
		int n = 1000000;
		arr = TestArray.create(n);
		boolean res;
		long start, total;
		int p = Runtime.getRuntime().availableProcessors();
		pool = Executors.newFixedThreadPool(p);
		activeCount = new AtomicInteger(0);

		start = System.nanoTime();
		activeCount.incrementAndGet();
		pool.execute(new QuickSort2(0, n));
		while (activeCount.get() != 0)
			;
		total = System.nanoTime() - start;
		pool.shutdown();
		res = TestArray.verify(arr);
		
		System.out.println("QuickSort2");
		System.out.println("Verified: " + (res ? "Passed" : "Failed"));
		System.out.println("Processors: " + p);
		System.out.println("Time(ns): " + total);
	}
}

