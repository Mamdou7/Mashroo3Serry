import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;


public class RiSC {

	/**
	 * @param args
	 */
	static int[] registers = new int[8];
	static Cache[] caches;
	static int[]memory = new int[64];
	static int pc = 0;
	
	
	
	
	
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
		 
//		 case 4 : load(Rd,Rs,imm); break;
//		 case 5 : store(Rd,Rs,imm); break;
//		 case 6 : branchEqual(Rd,Rs,imm); break;
//		 case 7 : addImmediate(Rd,Rs,imm); break;


		 default : System.out.println("You Entered a wrong instruction");
		 
		 }
		
	}
	

	}
	static void add(int Rd,int Rs,int Rt) {
		
	}
	
	


	
	public static void CacheInput() throws IOException{
		BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter the cache geometry in this format: /n [R] [C] [M] " +
				"/n [Hit Writing Policy]");
	}
	
	public static void Input() throws IOException{
		BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter the starting address of the PC");
		int start = Integer.parseInt(r.readLine());
		pc = start;
		System.out.println("Etfadal enter your instructions. write 'END' when you are finished.");
		while(true){
			String line = r.readLine();
			if(line.equals("END"))break;
			StringTokenizer tkn = new StringTokenizer(line);
			String instruction = tkn.nextToken();
			String op = "";
			
			switch(instruction){
			case "LW": op = "100"; break;
			case "SW": op = "101"; break;
			case "JMP": op = "0000"; break;
			case "BEQ": op = "110"; break;
			case "JALR": op = "0001"; break;
			case "RET": op = "0010"; break;
			case "ADD": op = "0011"; break;
			case "SUB": op = "0100"; break;
			case "ADDI": op = "111"; break;
			case "NAND": op = "0101"; break;
			case "MUL": op = "0110"; break;
			default : System.out.println("ERROR KATEL!");return;
			}
			
			while(tkn.hasMoreTokens()){
				String val = tkn.nextToken();
				switch(val.charAt(0)){
				case 'R' :
					String register = Integer.toBinaryString(Integer.parseInt(val.charAt(1)+""));
					while(register.length()<3){
						register = "0"+register;
					}
					op = op+register;
					break;
					
				default : String imm = Integer.toBinaryString(Integer.parseInt(val));
				while(imm.length()<4){
					imm = "0"+imm;
				}
				op= op+imm;
			}
			
		}
			while(op.length()>16){
				op = op+"0";
			}
//			System.out.println(op);
			memory[start]=Integer.parseInt(op,2);
			start++;
			
		}
		
		System.out.println("Etfadal enter you data in this format: " +
				"\n [Memory Location] [Integer Value] \n Enter 'END' when you are finished.");
		while(true){
			String line = r.readLine();
			if(line.equals("END"))break;
			StringTokenizer tkn = new StringTokenizer(line);
			int location = Integer.parseInt(tkn.nextToken());
			int value = Integer.parseInt(tkn.nextToken());
			memory[location]=value;			
		}
		
		
	}
	
	

}
