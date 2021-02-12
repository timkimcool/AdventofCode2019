import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class Day8 {
	private File file;
	private Scanner scan;
	private String imageData;
	private final int PIXEL_WIDTH;
	private final int PIXEL_HEIGHT;
	
	public Day8(String path, int PIXEL_WIDTH, int PIXEL_HEIGHT) {
		try {
			file = new File (path);
			scan = new Scanner(file);
			 imageData = scan.nextLine();
			scan.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Error: " + ex);
		}
		this.PIXEL_WIDTH = PIXEL_WIDTH;
		this.PIXEL_HEIGHT = PIXEL_HEIGHT;
		
	}
	
	public int getDigitsProduct() {
		int[] charCount = getFewestZeroLayer(imageData);
		return charCount[1] * charCount[2];
	}
	
	private int[] getFewestZeroLayer(String imageData) {
		int increment = PIXEL_WIDTH * PIXEL_HEIGHT;
		int[] charCountReturn = {0, 0, 0};
		int numberOfZeroes = Integer.MAX_VALUE;
		
		for (int i = 0; i < imageData.length(); i += increment) {
			String layer = imageData.substring(i, i + increment);
			int[] charCount = getCharCount(layer);
			if (charCount[0] < numberOfZeroes) {
				charCountReturn = charCount;
				numberOfZeroes = charCount[0];
			}
		}
		return charCountReturn;
	}

	private int[] getCharCount(String layer) {
		int[] charCounts = {0, 0, 0};
		for (int i = 0; i < layer.length(); i++) {
			charCounts[Integer.parseInt(layer.substring(i, i + 1))]++;
		}
		return charCounts;
	}
	
	public String getMessage() {
		String msg = "";
		int increment = PIXEL_WIDTH * PIXEL_HEIGHT;
		for (int i = 0; i < increment; i++) {
			if (i % PIXEL_WIDTH == 0) {
				msg += "\n";
			}
			for(int j = i; j < imageData.length(); j += increment) {
				int imageValue = Integer.parseInt(imageData.substring(j, j + 1));
				if (imageValue == 2) {
					continue;
				}
				msg += imageValue == 1 ? "O" : " ";
				break;
			}
		}
		return msg;
	}
	
	public static void main(String[] args) {
		Day8 day8 = new Day8("inputs/Day8.txt", 25, 6);
		System.out.println("Part One: " + day8.getDigitsProduct());
		System.out.print("Part Two: " + day8.getMessage());
		
	}
}
