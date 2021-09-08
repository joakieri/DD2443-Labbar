class Part3 {
	public static void main(String[] args) {
		Thread t1 = new Thread(new Program(1));
		Thread t2 = new Thread(new Program(2));
		t1.start();
		t2.start();
	}
}

class MyInt {
	private int val = 0;

	public void inc() {
		val++;
	}

	public int getVal() {
		return val;
	}
}

class Program implements Runnable {
	private static MyInt val = new MyInt();
	private boolean con = false;
	private int num = 0;

	public Program(int i) {
		num = i;
	}

	@Override
	public void run() {
		if (num == 1) {
			synchronized(val) {
			con = true;
			for (int i = 0; i< 1000000; i++)
				val.inc();
			con = false;
			val.notify();
			}
		}
		else if (num == 2) {
			try{
			synchronized(val) {
			while(con)
				val.wait();
			System.out.println(val.getVal());
			}
			}
			catch(InterruptedException e) {
				System.out.println(e);
			}
		}
	}
}
