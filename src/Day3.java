import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;
import java.io.File;
import java.io.FileNotFoundException;

public class Day3 {
	private File file;
	private Scanner scan;
	private List<String> path1;
	private List<String> path2;
	
	public Day3(String path) {
		try {
			file = new File (path);
			scan = new Scanner(file);
			path1 = Arrays.asList(scan.nextLine().split(","));
			path2 = Arrays.asList(scan.nextLine().split(","));
			scan.close();
		} catch (FileNotFoundException ex) {
			System.out.println("Error: " + ex);
		}
	}
	
	private Map<String, Integer> getCoordAndSteps(List<String> path) {
		Map<String, Integer> coordStepMap = new HashMap<>();
		int x = 0;
		int y = 0;
		int totalSteps = 0;
		for(String move : path) {
			char dir = move.charAt(0);
			Integer distance = Integer.parseInt(move.substring(1));
			Integer step = (dir == 'L' || dir == 'D') ? -1 : 1;
			for (int i = 1; i <= distance; i++) {
				if (dir == 'U' || dir == 'D')  {
					y += step;
				} else {
					x += step;
				}
				totalSteps++;
				coordStepMap.put(x + "," + y, totalSteps);
			}
		}
		return coordStepMap;
	}
	
	private Map<String, Integer> findOverlap() {
		Map<String, Integer> overLaps = new HashMap<>();
		Map<String, Integer> path1Map = getCoordAndSteps(path1);
		Map<String, Integer> path2Map = getCoordAndSteps(path2);
		for (String coord : path2Map.keySet()) {
			if(path1Map.containsKey(coord)) {
				overLaps.put(coord, path1Map.get(coord) + path2Map.get(coord));
			}
		}
		return overLaps;		
	}
	
	private Integer calcManHatDis(String coord) {
		int x = Integer.parseInt(coord.split(",")[0]);
		int y = Integer.parseInt(coord.split(",")[1]);
		return Math.abs(x) + Math.abs(y);
	}
	
	private int getShortestDis() {
		Map<String, Integer> overLaps = findOverlap();
		return overLaps.keySet().stream().map(coord -> calcManHatDis(coord))
				.reduce(Integer.MAX_VALUE, (minDis, curDis) -> curDis < minDis ? curDis : minDis);
	}
	
	private int getLeastSteps() {
		Map<String, Integer> overLaps = findOverlap();
		return overLaps.values().stream()
				.reduce(Integer.MAX_VALUE, (minStep, curStep) -> curStep < minStep ? curStep : minStep);
	}
	
	
	public static void main(String[] args) {
		Day3 day3 = new Day3("inputs/Day3.txt");
		System.out.println("Part One: " + day3.getShortestDis());
		System.out.println("Part Two: " + day3.getLeastSteps());
		
	}
}


