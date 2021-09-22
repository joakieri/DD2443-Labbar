class QuickSort1 {
	static void sort(int[] arr, int l, int r) {
		if (l >= r)
			return;
		int pivot = arr[r-1];
		int i = l;
		int j = r-1;
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
		sort(arr, l, j);
		sort(arr, j+1, r);
	}

	public static void main(String[] args) {
		int n = 1000000;
		int[] arr = TestArray.create(n);
		boolean res;
		long start, total;
		
		start = System.nanoTime();
		sort(arr, 0, n);
		total = System.nanoTime() - start;
		res = TestArray.verify(arr);
		
		System.out.println("QuickSort1");
		System.out.println("Verified: " + (res ? "Passed" : "Failed"));
		System.out.println("Processors: " + Runtime.getRuntime().availableProcessors());
		System.out.println("Time(ns): " + total);
	}
}

