import java.util.HashMap;

class Part2 {
	public static void main(String[] args) {
		System.out.println(Runtime.getRuntime().availableProcessors());
		int n = 12;
		Thread[] threads = new Thread[n];
		for (int i = 0; i < n; i++) {
			threads[i] = new MyThread();
			threads[i].start();
		}
/*
		boolean con;
		do {
			con = false;
			for (int i = 0; i < n; i++)
				con = con || threads[i].isAlive();
		} while(con);
*/
		System.out.println(MyThread.getVal());
	}
}

class MyInt {
	private int val = 0;

	public synchronized void inc() {
		val++;
	}

	public synchronized int getVal() {
		return val;
	}
}

class MyThread extends Thread {
	private static MyInt val = new MyInt();

	@Override
	public void run() {
		for (int i = 0; i < 1000000; i++)
			val.inc();
	}
		
	public static int getVal() {
		return val.getVal();
	}	
}
	
