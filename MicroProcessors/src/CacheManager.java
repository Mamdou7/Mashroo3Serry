


public class CacheManager {

	
	private Cache[] caches;
	private int hits, misses, cacheLevel, memCalls;
	private int nextLevelToInsertInto, dataNextLevelToInsertInto;
	private short[] memory;

	public CacheManager(int levels, short[] mem) {
		caches = new Cache[levels];
		//read specs TODO
		//Create caches TODO
		memory = mem;
	}
	
	public double calcAMAT() {
		double res = 0;
		for(int i=0;i<cacheLevel;i++) {
			int hits = caches[i].getHits();
			int misses = caches[i].getMisses();
			res += hits*caches[i].getAccess() + (misses / (hits+misses)) * caches[i].getPenalty();
		}
		return res;
	}

	public void calcHitsMisses() {
		for(int i=0;i<cacheLevel;i++)
			System.out.printf("Cache Level %d:\nHits = %d\n Misses = %d\n", (i+1), caches[i].getHits(), caches[i].getMisses());
	}

	public void createCache(int S, int L, int M, boolean replacePolicy, boolean writePolicy, int access, int penalty) {
		caches[cacheLevel++] = new Cache(S, L, M, replacePolicy, writePolicy, memory, access, penalty);
	}
	
	public void writeEntry(int address, String type, int newVal) {
		for(int i=0;i < cacheLevel;i++) {
			int tagSize = caches[i].getTag();
			int indexSize = caches[i].getIndex();
			int dispSize = caches[i].getDisp();
			CacheEntry toAdd = new CacheEntry(tagSize, indexSize, dispSize, address);
			Integer ret = caches[i].getEntry(toAdd, type);
			if(ret == null) {
				getFromMem(address, type);
				return;
			}else {
				caches[i].wrtieEntry(toAdd, type, newVal);
			}
			// assuming all levels have the same miss and hit rates TODO
//			CacheEntry toAdd2 = new CacheEntry(tagSize, indexSize, dispSize, 5);
//			Integer t = caches[i].getEntry(toAdd2, type);
//			System.out.println("DOUND " + ret + " " + t);
		}
	}
	public Integer getEntry(int address, String type) {
		// search the caches level by level
		for(int i=0;i < cacheLevel;i++) {
			int tagSize = caches[i].getTag();
			int indexSize = caches[i].getIndex();
			int dispSize = caches[i].getDisp();
			CacheEntry toAdd = new CacheEntry(tagSize, indexSize, dispSize, address);
			Integer ret = caches[i].getEntry(toAdd, type);
			// assuming all levels have the same miss and hit rates TODO
//			CacheEntry toAdd2 = new CacheEntry(tagSize, indexSize, dispSize, 5);
//			Integer t = caches[i].getEntry(toAdd2, type);
//			System.out.println("DOUND " + ret + " " + t);
			if(ret == null) {
				misses += 1;
			}else {
				hits += 1;
				return ret;
			}
		}
		return getFromMem(address, type);
	}
           
	public int getHits() {
		return hits;
	}

	public void setHits(int hits) {
		this.hits = hits;
	}

	public int getMisses() {
		return misses;
	}

	public void setMisses(int misses) {
		this.misses = misses;
	}

	public int getMemCalls() {
		return memCalls;
	}

	public void setMemCalls(int memCalls) {
		this.memCalls = memCalls;
	}

	public int getNextLevelToInsertInto() {
		return nextLevelToInsertInto;
	}

	public void setNextLevelToInsertInto(int nextLevelToInsertInto) {
		this.nextLevelToInsertInto = nextLevelToInsertInto;
	}

	private int getFromMem(int address, String type) {
		memCalls++;
		//TODO
		int value = memory[address];
		insertIntoCache(address, value, type);
		return value;
	}

	private void insertIntoCache(int address, int value, String type) {
		if(type.equals("Data")) {
			for(int i=0;i<cacheLevel;i++) {
				if(caches[i].softInsert(address, value, "Data")) {
					return;
				}
			}
			caches[dataNextLevelToInsertInto].hardInsert(address, value, "Data");
			dataNextLevelToInsertInto++;
			dataNextLevelToInsertInto %= cacheLevel;
		}else {
			for(int i=0;i<cacheLevel;i++) {
				if(caches[i].softInsert(address, value, "inst")) {
					return;
				}
			}
			caches[nextLevelToInsertInto].hardInsert(address, value, "inst");
			nextLevelToInsertInto++;
			nextLevelToInsertInto %= cacheLevel;
		}
	}
}