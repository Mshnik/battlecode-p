package opponents.cole_player;

import battlecode.common.*;

/**
 * Created by Matt on 1/9/17.
 */
public class BotGardner extends Globals {

    public static void loop() throws GameActionException {
        System.out.println("I'm a gardener!");

        // The code you want your robot to perform every round should be in this loop
        while (true) {
            if (rc.getTeamBullets() > 1000) {
                rc.donate(300);
            }

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {

                TreeInfo[] myTrees = rc.senseNearbyTrees();

                for (TreeInfo tree : myTrees) {
                    MapLocation mapLocation = tree.location;
                    if (tree.getHealth() < 25) {
                        if (rc.canWater(mapLocation)) {
                            rc.water(mapLocation);
                        }
                    }

                }

                // Listen for home archon's location
                int xPos = rc.readBroadcast(0);
                int yPos = rc.readBroadcast(1);
                MapLocation archonLoc = new MapLocation(xPos, yPos);

                // Generate a random direction
                Direction dir = randomDirection();

                // Randomly attempt to build a soldier or lumberjack in this direction
                if (rc.canBuildRobot(RobotType.SOLDIER, dir) && Math.random() < .01) {
                    rc.buildRobot(RobotType.SOLDIER, dir);
                } else if (rc.canBuildRobot(RobotType.LUMBERJACK, dir) && Math.random() < .01 && rc.isBuildReady()) {
                    rc.buildRobot(RobotType.LUMBERJACK, dir);
                } else {
                    if (rc.canPlantTree(dir)) {
                        rc.plantTree(dir);
                    }
                }

                // Move randomly
                tryMove(randomDirection());

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Gardener Exception");
                e.printStackTrace();
            }
        }
    }
}
