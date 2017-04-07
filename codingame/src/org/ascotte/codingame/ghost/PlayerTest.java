package org.ascotte.codingame.ghost;

import org.junit.Test;
import org.junit.Assert;

public class PlayerTest {

	@Test
	public void productionInOneFactoryOwnedByMeIncreaseCyborgNumber() {
		Game game = new Game(1);
		game.addFactory(0, Owner.PLAYER, 3, 2);
		
		game.play();
		
		Assert.assertEquals("Production increase is not done on my factory", 5, game.getFactory(0).getNbCyborgs());
	}
	
	@Test
	public void productionInSeveralFactoriesOwnedByMeIncreaseCyborgNumber() {
		Game game = new Game(2);
		game.addFactory(0, Owner.PLAYER, 3, 2);
		game.addFactory(1, Owner.PLAYER, 2, 2);
		
		game.play();
		
		Assert.assertEquals("Production increase is not done on my factory 1", 5, game.getFactory(0).getNbCyborgs());
		Assert.assertEquals("Production increase is not done on my factory 2", 4, game.getFactory(1).getNbCyborgs());
	}
	
	@Test
	public void productionInOneFactoryOwnedByEnnemyIncreaseCyborgNumber() {
		Game game = new Game(1);
		game.addFactory(0, Owner.OPPONENT, 3, 2);
		
		game.play();
		
		Assert.assertEquals("Production increase is not done on your factory", 5, game.getFactory(0).getNbCyborgs());
	}
	
	@Test
	public void productionInOneFactoryOwnedByNeutralIncreaseCyborgNumber() {
		Game game = new Game(1);
		game.addFactory(0, Owner.NOBODY, 3, 2);
		
		game.play();
		
		Assert.assertEquals("Production increase is not done on your factory", 2, game.getFactory(0).getNbCyborgs());
	}
}
