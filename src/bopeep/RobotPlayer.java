package bopeep;
import battlecode.common.*;
import bopeep.bots.Archon;
import bopeep.bots.Gardener;
import bopeep.bots.Lumberjack;
import bopeep.bots.Soldier;

// Note - this class name can't be changed.
public strictfp class RobotPlayer {

  public static RobotController rc;

  /**
   * run() is the method that is called when a robot is instantiated in the Battlecode world.
   * If this method returns, the robot dies!
   **/
  @SuppressWarnings("unused")
  public static void run(RobotController rc) throws GameActionException {
    RobotPlayer.rc = rc;

    switch (rc.getType()) {
      case ARCHON:
        Archon.run();
        break;
      case GARDENER:
        Gardener.run();
        break;
      case SOLDIER:
        Soldier.run(rc);
        break;
      case LUMBERJACK:
        Lumberjack.run(rc);
        break;
      default:
        System.out.println("Unknown unit type " + rc.getType());
        break;
    }
  }

  // Noise channel for non-control archons to blast nothing.
  public final static int ARCHON_NOISE_INDEX = GameConstants.BROADCAST_MAX_CHANNELS - 1;

}
