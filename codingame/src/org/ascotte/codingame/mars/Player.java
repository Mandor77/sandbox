package org.ascotte.codingame.mars;
import java.util.Scanner;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Player {

	static Vaisseau vaisseau = new Vaisseau();
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
	static double G = 3.711;
	
    public static void main(String args[]) {
       
    	Scanner in = new Scanner(System.in);
       
    	int surfaceN = in.nextInt(); // the number of points used to draw the surface of Mars.
        int previousLandX = -1;
        int previousLandY = -1;
        int targetStartLandX = -1; 
        int targetEndLandX = -1;
        int targetLandY = -1;
        
        for (int i = 0; i < surfaceN; i++) {
        	int landX = in.nextInt(); // X coordinate of a surface point. (0 to 6999)
            int landY = in.nextInt(); // Y coordinate of a surface point. By linking all the points together in a sequential fashion, you form the surface of Mars.
            if (previousLandY == landY) {
            	targetStartLandX = previousLandX;
            	targetEndLandX = landX;
            	targetLandY = landY;
            }
            previousLandX = landX;
            previousLandY = landY;
        }

        
        
        // game loop
        while (true) {
            vaisseau.position.x = in.nextInt();
            vaisseau.position.y = in.nextInt();
            vaisseau.hSpeed = in.nextInt(); // the horizontal speed (in m/s), can be negative.
            vaisseau.vSpeed = in.nextInt(); // the vertical speed (in m/s), can be negative.
            vaisseau.fuel = in.nextInt(); // the quantity of remaining fuel in liters.
            vaisseau.rotate = in.nextInt(); // the rotation angle in degrees (-90 to 90).
            vaisseau.power = in.nextInt(); // the thrust power (0 to 4).


            // rotate power. rotate is the desired rotation angle. power is the desired thrust power.
            System.out.println("-20 3");
        }
        
    }

	public static void move(Vaisseau vaisseau, World world, int rotate, int power) throws InvalidPositionException {

		// Power calculation
		power = vaisseau.power + Integer.signum(power-vaisseau.power);
		power = Math.min(Math.max(power, MIN_POWER), MAX_POWER);
		
		// Rotation calculation
		rotate = vaisseau.rotate + (Math.min(Math.abs(rotate), MAX_ROTATION)) * Integer.signum(rotate);
		rotate = Math.min(Math.max(rotate, MIN_ANGLE), MAX_ANGLE);
		
		// Vertical speed calculation
		vaisseau.vSpeed = vaisseau.vSpeed - G + (double)vaisseau.power;
		vaisseau.vSpeed = Math.min(Math.max(vaisseau.vSpeed, MIN_SPEED), MAX_SPEED);
		
		// New vertical location
		double initialY = vaisseau.position.y;
		vaisseau.position.y += vaisseau.vSpeed;
		double endY = vaisseau.position.y;
		
		// Check obstacles
		checkObstacles(vaisseau, world, initialY, endY);
		
		// Update properties
		vaisseau.power = power;
		vaisseau.rotate = rotate;
		vaisseau.fuel -= power;
	}
	
	public static void checkObstacles(Vaisseau vaisseau, World world, double initialY, double endY) {
		
		int minY = 0;
		int maxY = 0;
		if (initialY < endY) {
			minY = (int) Math.ceil(initialY);
			maxY = (int) Math.floor(endY);
		} else {
			minY = (int) Math.ceil(endY);
			maxY = (int) Math.floor(initialY);
		}
		
		for(int i = minY; i <= maxY; i++) {
			if (world.platform[(int) vaisseau.position.x][i]) {
				vaisseau.isCrashed = true;
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

class Vaisseau {
	Position position;
	double hSpeed;
	double vSpeed;
	int fuel;
	int rotate;
	int power;
	boolean isCrashed;
	
	Vaisseau() {
		position = new Position();
	}
}

class World {
	boolean[][] platform;
	int x;
	int y;
	
	World(int x, int y) {
		this.platform = new boolean[x][y];
		this.x = x;
		this.y = y;
	}
	
	void draw(int x, int y) {
		// Invalid draw
		if (x < 0 || x > this.x || y < 0 || y > this.y) { return; }
		this.platform[x][y] = true;
		return;
	}
}

class InvalidPositionException extends Exception {
	int x;
	int y;
	public InvalidPositionException(int x, int y) {
		this.x = x;
		this.y = y;
	}
}


