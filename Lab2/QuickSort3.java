import java.util.concurrent.RecursiveAction;
import java.util.concurrent.ForkJoinPool;

class QuickSort3 extends RecursiveAction {
	static int[] arr;
	int left, right;

	QuickSort3(int l, int r) {
		left = l;
		right = r;
	}

	protected void compute() {
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
		
		QuickSort3 q1, q2;
		if (left < (j-1)) {
			q1 = new QuickSort3(left, j);
			q1.fork();
		}
		if ((j+1) < (right-1)) {
			q2 = new QuickSort3(j+1, right);
			q2.fork();
		}
	}

	public static void main(String[] args) {
		int n = 1000000;
		arr = TestArray.create(n);
		boolean res;
		long start, total;
		int p = Runtime.getRuntime().availableProcessors();
		ForkJoinPool pool = new ForkJoinPool(p);

		start = System.nanoTime();
		pool.invoke(new QuickSort3(0, n));
		while(!pool.isQuiescent())
			;
		total = System.nanoTime() - start;
		res = TestArray.verify(arr);
		
		System.out.println("QuickSort3");
		System.out.println("Verified: " + (res ? "Passed" : "Failed"));
		System.out.println("Processors: " + p);
		System.out.println("Time(ns): " + total);
	}
}

