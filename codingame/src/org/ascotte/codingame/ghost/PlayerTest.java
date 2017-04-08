package org.ascotte.codingame.ghost;

import org.junit.Test;
import org.junit.Assert;

public class PlayerTest {

	final static int FACTORY_PLAYER_NO_PRODUCTION = 0;
	final static int FACTORY_PLAYER_PRODUCTION_2 = 1;
	final static int FACTORY_PLAYER_PRODUCTION_3 = 2;
	final static int FACTORY_NEUTRAL_PRODUCTION_2 = 3;
	final static int FACTORY_OPPONENT_NO_PRODUCTION = 4;
	final static int FACTORY_OPPONENT_PRODUCTION_2 = 5;
	final static int FACTORY_OPPONENT_PRODUCTION_3 = 6;
	final static int NB_FACTORY = 7;
	final static int NB_CYBORGS_2 = 2;
	final static int NB_CYBORGS_5 = 5;
	final static int NB_CYBORGS_12 = 12;
	final static int FIXED_DISTANCE = 2;
	
	public Game createStandardGame() {
		Game game = new Game(NB_FACTORY);
		game.addFactory(FACTORY_PLAYER_NO_PRODUCTION, Owner.PLAYER, Game.PRODUCTION_0, NB_CYBORGS_2);
		game.addFactory(FACTORY_PLAYER_PRODUCTION_2, Owner.PLAYER, Game.PRODUCTION_2, NB_CYBORGS_2);
		game.addFactory(FACTORY_PLAYER_PRODUCTION_3, Owner.PLAYER, Game.PRODUCTION_3, NB_CYBORGS_5);
		game.addFactory(FACTORY_NEUTRAL_PRODUCTION_2, Owner.NOBODY, Game.PRODUCTION_2, NB_CYBORGS_5);
		game.addFactory(FACTORY_OPPONENT_NO_PRODUCTION, Owner.OPPONENT, Game.PRODUCTION_0, NB_CYBORGS_2);
		game.addFactory(FACTORY_OPPONENT_PRODUCTION_2, Owner.OPPONENT, Game.PRODUCTION_2, NB_CYBORGS_2);
		game.addFactory(FACTORY_OPPONENT_PRODUCTION_3, Owner.OPPONENT, Game.PRODUCTION_3, NB_CYBORGS_5);
		game.initDistance(FIXED_DISTANCE);
		return game;
	}
	
	@Test
	public void productionIsDoneForOwnedFactoryWithProduction() {
		Game game = createStandardGame();
		
		game.play();
		
		Assert.assertEquals("Production is not done for owned factory", 4, game.getFactory(FACTORY_PLAYER_PRODUCTION_2).getNbCyborgs());
		Assert.assertEquals("Production is not done for owned factory", 8, game.getFactory(FACTORY_PLAYER_PRODUCTION_3).getNbCyborgs());
		Assert.assertEquals("Production is not done for owned factory", 4, game.getFactory(FACTORY_OPPONENT_PRODUCTION_2).getNbCyborgs());
		Assert.assertEquals("Production is not done for owned factory", 8, game.getFactory(FACTORY_OPPONENT_PRODUCTION_3).getNbCyborgs());
	}
	
	@Test
	public void productionIsNotDoneForOwnedFactoryWithoutProduction() {
		Game game = createStandardGame();
		
		game.play();
		
		Assert.assertEquals("Production is done for owned factory without production", 2, game.getFactory(FACTORY_PLAYER_NO_PRODUCTION).getNbCyborgs());
		Assert.assertEquals("Production is done for owned factory without production", 2, game.getFactory(FACTORY_OPPONENT_NO_PRODUCTION).getNbCyborgs());
	}
	
	@Test
	public void productionIsNotDoneForNotOwnedFactory() {
		Game game = createStandardGame();
		Factory factory = game.getFactory(FACTORY_NEUTRAL_PRODUCTION_2);
		int initialNbCyborgs = factory.getNbCyborgs();
		
		game.play();
		
		Assert.assertEquals("Production is done for not owned factory", initialNbCyborgs, factory.getNbCyborgs());
	}
	
	@Test
	public void troopMoveOfOneByTurn() {
		Game game = createStandardGame();
		game.addTroop(Owner.PLAYER, FACTORY_PLAYER_NO_PRODUCTION, FACTORY_OPPONENT_NO_PRODUCTION, 1, 5);
		
		game.play();
		
		Assert.assertEquals("Troop don't move in a turn", 4, game.getTroop(0).getRemainingTurns());
	}
	
	@Test
	public void troopAttackAnEnnemyFactory() {
		Game game = createStandardGame();
		game.addTroop(Owner.PLAYER, FACTORY_PLAYER_NO_PRODUCTION, FACTORY_OPPONENT_NO_PRODUCTION, 1, 1);
		Factory factory = game.getFactory(FACTORY_OPPONENT_NO_PRODUCTION);
		int initialNbCyborgs = factory.getNbCyborgs();
		
		game.play();
		
		Assert.assertEquals("Troop does not attack when arrives", initialNbCyborgs - 1, factory.getNbCyborgs());
	}
	
	@Test
	public void troopReinforceAmicalFactory() {
		Game game = createStandardGame();
		game.addTroop(Owner.OPPONENT, FACTORY_PLAYER_NO_PRODUCTION, FACTORY_OPPONENT_NO_PRODUCTION, 1, 1);
		Factory factory = game.getFactory(FACTORY_OPPONENT_NO_PRODUCTION);
		int initialNbCyborgs = factory.getNbCyborgs();
		
		game.play();
		
		Assert.assertEquals("Troop does not reinforce when arrives", initialNbCyborgs + 1, factory.getNbCyborgs());
	}
	
	@Test
	public void troopWinAnEnnemyFactory() {
		Game game = createStandardGame();
		game.addTroop(Owner.PLAYER, FACTORY_PLAYER_NO_PRODUCTION, FACTORY_OPPONENT_NO_PRODUCTION, 5, 1);
		Factory factory = game.getFactory(FACTORY_OPPONENT_NO_PRODUCTION);
		int initialNbCyborgs = factory.getNbCyborgs();
		
		game.play();
		
		Assert.assertEquals("Final cyborgs number is not correct", Math.abs(initialNbCyborgs - 5), factory.getNbCyborgs());
		Assert.assertEquals("Ownership is not correctly changed", Owner.PLAYER, factory.getOwner());
	}
	
	@Test
	public void troopWinANotOwnedFactory() {
		Game game = createStandardGame();
		game.addTroop(Owner.PLAYER, FACTORY_PLAYER_NO_PRODUCTION, FACTORY_NEUTRAL_PRODUCTION_2, 6, 1);
		Factory factory = game.getFactory(FACTORY_NEUTRAL_PRODUCTION_2);
		int initialNbCyborgs = factory.getNbCyborgs();
		
		game.play();
		
		Assert.assertEquals("Final cyborgs number is not correct", Math.abs(initialNbCyborgs - 6), factory.getNbCyborgs());
		Assert.assertEquals("Ownership is not correctly changed", Owner.PLAYER, factory.getOwner());
	}
	
	
	@Test
	public void troopAttackInPriorityOtherTroops() {
		Game game = createStandardGame();
		game.addTroop(Owner.PLAYER, FACTORY_PLAYER_NO_PRODUCTION, FACTORY_OPPONENT_NO_PRODUCTION, 1, 1);
		game.addTroop(Owner.OPPONENT, FACTORY_OPPONENT_PRODUCTION_2, FACTORY_OPPONENT_NO_PRODUCTION, 1, 1);
		Factory factory = game.getFactory(FACTORY_OPPONENT_NO_PRODUCTION);
		int initialNbCyborgs = factory.getNbCyborgs();
		
		game.play();
		
		Assert.assertEquals("Conflicts are not resolved before impacting factory", initialNbCyborgs, factory.getNbCyborgs());
	}
	
	
	
	@Test
	public void troopAttackBothEnnemyTroopsAndEnnemyFactoryWithoutConquest() {
		Game game = createStandardGame();
		game.addTroop(Owner.OPPONENT, FACTORY_OPPONENT_PRODUCTION_2, FACTORY_OPPONENT_NO_PRODUCTION, 5, 1);
		game.addTroop(Owner.OPPONENT, FACTORY_OPPONENT_PRODUCTION_3, FACTORY_OPPONENT_NO_PRODUCTION, 1, 1);
		game.addTroop(Owner.PLAYER, FACTORY_PLAYER_NO_PRODUCTION, FACTORY_OPPONENT_NO_PRODUCTION, 4, 1);
		game.addTroop(Owner.PLAYER, FACTORY_PLAYER_PRODUCTION_2, FACTORY_OPPONENT_NO_PRODUCTION, 3, 1);
		Factory factory = game.getFactory(FACTORY_OPPONENT_NO_PRODUCTION);
		int initialNbCyborgs = factory.getNbCyborgs();
		
		game.play();
		
		Assert.assertEquals("Final cyborgs number is not correct", initialNbCyborgs - 1, factory.getNbCyborgs());
		Assert.assertEquals("Ownership is not correctly changed", Owner.OPPONENT, factory.getOwner());
	}
	
	@Test
	public void troopAttackBothEnnemyTroopsAndEnnemyFactoryWithConquest() {
		Game game = createStandardGame();
		game.addTroop(Owner.OPPONENT, FACTORY_OPPONENT_PRODUCTION_2, FACTORY_OPPONENT_NO_PRODUCTION, 5, 1);
		game.addTroop(Owner.OPPONENT, FACTORY_OPPONENT_PRODUCTION_3, FACTORY_OPPONENT_NO_PRODUCTION, 1, 1);
		game.addTroop(Owner.PLAYER, FACTORY_PLAYER_NO_PRODUCTION, FACTORY_OPPONENT_NO_PRODUCTION, 8, 1);
		game.addTroop(Owner.PLAYER, FACTORY_PLAYER_PRODUCTION_2, FACTORY_OPPONENT_NO_PRODUCTION, 3, 1);
		Factory factory = game.getFactory(FACTORY_OPPONENT_NO_PRODUCTION);
		int initialNbCyborgs = factory.getNbCyborgs();
		
		game.play();
		
		Assert.assertEquals("Final cyborgs number is not correct", Math.abs(initialNbCyborgs - 5), factory.getNbCyborgs());
		Assert.assertEquals("Ownership is not correctly changed", Owner.PLAYER, factory.getOwner());
	}
	
	@Test
	public void troopAttackBothEnnemyTroopsAndNotOwnedFactoryWithConquest() {
		Game game = createStandardGame();
		game.addTroop(Owner.OPPONENT, FACTORY_OPPONENT_PRODUCTION_2, FACTORY_NEUTRAL_PRODUCTION_2, 5, 1);
		game.addTroop(Owner.OPPONENT, FACTORY_OPPONENT_PRODUCTION_3, FACTORY_NEUTRAL_PRODUCTION_2, 1, 1);
		game.addTroop(Owner.PLAYER, FACTORY_PLAYER_NO_PRODUCTION, FACTORY_NEUTRAL_PRODUCTION_2, 12, 1);
		game.addTroop(Owner.PLAYER, FACTORY_PLAYER_PRODUCTION_2, FACTORY_NEUTRAL_PRODUCTION_2, 3, 1);
		Factory factory = game.getFactory(FACTORY_NEUTRAL_PRODUCTION_2);
		int initialNbCyborgs = factory.getNbCyborgs();
		
		game.play();
		
		Assert.assertEquals("Final cyborgs number is not correct", Math.abs(initialNbCyborgs - 9), factory.getNbCyborgs());
		Assert.assertEquals("Ownership is not correctly changed", Owner.PLAYER, factory.getOwner());
	}
	
	@Test
	public void incrementWorkOnlyIfMoreThanTenCyborgs() {
		Game game = createStandardGame();
		game.addActionIncrement(FACTORY_PLAYER_PRODUCTION_2, Owner.PLAYER);
		Factory factory = game.getFactory(FACTORY_PLAYER_PRODUCTION_2);
		int initialNbCyborgs = factory.getNbCyborgs() + Game.PRODUCTION_2;
		int initialProduction = factory.getProduction();
		
		game.play();
		
		Assert.assertEquals("Initial number of cyborgs was modified", initialNbCyborgs, factory.getNbCyborgs());
		Assert.assertEquals("Initial production was modified", initialProduction, factory.getProduction());
	}
	
	@Test
	public void incrementWorkOnlyIfLessThanMaxProduction() {
		Game game = createStandardGame();
		game.addActionIncrement(FACTORY_PLAYER_PRODUCTION_3, Owner.PLAYER);
		Factory factory = game.getFactory(FACTORY_PLAYER_PRODUCTION_3);
		factory.setNbCyborgs(15);
		int initialNbCyborgs = factory.getNbCyborgs() + Game.PRODUCTION_3;
		int initialProduction = factory.getProduction();
		
		game.play();
		
		Assert.assertEquals("Initial number of cyborgs was modified", initialNbCyborgs, factory.getNbCyborgs());
		Assert.assertEquals("Initial production was modified", initialProduction, factory.getProduction());
	}
	
	@Test
	public void incrementWorkOnlyIfOwnerIsSameThanAction() {
		Game game = createStandardGame();
		game.addActionIncrement(FACTORY_OPPONENT_PRODUCTION_2, Owner.PLAYER);
		Factory factory = game.getFactory(FACTORY_OPPONENT_PRODUCTION_2);
		factory.setNbCyborgs(15);
		int initialNbCyborgs = factory.getNbCyborgs() + Game.PRODUCTION_2;
		int initialProduction = factory.getProduction();
		
		game.play();
		
		Assert.assertEquals("Initial number of cyborgs was modified", initialNbCyborgs, factory.getNbCyborgs());
		Assert.assertEquals("Initial production was modified", initialProduction, factory.getProduction());
	}
	
	@Test
	public void incrementRemoveTenCyborgsAndAddOneProduction () {
		Game game = createStandardGame();
		game.addActionIncrement(FACTORY_PLAYER_PRODUCTION_2, Owner.PLAYER);
		Factory factory = game.getFactory(FACTORY_PLAYER_PRODUCTION_2);
		factory.setNbCyborgs(15);
		int initialNbCyborgs = factory.getNbCyborgs() + Game.PRODUCTION_3;
		int initialProduction = factory.getProduction();
		
		game.play();
		
		Assert.assertEquals("Initial number of cyborgs was not modified", initialNbCyborgs - Game.INCREMENT_CYBORGS_COST, factory.getNbCyborgs());
		Assert.assertEquals("Initial production was not modified", initialProduction + 1, factory.getProduction());
	}
	
	@Test
	public void moveCreateATroop () {
		Game game = createStandardGame();
		game.addActionMove(FACTORY_PLAYER_PRODUCTION_2, FACTORY_OPPONENT_PRODUCTION_2, 2, Owner.PLAYER);
		
		game.play();
		
		Assert.assertEquals("Troop was not created", 1, game.getNbTroops());
	}
	
	@Test
	public void severalMoveCreateSeveralTroops () {
		Game game = createStandardGame();
		game.addActionMove(FACTORY_PLAYER_PRODUCTION_2, FACTORY_OPPONENT_PRODUCTION_2, 2, Owner.PLAYER);
		game.addActionMove(FACTORY_PLAYER_PRODUCTION_3, FACTORY_OPPONENT_PRODUCTION_2, 3, Owner.PLAYER);
		
		game.play();
		
		Assert.assertEquals("Troops was not created", 2, game.getNbTroops());
	}
	
	@Test
	public void moveCreateATroopOnlyIfOwnerIsSameThanFromFactory () {
		Game game = createStandardGame();
		game.addActionMove(FACTORY_PLAYER_PRODUCTION_2, FACTORY_OPPONENT_PRODUCTION_2, 2, Owner.OPPONENT);
		
		game.play();
		
		Assert.assertEquals("Troop was created", 0, game.getNbTroops());
	}
	
	@Test
	public void moveCannotUseMoreThanFactoryNbCyborgs () {
		Game game = createStandardGame();
		game.addActionMove(FACTORY_PLAYER_PRODUCTION_2, FACTORY_OPPONENT_PRODUCTION_2, 25, Owner.PLAYER);
		Factory fromFactory = game.getFactory(FACTORY_PLAYER_PRODUCTION_2);
		int fromFactoryNbCyborgs = fromFactory.getNbCyborgs();
		
		game.play();
		
		Assert.assertEquals("Cyborg number can not be higher than cyborg number in the from factory", fromFactoryNbCyborgs, game.getTroop(0).getNbCyborgs());
	}
	
	@Test
	public void moveRemoveCyborgsFromFactory () {
		Game game = createStandardGame();
		game.addActionMove(FACTORY_PLAYER_NO_PRODUCTION, FACTORY_OPPONENT_PRODUCTION_2, 1, Owner.PLAYER);
		Factory factory = game.getFactory(FACTORY_PLAYER_NO_PRODUCTION);
		int initialNumberOfCyborg = factory.getNbCyborgs();
		
		game.play();
		
		Assert.assertEquals("From factory don't have the correct number of cyborg", Math.max(0, initialNumberOfCyborg - 1), factory.getNbCyborgs());
	}
	
	@Test
	public void moveRemoveMaximumCyborgsFromFactory() {
		Game game = createStandardGame();
		game.addActionMove(FACTORY_PLAYER_NO_PRODUCTION, FACTORY_OPPONENT_PRODUCTION_2, 25, Owner.PLAYER);
		Factory factory = game.getFactory(FACTORY_PLAYER_NO_PRODUCTION);
		int initialNumberOfCyborg = factory.getNbCyborgs();
		
		game.play();
		
		Assert.assertEquals("From factory don't have the correct number of cyborg", Math.max(0, initialNumberOfCyborg - 2), factory.getNbCyborgs());
	}
	
	@Test
	public void moveCantBeDoneOnSameFactory() {
		Game game = createStandardGame();
		game.addActionMove(FACTORY_PLAYER_PRODUCTION_2, FACTORY_PLAYER_PRODUCTION_2, 2, Owner.PLAYER);
		
		game.play();
		
		Assert.assertEquals("Troop was created", 0, game.getNbTroops());
	}
	
	
	@Test
	public void moveUseCorrectDistanceToSetRemainingTurn() {
		Game game = createStandardGame();
		game.addActionMove(FACTORY_PLAYER_NO_PRODUCTION, FACTORY_OPPONENT_PRODUCTION_2, 1, Owner.PLAYER);
		Factory fromFactory = game.getFactory(FACTORY_PLAYER_NO_PRODUCTION);
		Factory toFactory = game.getFactory(FACTORY_OPPONENT_PRODUCTION_2);
		int distance = game.getDistance(fromFactory.getId(), toFactory.getId());
		
		game.play();
		
		Assert.assertEquals("Remaining turn number is not correct", distance, game.getTroop(0).getRemainingTurns());
	
	}
	
	@Test
	public void aTroopMoveAndWinAFactoryOnceArrived() {
		Game game = createStandardGame();
		game.addActionMove(FACTORY_PLAYER_NO_PRODUCTION, FACTORY_OPPONENT_PRODUCTION_2, 25, Owner.PLAYER);
		Factory factory = game.getFactory(FACTORY_PLAYER_NO_PRODUCTION);
		factory.setNbCyborgs(25);
		
		game.play();
		Assert.assertEquals("Target factory is captured too soon", Owner.OPPONENT, game.getFactory(FACTORY_OPPONENT_PRODUCTION_2).getOwner());
	
		game.play();
		Assert.assertEquals("Target factory is captured too soon", Owner.OPPONENT, game.getFactory(FACTORY_OPPONENT_PRODUCTION_2).getOwner());
		
		game.play();
		Assert.assertEquals("Target factory is not captured", Owner.PLAYER, game.getFactory(FACTORY_OPPONENT_PRODUCTION_2).getOwner());
		
	}
	
	@Test
	public void notMoreThanTwoBombsCanBeCreated() {
		Game game = createStandardGame();
		game.addActionBomb(FACTORY_PLAYER_NO_PRODUCTION, FACTORY_OPPONENT_PRODUCTION_2, Owner.PLAYER);
		game.addActionBomb(FACTORY_PLAYER_PRODUCTION_2, FACTORY_OPPONENT_PRODUCTION_2, Owner.PLAYER);
		game.addActionBomb(FACTORY_PLAYER_PRODUCTION_3, FACTORY_OPPONENT_PRODUCTION_2, Owner.PLAYER);
		
		game.play();
		
		Assert.assertEquals("Too many bombs", Game.MAX_NB_BOMBS_PER_OWNER, game.getNbBombs());
	}
	
	// Do a factory send a bomb can not send a troop
	
}
