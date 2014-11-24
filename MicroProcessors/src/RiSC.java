import java.util.ArrayList;


public class RiSC {

	/**
	 * @param args
	 */

	int[] registers = new int[8];
	Cache[] caches;
	static short[]memory = new short[2000];

	public int getValue(int address) {
		return 0;
	}

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		memory[5] = 10;
		memory[6] = 20;
		CacheManager mng = new CacheManager(1, memory);
		mng.createCache(24, 4, 3, true);
		int val = mng.getEntry(5, "Data");
		System.out.println("-----------> " + mng.getMisses() + " " + mng.getMemCalls() + " " + mng.getHits() + " " + val);
		System.out.println("HAHAHHAHA");
		val = mng.getEntry(6, "Data");
		System.out.println("HAHAHHAHA");
		System.out.println("-----------> " + mng.getMisses() + " " + mng.getMemCalls() + " " + mng.getHits() + " " + val);
		val = mng.getEntry(5, "Data");
		System.out.println("-----------> " + mng.getMisses() + " " + mng.getMemCalls() + " " + mng.getHits() + " " + val);
	}

}