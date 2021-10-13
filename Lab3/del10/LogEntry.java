import java.util.Collection;
import java.util.Arrays;
import java.util.Comparator;

class LogEntry {
	long time;
	int type;
	int number;
	boolean result;

	static final int CONTAINS = 0;
	static final int ADD = 1;
	static final int REMOVE = 2;

	public LogEntry(long ti, int ty, int n, boolean r) {
		time = ti;
		type = ty;
		number = n;
		result = r;
	}

	static boolean checkLog(Collection<LogEntry> log) {
		LogEntry[] arr = (LogEntry[])log.toArray();
		Arrays.sort(arr, new Comparator<LogEntry>() {
			public int compare(LogEntry a, LogEntry b) {
				return (int)a.time - (int)b.time;
			}}); 
		for (int i = arr.length-1; i >= 0; i--) {
			LogEntry a = arr[i];
			if (!a.result)
				continue;
			switch (arr[i].type) {
			case CONTAINS:
				for (int j = i-1; j >= 0; j--) {
					LogEntry b = arr[j];
					if (b.result && b.number == a.number) {
						if (b.type == ADD || b.type == CONTAINS)
							break;
						else if(b.type == REMOVE)
							return false;
					}
				}	
				break;
			case ADD:
				for (int j = i-1; j >= 0; j--) {
					LogEntry b = arr[j];
					if (b.result && b.number == a.number) {
						if (b.type == REMOVE)
							break;
						else if(b.type == ADD || b.type == CONTAINS)
							return false;
					}
				}
				break;
			case REMOVE:
				for (int j = i-1; j >= 0; j--) {
					LogEntry b = arr[j];
					if (b.result && b.number == a.number) {
						if (b.type == ADD || b.type == CONTAINS)
							break;
						else if(b.type == REMOVE)
							return false;
					}
				}	
				break;
			default:
			}
		}
		return true;
	}
}
