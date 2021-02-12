import java.io.File;
import java.io.FileNotFoundException;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.stream.Stream;

public class Day9 {
	private File file;
	private Scanner scan;
	private Map<Long, Long> intcode;
	private long relativeBase;

	public Day9(String path) {
		try {
			file = new File (path + this.getClass().getSimpleName() + ".txt");
			scan = new Scanner(file);
			long[] i = { 0 };
			intcode = new HashMap<>();
			Stream.of(scan.nextLine().split(",")).forEach(code -> {
				intcode.put(i[0]++, Long.parseLong(code));
			});
			relativeBase = 0;
			scan.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Error: " + ex);
		}
	}

	public long runIntcode(long inputValue) {
		long i = 0;
		long setValueFor78 = 0;
		long outputValue = 0;
		intcodeLoop:
		while(true) {
			int opcode = intcode.get(i++).intValue();
			int paramMode1 = opcode > 9 ? (opcode / 100) % 10 : 0;
			int paramMode2 = opcode > 9 ? (opcode / 1000) % 10 : 0;
			int paramMode3 = opcode > 9 ? (opcode / 10000) % 10 : 0;
			opcode %= 100;
			switch (opcode) {
				case 1:
					setIntcodeValue(intcode.get(i + 2), paramMode3, getIntcodeValue(i, paramMode1, intcode) + 
							getIntcodeValue(i + 1, paramMode2, intcode), intcode);
					i += 3;
					break;
				case 2:
					setIntcodeValue(intcode.get(i + 2), paramMode3, getIntcodeValue(i, paramMode1, intcode) * 
							getIntcodeValue(i + 1, paramMode2, intcode), intcode);
					i += 3;
					break;
				case 3:				
					if (paramMode1 == 2) {
						intcode.put(getIntcodeValue(i++ , 1, intcode) + relativeBase, inputValue);
					} else {
						intcode.put(getIntcodeValue(i++, paramMode1, intcode), inputValue);
					}
					break;
				case 4:
					outputValue = getIntcodeValue(i++, paramMode1, intcode);
					break;
				case 5:
					if (getIntcodeValue(i, paramMode1,  intcode) != 0) {
						i = getIntcodeValue(i + 1, paramMode2, intcode);
					} else {
						i += 2;
					}
					break;
				case 6:
					if (getIntcodeValue(i, paramMode1,  intcode) == 0) {
						i = getIntcodeValue(i + 1, paramMode2, intcode);
					} else {
						i += 2;
					}
					break;
				case 7:
					setValueFor78 = (getIntcodeValue(i, paramMode1,  intcode) < 
							getIntcodeValue(i + 1, paramMode2, intcode)) ? 1 : 0;
					setIntcodeValue(intcode.get(i + 2), paramMode3, setValueFor78, intcode);
					i += 3;
					break;
				case 8:
					setValueFor78 = getIntcodeValue(i, paramMode1,  intcode) == 
							getIntcodeValue(i + 1, paramMode2, intcode) ? 1 : 0;
					setIntcodeValue(intcode.get(i + 2), paramMode3, setValueFor78, intcode);
					i += 3;
					break;	
				case 9:
					relativeBase += getIntcodeValue(i++, paramMode1, intcode);
					break;
				case 99:
					break intcodeLoop;
				default:
					System.out.println("ERROR with opcode: " + opcode + "|" + i);
					return -1;
			}
		}
		return outputValue;
	}
	
	private long getIntcodeValue(long i, int paramMode, Map<Long, Long> intcode) {
		try {
			switch (paramMode) {
			case 0:
				return intcode.get(intcode.get(i));
			case 1:
				return intcode.get(i);
			case 2:
				return intcode.get(intcode.get(i) + relativeBase);
			}
		} catch (NullPointerException ex) {
			return 0;
		}
		return 0;
	}
	
	private void setIntcodeValue(long index, int paramMode, long setValue, Map<Long, Long> intcode) {
		if (paramMode == 2) {
			intcode.put(index + relativeBase, setValue);
		} else {
			intcode.put(index, setValue);
		}
	}
	
	public static void main(String[] args) {
		Day9 part1 = new Day9("inputs/");
		System.out.println("Part One: " + part1.runIntcode(1));
		Day9 part2 = new Day9("inputs/");
		System.out.println("Part Two: " + part2.runIntcode(2));
		// System.out.println("Part Two: " + day9.runIntcode(5));
	}
}
