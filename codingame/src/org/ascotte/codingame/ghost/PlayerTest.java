package org.ascotte.codingame.ghost;

import org.junit.Test;
import org.junit.Assert;

public class PlayerTest {

	@Test
	public void productionInOneFactoryOwnedByMeIncreaseCyborgNumber() {
		Game game = new Game(1, 0);
		game.addFactory(0, Owner.PLAYER, 3, 2);
		
		game.play();
		
		Assert.assertEquals("Production increase is not done on my factory", 5, game.getFactory(0).getNbCyborgs());
	}
	
	@Test
	public void productionInSeveralFactoriesOwnedByMeIncreaseCyborgNumber() {
		Game game = new Game(2, 0);
		game.addFactory(0, Owner.PLAYER, 3, 2);
		game.addFactory(1, Owner.PLAYER, 2, 2);
		
		game.play();
		
		Assert.assertEquals("Production increase is not done on my factory 1", 5, game.getFactory(0).getNbCyborgs());
		Assert.assertEquals("Production increase is not done on my factory 2", 4, game.getFactory(1).getNbCyborgs());
	}
	
	@Test
	public void productionInOneFactoryOwnedByEnnemyIncreaseCyborgNumber() {
		Game game = new Game(1, 0);
		game.addFactory(0, Owner.OPPONENT, 3, 2);
		
		game.play();
		
		Assert.assertEquals("Production increase is not done on your factory", 5, game.getFactory(0).getNbCyborgs());
	}
	
	@Test
	public void productionInOneFactoryOwnedByNeutralIncreaseCyborgNumber() {
		Game game = new Game(1, 0);
		game.addFactory(0, Owner.NOBODY, 3, 2);
		
		game.play();
		
		Assert.assertEquals("Production increase is not done on your factory", 2, game.getFactory(0).getNbCyborgs());
	}
	
	@Test
	public void movingTroopProgressOfOneInATurn() {
		Game game = new Game(2, 1);
		game.addFactory(0, Owner.PLAYER, 3, 2);
		game.addFactory(1, Owner.OPPONENT, 3, 2);
		game.addTroop(0, Owner.PLAYER, 0, 1, 1, 5);
		
		game.play();
		
		Assert.assertEquals("Troop move is not done in a game", 4, game.getTroop(0).getRemainingTurns());
	}
	
	@Test
	public void factoryIsAttackedByAEnnemyArrivedTroop() {
		Game game = new Game(2, 1);
		game.addFactory(0, Owner.PLAYER, 0, 2);
		game.addFactory(1, Owner.OPPONENT, 0, 2);
		game.addTroop(0, Owner.PLAYER, 0, 1, 1, 1);
		
		game.play();
		
		Assert.assertEquals("Troop does not attack when arrives", 1, game.getFactory(1).getNbCyborgs());
	}
	
	@Test
	public void factoryIsReinforcedByAnAmicalArrivedTroop() {
		Game game = new Game(2, 1);
		game.addFactory(0, Owner.PLAYER, 0, 2);
		game.addFactory(1, Owner.OPPONENT, 0, 2);
		game.addTroop(0, Owner.OPPONENT, 0, 1, 1, 1);
		
		game.play();
		
		Assert.assertEquals("Troop does not attack when arrives", 3, game.getFactory(1).getNbCyborgs());
	}
	
	@Test
	public void troopConflictAreResolvedBeforeAttackingFactory() {
		Game game = new Game(3, 2);
		game.addFactory(0, Owner.PLAYER, 0, 2);
		game.addFactory(1, Owner.OPPONENT, 0, 2);
		game.addFactory(2, Owner.PLAYER, 0, 2);
		game.addTroop(0, Owner.OPPONENT, 0, 1, 1, 1);
		game.addTroop(1, Owner.PLAYER, 2, 1, 1, 1);
		
		game.play();
		
		Assert.assertEquals("Conflicts are not resolved before impacting factory", 2, game.getFactory(1).getNbCyborgs());
	}
}
