package org.ascotte.codingame.caribbean;

import java.util.*;

import java.io.*;
import java.math.*;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

	static int nbTurn = 0;
	
    public static void main(String args[]) {
        Scanner in = new Scanner(System.in);

        // create a grid
        Engine engine = new Engine();
        engine.createGrid(Engine.GRID_WIDTH, Engine.GRID_HEIGHT);
        
        while (true) {
            int myShipCount = in.nextInt(); // the number of remaining ships
            int entityCount = in.nextInt(); // the number of entities (e.g. ships, mines or cannonballs)
            for (int i = 0; i < entityCount; i++) {
                int entityId = in.nextInt();
                String entityType = in.next();
                
                int x = in.nextInt();
                int y = in.nextInt();
                int direction = in.nextInt();
                int speed = in.nextInt();
                int rumQuantity = in.nextInt();
                int owner = in.nextInt();
                
                if("SHIP".equals(entityType)) {
                	if (nbTurn == 0) {
                		try {
                			engine.createBoat(entityId, x, y, direction, owner);
                		}
                		catch (InvalidBoatLocationException e) {
                			System.err.println(e.getMessage());
                		}
                		catch (InvalidBoatIdException e) {
                			System.err.println(e.getMessage());
                		}
                	}
                	else {
                		engine.getBoat(entityId).setDirection(direction);
                		engine.getBoat(entityId).setSpeed(speed);
                		engine.getBoat(entityId).setRumQuantity(rumQuantity);
                		engine.setBoatLocation(entityId, x, y);
                	}
                }
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
	final static int MIN_RUM_QUANTITY = 10;
	final static int MAX_RUM_QUANTITY = 20;
	final static int DEFAULT_RUM_QUANTITY = 100;
	final static int MAX_NUMBER_OF_BOATS = 2;
	final static int RUM_QUANTITY_USED_BY_TURN = 1;
	final static int SPEED_0 = 0;
	final static int SPEED_1 = 1;
	final static int SPEED_2 = 2;
	final static int MAX_SPEED = 1;
	final static int OWNER_PLAYER = 0;
	final static int OWNER_OPPONENT = 1;
	
	Grid grid = null;
	
	Boat boats[] = new Boat[MAX_NUMBER_OF_BOATS];
	
    public Grid createGrid(int x, int y) {
    	this.grid = new Grid(x, y);
    	return this.grid;
    }
    
    public Boat createBoat(int id, int x, int y, int direction, int owner) 
    		throws InvalidBoatLocationException, InvalidBoatIdException{
    	if (id >= MAX_NUMBER_OF_BOATS) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("Boat id out of the bounds: id=").append(id);
			throw new InvalidBoatIdException(buffer.toString());
    	}
    	if (boats[id] != null) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("Boat id already exist: id=").append(id);
			throw new InvalidBoatIdException(buffer.toString());
    	}
    	if (x >= GRID_WIDTH || y >= GRID_HEIGHT) { 
			StringBuffer buffer = new StringBuffer();
			buffer.append("Boat location is incorrect x=").append(x).append(" y=").append(y);
			throw new InvalidBoatLocationException(buffer.toString());
    	}
    	Boat boat = new Boat(grid.get(x, y), direction, owner);
    	boat.drawBoat(grid);
    	boats[id] = boat;
    	return boat;
    }
    
    public Barrel createBarrel(int id, int x, int y, int rumQuantity) 
    		throws InvalidBarrelLocationException, InvalidRumQuantityLocationException{
    	Barrel barrel = new Barrel(x, y);
    	barrel.setRumQuantity(rumQuantity);
    	barrel.drawBarrel(grid);
    	return barrel;
    }
    
    public Boat getBoat(int id) {
    	return boats[id];
    }
    
    public Cell getBoatLocation(int id) {
    	Boat boat = boats[id];
		Cell cell = null;
    	if (boat != null) {
    		cell = boat.getLocation();
		}
    	return cell;
    }
    
    public void setBoatLocation(int id, int x, int y) {
    	Boat boat = boats[id];
		Cell location = grid.get(x, y);
		if (boat != null) {
			boat.setLocation(location);
		}
    }
    
    public void play() {
    	boatRumQuantityManagement();
    	boatMoveManagement();
    	boatLifeManagement();
    }
    
    public void boatRumQuantityManagement() {
    	for (int id = 0; id < MAX_NUMBER_OF_BOATS; id++) {
    		Boat boat = boats[id];
    		if (boat != null) {
    			boat.removeRumQuantity(RUM_QUANTITY_USED_BY_TURN);
    		}
    	}
    }
    
    public void boatLifeManagement() {
    	for (int id = 0; id < MAX_NUMBER_OF_BOATS; id++) {
    		Boat boat = boats[id];
    		if (boat != null && boat.getRumQuantity() == 0) {
    			boat.kill();
    		}
    	}
    }
    
    public void boatMoveManagement() {
    	for (int id = 0; id < MAX_NUMBER_OF_BOATS; id++) {
    		Boat boat = boats[id];
    		if (boat != null) {
    			Action action = getAutomaticAction(boat);
    			playAction(action, boat);
    		}
    	}
    }
    
    public void playAction(Action action, Boat boat) {
    	if (action == null) { return;}
    	
    	switch(action) {
    		case FASTER:
    			boat.speedIncrease();
    		break;
    		default:
    		break;
    	}
    }
  
    public Action getAutomaticAction(Boat boat) {
        Cell currentPosition = boat.getLocation();
        Cell targetPosition = boat.getTarget();
        int orientation = boat.getDirection();
        Action action = Action.WAIT;
        
        if (targetPosition == null) { return null; }
        if (currentPosition.equals(targetPosition)) {
            //this.action = Action.SLOWER;
            //return;
        }

        double targetAngle, angleStraight, anglePort, angleStarboard, centerAngle, anglePortCenter, angleStarboardCenter;

        switch (boat.getSpeed()) {
        case SPEED_2:
            //this.action = Action.SLOWER;
            break;
        case SPEED_1:
            // Suppose we've moved first
            currentPosition = grid.getNeighbor(currentPosition, orientation);
            
            /*if (!currentPosition.isInsideMap()) {
                this.action = Action.SLOWER;
                break;
            }*/

            // Target reached at next turn
            /*if (currentPosition.equals(targetPosition)) {
                this.action = null;
                break;
            }*/

            // For each neighbor cell, find the closest to target
            targetAngle = grid.angle(currentPosition, targetPosition);
            angleStraight = Math.min(Math.abs(orientation - targetAngle), 6 - Math.abs(orientation - targetAngle));
            anglePort = Math.min(Math.abs((orientation + 1) - targetAngle), Math.abs((orientation - 5) - targetAngle));
            angleStarboard = Math.min(Math.abs((orientation + 5) - targetAngle), Math.abs((orientation - 1) - targetAngle));

            centerAngle = grid.angle(currentPosition, grid.getCenterPosition());
            anglePortCenter = Math.min(Math.abs((orientation + 1) - centerAngle), Math.abs((orientation - 5) - centerAngle));
            angleStarboardCenter = Math.min(Math.abs((orientation + 5) - centerAngle), Math.abs((orientation - 1) - centerAngle));

            // Next to target with bad angle, slow down then rotate (avoid to turn around the target!)
            /*if (currentPosition.distanceTo(targetPosition) == 1 && angleStraight > 1.5) {
                this.action = Action.SLOWER;
                break;
            }*/

            Integer distanceMin = null;

            // Test forward
            Cell nextPosition = grid.getNeighbor(currentPosition, orientation);
            /*if (nextPosition.isInsideMap()) {
                distanceMin = nextPosition.distanceTo(targetPosition);
                this.action = null;
            }*/

            // Test port
            /*nextPosition = currentPosition.neighbor((orientation + 1) % 6);
            if (nextPosition.isInsideMap()) {
                int distance = nextPosition.distanceTo(targetPosition);
                if (distanceMin == null || distance < distanceMin || distance == distanceMin && anglePort < angleStraight - 0.5) {
                    distanceMin = distance;
                    this.action = Action.PORT;
                }
            }*/

            // Test starboard
            /*nextPosition = currentPosition.neighbor((orientation + 5) % 6);
            if (nextPosition.isInsideMap()) {
                int distance = nextPosition.distanceTo(targetPosition);
                if (distanceMin == null || distance < distanceMin
                        || (distance == distanceMin && angleStarboard < anglePort - 0.5 && this.action == Action.PORT)
                        || (distance == distanceMin && angleStarboard < angleStraight - 0.5 && this.action == null)
                        || (distance == distanceMin && this.action == Action.PORT && angleStarboard == anglePort
                                && angleStarboardCenter < anglePortCenter)
                        || (distance == distanceMin && this.action == Action.PORT && angleStarboard == anglePort
                                && angleStarboardCenter == anglePortCenter && (orientation == 1 || orientation == 4))) {
                    distanceMin = distance;
                    this.action = Action.STARBOARD;
                }
            }*/
            break;
        case SPEED_0:
            // Rotate ship towards target
            targetAngle = grid.angle(currentPosition, targetPosition);
            angleStraight = Math.min(Math.abs(orientation - targetAngle), 6 - Math.abs(orientation - targetAngle));
            anglePort = Math.min(Math.abs((orientation + 1) - targetAngle), Math.abs((orientation - 5) - targetAngle));
            angleStarboard = Math.min(Math.abs((orientation + 5) - targetAngle), Math.abs((orientation - 1) - targetAngle));

            centerAngle = grid.angle(currentPosition, grid.getCenterPosition());
            anglePortCenter = Math.min(Math.abs((orientation + 1) - centerAngle), Math.abs((orientation - 5) - centerAngle));
            angleStarboardCenter = Math.min(Math.abs((orientation + 5) - centerAngle), Math.abs((orientation - 1) - centerAngle));

            Cell forwardPosition = grid.getNeighbor(currentPosition, orientation);

            if (anglePort <= angleStarboard) {
                action = Action.PORT;
            }

            if (angleStarboard < anglePort || angleStarboard == anglePort && angleStarboardCenter < anglePortCenter
                    || angleStarboard == anglePort && angleStarboardCenter == anglePortCenter && (orientation == 1 || orientation == 4)) {
                action = Action.STARBOARD;
            }

            if (/*forwardPosition.isInsideMap() && */angleStraight <= anglePort && angleStraight <= angleStarboard) {
                action = Action.FASTER;
            }
            break;
        }
        return action;
    }
}

class Grid {

	private final static int[][] DIRECTIONS_EVEN = new int[][] { { 1, 0 }, { 0, -1 }, { -1, -1 }, { -1, 0 }, { -1, 1 }, { 0, 1 } };
    private final static int[][] DIRECTIONS_ODD = new int[][] { { 1, 0 }, { 1, -1 }, { 0, -1 }, { -1, 0 }, { 0, 1 }, { 1, 1 } };
	Cell[][] cells = null;
	Cell centerPosition = new Cell(Engine.GRID_WIDTH / 2, Engine.GRID_HEIGHT / 2);
	
	public Grid(int x, int y) {
		cells = new Cell[x][y];
		for (int xi = 0; xi < x; xi++) {
			for (int yi = 0; yi < y; yi++) {
				cells[xi][yi] = new Cell(xi, yi);
			}
		}
	}
	
	public Cell getCenterPosition() {
		return this.centerPosition;
	}
	
	public Cell get(int x, int y){
		return cells[x][y];
	}
	
	public Cell getNeighbor(Cell cell, int orientation) {
		int newX, newY;
        if (cell.getY() % 2 == 1) {
            newY = cell.getY() + DIRECTIONS_ODD[orientation][1];
            newX = cell.getX() + DIRECTIONS_ODD[orientation][0];
        } else {
            newY = cell.getY() + DIRECTIONS_EVEN[orientation][1];
            newX = cell.getX() + DIRECTIONS_EVEN[orientation][0];
        }
        return cells[newX][newY];
	}
	
    public double angle(Cell currentPosition, Cell targetPosition) {
        double dy = (targetPosition.getY() - currentPosition.getY()) * Math.sqrt(3) / 2;
        double dx = targetPosition.getX() - currentPosition.getX() + ((currentPosition.getY() - targetPosition.getY()) & 1) * 0.5;
        double angle = -Math.atan2(dy, dx) * 3 / Math.PI;
        if (angle < 0) {
            angle += 6;
        } else if (angle >= 6) {
            angle -= 6;
        }
        return angle;
    }
}

class Cell {
	
	Boat boat = null;
	Barrel barrel = null;
	int x;
	int y;
	
	public Cell(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public int getX() {
		return x;
	}
	
	public int getY() {
		return y;
	}
	
	public Boat getBoat() {
		return this.boat; 
	}
	
	public Barrel getBarrel() {
		return this.barrel;
	}
	
	public boolean containBoat() {
		return boat == null ? false : true;
	}
	
	public void markBoat(Boat boat) {
		this.boat = boat;
	}
	
	public void markBarrel(Barrel barrel) {
		this.barrel = barrel;
	}
}

class Barrel {
	int x;
	int y;
	int rumQuantity;
	
	public Barrel(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void setRumQuantity(int rumQuantity) throws InvalidRumQuantityLocationException {
		if (rumQuantity < Engine.MIN_RUM_QUANTITY || rumQuantity > Engine.MAX_RUM_QUANTITY) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("Bad rum quantity : rumQuantity=").append(rumQuantity);
			throw new InvalidRumQuantityLocationException(buffer.toString());
		}
		this.rumQuantity = rumQuantity;
	}
	
	public void drawBarrel(Grid grid) throws InvalidBarrelLocationException {
		try{
			grid.get(this.x, this.y).markBarrel(this);
		}
		catch (IndexOutOfBoundsException e) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("Bad boat location : x=").append(this.x).append(" y=").append(this.y);
			throw new InvalidBarrelLocationException(buffer.toString());
		}
	}
	
	public int getRumQuantity() {
		return this.rumQuantity;
	}
}

class Boat {
	Cell location;
	Cell target;
	int direction;
	int rumQuantity;
	boolean isAlive;
	int speed;
	int owner;
	
	public Boat(Cell location, int direction, int owner) {
		this.location = location;
		this.direction = direction;
		this.rumQuantity = Engine.DEFAULT_RUM_QUANTITY;
		this.isAlive = true;
		this.speed = Engine.SPEED_0;
		this.owner = owner;
	}
	
	public int getOwner() {
		return this.owner;
	}
	
	public int getSpeed() {
		return this.speed;
	}
	
	public void setSpeed(int speed) {
		this.speed = speed;
	}
	
	public int getDirection() {
		return this.direction;
	}
	
	public Cell getTarget() {
		return target;
	}
	
	public Cell getLocation() {
		return location;
	}
	
	public void setLocation(Cell location) {
		this.location = location;
	}
	
	public void kill() {
		this.isAlive = false;
	}
	
	public int getRumQuantity() {
		return this.rumQuantity;
	}
	
	public void speedIncrease() {
		this.speed++;
	}
	
	public void setRumQuantity(int rumQuantity) {
		this.rumQuantity = rumQuantity;
	}
	
	public void setDirection(int direction) {
		this.direction = direction;
	}
	
	public void removeRumQuantity(int rumQuantityToRemove) {
		this.rumQuantity = Math.max(0, this.rumQuantity - rumQuantityToRemove);
	}
	
	public boolean isAlive() {
		return this.isAlive;
	}
	
	public void setTarget(Cell target) {
		this.target = target;
	}
	
	public void drawBoat(Grid grid) throws InvalidBoatLocationException {
		try{ 
			location.markBoat(this);
			switch(direction) {
			case Engine.DIRECTION_RIGHT: 
				grid.get(location.getX() - 1, location.getY()).markBoat(this);
				grid.get(location.getX() + 1, location.getY()).markBoat(this);
			break;
			case Engine.DIRECTION_LEFT:
				grid.get(location.getX() - 1, location.getY()).markBoat(this);
				grid.get(location.getX() + 1, location.getY()).markBoat(this);
			break;
			case Engine.DIRECTION_UP_LEFT:
				if (location.getY()%2 == 0) {
					grid.get(location.getX()-1, location.getY()-1).markBoat(this);
					grid.get(location.getX(), location.getY()+1).markBoat(this);
				}
				else {
					grid.get(location.getX(), location.getY()-1).markBoat(this);
					grid.get(location.getX()+1, location.getY()+1).markBoat(this);	
				}
			break;
			case Engine.DIRECTION_DOWN_RIGHT:
				if (location.getY()%2 == 0) {
					grid.get(location.getX()-1, location.getY()-1).markBoat(this);
					grid.get(location.getX(), location.getY()+1).markBoat(this);
				}
				else {
					grid.get(location.getX(), location.getY()-1).markBoat(this);
					grid.get(location.getX()+1, location.getY()+1).markBoat(this);	
				}
			break;
			case Engine.DIRECTION_UP_RIGHT:
				if (location.getY()%2 == 0) {
					grid.get(location.getX(), location.getY()-1).markBoat(this);
					grid.get(location.getX()-1, location.getY()+1).markBoat(this);
				}
				else {
					grid.get(location.getX()+1, location.getY()-1).markBoat(this);
					grid.get(location.getX(), location.getY()+1).markBoat(this);	
				}
			break;
			case Engine.DIRECTION_DOWN_LEFT:
				if (location.getY()%2 == 0) {
					grid.get(location.getX(), location.getY()-1).markBoat(this);
					grid.get(location.getX()-1, location.getY()+1).markBoat(this);
				}
				else {
					grid.get(location.getX()+1, location.getY()-1).markBoat(this);
					grid.get(location.getX(), location.getY()+1).markBoat(this);	
				}
			break;
			}
		}
		catch (IndexOutOfBoundsException e) {
			StringBuffer buffer = new StringBuffer();
			buffer.append("Bad boat location : x=").append(location.getX()).append(" y=").append(location.getY())
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

class InvalidBarrelLocationException extends Exception {

	private static final long serialVersionUID = 1L;
	public InvalidBarrelLocationException(String message) {
		super(message);
	}
}

class InvalidRumQuantityLocationException extends Exception {

	private static final long serialVersionUID = 1L;
	public InvalidRumQuantityLocationException(String message) {
		super(message);
	}
}

class InvalidBoatIdException extends Exception {

	private static final long serialVersionUID = 1L;
	public InvalidBoatIdException(String message) {
		super(message);
	}
}

enum Action {
    FASTER, SLOWER, PORT, STARBOARD, FIRE, MINE, WAIT
}

