import java.util.ArrayList;


public class Cache {
	
	private ArrayList<CacheEntry[]>[] dataCache, instructionCache;
	private int tagSize, indexSize, dispSize, associativity, writeBufferSize;
	private int setRecords, blockWidth, memCall, hits, misses, accessTime, missPenalty;
	private double[][] dataUsageTable;
	private double[][] insUsageTable;
	// writePolicy 1 -> through, 0 -> back
	private boolean replacePolicy, writePolicy;
	private short[] memory;
	private ArrayList<CacheEntry> writeBuffer;

	public Cache(int S, int L, int M, boolean replacePolicy, boolean writePolicy, short[] memory, int accessTime, int missPenalty) {
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
		this.missPenalty = missPenalty;
		associativity = M;
		dataUsageTable = new double[M][setRecords];
		insUsageTable = new double[M][setRecords];
		this.replacePolicy = replacePolicy;
		this.memory = memory;
		writeBuffer = new ArrayList<CacheEntry>();
		this.writeBufferSize = 5;
		this.accessTime = accessTime;
//		System.out.println("Sets " + associativity + " wid " + blockWidth + " lines " + setRecords );
		initCaches();
	}

	public int getMisses() {
		return misses;
	}
	
	public int getHits() {
		return hits;
	}
	
	public int getAccess() {
		return accessTime;
	}
	private void initCaches() {
		for(int i=0;i < dataCache.length;i++) {
			dataCache[i] = new ArrayList<CacheEntry[]>();
			instructionCache[i] = new ArrayList<CacheEntry[]>();
		}
	}
	
	public int getPenalty() {
		return missPenalty;
	}
	public void hardInsert(int address, int value, String type) {
//		System.out.println("ana fe hard insert");
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

				for(int i=0;i<dataCache[index].get(pos).length;i++) {
					if(dataCache[index].get(pos)[i].isDirty()) {
						short val = (short)dataCache[index].get(pos)[i].getValue();
						int addr = dataCache[index].get(pos)[i].getAddress();
						memory[addr] = val;
						memCall++;
					}
				}
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
				dataCache[index].remove(pos);
				dataCache[index].add(pos, lis);
//				System.out.println("SIZE NOW " + dataCache[index].size());
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
				for(int i=0;i < dataUsageTable[index].length;i++) {
					if(dataUsageTable[index][i] < least) {
						least = dataUsageTable[index][i];
						pos = i;
					}
				}
				CacheEntry[] lis = new CacheEntry[blockWidth];
				lis[entry.getDisp()] = entry;

				for(int i=0;i<instructionCache[index].get(pos).length;i++) {
					if(instructionCache[index].get(pos)[i].isDirty()) {
						short val = (short)instructionCache[index].get(pos)[i].getValue();
						int addr = instructionCache[index].get(pos)[i].getAddress();
						memory[addr] = val;
						memCall++;
					}
				}
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
				instructionCache[index].remove(pos);
				instructionCache[index].add(pos, lis);
//				System.out.println("SIZE NOW " + instructionCache[index].size());
				dataUsageTable[index][pos]= System.currentTimeMillis();
			}else {
				int rand = (int)(Math.random() * setRecords) + 1;
				instructionCache[index].get(rand)[entry.getDisp()] = entry;
			}

		}
		
	}

	public void wrtieEntry(CacheEntry entry, String type, int newVal) {
		if(type.equals("Data")) {
			if(this.getEntry(entry, type) != null) {
				// write hit
				if(writePolicy) {
					// through
					if(writeBuffer.size() < writeBufferSize){
						writeBuffer.add(entry);
					}else {
						CacheEntry toWrite = writeBuffer.get(0);
						writeBuffer.remove(0);
						memCall++;
						memory[toWrite.getAddress()] = (short)toWrite.getValue();
						writeBuffer.add(entry);
					}
				}else {
					entry.setDirty();
					entry.setValue(newVal);
				}
			}else {
				if(this.softInsert(entry.getAddress(), newVal, type)) {
					return;
				}
				this.hardInsert(entry.getAddress(), newVal, type);
			}

		}else {
			if(this.getEntry(entry, type) != null) {
				// write hit
				if(writePolicy) {
					// through
					if(writeBuffer.size() < writeBufferSize){
						writeBuffer.add(entry);
					}else {
						CacheEntry toWrite = writeBuffer.get(0);
						writeBuffer.remove(0);
						memCall++;
						memory[toWrite.getAddress()] = (short)toWrite.getValue();
						writeBuffer.add(entry);
					}
				}else {
					entry.setDirty();
					entry.setValue(newVal);
				}
			}else {
				if(this.softInsert(entry.getAddress(), newVal, type)) {
					return;
				}
				this.hardInsert(entry.getAddress(), newVal, type);
			}
		}
		
	}

	public boolean softInsert(int address, int value, String type) {
		if(type.equals("Data")) {
//			System.out.println("ana fe softinsert");
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
			System.out.println("ana fe softinsert");
			CacheEntry entry = new CacheEntry(tagSize, indexSize, dispSize, address);
			int index = entry.getIndex();
			entry.setValue(value);
			if(instructionCache[index].size() < setRecords) {
				int size = instructionCache[index].size();
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
				instructionCache[index].add(lis);
				return true;
			}
			return false;
		}
	}


	public Integer getEntry(CacheEntry entry, String cacheType) {
		int index, tag, disp;
		Integer searched = null;
		tag = entry.getTag();
		index = entry.getIndex();
		disp = entry.getDisp();
		searched = search(index, disp, tag, cacheType);
		if(searched == null)
			misses++;
		else
			hits++;
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