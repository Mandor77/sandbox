package org.ascotte.codingame.caribbean;

import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        while (true) {
            int myShipCount = in.nextInt(); // the number of remaining ships
            int entityCount = in.nextInt(); // the number of entities (e.g. ships, mines or cannonballs)
            for (int i = 0; i < entityCount; i++) {
                int entityId = in.nextInt();
                String entityType = in.next();
                int x = in.nextInt();
                int y = in.nextInt();
                int arg1 = in.nextInt();
                int arg2 = in.nextInt();
                int arg3 = in.nextInt();
                int arg4 = in.nextInt();
            }
            
            
            for (int i = 0; i < myShipCount; i++) {
                System.out.println("MOVE 11 10"); // Any valid action, such as "WAIT" or "MOVE x y"
            }
        }
    }
}

class Engine {
	
	final static int GRID_WIDTH = 23;
	final static int GRID_HEIGHT = 21;
	final static int DIRECTION_RIGHT = 0;
	final static int DIRECTION_UP_RIGHT = 1;
	final static int DIRECTION_UP_LEFT = 2;
	final static int DIRECTION_LEFT = 3;
	final static int DIRECTION_DOWN_LEFT = 4;
	final static int DIRECTION_DOWN_RIGHT = 5;
	
	Grid grid = null;
	
    public Grid createGrid(int x, int y) {
    	this.grid = new Grid(x, y);
    	return this.grid;
    }
    
    public Boat createBoat(int id, int x, int y, int direction) throws InvalidBoatLocationException{
    	Boat boat = new Boat(x, y, direction);
    	boat.drawBoat(grid);
    	return boat;
    }
}

class Grid {

	Cell[][] cells = null;
	
	public Grid(int x, int y) {
		cells = new Cell[x][y];
		for (int xi = 0; xi < x; xi++) {
			for (int yi = 0; yi < y; yi++) {
				cells[xi][yi] = new Cell();
			}
		}
	}
	
	public Cell get(int x, int y) {
		return cells[x][y];
	}
}

class Cell {
	
	Boat boat = null;
	
	public Boat getBoat() {
		return this.boat; 
	}
	
	public boolean containBoat() {
		return boat == null ? false : true;
	}
	
	public void markBoat(Boat boat) {
		this.boat = boat;
	}
}

class Boat {
	int x;
	int y;
	int direction;
	
	public Boat(int x, int y, int direction) {
		this.x = x;
		this.y = y;
		this.direction = direction;
	}
	
	public void drawBoat(Grid grid) throws InvalidBoatLocationException {
		try{ 
			grid.get(this.x, this.y).markBoat(this);
			switch(direction) {
			case Engine.DIRECTION_RIGHT: 
				grid.get(this.x + 1, this.y).markBoat(this);
				grid.get(this.x + 2, this.y).markBoat(this);
			break;
			case Engine.DIRECTION_LEFT:
				grid.get(this.x - 1, this.y).markBoat(this);
				grid.get(this.x - 2, this.y).markBoat(this);
			break;
			case Engine.DIRECTION_UP_LEFT:
				if (this.y%2 == 0) {
					grid.get(this.x-1, this.y-1).markBoat(this);
					grid.get(this.x-1, this.y-2).markBoat(this);
				}
				else {
					grid.get(this.x, this.y-1).markBoat(this);
					grid.get(this.x-1, this.y-2).markBoat(this);	
				}
			break;
			}
		}
		catch (IndexOutOfBoundsException e) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("Bad boat location : x=").append(this.x).append(" y=").append(this.y)
			.append(" direction=").append(direction);
			throw new InvalidBoatLocationException(buffer.toString());
		}
	}
}

class InvalidBoatLocationException extends Exception {

	private static final long serialVersionUID = 1L;
	public InvalidBoatLocationException(String message) {
		super(message);
	}
	
}