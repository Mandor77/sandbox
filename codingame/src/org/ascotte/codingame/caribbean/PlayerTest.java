package org.ascotte.codingame.caribbean;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PlayerTest {

	final static int PLAYER_BOAT_0 = 0;
	final Engine engine = new Engine();
	Grid grid = null;
	
	@Before
	public void createGrid() {
		this.grid = engine.createGrid(Engine.GRID_WIDTH, Engine.GRID_HEIGHT);
	}
	
	public Boat createBoat(int id, int x, int y, int direction) {
		Boat boat = null;
		try{
			boat = engine.createBoat(id, x, y, direction);
		}
		catch (InvalidBoatLocationException e) {
			
		}
		return boat;
	}
	/**
	 * Move test
	 */
	@Test
	public void boatIsDetectedOnTheGrid() {
		
		Boat boat = this.createBoat(PLAYER_BOAT_0, 3, 3, Engine.DIRECTION_RIGHT);
		
		Assert.assertEquals("Boat is not detected", true, grid.get(3, 3).containBoat());
		Assert.assertEquals("Boat is not detected", true, grid.get(4, 3).containBoat());
		Assert.assertEquals("Boat is not detected", true, grid.get(5, 3).containBoat());
	}
	
	@Test
	public void anEmptyCellDontDetectABoat() {
		
		Boat boat = this.createBoat(PLAYER_BOAT_0, 3, 3, Engine.DIRECTION_RIGHT);
		
		Assert.assertEquals("Boat is detected in a bad position", false, grid.get(2, 3).containBoat());
		Assert.assertEquals("Boat is detected in a bad position", false, grid.get(3, 2).containBoat());
		Assert.assertEquals("Boat is detected in a bad position", false, grid.get(3, 4).containBoat());
		Assert.assertEquals("Boat is detected in a bad position", false, grid.get(6, 3).containBoat());
	}
	
	@Test
	public void boatDetectedOnTheGridCanBeIdentified() {
		
		Boat boat = this.createBoat(PLAYER_BOAT_0, 3, 3, Engine.DIRECTION_RIGHT);
		
		Assert.assertEquals("Boat on the grid is not the correct boat", boat, grid.get(3, 3).getBoat());
		Assert.assertEquals("Boat on the grid is not the correct boat", boat, grid.get(4, 3).getBoat());
		Assert.assertEquals("Boat on the grid is not the correct boat", boat, grid.get(5, 3).getBoat());
	}
	
	@Test(expected=InvalidBoatLocationException.class)
	public void cantCreateABoatOutsideTheGrid() throws InvalidBoatLocationException {
		
		Boat boat = engine.createBoat(PLAYER_BOAT_0, 21, 21, Engine.DIRECTION_RIGHT);
	}
	
	@Test
	public void boatWithDirectionRightAreCorrectlyDrawed() {
		Boat boat = this.createBoat(PLAYER_BOAT_0, 3, 3, Engine.DIRECTION_RIGHT);
		
		Assert.assertEquals("Boat on the grid is not the correct boat", boat, grid.get(3, 3).getBoat());
		Assert.assertEquals("Boat on the grid is not the correct boat", boat, grid.get(4, 3).getBoat());
		Assert.assertEquals("Boat on the grid is not the correct boat", boat, grid.get(5, 3).getBoat());
	}
	
	@Test
	public void boatWithDirectionLeftAreCorrectlyDrawed() {
		Boat boat = this.createBoat(PLAYER_BOAT_0, 4, 6, Engine.DIRECTION_LEFT);
		
		Assert.assertEquals("Boat on the grid is not the correct boat", boat, grid.get(4, 6).getBoat());
		Assert.assertEquals("Boat on the grid is not the correct boat", boat, grid.get(3, 6).getBoat());
		Assert.assertEquals("Boat on the grid is not the correct boat", boat, grid.get(2, 6).getBoat());
	}
	
	@Test
	public void boatWithDirectionUpLeftAreCorrectlyDrawedOnEvenCase() {
		Boat boat = this.createBoat(PLAYER_BOAT_0, 6, 3, Engine.DIRECTION_UP_LEFT);
		
		Assert.assertEquals("Boat is not correctly drawed", boat, grid.get(6, 3).getBoat());
		Assert.assertEquals("Boat is not correctly drawed", boat, grid.get(6, 2).getBoat());
		Assert.assertEquals("Boat is not correctly drawed", boat, grid.get(5, 1).getBoat());
	}
	
	@Test
	public void boatWithDirectionUpLeftAreCorrectlyDrawedOnOddCase() {
		Boat boat = this.createBoat(PLAYER_BOAT_0, 6, 4, Engine.DIRECTION_UP_LEFT);
		
		Assert.assertEquals("Boat is not correctly drawed", boat, grid.get(6, 4).getBoat());
		Assert.assertEquals("Boat is not correctly drawed", boat, grid.get(5, 3).getBoat());
		Assert.assertEquals("Boat is not correctly drawed", boat, grid.get(5, 2).getBoat());
	}
	
	
}
