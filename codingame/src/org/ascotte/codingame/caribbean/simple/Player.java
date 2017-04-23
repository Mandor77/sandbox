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
                	System.err.println("MINE");
                	engine.setMineX(mineIds, x);
                	engine.setMineY(mineIds, y);
                	mineIds++;
                }
                else if ("CANNONBALL".equals(entityType)) {
                	if (arg2 <= 2) {
                		engine.setMineX(mineIds, x);
                		engine.setMineY(mineIds, y);
                		mineIds++;
                	}
                }
            }
            
            // Pour chaque bateau
            for (int boatId = 0; boatId < boatIds; boatId++) {
            	if (engine.getBoatOwner(boatId) == Engine.PLAYER) {
            		int barrelId = engine.chooseClosestBarrel(boatId, barrelIds);
            		System.err.println("Locked barril id = " + barrelId);
            		
            		if (barrelId != -1) {
            			//engine.moveToBarrel(boatId, barrelId);
            			engine.chooseBestPath(boatId, barrelId, mineIds, boatIds);
            		}
            		else {
            			engine.chooseBestPath(boatId, barrelId, mineIds, boatIds);
            			// Do something
            		}
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
	final static int CUBES = 20;
	
	static int TURN = 0;
	
	Cube grid [][][] = new Cube[TURNS][WIDTH][HEIGHT];
	
	int boatX [][] = new int [TURNS][BOATS];
	int boatY [][] = new int [TURNS][BOATS];
	int boatOwner [][] = new int [TURNS][BOATS];
	int boatDirection [][] = new int [TURNS][BOATS];
	int boatRumQuantity [][] = new int [TURNS][BOATS];
	int boatSpeed [][] = new int [TURNS][BOATS];
	int barrelX [][] = new int [TURNS][BARRELS];
	int barrelY [][] = new int [TURNS][BARRELS];
	int barrelLock [][] = new int [TURNS][BARRELS];
	int barrelRumQuantity [][] = new int[TURNS][BARRELS];
	int mineX [][] = new int [TURNS][MINES];
	int mineY [][] = new int [TURNS][MINES];
	Cube cubes[] = new Cube[CUBES];
	int boatPreviousMove [][] = new int [TURNS][BOATS];
			
	public Engine() {
		for (int cubeId = 0; cubeId < CUBES; cubeId++) {
			this.cubes[cubeId] = new Cube();
		}
		
		for (int i = 0; i < TURNS; i++) {
			for (int j = 0; j < WIDTH; j++) {
				for (int k = 0; k < HEIGHT; k++) {
					this.grid[i][j][k] = new Cube(j, k);
				}
			}
		}
	}
	
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
	
	public int getNbCases(int x, int y, int xt, int yt) {
		cubes[0].setCube(x, y);
		cubes[1].setCube(xt, yt);
		return CubeOperation.getNbCases(cubes[0], cubes[1]);
	}
	
	public int chooseClosestBarrel(int boatId, int barrelIds) {
		
		int bestBarrelId = 0;
		int bestDistance = 9999;
		if (barrelIds == 0) { return -1; }
		for (int barrelId = 0; barrelId < barrelIds; barrelId++) {
			if (barrelLock[TURN][barrelId] != 1) {
				int distance = getNbCases(this.boatX[TURN][boatId], this.boatY[TURN][boatId],
					this.barrelX[TURN][barrelId], this.barrelY[TURN][barrelId]);
				if (distance < bestDistance) {
					bestDistance = distance;
					bestBarrelId = barrelId;
				}
			}
		}
		
		barrelLock[TURN][bestBarrelId] = 1;
		return bestBarrelId;
	}
	
	public void chooseBestPath(int boatId, int barrelId, int mineIds, int boatIds) {
		Cube source = cubes[0];
		Cube next = cubes[1];
		Cube target = cubes[2];
		Cube nextUp = cubes[3];
		Cube nextAfterStarboardUp = cubes[4];
		Cube nextAfterStarboardDown = cubes[5];
		Cube nextAfterPortUp = cubes[6];
		Cube nextAfterPortDown = cubes[7];
		Cube sourceAfterStarboardUp = cubes[8];
		Cube sourceAfterStarboardDown = cubes[9];
		Cube sourceAfterPortUp = cubes[10];
		Cube sourceAfterPortDown = cubes[11];
		Cube test = cubes[12];
		
		source.setCube(this.boatX[TURN][boatId], this.boatY[TURN][boatId]);
		
		next.setCube(this.boatX[TURN][boatId], this.boatY[TURN][boatId]);
		next.move(this.boatDirection[TURN][boatId]);
		boolean isNextMine = checkMine(next, mineIds);
		
		nextUp.setCube(this.boatX[TURN][boatId], this.boatY[TURN][boatId]);
		nextUp.move(this.boatDirection[TURN][boatId]);
		nextUp.move(this.boatDirection[TURN][boatId]);
		boolean isNextUpMine = checkMine(nextUp, mineIds);
		
		nextAfterPortUp.setCube(this.boatX[TURN][boatId], this.boatY[TURN][boatId]);
		nextAfterPortUp.move(this.boatDirection[TURN][boatId]);
		nextAfterPortUp.move((this.boatDirection[TURN][boatId] + 1)%6);
		boolean isNextAfterPortUpMine = checkMine(nextAfterPortUp, mineIds);
		
		nextAfterPortDown.setCube(this.boatX[TURN][boatId], this.boatY[TURN][boatId]);
		nextAfterPortDown.move(this.boatDirection[TURN][boatId]);
		nextAfterPortDown.move((this.boatDirection[TURN][boatId] + 1 + 3)%6);
		boolean isNextAfterPortDownMine = checkMine(nextAfterPortDown, mineIds);
		
		nextAfterStarboardUp.setCube(this.boatX[TURN][boatId], this.boatY[TURN][boatId]);
		nextAfterStarboardUp.move(this.boatDirection[TURN][boatId]);
		nextAfterStarboardUp.move((this.boatDirection[TURN][boatId] - 1 + 6)%6);
		boolean isNextAfterStarboardUpMine = checkMine(nextAfterStarboardUp, mineIds);
		
		nextAfterStarboardDown.setCube(this.boatX[TURN][boatId], this.boatY[TURN][boatId]);
		nextAfterStarboardDown.move(this.boatDirection[TURN][boatId]);
		nextAfterStarboardDown.move((this.boatDirection[TURN][boatId] - 1 - 3 + 6)%6);
		boolean isNextAfterStarboardDownMine = checkMine(nextAfterStarboardDown, mineIds);
		
		sourceAfterPortUp.setCube(this.boatX[TURN][boatId], this.boatY[TURN][boatId]);
		sourceAfterPortUp.move((this.boatDirection[TURN][boatId] + 1)%6);
		boolean isSourceAfterPortUpMine = checkMine(sourceAfterPortUp, mineIds);
		
		sourceAfterPortDown.setCube(this.boatX[TURN][boatId], this.boatY[TURN][boatId]);
		sourceAfterPortDown.move((this.boatDirection[TURN][boatId] + 1 + 3)%6);
		boolean isSourceAfterPortDownMine = checkMine(sourceAfterPortDown, mineIds);
		
		sourceAfterStarboardUp.setCube(this.boatX[TURN][boatId], this.boatY[TURN][boatId]);
		sourceAfterStarboardUp.move((this.boatDirection[TURN][boatId] - 1 + 6)%6);
		boolean isSourceAfterStarboardUpMine = checkMine(sourceAfterStarboardUp, mineIds);
		
		sourceAfterStarboardDown.setCube(this.boatX[TURN][boatId], this.boatY[TURN][boatId]);
		sourceAfterStarboardDown.move((this.boatDirection[TURN][boatId] - 1 - 3 + 6)%6);
		boolean isSourceAfterStarboardDownMine = checkMine(sourceAfterStarboardDown, mineIds);
		
		if (barrelId != -1) {
			target.setCube(this.barrelX[TURN][barrelId], this.barrelY[TURN][barrelId]);
		}
		else {
			target.setCube(TURN%20, TURN%20);
		}
		
		int directionNextTarget = CubeOperation.getZone(next, target);
		int angleNextTarget = this.boatDirection[TURN][boatId] - directionNextTarget;
		int directionSourceTarget = CubeOperation.getZone(source, target);
		int angleSourceTarget = this.boatDirection[TURN][boatId] - directionSourceTarget;
		
		int distanceSourceTarget = CubeOperation.getNbCases(source, target);
		System.err.println("Direction Next Target = " + directionNextTarget);
		System.err.println("Angle Source Target = " + angleSourceTarget);
		System.err.println("Angle Next Target = " + angleNextTarget);
		System.err.println("Next is a mine = " + isNextUpMine);
		System.err.println("Source X = " + this.boatX[TURN][boatId] + " Y = " + this.boatY[TURN][boatId]);
		System.err.println("Source X = " + source.x + " Y = " + source.y + " Z = " + source.z);
		System.err.println("Next Up X = " + nextUp.x + " Y = " + nextUp.y + " Z = " + nextUp.z);
		System.err.println("Starboard Up X = " + nextAfterStarboardUp.x + " Y = " + nextAfterStarboardUp.y + " Z = " + nextAfterStarboardUp.z);
		System.err.println("Boat direction = " + this.boatDirection[TURN][boatId]);
		printMine(mineIds);
		test.setCube(4, 14);
		System.err.println("Test Up X = " + test.x + " Y = " + test.y + " Z = " + test.z);
		
		// Si la vitesse est nulle
		if (this.boatSpeed[TURN][boatId] == 0) {
			
			// Si le barril est en face et qu'il n'y a pas de mine
			if (angleSourceTarget == 0 && !isNextUpMine) {
				if (this.boatPreviousMove[TURN][boatId] != Action.SLOWER) {
					this.faster(boatId);
					return;
				}
			}
			
			// Si il faut pivoter (starboard)
			if ((angleSourceTarget > 0 && angleSourceTarget <= 3) || (angleSourceTarget <= -3)) {
				if (!isSourceAfterStarboardUpMine && !isSourceAfterStarboardDownMine) {
					if (this.boatPreviousMove[TURN][boatId] != Action.PORT) {
						this.starboard(boatId);
						return;
					}
				}
			}
			else {
				if (!isSourceAfterPortUpMine && !isSourceAfterPortDownMine) {
					if (this.boatPreviousMove[TURN][boatId] != Action.STARBOARD) {
						this.port(boatId);
						return;
					}
				}
			}
			
			// Les mines bloquent le mouvement naturel
			if (!isNextUpMine) {
				if (this.boatPreviousMove[TURN][boatId] != Action.SLOWER) {
					this.faster(boatId);
					return;
				}
			}
			if (!isSourceAfterStarboardUpMine && !isSourceAfterStarboardDownMine) {
				if (this.boatPreviousMove[TURN][boatId] != Action.PORT) {
					this.starboard(boatId);
					return;
				}
			}
			if (!isSourceAfterPortUpMine && !isSourceAfterPortDownMine) {
				if (this.boatPreviousMove[TURN][boatId] != Action.STARBOARD) {
					this.port(boatId);
					return;
				}
			}
			
			// Aucun mouvement n'est possible
			this.faster(boatId);
			return;
		}
		
		// Si la vitesse n'est pas null
		if (this.boatSpeed[TURN][boatId] > 0) {
			
			// Si on est à coté du barril
			if (distanceSourceTarget == 1) {
				this.slower(boatId);
				return;
			}
			
			// Si il y a une mine en face
			if (isNextUpMine) {
				this.slower(boatId);
				return;
			}
			
			// Si il faut aller en face
			if (angleNextTarget == 0 && !isNextUpMine) {
				this.nothing(boatId, boatIds);
				return;
			}
			
			// Si il faut pivoter
			if ((angleNextTarget > 0 && angleNextTarget <= 3) || (angleNextTarget <= -3)) {
				if (!isNextAfterStarboardUpMine && !isNextAfterStarboardDownMine) {
					this.starboard(boatId);
					return;
				}
			}
			else {
				if (!isNextAfterPortUpMine && !isNextAfterPortDownMine) {
					this.port(boatId);
					return;
				}
			}
			
			// Les mines bloquent le mouvement naturel
			if (!isNextUpMine) {
				this.nothing(boatId, boatIds);
				return;
			}
			if (!isNextAfterStarboardUpMine && !isNextAfterStarboardDownMine) {
				this.starboard(boatId);
				return;
			}
			if (!isNextAfterPortUpMine && !isNextAfterPortDownMine) {
				this.port(boatId);
				return;
			}
			
			// Aucun mouvement n'est possible
			this.slower(boatId);
			return;
		}
		
		this.nothing(boatId, boatIds);
		return;
	}
	
	public void slower(int boatId) {
		boatPreviousMove[TURN+1][boatId] = Action.SLOWER;
		Action.slower();
	}
	
	public void faster(int boatId) {
		boatPreviousMove[TURN+1][boatId] = Action.FASTER;
		Action.faster();
	}
	
	public void port(int boatId) {
		boatPreviousMove[TURN+1][boatId] = Action.PORT;
		Action.port();
	}
	
	public void starboard(int boatId) {
		boatPreviousMove[TURN+1][boatId] = Action.STARBOARD;
		Action.starboard();
	}
	
	public void nothing(int boatId, int boatIds) {
		boatPreviousMove[TURN+1][boatId] = Action.WAIT;
		if (!this.fire(boatId, boatIds)) {
			Action.nothing();
		}
		else {
			boatPreviousMove[TURN+1][boatId] = Action.FIRE;
		}
	}
	
	public boolean fire(int boatId, int boatIds) {
		int opponentBoatId = -1;
		for (int id = 0; id < boatIds; id++) {
			if (this.boatOwner[TURN][id] == OPPONENT) {
				opponentBoatId = id;
				break;
			}
		}
		
		if (this.boatSpeed[TURN][opponentBoatId] == 0) {
			Action.fire(this.boatX[TURN][opponentBoatId], this.boatY[TURN][opponentBoatId]);
			return true;
		}
		else {
			Cube source = cubes[13];
			Cube target = cubes[14];
			source.setCube(this.boatX[TURN][boatId], this.boatY[TURN][boatId]);
			target.setCube(this.boatX[TURN][opponentBoatId], this.boatY[TURN][opponentBoatId]);
			for (int i = 1; i < 10; i++) {
				// On le deplace d'une case
				target.move(this.boatDirection[TURN][opponentBoatId]);
				int distanceSourceTarget = CubeOperation.getNbCases(source, target);
				if ((1 + (distanceSourceTarget) / 3) == i) {
					Action.fire(target.getOffsetX(), target.getOffsetY());
					return true;
				}
			}
		}
		
		return false;
	}
	
	
	public boolean checkMine(Cube cube, int minesId) {
		Cube mine = cubes[19];
		for (int mineId = 0; mineId < minesId; mineId++) {
			mine.setCube(this.mineX[TURN][mineId], this.mineY[TURN][mineId]);
			if (CubeOperation.isEquals(mine, cube)) { return true;}
		}
		return false;
	}
	
	public void printMine(int minesId) {
		Cube mine = cubes[19];
		for (int mineId = 0; mineId < minesId; mineId++) {
			mine.setCube(this.mineX[TURN][mineId], this.mineY[TURN][mineId]);
			System.err.println("MINE X=" + mine.x + " Y=" + mine.y + " Z=" + mine.z);
		}
	}
	
	public void registerMineOnMap(int mineId) {
		grid[TURN][this.mineX[TURN][mineId]][this.mineY[TURN][mineId]].block();
	}
	
}

class Action {
	
	final static int WAIT = 0;
	final static int SLOWER = 1;
	final static int FASTER = 2;
	final static int STARBOARD = 3;
	final static int PORT = 4;
	final static int FIRE = 5;
	
	public static void move(int x, int y) {
		System.out.println("MOVE " + x + " " + y);
	}
	
	public static void fire(int x, int y) {
		System.out.println("FIRE " + x + " " + y);
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

class Cube {
	int x;
	int y;
	int z;
	boolean blocked = false;
	
	public Cube() {
		
	}
	
	public Cube(int x, int y) {
		this.setCube(x, y);
	}
	
	public void setCube(int x, int y) {
		this.x = x-(y-(y%2))/2;
		this.z = y;
		this.y = -this.x-this.z;
	}

	public void block() {
		this.blocked = true;
	}
	
	public int getOffsetX() {
		return this.x + (this.z - (this.z%2)) / 2;
	}
	
	public int getOffsetY() {
		return this.z;
	}
	
	public void move(int direction) {
		switch(direction) {
		case CubeOperation.RIGHT:
			this.x++;
			this.y--;
			break;
		case CubeOperation.UP_RIGHT:
			this.x++;
			this.z--;
			break;
		case CubeOperation.UP_LEFT:
			this.y++;
			this.z--;
			break;
		case CubeOperation.LEFT:
			this.y++;
			this.x--;
			break;
		case CubeOperation.DOWN_LEFT:
			this.z++;
			this.x--;
			break;
		case CubeOperation.DOWN_RIGHT:
			this.z++;
			this.y--;
			break;
		}
	}
}

class CubeOperation {
	
	final static int RIGHT = 0;
	final static int UP_RIGHT = 1;
	final static int UP_LEFT = 2;
	final static int LEFT = 3;
	final static int DOWN_LEFT = 4;
	final static int DOWN_RIGHT = 5;
	
	public static int getNbCases(Cube a, Cube b) {
		return (int)((Math.abs(a.x-b.x) + Math.abs(a.y-b.y) + Math.abs(a.z-b.z))/2);
	}
	
	public static int getZone(Cube a, Cube b) {
		// Right
		if (b.z <= a.z && b.x > a.x && b.y < a.y) {
			return RIGHT;
		}
		else if (b.z < a.z && b.x > a.x && b.y >= a.y ) {
			return UP_RIGHT;
		}
		else if (b.z < a.z && b.x <= a.x && b.y > a.y) {
			return UP_LEFT;
		}
		else if (b.z >= a.z && b.x < a.x && b.y > a.y) {
			return LEFT;
		}
		else if (b.z > a.z && b.x < a.x && b.y <= a.y) {
			return DOWN_LEFT;
		}
		else if (b.z > a.z && b.x >= a.x && b.y < a.y) {
			return DOWN_RIGHT;
		}
		return -1;
	}
	
	public static boolean isEquals(Cube a, Cube b) {
		if (a.x == b.x && a.y == b.y && a.z == b.z) {
			return true;
		}
		return false;
	}
}