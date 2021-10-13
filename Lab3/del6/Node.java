import java.util.concurrent.atomic.AtomicMarkableReference;

class Node {
	AtomicMarkableReference<Node>[] next;
	final int num;

	public Node(int num, int levels) {
		this.num = num;
		next = new AtomicMarkableReference[levels];
		for (int i = 0; i < levels; i++)
			next[i] = new AtomicMarkableReference<Node>(null, false);
	}
}

