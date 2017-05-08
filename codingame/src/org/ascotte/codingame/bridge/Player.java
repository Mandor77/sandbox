package org.ascotte.codingame.bridge;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.ListIterator;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);
        int M = in.nextInt(); // the amount of motorbikes to control
        int V = in.nextInt(); // the minimum amount of motorbikes that must survive
        String L0 = in.next(); // L0 to L3 are lanes of the road. A dot character . represents a safe space, a zero 0 represents a hole in the road.
        String L1 = in.next();
        String L2 = in.next();
        String L3 = in.next();

        // game loop
        while (true) {
            int S = in.nextInt(); // the motorbikes' speed
            for (int i = 0; i < M; i++) {
                int X = in.nextInt(); // x coordinate of the motorbike
                int Y = in.nextInt(); // y coordinate of the motorbike
                int A = in.nextInt(); // indicates whether the motorbike is activated "1" or detroyed "0"
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");


            // A single line containing one of 6 keywords: SPEED, SLOW, JUMP, WAIT, UP, DOWN.
            System.out.println("UP");
        }
    }
}

class Bridge {
	
	final static int NB_LINES = 4;
	final static int NB_CELLS = 10;
	
	HashMap<Integer,ArrayList<Cell>> lines = new HashMap<Integer,ArrayList<Cell>>();
	HashMap<Integer,Moto> motos = new HashMap<Integer,Moto>();
	HashMap<Integer,Moto> diedMotos = new HashMap<Integer,Moto>();
	
	Bridge() {
		for (int lineId = 0; lineId < NB_LINES; lineId++) {
			lines.put(lineId, new ArrayList<Cell>());
			for (int columnId = 0; columnId < NB_CELLS; columnId++) {
				lines.get(lineId).add(new Cell(lineId, columnId));
			}
		}
	}
	
	void addMoto(int motoId, Cell cell) {
		this.motos.put(motoId, new Moto());
		this.motos.get(motoId).setLocation(cell);
	}
	
	ArrayList<Cell> getLine(int lineId) {
		return this.lines.get(lineId);
	}
	
	Moto getMoto(int motoId) {
		return this.motos.get(motoId);
	}
	
	Cell get(int lineId, int columnId) {
		return this.lines.get(lineId).get(columnId);
	}
	
	Cell getMotoLocation(int motoId) {
		return this.motos.get(motoId).getLocation();
	}
	
	int getMotoNumber() {
		return this.motos.size();
	}
	
	int getDiedMotoNumber() {
		return this.diedMotos.size();
	}
	
	void up() {	
		upOrDown(Order.UP);
	}
	
	void down() {
		upOrDown(Order.DOWN);
	}

	void upOrDown(Order order) {
		for (int motoId:motos.keySet()) {
			Moto moto = this.motos.get(motoId);
			Cell cell = moto.getLocation();
			Cell targetCell = null;
			try {
				
				if (Order.UP.equals(order)) {
					targetCell = this.getLine(cell.lineId - 1).get(cell.columnId);
				}
				else if (Order.DOWN.equals(order)) {
					targetCell = this.getLine(cell.lineId + 1).get(cell.columnId);
				}
				
				if (targetCell != null && !targetCell.haveMoto) {
					moto.setLocation(targetCell);
					if(!moto.checkAliveness()) {
						this.killMoto(motoId);
					}
				}
			}
			catch (NullPointerException e) {
				// Do nothing, position is invalid
			}
		}
	}
	
	void killMoto(int motoId) {
		Moto moto = this.motos.get(motoId);
		moto.die();
		this.diedMotos.put(motoId, moto);
		this.motos.remove(motoId);
	}
}

class Moto {
	Cell cell;
	boolean isAlive = true;
	
	void setLocation(Cell cell) {
		if (this.cell != null) {
			this.cell.unregisterMoto();
		}
		this.cell = cell;
		this.cell.registerMoto();
	}
	
	Cell getLocation() {
		return this.cell;
	}
	
	boolean isAlive() {
		return isAlive;
	}
	
	boolean checkAliveness() {
		if (cell.isHole()) {
			return false;
		}
		return true;
	}
	
	void die() {
		this.isAlive = false;
	}
}

class Cell {
	int lineId;
	int columnId;
	boolean haveMoto = false;
	boolean hole = false;
	
	Cell(int lineId, int columnId) {
		this.lineId = lineId;
		this.columnId = columnId;
	}
	
	void registerMoto() {
		this.haveMoto = true;
	}
	
	void unregisterMoto() {
		this.haveMoto = false;
	}
	
	boolean isHole() {
		return hole;
	}
	
	void setHole() {
		this.hole = true;
	}
}

enum Order {
	UP, DOWN;
}