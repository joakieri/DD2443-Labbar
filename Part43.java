class ChopStick {
	private boolean taken = false;

	public synchronized void pickUp() throws InterruptedException {
		while (taken)
			wait();
		taken = true;
	}

	public synchronized void putDown() {
		taken = false;
		notify();	
	}
}

class Philosopher extends Thread {
	private int timesEaten = 0;
	private ChopStick[] sticks;
	private int position;

	public Philosopher(ChopStick[] sticks, int position) {
		this.sticks = sticks;
		this.position = position;
	}

	public int getTimesEaten() {
		return timesEaten;		
	}

	public void run() {
		try {
		int left = position;
		int right = (left + 1) % sticks.length;
		for (;;) {
			if (position % 2 == 0) {
				sticks[left].pickUp();
				sticks[right].pickUp();
			}
			else {
				sticks[right].pickUp();
				sticks[left].pickUp();
			}

			Thread.sleep(1000); //Eating
			timesEaten++;

			if (position % 2 == 0) {
				sticks[left].putDown();
				sticks[right].putDown();
			}
			else {
				sticks[right].putDown();
				sticks[left].putDown();
			}

			Thread.sleep(1000); //Thinking
		}
		} catch(Exception e) {
			System.out.println(e);
		}
	}
}

class Part43 {
	public static void main(String[] args) {
		int n;
		try {
			n = Integer.parseInt(args[0]);
		} catch(Exception e) {
			n = 5;
		}
		ChopStick[] sticks = new ChopStick[n];
		for (int i = 0; i < n; i++)
			sticks[i] = new ChopStick();

		Philosopher[] philosophers = new Philosopher[n];
		for (int i = 0; i < n; i++) {
			philosophers[i] = new Philosopher(sticks, i);
			philosophers[i].start();
		}

		try {
		for (;;) {
			Thread.sleep(5000);
			for (int j = 0; j < n; j++)
				System.out.print(philosophers[j].getTimesEaten() + " ");
			System.out.println();
		}}
		catch (Exception e) {
			System.out.println(e);
		}
	}
}

