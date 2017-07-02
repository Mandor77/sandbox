package org.ascotte.codingame.wondev.second;

import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
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
				//Utils.debug("Proposed action = " + atype + " " + index + " " + dir1 + " " + dir2);
			}
			
			long start = System.nanoTime();
			List<Move> legalMoves = new ArrayList<>();
			legalMoves = game.getLegalMoves(PLAYER, true);
			Utils.debug("Nombre actions " + legalMoves.size() + " / " + legalMoveNumber);
			long intermediary = System.nanoTime();
			Utils.debug("Duration = " + (intermediary - start));
			//for (Move move:legalMoves) {
			//	Utils.debug("Action = " + move.toString());
			//}
			
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
	final static int COMMAND_NUMBER = 2;
	static Move[] MOVES = new Move[PLAYER_NUMBER * PAWN_NUMBER * COMMAND_NUMBER * DIRECTION_NUMBER * DIRECTION_NUMBER];
	
	public List<Move> getLegalMoves(int playerId, boolean DEBUG) {

		List<Move> legalMoves = new ArrayList<Move>();
		PriorityQueue<PriorityWrapper> wrapperQueue = new PriorityQueue<PriorityWrapper>();
		Pawn[] pawns = this.humans[playerId].getPawns();
		boolean isMovable = false;
		boolean isPushable = false;

		// For each pawn
		for (int pawnId = 0; pawnId < pawns.length; pawnId++) {
			Pawn pawn = pawns[pawnId];
			// If pawn already out
			if (!pawn.isActive() || pawn.getLocation() == null) {
				if (DEBUG) {
					Utils.debug("Pawn is died "+pawn.isActive());
				}
				continue;
			}

			Cell currentCell = pawn.getLocation();
			for (int i = 0; i < DIRECTION_NUMBER; i++) {
				Direction moveDirection = Direction.get(i);
				Cell targetCell = this.grid.getNeighbourg(currentCell, moveDirection);
				if (targetCell != null && targetCell.isReachable()) {
					wrapperQueue.add(new PriorityWrapper(targetCell, moveDirection, pawn, targetCell.height));
					targetCell.setFromDirection(moveDirection);
				}
			}
		}

		while (!wrapperQueue.isEmpty()) {
			PriorityWrapper wrapper = wrapperQueue.poll();
			Cell targetCell = wrapper.getTargetCell();
			Direction moveDirection = wrapper.getMoveTo();
			Pawn pawn = wrapper.getPawn();
			Cell currentCell = pawn.getLocation();

			// Cell targetCell = this.grid.getNeighbourg(currentCell,
			// moveDirection);
			// Move is not possible
			// if (targetCell == null || !targetCell.isReachable()) {
			// Utils.debug("Move not possible " + moveDirection + " " +
			// currentCell.width + " " + currentCell.length);
			// continue;
			// }

			isMovable = targetCell.isMovable(currentCell.height);
			isPushable = targetCell.isPushable(pawn);
			if (!isMovable && !isPushable) {
				continue;
			}

			// Move or push is possible
			for (int j = 0; j < DIRECTION_NUMBER; j++) {
				Direction buildDirection = Direction.get(j);
				Cell buildCell = this.grid.getNeighbourg(targetCell, buildDirection);
				// Build is not possible
				if (buildCell == null || !buildCell.isBuildable(pawn)) {
					// Utils.debug("Build not possible " + buildDirection + " "
					// + targetCell.width + " " + targetCell.length);
					continue;
				}

				// If move and build is possible
				if (isMovable) {
					legalMoves.add(
							getMove(playerId, pawn.getId(), Command.MOVE_AND_BUILD, moveDirection, buildDirection));
				}

				if (isPushable) {
					if ((buildDirection.id >= (8 + moveDirection.id - 1) % 8)
							&& buildDirection.id <= (8 + moveDirection.id + 1) % 8) {
						if (buildCell.isReachable() && buildCell.isMovable(targetCell.height)) {
							legalMoves.add(getMove(playerId, pawn.getId(), Command.PUSH_AND_BUILD, moveDirection,
									buildDirection));
						}
					}
				}
			}
		}

		return legalMoves;

	}
	
	public void initGame(int gridSize, int nbPawnsByHuman) {
		this.grid.createGrid(gridSize);
		for (int i = 0; i < humans.length; i++) {
			this.humans[i] = new Human(i, nbPawnsByHuman);
		}
		this.createMoves();
	}

	public Grid getGrid() {
		return this.grid;
	}
	
	public void setPawnLocation(int playerId, int pawnId, int x, int y) {
		
		Human human = this.humans[playerId];
		Pawn pawn = human.getPawn(pawnId);
		Cell currentLocation = pawn.getLocation();
		
		// Remove pawn from its current location
		if (currentLocation != null) {
			currentLocation.removePawn();
		}
		
		// In case of the pawn is not visible
		if (x == -1 || y == -1) {
			pawn.setLocation(null);
			Utils.debug("Opponent pawn is not visible = " + pawnId);
			return; 
		}
		
		Cell newLocation = this.grid.getCell(x, y);
		// Put pawn to its new location
		newLocation.addPawn(pawn);
		pawn.setLocation(newLocation);
	}

	public void simulate(Move move) {
		
		int playerId = move.getPlayerIndex();
		int pawnId = move.getPawnId();
		Pawn pawn = this.humans[playerId].getPawn(pawnId);
		Cell currentCell = pawn.getLocation();
		Cell firstCell = null;
		Cell secondCell = null;
		Direction moveDirection = move.getMoveTo();
		Direction buildDirection = move.getBuildTo();
		
		switch(move.getCommand()) {
		case MOVE_AND_BUILD:
			firstCell = this.grid.getNeighbourg(currentCell, moveDirection);
			this.setPawnLocation(playerId, pawnId, firstCell.x, firstCell.y);
			this.markScore(playerId, pawnId, 1);
			secondCell = this.grid.getNeighbourg(firstCell, buildDirection);
			if (secondCell == null) {
				Utils.debug(move.toString() + " " + currentCell.x + "/" + currentCell.y);
				Utils.debug(move.toString() + " " + firstCell.x + "/" + firstCell.y);
			}
			if (secondCell.getPawn() != null) {
				secondCell.upHeight();
			}
			break;
		case PUSH_AND_BUILD:
			firstCell = this.grid.getNeighbourg(currentCell, moveDirection);
			secondCell = this.grid.getNeighbourg(firstCell, buildDirection);
			Pawn opponentPawn = firstCell.getPawn();
			if (opponentPawn != null) {
				this.setPawnLocation(opponentPawn.getPlayerId(), opponentPawn.getId(), secondCell.x, secondCell.y);
			}
			if (firstCell.getPawn() != null) {
				firstCell.upHeight();
			}
		default:
			break;
		}
	}

	public void markScore(int playerId, int pawnId, int score) {
		Human human = this.humans[playerId];
		Pawn pawn = human.getPawn(pawnId);
		if (pawn.getLocation() != null && pawn.getLocation().height == 3) {
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
		Cell firstCell = null;
		Cell secondCell = null;
		Direction moveDirection = move.getMoveTo();
		Direction buildDirection = move.getBuildTo();
		
		switch(move.getCommand()) {
		case MOVE_AND_BUILD:
			moveDirection = move.getMoveTo().getReverse();
			secondCell = this.grid.getNeighbourg(currentCell, buildDirection);
			if (secondCell.getPawn() != null) {
				secondCell.downHeight();
			}
			this.markScore(playerId, pawnId, -1);
			firstCell = this.grid.getNeighbourg(currentCell, moveDirection);
			this.setPawnLocation(playerId, pawnId, firstCell.x, firstCell.y);
			break;
		case PUSH_AND_BUILD:
			firstCell = this.grid.getNeighbourg(currentCell, moveDirection);
			secondCell = this.grid.getNeighbourg(firstCell, buildDirection);
			Pawn opponentPawn = secondCell.getPawn();
			if (opponentPawn != null) {
				this.setPawnLocation(opponentPawn.getPlayerId(), opponentPawn.getId(), firstCell.x, firstCell.y);
			}
			if (firstCell.getPawn() != null) {
				firstCell.downHeight();
			}
		default:
			break;
		}
	}
	
	public double eval(boolean noMoreLegalMove) {
		Human player = this.humans[Player.PLAYER];
		Human opponent = this.humans[Player.OPPONENT];
		
		int fitness = 0;

		fitness = player.pawn[0].getLocation().height;
		
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
	static int MAX_DEPTH = 3; 
	static int NB_TRY = 0;
	static int NB_ALPHA = 0;
	static int NB_BETA = 0;
	static long start = 0;
	
	public Logic(Game game) {
		this.game = game;
	}
	
	public void play(int legalMoveNumber) {
		
		double value = minimax(MAX_DEPTH, Double.NEGATIVE_INFINITY, Double.POSITIVE_INFINITY, true);
		Utils.debug("TRY=" + NB_TRY + " ALPHA=" + NB_ALPHA + " BETA=" + NB_BETA);
	}
	
	public double minimax(int depth, double alpha, double beta, boolean maximizingPlayer) {

		List<Move> legalMoves = null;
		
		if (depth == 0) {
			NB_TRY++;
			double eval = game.eval(false);
			return eval;
		}

		int nbMove = 0;
		double bestValue = 0d;
		Move bestMove = null;
		long end = 0;
		if (depth == MAX_DEPTH) {
			start = System.nanoTime();
		}
		
		// min
		if (!maximizingPlayer) {
			bestValue = Double.POSITIVE_INFINITY;
			legalMoves = game.getLegalMoves(Player.OPPONENT, false);
			if (legalMoves.isEmpty()) { bestValue = minimax(depth - 1, alpha, beta, true);}
			for (Move move : legalMoves) {
				game.simulate(move);
				double value = minimax(depth - 1, alpha, beta, true);
				if (value < bestValue) {
					bestValue = value;
					bestMove = move;
				}
				
				game.rollback(move);
				if (alpha > bestValue) {
					NB_ALPHA++;
					break;
				}
				
				beta = Math.min(beta, bestValue);
				if (depth > MAX_DEPTH-2) {
					end = System.nanoTime();
					if ((end - start) > 44000000) {
						if (depth == MAX_DEPTH) {
							Utils.debug("Force break after " + nbMove);
						}
						break;
					}
				}
			}
		}

		// max
		else {
			bestValue = Double.NEGATIVE_INFINITY;
			legalMoves = game.getLegalMoves(Player.PLAYER, false);
			if (legalMoves.isEmpty()) {return game.eval(true);}
			for (Move move : legalMoves) {
				if (depth == MAX_DEPTH) {
					nbMove++;
				}
				game.simulate(move);
				double value = minimax(depth - 1, alpha, beta, false);
				if (value > bestValue) {
					bestValue = value;
					bestMove = move;
				}
				game.rollback(move);
				if (depth == MAX_DEPTH) {
					Utils.debug("Val="+value+" "+move.toString());
				}
				if (beta < bestValue) {
					NB_BETA++;
					break;
				}
				alpha = Math.max(alpha, bestValue);
				if (depth > MAX_DEPTH-2) {
					end = System.nanoTime();
					if ((end - start) > 44000000) {
						if (depth == MAX_DEPTH) {
							Utils.debug("Force break after " + nbMove);
						}
						break;
					}
				}
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

class Cell implements Comparable<Cell> {
	int height = 0; // -1 to 4
	int x = 0;
	int y = 0;
	Direction fromDirection;
	
	Pawn pawn = null;
	
	public Cell(int x, int y) {
		this.x = x;
		this.y = y;
	}
	
	public void setFromDirection(Direction direction) {
		this.fromDirection = direction;
	}
	
	public Direction getFromDirection() {
		return this.fromDirection;
	}
	
	public void setHeight(int height) {
		this.height = height;
	}
	
	public void addPawn(Pawn pawn) {
		this.pawn = pawn;
	}
	
	public Pawn getPawn() {
		return this.pawn;
	}
	
	public void removePawn() {
		this.pawn = null;
	}
	
	public boolean isReachable() {
		if (height == -1 || height == 4) {return false;}
		return true;
	}
	
	public boolean isPushable(Pawn fromPawn) {
		if (pawn == null) { return false; }
		if (pawn.getPlayerId() == fromPawn.getPlayerId()) { return false; }
		return true;
	}
	
	public boolean isMovable(int fromHeight) {
		if (pawn != null) { return false; }
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

	@Override
	public int compareTo(Cell o) {
		if (this.height < o.height) { return 1; }
		if (this.height == o.height) { return 0; }
		if (this.height > o.height) { return -1; } 
		return 0;
	}
}

class PriorityWrapper implements Comparable<PriorityWrapper>{
	
	Cell targetCell;
	Pawn pawn;
	Direction moveTo;
	int height;
	
	PriorityWrapper(Cell targetCell, Direction moveTo, Pawn pawn, int height) {
		this.targetCell = targetCell;
		this.pawn = pawn;
		this.moveTo = moveTo;
		this.height = height;
	}
	
	public Cell getTargetCell() {
		return targetCell;
	}

	public Pawn getPawn() {
		return pawn;
	}

	public Direction getMoveTo() {
		return moveTo;
	}

	public int getHeight() {
		return height;
	}
	
	@Override
	public int compareTo(PriorityWrapper o) {
		if (this.height < o.height) { return 1; }
		if (this.height == o.height) { return 0; }
		if (this.height > o.height) { return -1; } 
		return 0;
	}
}

class Human {
	Pawn[] pawn = null;
	int nbPawns = 0;
	int score = 0;
	int playerId;
	
	public Human(int playerId, int nbPawns) {
		this.pawn = new Pawn[nbPawns];
		this.nbPawns = nbPawns;
		this.playerId = playerId;
		for (int i = 0; i < nbPawns; i++) {
			this.pawn[i] = new Pawn(playerId, i);
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
	int id;
	int playerId;
	Cell location = null;
	boolean isActive = true;
	
	public Pawn(int playerId, int id) {
		this.playerId = playerId;
		this.id = id;
	}
	
	public int getId() {
		return this.id;
	}
	
	public int getPlayerId() {
		return this.playerId;
	}
	
	public void setLocation(Cell location) {
		this.location = location;
	}
	
	public Cell getLocation() {
		return this.location;
	}
	
	public void inactive() {
		this.isActive = false;
	}
	
	public void active() {
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
		case 1: return Command.PUSH_AND_BUILD;
		}
		
		return null;
	}
	
	public int toInteger() {
		switch(this) {
		case MOVE_AND_BUILD: return 0;
		case PUSH_AND_BUILD: return 1;
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