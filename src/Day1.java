import java.util.stream.Stream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.Files;

public class Day1 {
	private Path path;
	private Stream<String> lines;
	private Stream<Integer> masses;

	public Day1(String path) {
		this.path = Paths.get(path + this.getClass().getSimpleName().toLowerCase() + ".txt");
		try {
			this.lines = Files.lines(this.path);
			this.masses = lines.map(Integer::parseInt);
		} catch (IOException ex) {
			System.out.println("Error: " + ex);
		}
	}
	
	public int getFuelReqPartOne() {
		return masses.map(mass -> getFuel(mass))
			.reduce(0, (acc, cur) -> acc + cur);
	}
	
	private int getFuel(int mass) {
		return mass / 3 - 2;
	}
	
	public int getFuelReqPartTwo() {
		return masses.map(mass -> getTotalFuel(mass))
			.reduce(0, (acc, cur) -> acc + cur);
	}
	
	private int getTotalFuel(int mass) {
		int totalFuel = 0;
		mass = getFuel(mass);
		while (mass > 0) {
			totalFuel += mass;
			mass = getFuel(mass);
		} 
		return totalFuel;
	}
	
	public static void main(String[] args) {
		Day1 part1 = new Day1("inputs/");
		System.out.println("Part One: " + part1.getFuelReqPartOne());
		
		Day1 part2 = new Day1("inputs/");
		System.out.println("Part Two: " + part2.getFuelReqPartTwo());
	}
}


