package org.ascotte.codingame.kutulu;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PlayerTest {

	@Before
	public void init_game () {
		Game.init();
	}
	
	@Test
	public void explorer_with_life_is_alive() {
		Assert.assertEquals(Game.explorer[0].isDied(), false);
	}
	
	@Test
	public void explorer_with_no_life_is_dead () {
		Game.explorer[0].mentalHealth = 0;
		
		Assert.assertEquals(Game.explorer[0].isDied(), true);
	}
	
	@Test
	public void explorer_lost_life_every_turn () {
		Game.next();
		Game.next();
		
		Assert.assertEquals(Game.explorer[0].mentalHealth, Game.defaultMentalHealth - Game.healthLostByTurn * 2);
	}
	
	@Test
	public void explorer_close_0_lost_reduced_life_every_turn () {
		Game.explorer[0].width = Game.explorer[1].width;
		Game.explorer[0].height = Game.explorer[1].height;
		
		Game.next();
		
		Assert.assertEquals(Game.explorer[0].mentalHealth, Game.defaultMentalHealth - Game.reducedHealthLostByTurn);
		Assert.assertEquals(Game.explorer[1].mentalHealth, Game.defaultMentalHealth - Game.reducedHealthLostByTurn);
		Assert.assertEquals(Game.explorer[2].mentalHealth, Game.defaultMentalHealth - Game.healthLostByTurn);
		Assert.assertEquals(Game.explorer[3].mentalHealth, Game.defaultMentalHealth - Game.healthLostByTurn);
	}
	
	@Test
	public void explorer_close_1_lost_reduced_life_every_turn () {
		Game.explorer[0].width = Game.explorer[2].width-1;
		Game.explorer[0].height = Game.explorer[2].height+1;
		
		Game.next();
		
		Assert.assertEquals(Game.explorer[0].mentalHealth, Game.defaultMentalHealth - Game.reducedHealthLostByTurn);
		Assert.assertEquals(Game.explorer[1].mentalHealth, Game.defaultMentalHealth - Game.healthLostByTurn);
		Assert.assertEquals(Game.explorer[2].mentalHealth, Game.defaultMentalHealth - Game.reducedHealthLostByTurn);
		Assert.assertEquals(Game.explorer[3].mentalHealth, Game.defaultMentalHealth - Game.healthLostByTurn);
	}
	
	@Test
	public void explorer_close_2_lost_reduced_life_every_turn () {
		Game.explorer[2].width = Game.explorer[3].width-2;
		Game.explorer[2].height = Game.explorer[3].height+2;
		
		Game.next();
		
		Assert.assertEquals(Game.explorer[0].mentalHealth, Game.defaultMentalHealth - Game.healthLostByTurn);
		Assert.assertEquals(Game.explorer[1].mentalHealth, Game.defaultMentalHealth - Game.healthLostByTurn);
		Assert.assertEquals(Game.explorer[2].mentalHealth, Game.defaultMentalHealth - Game.reducedHealthLostByTurn);
		Assert.assertEquals(Game.explorer[3].mentalHealth, Game.defaultMentalHealth - Game.reducedHealthLostByTurn);
	}
	
	@Test
	public void explorer_close_3_lost_life_every_turn () {
		Game.explorer[0].width = Game.explorer[1].width-3;
		Game.explorer[0].height = Game.explorer[1].height+3;
		
		Game.next();
		
		Assert.assertEquals(Game.explorer[0].mentalHealth, Game.defaultMentalHealth - Game.healthLostByTurn);
		Assert.assertEquals(Game.explorer[1].mentalHealth, Game.defaultMentalHealth - Game.healthLostByTurn);
		Assert.assertEquals(Game.explorer[2].mentalHealth, Game.defaultMentalHealth - Game.healthLostByTurn);
		Assert.assertEquals(Game.explorer[3].mentalHealth, Game.defaultMentalHealth - Game.healthLostByTurn);
	}
	
	@Test
	public void explorer_lost_a_lot_life_when_meet_a_wanderer () {
		Game.wanderers.add(new Wanderer(Game.explorer[0].width, Game.explorer[0].height));
		
		Game.next();
		
		Assert.assertEquals(Game.explorer[0].mentalHealth, Game.defaultMentalHealth - Game.augmentedHealthLostByTurn);
		Assert.assertEquals(Game.explorer[1].mentalHealth, Game.defaultMentalHealth - Game.healthLostByTurn);
		Assert.assertEquals(Game.explorer[2].mentalHealth, Game.defaultMentalHealth - Game.healthLostByTurn);
		Assert.assertEquals(Game.explorer[3].mentalHealth, Game.defaultMentalHealth - Game.healthLostByTurn);
	}
	
	@Test
	public void wanderer_disappears_when_meet_an_explorer () {
		Game.wanderers.add(new Wanderer(Game.explorer[0].width, Game.explorer[0].height));
		
		Game.next();
		
		Assert.assertEquals(Game.wanderers.size(), 0);
	}
}
