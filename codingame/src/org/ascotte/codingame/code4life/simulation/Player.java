package org.ascotte.codingame.code4life.simulation;

import static java.util.Arrays.stream;
import static java.util.stream.Collectors.toMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class Player {

	static Engine engine = new Engine();
	
    public static void main(String args[]) {
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
}

class Robot {
	final Carrier id;
	
	ModuleName fromModule;
	ModuleName toModule;
	int distanceToModule;
	int score;
	int[] expertise = new int[Constants.NB_MOLECULES];
	int[] storage = new int[Constants.NB_MOLECULES];
	List<Sample> samples = new ArrayList<Sample>();

	Robot(Carrier id) {
		this.id = id;
	}
	
	public void moveToModule(ModuleName toModule) throws InvalidMoveException {
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
	
	public void addSample(Sample sample) throws InvalidSampleOrderException {
		if (this.samples.size() == Constants.MAX_SAMPLES_BY_ROBOT) {
			throw new InvalidSampleOrderException(Messages.exception_max_samples_by_robot_reached);
		}
		this.samples.add(sample);
	}
	
	public void removeSample(Sample sample) throws InvalidSampleOrderException {
		if (!this.samples.contains(sample)) {
			throw new InvalidSampleOrderException(Messages.exception_sample_is_not_found);
		}
		this.samples.remove(sample);
	}
	
	public int getSampleFreeCapacity() throws InvalidSampleOrderException {
		int freeCapacity = Constants.MAX_SAMPLES_BY_ROBOT - this.samples.size();
		if (freeCapacity < 0) { throw new InvalidSampleOrderException(Messages.exception_invalid_free_capacity); }
		return freeCapacity;
	}
}

class Project {
	private final int id;
	private final int[] cost;
	private boolean isActive;
	
	Project(int id, int[] cost) {
		this.id = id;
		this.cost = cost;
		this.isActive = true;
	}
	
	public boolean checkCompletion(int [] expertise) {
		if (!this.isActive) { return false; }
		for (int i = 0; i < Constants.NB_MOLECULES; i++) {
			if (this.cost[i] > expertise[i]) {
				return false;
			}
		}
		this.isActive = false;
		return true;
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

class Engine {

	private final Robot player = new Robot(Carrier.PLAYER);
	private final Robot opponent = new Robot(Carrier.OPPONENT);
	private final Module samplesModule = new SamplesModule();
	private final Module diagnosisModule = new DiagnosisModule();
	private final Module moleculesModule = new MoleculesModule();
	private final Module laboratoryModule = new LaboratoryModule();
	
	Engine() {
		
	}
	
	public void run() {
		
	}
	
	public Robot getRobot(Carrier carrier) {
		Robot robot = null;
		if (Carrier.PLAYER.equals(carrier)) { robot = this.player; }
		else if(Carrier.OPPONENT.equals(carrier)) { robot = this.opponent; }
		return robot;
	}
	
	private boolean goToModule(Robot robot, ModuleName toModule) {
		try {
			robot.moveToModule(toModule);
			return true;
		} catch (InvalidMoveException e) {
			System.err.println(e.getMessage());
			return false;
		}
	}
}

class Module {
	ModuleName name;
	
	Module(ModuleName name) {
		this.name = name;
	}
}

class SamplesModule extends Module {
	
	SamplesModule() {
		super(ModuleName.SAMPLES);
	}
}

class DiagnosisModule extends Module {
	
	DiagnosisModule() {
		super(ModuleName.DIAGNOSIS);
	}
}

class MoleculesModule extends Module {
	
	MoleculesModule() {
		super(ModuleName.MOLECULES);
	}
}

class LaboratoryModule extends Module {
	
	LaboratoryModule() {
		super(ModuleName.LABORATORY);
	}
}

class Constants {
	final static int NB_MOLECULES = 5;
	final static int NB_MODULES = 5;
	final static int[][] distancesBetweenModules = {{0, 3, 3, 3, 2},
													{3, 0, 3, 4, 2},
													{3, 3, 0, 3, 2},
													{3, 4, 3, 0, 2},
													{2, 2, 2, 2, 0}};
	final static int MAX_STORAGE = 10;
	final static int MAX_SAMPLES_BY_ROBOT = 3;
}

class Messages {
	final static String exception_invalid_move_not_arrived = "Remaining distance is not equal to 0";
	final static String exception_invalid_move_same_target = "Target module is the same than current module";
	final static String exception_invalid_target_module = "Target module name is invalid (maybe null)";
	final static String exception_too_much_molecules = "Too much molecules for current storage capacity";
	final static String exception_not_found_molecule = "Requested molecule was not found inside storage";
	final static String exception_invalid_free_capacity = "Current storage is over maximal capacity";
	final static String exception_storage_is_full = "Storage is full, not possible to add a new molecule";
	final static String exception_max_samples_by_robot_reached = "The number max of samples by robot is reached";
	final static String exception_sample_is_not_found = "Sample is not found in the sample list";
}

enum ModuleName {
	SAMPLES(0), DIAGNOSIS(1), MOLECULES(2), LABORATORY(4), CENTER(5); 
	private final int id;
	private final static Map<Integer, ModuleName> map =
		stream(ModuleName.values()).collect(toMap(module -> module.toInteger(), module -> module));

	ModuleName(int id) { this.id = id; }
	ModuleName get(int id) { return map.get(id); }
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

enum Order {
	GOTO_DIAGNOSIS, GOTO_MOLECULES, GOTO_LABORATORY, GOTO_SAMPLES;
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

class InvalidSampleOrderException extends Exception {
	private static final long serialVersionUID = 1L;
	InvalidSampleOrderException(String message) {
		super(message);
	}
}