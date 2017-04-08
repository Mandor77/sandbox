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
	
	public Game createStandardGame() {
		Game game = new Game(NB_FACTORY);
		game.addFactory(FACTORY_PLAYER_NO_PRODUCTION, Owner.PLAYER, Game.PRODUCTION_0, NB_CYBORGS_2);
		game.addFactory(FACTORY_PLAYER_PRODUCTION_2, Owner.PLAYER, Game.PRODUCTION_2, NB_CYBORGS_2);
		game.addFactory(FACTORY_PLAYER_PRODUCTION_3, Owner.PLAYER, Game.PRODUCTION_3, NB_CYBORGS_5);
		game.addFactory(FACTORY_NEUTRAL_PRODUCTION_2, Owner.NOBODY, Game.PRODUCTION_2, NB_CYBORGS_5);
		game.addFactory(FACTORY_OPPONENT_NO_PRODUCTION, Owner.OPPONENT, Game.PRODUCTION_0, NB_CYBORGS_2);
		game.addFactory(FACTORY_OPPONENT_PRODUCTION_2, Owner.OPPONENT, Game.PRODUCTION_2, NB_CYBORGS_2);
		game.addFactory(FACTORY_OPPONENT_PRODUCTION_3, Owner.OPPONENT, Game.PRODUCTION_3, NB_CYBORGS_5);
		return game;
	}
	
	@Test
	public void productionIsDoneForOwnedFactoryWithProduction() {
		Game game = createStandardGame();
		game.initNbTroop(0);
		
		game.play();
		
		Assert.assertEquals("Production is not done for owned factory", 4, game.getFactory(FACTORY_PLAYER_PRODUCTION_2).getNbCyborgs());
		Assert.assertEquals("Production is not done for owned factory", 8, game.getFactory(FACTORY_PLAYER_PRODUCTION_3).getNbCyborgs());
		Assert.assertEquals("Production is not done for owned factory", 4, game.getFactory(FACTORY_OPPONENT_PRODUCTION_2).getNbCyborgs());
		Assert.assertEquals("Production is not done for owned factory", 8, game.getFactory(FACTORY_OPPONENT_PRODUCTION_3).getNbCyborgs());
	}
	
	@Test
	public void productionIsNotDoneForOwnedFactoryWithoutProduction() {
		Game game = createStandardGame();
		game.initNbTroop(0);
		
		game.play();
		
		Assert.assertEquals("Production is done for owned factory without production", 2, game.getFactory(FACTORY_PLAYER_NO_PRODUCTION).getNbCyborgs());
		Assert.assertEquals("Production is done for owned factory without production", 2, game.getFactory(FACTORY_OPPONENT_NO_PRODUCTION).getNbCyborgs());
	}
	
	@Test
	public void productionIsNotDoneForNotOwnedFactory() {
		Game game = createStandardGame();
		game.initNbTroop(0);
		Factory factory = game.getFactory(FACTORY_NEUTRAL_PRODUCTION_2);
		int initialNbCyborgs = factory.getNbCyborgs();
		
		game.play();
		
		Assert.assertEquals("Production is done for not owned factory", initialNbCyborgs, factory.getNbCyborgs());
	}
	
	@Test
	public void troopMoveOfOneByTurn() {
		Game game = createStandardGame();
		game.initNbTroop(1);
		game.addTroop(0, Owner.PLAYER, FACTORY_PLAYER_NO_PRODUCTION, FACTORY_OPPONENT_NO_PRODUCTION, 1, 5);
		
		game.play();
		
		Assert.assertEquals("Troop don't move in a turn", 4, game.getTroop(0).getRemainingTurns());
	}
	
	@Test
	public void troopAttackAnEnnemyFactory() {
		Game game = createStandardGame();
		game.initNbTroop(1);
		game.addTroop(0, Owner.PLAYER, FACTORY_PLAYER_NO_PRODUCTION, FACTORY_OPPONENT_NO_PRODUCTION, 1, 1);
		Factory factory = game.getFactory(FACTORY_OPPONENT_NO_PRODUCTION);
		int initialNbCyborgs = factory.getNbCyborgs();
		
		game.play();
		
		Assert.assertEquals("Troop does not attack when arrives", initialNbCyborgs - 1, factory.getNbCyborgs());
	}
	
	@Test
	public void troopReinforceAmicalFactory() {
		Game game = createStandardGame();
		game.initNbTroop(1);
		game.addTroop(0, Owner.OPPONENT, FACTORY_PLAYER_NO_PRODUCTION, FACTORY_OPPONENT_NO_PRODUCTION, 1, 1);
		Factory factory = game.getFactory(FACTORY_OPPONENT_NO_PRODUCTION);
		int initialNbCyborgs = factory.getNbCyborgs();
		
		game.play();
		
		Assert.assertEquals("Troop does not reinforce when arrives", initialNbCyborgs + 1, factory.getNbCyborgs());
	}
	
	@Test
	public void troopWinAnEnnemyFactory() {
		Game game = createStandardGame();
		game.initNbTroop(1);
		game.addTroop(0, Owner.PLAYER, FACTORY_PLAYER_NO_PRODUCTION, FACTORY_OPPONENT_NO_PRODUCTION, 5, 1);
		Factory factory = game.getFactory(FACTORY_OPPONENT_NO_PRODUCTION);
		int initialNbCyborgs = factory.getNbCyborgs();
		
		game.play();
		
		Assert.assertEquals("Final cyborgs number is not correct", Math.abs(initialNbCyborgs - 5), factory.getNbCyborgs());
		Assert.assertEquals("Ownership is not correctly changed", Owner.PLAYER, factory.getOwner());
	}
	
	@Test
	public void troopWinANotOwnedFactory() {
		Game game = createStandardGame();
		game.initNbTroop(1);
		game.addTroop(0, Owner.PLAYER, FACTORY_PLAYER_NO_PRODUCTION, FACTORY_NEUTRAL_PRODUCTION_2, 6, 1);
		Factory factory = game.getFactory(FACTORY_NEUTRAL_PRODUCTION_2);
		int initialNbCyborgs = factory.getNbCyborgs();
		
		game.play();
		
		Assert.assertEquals("Final cyborgs number is not correct", Math.abs(initialNbCyborgs - 6), factory.getNbCyborgs());
		Assert.assertEquals("Ownership is not correctly changed", Owner.PLAYER, factory.getOwner());
	}
	
	
	@Test
	public void troopAttackInPriorityOtherTroops() {
		Game game = createStandardGame();
		game.initNbTroop(2);
		game.addTroop(0, Owner.PLAYER, FACTORY_PLAYER_NO_PRODUCTION, FACTORY_OPPONENT_NO_PRODUCTION, 1, 1);
		game.addTroop(1, Owner.OPPONENT, FACTORY_OPPONENT_PRODUCTION_2, FACTORY_OPPONENT_NO_PRODUCTION, 1, 1);
		Factory factory = game.getFactory(FACTORY_OPPONENT_NO_PRODUCTION);
		int initialNbCyborgs = factory.getNbCyborgs();
		
		game.play();
		
		Assert.assertEquals("Conflicts are not resolved before impacting factory", initialNbCyborgs, factory.getNbCyborgs());
	}
	
	
	
	@Test
	public void troopAttackBothEnnemyTroopsAndEnnemyFactoryWithoutConquest() {
		Game game = createStandardGame();
		game.initNbTroop(4);
		game.addTroop(0, Owner.OPPONENT, FACTORY_OPPONENT_PRODUCTION_2, FACTORY_OPPONENT_NO_PRODUCTION, 5, 1);
		game.addTroop(1, Owner.OPPONENT, FACTORY_OPPONENT_PRODUCTION_3, FACTORY_OPPONENT_NO_PRODUCTION, 1, 1);
		game.addTroop(2, Owner.PLAYER, FACTORY_PLAYER_NO_PRODUCTION, FACTORY_OPPONENT_NO_PRODUCTION, 4, 1);
		game.addTroop(3, Owner.PLAYER, FACTORY_PLAYER_PRODUCTION_2, FACTORY_OPPONENT_NO_PRODUCTION, 3, 1);
		Factory factory = game.getFactory(FACTORY_OPPONENT_NO_PRODUCTION);
		int initialNbCyborgs = factory.getNbCyborgs();
		
		game.play();
		
		Assert.assertEquals("Final cyborgs number is not correct", initialNbCyborgs - 1, factory.getNbCyborgs());
		Assert.assertEquals("Ownership is not correctly changed", Owner.OPPONENT, factory.getOwner());
	}
	
	@Test
	public void troopAttackBothEnnemyTroopsAndEnnemyFactoryWithConquest() {
		Game game = createStandardGame();
		game.initNbTroop(4);
		game.addTroop(0, Owner.OPPONENT, FACTORY_OPPONENT_PRODUCTION_2, FACTORY_OPPONENT_NO_PRODUCTION, 5, 1);
		game.addTroop(1, Owner.OPPONENT, FACTORY_OPPONENT_PRODUCTION_3, FACTORY_OPPONENT_NO_PRODUCTION, 1, 1);
		game.addTroop(2, Owner.PLAYER, FACTORY_PLAYER_NO_PRODUCTION, FACTORY_OPPONENT_NO_PRODUCTION, 8, 1);
		game.addTroop(3, Owner.PLAYER, FACTORY_PLAYER_PRODUCTION_2, FACTORY_OPPONENT_NO_PRODUCTION, 3, 1);
		Factory factory = game.getFactory(FACTORY_OPPONENT_NO_PRODUCTION);
		int initialNbCyborgs = factory.getNbCyborgs();
		
		game.play();
		
		Assert.assertEquals("Final cyborgs number is not correct", Math.abs(initialNbCyborgs - 5), factory.getNbCyborgs());
		Assert.assertEquals("Ownership is not correctly changed", Owner.PLAYER, factory.getOwner());
	}
	
	@Test
	public void troopAttackBothEnnemyTroopsAndNotOwnedFactoryWithConquest() {
		Game game = createStandardGame();
		game.initNbTroop(4);
		game.addTroop(0, Owner.OPPONENT, FACTORY_OPPONENT_PRODUCTION_2, FACTORY_NEUTRAL_PRODUCTION_2, 5, 1);
		game.addTroop(1, Owner.OPPONENT, FACTORY_OPPONENT_PRODUCTION_3, FACTORY_NEUTRAL_PRODUCTION_2, 1, 1);
		game.addTroop(2, Owner.PLAYER, FACTORY_PLAYER_NO_PRODUCTION, FACTORY_NEUTRAL_PRODUCTION_2, 12, 1);
		game.addTroop(3, Owner.PLAYER, FACTORY_PLAYER_PRODUCTION_2, FACTORY_NEUTRAL_PRODUCTION_2, 3, 1);
		Factory factory = game.getFactory(FACTORY_NEUTRAL_PRODUCTION_2);
		int initialNbCyborgs = factory.getNbCyborgs();
		
		game.play();
		
		Assert.assertEquals("Final cyborgs number is not correct", Math.abs(initialNbCyborgs - 9), factory.getNbCyborgs());
		Assert.assertEquals("Ownership is not correctly changed", Owner.PLAYER, factory.getOwner());
	}
}
