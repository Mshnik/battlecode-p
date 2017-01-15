package bopeep.bots;

import static bopeep.bots.CommonFunc.*;

import battlecode.common.*;
import bopeep.RobotPlayer;

/**
 * @author Mshnik
 */
public strictfp class Archon {

  /** Cooldown on hiring a gardener */
  private static final int GARDENER_COOLDOWN = 10;
  /** Cost of hiring a gardener */
  private static final int GARDENER_COST = 100;
  /** Number of Gardeners the control Archon will try to build */
  private static final int GARDENERS_TARGET = 11;
  /** Radius of an archon */
  private static final int ARCHON_RADIUS = 2;

  /** The index of this archon. Set initially, not changed afterwards */
  private static int archonIndex;

  /** Current location of this Archon. Updated each round and when this moves */
  private static MapLocation currentLocation;
  /** Number of gardeners this Archon has hired */
  private static int gardenersHired;
  /** Locations of gardeners created. Locations will be null until that gardener is created. */
  private static MapLocation[] gardenerLocations;
  /** Rounds since a gardener has been purchased */
  private static int roundsSinceGardenerHired;

  public static void run() {
    archonIndex = calculateArchonIndex();
    gardenerLocations = new MapLocation[GARDENERS_TARGET];
    roundsSinceGardenerHired = GARDENER_COOLDOWN;
    gardenersHired = 0;

    // The code you want your robot to perform every round should be in this loop
    while (true) {
      roundsSinceGardenerHired++;
      currentLocation = RobotPlayer.rc.getLocation();

      // TODO: check if control Archon has died, potentially assume control.

      if (archonIndex == 0) {
        performControlRoutine();
      } else {
        performNonControlRoutine();
      }

      Clock.yield();
    }
  }

  private static void performControlRoutine() {
    // Check if we need another gardener and can afford one
    if (gardenersHired < GARDENERS_TARGET
        && roundsSinceGardenerHired >= GARDENER_COOLDOWN
        && RobotPlayer.rc.getTeamBullets() >= GARDENER_COST) {

      // Find a spawn direction
      Direction dir = Direction.getNorth();
      int maxAttempts = 36;
      int attempts = 0;

      while (! RobotPlayer.rc.canHireGardener(dir) && attempts < maxAttempts) {
        dir = dir.rotateLeftDegrees(360.0f/maxAttempts);
        attempts++;
      }

      // Hire one, store location
      try {
        RobotPlayer.rc.hireGardener(dir);
        gardenerLocations[gardenersHired] = currentLocation.add(dir, ARCHON_RADIUS + Gardener.GARDENER_RADIUS);
        roundsSinceGardenerHired = 0;
        gardenersHired++;
      } catch (GameActionException e) {
        e.printStackTrace();
      }
    }

    move();
  }

  /**
   *  Returns the index of this Archon.
   *  If there is exactly 1 archon, returns 0.
   *  If there is more than 1 archon, they are ordered from furthest to nearest to other archons.
   *  Ties broken by y value, increasing, then x value, increasing.
   */
  private static int calculateArchonIndex() {
    Team team = RobotPlayer.rc.getTeam();
    MapLocation location = RobotPlayer.rc.getLocation();
    MapLocation[] archonLocations = RobotPlayer.rc.getInitialArchonLocations(team);
    MapLocation[] enemyArchonLocations = RobotPlayer.rc.getInitialArchonLocations(team.opponent());

    if (archonLocations.length == 1) {
      return 0;
    }

    int furtherArchons = 0;
    float distToEnemyArchons = 0;
    for(MapLocation enemyLocation : enemyArchonLocations) {
      distToEnemyArchons += location.distanceTo(enemyLocation);
    }

    for(MapLocation alliedLocation : archonLocations) {
      if (! location.equals(alliedLocation)) {
        float alliedDistToEnemyArchons = 0;
        for(MapLocation enemyLocation : enemyArchonLocations) {
          alliedDistToEnemyArchons += alliedLocation.distanceTo(enemyLocation);
        }
        if (distToEnemyArchons < alliedDistToEnemyArchons || alliedDistToEnemyArchons == distToEnemyArchons
            && (location.y < alliedLocation.y || location.y == alliedLocation.y && location.x < alliedLocation.x)) {
          furtherArchons++;
        }
      }
    }
    return furtherArchons;
  }

  private static void performNonControlRoutine() {
    try {
      // Blast some noise, make sure enemies find these archons.
      RobotPlayer.rc.broadcast(RobotPlayer.ARCHON_NOISE_INDEX, 1337);
    } catch (GameActionException e) {
      e.printStackTrace();
    }

    move();
  }

  /**
   * Archon moving pattern. Wants to (in order):
   *  - Avoid bullets
   *  - Move away from created gardeners
   *  - Move away from enemy robots
   */
  private static void move() {
    try {
      // Move in a random direction.
      // TODO: move randomly, but away from past gardeners.
      tryMove(RobotPlayer.rc, randomDirection());
    } catch (GameActionException e) {
      e.printStackTrace();
    }
  }
}
