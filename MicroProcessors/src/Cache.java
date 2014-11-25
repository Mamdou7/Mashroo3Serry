import java.util.ArrayList;


public class Cache {
	
	private ArrayList<CacheEntry[]>[] dataCache, instructionCache;
	private int tagSize, indexSize, dispSize, associativity;
	private int setRecords, blockWidth;
	private double[][] dataUsageTable;
	private double[][] insUsageTable;
	private boolean replacePolicy;
	private short[] memory;

	public Cache(int S, int L, int M, boolean replacePolicy, short[] memory, int cycles) {
//		To be fully associative, enter M = C
		int C = S / L;
		setRecords = C / M;
		blockWidth = L;
		dataCache = new ArrayList[C/M];
		instructionCache = new ArrayList[C/M];
		dispSize = (int)(Math.log(L)/Math.log(2));
		//TODO log m
		indexSize = (int)Math.ceil(Math.log(C/M)/Math.log(2));
		tagSize = 32 - indexSize - dispSize;

		associativity = M;
		dataUsageTable = new double[M][setRecords];
		insUsageTable = new double[M][setRecords];
		this.replacePolicy = replacePolicy;
		this.memory = memory;
		System.out.println("Sets " + associativity + " wid " + blockWidth + " lines " + setRecords );
		initCaches();
	}
	
	private void initCaches() {
		for(int i=0;i < dataCache.length;i++) {
			dataCache[i] = new ArrayList<CacheEntry[]>();
			instructionCache[i] = new ArrayList<CacheEntry[]>();
		}
	}
	
	public void hardInsert(int address, int value, String type) {
		System.out.println("ana fe hard insert");
		if(type.equals("Data")) {
			double least = Double.MAX_VALUE;
			int pos = -1;
			CacheEntry entry = new CacheEntry(tagSize, indexSize, dispSize, address);
			entry.setValue(value);
			int index = entry.getIndex();
			if(replacePolicy) {
				// LRU
				for(int i=0;i < dataUsageTable[index].length;i++) {
					if(dataUsageTable[index][i] < least) {
						least = dataUsageTable[index][i];
						pos = i;
					}
				}
				CacheEntry[] lis = new CacheEntry[blockWidth];
				lis[entry.getDisp()] = entry;
				
				for(int x=1; entry.getDisp() - x > -1; x++) {
					CacheEntry en = new CacheEntry(tagSize, indexSize, dispSize, entry.getAddress() - x);
					en.setValue(memory[address-x]);
					lis[en.getDisp()] = en;
					dataUsageTable[index][pos] = System.currentTimeMillis();
				}
				for(int x=1; entry.getDisp() + x < blockWidth; x++) {
					CacheEntry en = new CacheEntry(tagSize, indexSize, dispSize, entry.getAddress() + x);
					en.setValue(memory[address+x]);
					lis[en.getDisp()] = en;
					dataUsageTable[index][pos] = System.currentTimeMillis();
				}
				dataCache[index].add(pos, lis);
				dataUsageTable[index][pos]= System.currentTimeMillis();
			}else {
				int rand = (int)(Math.random() * setRecords) + 1;
				dataCache[index].get(rand)[entry.getDisp()] = entry;
			}
		}else {
			double least = Double.MAX_VALUE;
			int pos = -1;
			CacheEntry entry = new CacheEntry(tagSize, indexSize, dispSize, address);
			entry.setValue(value);
			int index = entry.getIndex();
			if(replacePolicy) {
				// LRU
				for(int i=0;i < insUsageTable[index].length;i++) {
					if(insUsageTable[index][i] < least) {
						least = insUsageTable[index][i];
						pos = i;
					}
				}
				instructionCache[index].get(pos)[entry.getDisp()] = entry;
				insUsageTable[index][pos] = System.currentTimeMillis();
			}else {
				int rand = (int)(Math.random() * setRecords) + 1;
				instructionCache[index].get(rand)[entry.getDisp()] = entry;
			}
		}
		
	}

	public boolean softInsert(int address, int value, String type) {
		if(type.equals("Data")) {
			System.out.println("ana fe softinsert");
			CacheEntry entry = new CacheEntry(tagSize, indexSize, dispSize, address);
			int index = entry.getIndex();
			entry.setValue(value);
			if(dataCache[index].size() < setRecords) {
				int size = dataCache[index].size();
				CacheEntry[] lis = new CacheEntry[blockWidth];
				lis[entry.getDisp()] = entry;
				
				for(int x=1; entry.getDisp() - x > -1; x++) {
					CacheEntry en = new CacheEntry(tagSize, indexSize, dispSize, entry.getAddress() - x);
					en.setValue(memory[address-x]);
					lis[en.getDisp()] = en;
					dataUsageTable[index][size] = System.currentTimeMillis();
				}
				for(int x=1; entry.getDisp() + x < blockWidth; x++) {
					CacheEntry en = new CacheEntry(tagSize, indexSize, dispSize, entry.getAddress() + x);
					en.setValue(memory[address+x]);
					lis[en.getDisp()] = en;
					dataUsageTable[index][size] = System.currentTimeMillis();
				}
				dataCache[index].add(lis);
				return true;
			}
			return false;
		}else {
			CacheEntry entry = new CacheEntry(tagSize, indexSize, dispSize, address);
			int index = entry.getIndex();
			entry.setValue(value);
			for(int i=0; i < instructionCache[index].size();i++) {
				if(instructionCache[index].get(i)[entry.getDisp()] == null) {
					instructionCache[index].get(i)[entry.getDisp()] = entry;
					insUsageTable[index][i] = System.currentTimeMillis();
					return true;
				}
			}
			if(instructionCache[index].size() < setRecords) {
				CacheEntry[] lis = new CacheEntry[blockWidth];
				instructionCache[index].add(lis);
				lis[entry.getDisp()] = entry;
				return true;
			}
		}
		return false;
	}

	public Integer getEntry(CacheEntry entry, String cacheType) {
		int index, tag, disp;
		Integer searched = null;
		tag = entry.getTag();
		index = entry.getIndex();
		disp = entry.getDisp();
		searched = search(index, disp, tag, cacheType);
		return searched;
	}

	private Integer search(int index, int disp, int tag, String type) {
		if(type.equals("Data")) {
			for(int i=0; i < dataCache[index].size();i++) {
				// check all words in each block
				CacheEntry thisOne = dataCache[index].get(i)[disp]; 
				if(thisOne != null && thisOne.getTag() == tag) {
					return thisOne.getValue(); 
				}
			}
			return null;
		}
		for(int i=0; i < instructionCache[index].size();i++) {
			// check all words in each block
			CacheEntry thisOne = instructionCache[index].get(i)[disp]; 
			if(thisOne != null && thisOne.getTag() == tag) {
				return thisOne.getValue(); 
			}
		}
		return null;
	}

	public int getTag() {
		return tagSize;
	}

	public void setTag(int tag) {
		this.tagSize = tag;
	}

	public int getIndex() {
		return indexSize;
	}

	public void setIndex(int index) {
		this.indexSize = index;
	}

	public int getDisp() {
		return dispSize;
	}

	public void setDisp(int disp) {
		this.dispSize = disp;
	}

}