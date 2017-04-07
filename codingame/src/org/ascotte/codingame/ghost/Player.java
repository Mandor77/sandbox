package org.ascotte.codingame.ghost;

public class Player {

}

class Game {
	
	Factory[] factories;
	Troop[] troops;
	Builder builder = new Builder();
	
	public Game (int nbFactories, int nbTroops) {
		this.factories = new Factory[nbFactories];
		this.troops = new Troop[nbTroops];
	}
	
	public void addFactory(int id, Owner owner, int production, int nbCyborgs) {
		this.factories[id] = builder.createFactory(id, owner, production, nbCyborgs);
	}
	
	public void addTroop(int id, Owner owner, int from, int to, int nbCyborgs, int remainingTurns) {
		this.troops[id] = builder.createTroop(id, owner, from, to, nbCyborgs, remainingTurns);
	}
	
	public Factory getFactory(int id) {
		return factories[id];
	}
	
	public Troop getTroop(int id) {
		return troops[id];
	}
	
	public void play(){
		
		// Move management
		for (int id = 0; id < troops.length; id++) {
			Troop troop = this.troops[id];
			troop.move();
		}
		
		// Production management
		for (int id = 0; id < factories.length; id++) {
			Factory factory = this.factories[id];
			if (factory.getOwner() != Owner.NOBODY) {
				factory.addProductionToNbCyborgs();
			}
		}
		
		// Battle management
		for (int id = 0; id < troops.length; id++) {
			Troop troop = this.troops[id];
			if (troop.getRemainingTurns() == 0) {
				Factory factory = this.factories[troop.getTo()];
				if (troop.getOwner().equals(factory.getOwner())) {
					factory.addCyborgs(troop.getNbCyborgs());
				}
				else {
					factory.removeCyborgs(troop.getNbCyborgs());
				}
			}
		}

	}
}

class Troop {
	int id;
	Owner owner;
	int from;
	int to;
	int nbCyborgs;
	int remainingTurns;
	
	public Troop(int id) {
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	
	public Owner getOwner() {
		return owner;
	}

	public void setOwner(Owner owner) {
		this.owner = owner;
	}

	public int getFrom() {
		return from;
	}

	public void setFrom(int from) {
		this.from = from;
	}

	public int getTo() {
		return to;
	}

	public void setTo(int to) {
		this.to = to;
	}

	public int getNbCyborgs() {
		return nbCyborgs;
	}

	public void setNbCyborgs(int nbCyborgs) {
		this.nbCyborgs = nbCyborgs;
	}

	public int getRemainingTurns() {
		return remainingTurns;
	}

	public void setRemainingTurns(int remainingTurns) {
		this.remainingTurns = remainingTurns;
	}
	
	public void move() {
		this.remainingTurns -= 1;
	}
}

class Factory {
	int id;
	Owner owner;
	int production;
	int nbCyborgs;

	public Factory(int id) {
		this.id = id;
	}

	public int getId() {
		return this.id;
	}
	
	public int getProduction() {
		return this.production;
	}
	
	public void setProduction(int production) {
		this.production = production;
	}
	
	public int getNbCyborgs() {
		return this.nbCyborgs;
	}
	
	public void setNbCyborgs(int nbCyborgs) {
		this.nbCyborgs = nbCyborgs;
	}
	
	public Owner getOwner() {
		return this.owner;
	}
	
	public void setOwner(Owner owner) {
		this.owner = owner;
	}
	
	public void addProductionToNbCyborgs() {
		this.nbCyborgs += this.production;
	}
	
	public void addCyborgs(int nbCyborgs) {
		this.nbCyborgs += nbCyborgs;
	}
	
	public void removeCyborgs(int nbCyborgs) {
		this.nbCyborgs -= nbCyborgs;
	}
}

class Builder {
	
	public Factory createFactory(int id, Owner owner, int production, int nbCyborgs) {
		Factory factory = new Factory(id);
		factory.setNbCyborgs(nbCyborgs);
		factory.setOwner(owner);
		factory.setProduction(production);
		return factory;
	}
	
	public Troop createTroop(int id, Owner owner, int from, int to, int nbCyborgs, int remainingTurns) {
		Troop troop = new Troop(id);
		troop.setOwner(owner);
		troop.setFrom(from);
		troop.setTo(to);
		troop.setNbCyborgs(nbCyborgs);
		troop.setRemainingTurns(remainingTurns);
		return troop;
	}
}

enum Owner {
	OPPONENT(-1), NOBODY(0), PLAYER(1);
	
	int owner;
	Owner(int owner) {
		this.owner = owner;
	}
}