package org.ascotte.codingame.ghost;

public class Player {

}

class Game {

	final static int PRODUCTION_0 = 0;
	final static int PRODUCTION_1 = 1;
	final static int PRODUCTION_2 = 2;
	final static int PRODUCTION_3 = 3;
	final static int INCREMENT_CYBORGS_COST = 10;
	final static int MAX_PRODUCTION = 3;
	final static int MAX_TROOPS = 400;
	final static int MAX_ACTIONS = 100;
	final static int MAX_NB_BOMBS_PER_OWNER = 2;
	static int NB_PLAYER_BOMBS = 0;
	static int NB_OPPONENT_BOMBS = 0;
	final static int NB_INACTIVE_TURN_AFTER_BOMB = 5;
	final static int FLOOR_NB_CYBORGS_KILLED_BY_BOMB = 10;
	
	Factory[] factories;
	Troop[] troops;
	Action[] actions;
	Bomb[] bombs;
	int[][] distances;
	
	int nbActions = 0;
	int nbTroops = 0;
	int nbBombs = 0;
	Builder builder = new Builder();
	
	public Game (int nbFactories) {
		this.factories = new Factory[nbFactories];
		this.troops = new Troop[MAX_TROOPS];
		this.actions = new Action[MAX_ACTIONS];
		this.distances = new int[nbFactories][nbFactories];
		this.bombs = new Bomb[MAX_NB_BOMBS_PER_OWNER * 2];
	}
	
	public void initDistance(int fixed) {
		for (int i = 0; i < this.factories.length; i++) {
			for (int j = 0; j < this.factories.length; j++) {
				if (i == j) {
					this.distances[i][j] = 0;
				}
				else {
					this.distances[i][j] = fixed;
				}
			}
		}
	}
	
	public int getDistance(int from, int to) {
		return this.distances[from][to];
	}
	
	public void incrementTroop() {
		this.nbTroops++;
	}
	
	public void incrementAction() {
		this.nbActions++;
	}
	
	public void incrementBomb() {
		this.nbBombs++;
	}

	public void addActionIncrement(int from, Owner owner) {
		this.actions[this.nbActions] = builder.createActionIncrement(owner, from);
		incrementAction();
	}
	
	public void addActionMove(int from, int to, int nbCyborgs, Owner owner) {
		this.actions[this.nbActions] = builder.createActionMove(owner, from, to, nbCyborgs);
		incrementAction();
	}
	
	public void addActionBomb(int from, int to, Owner owner){
		this.actions[this.nbActions] = builder.createActionBomb(owner, from, to);
		incrementAction();
	}
	
	public void addActionWait(Owner owner) {
		this.actions[this.nbActions] = builder.createActionWait(owner);
		incrementAction();
	}
	
	public void addFactory(int id, Owner owner, int production, int nbCyborgs) {
		this.factories[id] = builder.createFactory(id, owner, production, nbCyborgs);
	}
	
	public void addTroop(Owner owner, int from, int to, int nbCyborgs, int remainingTurns) {
		this.troops[this.nbTroops] = builder.createTroop(owner, from, to, nbCyborgs, remainingTurns);
		incrementTroop();
	}
	
	public void addBomb(Owner owner, int from, int to, int remainingTurns) {
		this.bombs[this.nbBombs] = builder.createBomb(owner, from, to, remainingTurns);
		if (owner.equals(Owner.PLAYER)) { NB_PLAYER_BOMBS++; }
		else if (owner.equals(Owner.OPPONENT)) { NB_OPPONENT_BOMBS++; }
		incrementBomb();
	}
	
	public Factory getFactory(int id) {
		return factories[id];
	}
	
	public Troop getTroop(int id) {
		return troops[id];
	}
	
	public Bomb getBombs(int id) {
		return bombs[id];
	}
	
	public int getNbActions() {
		return this.nbActions;
	}
	
	public int getNbTroops() {
		return this.nbTroops;
	}
	
	public int getNbBombs() {
		return this.nbBombs;
	}
	
	private void moveManagement() {
		// Move management
		for (int id = 0; id < this.nbTroops; id++) {
			Troop troop = this.troops[id];
			troop.move();
		}
		
		for (int id = 0; id < this.nbBombs; id++) {
			Bomb bomb = this.bombs[id];
			bomb.move();
		}
	}
	
	private void actionManagement() {
		// Validate action
		for (int id = 0; id < this.nbActions; id++) {
			Action action = this.actions[id];
			Factory fromFactory = this.factories[action.getFrom()];
			Factory toFactory = this.factories[action.getTo()];
			switch (action.action) {
				case MOVE:
					// Not same owner => invalid
					if (!fromFactory.getOwner().equals(action.getOwner())) {
						action.invalidate();
					}
					// Same factory => invalid
					if (fromFactory.getId()==toFactory.getId()) {
						action.invalidate();
					}
					// Valid troop invalid all other troops sent from same factory
					if (action.isValid()) {
						for (int id2 = 0; id2 < this.nbActions; id2++) {
							Action action2 = this.actions[id2];
							if (!action2.equals(action) && action2.action.equals(Actions.MOVE)) {
								if (fromFactory.equals(this.factories[action2.getFrom()]) &&
										toFactory.equals(this.factories[action2.getTo()])) {
									action2.invalidate();
								}
							}
						}
					}
				break;
				case INCREMENT:
					// Not same owner => invalid
					if (!fromFactory.getOwner().equals(action.getOwner())) {
						action.invalidate();
					}
				break;
				case BOMB:
					// Not same owner => invalid
					if (!fromFactory.getOwner().equals(action.getOwner())) {
						action.invalidate();
					}
					// Same factory => invalid
					if (fromFactory.getId()==toFactory.getId()) {
						action.invalidate();
					}
					// Valid bomb invalid all other action, including second bomb ?
					if (action.isValid()) {
						for (int id2 = 0; id2 < this.nbActions; id2++) {
							Action action2 = this.actions[id2];
							if (!action2.equals(action)) {
								if (fromFactory.equals(this.factories[action2.getFrom()]) &&
										toFactory.equals(this.factories[action2.getTo()])) {
									action2.invalidate();
								}
							}
						}
					}
				break;
			}
		}
		
		// Action management
		for (int id = 0; id < this.nbActions; id++) {
			Action action = this.actions[id];
			Factory fromFactory = this.factories[action.getFrom()];
			Factory toFactory = this.factories[action.getTo()];
			// Increment management
			switch (action.action) {
				case INCREMENT:
					if (action.isValid()) {
						if (fromFactory.getNbCyborgs() >= INCREMENT_CYBORGS_COST && fromFactory.getProduction() < MAX_PRODUCTION) {
							fromFactory.removeCyborgs(INCREMENT_CYBORGS_COST);
							fromFactory.increaseProduction();
						}
					}
				break;
				case BOMB:
					if (action.isValid()) {
						if (action.getOwner().equals(Owner.PLAYER) && NB_PLAYER_BOMBS < MAX_NB_BOMBS_PER_OWNER ||
								action.getOwner().equals(Owner.OPPONENT) && NB_OPPONENT_BOMBS < MAX_NB_BOMBS_PER_OWNER) {
							int remainingTurns = getDistance(fromFactory.getId(), toFactory.getId());
							this.addBomb(action.getOwner(), fromFactory.getId(), toFactory.getId(), remainingTurns);
						}
					}
				break;
				case MOVE:
					if (action.isValid()) {
						int nbCyborgsSent = Math.min(fromFactory.getNbCyborgs(), action.getNbCyborgs());
						int remainingTurns = getDistance(fromFactory.getId(), toFactory.getId());
						this.addTroop(action.getOwner(), fromFactory.getId(), toFactory.getId(), nbCyborgsSent, remainingTurns);
						fromFactory.removeCyborgs(nbCyborgsSent);
					}
				break;
				case WAIT:
					// Do Nothing
				break;
				default:
				break;
			}
		}
		this.nbActions = 0;
	}
	
	private void productionManagement() {
		// Production management
		for (int id = 0; id < factories.length; id++) {
			Factory factory = this.factories[id];
			if (factory.getOwner() != Owner.NOBODY && factory.isActive()) {
				factory.addProductionToNbCyborgs();
			}
		}
	}
	
	private void battleManagement() {
		// Battle management
		for (int id = 0; id < this.nbTroops; id++) {
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
	}
	
	private void ownershipManagement() {
		// Ownership management
		for (int id = 0; id < factories.length; id++) {
			Factory factory = this.factories[id];
			factory.resolveBattle();
		}
	}
	
	private void bombManagement() {
		for (int id = 0; id < this.nbBombs; id++) {
			Bomb bomb = this.bombs[id];
			if (bomb.getRemainingTurns() == 0) {
				Factory factory = this.factories[bomb.getTo()];
				factory.inactivate(NB_INACTIVE_TURN_AFTER_BOMB);
				
				int nbCyborgs = factory.getNbCyborgs();
				if (nbCyborgs <= FLOOR_NB_CYBORGS_KILLED_BY_BOMB) {
					factory.removeCyborgs(nbCyborgs);
				}
				else {
					factory.removeCyborgs(Math.max(FLOOR_NB_CYBORGS_KILLED_BY_BOMB, (nbCyborgs / 2)));
				}
			}
		}
	}
	
	private void factoryManagement() {
		for (int id = 0; id < factories.length; id++) {
			Factory factory = this.factories[id];
			factory.restoreInactivation(1);
		}
	}
	
	public void play(){
		
		factoryManagement();
		moveManagement();
		bombManagement();
		actionManagement();
		productionManagement();
		battleManagement();
		ownershipManagement();
	}
}

class Bomb {
	Owner owner;
	int from;
	int to;
	int remainingTurns;
	
	public Bomb() {
		
	}
	
	public void setOwner(Owner owner) {
		this.owner = owner;
	}
	
	public Owner getOwner() {
		return this.owner;
	}
	
	public void setRemainingTurns(int remainingTurns) {
		this.remainingTurns = remainingTurns;
	}
	
	public int getRemainingTurns() {
		return this.remainingTurns;
	}
	
	public void setFrom (int from) {
		this.from = from;
	}
	public void setTo (int to) {
		this.to = to;
	}
	
	public int getFrom() {
		return this.from;
	}
	
	public int getTo() {
		return this.to;
	}
	
	public void move() {
		this.remainingTurns--;
	}
}
class Action {
	Actions action;
	Owner owner;
	int from;
	int to;
	int nbCyborgs;
	boolean isValid;
	
	public Action(Actions action, Owner owner) {
		this.action = action;
		this.owner = owner;
		this.isValid = true;
	}

	public Actions getAction() {
		return this.action;
	}

	public Owner getOwner() {
		return this.owner;
	}
	
	public int getFrom() {
		return this.from;
	}
	
	public int getTo() {
		return this.to;
	}
	
	public int getNbCyborgs() {
		return this.nbCyborgs;
	}
	
	public void setFrom(int from) {
		this.from = from;
	}
	
	public void setTo(int to) {
		this.to = to;
	}
	
	public void setNbCyborgs(int nbCyborgs) {
		this.nbCyborgs = nbCyborgs;
	}
	
	public boolean isValid() {
		return isValid;
	}
	
	public void invalidate() {
		this.isValid = false;
	}
}
	
class Troop {
	Owner owner;
	int from;
	int to;
	int nbCyborgs;
	int remainingTurns;
	
	public Troop() {
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
		this.remainingTurns--;
	}
}

class Factory {
	int id;
	Owner owner;
	int production;
	int nbCyborgs;
	int playerNbCyborgs;
	int opponentNbCyborgs;
	boolean active;
	int inactiveRemainingTurn;

	public Factory(int id) {
		this.id = id;
		this.active = true;
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
	
	public void increaseProduction() {
		this.production += 1;
	}
	
	public void inactivate(int inactiveRemainingTurn) {
		this.active = false;
		this.inactiveRemainingTurn = inactiveRemainingTurn;
	}
	
	public boolean isActive() {
		return this.active;
	}
	
	public void restoreInactivation(int nbTurn) {
		if (this.inactiveRemainingTurn > 0) {
			this.inactiveRemainingTurn--;
		}
		if (this.inactiveRemainingTurn == 0) {
			this.active = true;
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
	
	public Troop createTroop(Owner owner, int from, int to, int nbCyborgs, int remainingTurns) {
		Troop troop = new Troop();
		troop.setOwner(owner);
		troop.setFrom(from);
		troop.setTo(to);
		troop.setNbCyborgs(nbCyborgs);
		troop.setRemainingTurns(remainingTurns);
		return troop;
	}
	
	public Bomb createBomb(Owner owner, int from, int to, int remainingTurns) {
		Bomb bomb = new Bomb();
		bomb.setFrom(from);
		bomb.setTo(to);
		bomb.setOwner(owner);
		bomb.setRemainingTurns(remainingTurns);
		return bomb;
	}
	
	public Action createActionMove(Owner owner, int from, int to, int nbCyborgs) {
		Action action = new Action(Actions.MOVE, owner);
		action.setFrom(from);
		action.setTo(to);
		action.setNbCyborgs(nbCyborgs);
		return action;
	}
	
	public Action createActionIncrement(Owner owner, int from) {
		Action action = new Action(Actions.INCREMENT, owner);
		action.setFrom(from);
		return action;
	}
	
	public Action createActionWait(Owner owner) {
		Action action = new Action(Actions.WAIT, owner);
		return action;
	}
	
	public Action createActionBomb(Owner owner, int from, int to) {
		Action action = new Action(Actions.BOMB, owner);
		action.setFrom(from);
		action.setTo(to);
		return action;
	}
}

enum Owner {
	OPPONENT(-1), NOBODY(0), PLAYER(1);
	
	int owner;
	Owner(int owner) {
		this.owner = owner;
	}
}

enum Actions {
	MOVE, INCREMENT, BOMB, WAIT;
}