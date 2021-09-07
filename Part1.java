class Part1 {
	public static void main(String[] args) {
		class MyThread extends Thread {
			@Override
			public void run() {
				System.out.println("Hello world " + getId());
			}
		}

		MyThread[] threads = new MyThread[5];
		for (int i = 0; i < 5; i++)
			threads[i] = new MyThread();


		for (MyThread t : threads)
			t.start();
	}
}

/*
class Part1 extends Runnable {
	public void run() {
		long id = Thread.currentThread().getId();
		System.out.println("Hello world " + id);
	}

	public static void main(String[] args) {
		MyThread[] threads = new MyThread[5];
		for (int i = 0; i < 5; i++) {
			threads[i] = new MyThread();
			threads[i].start();
		}
	}

}
*/
