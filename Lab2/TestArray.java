import java.util.Random;

class TestArray {
	static int[] create(int n) {
		int[] arr = new int[n];
		
		// Fill array
		for (int i = 0; i < n; i++)
			arr[i] = i + 1;
		
		// Scramble array
		Random rand = new Random();
		for (int i = 0; i < n; i++) {
			int j = rand.nextInt(n);
			int r = arr[i];
			arr[i] = arr[j];
			arr[j] = r;
		}

		return arr;
	}

	static boolean verify(int[] arr) {
		for (int i = 0; i < arr.length; i++)
			if (arr[i] != (i + 1))
				return false;
		
		return true;
	}
}

