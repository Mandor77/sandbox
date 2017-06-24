package org.ascotte.codingame.wondev;

import java.util.ArrayList;
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
	final static int MAX_SIMULATIONS = 1000;
	final static int SIMULATION_LENGTH = 3;
	
	public static void main(String args[]) {
		Scanner in = new Scanner(System.in);
		int gridSize = in.nextInt();
		int nbPawnsByHuman = in.nextInt();

		// init game
		game.initGame(gridSize, nbPawnsByHuman);
		
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
				game.setPawnLocation(PLAYER, i, unitX, unitY, false);
			}
			for (int i = 0; i < nbPawnsByHuman; i++) {
				int otherX = in.nextInt();
				int otherY = in.nextInt();
				game.setPawnLocation(OPPONENT, i, otherX, otherY, false);
			}
			
			int legalActions = in.nextInt();
			for (int i = 0; i < legalActions; i++) {
				String atype = in.next();
				int index = in.nextInt();
				String dir1 = in.next();
				String dir2 = in.next();
			}
			
			long debut = System.currentTimeMillis();
			List<LegalAction> legalActionList= new ArrayList<>();
			legalActionList = game.getLegalActions(PLAYER, 0);
			legalActionList.addAll(game.getLegalActions(PLAYER, 1));
			Utils.debug("Nombre actions " + legalActionList.size() + " / " + legalActions);
			
			LegalAction.globalAverageFitness = 0;
			LegalAction.globalNumberFitness = 0;
			
			simulate(legalActionList);
			chooseBest(legalActionList);
			long end = System.currentTimeMillis();
			Utils.debug("Duration = " + (end - debut));
			
			for (LegalAction legalAction:legalActionList) {
				Utils.debug("Average " + legalAction.pawnId + " " + legalAction.moveTo + " " + 
			      legalAction.buildTo + " = " + legalAction.numberFitness + "/" + legalAction.fitness + "/" + legalAction.averageFitness);
			}
		}
	}
	
	public static void simulate(List<LegalAction> initialLegalActionList) {
		Stack<LegalAction> rollbacks = new Stack<>();
		LegalAction rollbackAction;
		
		// Choose and play a random action
		for (int i = 0; i < MAX_SIMULATIONS; i++) {
			LegalAction initialLegalAction;
			if (i < 500) {
				initialLegalAction = IA.playRandomIA(initialLegalActionList);
			}
			else {
				initialLegalAction = IA.playSelectiveIA(initialLegalActionList);
			}
			rollbackAction = simulateLegalAction(initialLegalAction);
			rollbacks.push(rollbackAction);
			
			initialLegalAction.setFitness((game.fitness(PLAYER)));
			
			// Then explore
			LegalAction legalAction = initialLegalAction;
			for (int j = SIMULATION_LENGTH; j > 0; j--) {
				// If childs not defined
				if (legalAction.getChilds() == null) {
					List<LegalAction> legalActionList = game.getLegalActions(PLAYER, 0);
					legalActionList.addAll(game.getLegalActions(PLAYER, 1));
					legalAction.setChilds(legalActionList);
				}
				
				LegalAction nextLegalAction = IA.playRandomIA(legalAction.getChilds());
				if (nextLegalAction == null) { break; }
				LegalAction nextRollbackAction = simulateLegalAction(nextLegalAction);
				rollbacks.push(nextRollbackAction);
				
				initialLegalAction.setFitness((game.fitness(PLAYER)));
				
				legalAction = nextLegalAction;
			}
			
			while(!rollbacks.isEmpty()) {
				simulateLegalAction(rollbacks.pop());
			}
		}
	}
	
	public static void chooseBest(List<LegalAction> initialLegalActionList)
	{
		LegalAction bestLegalAction = null;
		for (LegalAction legalAction:initialLegalActionList) {
			if (bestLegalAction == null) { bestLegalAction = legalAction; continue; }
			if (legalAction.getFitness() > bestLegalAction.getFitness()) {
				bestLegalAction = legalAction;
			}
			else if (legalAction.getFitness() == bestLegalAction.getFitness()) {
				if (legalAction.getAverageFitness() > bestLegalAction.getAverageFitness()) {
					bestLegalAction = legalAction;
				}
			}
		}
		
		publishLegalAction(bestLegalAction);
	}
	
	public static void publishLegalAction(LegalAction legalAction) {
		Utils.publish(legalAction.getCommand(), String.valueOf(legalAction.getPawnId()), legalAction.getMoveTo().name(), legalAction.getBuildTo().name());
	}
	
	public static LegalAction simulateLegalAction(LegalAction legalAction) {
		
		game.playLegalAction(legalAction);
		return legalAction.getReverse();
	}
}


class Game {
	final Grid grid = new Grid();
	final Human[] humans = new Human[2];
	
	public void initGame(int gridSize, int nbPawnsByHuman) {
		this.grid.createGrid(gridSize);
		for (int i = 0; i < humans.length; i++) {
			this.humans[i] = new Human(nbPawnsByHuman);
		}
	}
	
	public Grid getGrid() {
		return this.grid;
	}
	
	public void setPawnLocation(int playerId, int pawnId, int x, int y, boolean rollback) {
		// If not visible cancel
		if (x == -1 || y == -1) { return; }
		Cell cell = this.grid.getCell(x, y);
		Human human = this.humans[playerId];
		Pawn pawn = human.getPawn(pawnId);
		// Remove current pawn location
		Cell currentLocation = pawn.getLocation();
		if (currentLocation != null) {
			currentLocation.removePawn();
		}
		cell.addPawn(pawn);
		pawn.setLocation(cell);
		
		// Scoring
		if (!rollback && cell.height == 3) {
			human.addToScore(1);
		}
		else if (rollback && currentLocation !=null && currentLocation.height == 3) {
			human.addToScore(-1);
		}
	}
	
	public List<LegalAction> getLegalActions(int playerId, int pawnId) {
		
		List<LegalAction> legalActions = new ArrayList<LegalAction>();
		Pawn pawn = this.humans[playerId].getPawn(pawnId);
		Cell currentCell = pawn.getLocation();
		
		for (int i = 0; i < 8; i++) {
			Direction moveDirection = Direction.get(i);
			Cell targetCell = this.grid.getNeighbourgCell(currentCell.width, currentCell.length, moveDirection);
			// Move is not possible
			if (targetCell == null || !targetCell.isReachable(currentCell.height)) {
				//Utils.debug("Move not possible " + moveDirection + " " + currentCell.width + " " + currentCell.length);
				continue;
			}
		
			// Move is possible
			for (int j = 0; j < 8; j++) {
				Direction buildDirection = Direction.get(j);
				Cell buildCell = this.grid.getNeighbourgCell(targetCell.width, targetCell.length, buildDirection);
				// Build is not possible
				if (buildCell == null || !buildCell.isBuildable(pawn)) {
					//Utils.debug("Build not possible " + buildDirection + " " + targetCell.width + " " + targetCell.length);
					continue;
					}
				
				// Build is possible
				legalActions.add(new LegalAction(Command.MOVEBUILD, playerId, pawnId, moveDirection, buildDirection));
			}
		}
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
			this.setPawnLocation(playerId, pawnId, targetCell.width, targetCell.length, false);
			buildCell = this.grid.getNeighbourgCell(targetCell.width, targetCell.length, buildDirection);
			buildCell.upHeight();
			break;
		case UNBUILDMOVE:
			buildCell = this.grid.getNeighbourgCell(currentCell.width, currentCell.length, buildDirection);
			buildCell.downHeight();
			targetCell = this.grid.getNeighbourgCell(currentCell.width, currentCell.length, moveDirection);
			this.setPawnLocation(playerId, pawnId, targetCell.width, targetCell.length, true);
			break;
		}
	}
	
	public int fitness(int playerId) {
		Human human = this.humans[playerId];
		Pawn pawn = human.getPawn(0);
		int fitness = 5 * human.getScore() + pawn.getLocation().height;
		return fitness;
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
	
	public void addToScore(int increment) {
		this.score += increment;
	}
	
	public int getScore() {
		return this.score;
	}
}

class Pawn {
	Cell location = null;
	
	public void setLocation(Cell location) {
		this.location = location;
	}
	
	public Cell getLocation() {
		return this.location;
	}
}

class Engine {

}


class LegalAction implements Comparable<LegalAction> {
	static double globalAverageFitness = 0;
	static double globalNumberFitness = 0;
	Command command;
	int playerIndex;
	int pawnId;
	Direction moveTo;
	Direction buildTo;
	int fitness = 0;
	double averageFitness = 0;
	int numberFitness = 0;
	List<LegalAction> childs;
	
	LegalAction(Command command, int playerIndex, int pawnId, Direction moveTo, Direction buildTo) {
		this.command = command;
		this.playerIndex = playerIndex;
		this.pawnId = pawnId;
		this.moveTo = moveTo;
		this.buildTo = buildTo;
	}
	
	public Command getCommand() {
		return this.command;
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
	
	public int getFitness() {
		return this.fitness;
	}
	
	public double getAverageFitness() {
		return this.averageFitness;
	}
	
	public LegalAction getReverse() {
		Command command = Command.getReverse(this.command);
		Direction moveTo = Direction.getReverse(this.moveTo);
		LegalAction legalAction = new LegalAction(command, this.playerIndex, this.pawnId, moveTo, this.buildTo);
		return legalAction;
	}
	
	public void setChilds(List<LegalAction> legalActionList) {
		this.childs = legalActionList;
	}
	
	public List<LegalAction> getChilds() {
		return this.childs;
	}
	
	public void setFitness(int fitness) {
		this.fitness = Math.max(this.fitness, fitness);
		this.averageFitness = ((this.averageFitness * this.numberFitness) + fitness) / (this.numberFitness + 1);
		this.numberFitness++;
		globalAverageFitness = ((globalAverageFitness * globalNumberFitness) + fitness) / (globalNumberFitness + 1);
		globalNumberFitness++;
	}
	
	public String toString() {
		return this.command.name() + " " + this.playerIndex + " " + this.pawnId + " " + this.moveTo.name() + " " + this.buildTo.name();
	}
	
	public int compareTo(LegalAction legalAction) {
		if (this.averageFitness > legalAction.averageFitness) {
			return -1;
		}
		else if (this.averageFitness < legalAction.averageFitness) {
			return 1;
		}
		return 0;
		
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


