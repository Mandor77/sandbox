package org.ascotte.codingame.mars;
import java.util.LinkedList;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

	static Ship ship = new Ship();
	static int MAX_POWER = 4;
	static int MIN_POWER = 0;
	static int MAX_ROTATION = 15;
	static int MIN_ROTATION = -15;
	static int MAX_ANGLE = 90;
	static int MIN_ANGLE = -90;
	static int MAX_SPEED = 500;
	static int MIN_SPEED = -500;
	static int MIN_X = 0;
	static int MAX_X = 7000;
	static int MIN_Y = 0;
	static int MAX_Y = 3000;
	static int MAX_VSPEED_TO_LAND = 40;
	static int MAX_HSPEED_TO_LAND = 20;	
	static double G = 3.711;
	
	static World world = new World(MAX_X, MAX_Y);
	
    public static void main(String args[]) {
       
    	Scanner in = new Scanner(System.in);
    	
    	int surfaceN = in.nextInt(); // the number of points used to draw the surface of Mars.
    	int previousLandX = -1;
        int previousLandY = -1;
        
    	for (int i = 0; i < surfaceN; i++) {
        	int landX = in.nextInt(); // X coordinate of a surface point. (0 to 6999)
            int landY = in.nextInt(); // Y coordinate of a surface point. By linking all the points together in a sequential fashion, you form the surface of Mars.
            
            if (i == 0) { continue; }
            else {
            	if(landY == previousLandY) {
            		world.addCrashSegment(previousLandX, previousLandY, landX, landY);
            	}
            	else {
            		world.addLandSegment(previousLandX, previousLandY, landX, landY);
            	}
            	
            }
            
            previousLandX = landX;
            previousLandY = landY;
        }

        // game loop
        while (true) {
            ship.position.x = in.nextInt();
            ship.position.y = in.nextInt();
            ship.hSpeed = in.nextInt(); // the horizontal speed (in m/s), can be negative.
            ship.vSpeed = in.nextInt(); // the vertical speed (in m/s), can be negative.
            ship.fuel = in.nextInt(); // the quantity of remaining fuel in liters.
            ship.rotate = in.nextInt(); // the rotation angle in degrees (-90 to 90).
            ship.power = in.nextInt(); // the thrust power (0 to 4).


            // rotate power. rotate is the desired rotation angle. power is the desired thrust power.
            System.out.println("-20 3");
        }
        
    }

	public static void move(int rotate, int power) {

		// Power calculation
		power = ship.power + Integer.signum(power-ship.power);
		power = Math.min(Math.max(power, MIN_POWER), MAX_POWER);
		
		// Rotation calculation
		rotate = ship.rotate + (Math.min(Math.abs(rotate), MAX_ROTATION)) * Integer.signum(rotate);
		rotate = Math.min(Math.max(rotate, MIN_ANGLE), MAX_ANGLE);
		
		// Vertical speed calculation
		ship.vSpeed = ship.vSpeed - G + (double)ship.power;
		ship.vSpeed = Math.min(Math.max(ship.vSpeed, MIN_SPEED), MAX_SPEED);
		
		// New vertical location
		double startY = ship.position.y;
		ship.position.y += ship.vSpeed;
		double endY = ship.position.y;
		
		// Check obstacles
		checkEndOfMap(endY);
		checkObstacles(ship.position.x, startY, ship.position.x, endY);
		checkLanding(ship.position.x, startY, ship.position.x, endY);
		
		// Update properties
		ship.power = power;
		ship.rotate = rotate;
		ship.fuel -= power;
	}
	
	public static void checkEndOfMap(double endY) {
		
		if (endY < 0 || endY >= world.y) {
			ship.crash();
		}
	}
	
	public static void checkObstacles(double startX, double startY, double endX, double endY) {
		
		Segment shipSegment = new Segment(startX, startY, endX, endY);
		
		for (Segment worldSegment:world.crashSegments) {
			if (Segment.checkIntersection(shipSegment, worldSegment)){
					ship.crash();
			}
		}
	}
	
	public static void checkLanding(double startX, double startY, double endX, double endY) {
		
		Segment shipSegment = new Segment(startX, startY, endX, endY);
		
		for (Segment worldSegment:world.landSegments) {
			if (Segment.checkIntersection(shipSegment, worldSegment)){
				if (ship.rotate == 0 && Math.abs(ship.hSpeed) < MAX_HSPEED_TO_LAND &&
						Math.abs(ship.vSpeed) < MAX_VSPEED_TO_LAND) {
							ship.land();	
				}
				else {
					ship.crash();
				}
			}
		}
	}
}

class Position {
	double x;
	double y;
	Position(){};
	Position(double x, double y) { this.x = x; this.y = y;}
}

class Ship {
	Position position;
	double hSpeed;
	double vSpeed;
	int fuel;
	int rotate;
	int power;
	boolean isCrashed;
	boolean isLanded;
	
	Ship() {
		position = new Position();
	}
	
	void land() {
		this.isLanded = true;
	}
	
	void crash() {
		this.isCrashed = true;
	}
}

class World {
	int[] platform;
	LinkedList<Segment> crashSegments = new LinkedList<Segment>();
	LinkedList<Segment> landSegments = new LinkedList<Segment>();
	int x;
	int y;
	
	World(int x, int y) {
		this.platform = new int[x];
		this.x = x;
		this.y = y;
	}
	
	void addCrashSegment(int startX, int startY, int endX, int endY) {
		this.crashSegments.add(new Segment(startX, startY, endX, endY));
	}
	
	void addLandSegment(int startX, int startY, int endX, int endY) {
		this.landSegments.add(new Segment(startX, startY, endX, endY));
	}
}

class Segment {
	double startX;
	double startY;
	double endX;
	double endY;
	double a;
	double b;
	
	Segment(double startX, double startY, double endX, double endY) {
		double a = Double.NaN;
		
		if (startX > endX || (startX == endX && startY > endY)) {
			double tempX = startX;
			double tempY = startY;
			startX = endX;
			startY = endY;
			endX = tempX;
			endY = tempY;
		}
		
		if (endX != startX) { a = (endY - startY) / (endX - startX); }
		else { a = Double.POSITIVE_INFINITY; }
		this.b = startY - a * startX;
		this.startX = startX;
		this.endX = endX;
		this.startY = startY;
		this.endY = endY;
		this.a = a;
	}
	
	static boolean checkIntersection(Segment segment1, Segment segment2) {
		double xCollision = 0d;
		double yCollision = 0d;
		
		if (segment1.a == segment2.a) { return false; }
		if (Double.isInfinite(segment1.a)) {
			yCollision = segment2.a * segment1.startX + segment2.b;
			if (yCollision >= segment1.startY && yCollision <= segment1.endY) {
				return true;
			}
		}
		if (Double.isInfinite(segment2.a)) {
			yCollision = segment1.a * segment2.startX + segment1.b;
			if (yCollision >= segment2.startY && yCollision <= segment2.endY) {
				return true;
			}
		}
		
		xCollision = (segment1.b - segment2.b) / (segment2.a - segment1.a);
		if (xCollision >= segment1.startX && xCollision >= segment2.startX &&
				xCollision <= segment1.endX && xCollision <= segment2.endX) {
			return true;
		}
		return false;
	}
}

