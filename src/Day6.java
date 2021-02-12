import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Map;
import java.util.Set;
import java.util.stream.Stream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.IOException;
import java.nio.file.Files;

public class Day6 {

	private final Path path;
	private Stream<String> orbits;
	private final String startObj;
	private final String targetObj = "SAN";

	public Day6(String path, String startObj) {
		this.path = Paths.get(path + this.getClass().getSimpleName().toLowerCase() + ".txt");
		try {
			orbits = Files.lines(this.path);
		} catch (IOException ex) {
			System.out.println("Error: " + ex);
		}
		this.startObj = startObj;
	}
	
	
	// Part One
	public int getTotalOrbits() {
		Map<String, OrbitObj> orbitMap = new HashMap<>();
		orbits.forEach(orbit -> addToOrbitObj(orbitMap, orbit));
		return getTotalOrbitsRecur(orbitMap, orbitMap.get(startObj).getMoons(), 1);
	}
	
	private void addToOrbitObj(Map<String, OrbitObj> orbitMap, String orbit) {
		String earth = orbit.split("\\)")[0];
		String moon = orbit.split("\\)")[1];
		if (!orbitMap.containsKey(earth)) {
			orbitMap.put(earth, new OrbitObj());
		}
		orbitMap.get(earth).addMoon(moon);
		
		if (!orbitMap.containsKey(moon)) {
			orbitMap.put(moon, new OrbitObj());
		}
		orbitMap.get(moon).setSun(earth);
	}
	
	private int getTotalOrbitsRecur(Map<String, OrbitObj> orbitMap, Set<String> orbiters, int level) {
		int orbitCount = 0;
		for (String orbiter : orbiters) {
			orbitCount += level;
			if (orbitMap.containsKey(orbiter)) {
				orbitCount += getTotalOrbitsRecur(orbitMap, orbitMap.get(orbiter).getMoons(), level + 1);
			}
		}
		return orbitCount;
	}
	
	// Part Two
	public int getShortestPath() {
		Map<String, OrbitObj> orbitMap = new HashMap<>();
		orbits.forEach(orbit -> addToOrbitObj(orbitMap, orbit));
		
		LinkedList<String> orbiterQueue = new LinkedList<String>(orbitMap.get(startObj).getMoons());
		orbiterQueue.add(orbitMap.get(startObj).getSun());
		orbiterQueue.add("INCREASE");
		
		return findTargetDis(orbitMap, orbiterQueue);
	}
	
	private int findTargetDis(Map<String, OrbitObj> orbitMap, LinkedList<String> orbiterQueue) {
		Set<String> visitedSet = new HashSet<>();
		visitedSet.add(startObj);
		int pathDist = 0;
		
		while (orbiterQueue.size() > 0) {
			String orbiter = orbiterQueue.removeFirst();
			if (orbiter.equals(targetObj)) {
				return pathDist - 1;
			}
			if (orbiter.equals("INCREASE")) {
				pathDist++;
				orbiterQueue.add("INCREASE");
				continue;
			}
			addConnectedObj(orbitMap.get(orbiter), orbiterQueue, visitedSet);
			visitedSet.add(orbiter);
		}
		
		return -1;
	}
	
	private void addConnectedObj(OrbitObj orbitObj, LinkedList<String> orbiterQueue, Set<String> visitedSet) {
		for(String obj : orbitObj.getAllObj()) {
			if (!visitedSet.contains(obj)) {
				orbiterQueue.add(obj);
			}
		}
	}
	
	// Nested Graph Class
	public class OrbitObj {
		private String sun;
		private Set<String> moons;
		
		public OrbitObj() {
			this.moons = new HashSet<>();
		}
		
		public void setSun(String sun) {
			this.sun = sun;
		}
		
		public void addMoon(String moon) {
			moons.add(moon);
		}
		
		public String getSun() {
			return sun;
		}
		
		public Set<String> getMoons() {
			return moons;
		}
		
		public Set<String> getAllObj() {
			Set <String> allObj = new HashSet<>(moons);
			if (sun != null) {
				allObj.add(sun);
			}
			return allObj;
		}
		
	}
	
	public static void main(String[] args) {
		Day6 part1 = new Day6("inputs/", "COM");
		System.out.println("Part One: " + part1.getTotalOrbits());
		
		Day6 part2 = new Day6("inputs/", "YOU");
		System.out.println("Part Two: " + part2.getShortestPath());
	}
}


