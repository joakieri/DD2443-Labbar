import java.util.Random;
import java.util.ArrayList;
import java.util.concurrent.locks.ReentrantLock;

class LCSLS6 {
	final int maxLevel;
	final Node head;
	final Node tail;
	private Random rand = new Random();
	private ArrayList<Integer> randLevelDist;
	private ReentrantLock lock = new ReentrantLock();

	public LCSLS6(int levels) {
		maxLevel = levels - 1;
		head = new Node(Integer.MIN_VALUE, levels);
		tail = new Node(Integer.MAX_VALUE, levels);
		for (int i = 0; i < levels; i++)
			head.next[i].set(tail, false);

		randLevelDist = new ArrayList();		
		int n = ((int)Math.pow(2, maxLevel));
		int m = 1;		
		while(n > 0) {
			for (int i = 0; i < n; i++)
				randLevelDist.add(new Integer(m));
			m++;
			n /= 2;
		}
	}

	private int getRandLevel() {
		int i = rand.nextInt(randLevelDist.size());
		return randLevelDist.get(i).intValue();
	}

	public boolean contains(int num) {	
		boolean[] marked = { false };
		boolean changed;
		Node prev = null, curr = null, next = null;

		prev = head;
		for (int level = maxLevel; level >= 0; level--) {
			curr = prev.next[level].getReference();
			while (true) { 
				next = curr.next[level].get(marked);
				while (marked[0]) {
					curr = next;
					next = curr.next[level].get(marked);
				}
				
				if (curr.num < num) {
					prev = curr;
					curr = next;
				}
				else
					break;
			}
		}
		lock.lock();
		long t = System.nanoTime();
 		boolean r = curr.num == num; //LIN. POINT
		lock.unlock();
		return r;
	}

	public boolean find(int num, Node[] preds, Node[] succs) {
		boolean[] marked = { false };
		boolean changed;
		Node prev = null, curr = null, next = null;

		retry:
		while (true) { 
			prev = head;
			for (int level = maxLevel; level >= 0; level--) {

				curr = prev.next[level].getReference();
				while (true) { 
					next = curr.next[level].get(marked);
					while (marked[0]) {
						changed = !prev.next[level].compareAndSet(curr, next, false, false);
						if (changed)
							continue retry;

						curr = prev.next[level].getReference();
						next = curr.next[level].get(marked);
					}
					
					if (curr.num < num) {
						prev = curr;
						curr = next;
					}
					else
						break;
				}
				preds[level] = prev;
				succs[level] = curr;
			}
			return curr.num == num;
		}
	}

	public boolean add(int num) {
		int levels = getRandLevel();
		Node[] preds = new Node[maxLevel + 1];
		Node[] succs = new Node[maxLevel + 1];
		long sample;

		while (true) {
			boolean found = find(num, preds, succs);
			lock.lock();
			sample = System.nanoTime();
			if (found) { // LIN. POINT
				lock.unlock();
				return false;
			}
			else {
				lock.unlock();
				Node newNode = new Node(num, levels);
				for (int level = 0; level < levels; level++) {
					Node next = succs[level];
					newNode.next[level].set(next, false);
				}
				
				Node prev = preds[0];
				Node next = succs[0];
				lock.lock();
				sample = System.nanoTime();
				boolean r = prev.next[0].compareAndSet(next, newNode, false, false); // LIN. POINT
				lock.unlock();
				if (!r)
					continue;

				for (int level = 1; level < levels; level++) {
					while (true) {
						prev = preds[level];
						next = succs[level];
						if (prev.next[level].compareAndSet(next, newNode, false, false))
							break;
						find(num, preds, succs);
					}
				}
				
				return true;
			}
		}
	}

	public boolean remove(int num) {
		Node[] preds = new Node[maxLevel + 1];
		Node[] succs = new Node[maxLevel + 1];
		Node next;
		boolean[] marked = { false };
		boolean linearized = false;
		long sample;
		
		while (true) { 
			boolean found = find(num, preds, succs);
			lock.lock();	
			sample = System.nanoTime();
			if (!found) { // LIN. POINT
				lock.unlock();
				return false;
			}
			else {
				lock.unlock();
				Node nodeToRemove = succs[0]; 
				for (int level = nodeToRemove.next.length-1; level >= 1; level--) {
					next = nodeToRemove.next[level].get(marked);
					while (!marked[0]) {
						lock.lock();
						sample = System.nanoTime();
						boolean r = nodeToRemove.next[level].compareAndSet(next, next, false, true); // LIN. POINT
						lock.unlock();
						if (!linearized && r) {
							linearized = true;
						}
						next = nodeToRemove.next[level].get(marked);
					}
				}
			
				next = nodeToRemove.next[0].get(marked);
				while (true) {
					lock.lock();
					sample = System.nanoTime();
					boolean iMarkedIt = nodeToRemove.next[0].compareAndSet(next, next, false, true); // LIN. POINT
					lock.unlock();

					if (!linearized && iMarkedIt) {
						linearized = true;
					}
					if (iMarkedIt) {
						find(num, preds, succs);
						return true;
					}
					
					next = succs[0].next[0].get(marked);
					lock.lock();
					sample = System.nanoTime();
					if (!iMarkedIt && marked[0]) { // LIN. POINT
						lock.unlock();
						return false;
					}
					lock.unlock();
				}
			}
		}
	}

	public String toString() {
		String str = new String();
		boolean[] marked = { false };
		Node n = head.next[0].get(marked);
		while (n != tail) {
			if (!marked[0])
				str += "(" + n.num + ":" + n.next.length + ")";
			n = n.next[0].get(marked);
		}
		return str;
	}
}
