import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.StringTokenizer;

public class RiSC {

	/**
	 * @param args
	 */

	static short[] registers = new short[8];
	static Cache[] caches;
	static short[] memory = new short[64 * 1024 / 16];
	static int pc = 0;
	static CacheManager mng;
	static int MemoryTime;
	public static void main(String[] args) throws Exception {
		// TODO Auto-generated method stub
		//
		BufferedReader input = new BufferedReader(new InputStreamReader(
				System.in));
		System.out.println("Please enter the memory access time");
		
		MemoryTime = Integer.parseInt(input.readLine());
		
		System.out.println("Please enter the number of cashes levels");



		int cashlevels = Integer.parseInt(input.readLine());
		//
		mng = new CacheManager(cashlevels, memory);
		// LRU TRUE RANDOM FALSE
		for (int i = 0; i < cashlevels; i++) {
			System.out.println("Enter S L M ");
			StringTokenizer line = new StringTokenizer(input.readLine());
			int S = Integer.parseInt(line.nextToken());
			int L = Integer.parseInt(line.nextToken());
			int M = Integer.parseInt(line.nextToken());
			System.out.println("Enter 'LRU' or 'Random'for the writing policy");
			String x = input.readLine();
			boolean type = false;
			if (x.equals("LRU"))
				type = true;
			System.out.println("Please enter the Number of cycles required to access");
			int cycles = Integer.parseInt(input.readLine());

			System.out.println("Please Enter the writing policy WriteBack/WriteThrough");
			x = (input.readLine());
			boolean write = false;
			
			System.out.println("Please enter the Penealty ");
			int pent = Integer.parseInt(input.readLine());
			
			if(x.equals("WriteThrough")) write = true;
			
			 mng.createCache(S,L,M,type,write,cycles,pent);
//		mng.createCache(1, 1, 1, true, true, 1, 1);
		}

		registers[1] = 5;
		registers[2] = 5;
		memory[4] = 5;
		memory[5] = 10;
		memory[6] = 20;
		memory[7] = 30;
		memory[12] = 50;
		Input();
		Execute();
		
		mng.calcHitsMisses();
		System.out.println("AMAT = " + mng.calcAMAT());

		// int val = mng.getEntry(5, "Data");
		// System.out.println("-----------> " + mng.getMisses() + " " +
		// mng.getMemCalls() + " " + mng.getHits() + " " + val);
		// val = mng.getEntry(6, "Data");
		// System.out.println("-----------> " + mng.getMisses() + " " +
		// mng.getMemCalls() + " " + mng.getHits() + " " + val);
		// val = mng.getEntry(5, "Data");
		// System.out.println("-----------> " + mng.getMisses() + " " +
		// mng.getMemCalls() + " " + mng.getHits() + " " + val);
		// val = mng.getEntry(4, "Data");
		// System.out.println("-----------> " + mng.getMisses() + " " +
		// mng.getMemCalls() + " " + mng.getHits() + " " + val);
		// val = mng.getEntry(7, "Data");
		// System.out.println("-----------> " + mng.getMisses() + " " +
		// mng.getMemCalls() + " " + mng.getHits() + " " + val);
		// System.out.println("\n\nINsert 7aga gdeda\n\n");
		// val = mng.getEntry(12, "Data");
		// System.out.println("-----------> " + mng.getMisses() + " " +
		// mng.getMemCalls() + " " + mng.getHits() + " " + val);
		// val = mng.getEntry(4, "Data");
		// System.out.println("-----------> " + mng.getMisses() + " " +
		// mng.getMemCalls() + " " + mng.getHits() + " " + val);
		//

	}

	public static void decode(short instruction) {
		System.out.println(Integer.toBinaryString(instruction));

		if ((instruction & (1 << 15)) == 0) {
			int opcode = instruction
					& ((1 << 13) | (1 << 14) | (1 << 15) | (1 << 12));
			opcode = opcode >> 12;

			int Rd = instruction & ((1 << 11) | (1 << 10) | (1 << 9));
			Rd = Rd >> 9;

			int Rs = instruction & ((1 << 8) | (1 << 7) | (1 << 6));
			Rs = Rs >> 6;

			int Rt = instruction & ((1 << 5) | (1 << 4) | (1 << 3));
			Rt = Rt >> 3;

			switch (opcode) {

			case 0:
				int imm = (instruction & ((1 << 8) | (1 << 7) | (1 << 6)
						| (1 << 5) | (1 << 4) | (1 << 3) | (1 << 2)));
				jump(Rd, imm);
				break;
			case 1:
				jumpAndLink(Rd, Rs);
				break;
			case 2:
				Return(Rd);
				break;
			case 3:
				add(Rd, Rs, Rt);
				break;
			case 4:
				subtract(Rd, Rs, Rt);
				break;
			case 5:
				Nand(Rd, Rs, Rt);
				break;
			case 6:
				Multiply(Rd, Rs, Rt);
				break;

			default:
				System.out.println("You Entered a wrong instruction");

			}
		} else {
			// System.out.println(Integer.toBinaryString(instruction));
			int opcode = instruction & ((1 << 13) | (1 << 14) | (1 << 15));
			opcode = opcode >> 13;

			int Rd = instruction & ((1 << 12) | (1 << 11) | (1 << 10));
			Rd = Rd >> 10;

			int Rs = instruction & ((1 << 9) | (1 << 8) | (1 << 7));
			Rs = Rs >> 7;

			short imm = (short) (instruction & ((1 << 6) | (1 << 5) | (1 << 4)
					| (1 << 3) | (1 << 2) | (1 << 1) | (1 << 0)));

			switch (opcode) {

			case 4:
				load(Rd, Rs, imm);
				break;
			case 5:
				store(Rd, Rs, imm);
				break;
			case 6:
				branchEqual(Rd, Rs, imm);
				break;
			case 7:
				addImmediate(Rd, Rs, imm);
				break;

			default:
				System.out.println("You Entered a wrong instruction");

			}

		}

	}

	private static void branchEqual(int rd, int rs, short imm) {
		// TODO Auto-generated method stub
		if (registers[rs] == registers[rd]) {
			pc = pc + imm;
		}

	}

	private static void store(int rd, int rs, short imm) {
		// short address = (short) (registers[rs] + imm);
		// write(address,registers[rd]);

	}

	private static void load(int rd, int rs, short imm) {
		if (rd == 0) {
			System.out.println("LA2AAAA 7EELAK");
			return;
		}
		// short address = (short) (registers[rs]+imm);
		registers[rd] = mng.getEntry(registers[rs] + imm, "Data").shortValue();
		System.out.println("HITS = " + mng.getHits());
		System.out.println("MISSES = " + mng.getMisses());
	}

	private static void addImmediate(int rd, int rs, short imm) {
		if (rd == 0) {
			System.out.println("RAYEE7 FEEEN");
			return;
		}

		registers[rd] = (short) (registers[rs] + imm);

	}

	private static void Multiply(int rd, int rs, int rt) {
		if (rd == 0) {
			System.out.println("LAA2AAA! LA2A! LA2A!");
			return;
		}
		short op1 = registers[rs];
		short op2 = registers[rt];

		short result = (short) (op1 * op2);
		registers[rd] = result;
	}

	private static void Nand(int rd, int rs, int rt) {
		if (rd == 0) {
			System.out
					.println("ZERO REGISTER MAMNOO3 EL EKTERAB AW EL TASWEER");
			return;
		}
		registers[rd] = (short) ~(registers[rs] | registers[rt]);

	}

	private static void subtract(int rd, int rs, int rt) {
		if (rd == 0) {
			System.out.println("ZERO REGISTER 5AT A7MAR");
			return;
		}
		registers[rd] = (short) (registers[rs] - registers[rt]);

	}

	private static void Return(int rd) {
		pc = registers[rd];

	}

	private static void jumpAndLink(int rd, int rs) {
		if (rd == 0) {
			System.out.println("ELLAA EL ZERO");
			return;
		}
		registers[rd] = (short) (pc);
		pc = registers[rs] - 1;

	}

	private static void jump(int rd, int imm) {
		pc = pc + registers[rd] + imm;

	}

	static void add(int rd, int rs, int rt) {
		if (rd == 0) {
			System.out.println("EL ZERO DAH REGISTER 3AZIM");
			return;
		}
		registers[rd] = (short) (registers[rs] + registers[rt]);
		System.out.println("Val " + registers[rd]);
	}

	public static void Execute() {

		while (true) {

			int instruction = memory[pc];
//			int instruction = mng.getEntry(pc, "ins");
			// System.out.println(instruction+" "+Integer.parseInt("0111000000000000",2));
			if (instruction == Integer.parseInt("0111000000000000", 2))
				break;
			decode((short) instruction);
			pc++;

			System.out.println("THE PROGRAM COUNTER IS = TO " + pc);
		}

	}

	public static void CacheInput() throws IOException {
		BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
		System.out
				.println("Enter the cache geometry in this format: /n [R] [C] [M] "
						+ "/n [Hit Writing Policy]");
	}

	public static void Input() throws IOException {
		BufferedReader r = new BufferedReader(new InputStreamReader(System.in));
		System.out.println("Enter the starting address of the PC");
		int start = Integer.parseInt(r.readLine());
		pc = start;
		System.out
				.println("Etfadal enter your instructions. write 'END' when you are finished.");
		while (true) {
			String line = r.readLine();
			StringTokenizer tkn = new StringTokenizer(line);
			String instruction = tkn.nextToken();
			String op = "";

			switch (instruction) {
			case "LW":
				op = "100";
				break;
			case "SW":
				op = "101";
				break;
			case "JMP":
				op = "0000";
				break;
			case "BEQ":
				op = "110";
				break;
			case "JALR":
				op = "0001";
				break;
			case "RET":
				op = "0010";
				break;
			case "ADD":
				op = "0011";
				break;
			case "SUB":
				op = "0100";
				break;
			case "ADDI":
				op = "111";
				break;
			case "NAND":
				op = "0101";
				break;
			case "MUL":
				op = "0110";
				break;
			case "END":
				op = "0111";
				break;
			default:
				System.out.println("ERROR KATEL!");
				return;
			}

			while (tkn.hasMoreTokens()) {
				String val = tkn.nextToken();
				switch (val.charAt(0)) {
				case 'R':
					String register = Integer.toBinaryString(Integer
							.parseInt(val.charAt(1) + ""));
					while (register.length() < 3) {
						register = "0" + register;
					}
					op = op + register;
					break;

				default:
					String bom = Integer.toBinaryString(Integer.parseInt(val));
					String imm = "";
					int j = 1;
					for (int i = 0; i < 7 && (bom.length() - j) >= 0; i++) {
						imm = bom.charAt(bom.length() - j) + imm;
						j++;
					}
					while (imm.length() < 7) {
						imm = "0" + imm;
					}
					op = op + imm;
				}

			}
			while (op.length() < 16) {
				op = op + "0";
			}
//			System.out.println("--> " + op + " " + Integer.parseInt(op, 2));
			memory[start] = (short) Integer.parseInt(op, 2);
			// System.out.println("--> "+Integer.toBinaryString(memory[start])+" "+memory[start]);
			start++;
			if (line.equals("END"))
				break;

		}

		System.out
				.println("Etfadal enter you data in this format: "
						+ "\n [Memory Location] [Integer Value] \n Enter 'END' when you are finished.");
		while (true) {
			String line = r.readLine();
			if (line.equals("END"))
				break;
			StringTokenizer tkn = new StringTokenizer(line);
			int location = Integer.parseInt(tkn.nextToken());
			short value = Short.parseShort(tkn.nextToken());
			memory[location] = value;
		}

	}

}
