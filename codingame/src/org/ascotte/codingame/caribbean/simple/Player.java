package org.ascotte.codingame.caribbean.simple;

import java.util.*;
import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

    public static void main(String args[]) {
    	Engine engine = new Engine();
        Scanner in = new Scanner(System.in);
        
        // game loop
        while (true) {
        	System.err.println("Nouveau tour " + Engine.TURN);
        	long start = System.nanoTime();
            int myShipCount = in.nextInt(); // the number of remaining ships
            System.err.println("Elapsed time " + (System.nanoTime() - start));
            int entityCount = in.nextInt(); // the number of entities (e.g. ships, mines or cannonballs)
            int boatIds = 0;
            int barrelIds = 0;
            int mineIds = 0;

            for (int i = 0; i < entityCount; i++) {
            	long startEntity = System.nanoTime();
                int entityId = in.nextInt();
                String entityType = in.next();
                int x = in.nextInt();
                int y = in.nextInt();
                
            	int arg1 = in.nextInt();
            	int arg2 = in.nextInt();
            	int arg3 = in.nextInt();
            	int arg4 = in.nextInt();
            	
                if ("SHIP".equals(entityType)) {
                	engine.setBoatX(boatIds, x);
                	engine.setBoatY(boatIds, y);
                	engine.setBoatOwner(boatIds, arg4);
                	engine.setBoatRumQuantity(boatIds, arg3);
                	engine.setBoatSpeed(boatIds, arg2);
                	engine.setBoatDirection(boatIds, arg1);
                	boatIds++;
                }
                else if ("BARREL".equals(entityType)) {
                	engine.setBarrelX(barrelIds, x);
                	engine.setBarrelY(barrelIds, y);
                	engine.setBarrelRumQuantity(barrelIds, arg1);
                	barrelIds++;
                }
                else if ("MINE".equals(entityType)) {
                	engine.setMineX(mineIds, x);
                	engine.setMineY(mineIds, y);
                }
                else if ("CANNONBALL".equals(entityType)) {
                	
                }
            }
            
            // Pour chaque bateau
            for (int boatId = 0; boatId < boatIds; boatId++) {
            	if (engine.getBoatOwner(boatId) == Engine.PLAYER) {
            		int barrelId = engine.chooseClosestBarrel(boatId, barrelIds);
            		engine.moveToBarrel(boatId, barrelId);
            	}	
            }


        System.err.println("Fin du tour " + Engine.TURN);
        Engine.TURN++;
        }
    }
}

class Engine {
	
	final static int WIDTH = 23;
	final static int HEIGHT = 21;
	final static int BOATS = 6;
	final static int TURNS = 220;
	final static int BARRELS = 100;
	final static int MINES = 100;
	final static int PLAYER = 1;
	final static int OPPONENT = 0;
	
	static int TURN = 0;
	
	int grid [][] = new int[WIDTH][HEIGHT];
	
	int boatX [][] = new int [TURNS][BOATS];
	int boatY [][] = new int [TURNS][BOATS];
	int boatOwner [][] = new int [TURNS][BOATS];
	int boatDirection [][] = new int [TURNS][BOATS];
	int boatRumQuantity [][] = new int [TURNS][BOATS];
	int boatSpeed [][] = new int [TURNS][BOATS];
	int barrelX [][] = new int [TURNS][BARRELS];
	int barrelY [][] = new int [TURNS][BARRELS];
	int barrelRumQuantity [][] = new int[TURNS][BARRELS];
	int mineX [][] = new int [TURNS][MINES];
	int mineY [][] = new int [TURNS][MINES];
	
	public void setBarrelRumQuantity(int id, int rumQuantity) {
		this.barrelRumQuantity[TURN][id] = rumQuantity;
	}
	
	public void setBoatX(int id, int x) {
		this.boatX[TURN][id] = x;
	}
	
	public void setMineX(int id, int x) {
		this.mineX[TURN][id] = x;
	}
	
	public void setMineY(int id, int y) {
		this.mineY[TURN][id] = y;
	}
	
	public void setBoatSpeed(int id, int speed) {
		this.boatSpeed[TURN][id] = speed;
	}
	public void setBoatY(int id, int y) {
		this.boatY[TURN][id] = y;
	}
	
	public void setBoatOwner(int id, int owner) {
		this.boatOwner[TURN][id] = owner;
	}
	
	public void setBoatDirection(int id, int direction) {
		this.boatDirection[TURN][id] = direction;
	}
	
	public void setBoatRumQuantity(int id, int rumQuantity) {
		this.boatRumQuantity[TURN][id] = rumQuantity;
	}
	
	public void setBarrelX(int id, int x) {
		this.barrelX[TURN][id] = x;
	}
	
	public void setBarrelY(int id, int y) {
		this.barrelY[TURN][id] = y;
	}
	
	public int getBoatOwner(int id) {
		return this.boatOwner[TURN][id];
	}
	
	public void moveToBarrel(int boatId, int barrelId) {
		int targetX = this.barrelX[TURN][barrelId];
		int targetY = this.barrelY[TURN][barrelId];
		
		Action.move(targetX, targetY);
	}
	
	public static int getNbCases(int x, int y, int xt, int yt) {
		double xa = x-(y-(y%2));
		double za = y;
		double ya = -xa-za;
		double xb = xt-(yt-(yt%2));
		double zb = yt;
		double yb = -xb-zb;
		
		int nbCase = (int)((Math.abs(xa-xb) + Math.abs(ya-yb) + Math.abs(za-zb))/2);
		return nbCase;
	}
	
	public int chooseClosestBarrel(int boatId, int barrelIds) {
		
		int bestBarrelId = 0;
		int bestDistance = 9999;
		if (barrelIds == 0) { return -1; }
		for (int barrelId = 0; barrelId < barrelIds; barrelId++) {
			int distance = getNbCases(this.boatX[TURN][boatId], this.boatY[TURN][boatId],
					this.barrelX[TURN][barrelId], this.barrelY[TURN][barrelId]);
			if (distance < bestDistance) {
				bestDistance = distance;
				bestBarrelId = barrelId;
			}
		}
		
		return bestBarrelId;
	}
	
	public void chooseBestPath(int boatId, int barrelId) {
		int xBoat = this.boatX[TURN][boatId];
		int yBoat = this.boatY[TURN][boatId];
		int xBarrel = this.barrelX[TURN][boatId];
		int yBarrel = this.barrelY[TURN][boatId];
		double x = xBoat-(yBoat-(yBoat%2));
		double z = yBoat;
		double y = -x-z;
		double targetX = xBarrel-(yBarrel-(yBarrel%2));
		double targetZ = yBarrel;
		double targetY = -targetX-targetZ;
	}
	
}

class Action {
	
	public static void move(int x, int y) {
		System.out.println("MOVE " + x + " " + y);
	}
	
	public static void starboard() {
		System.out.println("STARBOARD");
	}
	
	public static void nothing() {
		System.out.println("WAIT");
	}
	
	public static void port() {
		System.out.println("PORT");
	}
	
	public static void faster() {
		System.out.println("FASTER");
	}
	
	public static void slower() {
		System.out.println("SLOWER");
	}
}