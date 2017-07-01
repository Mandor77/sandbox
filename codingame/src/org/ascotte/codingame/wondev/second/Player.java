package org.ascotte.codingame.wondev.second;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

class Utils {
	final static StringBuilder buffer = new StringBuilder();
	
	static void publish(Command command, String... args) {
		buffer.setLength(0);
		buffer.append(command.name);
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

class Player {
	final static Game game = new Game();
	final static Logic logic = new Logic(game);
	final static int PLAYER = 0;
	final static int OPPONENT = 1;
	static int NUM_TURN = 0;

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
			
			//TODO : use it for first iteration
			int legalMoveNumber = in.nextInt();
			for (int i = 0; i < legalMoveNumber; i++) {
				String atype = in.next();
				int index = in.nextInt();
				String dir1 = in.next();
				String dir2 = in.next();
			}
			
			long start = System.nanoTime();
			List<Move> legalMoves = new ArrayList<>();
			legalMoves = game.getLegalMoves(PLAYER);
			Utils.debug("Nombre actions " + legalMoves.size() + " / " + legalMoveNumber);
			long intermediary = System.nanoTime();
			Utils.debug("Duration = " + (intermediary - start));
			
			logic.play(legalMoveNumber);
			
			long end = System.nanoTime();
			Utils.debug("Duration = " + (end - start));
			
			NUM_TURN++;
		}
	}
}

class Game {
	
	final Grid grid = new Grid();
	final Human[] humans = new Human[PLAYER_NUMBER];
	final static int PLAYER_NUMBER = 2;
	final static int PAWN_NUMBER = 2;
	final static int DIRECTION_NUMBER = 8;
	final static int COMMAND_NUMBER = 1;
	static Move[] MOVES = new Move[PLAYER_NUMBER * PAWN_NUMBER * COMMAND_NUMBER * DIRECTION_NUMBER * DIRECTION_NUMBER];
	
	public List<Move> getLegalMoves(int playerId) {
		
		List<Move> legalMoves = new ArrayList<Move>();
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
				Cell targetCell = this.grid.getNeighbourg(currentCell, moveDirection);
				// Move is not possible
				if (targetCell == null || !targetCell.isReachable(currentCell.height)) {
					//Utils.debug("Move not possible " + moveDirection + " " + currentCell.width + " " + currentCell.length);
					continue;
				}
			
				// Move is possible
				for (int j = 0; j < DIRECTION_NUMBER; j++) {
					Direction buildDirection = Direction.get(j);
					Cell buildCell = this.grid.getNeighbourg(targetCell, buildDirection);
					// Build is not possible
					if (buildCell == null || !buildCell.isBuildable(pawn)) {
						//Utils.debug("Build not possible " + buildDirection + " " + targetCell.width + " " + targetCell.length);
						continue;
						}
					
					// Build is possible
					legalMoves.add(getMove(playerId, pawnId, Command.MOVE_AND_BUILD, moveDirection, buildDirection));
					nbLegalActionForPawn++;
				}
			}
			
			// If no action, kill pawn
			if (nbLegalActionForPawn == 0) {
				pawn.inactive();
			}
		}

		return legalMoves;
	}
	
	public void initGame(int gridSize, int nbPawnsByHuman) {
		this.grid.createGrid(gridSize);
		for (int i = 0; i < humans.length; i++) {
			this.humans[i] = new Human(nbPawnsByHuman);
		}
		this.createMoves();
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

	public void simulate(Move move) {
		
		int playerId = move.getPlayerIndex();
		int pawnId = move.getPawnId();
		Pawn pawn = this.humans[playerId].getPawn(pawnId);
		Cell currentCell = pawn.getLocation();
		Cell targetCell = null;
		Cell buildCell = null;
		Direction moveDirection = move.getMoveTo();
		Direction buildDirection = move.getBuildTo();
		
		switch(move.getCommand()) {
		case MOVE_AND_BUILD:
			targetCell = this.grid.getNeighbourg(currentCell, moveDirection);
			this.setPawnLocation(playerId, pawnId, targetCell.x, targetCell.y);
			this.markScore(playerId, pawnId, 1);
			buildCell = this.grid.getNeighbourg(targetCell, buildDirection);
			if (buildCell == null) {
				Utils.debug(move.toString() + " " + currentCell.x + "/" + currentCell.y);
				Utils.debug(move.toString() + " " + targetCell.x + "/" + targetCell.y);
			}
			buildCell.upHeight();
			break;
		default:
			break;
		}
	}

	public void markScore(int playerId, int pawnId, int score) {
		Human human = this.humans[playerId];
		Pawn pawn = human.getPawn(pawnId);
		if (pawn.getLocation().height == 3) {
			human.addToScore(score);
		}
	}
	
	public void play(Move move) {
		Utils.publish(move.getCommand(), String.valueOf(move.getPawnId()), move.getMoveTo().name(), move.getBuildTo().name());
	}

	public void rollback(Move move) {
		int playerId = move.getPlayerIndex();
		int pawnId = move.getPawnId();
		Pawn pawn = this.humans[playerId].getPawn(pawnId);
		Cell currentCell = pawn.getLocation();
		Cell targetCell = null;
		Cell buildCell = null;
		Direction moveDirection = move.getMoveTo().getReverse();
		Direction buildDirection = move.getBuildTo();
		
		switch(move.getCommand()) {
		case MOVE_AND_BUILD:
			buildCell = this.grid.getNeighbourg(currentCell, buildDirection);
			buildCell.downHeight();
			this.markScore(playerId, pawnId, -1);
			targetCell = this.grid.getNeighbourg(currentCell, moveDirection);
			this.setPawnLocation(playerId, pawnId, targetCell.x, targetCell.y);
			break;
		default:
			break;
	
		}
	}

	public double eval() {
		Human human = this.humans[Player.PLAYER];
		double fitness = 0;
		for (int i = 0; i < human.nbPawns; i++) {
			fitness += human.getPawn(i).getLocation().height;
		}
		return fitness;
	}
	
	public void createMoves() {
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
							MOVES[index] = new Move(index, Command.get(commandId), playerId, pawnId, Direction.get(moveId), Direction.get(buildId));
						}
					}
				}
			}
		}
	}
	
	public Move getMove(int playerId, int pawnId, Command command, Direction move, Direction build) {
		int index = playerId * PAWN_NUMBER * COMMAND_NUMBER * DIRECTION_NUMBER * DIRECTION_NUMBER
				+ pawnId * COMMAND_NUMBER * DIRECTION_NUMBER * DIRECTION_NUMBER
				  + command.toInteger() * DIRECTION_NUMBER * DIRECTION_NUMBER
				    + move.id * DIRECTION_NUMBER
				      + build.id;
		return MOVES[index];
	}
}

class Logic {

	Game game;
	final static int MAX_DEPTH = 2;
	
	public Logic(Game game) {
		this.game = game;
	}
	
	//Level B
	/*public void play() {
		
		double bestValue = Double.NEGATIVE_INFINITY;
		Move bestMove = null;
		long start = System.currentTimeMillis();
		
		for (Move move:game.getLegalMoves(Player.PLAYER)) {
			
			game.simulate(move);
			double value = minimax(MAX_DEPTH, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, false);
			
			if (value > bestValue) {
				bestValue = value;
				bestMove = move;
			}
			
			game.rollback(move);
			long end = System.currentTimeMillis();
			Utils.debug("One game = " + (end - start));
		}
		
		game.play(bestMove);
	}*/
	
	public void play(int legalMoveNumber) {
		
		int depth = MAX_DEPTH;
		if (legalMoveNumber < 50) { depth++; }
		
		double value = minimax(depth, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, true);
	}
	
	public double minimax(int depth, double alpha, double beta, boolean maximizingPlayer) {

		if (depth == 0) {
			return game.eval();
		}

		double bestValue = 0d;
		Move bestMove = null;
		
		// min
		if (!maximizingPlayer) {
			bestValue = Double.POSITIVE_INFINITY;
			for (Move move : game.getLegalMoves(Player.OPPONENT)) {
				game.simulate(move);
				double value = minimax(depth - 1, alpha, beta, true);
				if (value < bestValue) {
					bestValue = value;
					bestMove = move;
				}
				game.rollback(move);
				if (alpha > bestValue) {
					break;
				}
				beta = Math.min(beta, bestValue);
			}
		}

		// max
		else {
			bestValue = Double.NEGATIVE_INFINITY;
			for (Move move : game.getLegalMoves(Player.PLAYER)) {
				game.simulate(move);
				double value = minimax(depth - 1, alpha, beta, true);
				if (value > bestValue) {
					bestValue = value;
					bestMove = move;
				}
				game.rollback(move);
				if (beta < bestValue) {
					break;
				}
				alpha = Math.max(alpha, bestValue);
			}
		}

		if (depth == MAX_DEPTH) {
			game.play(bestMove);
		}
		
		return bestValue;
	}
}

class Grid {
	
	Cell[][] cells = null;
	int size;
	final static int[] weights = {0, -1, 1, -1, 1, 0, 1, 1, 0, 1, -1, 1, -1, 0, -1, -1};
	
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
	
	public Cell getNeighbourg(Cell cell, Direction direction) {
		
		Cell neighbourgCell = null;
		int x = cell.x + weights[2 * direction.id];
		int y = cell.y + weights[2 * direction.id + 1];
		if (x >= 0 && y >= 0 && x < size && y < size) {
			neighbourgCell = cells[x][y];
		}
		return neighbourgCell;
	}
}

class Cell {
	int height = 0; // -1 to 4
	int x = 0;
	int y = 0;
	
	Pawn pawn = null;
	
	public Cell(int x, int y) {
		this.x = x;
		this.y = y;
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

class Move {
	int id;
	Command command;
	int playerIndex;
	int pawnId;
	Direction moveTo;
	Direction buildTo;
	
	Move(int id, Command command, int playerIndex, int pawnId, Direction moveTo, Direction buildTo) {
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

	public String toString() {
		return this.command.name() + " " + this.playerIndex + " " + this.pawnId + " " + this.moveTo.name() + " " + this.buildTo.name();
	}
}
enum Command {
	MOVE_AND_BUILD("MOVE&BUILD"), PUSH_AND_BUILD("PUSH&BUILD"), UNBUILD_AND_MOVE("UNBUILD&MOVE"), UNBUILD_AND_PULL("UNBUILD&PULL");
	String name;
	Command(String name) {
		this.name = name;
	}
	
	public static Command get(int index) {
		switch(index) {
		case 0: return Command.MOVE_AND_BUILD;
		}
		return null;
	}
	
	public int toInteger() {
		switch(this) {
		case MOVE_AND_BUILD: return 0;
		}
		return -1;
	}
}

enum Direction {
	N(0), NE(1), E(2), SE(3), S(4), SW(5), W(6), NW(7);
	int id;
	Direction(int id) {
		this.id = id;
	}
	
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
	
	public Direction getReverse() {
		switch(this) {
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