package org.ascotte.codingame.wondev.third;

import java.util.List;

class Player {

}

class Engine {
	
}

class Cell {
	
	final static int MIN_HEIGHT = -1;
	final static int MAX_HEIGHT = 4;
	
	int height = 0;
	
	
	public int getHeight() {
		return this.height;
	}
	
	public void up() {
		this.height = Math.min(++this.height, MAX_HEIGHT);
	}
	
	public void down() {
		this.height = Math.max(--this.height, MIN_HEIGHT);
	}
}

class Pawn {
	
	Cell cell = null;
	
	public void setCell(Cell cell) {
		this.cell = cell;
	}
}


interface Move {
	
}

class Logic {
	
	public double negamax(int depth) {
		if (this.isGameOver() || depth <= 0) {
			return this.eval();
		}
		
		double bestScore = Double.NEGATIVE_INFINITY;
		Move bestMove;
		
		for (Move move:this.getMoves()) {
			this.play(move);
			double score = -1 * negamax(depth - 1);
			this.unplay(move);
			if (score >= bestScore) {
				bestScore = score;
				bestMove = move;
			}
		}
		
		return bestScore;
	}
	
	public boolean isGameOver() {
		return false;
	}
	
	public double eval() {
		return 0d;
	}
	
	public void play(Move move) {
		return;
	}
	
	public void unplay(Move move) {
		return;
	}
	
	public List<Move> getMoves() {
		return null;
	}
}