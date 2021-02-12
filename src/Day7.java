import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Day7 {
	private File file;
	private final int PHASE_MIN;
	private final int PHASE_MAX;
	private List<Integer> intcodeInstruction;
	private Scanner scan;
	private final int NUMBER_OF_AMPS = 5;
	
	public Day7(String path, int PHASE_MIN, int PHASE_MAX) {
		try {
			file = new File (path);
			scan = new Scanner(file);
			intcodeInstruction = Stream.of(scan.nextLine().split(","))
					.map(Integer::parseInt)
					.collect(Collectors.toList());
			scan.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Error: " + ex);
		}
		this.PHASE_MIN = PHASE_MIN;
		this.PHASE_MAX = PHASE_MAX;
		
	}
	
	// Part 1
	public int getMaxThrusterSignal() {
		List<int[]> phaseCombos = getPhaseSettingCombos();
		int maxThrust = 0;
		for (int[] phaseCombo : phaseCombos) {
			int ThrusterSignal = getThrusterSignal(phaseCombo);
			maxThrust = ThrusterSignal > maxThrust ? ThrusterSignal : maxThrust;
		}
		
		return maxThrust;
	}
	
	private int getThrusterSignal(int[] phaseCombo) {
		Intcode intcode = new Intcode(intcodeInstruction);
		int outputValue = 0;
		for (int i : phaseCombo) {
			outputValue = intcode.runIntcodePart1(i, outputValue);
		}
		return outputValue;
	}
	
	public List<int[]> getPhaseSettingCombos() {
		List<int[]> phaseCombos = new ArrayList<>();
		for (int i = PHASE_MIN; i <= PHASE_MAX; i++) {
			for (int j = PHASE_MIN; j <= PHASE_MAX; j++) {
				if (j == i) {
					continue;
				}
				for (int k = PHASE_MIN; k <= PHASE_MAX; k++) {
					if (k == j || k == i) {
						continue;
					}
					for (int l = PHASE_MIN; l <= PHASE_MAX; l++) {
						if (l == i || l == j || l == k) {
							continue;
						}
						for (int m = PHASE_MIN; m <= PHASE_MAX; m++) {
							if (m == i || m == j || m == k || m == l) {
								continue;
							}
							int[] combo = {i, j, k, l, m};
							phaseCombos.add(combo);
						}
					}
				}
			}
		}
		return phaseCombos;
	}
	
	
	// Part 2
	public int getMaxThrusterSignalLooping() {
		List<int[]> phaseCombos = getPhaseSettingCombos();
		int maxThrust = 0;
		for (int[] phaseCombo : phaseCombos) {
			int ThrusterSignal = getThrusterSignalLooping(phaseCombo);
			maxThrust = ThrusterSignal > maxThrust ? ThrusterSignal : maxThrust;
		}
		return maxThrust;
	}
	
	private int getThrusterSignalLooping(int[] phaseCombo) {
		List<Intcode> intcodes = new ArrayList<>();
		for (int i = 0; i < NUMBER_OF_AMPS; i++) {
			intcodes.add(new Intcode(intcodeInstruction));
			int j = i + 1 < NUMBER_OF_AMPS ? i + 1 : 0;
			intcodes.get(i).outputValues.add(phaseCombo[j]);
		}

		Intcode prevIntcode = intcodes.get(NUMBER_OF_AMPS - 1);
		prevIntcode.outputValues.add(0);
		Intcode lastIntcode = intcodes.get(NUMBER_OF_AMPS - 1);
		
		while (!lastIntcode.isFinished) {
			for (Intcode intcode : intcodes) {
				intcode.runIntcodePart2(prevIntcode.outputValues);
				prevIntcode = intcode;
			}
		}

		return lastIntcode.outputValues.get(lastIntcode.outputValues.size() - 1);
	}
	
	class Intcode {
		private List<Integer> intcodeInstruction;
		private int pointer;
		private List<Integer> outputValues;
		private boolean isFinished;
		
		public Intcode(List<Integer> intcodeInstruction) {
			this.intcodeInstruction = makeListCopy(intcodeInstruction);
			outputValues = new ArrayList<>();
			isFinished = false;
			pointer = 0;
		}
		
		private List<Integer> makeListCopy(List<Integer> intcodeInstruction) {
			List<Integer> copy = new ArrayList<>();
			for (int i : intcodeInstruction) {
				copy.add(i);
			}
			return copy;
		}
		
		public int runIntcodePart1(int inputValue1, int inputValue2) {
			List<Integer> copyIntcode = new ArrayList<>(intcodeInstruction);
			int i = 0;
			int setValueFor78 = 0;
			int outputValue = 0;
			boolean isfirstInput = true;	// for day 7;
			
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
						if (isfirstInput) {
							copyIntcode.set(copyIntcode.get(i++), inputValue1);
							isfirstInput = false;
						} else {
							copyIntcode.set(copyIntcode.get(i++), inputValue2);
						}
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
			return outputValue;
		}
		
		public void runIntcodePart2(List<Integer> intputValues) {
			int setValueFor78 = 0;
			while(!isFinished) {
				int opcode = intcodeInstruction.get(pointer);
				int paramMode1 = opcode > 9 ? (opcode / 100) % 10 : 0;
				int paramMode2 = opcode > 9 ? (opcode / 1000) % 10 : 0;
				opcode %= 10;
				switch (opcode) {
					case 1:
						intcodeInstruction.set(intcodeInstruction.get(pointer + 3), 
								getIntcodeValue(pointer + 1, paramMode1, intcodeInstruction) + 
								getIntcodeValue(pointer + 2, paramMode2, intcodeInstruction));
						pointer += 4;
						break;
					case 2:
						intcodeInstruction.set(intcodeInstruction.get(pointer + 3), 
								getIntcodeValue(pointer + 1, paramMode1, intcodeInstruction) * 
								getIntcodeValue(pointer + 2, paramMode2, intcodeInstruction));
						pointer += 4;
						break;
					case 3:			
						if (intputValues.size() == 0) {
							return;
						}
						intcodeInstruction.set(intcodeInstruction.get(pointer + 1), intputValues.remove(0));
						pointer += 2;
						break;
					case 4:
						outputValues.add(getIntcodeValue(pointer + 1, paramMode1, intcodeInstruction));
						pointer += 2;
						break;
					case 5:
						if (getIntcodeValue(pointer + 1, paramMode1,  intcodeInstruction) != 0) {
							pointer = getIntcodeValue(pointer + 2, paramMode2, intcodeInstruction);
						} else {
							pointer += 3;
						}
						break;
					case 6:
						if (getIntcodeValue(pointer + 1, paramMode1,  intcodeInstruction) == 0) {
							pointer = getIntcodeValue(pointer + 2, paramMode2, intcodeInstruction);
						} else {
							pointer += 3;
						}
						break;
					case 7:
						setValueFor78 = (getIntcodeValue(pointer + 1, paramMode1,  intcodeInstruction) < 
								getIntcodeValue(pointer + 2, paramMode2, intcodeInstruction)) ? 1 : 0;
						intcodeInstruction.set(intcodeInstruction.get(pointer + 3), setValueFor78);
						pointer += 4;
						break;
					case 8:
						setValueFor78 = getIntcodeValue(pointer + 1, paramMode1,  intcodeInstruction) == 
								getIntcodeValue(pointer + 2, paramMode2, intcodeInstruction) ? 1 : 0;
						intcodeInstruction.set(intcodeInstruction.get(pointer + 3), setValueFor78);
						pointer += 4;
						break;	
					case 9:
						isFinished = true;
						return;
					default: 
						System.out.println("Invalid opcode: " + opcode);
						return;
				}
			}
		}
		
		private int getIntcodeValue(int i, int paramMode, List<Integer> intcode) {
			return paramMode == 0 ? intcode.get(intcode.get(i)) : intcode.get(i);
		}
	}
	
	public static void main(String[] args) {
		Day7 part1 = new Day7("inputs/Day7.txt", 0, 4);
		System.out.println("Part One: " + part1.getMaxThrusterSignal());
		
		Day7 part2 = new Day7("inputs/Day7.txt", 5, 9);
		System.out.println("Part One: " + part2.getMaxThrusterSignalLooping());
	}
}