package bopeep.bots;

import static bopeep.bots.CommonFunc.*;

import battlecode.common.*;
import bopeep.RobotPlayer;

/**
 * @author Mshnik
 */
public strictfp class Archon {

  // Value to set to the mem when an archon assumes control.
  private static final int CTRL = -1;
  private static boolean hasControl;

  private static final int GARDENERS_TARGET = 10;

  public static void run() {
    System.out.println("I'm an archon!");

    hasControl = false;
    // The code you want your robot to perform every round should be in this loop
    while (true) {


      // Check if no archon has assumed control
      try {
        if (RobotPlayer.rc.readBroadcast(RobotPlayer.ARCHON_CONTROL_INDEX) != CTRL) {
          RobotPlayer.rc.broadcast(RobotPlayer.ARCHON_CONTROL_INDEX, CTRL);
          hasControl = true;
        }
      } catch (GameActionException ex) {
        System.out.println(ex.getMessage());
      }

      // Check if in control archon or not
      if (hasControl) {
        performControlRoutine();
      } else {
        performNonControlRoutine();
      }

      // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
      Clock.yield();
    }
  }

  private static void performControlRoutine() {
    int gardenerCount = 0;
    try {
      gardenerCount = RobotPlayer.rc.readBroadcast(RobotPlayer.GARDENER_COUNT_INDEX);
    } catch (GameActionException e) {
      e.printStackTrace();
    }

    // Check if we need another gardener and can afford one
    if (gardenerCount < GARDENERS_TARGET && RobotPlayer.rc.getTeamBullets() >= 100) {   //Replace with gardener cost constant when it exists.

      // Find a spawn direction
      Direction dir = Direction.getNorth();
      int maxAttempts = 20;
      int attempts = 0;

      while (! RobotPlayer.rc.canHireGardener(dir) && attempts < maxAttempts) {
        dir = dir.rotateLeftDegrees(360.0f/maxAttempts);
        attempts++;
      }

      // Hire one, and move in the opposite direction.
      try {
        RobotPlayer.rc.hireGardener(dir);
        tryMove(RobotPlayer.rc, dir.opposite());
      } catch (GameActionException e) {
        e.printStackTrace();
      }
    } else {
      try {
        // Move in a random direction.
        // TODO: move randomly, but away from past gardeners.
        tryMove(RobotPlayer.rc, randomDirection());
      } catch (GameActionException e) {
        e.printStackTrace();
      }
    }
  }

  private static void performNonControlRoutine() {
    try {
      // Blast some noise, make sure enemies find these archons.
      RobotPlayer.rc.broadcast(RobotPlayer.ARCHON_NOISE_INDEX, 1337);
      // Move in a random direction.
      // TODO: move randomly, but away from past gardeners.
      tryMove(RobotPlayer.rc, randomDirection());
    } catch (GameActionException e) {
      e.printStackTrace();
    }
  }
}
