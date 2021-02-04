import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day5 {
	private File file;
	private Scanner scan;
	private List<Integer> intcode;

	public Day5(String path) {
		try {
			file = new File (path + this.getClass().getSimpleName() + ".txt");
			scan = new Scanner(file);
			intcode = Stream.of(scan.nextLine().split(","))
					.map(Integer::parseInt)
					.collect(Collectors.toList());
			scan.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Error: " + ex);
		}
	}

	public Integer runIntcode(int inputValue) {
		Scanner scan = new Scanner(System.in);
		List<Integer> copyIntcode = new ArrayList<>(intcode);
		int i = 0;
		int setValueFor78 = 0;
		int outputValue = 0;
		
		intcodeLoop:
		while(i < copyIntcode.size()) {
			int opcode = copyIntcode.get(i++);
			int paramMode1 = opcode > 9 ? (opcode / 100) % 10 : 0;
			int paramMode2 = opcode > 9 ? (opcode / 1000) % 10 : 0;
			opcode %= 10;
			switch (opcode) {
				case 1:
					copyIntcode.set(copyIntcode.get(i + 2), 
							getIntcodeValue(i, paramMode1, copyIntcode) + 
							getIntcodeValue(i + 1, paramMode2, copyIntcode));
					i += 3;
					break;
				case 2:
					copyIntcode.set(copyIntcode.get(i + 2), 
							getIntcodeValue(i, paramMode1, copyIntcode) * 
							getIntcodeValue(i + 1, paramMode2, copyIntcode));
					i += 3;
					break;
				case 3:				
					// System.out.println("Enter input: ");
					// int inputValue = Integer.parseInt(scan.nextLine());
					copyIntcode.set(copyIntcode.get(i++), inputValue);
					break;
				case 4:
					outputValue = getIntcodeValue(i++, paramMode1, copyIntcode);
					if (outputValue != 0) {
						break intcodeLoop;
					}
					break;
				case 5:
					if (getIntcodeValue(i, paramMode1,  copyIntcode) != 0) {
						i = getIntcodeValue(i + 1, paramMode2, copyIntcode);
					} else {
						i += 2;
					}
					break;
				case 6:
					if (getIntcodeValue(i, paramMode1,  copyIntcode) == 0) {
						i = getIntcodeValue(i + 1, paramMode2, copyIntcode);
					} else {
						i += 2;
					}
					break;
				case 7:
					setValueFor78 = (getIntcodeValue(i, paramMode1,  copyIntcode) < 
							getIntcodeValue(i + 1, paramMode2, copyIntcode)) ? 1 : 0;
					copyIntcode.set(copyIntcode.get(i + 2), setValueFor78);
					i += 3;
					break;
				case 8:
					setValueFor78 = getIntcodeValue(i, paramMode1,  copyIntcode) == 
							getIntcodeValue(i + 1, paramMode2, copyIntcode) ? 1 : 0;
					copyIntcode.set(copyIntcode.get(i + 2), setValueFor78);
					i += 3;
					break;	
				case 9:
					outputValue = copyIntcode.get(0);
					break intcodeLoop;
			}
		}
		scan.close();
		return outputValue;
	}
	
	private int getIntcodeValue(int i, int paramMode, List<Integer> intcode) {
		return paramMode == 0 ? intcode.get(intcode.get(i)) : intcode.get(i);
	}
	
	
	public static void main(String[] args) {
		Day5 day5 = new Day5("inputs/");
		System.out.println("Part One: " + day5.runIntcode(1));
		System.out.println("Part Two: " + day5.runIntcode(5));
	}
}
