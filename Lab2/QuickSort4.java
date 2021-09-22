import java.util.Arrays;
import java.util.stream.IntStream;
import java.util.OptionalInt;
import java.util.function.Supplier;

class QuickSort4 {
	static IntStream parallelSort(Supplier<IntStream> sup) {
		OptionalInt pivot = sup.get().findFirst();
		if (pivot.isPresent()) {
			return IntStream.concat(IntStream.concat(
				parallelSort(() -> sup.get().parallel().skip(1).filter(x -> x <= pivot.getAsInt())),
				IntStream.of(pivot.getAsInt())),
				parallelSort(() -> sup.get().parallel().skip(1).filter(x -> x > pivot.getAsInt())));
		}
		else
			return IntStream.empty();
	}

	public static void main(String[] args) {
		// DOES NOT WORK WITH n = 1000000
		int n = 10000;
		final int[] arr = TestArray.create(n);
		int[] arr_sorted;
		boolean res;
		long start, total;
		
		start = System.nanoTime();
		arr_sorted = parallelSort(() -> Arrays.stream(arr)).toArray();
		total = System.nanoTime() - start;
		res = TestArray.verify(arr_sorted);
		
		System.out.println("QuickSort4");
		System.out.println("Verified: " + (res ? "Passed" : "Failed"));
		System.out.println("Processors: " + Runtime.getRuntime().availableProcessors());
		System.out.println("Time(ns): " + total);
	}
}

