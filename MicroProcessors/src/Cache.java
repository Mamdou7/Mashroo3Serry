import java.util.ArrayList;


public class Cache {
	
	private ArrayList<Integer>[] dataCache, instructionCache;
	private int tag, index, disp;

	public Cache(int S, int L, int M) {
//		To be fully associative, enter M = C
		int C = S / L;
		dataCache = new ArrayList[C];
		instructionCache = new ArrayList[C];
		disp = (int)(Math.log(L)/Math.log(2));
		index = (int)(Math.log(C)/Math.log(2));
		tag = 32 - index - disp;
	}

	public void addEntry() {
		
	}
}