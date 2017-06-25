package org.ascotte.codingame.wondev;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Random;
import java.util.Scanner;
import java.util.Stack;

class Utils {
	final static StringBuilder buffer = new StringBuilder();
	
	static void publish(Command command, String... args) {
		buffer.setLength(0);
		buffer.append(command.toString());
		for (String arg:args) {
			buffer.append(" ").append(arg);
		}
		System.out.println(buffer.toString());
	}
	
	static void debug(String... args) {
		buffer.setLength(0);
		for (String arg:args) {
			buffer.append(" ").append(arg);
		}
		System.err.println(buffer.toString());
	}
}

/**
 * Auto-generated code below aims at helping you parse the standard input
 * according to the problem statement.
 **/
class Player {

	final static Game game = new Game();
	final static int PLAYER = 0;
	final static int OPPONENT = 1;
	final static int MAX_SIMULATIONS = 5000;
	final static int SIMULATION_LENGTH = 1;
	final static int MAX_TURN = 400;
	static int NUM_TURN = 0;
	static Statistics[][][] statistics = new Statistics[MAX_TURN][SIMULATION_LENGTH + 1][Game.LEGAL_ACTIONS.length];
	static HashMap<String, List<LegalAction>> actionMap = new HashMap<String, List<LegalAction>>();
	
	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		int gridSize = in.nextInt();
		int nbPawnsByHuman = in.nextInt();

		// init game
		game.initGame(gridSize, nbPawnsByHuman);
		for (int i = 0; i < MAX_TURN; i++) {
			for (int j = 0; j < SIMULATION_LENGTH + 1; j++) {
				for (int k = 0; k < Game.LEGAL_ACTIONS.length; k++) {
					statistics[i][j][k] = new Statistics();
				}
			}
		}
		
		// game loop
		while (true) {
			
			for (int j = 0; j < gridSize; j++) {
				String row = in.next();
				for (int i = 0; i < row.length(); i++) {
					char x = row.charAt(i);
					Cell cell = game.getGrid().getCell(i, j);
					switch (x) {
					case '0': cell.setHeight(0);
						break;
					case '1': cell.setHeight(1);
						break;
					case '2': cell.setHeight(2);
						break;
					case '3': cell.setHeight(3);
						break;
					case '4': cell.setHeight(4);
						break;
					case '.': cell.setHeight(-1);
						break;
					}
				}
			}
			
			for (int i = 0; i < nbPawnsByHuman; i++) {
				int unitX = in.nextInt();
				int unitY = in.nextInt();
				game.setPawnLocation(PLAYER, i, unitX, unitY);
			}
			
			for (int i = 0; i < nbPawnsByHuman; i++) {
				int otherX = in.nextInt();
				int otherY = in.nextInt();
				game.setPawnLocation(OPPONENT, i, otherX, otherY);
			}
			
			//TODO : use it for first iteration
			int legalActions = in.nextInt();
			for (int i = 0; i < legalActions; i++) {
				String atype = in.next();
				int index = in.nextInt();
				String dir1 = in.next();
				String dir2 = in.next();
			}
			
			long debut = System.currentTimeMillis();
			List<LegalAction> legalActionList= new ArrayList<>();
			String key = "" + NUM_TURN + "-";
			legalActionList = game.getLegalActions(PLAYER, key);
			Utils.debug("Nombre actions " + legalActionList.size() + " / " + legalActions);
	
			simulate(legalActionList);
			chooseBest();
			
			long end = System.currentTimeMillis();
			Utils.debug("Duration = " + (end - debut));
			
			for (LegalAction legalAction:legalActionList) {
				Utils.debug("Average " + legalAction.pawnId + " " + legalAction.moveTo + " " + 
			      legalAction.buildTo + " = " + statistics[NUM_TURN][0][legalAction.getId()].getNumberFitness() 
			      + "/" + statistics[NUM_TURN][0][legalAction.getId()].getFitness() 
			      + "/" + statistics[NUM_TURN][0][legalAction.getId()].getAverageFitness());
			}
			
			NUM_TURN++;
		}
	}
	
	public static void simulate(List<LegalAction> initialLegalActionList) {
		Stack<LegalAction> rollbacks = new Stack<>();
		LegalAction rollbackAction;
		
		// Choose and play a random action
		for (int i = 0; i < MAX_SIMULATIONS; i++) {
		    String key = "" + NUM_TURN + "-";
			LegalAction initialLegalAction;
			initialLegalAction = IA.playRandomIA(initialLegalActionList);
			key = key + initialLegalAction.id;
			
			rollbackAction = simulateLegalAction(initialLegalAction);
			rollbacks.push(rollbackAction);
			
			//statistics[NUM_TURN][0][initialLegalAction.getId()].setFitness(game.fitness(PLAYER));
			
			// Play opponent
			LegalAction legalAction;
			legalAction = initialLegalAction;
			/*
			if (initialLegalAction.getChilds() == null) {
				List<LegalAction> opponentLegalActionList = game.getLegalActions(OPPONENT);
				initialLegalAction.setChilds(opponentLegalActionList);
			}
			LegalAction opponentLegalAction = IA.playRandomIA(initialLegalAction.getChilds());
			if (opponentLegalAction != null) {
				LegalAction opponentNextRollbackAction = simulateLegalAction(opponentLegalAction);
				rollbacks.push(opponentNextRollbackAction);
				legalAction = opponentLegalAction;
			}
			else {
				legalAction = initialLegalAction;
				legalAction.setChilds(null);
			}*/
			
			// Then explore
			for (int j = SIMULATION_LENGTH; j > 0; j--) {
				// If childs not defined
				/*if (legalAction.getChilds() == null) {
					List<LegalAction> legalActionList = game.getLegalActions(PLAYER);
					legalAction.setChilds(legalActionList);
				}
				
				LegalAction nextLegalAction = IA.playRandomIA(legalAction.getChilds());
				*/
				
				List<LegalAction> legalActionList = game.getLegalActions(PLAYER, key);
				LegalAction nextLegalAction = IA.playRandomIA(legalActionList);
				if (nextLegalAction == null) { break; }
				key = key + nextLegalAction.id;
				LegalAction nextRollbackAction = simulateLegalAction(nextLegalAction);
				rollbacks.push(nextRollbackAction);
				statistics[NUM_TURN][0][initialLegalAction.getId()].setFitness(game.fitness(PLAYER));
				
				//initialLegalAction.setFitness((game.fitness(PLAYER)));
				
				legalAction = nextLegalAction;
			}
			
			while(!rollbacks.isEmpty()) {
				simulateLegalAction(rollbacks.pop());
			}
		}
	}
	
	public static void chooseBest()
	{
		int bestIndex = 0;		
		for (int index = 0; index < statistics[NUM_TURN][0].length; index++) {
			if (statistics[NUM_TURN][0][index].getFitness() > statistics[NUM_TURN][0][bestIndex].getFitness()) {
				bestIndex = index;
			}
			else if (statistics[NUM_TURN][0][index].getFitness() == statistics[NUM_TURN][0][bestIndex].getFitness()) {
				if (statistics[NUM_TURN][0][index].getAverageFitness() > statistics[NUM_TURN][0][bestIndex].getAverageFitness()) {
					bestIndex = index;
				}
			}
		}
		
		publishLegalAction(Game.LEGAL_ACTIONS[bestIndex]);
	}
	
	public static void publishLegalAction(LegalAction legalAction) {
		Utils.publish(legalAction.getCommand(), String.valueOf(legalAction.getPawnId()), legalAction.getMoveTo().name(), legalAction.getBuildTo().name());
	}
	
	public static LegalAction simulateLegalAction(LegalAction legalAction) {
		
		game.playLegalAction(legalAction);
		LegalAction reverseLegalAction = game.getLegalAction
				(legalAction.getPlayerIndex(), 
					legalAction.getPawnId(), 
					legalAction.getReverseCommand(),
					legalAction.getReverseMoveTo(), 
					legalAction.getBuildTo());
				
		return reverseLegalAction;
	}
}


class Game {
	final Grid grid = new Grid();
	final Human[] humans = new Human[2];
	final static int PLAYER_NUMBER = 2;
	final static int PAWN_NUMBER = 2;
	final static int DIRECTION_NUMBER = 8;
	final static int COMMAND_NUMBER = 2;
	static LegalAction[] LEGAL_ACTIONS = new LegalAction[PLAYER_NUMBER * PAWN_NUMBER * COMMAND_NUMBER * DIRECTION_NUMBER * DIRECTION_NUMBER];
	
	public void initGame(int gridSize, int nbPawnsByHuman) {
		this.grid.createGrid(gridSize);
		for (int i = 0; i < humans.length; i++) {
			this.humans[i] = new Human(nbPawnsByHuman);
		}
		this.createLegalActions();
	}

	public Grid getGrid() {
		return this.grid;
	}
	
	public void setPawnLocation(int playerId, int pawnId, int x, int y) {
		
		// In case of the pawn is not visible
		if (x == -1 || y == -1) { return; }
		
		Cell newLocation = this.grid.getCell(x, y);
		Human human = this.humans[playerId];
		Pawn pawn = human.getPawn(pawnId);
		Cell currentLocation = pawn.getLocation();
		
		// Remove pawn from its current location
		if (currentLocation != null) {
			currentLocation.removePawn();
		}
		
		// Put pawn to its new location
		newLocation.addPawn(pawn);
		pawn.setLocation(newLocation);
	}
	
	public void markScore(int playerId, int pawnId, int score) {
		Human human = this.humans[playerId];
		Pawn pawn = human.getPawn(pawnId);
		if (pawn.getLocation().height == 3) {
			human.addToScore(score);
		}
	}
	
	public List<LegalAction> getLegalActions(int playerId, String key) {
		
		if (Player.actionMap.containsKey(key)) { return Player.actionMap.get(key); }
		
		List<LegalAction> legalActions = new ArrayList<LegalAction>();
		Pawn[] pawns = this.humans[playerId].getPawns();
	
		// For each pawn
		for (int pawnId = 0; pawnId < pawns.length; pawnId++) {
			Pawn pawn = pawns[pawnId];
			// If pawn already out
			if (!pawn.isActive() || pawn.getLocation() == null) { continue; }
			int nbLegalActionForPawn = 0;
			
			Cell currentCell = pawn.getLocation();
			for (int i = 0; i < DIRECTION_NUMBER; i++) {
				Direction moveDirection = Direction.get(i);
				Cell targetCell = this.grid.getNeighbourgCell(currentCell.width, currentCell.length, moveDirection);
				// Move is not possible
				if (targetCell == null || !targetCell.isReachable(currentCell.height)) {
					//Utils.debug("Move not possible " + moveDirection + " " + currentCell.width + " " + currentCell.length);
					continue;
				}
			
				// Move is possible
				for (int j = 0; j < DIRECTION_NUMBER; j++) {
					Direction buildDirection = Direction.get(j);
					Cell buildCell = this.grid.getNeighbourgCell(targetCell.width, targetCell.length, buildDirection);
					// Build is not possible
					if (buildCell == null || !buildCell.isBuildable(pawn)) {
						//Utils.debug("Build not possible " + buildDirection + " " + targetCell.width + " " + targetCell.length);
						continue;
						}
					
					// Build is possible
					legalActions.add(getLegalAction(playerId, pawnId, Command.MOVEBUILD, moveDirection, buildDirection));
					nbLegalActionForPawn++;
				}
			}
			
			// If no action, kill pawn
			if (nbLegalActionForPawn == 0) {
				pawn.inactive();
			}
		}
		
		Player.actionMap.put(key, legalActions);
		return legalActions;
	}
	
	public void playLegalAction(LegalAction legalAction) {
		
		int playerId = legalAction.getPlayerIndex();
		int pawnId = legalAction.getPawnId();
		Pawn pawn = this.humans[playerId].getPawn(pawnId);
		Cell currentCell = pawn.getLocation();
		Cell targetCell = null;
		Cell buildCell = null;
		Direction moveDirection = legalAction.getMoveTo();
		Direction buildDirection = legalAction.getBuildTo();
		switch(legalAction.getCommand()) {
		case MOVEBUILD:
			targetCell = this.grid.getNeighbourgCell(currentCell.width, currentCell.length, moveDirection);
			this.setPawnLocation(playerId, pawnId, targetCell.width, targetCell.length);
			this.markScore(playerId, pawnId, 1);
			buildCell = this.grid.getNeighbourgCell(targetCell.width, targetCell.length, buildDirection);
			if (buildCell == null) {
				Utils.debug(legalAction.toString() + " " + currentCell.width + "/" + currentCell.length);
				Utils.debug(legalAction.toString() + " " + targetCell.width + "/" + targetCell.length);
			}
			buildCell.upHeight();
			break;
		case UNBUILDMOVE:
			buildCell = this.grid.getNeighbourgCell(currentCell.width, currentCell.length, buildDirection);
			buildCell.downHeight();
			this.markScore(playerId, pawnId, -1);
			targetCell = this.grid.getNeighbourgCell(currentCell.width, currentCell.length, moveDirection);
			this.setPawnLocation(playerId, pawnId, targetCell.width, targetCell.length);
			break;
		}
	}
	
	public int fitness(int playerId) {
		Human human = this.humans[playerId];
		Pawn pawn = human.getPawn(0);
		int fitness = 5 * human.getScore() + pawn.getLocation().height;
		return fitness;
	}
	
	public void createLegalActions() {
		for (int playerId = 0; playerId < PLAYER_NUMBER; playerId++) {
			for (int pawnId = 0; pawnId < PAWN_NUMBER; pawnId++) {
				for (int commandId = 0; commandId < COMMAND_NUMBER; commandId++) {
					for (int moveId = 0; moveId < DIRECTION_NUMBER; moveId++) {
						for (int buildId = 0; buildId < DIRECTION_NUMBER; buildId++) {
							int index = playerId * PAWN_NUMBER * COMMAND_NUMBER * DIRECTION_NUMBER * DIRECTION_NUMBER
									+ pawnId * COMMAND_NUMBER * DIRECTION_NUMBER * DIRECTION_NUMBER
									  + commandId * DIRECTION_NUMBER * DIRECTION_NUMBER
									    + moveId * DIRECTION_NUMBER
									      + buildId;
							LEGAL_ACTIONS[index] = new LegalAction(index, Command.get(commandId), playerId, pawnId, Direction.get(moveId), Direction.get(buildId));
						}
					}
				}
			}
		}
	}
	
	public LegalAction getLegalAction(int playerId, int pawnId, Command command, Direction move, Direction build) {
		int index = playerId * PAWN_NUMBER * COMMAND_NUMBER * DIRECTION_NUMBER * DIRECTION_NUMBER
				+ pawnId * COMMAND_NUMBER * DIRECTION_NUMBER * DIRECTION_NUMBER
				  + Command.toInteger(command) * DIRECTION_NUMBER * DIRECTION_NUMBER
				    + Direction.toInteger(move) * DIRECTION_NUMBER
				      + Direction.toInteger(build);
		return LEGAL_ACTIONS[index];
	}
}

class Grid {
	
	Cell[][] cells = null;
	int size;
	
	public void createGrid(int size) {
		this.cells = new Cell[size][size];
		for (int i = 0; i < size; i++) {
			for (int j = 0; j < size; j++) {
				this.cells[i][j] = new Cell(i, j);
			}
		}
		this.size = size;
	}
	
	public Cell getCell(int x, int y) {
		return this.cells[x][y];
	}
	
	public Cell getNeighbourgCell(int x, int y, Direction direction) {
		
		Cell neighbourgCell = null;
		
		switch (direction) {
		case N:
			if ((y-1) >= 0) { neighbourgCell = cells[x][y-1]; } 
			break;
		case NE:
			if ((y-1) >= 0 && (x+1) < size) { neighbourgCell = cells[x+1][y-1]; }
			break;
		case E:
			if ((x+1) < size) { neighbourgCell = cells[x+1][y]; }
			break;
		case SE:
			if ((y+1) < size && (x+1) < size) { neighbourgCell = cells[x+1][y+1]; }
			break;
		case S:
			if ((y+1) < size) { neighbourgCell = cells[x][y+1]; }
			break;
		case SW:
			if ((y+1) < size && (x-1) >= 0) { neighbourgCell = cells[x-1][y+1]; }
			break;
		case W:
			if ((x-1) >= 0) { neighbourgCell = cells[x-1][y]; }
			break;
		case NW:
			if ((y-1) >= 0 && (x-1) >= 0) { neighbourgCell = cells[x-1][y-1]; }
			break;
		default:
			break;
		}
		
		return neighbourgCell;
	}
}

class Cell {
	int height = 0; // -1 to 4
	int width = 0;
	int length = 0;
	
	Pawn pawn = null;
	
	public Cell(int width, int length) {
		this.width = width;
		this.length = length;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public void addPawn(Pawn pawn) {
		this.pawn = pawn;
	}
	
	public void removePawn() {
		this.pawn = null;
	}
	
	public boolean isReachable(int fromHeight) {
		if (pawn != null) {return false;}
		if (height == -1 || height == 4) {return false;}
		if (height - fromHeight > 1) {return false;}
		return true;
	}
	
	public boolean isBuildable(Pawn pawn) {
		if (this.pawn != null && !this.pawn.equals(pawn)) {return false;}
		if (height == -1 || height == 4) {return false;}
		return true;
	}
	
	public void upHeight() {
		if (this.height < 4) { this.height++; }
	}
	
	public void downHeight() {
		if (this.height > -1) { this.height--; }
	}
}

class Human {
	Pawn[] pawn = null;
	int nbPawns = 0;
	int score = 0;
	
	public Human(int nbPawns) {
		this.pawn = new Pawn[nbPawns];
		this.nbPawns = nbPawns;
		for (int i = 0; i < nbPawns; i++) {
			this.pawn[i] = new Pawn();
		}
	}
	
	public Pawn getPawn(int pawnId) {
		return this.pawn[pawnId];
	}
	
	public Pawn[] getPawns() {
		return this.pawn;
	}
	
	public void addToScore(int increment) {
		this.score += increment;
	}
	
	public int getScore() {
		return this.score;
	}
}

class Pawn {
	Cell location = null;
	boolean isActive = true;
	
	public void setLocation(Cell location) {
		this.location = location;
	}
	
	public Cell getLocation() {
		return this.location;
	}
	
	public void inactive() {
		this.isActive = true;
	}
	
	public boolean isActive() {
		return this.isActive;
	}
}

class Engine {

}

interface Node {
	
}

class LegalAction {
	int id;
	Command command;
	int playerIndex;
	int pawnId;
	Direction moveTo;
	Direction buildTo;
	List<LegalAction> childs;
	
	LegalAction(int id, Command command, int playerIndex, int pawnId, Direction moveTo, Direction buildTo) {
		this.command = command;
		this.playerIndex = playerIndex;
		this.pawnId = pawnId;
		this.moveTo = moveTo;
		this.buildTo = buildTo;
		this.id = id;
	}
	
	public Command getCommand() {
		return this.command;
	}
	
	public int getId() {
		return this.id;
	}
	
	public int getPlayerIndex() {
		return this.playerIndex;
	}
	
	public int getPawnId() {
		return this.pawnId;
	}
	
	public Direction getMoveTo() {
		return this.moveTo;
	}
	
	public Direction getBuildTo() {
		return this.buildTo;
	}
	
	public Command getReverseCommand() {
		Command command = Command.getReverse(this.command);
		return command;
	}
	
	public Direction getReverseMoveTo() {
		Direction moveTo = Direction.getReverse(this.moveTo);
		return moveTo;
	}
	
	public void setChilds(List<LegalAction> legalActionList) {
		this.childs = legalActionList;
	}
	
	public List<LegalAction> getChilds() {
		return this.childs;
	}

	public String toString() {
		return this.command.name() + " " + this.playerIndex + " " + this.pawnId + " " + this.moveTo.name() + " " + this.buildTo.name();
	}
}

class Statistics {
	int fitness = 0;
	int averageFitness = 0;
	int numberFitness = 0;
	
	public void setFitness(int fitness) {
		this.fitness = Math.max(this.fitness, fitness);
		this.averageFitness = ((this.averageFitness * this.numberFitness) + fitness) / (this.numberFitness + 1);
		this.numberFitness++;
	}
	
	public int getFitness() {
		return this.fitness;
	}
	
	public int getAverageFitness() {
		return this.averageFitness;
	}
	
	public int getNumberFitness() {
		return this.numberFitness;
	}
}

class IA {
	
	static Random rand = new Random();
	
	public static LegalAction playRandomIA(List<LegalAction> legalActionList) {
		int size = legalActionList.size();
		if (size == 0) { return null; }
		int index = rand.nextInt(size);
		LegalAction actionToPlay = legalActionList.get(index);
		return actionToPlay;
	}
	
	public static LegalAction playSelectiveIA(List<LegalAction> legalActionList) {
		PriorityQueue<LegalAction> priority = new PriorityQueue<LegalAction>();
		priority.addAll(legalActionList);
		int index = rand.nextInt(5);
		LegalAction actionToPlay = null;
		
		while(!priority.isEmpty() && index >= 0) {
			actionToPlay = priority.remove();
			index--;
		}
		return actionToPlay;
	}
}

enum Command {
	MOVEBUILD("MOVE&BUILD"), UNBUILDMOVE("UNBUILD&MOVE");
	
	String commandText;
	Command(String commandText) {
		this.commandText = commandText;
	}
	
	@Override
	public String toString() {
		return this.commandText;
	}
	
	public static Command get(String commandText) {
		if ("MOVE&BUILD".equals(commandText)) {
			return Command.MOVEBUILD;
		}
		else if ("UNBUILD&MOVE".equals(commandText)) {
			return Command.UNBUILDMOVE;
		}
		else return null;
	}
	
	public static Command get(int index) {
		switch(index) {
		case 0: return Command.MOVEBUILD;
		case 1: return Command.UNBUILDMOVE;
		}
		return null;
	}
	
	public static int toInteger(Command command) {
		switch(command) {
		case MOVEBUILD: return 0;
		case UNBUILDMOVE: return 1;
		}
		return -1;
	}
	
	public static Command getReverse(Command command) {
		if (MOVEBUILD.equals(command)) {
			return Command.UNBUILDMOVE;
		}
		else if (UNBUILDMOVE.equals(command)) {
			return Command.MOVEBUILD;
		}
		else return null;
	}
}

enum Direction {
	N, NE, E, SE, S, SW, W, NW; 
	public static Direction get(int directionId) {
		switch(directionId) {
		case 0: return N;
		case 1: return NE;
		case 2: return E;
		case 3: return SE;
		case 4: return S;
		case 5: return SW;
		case 6: return W;
		case 7: return NW;
		}
		return null;
	}
	
	public static int toInteger(Direction direction) {
		switch(direction) {
		case N: return 0;
		case NE: return 1;
		case E: return 2;
		case SE: return 3;
		case S: return 4;
		case SW: return 5;
		case W: return 6;
		case NW: return 7;
		}
		return -1;
	}
	
	public static Direction getReverse(Direction direction) {
		switch(direction) {
		case N: return S;
		case NE: return SW;
		case E: return W;
		case SE: return NW;
		case S: return N;
		case SW: return NE;
		case W: return E;
		case NW: return SE;
		}
		return null;
	}
}


