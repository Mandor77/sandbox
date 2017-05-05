package org.ascotte.codingame.mars;

import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

public class PlayerTest {

	/**
	 * Test a full scenario with one move only
	 */
	@Test
	@Ignore
	public void testPhysicalEngineFirstScenario(){
		Vaisseau vaisseau = createInitialVaisseau();
		
		this.move(vaisseau, -45, Player.MAX_POWER);
		
		Assert.assertEquals("Horizontal value is wrong", 4950, vaisseau.position.x);
		Assert.assertEquals("Vertical value is wrong", 2498, vaisseau.position.y);
		Assert.assertEquals("hSpeed value is wrong", -51, vaisseau.hSpeed, 0);
		Assert.assertEquals("vSpeed value is wrong", -3, vaisseau.vSpeed, 0);
		Assert.assertEquals("Fuel value is wrong", 999, vaisseau.fuel);
		Assert.assertEquals("Rotate value is wrong", 75, vaisseau.rotate);
		Assert.assertEquals("Power value is wrong", 1, vaisseau.power);
	}
	
	@Test
	@Ignore
	public void testPhysicalEngineThirdScenario(){
		Vaisseau vaisseau = new Vaisseau();
		vaisseau.position = new Position();
		vaisseau.position.x = 2500;
		vaisseau.position.y = 2499;
		vaisseau.hSpeed = -0;
		vaisseau.vSpeed = -3;
		vaisseau.fuel = 499;
		vaisseau.rotate = 0;
		vaisseau.power = 1;
		
		this.move(vaisseau, 0, 3);
		
		Assert.assertEquals("Horizontal value is wrong", 2500, vaisseau.position.x);
		Assert.assertEquals("Vertical value is wrong", 2495, vaisseau.position.y);
		Assert.assertEquals("hSpeed value is wrong", 0, vaisseau.hSpeed, 0);
		Assert.assertEquals("vSpeed value is wrong", -4, vaisseau.vSpeed, 0);
		Assert.assertEquals("Fuel value is wrong", 497, vaisseau.fuel);
		Assert.assertEquals("Rotate value is wrong", 0, vaisseau.rotate);
		Assert.assertEquals("Power value is wrong", 2, vaisseau.power);
	}
	
	/**
	 * Test a full scenario with two moves
	 */
	@Test
	@Ignore
	public void testPhysicalEngineSecondScenario(){
		Vaisseau vaisseau = new Vaisseau();
		this.move(vaisseau, -45, Player.MAX_POWER);
		this.move(vaisseau, -45, Player.MAX_POWER);
		
		Assert.assertEquals("Horizontal value is wrong", 4898, vaisseau.position.x);
		Assert.assertEquals("Vertical value is wrong", 2493, vaisseau.position.y);
		Assert.assertEquals("hSpeed value is wrong", -53, vaisseau.hSpeed, 0);
		Assert.assertEquals("vSpeed value is wrong", -6, vaisseau.vSpeed, 0);
		Assert.assertEquals("Fuel value is wrong", 997, vaisseau.fuel);
		Assert.assertEquals("Rotate value is wrong", 60, vaisseau.rotate);
		Assert.assertEquals("Power value is wrong", 2, vaisseau.power);
	}
	
	/**
	 * Test than ship can't go over maximal power
	 */
	
	@Test
	public void testOverMaximalPower(){
		Vaisseau vaisseau = createInitialVaisseau();
		vaisseau.power = Player.MAX_POWER;
		
		this.move(vaisseau, -45, Player.MAX_POWER + 1);
		
		Assert.assertEquals("Power is wrong", Player.MAX_POWER, vaisseau.power);
	}
	
	/**
	 * Test than ship can't go over minimal power
	 */
	@Test
	public void testOverMinimalPower() {
		Vaisseau vaisseau = createInitialVaisseau();
		vaisseau.power = Player.MIN_POWER;
		
		this.move(vaisseau, -45, Player.MIN_POWER - 1);
		
		Assert.assertEquals("Power is wrong", Player.MIN_POWER, vaisseau.power);
	}
	
	/**
	 * Test than ship change only power one by one
	 */
	@Test
	public void testOnlyOnePowerUpChange() {
		Vaisseau vaisseau = createInitialVaisseau();
		vaisseau.power = 2;
		
		this.move(vaisseau, 0, 4);
		
		Assert.assertEquals("Fuel is wrong", 3, vaisseau.power);
	}
	
	@Test
	public void testOnlyOnePowerDownChange() {
		Vaisseau vaisseau = createInitialVaisseau();
		vaisseau.power = 3;
		
		this.move(vaisseau, 0, 1);
		
		Assert.assertEquals("Fuel is wrong", 2, vaisseau.power);
	}
	
	/**
	 * Test than ship can't go over maximal authorized rotation
	 */
	@Test
	public void testOverMaximalRotation() {
		Vaisseau vaisseau = createInitialVaisseau();
		vaisseau.rotate = 0;
		int initialRotate = vaisseau.rotate;
		
		this.move(vaisseau, initialRotate + Player.MAX_ROTATION + 1, vaisseau.power);
		
		Assert.assertEquals("Rotation is wrong", initialRotate + Player.MAX_ROTATION, vaisseau.rotate);
	}
	
	/**
	 * Test than ship can't go over minimal authorized rotation
	 */
	@Test
	public void testOverMinimalRotation() {
		Vaisseau vaisseau = createInitialVaisseau();
		vaisseau.rotate = 0;
		int initialRotate = vaisseau.rotate;
		
		this.move(vaisseau, initialRotate + Player.MIN_ROTATION - 1, vaisseau.power);
		
		Assert.assertEquals("Rotation is wrong", initialRotate + Player.MIN_ROTATION, vaisseau.rotate);
	}
	
	/**
	 *  Test than ship rotate by less than maximal authorized rotation
	 */
	@Test
	public void testRotation() {
		Vaisseau vaisseau = createInitialVaisseau();
		vaisseau.rotate = 0;
		int initialRotate = vaisseau.rotate;
		int rotate = 10;
		
		this.move(vaisseau, rotate, vaisseau.power);
		
		Assert.assertEquals("Rotation is wrong", initialRotate + rotate, vaisseau.rotate);
	}
	
	/**
	 * Test than ship can't go over maximal angle
	 */
	@Test
	public void testOverMaximalAngle() {
		Vaisseau vaisseau = createInitialVaisseau();
		vaisseau.rotate = Player.MAX_ANGLE - 1;
		
		this.move(vaisseau, 15, vaisseau.power);
		
		Assert.assertEquals("Angle is wrong", Player.MAX_ANGLE, vaisseau.rotate);
	}
	
	/**
	 * Test than ship can't go over minimal angle
	 */
	@Test
	public void testOverMinimalAngle() {
		Vaisseau vaisseau = createInitialVaisseau();
		vaisseau.rotate = Player.MIN_ANGLE + 1;
		
		this.move(vaisseau, -15, vaisseau.power);
		
		Assert.assertEquals("Angle is wrong", Player.MIN_ANGLE, vaisseau.rotate);
	}
	
	/**
	 * Test than ship can't go over minimal speed
	 */
	@Test
	public void testOverMinimalVerticalSpeed() {
		Vaisseau vaisseau = createInitialVaisseau();
		vaisseau.hSpeed = 0;
		vaisseau.vSpeed = Player.MIN_SPEED;
		vaisseau.rotate = 0;
		vaisseau.power = 0;
		
		this.move(vaisseau, 0, vaisseau.power);
		
		Assert.assertEquals("VSpeed is wrong", Player.MIN_SPEED, vaisseau.vSpeed, 0);
	}
	
	@Test
	/**
	 * Test than ship can't go over maximal speed
	 */
	public void testOverMaximalVerticalSpeed() {
		Vaisseau vaisseau = createInitialVaisseau();
		vaisseau.hSpeed = 0;
		vaisseau.vSpeed = Player.MAX_SPEED;
		vaisseau.rotate = 0;
		vaisseau.power = 4;
		
		this.move(vaisseau, 0, vaisseau.power);
		
		Assert.assertEquals("VSpeed is wrong", Player.MAX_SPEED, vaisseau.vSpeed, 0);
	}
	
	/** 
	 * Test than fuel is correctly used
	 */
	@Test
	public void testFuelIsUsed() {
		Vaisseau vaisseau = createInitialVaisseau();
		vaisseau.power = 2;
		int initialFuel = vaisseau.fuel;
		
		this.move(vaisseau, 0, 3);
		
		Assert.assertEquals("Fuel is wrong", initialFuel - 3, vaisseau.fuel);
	}
	
	/**
	 * Test than moves with only gravity is correct
	 */
	@Test
	public void testMoveWithOnlyGravity() {
		Vaisseau vaisseau = createInitialVaisseau();
		vaisseau.rotate = 0;
		vaisseau.hSpeed = 0;
		vaisseau.vSpeed = -6;
		double initialPositionY = vaisseau.position.y;
		double initialVSpeed = vaisseau.vSpeed;
		this.move(vaisseau, 0, 0);
		this.move(vaisseau, 0, 0);
		
		Assert.assertEquals("Speed with only gravity is not correct", initialVSpeed + Player.G * -2, vaisseau.vSpeed, 0);
		Assert.assertEquals("Position with only gravity is not correct", initialPositionY + initialVSpeed + Player.G * -1 + initialVSpeed + Player.G * -2, vaisseau.position.y, 0);
	}
	
	/**
	 * Test than ship crash if encounter during move an obstacle
	 */
	@Test
	public void testShipCrashWhenEncounterObstacle() {
		Vaisseau vaisseau = createInitialVaisseau();
		vaisseau.rotate = 0;
		vaisseau.hSpeed = 0;
		World world = createInitialWorld();
		world.draw(4999,2499);
		world.draw(5000,2499);
		world.draw(5001,2499);
		
		this.move(vaisseau, 0, 0);
		
		Assert.assertEquals("Ship is not crashed", true, vaisseau.isCrashed);
	}
	
	//TODO : Ajouter les tests sur les depassements de carte
	
	public Vaisseau createInitialVaisseau() {
		Vaisseau vaisseau = new Vaisseau();
		vaisseau.position = new Position();
		vaisseau.position.x = 5000;
		vaisseau.position.y = 2500;
		vaisseau.hSpeed = -50;
		vaisseau.vSpeed = 0;
		vaisseau.fuel = 1000;
		vaisseau.rotate = 90;
		vaisseau.power = 0;
		return vaisseau;
	}
	
	public World createInitialWorld() {
		World world = new World(Player.MAX_X, Player.MAX_Y);
		
		return world;
	}
	
	public void move(Vaisseau vaisseau, int rotate, int power) {
		World world = createInitialWorld();
		
			try {
				Player.move(vaisseau, world, rotate, power);
			} catch (InvalidPositionException e) {
				// TODO Auto-generated catch block
			}
	}
}
