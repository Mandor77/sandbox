package org.ascotte.codingame.code4life;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 * Bring data on patient samples from the diagnosis machine to the laboratory with enough molecules to produce medicine!
 **/
class Player {

	static HashMap<Integer, Sample> samples = new HashMap<Integer, Sample>(); 
	static PriorityQueue<Sample> samplesQueue = new PriorityQueue<Sample>();
	static int NB_MOLECULES = 5;
	static int availables[] = new int[NB_MOLECULES];
	
	static Robot player = new Robot(Carrier.PLAYER);
	static Robot opponent = new Robot(Carrier.OPPONENT);
	
	static String NO_DIAGNOSED_EXPERTISE_VALUE = "0";
	
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
                
                if (i == 0) {
                	player.setModule(Module.fromString(target), eta);
                	player.setStorage(storageA, storageB, storageC, storageD, storageE);
                	player.setExpertise(expertiseA, expertiseB, expertiseC, expertiseD, expertiseE);
                	player.setScore(score);
                } else {
                	opponent.setModule(Module.fromString(target), eta);
                	opponent.setStorage(storageA, storageB, storageC, storageD, storageE);
                	opponent.setExpertise(expertiseA, expertiseB, expertiseC, expertiseD, expertiseE);
                	opponent.setScore(score);
                }
            }
            
            int availableA = in.nextInt();
            int availableB = in.nextInt();
            int availableC = in.nextInt();
            int availableD = in.nextInt();
            int availableE = in.nextInt();
            setAvailables(availableA, availableB, availableC, availableD, availableE);
            
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
                
                Sample sample;
                // Add sample to map if not exists
                if (!samples.containsKey(sampleId)) {
                	sample = new Sample(sampleId, Rank.fromInteger(rank-1));
                	samples.put(sampleId, sample);
                	samplesQueue.add(sample);
                    // Update carrier
                    sample.setCarriedBy(Carrier.fromInteger(carriedBy));
                	if (Carrier.PLAYER.equals(sample.carriedBy)) {
                		player.addSample(sample);
                	}
                }
                else {
                	sample = samples.get(sampleId);
                	sample.setCarriedBy(Carrier.fromInteger(carriedBy));
                }

                // Check if sample was diagnosed
                if (!sample.diagnosed && !NO_DIAGNOSED_EXPERTISE_VALUE.equals(expertiseGain)) {
                	sample.setDiagnosed(health, Molecule.fromString(expertiseGain));
                	sample.setCost(costA, costB, costC, costD, costE, player.expertise);
                }
            }

            player.reassignMolecule();
            play();
        }
    }
    
    static void play() {
    	playClassic();
    }
    
    /**
     * Play from no where
     */
    static void playFromNoWhere() {
    	if (playFromNoWhereR1()) { return; }  // Go to samples
    }
    
    static boolean playFromNoWhereR1() {
    	goTo(Module.SAMPLES);
    	return true;
    }
    
    
    /**
     * Play from samples
     */
    static void playFromSamples() {
    	if (playFromSamplesR1()) { return; }	// Fill with not diagnosed samples 
    	if (playFromSamplesR2()) { return; }	// Go to diagnosis
    }
    
    static boolean playFromSamplesR1() {
    	if(player.getFreeSample() != 0) {
    		if (player.getExpertiseLevel() < 4) {
    			connectTo(Rank.R0.rank + 1);
    		}
    		else if (player.getExpertiseLevel() < 9) {
    			connectTo(Rank.R1.rank + 1);
    		}
    		else {
    			connectTo(Rank.R2.rank + 1);
    		}
			
			return true;
		}
    	return false;
    }
    
    static boolean playFromSamplesR2() {
    	goTo(Module.DIAGNOSIS);
    	return true;
    }
    
    /**
     * Play from diagnosis
     */
    static void playFromDiagnosis() {
    	if (playFromDiagnosisR1()) { return; }	// Diagnosed undiagnosed samples
    	if (playFromDiagnosisR3()) { return; }	// Waste the worst
    	if (playFromDiagnosisR4()) { return; }
    	if (playFromDiagnosisR2()) { return; }	// Go to molecules
    }
    
    static boolean playFromDiagnosisR1() {
    	for (Sample sample:player.samples) {
    		if (!sample.diagnosed) {
    			connectTo(sample.id);
    			return true;
    		}
    	}
    	return false;
    }
    
    static boolean playFromDiagnosisR3() {
    	if(player.getFreeSample() == 0) {
    		if(player.getFreeMolecule() < 3) {
    			ArrayList<Sample> backups = new ArrayList<Sample>();
    			backups.add(player.samples.remove());
    			backups.add(player.samples.remove());
    			Sample sample = player.samples.remove();
    			connectTo(sample.id);
    			player.samples.addAll(backups);
    			return true;
    		}
    	}
    	return false;
    }
    
    static boolean playFromDiagnosisR4() {
    	for (Sample sample:player.samples) {
    		if(sample.getRemainingDue() > Robot.MAX_MOLECULES) {
    			connectTo(sample.id);
    			return true;
    		}
    	}
    	return false;
    }
    
    static boolean playFromDiagnosisR2() {
    	goTo(Module.MOLECULES);
    	return true;
    }
    
    // Get from diagnosis
    static boolean playFromDiagnosisR30() {
    	if(player.getFreeSample() != 0) {
			Sample sample = null;
			do {
				sample = null;
				if (samplesQueue.size() > 0) {
					sample = samplesQueue.remove();
				}
				else {
					break;
				}
			} while (!sample.carriedBy.equals(Carrier.CLOUD));
			if (sample != null) {
				player.addSample(sample);
				connectTo(sample.id);
				return true;
			}
		}
    	return false;
    }
    
    /**
     * Play from molecule
     */
    static void playFromMolecules() {
    	if (playFromMoleculesR1()) { return; }	// Get adequate molecule (todo : check quantity) for two first samples
    	if (playFromMoleculesR3()) { return; }	// If all samples not paid, go waste one
    	if (playFromMoleculesR2()) { return; }	// Go to laboratory
    }
    
    static boolean playFromMoleculesR1() {
    	ArrayList<Sample> backups = new ArrayList<Sample>();
    	if(player.getFreeMolecule() != 0) {
			LinkedList<Molecule> molecules;
			for (int i = 0; i < Math.min(2, player.samples.size()); i++) {
				Sample sample = player.samples.remove();
				backups.add(sample);
				if (!sample.due.isPaid) {
					molecules = player.getMoleculeToPay(sample);
					for (Molecule molecule:molecules) {
						if (availables[molecule.id] > 0) {
							player.payMolecule(sample, molecule, false);
							connectTo(molecule);
							player.samples.addAll(backups);
							return true;
						}
					}
				}
    		}
		}
    	player.samples.addAll(backups);
    	return false;
    }  
    
    static boolean playFromMoleculesR2() {
    	goTo(Module.LABORATORY);
    	return true;
    }
    
    static boolean playFromMoleculesR3() {
    	for (Sample sample:player.samples) {
			if (sample.due.isPaid) {
				return false;
			}
    	}
    	if (player.getFreeMolecule() == 0 || player.getFreeSample() == 0) {
    		goTo(Module.DIAGNOSIS);
    	}
    	else {
    		goTo(Module.SAMPLES);
    	}
		return true;
    }
    
    /**
     * Play from laboratory
     */
    
    static void playFromLaboratory() {
    	if (playFromLaboratoryR1()) { return; }	// Get adequate molecule (todo : check quantity)
    	if (playFromLaboratoryR3()) { return; } // If can finish the game go to molecules
    	if (playFromLaboratoryR4()) { return; } // If two remaining, no need to get one more
    	if (playFromLaboratoryR2()) { return; }	// Go to laboratory
    }
    
    static boolean playFromLaboratoryR1() {
    	Sample sample = player.getNextPaidSample();
		if (sample != null) {
			player.paySample(sample);
			connectTo(sample.id);
			return true;
		}
		return false;
    }
    
    static boolean playFromLaboratoryR2() {
    	goTo(Module.SAMPLES);
    	return true;
    }
    
    static boolean playFromLaboratoryR3() {
    	if (player.canWin()) {
    		goTo(Module.MOLECULES);
    		return true;
    	}
    	return false;
    }
    
    static boolean playFromLaboratoryR4() {
    	if (player.samples.size() > 1) {
    		goTo(Module.MOLECULES);
    		return true;
    	}
    	return false;
    }

    static void playClassic() {
    
    	if (player.getModule() == null) {
    		playFromNoWhere();
    	}
    	else if (player.isAtModule(Module.SAMPLES)) {
    		playFromSamples();
    	}
    	else if (player.isAtModule(Module.DIAGNOSIS)) {
    		playFromDiagnosis();
    	}
    	else if(player.isAtModule(Module.MOLECULES)) {
    		playFromMolecules();
    	}
    	else if(player.isAtModule(Module.LABORATORY)) {
    		playFromLaboratory();
    	}
    	else {
    		doNothing();
    	}
    }
    
    static void goTo(Module module) {
    	System.out.println("GOTO " + module.text);
    }
    
    static void connectTo(int id) {
    	System.out.println("CONNECT " + id);
    }
    
    static void connectTo(Rank rank) {
    	System.out.println("CONNECT " + rank);
    }
    
    static void connectTo(Molecule molecule) {
    	System.out.println("CONNECT " + molecule.text);
    }
    
    static void doNothing() {
    	System.out.println("WAIT");
    }
    
    static void setAvailables(int availableA, int availableB, int availableC, int availableD, int availableE) {
    	availables[Molecule.A.id] = availableA;
    	availables[Molecule.B.id] = availableB;
    	availables[Molecule.C.id] = availableC;
    	availables[Molecule.D.id] = availableD;
    	availables[Molecule.E.id] = availableE;
    }
}

class Robot {
	
	static int MAX_SAMPLES = 3;
	static int MAX_MOLECULES = 10;
	static int MAX_SCORE = 170;
	
	Module module = null;
	Carrier carrier;
	int distanceToModule = 0;
	int score = 0;
	int nbMolecule = 0;

	int[] expertise = new int[Player.NB_MOLECULES];
	int[] storage = new int[Player.NB_MOLECULES];
	PriorityQueue<Sample> samples = new PriorityQueue<Sample>();
	
	Robot(Carrier carrier) {
		this.carrier = carrier;
	}
	
	void setModule(Module module, int distanceToModule) {
		this.module = module;
		this.distanceToModule = distanceToModule;
	}
	
	Module getModule() {
		return this.module;
	}
	
	boolean isAtModule(Module module) {
		boolean value = false;
		if (this.module.equals(module) && this.distanceToModule == 0) { value = true; }
		return value;
	}
	
	boolean canWin() {
		int score = this.score;
		for (Sample sample:samples) {
			score += sample.health;
		}
		if (score > MAX_SCORE) {
			return true;
		}
		return false;
	}
	public int getExpertiseLevel() {
		return this.expertise[Molecule.A.id] +
				expertise[Molecule.B.id] +
				expertise[Molecule.C.id] +
				expertise[Molecule.D.id] +
				expertise[Molecule.E.id];
	}
	
	public void setScore(int score) {
		this.score = score;
	}
	
	public void reassignMolecule() {
		// Reinitialize cost
		for (Sample sample:samples) {
			sample.due = new Due(sample, expertise);
		}
		for (int numMolecule = 0; numMolecule < Player.NB_MOLECULES; numMolecule++) {
			int nbMolecule = storage[numMolecule];
			for (int j = 0; j < nbMolecule; j++) {
				// On cherche un sample candidat
				for (Sample sample:samples) {
					Molecule molecule = Molecule.fromInteger(numMolecule);
					if (sample.getRemainingDue(molecule) > 0) {
						sample.due.pay(molecule);
						break;
					}
				}
			}
		}
	}
	
	public void setExpertise(Integer expertiseA, int expertiseB, int expertiseC, int expertiseD, int expertiseE) {
		this.expertise[Molecule.A.id] = expertiseA;
		this.expertise[Molecule.B.id] = expertiseB;
		this.expertise[Molecule.C.id] = expertiseC;
		this.expertise[Molecule.D.id] = expertiseD;
		this.expertise[Molecule.E.id] = expertiseE;
	}
	
	public void setStorage(Integer storageA, int storageB, int storageC, int storageD, int storageE) {
		this.storage[Molecule.A.id] = storageA;
		this.storage[Molecule.B.id] = storageB;
		this.storage[Molecule.C.id] = storageC;
		this.storage[Molecule.D.id] = storageD;
		this.storage[Molecule.E.id] = storageE;
		this.nbMolecule = storageA + storageB + storageC + storageD + storageE;
	}
	
	/**
	 * Start of intelligence
	 */
	
	// Return false if max samples is reached
	boolean addSample(Sample sample) {
		boolean status = false;
		if (!(samples.size() > MAX_SAMPLES)) {
			samples.add(sample);
		}
		return status;
	}
	
	// Valid a sample
	void paySample(Sample sample) {
		Molecule molecule = sample.expertiseGain;
		this.expertise[molecule.id]++;
		this.remove(sample);
		for (Sample gameSample:Player.samples.values()) {
			this.payMolecule(gameSample, molecule, true);
		}
	}
	
	void remove(Sample sample) {
		samples.remove(sample);
	}
	
	int getFreeSample() {
		return MAX_SAMPLES - this.samples.size();
	}
	
	Sample getWorstSample() {
		Sample worstSample = null;
		int worstFitness = 999;
		for (Sample sample:samples) {
			if (worstSample == null) { worstSample = sample; break;}
			if (sample.compareTo(worstSample) < 0) { worstSample = sample;}
		}
		return worstSample;
	}
	
	int getFreeMolecule() {
		return MAX_MOLECULES - this.nbMolecule;
	}
	
	void payMolecule(Sample sample, Molecule molecule, boolean free) {
		if (sample.due != null) {
			sample.due.pay(molecule);
		}
	}
	
	Sample getNextPaidSample() {
		for (Sample sample:samples) {
			if (sample.due.isPaid) {
				return sample;
			}
		}
		return null;
	}
	
	Sample getSampleToPay() {
		for (Sample sample:samples) {
			if (!sample.due.isPaid) {
				return sample;
			}
		}
		return null;
	}
	
	LinkedList<Molecule> getMoleculeToPay(Sample sample) {
		return sample.due.getNextDue();
	}
}

class Due {

	int[] due = new int[Player.NB_MOLECULES];
	boolean isPaid = false;
	
	Due(Sample sample, int[] expertise) {
		this.due[Molecule.A.id] = Math.max(0, sample.cost[Molecule.A.id] - expertise[Molecule.A.id]);
		this.due[Molecule.B.id] = Math.max(0, sample.cost[Molecule.B.id] - expertise[Molecule.B.id]);
		this.due[Molecule.C.id] = Math.max(0, sample.cost[Molecule.C.id] - expertise[Molecule.C.id]);
		this.due[Molecule.D.id] = Math.max(0, sample.cost[Molecule.D.id] - expertise[Molecule.D.id]);
		this.due[Molecule.E.id] = Math.max(0, sample.cost[Molecule.E.id] - expertise[Molecule.E.id]);
		this.checkIfPaid();
	}
	
	public LinkedList<Molecule> getNextDue() {
		LinkedList<Molecule> molecules = new LinkedList<Molecule>();
		
		if (due[Molecule.A.id] > 0) { molecules.add(Molecule.A); }
		if (due[Molecule.B.id] > 0) { molecules.add(Molecule.B); }
		if (due[Molecule.C.id] > 0) { molecules.add(Molecule.C); }
		if (due[Molecule.D.id] > 0) { molecules.add(Molecule.D); }
		if (due[Molecule.E.id] > 0) { molecules.add(Molecule.E); }
		return molecules;
	}
	
	public void pay(Molecule molecule) {
		switch(molecule) {
			case A: due[Molecule.A.id] = Math.max(0, due[Molecule.A.id]-1);
			break;
			case B: due[Molecule.B.id] = Math.max(0, due[Molecule.B.id]-1);
			break;
			case C: due[Molecule.C.id] = Math.max(0, due[Molecule.C.id]-1);
			break;
			case D: due[Molecule.D.id] = Math.max(0, due[Molecule.D.id]-1);
			break;
			case E: due[Molecule.E.id] = Math.max(0, due[Molecule.E.id]-1);
			break;
		}
		this.checkIfPaid();
	}
	
	public void checkIfPaid() {
		if (due[Molecule.A.id] == 0 &&
				due[Molecule.B.id] == 0 &&
				due[Molecule.C.id] == 0 &&
				due[Molecule.D.id] == 0 &&
				due[Molecule.E.id] == 0) {
			isPaid = true;
		}
	}
	
	public int getMaxDue() {
		int maxDue = -1;
		maxDue = Math.max(maxDue, due[Molecule.A.id]);
		maxDue = Math.max(maxDue, due[Molecule.B.id]);
		maxDue = Math.max(maxDue, due[Molecule.C.id]);
		maxDue = Math.max(maxDue, due[Molecule.D.id]);
		maxDue = Math.max(maxDue, due[Molecule.E.id]);
		return maxDue;
	}
	
	public int getNbRemainingDue() {
		return due[Molecule.A.id] + 
				due[Molecule.B.id] +
				due[Molecule.C.id] +
				due[Molecule.D.id] +
				due[Molecule.E.id];
	}
	
	public int getNbRemainingDue(Molecule molecule) {
		return due[molecule.id];
	}
	
}

class Sample implements Comparable<Sample> {

	int id;
	int totalCost;
	int health;
	boolean diagnosed;
	int[] cost = new int[Player.NB_MOLECULES];
	Carrier carriedBy;
	Rank rank;
	Molecule expertiseGain;
	Due due;
	
	Sample(int id, Rank rank) {
		this.id = id; 
		this.rank = rank;
	}
	
	public int getNbMolecules() {
		return cost[Molecule.A.id] +
				cost[Molecule.B.id] +
				cost[Molecule.C.id] +
				cost[Molecule.D.id] +
				cost[Molecule.E.id];
	}
	
	public double getRemainingDue() {
		return this.due.getNbRemainingDue();
	}
	
	public double getRemainingDue(Molecule molecule) {
		return this.due.getNbRemainingDue(molecule);
	}
	
	public void setCost(Integer costA, int costB, int costC, int costD, int costE, int[] expertise) {
		this.cost[Molecule.A.id] = costA;
		this.cost[Molecule.B.id] = costB;
		this.cost[Molecule.C.id] = costC;
		this.cost[Molecule.D.id] = costD;
		this.cost[Molecule.E.id] = costE;
		this.totalCost = costA + costB + costC + costD + costE;
		this.due = new Due(this, expertise);
	}
	
	public void setCarriedBy(Carrier carriedBy) {
		this.carriedBy = carriedBy;
	}
	
	public void setDiagnosed(int health, Molecule expertiseGain) {
		this.health = health;
		this.expertiseGain = expertiseGain;
		this.diagnosed = true;
	}
	
	public int getMaxDue() {
		int maxDue = -1;
		if (this.due != null) {
			maxDue = this.due.getMaxDue();
		}
		return maxDue;
	}
	
	@Override
	public int compareTo(Sample o) {
		
		// Priority to diagnosed
		if (this.diagnosed && !o.diagnosed) {
			return -1;
		}
		else if (!this.diagnosed && o.diagnosed) {
			return 1;
		}
		// If both are not diagnosed, priority to total cost
		else if (!this.diagnosed && !o.diagnosed) {
			return Integer.signum(this.totalCost - o.totalCost);
		}
		// If both are diagnosed, priority to rank
		else {
			if (this.rank != o.rank) {
				return Integer.signum(this.rank.rank - o.rank.rank);
			}
			// If both are same rank, priority to max due
			if (Math.max(this.getMaxDue(), 4) != Math.max(o.getMaxDue(), 4)) {
				return Integer.signum(Math.max(this.getMaxDue(), 4) - Math.max(o.getMaxDue(), 4));
			}
			else {
				return Integer.signum(o.health - this.health);
			}
		}
	}
}

enum Rank {
	R0(0), R1(1), R2(2);
	int rank;
	Rank(int rank) {
		this.rank = rank;
	}
	static Rank fromInteger(int rank) {
		switch(rank) {
		case 0:
			return Rank.R0;
		case 1:
			return Rank.R1;
		case 2:
			return Rank.R2;
		}
		return null;
	}
}

enum Module {
	SAMPLES("SAMPLES"), DIAGNOSIS("DIAGNOSIS"), MOLECULES("MOLECULES"), LABORATORY("LABORATORY");
	String text;
	Module(String text) {
		this.text = text;
	}
	
	static Module fromString(String text) {
		if ("DIAGNOSIS".equals(text)) {
			return Module.DIAGNOSIS;
		}
		else if("MOLECULES".equals(text)) {
			return Module.MOLECULES;
		}
		else if("LABORATORY".equals(text)) {
			return Module.LABORATORY;
		}
		else if("SAMPLES".equals(text)) {
			return Module.SAMPLES;
		}
		return null;
	}
}

enum Molecule {
	A("A", 0), B("B", 1), C("C", 2), D("D", 3), E("E", 4);
	String text;
	int id;
	Molecule(String text, int id) {
		this.text = text;
		this.id = id;
	}
	
	static Molecule fromString(String molecule) {
		switch(molecule) {
		case "A":
			return Molecule.A;
		case "B":
			return Molecule.B;
		case "C":
			return Molecule.C;
		case "D":
			return Molecule.D;
		case "E":
			return Molecule.E;
		}
		return null;
	}
	
	static Molecule fromInteger(int molecule) {
		switch(molecule) {
		case 0:
			return Molecule.A;
		case 1:
			return Molecule.B;
		case 2:
			return Molecule.C;
		case 3:
			return Molecule.D;
		case 4:
			return Molecule.E;
		}
		return null;
	}
}

enum Carrier {
	PLAYER(0), OPPONENT(1), CLOUD(-1);
	int carriedBy;
	Carrier(int carriedBy) {
		this.carriedBy = carriedBy;
	}
	
	static Carrier fromInteger(int carriedBy) {
		switch(carriedBy) {
		case 0:
			return Carrier.PLAYER;
		case 1:
			return Carrier.OPPONENT;
		case -1:
			return Carrier.CLOUD;
		}
		return null;
	}
}

