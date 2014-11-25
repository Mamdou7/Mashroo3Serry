


public class CacheManager {

	
	private Cache[] caches;
	private int hits, misses, cacheLevel, memCalls;
	private int nextLevelToInsertInto;
	private short[] memory;

	public CacheManager(int levels, short[] mem) {
		caches = new Cache[levels];
		//read specs TODO
		//Create caches TODO
		memory = mem;
	}

	public void createCache(int S, int L, int M, boolean replacePolicy, int cycles) {
		caches[cacheLevel++] = new Cache(S, L, M, replacePolicy, memory, cycles);
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
			caches[nextLevelToInsertInto].hardInsert(address, value, "Data");
			nextLevelToInsertInto++;
			nextLevelToInsertInto %= cacheLevel;
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