package org.ascotte.codingame.code4life;

import java.util.PriorityQueue;

import org.junit.Assert;
import org.junit.Test;

public class PlayerTest {

	@Test
	public void testThanPriorityAreCorrectWhenDiagnosed() {
		Sample sample1 = new Sample(0, Rank.R0);
		Sample sample2 = new Sample(1, Rank.R1);
		sample2.diagnosed = true;
		PriorityQueue<Sample> queue = new PriorityQueue<Sample>();
		queue.add(sample1);
		queue.add(sample2);
		
		Sample bestSample = queue.peek();
		
		Assert.assertEquals(bestSample, sample2);
	}
	
	@Test
	public void testThanPriorityAreCorrectForRank() {
		Sample sample1 = new Sample(0, Rank.R0);
		Sample sample2 = new Sample(1, Rank.R1);
		sample1.diagnosed = true;
		sample2.diagnosed = true;
		PriorityQueue<Sample> queue = new PriorityQueue<Sample>();
		queue.add(sample1);
		queue.add(sample2);
		
		Sample bestSample = queue.peek();
		
		Assert.assertEquals(bestSample, sample1);
	}
	
	@Test
	public void testThanPriorityAreCorrectForTotalCost() {
		Sample sample1 = new Sample(0, Rank.R0);
		Sample sample2 = new Sample(1, Rank.R1);
		sample1.diagnosed = false;
		sample2.diagnosed = false;
		sample1.totalCost = 10;
		sample2.totalCost = 8;
		PriorityQueue<Sample> queue = new PriorityQueue<Sample>();
		queue.add(sample1);
		queue.add(sample2);
		
		Sample bestSample = queue.peek();
		
		Assert.assertEquals(bestSample, sample2);
	}
	
	@Test
	public void testThanPriorityAreCorrectForHealth() {
		Sample sample1 = new Sample(0, Rank.R1);
		Sample sample2 = new Sample(1, Rank.R1);
		sample1.diagnosed = true;
		sample2.diagnosed = true;
		sample1.health = 10;
		sample2.health = 20;
		PriorityQueue<Sample> queue = new PriorityQueue<Sample>();
		queue.add(sample1);
		queue.add(sample2);
		
		Sample bestSample = queue.peek();
		
		Assert.assertEquals(bestSample, sample2);
	}
	
}
