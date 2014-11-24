
public class CacheEntry {
	
	private int tagSize, indexSize, dispSize;
	private int tag, index, disp, value, address;

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
		int temp = dispSize;
		for(int i=0;i< temp;i++)
			if(((1 << i) & address) == 1)
				disp |= (1 << i);

		temp += indexSize;
		for(int i=disp; i < temp;i++)
			if(((1 << i) & address) == 1)
				index |= (1 << (i - disp));

		temp += tagSize;
		for(int i=index;i < temp;i++)
			if(((1 << i) & address) == 1)
				tag |= (1 << i) & address;
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