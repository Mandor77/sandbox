package org.ascotte.codingame.wondev.third;

import org.junit.Assert;
import org.junit.Test;

public class PlayerTest {

	@Test
	public void cellCanHeightUp() {
		Cell cell = new Cell();
		
		cell.up();
		
		Assert.assertEquals(cell.getHeight(), 1);
	}
	
	@Test
	public void cellCanHeightDown() {
		Cell cell = new Cell();
		
		cell.down();
		
		Assert.assertEquals(cell.getHeight(), -1);
	}
	
	@Test
	public void cellCantHeightMoreThanMax() {
		Cell cell = new Cell();
		cell.height = Cell.MAX_HEIGHT;
		
		cell.up();
		
		Assert.assertEquals(cell.getHeight(), Cell.MAX_HEIGHT);
	}
	
	@Test
	public void cellCantHeightLessThanMin() {
		Cell cell = new Cell();
		cell.height = Cell.MIN_HEIGHT;
		
		cell.down();
		
		Assert.assertEquals(cell.getHeight(), Cell.MIN_HEIGHT);
	}
}
