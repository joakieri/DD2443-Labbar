class Part42 implements Runnable {
	private static Semaphore sem = new Semaphore(5);

	public void run() {
		for (int i = 0; i < 3; i++) {
			sem.waits();
			try {
			Thread.sleep(2000);
			} catch(Exception e) {}
			sem.signal();
		}
	}

	public static void main(String[] args) {
		Thread[] threads = new Thread[20];
		for (int i = 0; i < 20; i++) {
			threads[i] = new Thread(new Part42());
			threads[i].start();	
		}
		
		boolean con;
		do {
			
			con = false;
			for (Thread t : threads) {
				System.out.print(t.getName() + ":" + t.getState() + " ");
				con = con || t.isAlive();
			}
			System.out.println();
			try { Thread.sleep(1000); } catch(Exception e) {}
		} while(con);
	}
}

class Semaphore {
	private int i;

	public Semaphore(int i) {
		this.i = i;
	}

	public synchronized void signal() {
		i++;
		this.notify();
	}

	public synchronized void waits() {
		while(i < 0)
			try {
			this.wait();
			} catch(Exception e) {}
		i--;
	}
}
