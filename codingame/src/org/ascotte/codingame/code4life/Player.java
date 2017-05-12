package org.ascotte.codingame.code4life;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Scanner;

/**
 * Bring data on patient samples from the diagnosis machine to the laboratory with enough molecules to produce medicine!
 **/
class Player {

	static HashMap<Integer, Sample> samples = new HashMap<Integer, Sample>(); 
	static PriorityQueue<Sample> samplesQueue = new PriorityQueue<Sample>();
	static int NB_MOLECULES = 5;
	static Robot robot = new Robot();
	
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
                	robot.setModule(Module.fromString(target));
                }
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
                
                // Add sample to map if not exists
                if (!samples.containsKey(sampleId)) {
                	Sample sample = new Sample(sampleId, costA+costB+costC+costD+costE, Carrier.fromInteger(carriedBy));
                	sample.setCost(costA, costB, costC, costD, costE);
                	samples.put(sampleId, sample);
                	samplesQueue.add(sample);
                }
                // Update carrier
                else {
                	samples.get(sampleId).setCarriedBy(Carrier.fromInteger(carriedBy));
                }
            }

            play();
        }
    }
    
    static void play() {
    	playClassic();
    }
    
    // Select best samples
    // Select adequate molecules
    // Valid samples
    static void playClassic() {
    
    	if (robot.getModule() == null) {
    		goTo(Module.DIAGNOSIS);
    	}
    	
    	// Get best samples
    	else if (Module.DIAGNOSIS.equals(robot.getModule())) {
    		if(robot.getFreeSample() != 0) {
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
    				robot.addSample(sample);
    				connectTo(sample.id);
    				return;
    			}
    		}
    		goTo(Module.MOLECULES);
    	}
    	
    	// Fill sample one by one
    	else if(Module.MOLECULES.equals(robot.getModule())) {
    		if(robot.getFreeMolecule() != 0) {
    			Sample sample = null;
    			sample = robot.getSampleToPay();
    			if (sample != null) {
    				Molecule molecule = robot.getMoleculeToPay(sample);
    				if (molecule != null) {
    					robot.payMolecule(sample, molecule);
        				connectTo(molecule);
        				return;
    				}
    			}
    		}
    		goTo(Module.LABORATORY);
    	}
    	
    	
    	else if(Module.LABORATORY.equals(robot.getModule())) {
    		Sample sample = robot.getNextPaidSample();
    		if (sample != null) {
    			robot.paySample(sample);
    			connectTo(sample.id);
    			return;
    		}
    		goTo(Module.DIAGNOSIS);
    	}
    }
    
    static void goTo(Module module) {
    	System.out.println("GOTO " + module.text);
    }
    
    static void connectTo(int id) {
    	System.out.println("CONNECT " + id);
    }
    
    static void connectTo(Molecule molecule) {
    	System.out.println("CONNECT " + molecule.text);
    }
}

class Robot {
	
	static int MAX_SAMPLES = 3;
	static int MAX_MOLECULES = 10;
	
	Module module = null;
	int nbMolecule = 0;
	ArrayList<Sample> samples = new ArrayList<Sample>();  
	HashMap<Sample, Due> dues = new HashMap<Sample, Due>();
	
	Robot() {
	}
	
	void paySample(Sample sample) {
		nbMolecule -= sample.getNbMolecules();
		samples.remove(sample);
		dues.remove(sample);
	}
	
	Sample getNextPaidSample() {
		for (Sample sample:samples) {
			if (dues.get(sample).isPaid) {
				return sample;
			}
		}
		return null;
	}
	
	Sample getSampleToPay() {
		for (Sample sample:samples) {
			if (!dues.get(sample).isPaid) {
				return sample;
			}
		}
		return null;
	}
	
	Molecule getMoleculeToPay(Sample sample) {
		return dues.get(sample).getNextDue();
	}
	
	void payMolecule(Sample sample, Molecule molecule) {
		dues.get(sample).pay(molecule);
		this.nbMolecule++;
	}
	
	int getFreeSample() {
		return this.samples.size() - MAX_SAMPLES;
	}
	
	int getFreeMolecule() {
		return this.nbMolecule - MAX_MOLECULES;
	}
	
	// Return false if max samples is reached
	boolean addSample(Sample sample) {
		boolean status = false;
		if (!(samples.size() > MAX_SAMPLES)) {
			samples.add(sample);
			dues.put(sample, new Due(sample));
		}
		return status;
	}
	
	void setModule(Module module) {
		this.module = module;
	}
	
	Module getModule() {
		return this.module;
	}
}

class Due {

	int[] due = new int[Player.NB_MOLECULES];
	boolean isPaid = false;
	
	Due(Sample sample) {
		this.due[Molecule.A.id] = sample.cost[Molecule.A.id];
		this.due[Molecule.B.id] = sample.cost[Molecule.B.id];
		this.due[Molecule.C.id] = sample.cost[Molecule.C.id];
		this.due[Molecule.D.id] = sample.cost[Molecule.D.id];
		this.due[Molecule.E.id] = sample.cost[Molecule.E.id];
	}
	
	public Molecule getNextDue() {
		if (due[Molecule.A.id] > 0) { return Molecule.A; }
		if (due[Molecule.B.id] > 0) { return Molecule.B; }
		if (due[Molecule.C.id] > 0) { return Molecule.C; }
		if (due[Molecule.D.id] > 0) { return Molecule.D; }
		if (due[Molecule.E.id] > 0) { return Molecule.E; }
		return null;
	}
	
	public void pay(Molecule molecule) {
		switch(molecule) {
			case A: due[Molecule.A.id]--;
			break;
			case B: due[Molecule.B.id]--;
			break;
			case C: due[Molecule.C.id]--;
			break;
			case D: due[Molecule.D.id]--;
			break;
			case E: due[Molecule.E.id]--;
			break;
		}
		if (due[Molecule.A.id] == 0 &&
				due[Molecule.B.id] == 0 &&
				due[Molecule.C.id] == 0 &&
				due[Molecule.D.id] == 0 &&
				due[Molecule.E.id] == 0) {
			isPaid = true;
		}
	}
	
}

class Sample implements Comparable<Sample> {

	int id;
	int nbMolecule;
	int[] cost = new int[Player.NB_MOLECULES];
	Carrier carriedBy;
	
	Sample(int id, int nbMolecule, Carrier carriedBy) {
		this.id = id; 
		this.nbMolecule = nbMolecule;
		this.carriedBy = carriedBy;
	}
	
	public int getNbMolecules() {
		return cost[Molecule.A.id] +
				cost[Molecule.B.id] +
				cost[Molecule.C.id] +
				cost[Molecule.D.id] +
				cost[Molecule.E.id];
	}
	public void setCost(Integer costA, int costB, int costC, int costD, int costE) {
		this.cost[Molecule.A.id] = costA;
		this.cost[Molecule.B.id] = costB;
		this.cost[Molecule.C.id] = costC;
		this.cost[Molecule.D.id] = costD;
		this.cost[Molecule.E.id] = costE;
	}
	
	public void setCarriedBy(Carrier carriedBy) {
		this.carriedBy = carriedBy;
	}
	
	@Override
	public int compareTo(Sample o) {
		int value = 0;
		if (this.nbMolecule >= o.nbMolecule) {
			value = 1;
		}
		else {
			value = -1;
		}
		return value;
	}
	
}

enum Module {
	DIAGNOSIS("DIAGNOSIS"), MOLECULES("MOLECULES"), LABORATORY("LABORATORY");
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

