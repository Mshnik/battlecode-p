package michael_player;

import battlecode.common.*;

/**
 * Created by Matt on 1/9/17.
 */
public class BotLumberjack extends Globals {

    public static void loop() throws GameActionException {
        System.out.println("I'm a lumberjack!");

        // The code you want your robot to perform every round should be in this loop
        while (true) {
            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                update();

                RobotInfo[] robots = rc.senseNearbyRobots(RobotType.LUMBERJACK.bodyRadius + GameConstants.LUMBERJACK_STRIKE_RADIUS, them);

                Direction toEnemy = Movement.directionToEnemy();

                if (robots.length > 0) {
                    if (rc.canStrike()) {
                        rc.strike();
                    }
                }
                else {
                    if (rc.canMove(toEnemy)) {
                        rc.move(toEnemy);
                    }
                    else {
                        TreeInfo[] treeInfos = rc.senseNearbyTrees(2);

                        for (int i = 0; i < treeInfos.length; i++) {
                            MapLocation treeLoc = here.add(toEnemy);
                            MapLocation realTreeLoc = treeInfos[i].location;
                            if (treeInfos[i].location.isWithinDistance(treeLoc,5)) {
                                if (rc.canChop(realTreeLoc)) {
                                    rc.chop(realTreeLoc);
                                }
                            }
                        }
                    }
                }

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Lumberjack Exception");
                e.printStackTrace();
            }
        }
    }
}
