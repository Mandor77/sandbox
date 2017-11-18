package org.ascotte.codingame.meanmax;

import java.util.*;
import java.io.*;
import java.math.*;

class Player {

	final static int WORLD_RADIUS = 6000;
	final static int NB_MAX_TURN = 200;
	final static int REAPER_RADIUS = 400;
	final static double REAPER_MASS = 0.5d;
	final static double REAPER_FRICTION = 0.2d;
	final static int MAX_THROTTLE = 300;
	
	static Game game = new Game();
	
	public static void doWait() {
		System.out.println("WAIT");
	}
	
	public static void doMove(int x, int y, int throttle) {
		System.out.println(x + " " + y + " " + throttle);
	}
	
    public static void main(String args[]) {
        
    	Scanner in = new Scanner(System.in);
    	
    	Reaper playerReaper = game.getPlayerReaper();
    	
        // game loop
        while (true) {
            int myScore = in.nextInt();
            int enemyScore1 = in.nextInt();
            int enemyScore2 = in.nextInt();
            int myRage = in.nextInt();
            int enemyRage1 = in.nextInt();
            int enemyRage2 = in.nextInt();
            int unitCount = in.nextInt();
            for (int i = 0; i < unitCount; i++) {
                int unitId = in.nextInt();
                int unitType = in.nextInt();
                int player = in.nextInt();
                float mass = in.nextFloat();
                int radius = in.nextInt();
                int x = in.nextInt();
                int y = in.nextInt();
                int vx = in.nextInt();
                int vy = in.nextInt();
                int extra = in.nextInt();
                int extra2 = in.nextInt();
                if (unitType == 0 && unitId == 0) {
                	playerReaper.update(x, y, vx, vy);
                	playerReaper.print();
                }
            }

            playerReaper.simulate(0, 0, 10);
            doMove(0, 0, 10);
            doWait();
            doWait();
        }
    }
}

class Game {
	Reaper playerReaper = new Reaper();

	public Reaper getPlayerReaper() {
		return this.playerReaper;
	}
}

class Reaper {
	int x;
	int y;
	int vx;
	int vy;
	
	public void update(int x, int y, int vx, int vy) {
		this.x = x;
		this.y = y;
		this.vx = vx;
		this.vy = vy;
	}
	
	public void simulate(int x, int y, int throttle) {
		double ax = throttle / Player.REAPER_MASS;
		double ay = throttle / Player.REAPER_MASS;
		double adj = Math.abs(x - this.x);
		double opp = Math.abs(y - this.y);
		double hyp = Math.sqrt(Math.pow((x - this.x), 2) + Math.pow((y - this.y), 2));
		double ang = Math.toDegrees(Math.acos(adj / hyp));
		
		ax = ax * Math.cos(Math.toRadians(ang)) * Math.signum(x - this.x);
		ay = ay * Math.sin(Math.toRadians(ang)) * Math.signum(y - this.y);
		
		double vx = this.vx + ax;
		double vy = this.vy + ay; 
		
		double newX = Math.round(this.x + this.vx + ax);
		double newY = Math.round(this.y + this.vy + ay);
		
		vx = Math.round(vx * (1 - Player.REAPER_FRICTION));
		vy = Math.round(vy * (1 - Player.REAPER_FRICTION));
		

		System.err.println("ax = " + ax + "; ay = " + ay);
		System.err.println("adj = " + adj + ";hyp = " + hyp + ";ang = " + ang);
		System.err.println("x = " + newX + ";y = " + newY + ";vx = " + vx + ";vy = " + vy);
	}
	
	public void print() {
		System.err.println("x = " + x + "; y = " + y + "; vx = " + vx + "; vy = " + vy); 
	}
}