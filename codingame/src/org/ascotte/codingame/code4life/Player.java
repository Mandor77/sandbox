package org.ascotte.codingame.code4life;

import java.util.ArrayList;
import java.util.Collections;
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
	static int NB_OPPONENT_SAMPLE_OF_LOWER_RANK = 0;
	static int NB_PLAYER_SAMPLE_OF_LOWER_RANK = 0;
	static int MAX_DIFFERENCE_OF_LOWER_RANK = 1;
	
	static int NB_TURN = 10;
	
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
                // Clean opponent
                opponent.samples.clear();
                
                // Add sample to map if not exists
                if (!samples.containsKey(sampleId)) {
                	sample = new Sample(sampleId, Rank.fromInteger(rank-1));
                	samples.put(sampleId, sample);
                	samplesQueue.add(sample);
                    // Update carrier
                    sample.setCarriedBy(Carrier.fromInteger(carriedBy));
                	if (Carrier.PLAYER.equals(sample.carriedBy)) {
                		player.addSample(sample);
                		if (Rank.R0.equals(sample.rank)) {
                			NB_PLAYER_SAMPLE_OF_LOWER_RANK++;
                		}
                	}
                	else if (Carrier.OPPONENT.equals(sample.carriedBy)) {
                		if (Rank.R0.equals(sample.rank)) {
                			NB_OPPONENT_SAMPLE_OF_LOWER_RANK++;
                		}
                	}
                }
                else {
                	sample = samples.get(sampleId);
                	sample.setCarriedBy(Carrier.fromInteger(carriedBy));
                }

                // Update opponent sample
                if (Carrier.OPPONENT.equals(sample.carriedBy)) {
                	opponent.addSample(sample);
                }
                
                // Check if sample was diagnosed
                if (!sample.diagnosed && !NO_DIAGNOSED_EXPERTISE_VALUE.equals(expertiseGain)) {
                	sample.setDiagnosed(health, Molecule.fromString(expertiseGain));
                	sample.setCost(costA, costB, costC, costD, costE);
                	sample.createDue(player.expertise);
                	sample.createOpponentDue(opponent.expertise);
                }
            }
            
            player.sortSamples();
            player.reassignMolecule();
            player.sortSamples();
            opponent.sortSamples();
            opponent.reassignMolecule();
            opponent.sortSamples();
            play();
            NB_TURN++;
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
    		if (player.getExpertiseLevel() < 4  && (NB_PLAYER_SAMPLE_OF_LOWER_RANK - NB_OPPONENT_SAMPLE_OF_LOWER_RANK) < MAX_DIFFERENCE_OF_LOWER_RANK) {
    			connectTo(Rank.R0.rank + 1);
    		}
    		else if (player.getExpertiseLevel() < 5) {
    			connectTo(Rank.R1.rank + 1);
    		}
    		else if (player.getExpertiseLevel() < 7) {
    			if (player.getNbSamplesByRank(Rank.R2) == 0) {
    				connectTo(Rank.R2.rank + 1);
    			}
    			else {
    				connectTo(Rank.R1.rank + 1);
    			}
    		}
    		else if (player.getExpertiseLevel() < 9) {
    			if (player.getNbSamplesByRank(Rank.R2) <= 1) {
    				connectTo(Rank.R2.rank + 1);
    			}
    			else {
    				connectTo(Rank.R1.rank + 1);
    			}
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
    	if (playFromDiagnosisR3()) { return; }	// Remove one big unaffordable
    	if (playFromDiagnosisR5()) { return; }  // Remove if not enough money
    	if (playFromDiagnosisR4()) { return; }	// Remove the too big one
    	if (playFromDiagnosisR2()) { return; }	// Go to samples or to molecules
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
    	if (player.getFreeSample() == 0) {
    		for (Sample sample:player.samples) {
    			if(!sample.due.checkIfCanBePaid(Player.availables) && sample.due.checkMissingAvailable(Player.availables) > 2) {
    				connectTo(sample.id);
    				player.remove(sample);
    				return true;
    			}
    		}
    	}
    	return false;
    }
    
    static boolean playFromDiagnosisR4() {
    	for (Sample sample:player.samples) {
    		if(sample.due.getTotalRemaining() > Robot.MAX_MOLECULES - 2) {
    			connectTo(sample.id);
    			player.remove(sample);
    			return true;
    		}
    	}
    	return false;
    }
    
    static boolean playFromDiagnosisR5() {
    	for (Sample sample:player.samples) {
    		if(sample.due.getTotalRemaining() > player.getFreeMolecule()) {
    			connectTo(sample.id);
    			player.remove(sample);
    			return true;
    		}
    	}
    	return false;
    }
    
    static boolean playFromDiagnosisR2() {
    	if (player.getFreeSample() > 2) {
    		goTo(Module.SAMPLES);
    		return true;
    	}
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
    	if (playFromMoleculesR4()) { return; }  // Chip molecules for blocking opponent
    	if (playFromMoleculesR1()) { return; }	// Get adequate molecule (todo : check quantity) for two first samples
    	if (playFromMoleculesR3()) { return; }	// If all samples not paid, go waste one
    	if (playFromMoleculesR2()) { return; }	// Go to laboratory
    }
    
    static boolean playFromMoleculesR1() {
    	if(player.getFreeMolecule() != 0) {
			LinkedList<Molecule> molecules;
			for (int i = 0; i < Math.min(3, player.samples.size()); i++) {
				Sample sample = player.samples.get(i);
				//if (!sample.due.isPaid && sample.due.checkIfCanBePaid(Player.availables)) {
				if (!sample.due.isPaid) {
					molecules = sample.due.getRemainingAsList();
					for (Molecule molecule:molecules) {
						if (availables[molecule.id] > 0) {
							sample.due.pay(molecule);
							connectTo(molecule);
							return true;
						}
					}
				}
    		}
		}
    	return false;
    }  
    
    static boolean playFromMoleculesR2() {
    	goTo(Module.LABORATORY);
    	return true;
    }
    
    static boolean playFromMoleculesR4() {
    	if(player.getFreeMolecule() != 0) {
    		for (Sample sample:opponent.samples) {
    			if (sample.opponentDue != null) {
    				Molecule molecule = sample.opponentDue.checkCriticMolecule(Player.availables);
    				if (molecule != null) {
    					System.err.println("Vol molecule " + molecule.text);
    					connectTo(molecule);
						return true;
    				}
    			}
    		}
    	}
    	return false;
    }
    
    static boolean playFromMoleculesR3() {
    	for (Sample sample:player.samples) {
			if (sample.due.isPaid) {
				return false;
			}
    	}
    	if (player.getFreeSample() == 0) {
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
    	Sample sample = player.getPaidSample();
		if (sample != null) {
			player.remove(sample);
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
    		for (Sample sample:player.samples) {
    			if (sample.due.checkIfCanBePaid(Player.availables)) {
    				goTo(Module.MOLECULES);
    				return true;
    			}
    		}
    	}
    	return false;
    }

    static void playClassic() {
    
    	if (player.module == null) {
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
	int totalStorage = 0;

	int[] expertise = new int[Player.NB_MOLECULES];
	int[] storage = new int[Player.NB_MOLECULES];
	int[] available = new int[Player.NB_MOLECULES];
	
	ArrayList<Sample> samples = new ArrayList<Sample>();
	
	Robot(Carrier carrier) {
		this.carrier = carrier;
	}
	
	void sortSamples() {
		Collections.sort(samples);
	}
	
	void setModule(Module module, int distanceToModule) {
		this.module = module;
		this.distanceToModule = distanceToModule;
	}
	
	boolean isAtModule(Module module) {
		boolean value = false;
		if (this.module.equals(module) && this.distanceToModule == 0) { value = true; }
		return value;
	}
	
	int getNbSamplesByRank(Rank rank) {
		int nbSamplesByRank = 0;
		for (Sample sample:samples) {
			if (sample.rank.equals(rank)) {
				nbSamplesByRank++;
			}
		}
		return nbSamplesByRank;
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
		int expertiseLevel = 0;
		for (int numMolecule = 0; numMolecule < Player.NB_MOLECULES; numMolecule++) {
			expertiseLevel += this.expertise[numMolecule];
		}
		return expertiseLevel;
	}
	
	public void setScore(int score) {
		this.score = score;
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
		this.totalStorage = storageA + storageB + storageC + storageD + storageE;
		for (int numMolecule = 0; numMolecule < Player.NB_MOLECULES; numMolecule++) {
			this.available[numMolecule] = this.storage[numMolecule];
		}
	}
	
	int getFreeSample() {
		return MAX_SAMPLES - this.samples.size();
	}
	
	int getFreeMolecule() {
		return MAX_MOLECULES - this.totalStorage;
	}
	
	boolean addSample(Sample sample) {
		boolean status = false;
		if (!(samples.size() > MAX_SAMPLES)) {
			samples.add(sample);
		}
		return status;
	}
	
	void remove(Sample sample) {
		if (samples.contains(sample)) {
			samples.remove(sample);
		}
	}
	
	/**
	 * Start of intelligence
	 */
	public void reassignMolecule() {
				
		for (Sample sample:samples) {
			if (sample.diagnosed) {
				sample.createDue(expertise);
			}
		}
		// Reinitialize cost
		for (Sample sample:samples) {
			if (sample.diagnosed) {
				if(sample.due.checkIfCanBePaid(available)) {
					System.err.println("Can be paid " + sample.id);
					for(Molecule molecule:sample.due.getRemainingAsList()) {
						sample.due.pay(molecule);
						this.available[molecule.id]--;
					}
				}
				if (sample.isWinner()) { break; }
			}
		}
		
		for (int numMolecule = 0; numMolecule < Player.NB_MOLECULES; numMolecule++) {
			int nbMolecule = available[numMolecule];
			for (int j = 0; j < nbMolecule; j++) {
				for (Sample sample:samples) {
					if (sample.diagnosed) {
						Molecule molecule = Molecule.fromInteger(numMolecule);
						if (sample.due.getRemaining(molecule) > 0) {
							sample.due.pay(molecule);
							this.available[molecule.id]--;
							break;
						}
					}
				}
			}
		}
	}
		
	Sample getPaidSample() {
		for (Sample sample:samples) {
			if (sample.due.isPaid) {
				return sample;
			}
		}
		return null;
	}
}

class Due {

	int[] due = new int[Player.NB_MOLECULES];
	boolean isPaid = false;
	
	Due(int[] cost, int[] expertise) {
		for (int numMolecule = 0; numMolecule < Player.NB_MOLECULES; numMolecule++){
			this.due[numMolecule] = Math.max(0, cost[numMolecule] - expertise[numMolecule]);
		}
		this.checkIfPaid();
	}
	
	public LinkedList<Molecule> getRemainingAsList() {
		LinkedList<Molecule> molecules = new LinkedList<Molecule>();
		for (int numMolecule = 0; numMolecule < Player.NB_MOLECULES; numMolecule++){
			for (int nbMolecule = 0; nbMolecule < due[numMolecule]; nbMolecule++) { 
				molecules.add(Molecule.fromInteger(numMolecule)); 
			}
		}
		
		return molecules;
	}
	
	public void pay(Molecule molecule) {
		due[molecule.id] = Math.max(0, due[molecule.id]-1);
		this.checkIfPaid();
	}
	
	public void checkIfPaid() {
		for (int numMolecule = 0; numMolecule < Player.NB_MOLECULES; numMolecule++){
			if (due[numMolecule] != 0) {
				return;
			}
		}
		isPaid = true;
	}
	
	public boolean checkIfCanBePaid(int[] available) {
		for (int numMolecule = 0; numMolecule < Player.NB_MOLECULES; numMolecule++){
			if(available[numMolecule] < due[numMolecule]) {
				return false;
			}
		}
		return true;
	}
	
	public int checkMissingAvailable(int[] available) {
		int missingAvailable = 0;
		for (int numMolecule = 0; numMolecule < Player.NB_MOLECULES; numMolecule++){
			if(available[numMolecule] < due[numMolecule]) {
				missingAvailable += (due[numMolecule] - available[numMolecule]);
			}
		}
		return missingAvailable;
	}
	
	public Molecule checkCriticMolecule(int[] available) {
		for (int numMolecule = 0; numMolecule < Player.NB_MOLECULES; numMolecule++){
			if(due[numMolecule] > 0 && available[numMolecule] == due[numMolecule]) {
				return Molecule.fromInteger(numMolecule);
			}
		}
		return null;
	}
	
	public int getMaxRemaining() {
		int maxDue = -1;
		for (int numMolecule = 0; numMolecule < Player.NB_MOLECULES; numMolecule++){
			maxDue = Math.max(maxDue, due[numMolecule]);
		}
		return maxDue;
	}
	
	public int getTotalRemaining() {
		int totalDue = 0;
		for (int numMolecule = 0; numMolecule < Player.NB_MOLECULES; numMolecule++){
			totalDue += due[numMolecule];
		}
		return totalDue;
	}

	public int getRemaining (Molecule molecule) {
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
	Due opponentDue;
	
	Sample(int id, Rank rank) {
		this.id = id; 
		this.rank = rank;
	}
	
	public void setCost(Integer costA, int costB, int costC, int costD, int costE) {
		this.cost[Molecule.A.id] = costA;
		this.cost[Molecule.B.id] = costB;
		this.cost[Molecule.C.id] = costC;
		this.cost[Molecule.D.id] = costD;
		this.cost[Molecule.E.id] = costE;
		this.totalCost = costA + costB + costC + costD + costE;
	}
	
	public void createDue(int[] expertise) {
		this.due = new Due(this.cost, expertise);
	}
	
	public void createOpponentDue(int[] expertise) {
		this.opponentDue = new Due(this.cost, expertise);
	}
	
	public void setCarriedBy(Carrier carriedBy) {
		this.carriedBy = carriedBy;
	}
	
	public void setDiagnosed(int health, Molecule expertiseGain) {
		this.health = health;
		this.expertiseGain = expertiseGain;
		this.diagnosed = true;
	}
	
	public boolean isWinner() {
		if (Player.player.score + this.health > Player.player.MAX_SCORE) {
			return true;
		}
		return false;
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
		// If both are diagnosed, priority to victory, then rank, then max due
		else {
			if (this.isWinner()) {
				return -1;
			}
			if (o.isWinner()) {
				return 1;
			}
			if (this.rank != o.rank) {
				return Integer.signum(this.rank.rank - o.rank.rank);
			}
			// If both are same rank, priority to max due
			if (this.due != null && o.due != null && Math.max(this.due.getMaxRemaining(), 4) != Math.max(o.due.getMaxRemaining(), 4)) {
				return Integer.signum(Math.max(this.due.getMaxRemaining(), 4) - Math.max(o.due.getMaxRemaining(), 4));
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

