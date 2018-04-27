package org.ascotte.codingame.coderoyale.backup;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Scanner;
import java.util.StringJoiner;
import java.util.function.Predicate;

/**
 * Auto-generated code below aims at helping you parse
 * the standard input according to the problem statement.
 **/
class Backup {

    public static void main(String args[]) {
            	
    	Scanner in = new Scanner(System.in);
        int numSites = in.nextInt();
        for (int i = 0; i < numSites; i++) {
            int siteId = in.nextInt();
            int x = in.nextInt();
            int y = in.nextInt();
            int radius = in.nextInt();
            int trancheId = new Integer((x / (Game.maxWide / Game.nbTranches)) + 1);
            Game.sites.put(siteId, new Site(siteId, x, y, radius, trancheId));
        }

        boolean isAlreadyCheckPosition = false;
        
        // game loop
        while (true) {
            int gold = in.nextInt();
            Game.friendlyQueen.gold = gold;
            
            int touchedSite = in.nextInt(); // -1 if none
            Game.friendlyQueen.touchedSite = touchedSite;
            
        	Site.nbOfFriendlyKnightBarracks = 0;
        	Site.nbOfFriendlyArcherBarracks = 0;
        	Site.nbOfFriendlyGiantBarracks = 0;
        	Site.nbOfFriendlyTowers = 0;
        	Site.nbOfFriendlyMines = 0;
        	Site.nbOfEnemyKnightBarracks = 0;
        	Site.nbOfEnemyArcherBarracks = 0;
        	Site.nbOfEnemyGiantBarracks = 0;
        	Site.nbOfEnemyTowers = 0;
        	Site.nbOfEnemyMines = 0;
        	
            for (int i = 0; i < numSites; i++) {
                int siteId = in.nextInt();
                int remainingGold = in.nextInt(); // used in future leagues
                int maxIncomeRate = in.nextInt(); // used in future leagues
                int structureType = in.nextInt(); // -1 = No structure, 2 = Barracks
                int owner = in.nextInt(); // -1 = No structure, 0 = Friendly, 1 = Enemy
                int param1 = in.nextInt();
                int param2 = in.nextInt();
                
                Site site = Game.sites.get(siteId);
                if (structureType == Game.BARRACKS) {
                	site.barrackTurnBeforeCreep = param1;
                	if (param2 == Game.KNIGHT) {
                		site.structureType = Structure.BARRACKS_KNIGHT;
                	}
                	else if (param2 == Game.ARCHER) {
                		site.structureType = Structure.BARRACKS_ARCHER;
                	}
                	else if (param2 == Game.GIANT) {
                		site.structureType = Structure.BARRACKS_GIANT;
                	}
                }
                else if (structureType == Game.TOWER) {
                	site.towerHealth = param1;
                	site.towerRadius = param2;
                	site.structureType = Structure.TOWER;
                }
                else if (structureType == Game.MINE) {
                	site.mineRemainingGold = remainingGold;
                	site.mineMaxIncomeRate = maxIncomeRate;
                	site.mineIncomeRate = param1;
                	site.structureType = Structure.MINE;
                }
                else {
                	if (site.structureType != Structure.NO_STRUCTURE) {
                		site.isDestroyed = true;
                	}
                	site.structureType = Structure.NO_STRUCTURE;
                }
                site.role = Role.getById(owner);
                
                if (Role.FRIENDLY.equals(site.role)) {
                	switch(site.structureType) {
                		case BARRACKS_KNIGHT:
                			Site.nbOfFriendlyKnightBarracks++;
                			break;
                		case BARRACKS_ARCHER:
                			Site.nbOfFriendlyArcherBarracks++;
                			break;
                		case BARRACKS_GIANT:
                			Site.nbOfFriendlyGiantBarracks++;
                			break;
                		case TOWER:
                			Site.nbOfFriendlyTowers++;
                			break;
                		case MINE:
                			Site.nbOfFriendlyMines++;
                			break;
                		default:
                			break;
                	}
                }
                else if (Role.ENEMY.equals(site.role)) {
                	switch(site.structureType) {
                		case BARRACKS_KNIGHT:
                			Site.nbOfEnemyKnightBarracks++;
                			break;
                		case BARRACKS_ARCHER:
                			Site.nbOfEnemyArcherBarracks++;
                			break;
                		case BARRACKS_GIANT:
                			Site.nbOfEnemyGiantBarracks++;
                			break;
                		case TOWER:
                			Site.nbOfEnemyTowers++;
                			break;
                		case MINE:
                			Site.nbOfEnemyMines++;
                			break;
                		default:
                			break;
                	}
                }
            }
            
            Game.friendlyKnight.clear();
            Game.enemyKnight.clear();
            Game.friendlyArcher.clear();
            Game.enemyArcher.clear();
            Game.friendlyGiant.clear();
            Game.enemyGiant.clear();
            
            int numUnits = in.nextInt();
            for (int i = 0; i < numUnits; i++) {
                int x = in.nextInt();
                int y = in.nextInt();
                int owner = in.nextInt();
                int unitType = in.nextInt(); // -1 = QUEEN, 0 = KNIGHT, 1 = ARCHER
                int health = in.nextInt();
                
                if (unitType == Game.QUEEN) {
                	if (owner == Game.FRIENDLY) {
                		Game.friendlyQueen.x = x;
                		Game.friendlyQueen.y = y;
                		Game.friendlyQueen.health = health;
                		if (!isAlreadyCheckPosition) {
                			Game.friendlyQueen.originX = x;
                			Game.friendlyQueen.originY = y;
                			isAlreadyCheckPosition = true;
                			// If Player 2
                			if (Game.friendlyQueen.x > (Game.maxWide / 2)) {
                				for (Site site:Game.sites.values()) {
                					site.inverseTranche();
                				}
                			}
                			if (Game.friendlyQueen.health == 25 && Game.enemyQueen.health == 25) {
                			    Game.style = Style.AGGRESSIVE;
                			} else {
                			    Game.style = Style.CLASSIC;
                			}
                		}
                	}
                	else if (owner == Game.ENEMY) {
                		Game.enemyQueen.x = x;
                		Game.enemyQueen.y = y;
                		Game.enemyQueen.health = health;
                	}
                }
                else if (unitType == Game.KNIGHT) {
                	if (owner == Game.FRIENDLY) {
                		Game.friendlyKnight.add(new Knight(Role.FRIENDLY, x, y, health));
                	}
                	else if (owner == Game.ENEMY) {
                		Game.enemyKnight.add(new Knight(Role.ENEMY, x, y, health));
                	}
                }
                else if (unitType == Game.ARCHER) {
                	if (owner == Game.FRIENDLY) {
                		Game.friendlyArcher.add(new Archer(Role.FRIENDLY, x, y, health));
                	}
                	else if (owner == Game.ENEMY) {
                		Game.enemyArcher.add(new Archer(Role.ENEMY, x, y, health));
                	}
                }
                else if (unitType == Game.GIANT) {
                	if (owner == Game.FRIENDLY) {
                		Game.friendlyGiant.add(new Giant(Role.FRIENDLY, x, y, health));
                	}
                	else if (owner == Game.ENEMY) {
                		Game.enemyGiant.add(new Giant(Role.ENEMY, x, y, health));
                	}
                }
            }

            Game.play();
        }
    }
}

class Game {
	
	final static int NEUTRAL = -1;
	final static int FRIENDLY = 0;
	final static int ENEMY = 1;
	
	final static int QUEEN = -1;
	final static int KNIGHT = 0;
	final static int ARCHER = 1;
	final static int GIANT = 2;
	
	final static int NO_STRUCTURE = -1;
	final static int MINE = 0;
	final static int TOWER = 1;
	final static int BARRACKS = 2;
	
	final static int maxWide = 1920;
	final static int maxHigh = 1000;
	
	final static int UNKNOWN = -1;
	
	final static int nbTranches = 6;
	
	final static int towerInDanger = 500;
	final static int towerNotMaxed = 700;
	
	final static int towerInDangerAggressive = 300;
	final static int towerNotMaxedAggressive = 500;
	
	static Queen friendlyQueen = new Queen(Role.FRIENDLY);
	static Queen enemyQueen = new Queen(Role.ENEMY);
	
	static Style style;
	
	static List<Knight> friendlyKnight = new ArrayList<Knight>();
	static List<Knight> enemyKnight = new ArrayList<Knight>();
	static List<Archer> friendlyArcher = new ArrayList<Archer>();
	static List<Archer> enemyArcher = new ArrayList<Archer>();
	static List<Giant> friendlyGiant = new ArrayList<Giant>();
	static List<Giant> enemyGiant = new ArrayList<Giant>();
	
	static Map<Integer, Site> sites = new HashMap<Integer, Site>();
	
	static void play() {
	    if (Game.style == Style.AGGRESSIVE) {
	        playAggressive();
	    }
	    else {
		    playClassic();
	    }
	}
	
	static void playAggressiveStep1() {
	    
	    Site site = null;
				
		// Evolve all mines
		site = Algorithm.getClosestSite(friendlyQueen, 
				Algorithm.isMine()
				.and(Algorithm.isOwned(friendlyQueen))
				.and(Algorithm.isTouched(friendlyQueen))
				.and(Algorithm.isNotMaxed()));
		if (site != null) {
			friendlyQueen.build(site.siteId, Structure.MINE);
			return;
		}
		
		// Evolve all towers
		site = Algorithm.getClosestSite(friendlyQueen, 
				Algorithm.isTowerNotMaxed(Game.towerNotMaxedAggressive)
				.and(Algorithm.isTouched(friendlyQueen))
				.and(Algorithm.isOwned(friendlyQueen)));
		if (site != null) {
			friendlyQueen.build(site.siteId, Structure.TOWER);
			return;
		}
		
		// First build two mines
		if (Site.nbOfFriendlyMines < 2) {
			site = Algorithm.getClosestSite(friendlyQueen, 
					Algorithm.isEmpty());
			if (site != null) {
				friendlyQueen.build(site.siteId, Structure.MINE);
				return;
			}
		}
		
		// Then build a barrack
		if (Site.nbOfFriendlyKnightBarracks < 1) {
			site = Algorithm.getClosestSite(friendlyQueen, 
					Algorithm.isEmpty().
					and((Algorithm.inTranche(2)).or(Algorithm.inTranche(3))));
			if (site != null) {
				friendlyQueen.build(site.siteId, Structure.BARRACKS_KNIGHT);
				return;
			}
		}
		
		// Then restore tower
		site = Algorithm.getClosestSite(friendlyQueen, 
				Algorithm.isTowerInDanger(Game.towerInDangerAggressive)
				.and(Algorithm.isOwned(friendlyQueen))
				.and(Algorithm.inTrancheLowerThan(3)));
		if (site != null) {
			friendlyQueen.build(site.siteId, Structure.TOWER);
			return;
		}
		
		// Then continue to mine
		if (Site.nbOfFriendlyMines < 3) {
			site = Algorithm.getClosestSite(friendlyQueen, 
					Algorithm.isEmpty()
					.and(Algorithm.inTrancheLowerThan(3)));
			if (site != null) {
				friendlyQueen.build(site.siteId, Structure.MINE);
				return;
			}
		}
		
		// Then build tower
		if (Site.nbOfFriendlyTowers < 5) {
			site = Algorithm.getClosestSite(friendlyQueen, 
				Algorithm.isEmpty().and(Algorithm.inTrancheLowerThan(3)));
			if (site != null) {
				friendlyQueen.build(site.siteId, Structure.TOWER);
				return;
			}
		}
		
		// Then move
		site = Algorithm.getClosestSite(friendlyQueen, Algorithm.isEmpty());
		friendlyQueen.move(friendlyQueen.originX, Game.maxHigh / 2);
	    
	}
	
	static void playClassicStep1() {
		
		Site site = null;
				
		// Evolve all mines
		site = Algorithm.getClosestSite(friendlyQueen, 
				Algorithm.isMine()
				.and(Algorithm.isOwned(friendlyQueen))
				.and(Algorithm.isTouched(friendlyQueen))
				.and(Algorithm.isNotMaxed()));
		if (site != null) {
			friendlyQueen.build(site.siteId, Structure.MINE);
			return;
		}
		
		// Evolve all towers
		site = Algorithm.getClosestSite(friendlyQueen, 
				Algorithm.isTowerNotMaxed(Game.towerNotMaxed)
				.and(Algorithm.isTouched(friendlyQueen))
				.and(Algorithm.isOwned(friendlyQueen)));
		if (site != null) {
			friendlyQueen.build(site.siteId, Structure.TOWER);
			return;
		}
		
		// First build two mines
		if (Site.nbOfFriendlyMines < 2) {
			site = Algorithm.getClosestSite(friendlyQueen, 
					Algorithm.isEmpty());
			if (site != null) {
				friendlyQueen.build(site.siteId, Structure.MINE);
				return;
			}
		}
		
		// Then build a barrack
		if (Site.nbOfFriendlyKnightBarracks < 1) {
			site = Algorithm.getClosestSite(friendlyQueen, 
					Algorithm.isEmpty().
					and((Algorithm.inTranche(2)).or(Algorithm.inTranche(3))));
			if (site != null) {
				if (site.trancheId == 2) {
					friendlyQueen.build(site.siteId, Structure.TOWER);
				}
				else {
					friendlyQueen.build(site.siteId, Structure.BARRACKS_KNIGHT);
				}
				return;
			}
		}
		
		// Then restore tower
		site = Algorithm.getClosestSite(friendlyQueen, 
				Algorithm.isTowerInDanger(Game.towerInDanger)
				.and(Algorithm.isOwned(friendlyQueen))
				.and(Algorithm.inTrancheLowerThan(3)));
		if (site != null) {
			friendlyQueen.build(site.siteId, Structure.TOWER);
			return;
		}
		
		// Then build tower
		if (Site.nbOfFriendlyTowers < 5) {
			site = Algorithm.getClosestSite(friendlyQueen, 
				Algorithm.isEmpty().and(Algorithm.inTrancheLowerThan(4)));
			if (site != null) {
				friendlyQueen.build(site.siteId, Structure.TOWER);
				return;
			}
		}
		
		// Then continue to mine
		if (Site.nbOfFriendlyMines < 5) {
			site = Algorithm.getClosestSite(friendlyQueen, 
					Algorithm.isEmpty()
					.and(Algorithm.inTrancheLowerThan(3)));
			if (site != null) {
				friendlyQueen.build(site.siteId, Structure.MINE);
				return;
			}
		}
		
		// Then move
		site = Algorithm.getClosestSite(friendlyQueen, Algorithm.isEmpty());
		friendlyQueen.move(friendlyQueen.originX, Game.maxHigh / 2);
		
	}
	
	static void playClassicStep2() {
		Site site = null;
		site = Algorithm.getClosestSite(Game.enemyQueen, 
				Algorithm.isOwned(friendlyQueen).and(Algorithm.isKnightBarracks()));
		
		// Action 2
		if (site != null) {
			friendlyQueen.train(site.siteId);
		}
		else {
			friendlyQueen.train();
		}
	}
	
	// Starter classique
	static void playClassic() {
		
		playClassicStep1();
		playClassicStep2();
	}
	
	// Starter aggresif
	static void playAggressive() {
		
		playAggressiveStep1();
		playClassicStep2();
	}
	
	static void debug(String debugText) {
		System.err.println(debugText);
	}
}

class Algorithm {
	
	static Site getClosestSite(Queen queen, Predicate<Site> predicate) {
		
		Optional<Site> closestSite = Game.sites.values().stream()
			.filter(predicate)
			.min(Comparator.comparing(p -> Math.sqrt(Math.pow(p.x - queen.x, 2d) + (Math.pow(p.y - queen.y, 2d)))));
		
		return closestSite.orElse(null);
	}
	
	
	static Site getFarestSite(Queen queen, Predicate<Site> predicate) {
		
		Optional<Site> farestSite = Game.sites.values().stream()
			.filter(predicate)
			.max(Comparator.comparing(p -> Math.sqrt(Math.pow(p.x - queen.x, 2d) + (Math.pow(p.y - queen.y, 2d)))));
		
		return farestSite.orElse(null);
	}
	
	static Double getClosestEnemyKnightDistance(Queen queen) {
		
		Optional<Double> closestTroopDistance = Game.enemyKnight.stream()
				.map(p -> Math.sqrt(Math.pow(p.x - queen.x, 2d) + (Math.pow(p.y - queen.y, 2d))))
				.min(Comparator.naturalOrder());
		
		return closestTroopDistance.orElse(null);
	}
	
	static Predicate<Site> allSites () {
		return p -> true;
	}
	
	static Predicate<Site> isEmpty() {
		return p -> Structure.NO_STRUCTURE.equals(p.structureType) && !p.isDestroyed;
	}
	
	static Predicate<Site> inTranche(int trancheId) {
		return p -> p.trancheId == trancheId;
	}
	
	static Predicate<Site> inTrancheLowerThan(int trancheId) {
		return p -> p.trancheId < trancheId;
	}
	
	static Predicate<Site> isTowerInDanger(int towerInDanger) {
		return p -> (Structure.TOWER.equals(p.structureType) && p.towerHealth < towerInDanger);
	}
	
	static Predicate<Site> isTowerNotMaxed(int towerNotMaxed) {
		return p -> (Structure.TOWER.equals(p.structureType) && p.towerHealth < towerNotMaxed);
	}
	
	static Predicate<Site> isKnightBarracks() {
		return p -> Structure.BARRACKS_KNIGHT.equals(p.structureType);
	}
	
	static Predicate<Site> isArcherBarracks() {
		return p -> Structure.BARRACKS_ARCHER.equals(p.structureType);
	}
	
	static Predicate<Site> isGiantBarracks() {
		return p -> Structure.BARRACKS_GIANT.equals(p.structureType);
	}
	
	static Predicate<Site> isMine() {
		return p -> Structure.MINE.equals(p.structureType);
	}
	
	static Predicate<Site> isTower() {
		return p -> Structure.TOWER.equals(p.structureType);
	}
	
	static Predicate<Site> isOwned(Queen queen) {
		return p -> p.role.equals(queen.role);
	}
	
	static Predicate<Site> isTouched(Queen queen) {
		return p -> p.siteId == queen.touchedSite;
	}
	
	static Predicate<Site> isNotMaxed() {
		return p -> p.mineMaxIncomeRate != p.mineIncomeRate && p.mineRemainingGold != 0;
	}
}


class Queen {
	Role role;
	
	final static int radius = 30;
	final static int movePerTurn = 60;
	final static int goldForKnight = 80;
	final static int goldForArcher = 100;
	final static int goldForGiant = 140;
	
	int health = 100;
	int touchedSite = -1;
	int gold = 100;
	
	int x;
	int y;
	int originX;
	int originY;
	
	Queen(Role role) {
		this.role = role;
	}
	
	void await() {
		System.out.println("WAIT");
	}
	
	void move(int x, int y) {
		System.out.println("MOVE " + x + " " + y);
	}
	
	void build(int siteId, Structure structureType) {
		System.out.println("BUILD " + siteId + " " + structureType.toString());
	}
	
	void train(int... siteId) {
		StringJoiner join = new StringJoiner(" ");
		join.add("TRAIN");
		for (int i = 0; i < siteId.length; i++) {
			if (Structure.BARRACKS_KNIGHT.equals(Game.sites.get(siteId[i]).structureType) && this.gold >= goldForKnight) {
				join.add(Integer.toString(siteId[i]));
				this.gold -= goldForKnight;
			}
			else if (Structure.BARRACKS_ARCHER.equals(Game.sites.get(siteId[i]).structureType) && this.gold >= goldForArcher) {
				join.add(Integer.toString(siteId[i]));
				this.gold -= goldForArcher;
			}
			else if (Structure.BARRACKS_GIANT.equals(Game.sites.get(siteId[i]).structureType) && this.gold >= goldForGiant) {
				join.add(Integer.toString(siteId[i]));
				this.gold -= goldForGiant;
			}
			else {
				// Invalid action
			}
		}
		System.out.println(join.toString());
	}
}

class Site {
	
	static int nbOfFriendlyKnightBarracks;
	static int nbOfFriendlyArcherBarracks;
	static int nbOfFriendlyGiantBarracks;
	static int nbOfFriendlyTowers;
	static int nbOfFriendlyMines;
	static int nbOfEnemyKnightBarracks;
	static int nbOfEnemyArcherBarracks;
	static int nbOfEnemyGiantBarracks;
	static int nbOfEnemyTowers;
	static int nbOfEnemyMines;
	
	int siteId;
	int trancheId;
	int x;
	int y;
	int radius;
	int barrackTurnBeforeCreep; // Only Barracks
	int mineRemainingGold = -1;
	int mineMaxIncomeRate;
	int mineIncomeRate;
	int towerHealth;
	int towerRadius;
	boolean isDestroyed = false;
	
	Structure structureType;
	Role role;
	
	Site(int siteId, int x, int y, int radius, int trancheId) {
		this.siteId = siteId;
		this.x = x;
		this.y = y;
		this.radius = radius;
		this.trancheId = trancheId;
		this.structureType = Structure.NO_STRUCTURE;
	}
	
	void inverseTranche() {
		this.trancheId = (Game.nbTranches + 1) - trancheId;
	}
}

class Knight {
	Role role;
	int x;
	int y;
	int health;
	
	Knight(Role role, int x, int y, int health) {
		this.x = x;
		this.y = y;
		this.health = health;
	}
}

class Archer {
	Role role;
	int x;
	int y;
	int health;
	
	Archer(Role role, int x, int y, int health) {
		this.x = x;
		this.y = y;
		this.health = health;
	}
}

class Giant {
	Role role;
	int x;
	int y;
	int health;
	
	Giant(Role role, int x, int y, int health) {
		this.x = x;
		this.y = y;
		this.health = health;
	}
}

enum Role {
	NEUTRAL(Game.NEUTRAL), FRIENDLY(Game.FRIENDLY), ENEMY(Game.ENEMY);
	int role;
	Role(int role) {
		this.role = role;
	}
	public static Role getById(int id) {
		if (id == Game.NEUTRAL) return NEUTRAL;
		else if (id == Game.FRIENDLY) return FRIENDLY;
		else if (id == Game.ENEMY) return ENEMY;
		else return null;
	}
}

enum Structure {
	NO_STRUCTURE(null), 
	BARRACKS_KNIGHT("BARRACKS-KNIGHT"), 
	BARRACKS_ARCHER("BARRACKS-ARCHER"),
	BARRACKS_GIANT("BARRACKS-GIANT"),
	TOWER("TOWER"),
	MINE("MINE");
	String text;
	Structure(String text) {
		this.text = text;
	}
	public String toString() {
		return this.text;
	}
}

enum Troop {
	QUEEN(Game.QUEEN), KNIGHT(Game.KNIGHT), ARCHER(Game.ARCHER), GIANT(Game.GIANT);
	int id;
	Troop(int id) {
		this.id = id;
	}
	public static Troop getById(int id) {
		if (id == Game.QUEEN) return QUEEN;
		else if (id == Game.KNIGHT) return KNIGHT;
		else if (id == Game.ARCHER) return ARCHER;
		else if (id == Game.GIANT) return GIANT;
		else return null;
	}
}

enum Style {
    CLASSIC, AGGRESSIVE;
}