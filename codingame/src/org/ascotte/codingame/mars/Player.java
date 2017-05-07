package org.ascotte.codingame.mars;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Random;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

	// Engine constants
	final static int MAX_POWER = 4;
	final static int MIN_POWER = 0;
	final static int MAX_ROTATION = 15;
	final static int MIN_ROTATION = -15;
	final static int MAX_ANGLE = 90;
	final static int MIN_ANGLE = -90;
	final static int MAX_SPEED = 500;
	final static int MIN_SPEED = -500;
	final static int MIN_X = 0;
	final static int MAX_X = 7000;
	final static int MIN_Y = 0;
	final static int MAX_Y = 3000;
	final static int MAX_VSPEED_TO_LAND = 40;
	final static int MAX_HSPEED_TO_LAND = 20;	
	
	// Physic constants
	final static double G = 3.711;
	
	// Engine objects
	static Ship ship = null;
	final static World world = new World(MAX_X, MAX_Y);
	
	// Genetic model parameters
	final static int NB_GENES = 50;
	final static int NB_GENOMES = 36;
	final static int NB_POPULATIONS = 100;
	final static int KEEP_TOURNAMENT_WINNER_PERCENTAGE = 95;
	final static int MUTATION_PERCENTAGE = 3;
	final static int MUTED_GENES_PER_MUTATION = 6;
	final static int[] rotations = {-90, 0, 0, 90};
	final static int[] powers = {0, 4, 4, 4};
	final static Random rand = new Random();

	// Population objects
	static HashSet<Genome> population = new HashSet<Genome>();
	static HashSet<Genome> nextPopulation = new HashSet<Genome>();
	
    public static void main(String args[]) {
       
    	Scanner in = new Scanner(System.in);
    	
    	int surfaceN = in.nextInt(); // the number of points used to draw the surface of Mars.
    	int previousLandX = -1;
        int previousLandY = -1;
        
    	for (int i = 0; i < surfaceN; i++) {
        	int landX = in.nextInt(); // X coordinate of a surface point. (0 to 6999)
            int landY = in.nextInt(); // Y coordinate of a surface point. By linking all the points together in a sequential fashion, you form the surface of Mars.
            
            if (i != 0) {
            	world.addSegment(previousLandX, previousLandY, landX, landY);
            }
            
            previousLandX = landX;
            previousLandY = landY;
        }

        // game loop
        while (true) {
        	
        	int x = in.nextInt();
            int y = in.nextInt();
            int hSpeed = in.nextInt(); // the horizontal speed (in m/s), can be negative.
            int vSpeed = in.nextInt(); // the vertical speed (in m/s), can be negative.
            int fuel = in.nextInt(); // the quantity of remaining fuel in liters.
            int rotate = in.nextInt(); // the rotation angle in degrees (-90 to 90).
            int power = in.nextInt(); // the thrust power (0 to 4).
        	
            if (ship == null) {
        		ship = new Ship(x, y, hSpeed, vSpeed, fuel, rotate, power);
        	}
        	
            log("Hspeed = " + ship.hSpeed + " " + "Vspeed = " + ship.vSpeed);
        	log("X = " + ship.x + " Y = " + ship.y);
        	log("Fuel = " + ship.fuel);
        	log("Angle = " + ship.rotate);
        	
    		// Genetic model
        	createPopulation();
        	for (int i = 0; i < Player.NB_POPULATIONS; i++) {
        		
        		selectPopulationByTournament();
        		completeNextPopulation();
            	mutateNextPopulation();
            	
        		HashSet<Genome> tempPopulation = population;
        		population = nextPopulation;
        		nextPopulation = tempPopulation;
        		nextPopulation.clear();
        	}

        	// Get the best genome in the final population for playing
        	evaluatePopulation();
        	
        	Genome bestGenome = selectBestGenome();
        	Gene gene = bestGenome.getFirstGene();
        	move(gene.rotation, gene.power, true);
        	play(ship.rotate, gene.power);
        	log("Best evaluation = " + bestGenome.evaluation);
        	log("Nb turn to land = " + bestGenome.nbTurnToLand);
        	log("World " + bestGenome.endX + " " + bestGenome.endY);
            population.clear();
        }
    }
    
    public static void log(String message) {
    	System.err.println(message);
    }
    
    public static void play(int rotation, int power) {
    	System.out.println(rotation + " " + power);
    }
    
    public static void evaluatePopulation() {
    	for (Genome genome:population) {
    		genome.evaluate();
    	}
    }
    
    public static Genome selectBestGenome() {

    	Genome bestGenome = null;
    	int bestEvaluation = -1;
    	boolean areAllCrashed = true;
    	
    	for (Genome genome:population) {
    		if (genome.evaluation > bestEvaluation) {
    			bestEvaluation = genome.evaluation;
    			bestGenome = genome;
    		}
    		if (!genome.isCrashed) {
    			areAllCrashed = false;
    		}
    	}
    	
    	return bestGenome;
    }
    
    public static void mutateNextPopulation() {
    	
    	for (Genome genome:nextPopulation) {
    		if (rand.nextInt(100) < MUTATION_PERCENTAGE) {
    			for (int i = 0; i < MUTED_GENES_PER_MUTATION; i++) {
    				genome.replaceGene(rand.nextInt(Math.min(NB_GENES, genome.nbTurnToLand+1)));
    			}
    		}
    	}
    }
    
    public static void completeNextPopulation() {
    	int numGenome = 0;
    	Genome previousGenome = null;
    	HashSet<Genome> childs = new HashSet<Genome>();
    	
    	for (Genome genome:nextPopulation) {
    		if (numGenome%2 == 1) {
    			int split = rand.nextInt(NB_GENES);
    			Genome child1 = new Genome();
    			child1.copyGenes(genome, 0, split);
    			child1.copyGenes(previousGenome, split, NB_GENES);
    			Genome child2 = new Genome();
    			child2.copyGenes(previousGenome, 0, split);
    			child2.copyGenes(genome, split, NB_GENES);
    			childs.add(child1);
    			childs.add(child2);
    		}
    		
    		previousGenome = genome;
    		numGenome++;
    	}
    	
		nextPopulation.addAll(childs);

    	Genome genome = new Genome();
    	genome.generateRandomGenes();
    	nextPopulation.add(genome);
    }
    
    public static void createPopulation() {
    	for (int i = 0; i < NB_GENOMES; i++) {
    		Genome genome = new Genome();
    		genome.generateRandomGenes();
    		population.add(genome);
    	}
    }

    public static void selectPopulationByTournament() {
    	int numGenome = 0;
    	Genome previousGenome = null;
    	
    	for (Genome genome:population) {
    		genome.evaluate();
    		if (numGenome%2 == 1) {    			
    			Genome bestGenome;
    			Genome worstGenome;
    			if (genome.evaluation >= previousGenome.evaluation) {
    				bestGenome = genome;
    				worstGenome = previousGenome;
    			} else {
    				bestGenome = previousGenome;
    				worstGenome = genome;
    			}
    			
    			if (rand.nextInt(100) < KEEP_TOURNAMENT_WINNER_PERCENTAGE) {
    				nextPopulation.add(bestGenome);
    			}
    			else {
    				nextPopulation.add(worstGenome);
    			}
    		}
    		
    		previousGenome = genome;
    		numGenome++;
    	}
    }
    
	public static void move(int rotate, int power, boolean audit) {

		// Power calculation
		power = ship.power + Integer.signum(power-ship.power);
		power = Math.min(Math.max(power, MIN_POWER), MAX_POWER);
		ship.power = power;
		
		// Rotation calculation
		rotate = ship.rotate + (Math.min(Math.abs(rotate), MAX_ROTATION)) * Integer.signum(rotate);
		rotate = Math.min(Math.max(rotate, MIN_ANGLE), MAX_ANGLE);
		ship.rotate = rotate;
		
		double forceY = - G + (double)ship.power * Math.cos(Math.toRadians(ship.rotate));
		double forceX = - (double)ship.power * Math.sin(Math.toRadians(ship.rotate));
				
		// New vertical location
		double startY = ship.y;
		ship.y += forceY / 2 + ship.vSpeed;
		double endY = ship.y;
		
		// Vertical speed calculation
		ship.vSpeed = ship.vSpeed + forceY;
		ship.vSpeed = Math.min(Math.max(ship.vSpeed, MIN_SPEED), MAX_SPEED);
		
		// New horizontal location
		double startX = ship.x;
		ship.x += forceX / 2 + ship.hSpeed;
		double endX = ship.x;
		
		ship.hSpeed = ship.hSpeed + forceX;
		ship.hSpeed = Math.min(Math.max(ship.hSpeed, MIN_SPEED), MAX_SPEED);
		
		// Check obstacles
		checkEndOfMap(endX, endY);
		checkObstacles(startX, startY, endX, endY);
		checkLanding(startX, startY, endX, endY, audit);
		
		// Update properties
		ship.fuel -= power;
	}
	
	public static void checkEndOfMap(double endX, double endY) {
		
		if (endY < 0 || endY >= world.y || endX < 0 || endX >= world.x) {
			ship.crash();
		}
	}
	
	public static void checkObstacles(double startX, double startY, double endX, double endY) {
		
		Segment shipSegment = new Segment(startX, startY, endX, endY);
		
		for (Segment worldSegment:world.crashSegments) {
			if (Segment.checkIntersection(shipSegment, worldSegment)){
					ship.crash();
			}
		}
	}
	
	public static void checkLanding(double startX, double startY, double endX, double endY, boolean audit) {

		Segment shipSegment = new Segment(startX, startY, endX, endY);
		Segment landSegment = world.landSegment;
		if (landSegment == null) {return;}
		if (Segment.checkIntersection(shipSegment, landSegment)) {
			if (ship.rotate == 0 && Math.abs(ship.hSpeed) < MAX_HSPEED_TO_LAND
					&& Math.abs(ship.vSpeed) < MAX_VSPEED_TO_LAND) {
				ship.land();
			} else {
				ship.crash();
			}
		}
	}
	
	public static int getDistanceFromShipToTarget() {
		
		int distance = 0;
		Segment landSegment = world.landSegment;
		if (landSegment == null) { return -1; }

		if (ship.x < landSegment.startX + 50) {
			distance = (int)Math.sqrt(Math.pow(landSegment.startX + 50 - ship.x, 2) + Math.pow(ship.y - ship.y, 2));
			
			// Penalty if lower than a pic between ship and target
			double maxY = 0;
			for (Segment worldSegment:world.crashSegments) {
				if (ship.x < landSegment.startX && ship.x < worldSegment.endX && worldSegment.endX < landSegment.endX) {
					if (worldSegment.startY > maxY) {
						maxY = worldSegment.startY;
					}
					if (worldSegment.endY > maxY) {
						maxY = worldSegment.endY;
					} 
				}
			}
			if (ship.y < maxY) {
				distance += 50 * (maxY - ship.y);
			}
			
		}
		else if (ship.x > landSegment.endX - 50) {
			distance = (int)Math.sqrt(Math.pow(landSegment.endX - 50 - ship.x, 2) + Math.pow(ship.y - ship.y, 2));
		
			// Penalty if lower than a pic between ship and target
			double maxY = 0;
			for (Segment worldSegment:world.crashSegments) {
				if (ship.x > landSegment.endX && ship.x > worldSegment.startX && worldSegment.startX > landSegment.startX) {
					if (worldSegment.startY > maxY) {
						maxY = worldSegment.startY;
					}
					if (worldSegment.endY > maxY) {
						maxY = worldSegment.endY;
					} 
				}
			}
			if (ship.y < maxY) {
				distance += 50 * (maxY - ship.y);
			}
			
		}
		else {
			distance = (int)((Math.abs(landSegment.a * ship.x - ship.y + landSegment.b)) / Math.sqrt(Math.pow(landSegment.a, 2) + Math.pow(-1, 2)));
		}
		
		// Penalty if lower than target
		if (ship.y < landSegment.startY) {
			distance += 50 * (landSegment.startY - ship.y);
		}
		
		return distance;
	}
}

class Genome {
	
	ArrayList<Gene> genes = new ArrayList<Gene>();
	int evaluation = 0;
	int evaluationTop = 0;
	int nbTurnToLand = 0;
	double endX = 0;
	double endY = 0;
	double vSpeed = 0;
	boolean isCrashed;
	
	Genome() {
	}
	
	void evaluate() {
		
		Ship backupShip = Player.ship.clone();
		int evaluation = 1000000;
		nbTurnToLand = 0;
		
		for (Gene gene:genes) {
			if (Player.ship.isCrashed || Player.ship.isLanded) { break;}
			Player.move(gene.rotation, gene.power, false);
			nbTurnToLand++;
		}
		
		// If ship is landed
		if (Player.ship.isLanded) {
			evaluation += Player.ship.fuel;
		}
		else {
			
			// Respect arrival conditions in priority
			evaluation -= 10000 * (int)Math.max(Math.abs(Player.ship.hSpeed) - Player.MAX_HSPEED_TO_LAND,0);
			evaluation -= 10000 * (int)Math.max(Math.abs(Player.ship.vSpeed) - Player.MAX_VSPEED_TO_LAND,0);
			
			// Then better distance
			evaluation -= Player.getDistanceFromShipToTarget();
			
			// Penalty if crashed
			if (Player.ship.isCrashed) {
				evaluation -= 500000;
			}
		}
		
		this.evaluation = evaluation;
		this.endX = Player.ship.x;
		this.endY = Player.ship.y;
		this.vSpeed = Player.ship.vSpeed;
		this.isCrashed = Player.ship.isCrashed;
		Player.ship = backupShip;
	}
	
	Gene getFirstGene() {
		return genes.get(0);
	}
	
	void replaceGene(int offset) {
		genes.set(offset, new Gene());
	}
	
	void generateRandomGenes() {
		for (int i = 0; i < Player.NB_GENES; i++) {
			genes.add(new Gene());
		}
	}
	
	void copyGenes(Genome genome, int from, int to) {
		for (int i = from; i < to; i++) {
			this.genes.add(genome.genes.get(i));
		}
	}
}

class Gene {
	int rotation;
	int power;
	
	Gene() {
		int randomRotationId = Player.rand.nextInt(Player.rotations.length);
		int randomPowerId = Player.rand.nextInt(Player.powers.length);
		this.rotation = Player.rotations[randomRotationId];
		this.power = Player.powers[randomPowerId];
	}
}

class Ship implements Cloneable {
	double x;
	double y;
	double hSpeed;
	double vSpeed;
	int fuel;
	int rotate;
	int power;
	boolean isCrashed = false;
	boolean isLanded = false;
	
	public Ship(int x, int y, int hSpeed, int vSpeed, int fuel, int rotate, int power) {
		this.x = x;
		this.y = y;
		this.hSpeed = hSpeed;
		this.vSpeed = vSpeed;
		this.fuel = fuel;
		this.rotate = rotate;
		this.power = power;
	}

	public Ship() {	
	}
	
	void land() {
		this.isLanded = true;
	}
	
	void crash() {
		this.isCrashed = true;
	}
	
	public Ship clone() {
		Object o = null;
		try {
			o = super.clone();
		} catch(CloneNotSupportedException cnse) {
			cnse.printStackTrace(System.err);
		}
		return (Ship)o;
	}
}

class World {
	int[] platform;
	Segment landSegment;
	LinkedList<Segment> crashSegments = new LinkedList<Segment>();
	int x;
	int y;
	
	World(int x, int y) {
		this.platform = new int[x];
		this.x = x;
		this.y = y;
	}
	
	void addSegment(int startX, int startY, int endX, int endY) {
		if (startY == endY && (endX - startX >= 1000)) {
			this.landSegment = new Segment(startX, startY, endX, endY);
		}
		else {
			this.crashSegments.add(new Segment(startX, startY, endX, endY));
		}
	}
}

class Segment {
	double startX;
	double startY;
	double endX;
	double endY;
	double a;
	double b;
	
	Segment(double startX, double startY, double endX, double endY) {
		double a = Double.NaN;
		
		if (startX > endX || (startX == endX && startY > endY)) {
			double tempX = startX;
			double tempY = startY;
			startX = endX;
			startY = endY;
			endX = tempX;
			endY = tempY;
		}
		
		if (endX != startX) { a = (endY - startY) / (endX - startX); }
		else { a = Double.POSITIVE_INFINITY; }
		this.b = startY - a * startX;
		this.startX = startX;
		this.endX = endX;
		this.startY = startY;
		this.endY = endY;
		this.a = a;
	}
	
	static boolean checkIntersection(Segment segment1, Segment segment2) {
		double xCollision = 0d;
		double yCollision = 0d;
		
		if (segment1.a == segment2.a) { return false; }
		if (Double.isInfinite(segment1.a)) {
			yCollision = segment2.a * segment1.startX + segment2.b;
			if ((
					(yCollision >= segment1.startY && yCollision <= segment1.endY) ||
					(yCollision <= segment1.startY && yCollision >= segment1.endY)
				)
					&& 
				(
					(yCollision >= segment2.startY && yCollision <= segment2.endY) ||
					(yCollision <= segment2.startY && yCollision >= segment2.endY)
				)) {
				return true;
			}
		}
		else if (Double.isInfinite(segment2.a)) {
			yCollision = segment1.a * segment2.startX + segment1.b;
			if ((
					(yCollision >= segment1.startY && yCollision <= segment1.endY) ||
					(yCollision <= segment1.startY && yCollision >= segment1.endY)
				)
					&& 
				(
					(yCollision >= segment2.startY && yCollision <= segment2.endY) ||
					(yCollision <= segment2.startY && yCollision >= segment2.endY)
				)) {
				return true;
			}
		}
		else {
			xCollision = (segment1.b - segment2.b) / (segment2.a - segment1.a);
			if (xCollision >= segment1.startX && xCollision >= segment2.startX &&
					xCollision <= segment1.endX && xCollision <= segment2.endX) {
				return true;
			}
		}
		return false;
	}
}

