
public class RiSC {

	/**
	 * @param args
	 */
	
	
	int[] registers = new int[8];
	Cache[] caches;
	int[]memory = new int[64*1024];
	
	
	
	
	
	
	public static void decode(int instruction) {
		
	if((instruction & (1<<15)) == 0 ){	
	int opcode =  instruction & ((1<<13) | (1<<14) | (1<<15)|(1<<12));
	opcode = opcode>>12;
	
	int Rd = instruction & ((1<<11) | (1<<10) | (1<<9));
	Rd = Rd>>9 ;

	int Rs = instruction & ((1<<8) | (1<<7) | (1<<6));
	Rs = Rs >> 6;

	int Rt = instruction & ((1<<5) | (1<<4) | (1<<3));	
	Rt = Rt >>3;
	
	 switch(opcode) {
	 
//	 case 0 : jump(Rd,Rs,Rt); break;
//	 case 1 : jumpAndLink(Rd,Rs,Rt); break;
//	 case 2 : Return(Rd,Rs,Rt); break;
//	 case 3 : add(Rd,Rs,Rt); break;
//	 case 4 : subtract(Rd,Rs,Rt); break;
//	 case 5 :	Nand(Rd,Rs,Rt); break;
//	 case 6 :	Multiply(Rd,Rs,Rt); break;

	 default : System.out.println("You Entered a wrong instruction");
	 
	 }
	}  else {
		System.out.println(Integer.toBinaryString(instruction));
		int opcode =  instruction & ((1<<13) | (1<<14) | (1<<15));
		opcode = opcode>>13;
		
		int Rd = instruction & ((1<<12) | (1<<11) | (1<<10));
		Rd = Rd>>10 ;

		int Rs = instruction & ((1<<9) | (1<<8) | (1<<7));
		Rs = Rs >> 7;

		int imm = instruction & ((1<<6)|(1<<5) | (1<<4) | (1<<3)|(1<<2) | (1<<1) | (1<<0));

		 switch(opcode) {
		 
//		 case 4 : load(Rd,Rs,Rt); break;
//		 case 5 : store(Rd,Rs,Rt); break;
//		 case 6 : branchEqual(Rd,Rs,Rt); break;
//		 case 7 : addImmediate(Rd,Rs,Rt); break;


		 default : System.out.println("You Entered a wrong instruction");
		 
		 }
		
	}
	

	}
	static void add(int Rd,int Rs,int Rt) {
		
	}
	
	
	
	
	
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		//	decode(24719);
			decode(42312);
	}

}
