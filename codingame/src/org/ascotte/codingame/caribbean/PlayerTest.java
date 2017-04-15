package org.ascotte.codingame.caribbean;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PlayerTest {

	final static int PLAYER_BOAT_0 = 0;
	final static int BARREL_0 = 0;
	final static int BARREL_1 = 1;
	final static int BARREL_2 = 2;
	final static int RUM_1 = 1;
	final static int RUM_10 = 10;
	final static int RUM_15 = 15;
	final static int RUM_20 = 20;
	final static int RUM_100 = 100;
	
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
		catch (InvalidBoatIdException e) {
			
		}
		return boat;
	}
	
	public Barrel createBarrel(int id, int x, int y, int rumQuantity) {
		Barrel barrel = null;
		try {
			barrel = engine.createBarrel(id, x, y, rumQuantity);
		}
		catch (InvalidBarrelLocationException e) {
			
		}
		catch (InvalidRumQuantityLocationException e) {
			
		}
		return barrel;
	}
	
	/**
	 * Move test
	 */
	@Test
	public void boatIsDetectedOnTheGrid() {
		
		Boat boat = this.createBoat(PLAYER_BOAT_0, 3, 3, Engine.DIRECTION_RIGHT);
		
		Assert.assertEquals("Boat is not detected", true, grid.get(4, 3).containBoat());
		Assert.assertEquals("Boat is not detected", true, grid.get(3, 3).containBoat());
		Assert.assertEquals("Boat is not detected", true, grid.get(2, 3).containBoat());
	}
	
	@Test
	public void anEmptyCellDontDetectABoat() {
		
		Boat boat = this.createBoat(PLAYER_BOAT_0, 3, 3, Engine.DIRECTION_RIGHT);
		
		Assert.assertEquals("Boat is detected in a bad position", false, grid.get(1, 3).containBoat());
		Assert.assertEquals("Boat is detected in a bad position", false, grid.get(3, 2).containBoat());
		Assert.assertEquals("Boat is detected in a bad position", false, grid.get(3, 4).containBoat());
		Assert.assertEquals("Boat is detected in a bad position", false, grid.get(5, 3).containBoat());
	}
	
	@Test
	public void boatDetectedOnTheGridCanBeIdentified() {
		
		Boat boat = this.createBoat(PLAYER_BOAT_0, 3, 3, Engine.DIRECTION_RIGHT);
		
		Assert.assertEquals("Boat on the grid is not the correct boat", boat, grid.get(2, 3).getBoat());
		Assert.assertEquals("Boat on the grid is not the correct boat", boat, grid.get(3, 3).getBoat());
		Assert.assertEquals("Boat on the grid is not the correct boat", boat, grid.get(4, 3).getBoat());
	}
	
	@Test(expected=InvalidBoatLocationException.class)
	public void cantCreateABoatOutsideTheGrid() 
			throws InvalidBoatLocationException, InvalidBoatIdException {
		
		Boat boat = engine.createBoat(PLAYER_BOAT_0, 21, 21, Engine.DIRECTION_RIGHT);
	}
	
	@Test
	public void boatWithDirectionRightAreCorrectlyDrawed() {
		Boat boat = this.createBoat(PLAYER_BOAT_0, 3, 3, Engine.DIRECTION_RIGHT);
		
		Assert.assertEquals("Boat on the grid is not the correct boat", boat, grid.get(2, 3).getBoat());
		Assert.assertEquals("Boat on the grid is not the correct boat", boat, grid.get(3, 3).getBoat());
		Assert.assertEquals("Boat on the grid is not the correct boat", boat, grid.get(4, 3).getBoat());
	}
	
	@Test
	public void boatWithDirectionLeftAreCorrectlyDrawed() {
		Boat boat = this.createBoat(PLAYER_BOAT_0, 4, 6, Engine.DIRECTION_LEFT);
		
		Assert.assertEquals("Boat is not correctly drawed", boat, grid.get(5, 6).getBoat());
		Assert.assertEquals("Boat is not correctly drawed", boat, grid.get(4, 6).getBoat());
		Assert.assertEquals("Boat is not correctly drawed", boat, grid.get(3, 6).getBoat());
	}
	
	@Test
	public void boatWithDirectionUpLeftAreCorrectlyDrawedOnEvenCase() {
		Boat boat = this.createBoat(PLAYER_BOAT_0, 6, 3, Engine.DIRECTION_UP_LEFT);
		
		Assert.assertEquals("Boat is not correctly drawed", boat, grid.get(6, 3).getBoat());
		Assert.assertEquals("Boat is not correctly drawed", boat, grid.get(6, 2).getBoat());
		Assert.assertEquals("Boat is not correctly drawed", boat, grid.get(7, 4).getBoat());
	}
	
	@Test
	public void boatWithDirectionUpLeftAreCorrectlyDrawedOnOddCase() {
		Boat boat = this.createBoat(PLAYER_BOAT_0, 6, 4, Engine.DIRECTION_UP_LEFT);
		
		Assert.assertEquals("Boat is not correctly drawed", boat, grid.get(6, 4).getBoat());
		Assert.assertEquals("Boat is not correctly drawed", boat, grid.get(5, 3).getBoat());
		Assert.assertEquals("Boat is not correctly drawed", boat, grid.get(6, 5).getBoat());
	}
	
	@Test
	public void boatWithDirectionDownRightAreCorrectlyDrawedOnEvenCase() {
		Boat boat = this.createBoat(PLAYER_BOAT_0, 6, 3, Engine.DIRECTION_DOWN_RIGHT);
		
		Assert.assertEquals("Boat is not correctly drawed", boat, grid.get(6, 3).getBoat());
		Assert.assertEquals("Boat is not correctly drawed", boat, grid.get(6, 2).getBoat());
		Assert.assertEquals("Boat is not correctly drawed", boat, grid.get(7, 4).getBoat());
	}
	
	@Test
	public void boatWithDirectionDownRightAreCorrectlyDrawedOnOddCase() {
		Boat boat = this.createBoat(PLAYER_BOAT_0, 6, 4, Engine.DIRECTION_DOWN_RIGHT);
		
		Assert.assertEquals("Boat is not correctly drawed", boat, grid.get(6, 4).getBoat());
		Assert.assertEquals("Boat is not correctly drawed", boat, grid.get(5, 3).getBoat());
		Assert.assertEquals("Boat is not correctly drawed", boat, grid.get(6, 5).getBoat());
	}
	
	@Test
	public void boatWithDirectionUpRightAreCorrectlyDrawedOnEvenCase() {
		Boat boat = this.createBoat(PLAYER_BOAT_0, 6, 3, Engine.DIRECTION_UP_RIGHT);
		
		Assert.assertEquals("Boat is not correctly drawed", boat, grid.get(6, 3).getBoat());
		Assert.assertEquals("Boat is not correctly drawed", boat, grid.get(7, 2).getBoat());
		Assert.assertEquals("Boat is not correctly drawed", boat, grid.get(6, 4).getBoat());
	}
	
	@Test
	public void boatWithDirectionUpRightAreCorrectlyDrawedOnOddCase() {
		Boat boat = this.createBoat(PLAYER_BOAT_0, 6, 4, Engine.DIRECTION_UP_RIGHT);
		
		Assert.assertEquals("Boat is not correctly drawed", boat, grid.get(6, 4).getBoat());
		Assert.assertEquals("Boat is not correctly drawed", boat, grid.get(6, 3).getBoat());
		Assert.assertEquals("Boat is not correctly drawed", boat, grid.get(5, 5).getBoat());
	}
	
	@Test
	public void boatWithDirectionDownLeftAreCorrectlyDrawedOnEvenCase() {
		Boat boat = this.createBoat(PLAYER_BOAT_0, 6, 3, Engine.DIRECTION_DOWN_LEFT);
		
		Assert.assertEquals("Boat is not correctly drawed", boat, grid.get(6, 3).getBoat());
		Assert.assertEquals("Boat is not correctly drawed", boat, grid.get(7, 2).getBoat());
		Assert.assertEquals("Boat is not correctly drawed", boat, grid.get(6, 4).getBoat());
	}
	
	@Test
	public void boatWithDirectionDownLeftAreCorrectlyDrawedOnOddCase() {
		Boat boat = this.createBoat(PLAYER_BOAT_0, 6, 4, Engine.DIRECTION_DOWN_LEFT);	
		
		Assert.assertEquals("Boat is not correctly drawed", boat, grid.get(6, 4).getBoat());
		Assert.assertEquals("Boat is not correctly drawed", boat, grid.get(6, 3).getBoat());
		Assert.assertEquals("Boat is not correctly drawed", boat, grid.get(5, 5).getBoat());
	}
	
	@Test 
	public void barrelIsDetectedOnTheGrid(){
		Barrel barrel = this.createBarrel(BARREL_0, 6, 4, RUM_10);
		
		Assert.assertEquals("Barrel is not detected", barrel, grid.get(6, 4).getBarrel());
	}
	
	@Test 
	public void barrelQuantityCanBeQueried(){
		Barrel barrel = this.createBarrel(BARREL_0, 6, 4, RUM_10);
		
		Assert.assertEquals("Barrel is not detected", RUM_10, grid.get(6, 4).getBarrel().getRumQuantity());
	}

	@Test(expected=InvalidRumQuantityLocationException.class)
	public void barrelQuantityShouldBeMoreThanMinimalValue() 
			throws InvalidBarrelLocationException, InvalidRumQuantityLocationException {
		Barrel barrel = engine.createBarrel(BARREL_0, 6, 4, Engine.MIN_RUM_QUANTITY-1);
	}
	
	@Test(expected=InvalidRumQuantityLocationException.class)
	public void barrelQuantityShouldBeLessThanMaximalValue() 
			throws InvalidBarrelLocationException, InvalidRumQuantityLocationException {
		Barrel barrel = engine.createBarrel(BARREL_0, 6, 4, Engine.MAX_RUM_QUANTITY+1);
	}
	
	@Test
	public void boatIsCreatedWithADefaultRumQuantity() {
		Boat boat = this.createBoat(PLAYER_BOAT_0, 6, 4, Engine.DIRECTION_DOWN_LEFT);
		
		Assert.assertEquals("Boat rum quantity is not correct", Engine.DEFAULT_RUM_QUANTITY, engine.getBoat(PLAYER_BOAT_0).getRumQuantity());
	}
	
	@Test(expected=InvalidBoatIdException.class)
	public void boatIdShouldBeUnique() 
			throws InvalidBoatIdException, InvalidBoatLocationException {
		Boat boat = engine.createBoat(PLAYER_BOAT_0, 6, 4, Engine.DIRECTION_DOWN_LEFT);
		Boat secondBoat = engine.createBoat(PLAYER_BOAT_0, 6, 4, Engine.DIRECTION_DOWN_LEFT);
	}
	
	@Test(expected=InvalidBoatIdException.class)
	public void boatIdShouldBeLessThanMaximalBoatNumber() 
			throws InvalidBoatIdException, InvalidBoatLocationException {
		Boat boat = engine.createBoat(Engine.MAX_NUMBER_OF_BOATS, 6, 4, Engine.DIRECTION_DOWN_LEFT);
	}
	
	@Test
	public void boatLostOneRumQuantityByGameTurn() {
		Boat boat = this.createBoat(PLAYER_BOAT_0, 6, 4, Engine.DIRECTION_DOWN_LEFT);

		engine.play();
		
		Assert.assertEquals("Boat rum quantity is not correct", Engine.DEFAULT_RUM_QUANTITY - 1, engine.getBoat(PLAYER_BOAT_0).getRumQuantity());
	}
	
	@Test
	public void boatWithRumAreMarketAsAlive() {
		Boat boat = this.createBoat(PLAYER_BOAT_0, 6, 4, Engine.DIRECTION_DOWN_LEFT);
		
		engine.play();
		
		Assert.assertEquals("Boat shoud be alive", true, engine.getBoat(PLAYER_BOAT_0).isAlive());
	}
	
	@Test
	public void boatWithoutRumAreMarketAsDied() {
		Boat boat = this.createBoat(PLAYER_BOAT_0, 6, 4, Engine.DIRECTION_DOWN_LEFT);
		boat.removeRumQuantity(Engine.DEFAULT_RUM_QUANTITY - 1);

		engine.play();
		
		Assert.assertEquals("Boat rum quantity is not correct", 0, engine.getBoat(PLAYER_BOAT_0).getRumQuantity());
		Assert.assertEquals("Boat should be died", false, engine.getBoat(PLAYER_BOAT_0).isAlive());
		
	}
	
	@Test
	public void boatWithInitialSpeedTo0GoFasterWhenMoveForward() {
		Boat boat = this.createBoat(PLAYER_BOAT_0, 6, 4, Engine.DIRECTION_RIGHT);
		boat.setTarget(grid.get(8, 5));
		
		engine.play();
		
		Assert.assertEquals("Boat speed should be 1", 1, boat.getSpeed());
	}
	
	@Test
	public void boatWithInitialMaxSpeedDontGoFasterWhenMoveForward() {
		Boat boat = this.createBoat(PLAYER_BOAT_0, 6, 4, Engine.DIRECTION_RIGHT);
		boat.setSpeed(Engine.MAX_SPEED);
		boat.setTarget(grid.get(8, 5));
		
		engine.play();
		
		Assert.assertEquals("Boat speed should be 1", 1, boat.getSpeed());
	}
}
