import java.util.Arrays;

class Part41 {
	public static void main(String[] args) {
		class Buffer {
			private int[] data;
			private boolean busy;

			public Buffer(int size) {
				data = new int[size];
				busy = false;
			}

			public void set(int i, int d) {
				data[i] = d;
			}

			public int get(int i) {
				return data[i];
			}

			public int size() {
				return data.length;
			}

			public String toString() {
				return Arrays.toString(data);
			}

			public void setBusy(boolean b) {
				busy = b;
			}

			public boolean getBusy() {
				return busy;
			}
		}

		class Producer implements Runnable {
			private Buffer buffer;
			
			public Producer(Buffer buffer) {
				this.buffer = buffer;
			}

			public void run() {
				synchronized(buffer) {
					buffer.setBusy(true);
					for (int i = 0; i < buffer.size(); i++)
						buffer.set(i, i + 1);
					buffer.notify();
					buffer.setBusy(false);
				}
			}
		}

		class Consumer implements Runnable {
			private Buffer buffer;

			public Consumer(Buffer buffer) {
				this.buffer = buffer;
			}

			public void run() {
				try {
				synchronized(buffer) {
					while(buffer.getBusy())
						buffer.wait();
					System.out.println(buffer);
				}}
				catch(InterruptedException e) {
					System.out.println(e);
				}
			}
		}

		Buffer buffer = new Buffer(10);

		Thread p = new Thread(new Producer(buffer));
		Thread c = new Thread(new Consumer(buffer));

		p.start();
		c.start();
	}
}
