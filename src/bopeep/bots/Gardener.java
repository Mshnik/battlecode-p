package bopeep.bots;

import static bopeep.bots.CommonFunc.*;

import battlecode.common.*;
import bopeep.RobotPlayer;
import java.util.Iterator;
import util.RingBuffer;

/**
 * @author Mshnik
 */
public strictfp class Gardener {

  private static final int TREE_MAX = 8;
  private static RingBuffer<MapLocation> treeLocations;

  public static void run() throws GameActionException {
    System.out.println("I'm a gardener!");
    treeLocations = new RingBuffer<>(TREE_MAX);

    // The code you want your robot to perform every round should be in this loop
    while (true) {

      // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
      try {
        //Check all trees to see if they're alive
        TreeInfo[] treeInfos = checkTrees();

        if (! treeLocations.isFull() && RobotPlayer.rc.getTeamBullets() >= GameConstants.BULLET_TREE_COST) {
          plantTree();
        }



//        // Listen for home archon's location
//        int xPos = RobotPlayer.rc.readBroadcast(0);
//        int yPos = RobotPlayer.rc.readBroadcast(1);
//        MapLocation archonLoc = new MapLocation(xPos,yPos);
//
//        // Generate a random direction
//        Direction dir = randomDirection();
//
//        // Randomly attempt to build a soldier or lumberjack in this direction
//        if (RobotPlayer.rc.canBuildRobot(RobotType.SOLDIER, dir) && Math.random() < .01) {
//          RobotPlayer.rc.buildRobot(RobotType.SOLDIER, dir);
//        } else if (RobotPlayer.rc.canBuildRobot(RobotType.LUMBERJACK, dir) && Math.random() < .01 && RobotPlayer.rc.isBuildReady()) {
//          RobotPlayer.rc.buildRobot(RobotType.LUMBERJACK, dir);
//        }

        // Move randomly
        tryMove(RobotPlayer.rc, randomDirection());

        // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
        Clock.yield();

      } catch (Exception e) {
        System.out.println("Gardener Exception");
        e.printStackTrace();
      }
    }
  }

  /** Checks all trees to make sure they're alive. Removes non-existent trees from list
   *
   * Bytecode cost:
   *
   * Returns an TreeInfo array current for this round.
   * May have nulls at the end if not all trees exist.
   */
  private static TreeInfo[] checkTrees() {
    TreeInfo[] treeInfos = new TreeInfo[TREE_MAX];
    int i = 0;
    Iterator<MapLocation> iter = treeLocations.iterator();
    while (iter.hasNext()) {
      try {
        TreeInfo treeInfo =RobotPlayer.rc.senseTreeAtLocation(iter.next()); // Bytecode cost: 20
        treeInfos[i++] = treeInfo;
      } catch (GameActionException e) {
        iter.remove();
      }
    }
    return treeInfos;
  }

  private static void plantTree() {

  }

}
