import java.util.Map;
import java.util.Arrays;

class Livelock {
	private boolean[] flag = new boolean[2];
	
	public void lock(int i) { // i: Thread ID
		int j = 1 - i; 
		flag[i] = true;
		while (flag[j]) {
			flag[i] = false;
			while (flag[j]) {}
			flag[i] = true;
		}
	}

	public void unlock(int i) {
		flag[i] = false;
	}
}

class MyThread extends Thread {
	private int id;
	private Livelock lock1, lock2;
	private static int a = 0;

	public MyThread(int id, Livelock lock1, Livelock lock2) {
		this.id = id;
		this.lock1 = lock1;
		this.lock2 = lock2;
	}

	private void task1() throws InterruptedException {
		lock1.lock(id);
		Thread.sleep(1000);
		lock2.lock(id);
		a++;
		Thread.sleep(1000);
		lock2.unlock(id);
		lock1.unlock(id);
	}

	private void task2() throws InterruptedException {
		lock2.lock(id);
		Thread.sleep(1000);
		lock1.lock(id);
		a++;
		Thread.sleep(1000);
		lock1.unlock(id);
		lock2.unlock(id);
	} 

	public void run() {
		try {
		for(;;) {
			if (id == 0) {
				task1();
				task2();
			}
			else {
				task2();
				task1();	
			}
		}} catch(Exception e) {}		
	}
}

class Part5 {
	public static void main(String[] args) {
		Livelock lock1 = new Livelock();
		Livelock lock2 = new Livelock();		

		MyThread t1 = new MyThread(0, lock1, lock2);
		MyThread t2 = new MyThread(1, lock1, lock2);
		
		t2.start();
		t1.start();

		for (;;) {
			System.out.println(t1.getState() + " " + t2.getState());
			try {Thread.sleep(500);} catch(Exception e) {}
		}	
	}
}
