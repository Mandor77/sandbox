package org.ascotte.codingame.ghost;

public class Player {

}

class Game {
	
	Factory[] factories;
	FactoryBuilder builder = new FactoryBuilder();
	
	public Game (int nbFactories) {
		this.factories = new Factory[nbFactories];
	}
	
	public void addFactory(int id, Owner owner, int production, int nbCyborgs) {
		this.factories[id] = builder.createFactory(id, owner, production, nbCyborgs);
	}
	
	public Factory getFactory(int id) {
		return factories[id];
	}
	
	public void play(){
		for (int id = 0; id < factories.length; id++) {
			Factory factory = this.factories[id];
			if (factory.getOwner() != Owner.NOBODY) {
				factory.addProductionToNbCyborgs();
			}
		}
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
}

class FactoryBuilder {
	
	public Factory createFactory(int id, Owner owner, int production, int nbCyborgs) {
		Factory factory = new Factory(id);
		factory.setNbCyborgs(nbCyborgs);
		factory.setOwner(owner);
		factory.setProduction(production);
		return factory;
	}
}

enum Owner {
	OPPONENT(-1), NOBODY(0), PLAYER(1);
	
	int owner;
	Owner(int owner) {
		this.owner = owner;
	}
}