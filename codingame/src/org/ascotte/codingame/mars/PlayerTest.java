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
		Ship ship = createInitialShip();
		
		this.move(-45, Player.MAX_POWER);
		
		Assert.assertEquals("Horizontal value is wrong", 4950, ship.x, 0);
		Assert.assertEquals("Vertical value is wrong", 2498, ship.y, 0);
		Assert.assertEquals("hSpeed value is wrong", -51, ship.hSpeed, 0);
		Assert.assertEquals("vSpeed value is wrong", -3, ship.vSpeed, 0);
		Assert.assertEquals("Fuel value is wrong", 999, ship.fuel);
		Assert.assertEquals("Rotate value is wrong", 75, ship.rotate);
		Assert.assertEquals("Power value is wrong", 1, ship.power);
	}
	
	@Test
	@Ignore
	public void testPhysicalEngineThirdScenario(){
		Ship ship = new Ship();
		ship.x = 2500;
		ship.y = 2499;
		ship.hSpeed = -0;
		ship.vSpeed = -3;
		ship.fuel = 499;
		ship.rotate = 0;
		ship.power = 1;
		
		this.move(0, 3);
		
		Assert.assertEquals("Horizontal value is wrong", 2500, ship.x, 0);
		Assert.assertEquals("Vertical value is wrong", 2495, ship.y, 0);
		Assert.assertEquals("hSpeed value is wrong", 0, ship.hSpeed, 0);
		Assert.assertEquals("vSpeed value is wrong", -4, ship.vSpeed, 0);
		Assert.assertEquals("Fuel value is wrong", 497, ship.fuel);
		Assert.assertEquals("Rotate value is wrong", 0, ship.rotate);
		Assert.assertEquals("Power value is wrong", 2, ship.power);
	}
	
	/**
	 * Test a full scenario with two moves
	 */
	@Test
	@Ignore
	public void testPhysicalEngineSecondScenario(){
		Ship ship = new Ship();
		this.move(-45, Player.MAX_POWER);
		this.move(-45, Player.MAX_POWER);
		
		Assert.assertEquals("Horizontal value is wrong", 4898, ship.x, 0);
		Assert.assertEquals("Vertical value is wrong", 2493, ship.y, 0);
		Assert.assertEquals("hSpeed value is wrong", -53, ship.hSpeed, 0);
		Assert.assertEquals("vSpeed value is wrong", -6, ship.vSpeed, 0);
		Assert.assertEquals("Fuel value is wrong", 997, ship.fuel);
		Assert.assertEquals("Rotate value is wrong", 60, ship.rotate);
		Assert.assertEquals("Power value is wrong", 2, ship.power);
	}
	
	/**
	 * Test than ship can't go over maximal power
	 */
	
	@Test
	public void testOverMaximalPower(){
		Ship ship = createInitialShip();
		ship.power = Player.MAX_POWER;
		
		this.move(-45, Player.MAX_POWER + 1);
		
		Assert.assertEquals("Power is wrong", Player.MAX_POWER, ship.power);
	}
	
	/**
	 * Test than ship can't go over minimal power
	 */
	@Test
	public void testOverMinimalPower() {
		Ship ship = createInitialShip();
		ship.power = Player.MIN_POWER;
		
		this.move(-45, Player.MIN_POWER - 1);
		
		Assert.assertEquals("Power is wrong", Player.MIN_POWER, ship.power);
	}
	
	/**
	 * Test than ship change only power one by one
	 */
	@Test
	public void testOnlyOnePowerUpChange() {
		Ship ship = createInitialShip();
		ship.power = 2;
		
		this.move(0, 4);
		
		Assert.assertEquals("Fuel is wrong", 3, ship.power);
	}
	
	@Test
	public void testOnlyOnePowerDownChange() {
		Ship ship = createInitialShip();
		ship.power = 3;
		
		this.move(0, 1);
		
		Assert.assertEquals("Fuel is wrong", 2, ship.power);
	}
	
	/**
	 * Test than ship can't go over maximal authorized rotation
	 */
	@Test
	public void testOverMaximalRotation() {
		Ship ship = createInitialShip();
		ship.rotate = 0;
		int initialRotate = ship.rotate;
		
		this.move(initialRotate + Player.MAX_ROTATION + 1, ship.power);
		
		Assert.assertEquals("Rotation is wrong", initialRotate + Player.MAX_ROTATION, ship.rotate);
	}
	
	/**
	 * Test than ship can't go over minimal authorized rotation
	 */
	@Test
	public void testOverMinimalRotation() {
		Ship ship = createInitialShip();
		ship.rotate = 0;
		int initialRotate = ship.rotate;
		
		this.move(initialRotate + Player.MIN_ROTATION - 1, ship.power);
		
		Assert.assertEquals("Rotation is wrong", initialRotate + Player.MIN_ROTATION, ship.rotate);
	}
	
	/**
	 *  Test than ship rotate by less than maximal authorized rotation
	 */
	@Test
	public void testRotation() {
		Ship ship = createInitialShip();
		ship.rotate = 0;
		int initialRotate = ship.rotate;
		int rotate = 10;
		
		this.move(rotate, ship.power);
		
		Assert.assertEquals("Rotation is wrong", initialRotate + rotate, ship.rotate);
	}
	
	/**
	 * Test than ship can't go over maximal angle
	 */
	@Test
	public void testOverMaximalAngle() {
		Ship ship = createInitialShip();
		ship.rotate = Player.MAX_ANGLE - 1;
		
		this.move(15, ship.power);
		
		Assert.assertEquals("Angle is wrong", Player.MAX_ANGLE, ship.rotate);
	}
	
	/**
	 * Test than ship can't go over minimal angle
	 */
	@Test
	public void testOverMinimalAngle() {
		Ship ship = createInitialShip();
		ship.rotate = Player.MIN_ANGLE + 1;
		
		this.move(-15, ship.power);
		
		Assert.assertEquals("Angle is wrong", Player.MIN_ANGLE, ship.rotate);
	}
	
	/**
	 * Test than ship can't go over minimal speed
	 */
	@Test
	public void testOverMinimalVerticalSpeed() {
		Ship ship = createInitialShip();
		ship.hSpeed = 0;
		ship.vSpeed = Player.MIN_SPEED;
		ship.rotate = 0;
		ship.power = 0;
		
		this.move(0, ship.power);
		
		Assert.assertEquals("VSpeed is wrong", Player.MIN_SPEED, ship.vSpeed, 0);
	}
	
	@Test
	/**
	 * Test than ship can't go over maximal speed
	 */
	public void testOverMaximalVerticalSpeed() {
		Ship ship = createInitialShip();
		ship.hSpeed = 0;
		ship.vSpeed = Player.MAX_SPEED;
		ship.rotate = 0;
		ship.power = 4;
		
		this.move(0, ship.power);
		
		Assert.assertEquals("VSpeed is wrong", Player.MAX_SPEED, ship.vSpeed, 0);
	}
	
	/** 
	 * Test than fuel is correctly used
	 */
	@Test
	public void testFuelIsUsed() {
		Ship ship = createInitialShip();
		ship.power = 2;
		int initialFuel = ship.fuel;
		
		this.move(0, 3);
		
		Assert.assertEquals("Fuel is wrong", initialFuel - 3, ship.fuel);
	}
	
	/**
	 * Test than moves with only gravity is correct
	 */
	@Test
	public void testMoveWithOnlyGravity() {
		Ship ship = createInitialShip();
		ship.rotate = 0;
		ship.hSpeed = 0;
		ship.vSpeed = 0;

		this.move(0, 4);
		this.move(0, 4);
		this.move(0, 0);
		this.move(0, 4);
		this.move(0, 4);
		this.move(0, 0);
		
		Assert.assertEquals("Speed with only gravity is not correct", -11.266, ship.vSpeed, 0.001);
		Assert.assertEquals("Position with only gravity is not correct", 2461.702, ship.y, 0.001);
	}
	
	/**
	 * Test than ship crash if encounter during move an obstacle
	 */
	@Test
	public void testShipCrashWhenEncounterObstacle() {
		Ship ship = createInitialShip();
		ship.rotate = 0;
		ship.hSpeed = 0;
		Player.world.addSegment(4990, 2499,  5010,  2499);
		
		this.move(0, 0);
		
		Assert.assertEquals("Ship is not crashed", true, ship.isCrashed);
	}

	/**
	 * Test than ship land if encounter during move an platform
	 */
	@Test
	public void testShipLandWhenEncounterPlatform() {
		Ship ship = createInitialShip();
		ship.rotate = 0;
		ship.hSpeed = 0;
		Player.world.addSegment(3990, 2499,  6010,  2499);
		
		this.move(0, 0);
		
		Assert.assertEquals("Ship is not landed", true, ship.isLanded);
	}
	
	/**
	 * Test than ship not land if angle is not correct
	 */
	@Test
	public void testShipNotLandWhenAngleIsNotCorrect() {
		Ship ship = createInitialShip();
		ship.rotate = 15;
		ship.hSpeed = 0;
		Player.world.addSegment(3990, 2499,  6010,  2499);
		
		this.move(0, 0);
		
		Assert.assertEquals("Ship should not landed", false, ship.isLanded);
		Assert.assertEquals("Ship should crashed", true, ship.isCrashed);
	}
	
	/**
	 * Test than ship not land if hspeed is not correct
	 */
	@Test
	public void testShipNotLandWhenHSpeedIsNotCorrect() {
		Ship ship = createInitialShip();
		ship.rotate = 0;
		ship.hSpeed = Player.MAX_HSPEED_TO_LAND+1;
		Player.world.addSegment(3990, 2499,  6010,  2499);
		
		this.move(0, 0);
		
		Assert.assertEquals("Ship should not landed", false, ship.isLanded);
		Assert.assertEquals("Ship should crashed", true, ship.isCrashed);
	}
	
	/**
	 * Test than ship not land if vspeed is not correct
	 */
	@Test
	public void testShipNotLandWhenVSpeedIsNotCorrect() {
		Ship ship = createInitialShip();
		ship.rotate = 0;
		ship.hSpeed = 0;
		ship.vSpeed = -Player.MAX_VSPEED_TO_LAND-1;
		Player.world.addSegment(3990, 2499,  6010,  2499);
		
		this.move(0, 0);
		
		Assert.assertEquals("Ship should not landed", false, ship.isLanded);
		Assert.assertEquals("Ship should crashed", true, ship.isCrashed);
	}
	
	/** 
	 * Test than ship crash if going outside the map
	 */
	@Test
	public void testShipCrashWhenOutsideVerticalMap() {
		Ship ship = createInitialShip();
		ship.y = 50;
		ship.vSpeed = -100;
		
		this.move(0, Player.MAX_POWER);
		
		Assert.assertEquals("Ship is not crashed", true, ship.isCrashed);
	}
	
	/**
	 * Test than two parallel segments have no intersection
	 */
	@Test
	public void testParallelSegmentsHaveNoIntersection() {
		Segment segment1 = new Segment(100, 100, 120, 120);
		Segment segment2 = new Segment(100, 120, 120, 140);
		
		boolean haveIntersection = Segment.checkIntersection(segment1, segment2);
		
		Assert.assertEquals("Segments should not have intersection", false, haveIntersection);
	}
	
	/**
	 * Test than two segments have intersection
	 */
	@Test
	public void testSegmentsHaveIntersection() {
		Segment segment1 = new Segment(100, 100, 120, 120);
		Segment segment2 = new Segment(100, 120, 120, 100);
		
		boolean haveIntersection = Segment.checkIntersection(segment1, segment2);
		
		Assert.assertEquals("Segments should have intersection", true, haveIntersection);
	}
	
	/**
	 * Test than two segments have no intersection when out of bounds
	 */
	@Test
	public void testSegmentsHaveIntersectionWhenOutOfBounds() {
		Segment segment1 = new Segment(150, 150, 200, 200);
		Segment segment2 = new Segment(100, 120, 120, 100);
		
		boolean haveIntersection = Segment.checkIntersection(segment1, segment2);
		
		Assert.assertEquals("Segments should not have intersection", false, haveIntersection);
	}
	
	/**
	 * Test than vertical segment have intersection with another segment
	 */
	@Test
	public void testVerticalSegmentHaveIntersectionWithOtherSegment() {
		Segment segment1 = new Segment(200, 300, 200, 0);
		Segment segment2 = new Segment(150, 120, 250, 100);
		
		boolean haveIntersection = Segment.checkIntersection(segment1, segment2);
		
		Assert.assertEquals("Segments should have intersection", true, haveIntersection);
	}
	
	/**
	 * Test than vertical segment have no intersection with another segment
	 */
	@Test
	public void testVerticalSegmentHaveNoIntersectionWithOtherSegment() {
		Segment segment1 = new Segment(200, 300, 200, 150);
		Segment segment2 = new Segment(150, 120, 250, 100);
		
		boolean haveIntersection = Segment.checkIntersection(segment1, segment2);
		
		Assert.assertEquals("Segments should have intersection", false, haveIntersection);
	}
	
	/**
	 * Test than vertical segment have no intersection with another segment
	 */
	@Test
	public void testPerso() {
		Segment segment1 = new Segment(2500, 1238.3556000000005, 2500.0, 1298.8186000000005);
		Segment segment2 = new Segment(1.0, 1.0, 1000.0, 500.0);
		
		boolean haveIntersection = Segment.checkIntersection(segment1, segment2);
		
		Assert.assertEquals("Segments should have intersection", false, haveIntersection);
	}
	
	public Ship createInitialShip() {
		Player.ship = new Ship();
		Player.ship.x = 5000;
		Player.ship.y = 2500;
		Player.ship.hSpeed = -50;
		Player.ship.vSpeed = 0;
		Player.ship.fuel = 1000;
		Player.ship.rotate = 90;
		Player.ship.power = 0;
		return Player.ship;
	}
	
	public void move(int rotate, int power) {
			Player.move(rotate, power, false);
	}
}
