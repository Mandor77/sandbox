package org.ascotte.codingame.kutulu;

import java.util.ArrayList;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PlayerTest {

	@Before
	public void init_game () {
		Game.init();
		List<String> lines = new ArrayList<String>();
		lines.add("..........");
		lines.add(".#.#.#.#.#");
		lines.add(".......#.#");
		lines.add(".#.#.#.#.#");
		lines.add(".......#.#");
		lines.add(".#.#.#.#.#");
		lines.add(".......#.#");
		lines.add(".#######.#");
		lines.add("..........");
		lines.add(".#######..");
		Game.setBoard(lines);
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
		Game.explorer[0].height = Game.explorer[2].height;
		
		Game.next();
		
		Assert.assertEquals(Game.explorer[0].mentalHealth, Game.defaultMentalHealth - Game.reducedHealthLostByTurn);
		Assert.assertEquals(Game.explorer[1].mentalHealth, Game.defaultMentalHealth - Game.healthLostByTurn);
		Assert.assertEquals(Game.explorer[2].mentalHealth, Game.defaultMentalHealth - Game.reducedHealthLostByTurn);
		Assert.assertEquals(Game.explorer[3].mentalHealth, Game.defaultMentalHealth - Game.healthLostByTurn);
	}
	
	@Test
	public void explorer_close_2_lost_reduced_life_every_turn () {
		Game.explorer[2].width = Game.explorer[3].width-1;
		Game.explorer[2].height = Game.explorer[3].height+1;
		
		Game.next();
		
		Assert.assertEquals(Game.explorer[0].mentalHealth, Game.defaultMentalHealth - Game.healthLostByTurn);
		Assert.assertEquals(Game.explorer[1].mentalHealth, Game.defaultMentalHealth - Game.healthLostByTurn);
		Assert.assertEquals(Game.explorer[2].mentalHealth, Game.defaultMentalHealth - Game.reducedHealthLostByTurn);
		Assert.assertEquals(Game.explorer[3].mentalHealth, Game.defaultMentalHealth - Game.reducedHealthLostByTurn);
	}
	
	@Test
	public void explorer_close_2_bis_lost_reduced_life_every_turn () {
		Game.explorer[2].width = Game.explorer[3].width;
		Game.explorer[2].height = Game.explorer[3].height;
		Game.explorer[3].died = true;
		
		Game.next();
		
		Assert.assertEquals(Game.explorer[0].mentalHealth, Game.defaultMentalHealth - Game.healthLostByTurn);
		Assert.assertEquals(Game.explorer[1].mentalHealth, Game.defaultMentalHealth - Game.healthLostByTurn);
		Assert.assertEquals(Game.explorer[2].mentalHealth, Game.defaultMentalHealth - Game.healthLostByTurn);
	}
	
	@Test
	public void explorer_close_3_lost_life_every_turn () {
		Game.explorer[0].width = Game.explorer[1].width-2;
		Game.explorer[0].height = Game.explorer[1].height+1;
		
		Game.next();
		
		Assert.assertEquals(Game.explorer[0].mentalHealth, Game.defaultMentalHealth - Game.healthLostByTurn);
		Assert.assertEquals(Game.explorer[1].mentalHealth, Game.defaultMentalHealth - Game.healthLostByTurn);
		Assert.assertEquals(Game.explorer[2].mentalHealth, Game.defaultMentalHealth - Game.healthLostByTurn);
		Assert.assertEquals(Game.explorer[3].mentalHealth, Game.defaultMentalHealth - Game.healthLostByTurn);
	}
	
	@Test
	public void explorer_lost_a_lot_life_when_meet_a_wanderer () {
		Game.explorer[1].width = Game.explorer[0].width;
		Game.explorer[1].height = Game.explorer[0].height;
		Game.wanderers.put(0, new Wanderer(Game.explorer[0].width, Game.explorer[0].height));
		Game.wanderers.put(1, new Wanderer(Game.explorer[2].width, Game.explorer[2].height));
		Game.wanderers.put(2, new Wanderer(Game.explorer[2].width, Game.explorer[2].height));
		Game.wanderers.put(3, new Wanderer(Game.explorer[0].width, Game.explorer[0].height));
		Game.wanderers.get(0).state = Game.WANDERING;
		Game.wanderers.get(1).state = Game.WANDERING;
		Game.wanderers.get(2).state = Game.WANDERING;
		
		Game.next();
		
		Assert.assertEquals(Game.explorer[0].mentalHealth, Game.defaultMentalHealth - Game.reducedHealthLostByTurn - Game.augmentedHealthLostByTurn);
		Assert.assertEquals(Game.explorer[1].mentalHealth, Game.defaultMentalHealth - Game.reducedHealthLostByTurn - Game.augmentedHealthLostByTurn);
		Assert.assertEquals(Game.explorer[2].mentalHealth, Game.defaultMentalHealth - Game.healthLostByTurn - Game.augmentedHealthLostByTurn * 2);
		Assert.assertEquals(Game.explorer[3].mentalHealth, Game.defaultMentalHealth - Game.healthLostByTurn);
	}
	
	@Test
	public void wanderer_disappears_when_meet_an_explorer () {
		Game.wanderers.put(0, new Wanderer(Game.explorer[0].width, Game.explorer[0].height));
		Game.wanderers.get(0).state = Game.WANDERING;
		
		Game.next();
		
		Assert.assertEquals(Game.wanderers.size(), 0);
	}
	
	@Test
	public void board_is_correctly_reduced () {
		
		Assert.assertEquals(0, Game.fetchingBoard[0][0][Game.UP]);
		Assert.assertEquals(2, Game.fetchingBoard[0][0][Game.RIGHT]);
		Assert.assertEquals(2, Game.fetchingBoard[0][0][Game.DOWN]);
		Assert.assertEquals(0, Game.fetchingBoard[0][0][Game.LEFT]);
		Assert.assertEquals(0, Game.fetchingBoard[1][8][Game.UP]);
		Assert.assertEquals(7, Game.fetchingBoard[1][8][Game.RIGHT]);
		Assert.assertEquals(0, Game.fetchingBoard[1][8][Game.DOWN]);
		Assert.assertEquals(1, Game.fetchingBoard[1][8][Game.LEFT]);
		Assert.assertEquals(0, Game.fetchingBoard[9][8][Game.UP]);
		Assert.assertEquals(0, Game.fetchingBoard[9][8][Game.RIGHT]);
		Assert.assertEquals(1, Game.fetchingBoard[9][8][Game.LEFT]);
		Assert.assertEquals(1, Game.fetchingBoard[9][8][Game.DOWN]);
	}
	
	@Test
	public void wanderer_have_correctly_moved () {
		Game.wanderers.put(0, new Wanderer(0, 4));
		Game.wanderers.put(1, new Wanderer(4, 0));
		Game.wanderers.put(2, new Wanderer(1, 2));
		Game.wanderers.put(3, new Wanderer(1, 8));
		Game.wanderers.put(4, new Wanderer(1, 8));
		Game.wanderers.put(5, new Wanderer(9, 8));
		Game.wanderers.get(0).target = 0;
		Game.wanderers.get(1).target = 0;
		Game.wanderers.get(2).target = 0;
		Game.explorer[1].width = 3;
		Game.explorer[1].height = 8;
		Game.explorer[2].width = 8;
		Game.explorer[2].height = 8;
		Game.wanderers.get(3).target = 1;
		Game.wanderers.get(4).target = -1;
		Game.wanderers.get(5).target = 2;
		
		Game.next();
		
		Assert.assertEquals(0, Game.wanderers.get(0).width);
		Assert.assertEquals(3, Game.wanderers.get(0).height);
		Assert.assertEquals(3, Game.wanderers.get(1).width);
		Assert.assertEquals(0, Game.wanderers.get(1).height);
		Assert.assertEquals(0, Game.wanderers.get(2).width);
		Assert.assertEquals(2, Game.wanderers.get(2).height);
		Assert.assertEquals(2, Game.wanderers.get(3).width);
		Assert.assertEquals(8, Game.wanderers.get(3).height);
		Assert.assertEquals(1, Game.wanderers.get(4).width);
		Assert.assertEquals(8, Game.wanderers.get(4).height);
		Assert.assertEquals(8, Game.wanderers.get(5).width);
		Assert.assertEquals(8, Game.wanderers.get(5).height);
	}
	
	@Test
	public void wanderer_target_closest_explorer () {
		Game.wanderers.put(0, new Wanderer(1, 2));
		Game.wanderers.put(1, new Wanderer(7, 7));
		Game.wanderers.get(0).state = Game.WANDERING;
		Game.wanderers.get(1).state = Game.WANDERING;
		
		Game.next();
		
		Assert.assertEquals(0, Game.wanderers.get(0).target);
		Assert.assertEquals(3, Game.wanderers.get(1).target);
	}
	
	@Test
	public void explorer_is_killed_when_no_life () {
		Game.explorer[0].mentalHealth = 2;
		
		Game.next();
		
		Assert.assertEquals(1, Game.explorer[0].mentalHealth);
		Assert.assertEquals(true, Game.explorer[0].died);
	}
	
	@Test
	public void explorer_is_still_ok_after_simulation () {
		
		Game.movePlayer();
		
		Assert.assertEquals(Game.defaultMentalHealth, Game.explorer[0].mentalHealth);
	}
	
	@Test
	public void explorer_should_move_correctly_0() {
		
		Game.explorer[0].width = 8;
		Game.explorer[0].height = 2;
		Game.wanderers.put(0, new Wanderer(8,1));
		Game.wanderers.get(0).state = Game.WANDERING;
		
		Game.movePlayer();
		
		Assert.assertEquals(8, Game.explorer[0].width);
		Assert.assertEquals(3, Game.explorer[0].height);
	}
	
	@Test
	public void explorer_should_move_correctly_1() {
		
		Game.explorer[0].width = 0;
		Game.explorer[0].height = 2;
		Game.wanderers.put(0, new Wanderer(0,1));
		Game.wanderers.put(1, new Wanderer(0,3));
		Game.wanderers.get(0).state = Game.WANDERING;
		Game.wanderers.get(1).state = Game.WANDERING;
		
		Game.movePlayer();
		
		Assert.assertEquals(1, Game.explorer[0].width);
		Assert.assertEquals(2, Game.explorer[0].height);
	}
	
	@Test
	public void explorer_should_move_correctly_2() {
		
		Game.explorer[0].width = 3;
		Game.explorer[0].height = 0;
		Game.explorer[1].width = 1;
		Game.explorer[1].height = 0;
		Game.wanderers.put(0, new Wanderer(2,0));
		Game.wanderers.get(0).state = Game.WANDERING;
		Game.wanderers.get(0).target = 1;
		
		Game.movePlayer();
		
		Assert.assertEquals(4, Game.explorer[0].width);
		Assert.assertEquals(0, Game.explorer[0].height);
	}
}
