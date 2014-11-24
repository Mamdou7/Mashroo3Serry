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
			case "LW": op = "0000"; break;
			case "SW": op = "0001"; break;
			case "JMP": op = "0010"; break;
			case "BEQ": op = "0011"; break;
			case "JALR": op = "0100"; break;
			case "RET": op = "0101"; break;
			case "ADD": op = "0110"; break;
			case "SUB": op = "0111"; break;
			case "ADDI": op = "1000"; break;
			case "NAND": op = "1001"; break;
			case "MUL": op = "1010"; break;
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
	
	
	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		//System.out.println(Integer.toBinaryString(-7));
		//System.out.println(Integer.parseInt("1001",2));
		Input();
		

	}

}
