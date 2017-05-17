package org.ascotte.codingame.code4life.simulation;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

import java.util.Map;
import java.util.Scanner;

public class Player {

    public static void mainmain(String args[]) {
        Scanner in = new Scanner(System.in);
        int projectCount = in.nextInt();
        for (int i = 0; i < projectCount; i++) {
            int a = in.nextInt();
            int b = in.nextInt();
            int c = in.nextInt();
            int d = in.nextInt();
            int e = in.nextInt();
        }

        // game loop
        while (true) {
            for (int i = 0; i < 2; i++) {
                String target = in.next();
                int eta = in.nextInt();
                int score = in.nextInt();
                int storageA = in.nextInt();
                int storageB = in.nextInt();
                int storageC = in.nextInt();
                int storageD = in.nextInt();
                int storageE = in.nextInt();
                int expertiseA = in.nextInt();
                int expertiseB = in.nextInt();
                int expertiseC = in.nextInt();
                int expertiseD = in.nextInt();
                int expertiseE = in.nextInt();
            }
            
            int availableA = in.nextInt();
            int availableB = in.nextInt();
            int availableC = in.nextInt();
            int availableD = in.nextInt();
            int availableE = in.nextInt();
            
            int sampleCount = in.nextInt();
            System.err.println("Sample count " + " = " + sampleCount);
            
            for (int i = 0; i < sampleCount; i++) {
                int sampleId = in.nextInt();
                int carriedBy = in.nextInt();
                int rank = in.nextInt();
                String expertiseGain = in.next();
                int health = in.nextInt();
                int costA = in.nextInt();
                int costB = in.nextInt();
                int costC = in.nextInt();
                int costD = in.nextInt();
                int costE = in.nextInt();   
            }
        }
    }
    
    public static void main(String args[]) {
    	
    }
}

class Robot {
	final Carrier id;
	
	Module fromModule;
	Module toModule;
	int distanceToModule;
	int score;
	int[] expertise = new int[Constants.NB_MOLECULES];
	int[] storage = new int[Constants.NB_MOLECULES];

	Robot(Carrier id) {
		this.id = id;
	}
	
	public void moveToModule(Module toModule) throws InvalidMoveException {
		if (this.distanceToModule != 0) { throw new InvalidMoveException(Messages.exception_invalid_move_not_arrived); }
		if (this.toModule == null) { throw new InvalidMoveException(Messages.exception_invalid_target_module); }
		if (this.toModule.equals(toModule)) { throw new InvalidMoveException(Messages.exception_invalid_move_same_target); }
		this.fromModule = this.toModule;
		this.toModule = toModule;
		this.distanceToModule = Constants.distancesBetweenModules[this.fromModule.toInteger()][this.toModule.toInteger()];
	}
	
	public void addScore(int score) {
		this.score += score;
	}
	
	public void addExpertise(Molecule molecule) {
		this.expertise[molecule.toInteger()]++;
	}
	
	public void addMolecule(Molecule molecule) throws InvalidStorageOrderException {
		if (this.getStorageFreeCapacity() == 0) { throw new InvalidStorageOrderException(Messages.exception_storage_is_full); } 
		this.storage[molecule.toInteger()]++;
	}
	
	public void removeMolecule(Molecule molecule) throws InvalidStorageOrderException {
		if (!(this.storage[molecule.toInteger()] > 0)) { throw new InvalidStorageOrderException(Messages.exception_not_found_molecule); }
	}
	
	public int getStorageFreeCapacity() throws InvalidStorageOrderException {
		int totalStorageCapacity = 0;
		int freeCapacity;
		
		for (int i = 0; i < Constants.NB_MOLECULES; i++) {
			totalStorageCapacity += this.storage[i];
		}
		
		freeCapacity = Constants.MAX_STORAGE - totalStorageCapacity;
		if (freeCapacity < 0) { throw new InvalidStorageOrderException(Messages.exception_invalid_free_capacity); }
		return freeCapacity;
	}
}


class Sample {
	private final int id;
	private final Rank rank;
	
	private int health;
	private Molecule expertiseMolecule;
	private int[] cost = new int[Constants.NB_MOLECULES];
	
	private Carrier carriedBy;
	private boolean diagnosed;
	
	Sample(int id, Rank rank) {
		this.id = id;
		this.rank = rank;
		this.diagnosed = false;
	}
	
	public boolean diagnose(int health, Molecule expertiseMolecule, int[] cost) {
		if (this.diagnosed) { return false; }	// Already diagnosed
		this.health = health;
		this.expertiseMolecule = expertiseMolecule;
		this.cost = cost;
		this.diagnosed = true;
		return true;
	}
	
	public void updateCarrier(Carrier carriedBy) {
		this.carriedBy = carriedBy;
	}
}

class Constants {
	final static int NB_MOLECULES = 5;
	final static int NB_MODULES = 5;
	final static int[][] distancesBetweenModules = {{0, 0, 0, 0, 0},
													{0, 0, 0, 0, 0},
													{0, 0, 0, 0, 0},
													{0, 0, 0, 0, 0},
													{0, 0, 0, 0, 0}};
	final static int MAX_STORAGE = 10;
}

class Messages {
	final static String exception_invalid_move_not_arrived = "Remaining distance is not equal to 0";
	final static String exception_invalid_move_same_target = "Target module is the same than current module";
	final static String exception_invalid_target_module = "Target module name is invalid (maybe null)";
	final static String exception_too_much_molecules = "Too much molecules for current storage capacity";
	final static String exception_not_found_molecule = "Requested molecule was not found inside storage";
	final static String exception_invalid_free_capacity = "Current storage is over maximal capacity";
	final static String exception_storage_is_full = "Storage is full, not possible to add a new molecule";
}

enum Module {
	SAMPLES(0), DIAGNOSIS(1), MOLECULES(2), LABORATORY(4), CENTER(5); 
	private final int id;
	private final static Map<Integer, Module> map =
		stream(Module.values()).collect(toMap(module -> module.toInteger(), module -> module));

	Module(int id) { this.id = id; }
	Module get(int id) { return map.get(id); }
	public int toInteger() { return this.id; }
}

enum Molecule {
	A(0), B(1), C(2), D(3), E(4);
	private final int id;
	private final static Map<Integer, Molecule> map = 
			stream(Molecule.values()).collect(toMap(molecule -> molecule.toInteger(), molecule -> molecule));
	
	Molecule(int id) { this.id = id;}
	Molecule get(int id) { return map.get(id); }
	public int toInteger() { return this.id; }
}

enum Rank {
	R1(1), R2(2), R3(3);
	private final int id;
	private final static Map<Integer, Rank> map =
			stream(Rank.values()).collect(toMap(rank -> rank.toInteger(), rank -> rank));
	
	Rank(int id) { this.id = id; }
	Rank get(int id) { return map.get(id); }
	public int toInteger() { return this.id; }
}

enum Carrier {
	PLAYER, OPPONENT, CLOUD;
}

class InvalidMoveException extends Exception {
	private static final long serialVersionUID = 1L;
	InvalidMoveException(String message) {
		super(message);
	}
}

class InvalidStorageOrderException extends Exception {
	private static final long serialVersionUID = 1L;
	InvalidStorageOrderException(String message) {
		super(message);
	}
}