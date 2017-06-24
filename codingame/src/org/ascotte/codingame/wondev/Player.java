package org.ascotte.codingame.wondev;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

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
				game.setPawnLocation(PLAYER, i, unitX, unitY);
			}
			for (int i = 0; i < nbPawnsByHuman; i++) {
				int otherX = in.nextInt();
				int otherY = in.nextInt();
				game.setPawnLocation(OPPONENT, i, otherX, otherY);
			}
			
			int legalActions = in.nextInt();
			for (int i = 0; i < legalActions; i++) {
				String atype = in.next();
				int index = in.nextInt();
				String dir1 = in.next();
				String dir2 = in.next();
			}
			
			List<LegalAction> legalActionList= new ArrayList<>();
			legalActionList = game.getLegalActions(PLAYER, 0);
			Utils.debug("Nombre actions " + legalActionList.size() + " / " + legalActions);
			
			for (int i = 0; i < MAX_SIMULATIONS; i++) {
				LegalAction legalAction = IA.playRandomIA(legalActionList);
				LegalAction rollbackAction = simulateLegalAction(legalAction);
				
				List<LegalAction> nextLegalActionList = new ArrayList<>();
				nextLegalActionList = game.getLegalActions(PLAYER, 0);
				LegalAction nextLegalAction = IA.playRandomIA(nextLegalActionList);
				LegalAction nextRollbackAction = simulateLegalAction(nextLegalAction);
				
				legalAction.setFitness(game.fitness(PLAYER));
				
				simulateLegalAction(nextRollbackAction);
				simulateLegalAction(rollbackAction);
			}
			
			LegalAction bestLegalAction = null;
			for (LegalAction legalAction:legalActionList) {
				if (bestLegalAction == null) { bestLegalAction = legalAction; continue; }
				if (legalAction.getFitness() > bestLegalAction.getFitness()) {
					bestLegalAction = legalAction;
				}
			}
			
			publishLegalAction(bestLegalAction);
		}
	}
	
	public static void publishLegalAction(LegalAction legalAction) {
		Utils.publish(legalAction.getCommand(), String.valueOf(legalAction.getPlayerIndex()), legalAction.getMoveTo().name(), legalAction.getBuildTo().name());
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
	
	public void setPawnLocation(int playerId, int pawnId, int x, int y) {
		Cell cell = this.grid.getCell(x, y);
		Pawn pawn = this.humans[playerId].getPawn(pawnId);
		// Remove current pawn location
		Cell currentLocation = pawn.getLocation();
		if (currentLocation != null) {
			currentLocation.removePawn();
		}
		cell.addPawn(pawn);
		pawn.setLocation(cell);
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
			this.setPawnLocation(playerId, pawnId, targetCell.width, targetCell.length);
			buildCell = this.grid.getNeighbourgCell(targetCell.width, targetCell.length, buildDirection);
			buildCell.upHeight();
			break;
		case UNBUILDMOVE:
			buildCell = this.grid.getNeighbourgCell(currentCell.width, currentCell.length, buildDirection);
			buildCell.downHeight();
			targetCell = this.grid.getNeighbourgCell(currentCell.width, currentCell.length, moveDirection);
			this.setPawnLocation(playerId, pawnId, targetCell.width, targetCell.length);
			break;
		}
	}
	
	public int fitness(int playerId) {
		Pawn pawn = this.humans[playerId].getPawn(0);
		return pawn.getLocation().height;
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
		if (height == -1) {return false;}
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


class LegalAction {
	Command command;
	int playerIndex;
	int pawnId;
	Direction moveTo;
	Direction buildTo;
	int fitness = 0;
	
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
	
	public LegalAction getReverse() {
		Command command = Command.getReverse(this.command);
		Direction moveTo = Direction.getReverse(this.moveTo);
		LegalAction legalAction = new LegalAction(command, this.playerIndex, this.pawnId, moveTo, this.buildTo);
		return legalAction;
	}
	
	public void setFitness(int fitness) {
		this.fitness = Math.max(this.fitness, fitness);
	}
	
	public String toString() {
		return this.command.name() + " " + this.playerIndex + " " + this.pawnId + " " + this.moveTo.name() + " " + this.buildTo.name();
	}
}

class IA {
	
	static Random rand = new Random();
	
	public static LegalAction playRandomIA(List<LegalAction> legalActionList) {
		LegalAction actionToPlay = legalActionList.get(rand.nextInt(legalActionList.size() - 1));
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


