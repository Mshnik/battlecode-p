package michael_player;

import battlecode.common.*;

/**
 * Created by Michael on 1/9/17.
 */
public class BotGardner extends Globals {

    public static void loop() throws GameActionException {
        System.out.println("I'm a gardener!");
        float directionOffset = (float) Math.PI / 3;

        // The code you want your robot to perform every round should be in this loop
        while (true) {
            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                update();
                if (rc.getTeamBullets() > 1000) {
                    rc.donate(300);
                }

                // create robots
                double rand = Math.random();
                Direction hole = new Direction(0);
                if (rc.canBuildRobot(RobotType.SCOUT, hole) && rand < .75 && rc.isBuildReady()) {
                    rc.buildRobot(RobotType.SCOUT, hole);
                } else if (rc.canBuildRobot(RobotType.SOLDIER, hole) && rand < 1 && rc.isBuildReady()) {
                    rc.buildRobot(RobotType.SOLDIER, hole);
                } else if (rc.canBuildRobot(RobotType.TANK, hole) && rand < 0 && rc.isBuildReady()) {
                    rc.buildRobot(RobotType.TANK, hole);
                } else if (rc.canBuildRobot(RobotType.LUMBERJACK, hole) && rand < 0 && rc.isBuildReady()) {
                    rc.buildRobot(RobotType.LUMBERJACK, hole);
                }

                // plant trees
                for (int i = 1; i < 6; i++) {
                    Direction d = new Direction((i * directionOffset));
                    if (rc.canPlantTree(d)) {
                        rc.plantTree(d);
                    }
                }

                // water trees
                waterTree();

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Gardener Exception");
                e.printStackTrace();
            }
        }
    }

    public static void waterTree() throws GameActionException {
        TreeInfo[] myTrees = rc.senseNearbyTrees(2, us);
        if (myTrees.length > 0) {
            int minIndex = -1;
            int minHealth = 51;

            while (rc.canWater()) {
                for (int i = 0; i < myTrees.length; i++) {
                    int curHealth = (int) myTrees[i].getHealth();
                    if (curHealth < minHealth) {
                        minHealth = curHealth;
                        minIndex = i;
                    }
                }
                if (minIndex != -1) {
                    int minID = myTrees[minIndex].ID;
                    if (rc.canWater(minID)) {
                        rc.water(minID);
                    }
                }

                minHealth = 51;
                minIndex = -1;
            }
        }

    }
}
