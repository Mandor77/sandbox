package org.ascotte.codingame.kutulu;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Player {

    public static void main(String args[]) {
    	
        Scanner in = new Scanner(System.in);
        Game.width = in.nextInt();
        Game.height = in.nextInt();
        Game.init();
        
        if (in.hasNextLine()) {
            in.nextLine();
        }
        for (int i = 0; i < Game.height; i++) {
            String line = in.nextLine();
        }
        int sanityLossLonely = in.nextInt(); // how much sanity you lose every turn when alone, always 3 until wood 1
        int sanityLossGroup = in.nextInt(); // how much sanity you lose every turn when near another player, always 1 until wood 1
        int wandererSpawnTime = in.nextInt(); // how many turns the wanderer take to spawn, always 3 until wood 1
        int wandererLifeTime = in.nextInt(); // how many turns the wanderer is on map after spawning, always 40 until wood 1

        // game loop
        while (true) {
            int entityCount = in.nextInt(); // the first given entity corresponds to your explorer
            for (int i = 0; i < entityCount; i++) {
                String entityType = in.next();
                int id = in.nextInt();
                int x = in.nextInt();
                int y = in.nextInt();
                int param0 = in.nextInt();
                int param1 = in.nextInt();
                int param2 = in.nextInt();
            }

            // Write an action using System.out.println()
            // To debug: System.err.println("Debug messages...");

            System.out.println("WAIT"); // MOVE <x> <y> | WAIT
        }
    }
}

class Game {
	static int[][] board;
	
	final static int defaultMentalHealth = 250;
	final static int healthLostByTurn = 3;
	final static int reducedHealthLostByTurn = 1;
	final static int augmentedHealthLostByTurn = 20;
	final static int numberOfCasesToBeReassured = 2;
	final static int numberOfExplorers = 4;
	final static int defaultWidth = 10;
	final static int defaultHeight = 10;
	
	static int width = defaultWidth;
	static int height = defaultHeight;
	static Explorer[] explorer = new Explorer[numberOfExplorers];
	public static List<Wanderer> wanderers = new ArrayList<>();
	
	// Technical fields
	static int[] lifeToRemoved = new int[numberOfExplorers];

	static void next() {
		removeLife();
	}
	
	static void removeLife() {
		for (int i = 0; i < Game.lifeToRemoved.length; i++) {
			Game.lifeToRemoved[i] = healthLostByTurn;
		}
		
		// If close to another explorer
		for (int i = 0; i < Game.explorer.length; i++) {
			for (int j = i+1; j < Game.explorer.length; j++) {
				if (
					(Math.abs(Game.explorer[i].width - Game.explorer[j].width) <= numberOfCasesToBeReassured)
					&&
					(Math.abs(Game.explorer[i].height - Game.explorer[j].height) <= numberOfCasesToBeReassured)
				) {
					Game.lifeToRemoved[i] = reducedHealthLostByTurn;
					Game.lifeToRemoved[j] = reducedHealthLostByTurn;
				}
			}
			//TODO : attention peut-on avoir plusieurs explorateurs sur la meme case ?
			Wanderer wandererToRemove = null;
			for (Wanderer wanderer:Game.wanderers) {
				if (wanderer.width == Game.explorer[i].width &&
					wanderer.height == Game.explorer[i].height) {
						Game.lifeToRemoved[i] = augmentedHealthLostByTurn;
						wandererToRemove = wanderer;
						break;
					}
			}
			if (wandererToRemove != null) { Game.wanderers.remove(wandererToRemove); }
			Game.explorer[i].mentalHealth -= Game.lifeToRemoved[i];
		}
	}
	
	static void init() {
        Game.board = new int[Game.width][Game.height];
        for (int i = 0; i < Game.explorer.length; i++) {
        	Game.explorer[i] = new Explorer();
        }
        Game.explorer[0].width = 0;
        Game.explorer[0].height = 0;
        Game.explorer[1].width = Game.width-1;
        Game.explorer[1].height = 0;
        Game.explorer[2].width = 0;
        Game.explorer[2].height = Game.height-1;
        Game.explorer[3].width = Game.width-1;
        Game.explorer[3].height = Game.height-1;
	}
}

class Unit {
	int width;
	int height;
}

class Explorer extends Unit {
	int mentalHealth = Game.defaultMentalHealth;
	
	boolean isDied () {
		return mentalHealth <= 0 ? true : false;
	}
	
	void fear() {
		this.mentalHealth -= 3;
	}
}

class Minion extends Unit {
	
}

class Wanderer extends Minion {

	public Wanderer(int width, int height) {
		this.width = width;
		this.height = height;
	}
	
}