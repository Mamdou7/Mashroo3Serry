import java.util.ArrayList;


public class Cache {
	
	private ArrayList<CacheEntry[]>[] dataCache, instructionCache;
	private int tagSize, indexSize, dispSize, associativity;
	private int setRecords, blockWidth;
	private double[][][] dataUsageTable;
	private double[][][] insUsageTable;
	private boolean replacePolicy;
	private short[] memory;

	public Cache(int S, int L, int M, boolean replacePolicy, short[] memory) {
//		To be fully associative, enter M = C
		int C = S / L;
		setRecords = C / M;
		blockWidth = L;
		dataCache = new ArrayList[M];
		instructionCache = new ArrayList[M];
		dispSize = (int)(Math.log(L)/Math.log(2));
		indexSize = (int)(Math.log(C/M)/Math.log(2));
		tagSize = 32 - indexSize - dispSize;
		
		associativity = M;
		dataUsageTable = new double[M][setRecords][L];
		insUsageTable = new double[M][setRecords][L];
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
		if(type.equals("Data")) {
			double least = Double.MAX_VALUE;
			int pos = -1;
			CacheEntry entry = new CacheEntry(tagSize, indexSize, dispSize, address);
			entry.setValue(value);
			int index = entry.getIndex();
			if(replacePolicy) {
				// LRU
				for(int i=0;i < dataUsageTable[index].length;i++) {
					if(dataUsageTable[index][i][entry.getDisp()] < least) {
						least = dataUsageTable[index][i][entry.getDisp()];
						pos = i;
					}
				}
				dataCache[index].get(pos)[entry.getDisp()] = entry;
				dataUsageTable[index][pos][entry.getDisp()] = System.currentTimeMillis();
			}
			int rand = (int)(Math.random() * setRecords) + 1;
			dataCache[index].get(rand)[entry.getDisp()] = entry;
		}else {
			double least = Double.MAX_VALUE;
			int pos = -1;
			CacheEntry entry = new CacheEntry(tagSize, indexSize, dispSize, address);
			entry.setValue(value);
			int index = entry.getIndex();
			if(replacePolicy) {
				// LRU
				for(int i=0;i < insUsageTable[index].length;i++) {
					if(insUsageTable[index][i][entry.getDisp()] < least) {
						least = insUsageTable[index][i][entry.getDisp()];
						pos = i;
					}
				}
				instructionCache[index].get(pos)[entry.getDisp()] = entry;
				insUsageTable[index][pos][entry.getDisp()] = System.currentTimeMillis();
			}
			int rand = (int)(Math.random() * setRecords) + 1;
			instructionCache[index].get(rand)[entry.getDisp()] = entry;
		}
		
	}

	public boolean softInsert(int address, int value, String type) {
		if(type.equals("Data")) {
			CacheEntry entry = new CacheEntry(tagSize, indexSize, dispSize, address);
			int index = entry.getIndex();
			entry.setValue(value);
//			for(int i=0; i < dataCache[index].size();i++) {
//				if(dataCache[index].get(i)[entry.getDisp()] == null) {
//					dataCache[index].get(i)[entry.getDisp()] = entry;
//					
//					for(int x=1; entry.getDisp() - x > -1; x++) {
//						CacheEntry en = new CacheEntry(tagSize, indexSize, dispSize, entry.getAddress() - x);
//						en.setValue(memory[address-x]);
//						System.out.println("add " + entry.getAddress());
//						System.out.println(en.getAddress() + " " + en.getValue());
//						dataCache[index].get(i)[en.getDisp()] = en;
//						dataUsageTable[index][i][en.getDisp()] = System.currentTimeMillis();
//					}
//					for(int x=1; entry.getDisp() + x < blockWidth; x++) {
//						CacheEntry en = new CacheEntry(tagSize, indexSize, dispSize, entry.getAddress() + x);
//						en.setValue(memory[address+x]);
//						dataCache[index].get(i)[en.getDisp()] = en;
//						dataUsageTable[index][i][en.getDisp()] = System.currentTimeMillis();
//					}
//					dataUsageTable[index][i][entry.getDisp()] = System.currentTimeMillis();
//					return true;
//				}
//			}
			System.out.println("ad " + setRecords);
			if(dataCache[index].size() < setRecords) {
				int size = dataCache[index].size();
				CacheEntry[] lis = new CacheEntry[blockWidth];
				lis[entry.getDisp()] = entry;
				
				System.out.println("Sizat " + indexSize + " " + dispSize);
				for(int x=1; entry.getDisp() - x > -1; x++) {
					CacheEntry en = new CacheEntry(tagSize, indexSize, dispSize, entry.getAddress() - x);
					en.setValue(memory[address-x]);
					System.out.println("add " + entry.getAddress());
					System.out.println(en.getAddress() + " " + en.getValue());
					lis[en.getDisp()] = en;
					System.out.println("Edeny " + en.getAddress() + " " + en.getDisp());
					dataUsageTable[index][size][en.getDisp()] = System.currentTimeMillis();
				}
				for(int x=1; entry.getDisp() + x < blockWidth; x++) {
					CacheEntry en = new CacheEntry(tagSize, indexSize, dispSize, entry.getAddress() + x);
					System.out.println("Edeny " + en.getIndex() + " " + en.getAddress() + " " + en.getDisp());
					en.setValue(memory[address+x]);
					lis[en.getDisp()] = en;
					dataUsageTable[index][size][en.getDisp()] = System.currentTimeMillis();
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
					insUsageTable[index][i][entry.getDisp()] = System.currentTimeMillis();
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
		System.out.println("first of all " + entry.getIndex() + " " + entry.getAddress() + " " + entry.getDisp());
		int index, tag, disp;
		Integer searched = null;
		tag = entry.getTag();
		index = entry.getIndex();
		disp = entry.getDisp();
		searched = search(index, disp, tag, cacheType);
		System.out.println("get ret " + searched);
		return searched;
	}

	private Integer search(int index, int disp, int tag, String type) {
		System.out.println("ins " + index + " ");
		if(type.equals("Data")) {
			for(int i=0; i < dataCache[index].size();i++) {
				// check all words in each block
				CacheEntry thisOne = dataCache[index].get(i)[disp]; 
				System.out.println(thisOne.getAddress() + " in " + thisOne.getValue());
				if(thisOne != null && thisOne.getTag() == tag) {
					System.out.println("RETURNED");
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