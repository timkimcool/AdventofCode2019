import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Day4 {
	private File file;
	private Scanner scan;
	private final int lowerBound;
	private final int upperBound;
	private final String[] input;
	private final String sameAdjacentDigitsRegex;
	
	
	public Day4(String path) {
		try {
			file = new File (path + this.getClass().getSimpleName() + ".txt");
			scan = new Scanner(file);
		} catch (FileNotFoundException ex) {
			System.out.println("Error: " + ex);
		}
		input = scan.nextLine().split("-");
		scan.close();
		lowerBound = Integer.parseInt(input[0]);
		upperBound = Integer.parseInt(input[1]);
		sameAdjacentDigitsRegex = "\\d*(\\d)\\1+\\d*";
	}
	
	public int getPasswordCount(int part) {
		int passwordCount = 0;
		for (int password = lowerBound; password <= upperBound; password++) {
			if (hasSameAdjacentDigits(password) && isAscendingNumbers(password)) {
				if (part == 1) {
					passwordCount++;
				}
				if (part == 2 && hasTwoSameAdjacentDigits(password)) {
					passwordCount++;
				}
			}
		}
		return passwordCount;
	}
	
	private boolean hasSameAdjacentDigits(int password) {
		return String.valueOf(password).matches(sameAdjacentDigitsRegex);
	}
	
	private boolean isAscendingNumbers(int password) {
		while (password > 0) {
			int rightDigit = password % 10;
			password /= 10;
			int leftDigit = password % 10;
			if (rightDigit < leftDigit) {
				return false;
			}
		}
		return true;
	}
	
	private boolean hasTwoSameAdjacentDigits(int password) {
		int countDigits = 1;
		while (password > 0) {
			int rightDigit = password % 10;
			password /= 10;
			int leftDigit = password % 10;
			if (rightDigit == leftDigit) {
				countDigits++;
			} else {
				if (countDigits == 2) {
					return true;
				}
				countDigits = 1;
			}
		}
		return false;
	}
	
	public static void main(String[] args) {
		Day4 day4 = new Day4("inputs/");
		System.out.println("Part One: " + day4.getPasswordCount(1));
		System.out.println("Part Two: " + day4.getPasswordCount(2));
		
	}
}
