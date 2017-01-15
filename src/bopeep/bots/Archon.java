package bopeep.bots;

import static bopeep.bots.CommonFunc.*;

import battlecode.common.*;
import bopeep.RobotPlayer;

/**
 * @author Mshnik
 */
public strictfp class Archon {

  /** Cooldown on hiring a gardener */
  private static final int GARDENER_COOLDOWN = 20;
  /** Cost of hiring a gardener */
  private static final int GARDENER_COST = 100;
  /** Number of Gardeners the control Archon will try to build */
  private static final int GARDENERS_TARGET = 11;
  /** Radius of an archon */
  private static final int ARCHON_RADIUS = 2;

  /** Value to set to the mem when an archon assumes control. */
  private static final int CTRL = -1;
  /** True if this Archon has control, false otherwise */
  private static boolean hasControl;

  /** Current location of this Archon. Updated each round and when this moves */
  private static MapLocation currentLocation;
  /** Number of gardeners this Archon has hired */
  private static int gardenersHired;
  /** Locations of gardeners created. Locations will be null until that gardener is created. */
  private static MapLocation[] gardenerLocations;
  /** Rounds since a gardener has been purchased */
  private static int roundsSinceGardenerHired;

  public static void run() {
    hasControl = false;
    gardenerLocations = new MapLocation[GARDENERS_TARGET];
    roundsSinceGardenerHired = GARDENER_COOLDOWN;
    gardenersHired = 0;

    // The code you want your robot to perform every round should be in this loop
    while (true) {
      roundsSinceGardenerHired++;
      currentLocation = RobotPlayer.rc.getLocation();

      // Check if no archon has assumed control
      try {
        if (RobotPlayer.rc.readBroadcast(RobotPlayer.ARCHON_CONTROL_INDEX) != CTRL) {
          RobotPlayer.rc.broadcast(RobotPlayer.ARCHON_CONTROL_INDEX, CTRL);
          hasControl = true;
        }
      } catch (GameActionException ex) {
        System.out.println(ex.getMessage());
      }

      if (hasControl) {
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
