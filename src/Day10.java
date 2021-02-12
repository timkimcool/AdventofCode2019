import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Stream;

public class Day10 {
	private final Path path;
	private Stream<String> asteroids;
	private List<String> array1D;
	private List<List<String>> grid;
	private int MAX_GRID_EDGE_Y;
	private int MAX_GRID_EDGE_X;
	private int ASTEROID_NUMBER;

	public Day10(String path, int astNumber) {
		this.path = Paths.get(path + this.getClass().getSimpleName().toLowerCase() + ".txt");
		try {
			asteroids =  Files.lines(this.path);
		} catch (IOException ex) {
			System.out.println("Error: " + ex);
		}
		grid = makeGrid(asteroids);
		MAX_GRID_EDGE_X = grid.size();
		MAX_GRID_EDGE_Y = grid.get(0).size();
		ASTEROID_NUMBER = astNumber;

	}
	
	private List<List<String>> makeGrid(Stream<String> asteroids) {
		grid = new ArrayList<>();
		array1D = new ArrayList<>();
		asteroids.forEach(line -> {
			array1D.add(line);
		});
		
		// flip x and y in arrayList
		for (int i = 0; i < array1D.size(); i++) {
			String[] list = array1D.get(i).split("");
			if (i == 0) {
				for (int j = 0; j < list.length; j++) {
					grid.add(new ArrayList<>());
				}
			}
			for (int j = 0; j < list.length; j++) {
				grid.get(j).add(list[j]);
			}
		}
		return grid;
	}
	
	private int countMaxAsteroids() {
		Map<String, Integer> asteroidCount = makeCountMap();
		return asteroidCount.values().stream().reduce(0, (acc, cur) -> {
			return cur > acc ? cur : acc;
		});
	}
	
	private Map<String, Integer> makeCountMap() {
		Map<String, Integer> asteroidCount = new HashMap<>();
		for (int x = 0; x < MAX_GRID_EDGE_X; x++) {
			for (int y = 0; y < MAX_GRID_EDGE_Y; y++) {
				if (isAsteroid(x, y)) {
					asteroidCount.put(x + "," + y, countAsteroids(x, y));
				}
			}
		}
		return asteroidCount;
	}
	
	private int countAsteroids(int x, int y) {
		int count = 0;
		List<Double> slopes = new ArrayList<>();
		
		// check above y
		for (int i_y = y - 1; i_y >= 0; i_y--) {
			for (int i_x = 0; i_x < MAX_GRID_EDGE_X; i_x++) {
				if (isAsteroid(i_x, i_y)) {
					count += addSlope(y - i_y, x - i_x, slopes) ? 1 : 0;
				}
			}
		}
		
		//check y and below
		slopes.clear();
		for (int i_y = y; i_y < MAX_GRID_EDGE_Y; i_y++) {
			for (int i_x = 0; i_x < MAX_GRID_EDGE_X; i_x++) {
				if (isAsteroid(i_x, i_y)) {
					if (i_x == x && i_y == y) {
						continue;
					}
					count += addSlope(y - i_y, x - i_x, slopes) ? 1 : 0;
				}
			}
		}
		return count;
	}
	
	private boolean addSlope(double y, double x, List<Double> slopes) {
		double slope = x == 0 ? Double.NaN : y / x;
		if (!slopes.contains(slope)) {
			slopes.add(slope);
			return true;
		}
		return false;
	}
	
	private Boolean isAsteroid(double x, double y) {
		return grid.get((int) x).get((int) y).equals("#");
	}
	
	
	// part 2
	private int shootAsteroids() {
		String coord = getMonitorLocation();
		int x = Integer.parseInt(coord.split(",")[0]);
		int y = Integer.parseInt(coord.split(",")[1]);
		Map<Integer, String> results = makeQuadMap(x, y);
		
		String astCoord = results.get(ASTEROID_NUMBER);
		int xCoord = Integer.parseInt(astCoord.split(",")[0]);
		int yCoord = Integer.parseInt(astCoord.split(",")[1]);	
		return xCoord * 100 + yCoord;
	}
	
	private String getMonitorLocation() {
		Map<String, Integer> asteroidCount = makeCountMap();
		int count =  asteroidCount.values().stream().reduce(0, (acc, cur) -> {
			return cur > acc ? cur : acc;
		});
		return asteroidCount.keySet().stream().reduce("", (acc, cur) -> asteroidCount.get(cur) == count ? cur : acc);
	}
	
	private Map<Integer, String> makeQuadMap(int x, int y) {
		Map<Double, List<String>> quad1 = new HashMap<>();
		Map<Double, List<String>> quad2 = new HashMap<>();
		Map<Double, List<String>> quad3 = new HashMap<>();
		Map<Double, List<String>> quad4 = new HashMap<>();
		Map<Integer, String> results = new HashMap<>();
		
		// Quad 1
		for (int i_y = y - 1; i_y >= 0; i_y--) {
			for (int i_x = x; i_x < MAX_GRID_EDGE_X; i_x++) {
				if (isAsteroid(i_x, i_y)) {
					double slope = addSlopeMap(y - i_y, x - i_x, quad1);
					quad1.get(slope).add(i_x + "," + i_y);
				}
			}
		}
		
		// Quad 2
		for (int i_y = y; i_y >= 0; i_y--) {
			for (int i_x = x - 1; i_x >= 0; i_x--) {
				if (isAsteroid(i_x, i_y)) {
					double slope = addSlopeMap(y - i_y, x - i_x, quad2);
					quad2.get(slope).add(i_x + "," + i_y);
				}
			}
		}
		
		// Quad 3
		for (int i_y = y + 1; i_y < MAX_GRID_EDGE_Y; i_y++) {
			for (int i_x = x; i_x >= 0; i_x--) {
				if (isAsteroid(i_x, i_y)) {
					double slope = addSlopeMap(y - i_y, x - i_x, quad3);
					quad3.get(slope).add(i_x + "," + i_y);
				}
			}
		}
		
		// Quad 4
		for (int i_y = y; i_y < MAX_GRID_EDGE_Y; i_y++) {
			for (int i_x = x + 1; i_x < MAX_GRID_EDGE_X; i_x++) {
				if (isAsteroid(i_x, i_y)) {
					double slope = addSlopeMap(y - i_y, x - i_x, quad4);
					quad4.get(slope).add(i_x + "," + i_y);
				}
			}
		}
		
		int[] count = { 0 };
		while(!quad1.keySet().isEmpty() || !quad2.keySet().isEmpty() || !quad3.keySet().isEmpty() || !quad4.keySet().isEmpty()) {
			removeAsteroids(quad1, 1, count, results);
			removeAsteroids(quad4, 4, count, results);
			removeAsteroids(quad3, 3, count, results);
			removeAsteroids(quad2, 2, count, results);
		}
		return results;

	}
	
	private double addSlopeMap(double y, double x, Map<Double, List<String>> quad) {
		double slope = x == 0 ? Double.NaN : y / x;
		if (!quad.containsKey(slope)) {
			quad.put(slope, new ArrayList<String>());
		}
		return slope;
	}
	
	private void removeAsteroids(Map<Double, List<String>> quad, int quadNum, int[] count, Map<Integer, String> results) {
		if (quad.keySet().isEmpty()) {
			return;
		}
		switch(quadNum) {
			case 1:
				removeBigY(quad.get(Double.NaN), count, results);
				quad.keySet().stream().sorted().forEach(slope -> {
					if (!slope.isNaN()) {
						removeBigY(quad.get(slope), count, results);
					}
					if (quad.get(slope).isEmpty()) {
						quad.remove(slope);
					}
				});
				break;
			case 2:
				quad.keySet().stream().sorted().forEach(slope -> {
					if (!slope.isNaN()) {
						removeSmallY(quad.get(slope), count, results);
					}
					if (quad.get(slope).isEmpty()) {
						quad.remove(slope);
					}
				});
				removeSmallY(quad.get(Double.NaN), count, results);
				break;
			case 3:
				quad.keySet().stream().sorted().forEach(slope -> {
					if (!slope.isNaN()) {
						removeSmallY(quad.get(slope), count, results);
					}
					if (quad.get(slope).isEmpty()) {
						quad.remove(slope);
					}
				});
				removeSmallY(quad.get(Double.NaN), count, results);
				break;
			case 4:
				quad.keySet().stream().sorted().forEach(slope -> {
					if (!slope.isNaN()) {
						removeBigY(quad.get(slope), count, results);
					}
					if (quad.get(slope).isEmpty()) {
						quad.remove(slope);
					}
				});
				removeBigY(quad.get(Double.NaN), count, results);
				break;
		}
	}
	
	private void removeBigY(List<String> coords, int[] count, Map<Integer, String> results) {
		if (coords == null) {
			return;
		}
		int y = -1;
		int index = -1;
		for (String coord : coords) {
			if (Integer.parseInt(coord.split(",")[1]) > y) {
				index = coords.indexOf(coord);
				y = Integer.parseInt(coord.split(",")[1]);
			}
		}
		results.put(++count[0], coords.get(index));
		coords.remove(index);
	}
	
	private void removeSmallY(List<String> coords, int[] count, Map<Integer, String> results) {
		if (coords == null) {
			return;
		}
		int y = Integer.MAX_VALUE;
		int index = -1;
		for (String coord : coords) {
			if (Integer.parseInt(coord.split(",")[1]) < y) {
				index = coords.indexOf(coord);
				y = Integer.parseInt(coord.split(",")[1]);
			}
		}
		results.put(++count[0], coords.get(index));
		coords.remove(index);
	}
	
	public static void main(String[] args) {
		Day10 day10 = new Day10("inputs/", 200);
		System.out.println("Part One: " + day10.countMaxAsteroids());
		System.out.println("Part Two: " + day10.shootAsteroids());
	}
}
