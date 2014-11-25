
public class CacheEntry {
	
	private int tagSize, indexSize, dispSize;
	private int tag, index, disp, value, address;
	private boolean dirtyBit;
	
	
	public void setDirty() {
		dirtyBit = true;
	}

	public boolean isDirty() {
		return dirtyBit;
	}

	public CacheEntry(int tag, int index, int disp, int address) {
		tagSize = tag;
		indexSize = index;
		dispSize = disp;
		this.address = address;
		setFields();
	}
	
	public void setAddress(int address) {
		this.address = address;
	}
	
	public int getAddress() {
		return address;
	}
	
	private void setFields() {
		System.out.println("~~~~~~~~~~~~~~~ Cache Enry ~~~~~~~~~~~~");
		int temp = dispSize;
		for(int i=0;i< temp;i++)
			if(((1 << i) & address) != 0)
				disp |= (1 << i);

		temp += indexSize;
		for(int i=dispSize; i < temp;i++)
			if(((1 << i) & address) != 0)
				index |= (1 << (i - dispSize));

		temp += tagSize;
		for(int i=temp - tagSize;i < temp;i++)
			if(((1 << i) & address) != 0)
				tag |= (1 << (i-dispSize - indexSize));
		
		System.out.println("Address " + address);
		System.out.println("Index " + index);
		System.out.println("Disp " + disp);
		System.out.println("value " + value);
		System.out.println("tag " + tag);
		System.out.println("indexSize " + indexSize);
		System.out.println("dispSie " + dispSize);
		System.out.println("tagsize " + tagSize);
		System.out.println("~~~~~~~~~~~~~~~ Cache Enry ~~~~~~~~~~~~");
	}
	
	public int getTag() {
		return tag;
	}

	public void setTag(int tag) {
		this.tag = tag;
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
	}

	public int getDisp() {
		return disp;
	}

	public void setDisp(int disp) {
		this.disp = disp;
	}

	public int getValue() {
		return value;
	}

	public void setValue(int value) {
		this.value = value;
	}
	
}