package org.ascotte.codingame.bridge;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

public class PlayerTest {

	Bridge bridge;
	
	@Before
	public void createBridge() {
		bridge = new Bridge();
	}
	
	@Test
	public void oneMotoCanUp() {
		bridge.addMoto(0, bridge.get(1,0));
		
		bridge.up();
		
		Assert.assertEquals("Moto is not correcly located after up", bridge.get(0, 0), bridge.getMotoLocation(0));	
	}
	
	@Test
	public void oneMotoCanDown() {
		bridge.addMoto(0, bridge.get(1,0));
		
		bridge.down();
		
		Assert.assertEquals("Moto is not correcly located after down", bridge.get(2, 0), bridge.getMotoLocation(0));	
	}
	
	@Test
	public void oneMotoCannotUpIfOutOfBounds() {
		bridge.addMoto(0, bridge.get(0, 0));
		
		bridge.up();
		
		Assert.assertEquals("Moto is not correcly located after up", bridge.get(0, 0), bridge.getMotoLocation(0));	
	}
	
	@Test
	public void oneMotoCannotDownIfOutOfBounds() {
		bridge.addMoto(0, bridge.get(3, 0));
		
		bridge.down();
		
		Assert.assertEquals("Moto is not correcly located after down", bridge.get(3, 0), bridge.getMotoLocation(0));	
	}
	
	@Test
	public void twoMotosCanUp() {
		bridge.addMoto(0, bridge.get(1, 0));
		bridge.addMoto(1, bridge.get(2, 0));
		
		bridge.up();
		
		Assert.assertEquals("Moto is not correcly located after up", bridge.get(0, 0), bridge.getMotoLocation(0));
		Assert.assertEquals("Moto is not correcly located after up", bridge.get(1, 0), bridge.getMotoLocation(1));
	}
	
	@Test
	public void twoMotosCanDown() {
		bridge.addMoto(0, bridge.get(1, 0));
		bridge.addMoto(1, bridge.get(2, 0));
		
		bridge.down();
		
		Assert.assertEquals("Moto is not correcly located after down", bridge.get(2, 0), bridge.getMotoLocation(0));
		Assert.assertEquals("Moto is not correcly located after down", bridge.get(3, 0), bridge.getMotoLocation(1));
	}
	
	@Test
	public void twoMotosCannotUpIfOutOfBounds() {
		bridge.addMoto(0, bridge.get(0, 0));
		bridge.addMoto(1, bridge.get(1, 0));
		
		bridge.up();
		
		Assert.assertEquals("Moto is not correcly located after up", bridge.get(0, 0), bridge.getMotoLocation(0));	
		Assert.assertEquals("Moto is not correcly located after up", bridge.get(1, 0), bridge.getMotoLocation(1));	
	}
	
	@Test
	public void threeMotosCanUpWhenNotOutOfBounds() {
		bridge.addMoto(0, bridge.get(0, 0));
		bridge.addMoto(1, bridge.get(1, 0));
		bridge.addMoto(2, bridge.get(3, 0));
		
		bridge.up();
		
		Assert.assertEquals("Moto is not correcly located after up", bridge.get(0, 0), bridge.getMotoLocation(0));	
		Assert.assertEquals("Moto is not correcly located after up", bridge.get(1, 0), bridge.getMotoLocation(1));	
		Assert.assertEquals("Moto is not correcly located after up", bridge.get(2, 0), bridge.getMotoLocation(2));	
	}
	
	@Test
	public void oneMotoDieIfUpIntoAHole() {
		bridge.addMoto(0, bridge.get(1, 0));
		bridge.get(0, 0).setHole();
		Moto moto = bridge.getMoto(0);
		
		bridge.up();
		
		Assert.assertEquals("Moto should be marked as died", moto.isAlive(), false);
	}
	
	@Test
	public void aDiedMotoIsRemovedFromBridge() {
		bridge.addMoto(0, bridge.get(1, 0));
		
		bridge.killMoto(0);
		
		Assert.assertEquals("Moto list should be empty", bridge.getMotoNumber(), 0);
		Assert.assertEquals("Died moto list should not be empty", bridge.getDiedMotoNumber(), 1);
		
	}
}
