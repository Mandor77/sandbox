package org.ascotte.codingame.kutulu;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Scanner;
import java.util.Set;

class Player {

	static int numTurn = 0;
	
    public static void main(String args[]) {
    	
        Scanner in = new Scanner(System.in);
        Game.width = in.nextInt();
        Game.height = in.nextInt();
        Game.init();

        if (in.hasNextLine()) {
        	in.nextLine();
        }
        List<String> lines = new ArrayList<>();
        for (int i = 0; i < Game.height; i++) {
        	lines.add(in.nextLine());
        }
        Game.setBoard(lines);
        
        int sanityLossLonely = in.nextInt(); // how much sanity you lose every turn when alone, always 3 until wood 1
        int sanityLossGroup = in.nextInt(); // how much sanity you lose every turn when near another player, always 1 until wood 1
        int wandererSpawnTime = in.nextInt(); // how many turns the wanderer take to spawn, always 3 until wood 1
        int wandererLifeTime = in.nextInt(); // how many turns the wanderer is on map after spawning, always 40 until wood 1

        // game loop
        while (true) {
        	// clean
        	Game.wanderers.clear();
        	
            int entityCount = in.nextInt(); // the first given entity corresponds to your explorer
            for (int i = 0; i < entityCount; i++) {
                String entityType = in.next();
                int id = in.nextInt();
                int x = in.nextInt();
                int y = in.nextInt();
                int param0 = in.nextInt();
                int param1 = in.nextInt();
                int param2 = in.nextInt();
                
                
                if (entityType.equals("EXPLORER")) {
                	Action.debug(entityType + "=" + id + "(" + x + "," + y + ")");
                	Game.explorer[id].width = x;
                	Game.explorer[id].height = y;
                	Game.explorer[id].mentalHealth = param0;
                	Game.explorer[id].gameMentalHealth = param0;
                }
                else if (entityType.equals("WANDERER")) {
                	Action.debug(entityType + "=" + id + "(" + x + "," + y + ");" + 
                			"time=" + param0 + ";" +
                			"state="+ param1 + ";" + "\n" + 
                			"target="+ param2);
                	Game.wanderers.put(id, new Wanderer(x, y));
            		Wanderer wanderer = Game.wanderers.get(id);
            		wanderer.width = x;
            		wanderer.height = y;
            		wanderer.gameWidth = x;
            		wanderer.gameHeight = y;
            		wanderer.target = param2;
            		wanderer.gameTarget = param2;
            		wanderer.remainingTime = param0;
            		wanderer.state = param1;
                	/*if (!Game.wanderers.containsKey(id)) {
                		Game.wanderers.put(id, new Wanderer(x, y));
                	}
                	else {
                		Wanderer wanderer = Game.wanderers.get(id);
                		wanderer.width = x;
                		wanderer.height = y;
                		wanderer.gameWidth = x;
                		wanderer.gameHeight = y;
                		wanderer.target = param2;
                		wanderer.gameTarget = param2;
                		wanderer.remainingTime = param0;
                		wanderer.state = param1;
                	}*/
                }
            }

            /*if (numTurn != 0) {
            	Game.next();
            	for (int i = 0; i < Game.explorer.length; i++) {
            		if (Game.explorer[i].mentalHealth != Game.explorer[i].gameMentalHealth) {
            			Action.debug("DESYNC MENTAL HEALTH : " + i + "-" + Game.explorer[i].mentalHealth
            				+ " instead of " + Game.explorer[i].gameMentalHealth);
            		}
            	}
            	for (Wanderer wanderer:Game.wanderers.values()) {
            		if (wanderer.width != wanderer.gameWidth || wanderer.height != wanderer.gameHeight) {
            			Action.debug("DESYNC WANDERER : " + "(" + wanderer.width + "," + wanderer.height + ")"
            					+ " instead of (" + wanderer.gameWidth + "," + wanderer.gameHeight + ")" + 
            					"\n;target=" + wanderer.target);
            		}
            	}
            }
            Action.playWait();
            */
            Game.movePlayer();
            numTurn++;
        }
    }
}

class Game {
	static int[][] board;
	static int[][][] fetchingBoard;
	static int[][][] fetchingSingleBoard;
	
	final static int defaultMentalHealth = 250;
	final static int healthLostByTurn = 3;
	final static int reducedHealthLostByTurn = 1;
	final static int augmentedHealthLostByTurn = 20;
	final static int numberOfCasesToBeReassured = 2;
	final static int numberOfExplorers = 4;
	final static int defaultWidth = 10;
	final static int defaultHeight = 10;
	final static int caseWall = 0;
	final static int caseClassic = 1;
	final static int casePortal = 2;
	final static int UP = 0;
	final static int RIGHT = 1;
	final static int DOWN = 2;
	final static int LEFT = 3;
	final static int MOVES[][] = {{0, -1}, {1, 0}, {0, 1}, {-1, 0}};
	final static int SPAWNING = 0;
	final static int WANDERING = 1;
	final static int TARGET_NOT_DEFINED = -1;
	
	static int width = defaultWidth;
	static int height = defaultHeight;
	static Explorer[] explorer = new Explorer[numberOfExplorers];
	public static Map<Integer, Wanderer> wanderers = new HashMap<>();
	
	// Technical fields
	static int[] lifeToRemoved = new int[numberOfExplorers];

	static void next() {	
		defineTarget();
		moveWanderer();
		removeLife();
	}

	static void setBoard(List<String> lines) {
		
		for (int i = 0; i < lines.size(); i++) {
			char[] characters = lines.get(i).toCharArray();
			for (int j = 0; j < characters.length; j++) {
				char x = characters[j];
				if (x == '.') {
					Game.board[j][i] = Game.caseClassic;
	         	}
	         	else if (x == 'w') {
	         		Game.board[j][i] = Game.casePortal;
	         	}
	         }
		}
		
		Game.createFetchingBoard();
		Game.createFetchingSingleBoard();
	}
	
	static void defineTarget() {
		for (Wanderer wanderer:Game.wanderers.values()) {
			if (wanderer.state == Game.WANDERING) {
				int target = -1;
				int distanceMin = 9999;
				for (int i = 0; i < Game.explorer.length; i++) {
					if (!Game.explorer[i].died){
						int distance = Algorithm.getDistance(
							new int[] {wanderer.width, wanderer.height}, 
							new int[] {Game.explorer[i].width, Game.explorer[i].height});
						if (distance < distanceMin) {
							distanceMin = distance;
							target = i;
						}
					}
				}
				wanderer.target = target;
			}
		}
	}
	
	static int[] recursiveMoveWanderer(int[] explorerPosition, int[] startPosition, 
			int originalMove, int lastMove, int compteur,
			int previousDoneDistance, int selectedValues[]) {
		
		// Pour chaque direction
		for (int i = 0; i < 4; i++) {
			//TODO : different de lastMove
			
			int[] newPosition = Algorithm.getNbCases(startPosition, i, Game.fetchingBoard);
			if (newPosition[2] != 0) {
				int doneDistance = previousDoneDistance + newPosition[2];
				
				// Si on est sur la même ligne ou colonne
				if (Algorithm.isBetween(explorerPosition, startPosition, newPosition)) 
				{
					if (doneDistance < selectedValues[1])
						selectedValues[0] = originalMove;
						selectedValues[1] = doneDistance;
						break;
				}
				
				// Recursivité
				if (compteur < 5) {
					compteur++;
					selectedValues = recursiveMoveWanderer(
							explorerPosition,
							newPosition,
							originalMove,
							i,
							compteur,
							doneDistance,
							selectedValues);
				} else {
					// Sur le dernier on teste la distance
					int distance = Algorithm.getDistance(newPosition, explorerPosition);
					if (distance < selectedValues[3]) {
						selectedValues[2] = originalMove;
						selectedValues[3] = distance;
					}
				}
			}
		}
		
		return selectedValues;
	}
	
	static void moveWanderer() {
		
		for (Wanderer wanderer:Game.wanderers.values()) {
			if (wanderer.target != -1) {
				Explorer explorer = Game.explorer[wanderer.target];
				int[] explorerPosition = new int[] { explorer.width, explorer.height };
				int[] wandererPosition = new int[] { wanderer.width, wanderer.height };
				// Si déjà sur la même position que le joueur
				if (Game.explorer[0].width == wandererPosition[0] && 
						Game.explorer[0].height == wandererPosition[1]) {
					return;
				}
				int selectedValues[] = {-1, 999, -1, 999};
				
				// Pour chaque direction
				for (int i = 0; i < 4;i++) {
					
					// Si le mouvement est possible
					int[] newPosition = Algorithm.getNbCases(wandererPosition, i, Game.fetchingBoard);
					if (newPosition[2] != 0) {
						
						int doneDistance = newPosition[2];
						// Si on est sur la même ligne ou colonne
						if (Algorithm.isBetween(explorerPosition, wandererPosition, newPosition)) 
						{
							selectedValues[0] = i;
							selectedValues[1] = doneDistance;
							break;
						}
						
						// Sinon on tente une recursivite
						selectedValues = recursiveMoveWanderer(explorerPosition, newPosition, i, i, 1, doneDistance, selectedValues);
					}
				}
				
				if (selectedValues[0] != -1) {
					wanderer.width += Game.MOVES[selectedValues[0]][0];
					wanderer.height += Game.MOVES[selectedValues[0]][1];
				} else if (selectedValues[2] != -1) {
					wanderer.width += Game.MOVES[selectedValues[2]][0];
					wanderer.height += Game.MOVES[selectedValues[2]][1];
				}
			}
		}
	}
	
	static void movePlayer() {
		
		Explorer explorer = Game.explorer[0];
		int[] explorerPosition = new int[] { explorer.width, explorer.height };
		int selectedValues[] = {-1, -999};
		
		// Pour chaque direction
		for (int i = 0; i < 4; i++) {
			// Si le mouvement est possible
			int[] newPosition = Algorithm.getNbCases(explorerPosition, i, Game.fetchingSingleBoard);
			if (newPosition[2] != 0) {

				selectedValues = recursiveMovePlayer(explorerPosition, newPosition, i, 
						i, 0, selectedValues);
			}
		}
		
		if (selectedValues[0] != -1) {
			Game.explorer[0].width = explorerPosition[0] + Game.MOVES[selectedValues[0]][0];
			Game.explorer[0].height = explorerPosition[1] + Game.MOVES[selectedValues[0]][1];
			System.err.println("Best mental hearth=" + selectedValues[1]);
			Action.playMove(Game.explorer[0].width, Game.explorer[0].height);
		} else {
			Action.playWait();
		}
		
	}
	
	static int[] recursiveMovePlayer(int[] explorerPosition, int[] startPosition, 
			int originalMove, int lastMove, int compteur, int[] selectedValues) {
		
		// On backup
		Map<Integer, Wanderer> backupWanderers = new HashMap<Integer,Wanderer>();
		for (Entry<Integer, Wanderer> entry:Game.wanderers.entrySet()) {
			backupWanderers.put(entry.getKey(), entry.getValue().clone());
		}
		Explorer[] backupExplorers = new Explorer[Game.explorer.length];
		for (int element = 0; element < Game.explorer.length; element++) {
			backupExplorers[element] = Game.explorer[element].clone();
		}
		
		// On bouge
		Game.explorer[0].width = startPosition[0];
		Game.explorer[0].height = startPosition[1];

		// On simule un tour
		Game.next();
		
		if (compteur < 3) {
			compteur++;
			// Pour chaque direction
			for (int i = 0; i < 4; i++) {
				// Si le mouvement est possible
				int[] newPosition = Algorithm.getNbCases(startPosition, i, Game.fetchingSingleBoard);
				if (newPosition[2] != 0) {

					selectedValues = recursiveMovePlayer(explorerPosition, newPosition, originalMove, 
							i, compteur, selectedValues);
				}
			}
		}
		else {
			Action.debug("Evaluer=" + originalMove + ";" + Game.explorer[0].mentalHealth);
			// On regarde la santé mentale
			if (Game.explorer[0].mentalHealth > selectedValues[1]) {
				selectedValues[1] = Game.explorer[0].mentalHealth;
				selectedValues[0] = originalMove;
			}
		}
		
		compteur++;
		
		// On rollback
		Game.wanderers = backupWanderers;
		Game.explorer = backupExplorers;
		
		return selectedValues;
	}
	
	
	static void removeLife() {
		for (int i = 0; i < Game.lifeToRemoved.length; i++) {
			Game.lifeToRemoved[i] = healthLostByTurn;
		}
		
		Set<Integer> keysToRemove = new HashSet<>();
		// If close to another explorer
		for (int i = 0; i < Game.explorer.length; i++) {
			if (Game.explorer[i].died){ continue;}
			for (int j = i+1; j < Game.explorer.length; j++) {
				if (Game.explorer[j].died){ continue;}
				if (
					Math.abs(Game.explorer[i].width - Game.explorer[j].width) +
					Math.abs(Game.explorer[i].height - Game.explorer[j].height) <= numberOfCasesToBeReassured
				) {
					Game.lifeToRemoved[i] = reducedHealthLostByTurn;
					Game.lifeToRemoved[j] = reducedHealthLostByTurn;
				}
			}

			
			for (Map.Entry<Integer, Wanderer> element:Game.wanderers.entrySet()) {
				Integer key = element.getKey();
				Wanderer wanderer = element.getValue();
				
				if (wanderer.state == Game.WANDERING && 
					wanderer.width == Game.explorer[i].width &&
					wanderer.height == Game.explorer[i].height) {
						Game.lifeToRemoved[i] += augmentedHealthLostByTurn;
						keysToRemove.add(key);
					}
			}
			Game.explorer[i].mentalHealth -= Game.lifeToRemoved[i];
			
			// Check if killed
			if (Game.explorer[i].mentalHealth < 1) {
				Game.explorer[i].mentalHealth = 1;
				Game.explorer[i].died = true;
			}
		}
		for (Integer key:keysToRemove) {
			Game.wanderers.remove(key);
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
        
        Game.wanderers.clear();
	}
	
	static void createFetchingSingleBoard() {
		
		fetchingSingleBoard = new int[Game.width][Game.height][4];
		for (int i = 0; i < Game.width; i++) {
			for (int j = 0; j < Game.height; j++) {
				if (board[i][j] > 0) {
					//Right
					if (Algorithm.getRight(i, j) > 0) {
						fetchingSingleBoard[i][j][Game.RIGHT] = 1;
					}
					//Left
					if (Algorithm.getLeft(i, j) > 0) {
						fetchingSingleBoard[i][j][Game.LEFT] = 1;
					}
					//Up
					if (Algorithm.getUp(i, j) > 0) {
						fetchingSingleBoard[i][j][Game.UP] = 1;
					}
					//Down
					if (Algorithm.getDown(i, j) > 0) {
						fetchingSingleBoard[i][j][Game.DOWN] = 1;
					}
				}
			}
		}
	}

	static void createFetchingBoard() {
	
		fetchingBoard = new int[Game.width][Game.height][4];
		for (int i = 0; i < Game.width; i++) {
			for (int j = 0; j < Game.height; j++) {
				// Si pas un mur
				if (board[i][j] > 0) {
					
					//Right
					int x = i, y = j;
					while (Algorithm.getRight(x, y) > 0) {
						x++;
						if (Algorithm.getUp(x, y) > 0 || Algorithm.getDown(x, y) > 0) {
							break;
						}
					}
					fetchingBoard[i][j][Game.RIGHT] = x - i;
					
					//Left
					x = i; y = j;
					while (Algorithm.getLeft(x, y) > 0) {
						x--;
						if (Algorithm.getUp(x, y) > 0 || Algorithm.getDown(x, y) > 0) {
							break;
						}
					}
					fetchingBoard[i][j][Game.LEFT] = i - x;
					
					//Up
					x = i; y = j;
					while (Algorithm.getUp(x, y) > 0) {
						y--;
						if (Algorithm.getLeft(x, y) > 0 || Algorithm.getRight(x, y) > 0) {
							break;
						}
					}
					fetchingBoard[i][j][Game.UP] = j - y;
					
					//Down
					x = i; y = j;
					while (Algorithm.getDown(x, y) > 0) {
						y++;
						if (Algorithm.getLeft(x, y) > 0 || Algorithm.getRight(x, y) > 0) {
							break;
						}
					}
					fetchingBoard[i][j][Game.DOWN] = y - j;
				}
			}
		}
		
	}
}

class Unit {
	int width;
	int height;
	int gameWidth;
	int gameHeight;
}

class Explorer extends Unit implements Cloneable {
	int mentalHealth = Game.defaultMentalHealth;
	int gameMentalHealth = Game.defaultMentalHealth;
	boolean died = false;
	
	boolean isDied () {
		return mentalHealth <= 0 ? true : false;
	}
	
	void fear() {
		this.mentalHealth -= 3;
	}
	
	public Explorer clone() {
		Explorer obj = null;
		try {
			obj = (Explorer) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return obj;
	}
}

class Minion extends Unit {
	
}

class Wanderer extends Minion implements Cloneable {

	int target = Game.TARGET_NOT_DEFINED;
	int gameTarget = Game.TARGET_NOT_DEFINED;
	int state = Game.SPAWNING;
	int remainingTime = 999;
	
	boolean toKill = false;
	boolean died = false;
	
	public Wanderer(int width, int height) {
		this.width = width;
		this.height = height;
		this.gameWidth = width;
		this.gameHeight = height;
	}
	
	public Wanderer clone() {
		Wanderer obj = null;
		try {
			obj = (Wanderer) super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return obj;
	}
	
}

class Action {
	
	static void playWait() {
		System.out.println("WAIT");
	}
	
	static void playMove(int width, int height) {
		System.out.println("MOVE " + width + " " + height);
	}
	
	static void debug(String message) {
		System.err.println(message);
	}
}

class Algorithm {
		
	static int getRight(int x, int y) {
		if (x+1 < Game.width) { return Game.board[x+1][y]; }
		return 0;
	}
	
	static int getLeft(int x, int y) {
		if (x-1 >= 0) { return Game.board[x-1][y]; }
		return 0;
	}
	
	static int getUp(int x, int y) {
		if (y-1 >= 0 ) { return Game.board[x][y-1]; }
		return 0;
	}
	
	static int getDown(int x, int y) {
		if (y+1 < Game.height) { return Game.board[x][y+1]; }
		return 0;
	}
	
	static boolean isBetween(int[] position, int[] startPosition, int[] endPosition) {
	
		if (position[0] == startPosition[0] && position[0] == endPosition[0]) {
			if (
					(position[1] >= startPosition[1] && position[1] <= endPosition[1])
					||
					(position[1] <= startPosition[1] && position[1] >= endPosition[1])
				) {
				return true;
			}
		}
		else if (position[1] == startPosition[1] && position[1] == endPosition[1]) {
			if (
					(position[0] >= startPosition[0] && position[0] <= endPosition[0])
					||
					(position[0] <= startPosition[0] && position[0] >= endPosition[0])
				) {
				return true;
			}
		}
		
		return false;
	}
	
	static int[] getNbCases(int[] position, int direction, int[][][] board) {
		int x = position[0];
		int y = position[1];
		int nbCases = board[x][y][direction];
		int[] newPosition = {x, y, nbCases};
		if (direction == Game.UP) {
			newPosition[1] -= nbCases;
		}
		else if (direction == Game.RIGHT) {
			newPosition[0] += nbCases;
		}
		else if (direction == Game.DOWN) {
			newPosition[1] += nbCases;
		}
		else if (direction == Game.LEFT) {
			newPosition[0] -= nbCases;
		}
		return newPosition;
	}
	
	static int getDistance(int[] source, int[] target) {
		return Math.abs(target[0] - source[0]) + Math.abs(target[1] - source[1]);
	}
}
