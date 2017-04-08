package org.ascotte.codingame.ghost;

public class Player {

}

class Game {

	final static int PRODUCTION_0 = 0;
	final static int PRODUCTION_1 = 1;
	final static int PRODUCTION_2 = 2;
	final static int PRODUCTION_3 = 3;
	
	Factory[] factories;
	Troop[] troops;
	Builder builder = new Builder();
	
	public Game (int nbFactories) {
		this.factories = new Factory[nbFactories];
	}
	
	public void initNbTroop(int nbTroops) {
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
				if (troop.getOwner().equals(Owner.PLAYER)) {
					factory.addPlayerNbCyborgs(troop.getNbCyborgs());
				}
				else if (troop.getOwner().equals(Owner.OPPONENT)){
					factory.addOpponentNbCyborgs(troop.getNbCyborgs());
				}
			}
		}
		
		// Ownership management
		for (int id = 0; id < factories.length; id++) {
			Factory factory = this.factories[id];
			factory.resolveBattle();
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
	int playerNbCyborgs;
	int opponentNbCyborgs;

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
	
	public void setPositiveNbCyborgs() {
		this.nbCyborgs = Math.abs(this.nbCyborgs);
	}
	
	public int getPlayerNbCyborgs() {
		return this.playerNbCyborgs;
	}
	
	public int getOpponentNbCyborgs() {
		return this.opponentNbCyborgs;
	}
	
	public void addPlayerNbCyborgs(int playerNbCyborg) {
		this.playerNbCyborgs += playerNbCyborg;
	}
	
	public void addOpponentNbCyborgs(int opponentNbCyborgs) {
		this.opponentNbCyborgs += opponentNbCyborgs;
	}
	
	public void solveOwnership(Owner owner) {
		if (this.nbCyborgs < 0) {
			this.owner = owner;
			setPositiveNbCyborgs();
		}
	}
	
	public void resolveBattle() {
		if (this.playerNbCyborgs == this.opponentNbCyborgs) {
			// Do Nothing
		}
		else if (this.playerNbCyborgs > this.opponentNbCyborgs) {
			int finalPlayerNbCyborgs = this.playerNbCyborgs - this.opponentNbCyborgs;
			if (this.owner == Owner.PLAYER) {
				addCyborgs(finalPlayerNbCyborgs);
			}
			else {
				removeCyborgs(finalPlayerNbCyborgs);
				solveOwnership(Owner.PLAYER);
			}
		}
		else if (this.opponentNbCyborgs > this.playerNbCyborgs) {
			int finalOpponentNbCyborgs = this.opponentNbCyborgs - this.playerNbCyborgs;
			if (this.owner == Owner.OPPONENT) {
				addCyborgs(finalOpponentNbCyborgs);
			}
			else {
				removeCyborgs(finalOpponentNbCyborgs);
				solveOwnership(Owner.OPPONENT);
			}
		}
		this.playerNbCyborgs = 0;
		this.opponentNbCyborgs = 0;
		return;
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