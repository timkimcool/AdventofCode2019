import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day2 {
	private File file;
	private Scanner scan;
	private List<Integer> intcode;
	private final int OUTPUT = 19690720;
	private final int INPUT_ONE = 12;
	private final int INPUT_TWO = 2;

	public Day2(String path) {
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
	
	public int runIntcode() {
		return runIntcode(INPUT_ONE, INPUT_TWO);
	}
	
	public int runIntcode(int replace1, int replace2) {
		intcode.set(1, replace1);
		intcode.set(2, replace2);
		List<Integer> copyIntcode = new ArrayList<>(intcode);
		int i = 0;
		while(i < copyIntcode.size()) {
			int opcode = copyIntcode.get(i);
			if (opcode == 99) {
				return copyIntcode.get(0);
			}
			if (opcode == 1) {
				copyIntcode.set(copyIntcode.get(i + 3), 
						getIntcodeValue(i + 1, copyIntcode) + getIntcodeValue(i + 2, copyIntcode));
			} else if (opcode == 2) {
				copyIntcode.set(copyIntcode.get(i + 3), 
						getIntcodeValue(i + 1, copyIntcode) * getIntcodeValue(i + 2, copyIntcode));
			}
			i += 4;
		}
		
		return -1;
	}
	
	private int getIntcodeValue(int i, List<Integer> intcode) {
		return intcode.get(intcode.get(i));
	}
	
	public int findNounVerb() {
		for (int i = 0; i <= 99; i++) {
			for (int j = 0; j <= 99; j++) {
				if (runIntcode(i, j) == OUTPUT) {
					return 100 * i + j;
				}
			}
		}
		return -1;
	}
	
	public static void main(String[] args) {
		Day2 part1 = new Day2("inputs/");
		System.out.println("Part One: " + part1.runIntcode());
		
		Day2 part2 = new Day2("inputs/");
		System.out.println("Part Two: " + part2.findNounVerb());
	}
}


