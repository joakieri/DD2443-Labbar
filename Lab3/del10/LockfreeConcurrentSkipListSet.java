import java.util.Random;
import java.util.Collection;
import java.util.ArrayList;

class LockfreeConcurrentSkipListSet {
	final int maxLevel;
	final Node head;
	final Node tail;
	private Random rand = new Random();
	private ArrayList<Integer> randLevelDist;
	private Collection<LogEntry> log; 

	public LockfreeConcurrentSkipListSet(int levels, Collection<LogEntry> log) {
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
		this.log = log;
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
		long t = System.nanoTime(); //LIN. POINT
		boolean r = curr.num == num;
		log.add(new LogEntry(t, LogEntry.CONTAINS, num, r));
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
			sample = System.nanoTime();
			if (found) { // LIN. POINT
				log.add(new LogEntry(sample, LogEntry.ADD, num, false));
				return false;
			}
			else {
				Node newNode = new Node(num, levels);
				for (int level = 0; level < levels; level++) {
					Node next = succs[level];
					newNode.next[level].set(next, false);
				}
				
				Node prev = preds[0];
				Node next = succs[0];
				sample = System.nanoTime();
				boolean r = prev.next[0].compareAndSet(next, newNode, false, false); // LIN. POINT	
				if (r)
					log.add(new LogEntry(sample, LogEntry.ADD, num, true));
				else
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
			sample = System.nanoTime();
			if (!found) { // LIN. POINT	
				log.add(new LogEntry(sample, LogEntry.REMOVE, num, false));
				return false;
			}
			else {
				Node nodeToRemove = succs[0];
				for (int level = nodeToRemove.next.length-1; level >= 1; level--) {
					next = nodeToRemove.next[level].get(marked);
					while (!marked[0]) {
						sample = System.nanoTime();
						boolean r = nodeToRemove.next[level].compareAndSet(next, next, false, true); // LIN. POINT
						if (!linearized && r) {
							log.add(new LogEntry(sample, LogEntry.REMOVE, num, true));
							linearized = true;
						}
						next = nodeToRemove.next[level].get(marked);
					}
				}
			
				next = nodeToRemove.next[0].get(marked);
				while (true) {
					sample = System.nanoTime();
					boolean iMarkedIt = nodeToRemove.next[0].compareAndSet(next, next, false, true); // LIN. POINT
					if (!linearized && iMarkedIt) {
						log.add(new LogEntry(sample, LogEntry.REMOVE, num, true));
						linearized = true;
					}

					if (iMarkedIt) {
						find(num, preds, succs);
						return true;
					}
					
					next = succs[0].next[0].get(marked);
					sample = System.nanoTime();
					if (!iMarkedIt && marked[0]) { // LIN. POINT
						log.add(new LogEntry(sample, LogEntry.REMOVE, num, false));
						return false;
					}
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
