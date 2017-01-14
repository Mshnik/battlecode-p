package tree_break;

import battlecode.common.*;

/**
 * Created by Matt on 1/9/17.
 */
public class BotGardner extends Globals {
    static Direction dir;

    public static void loop() throws GameActionException {
        System.out.println("I'm a gardener!");

        // The code you want your robot to perform every round should be in this loop
        while (true) {
            try {
                Globals.update();

                // Generate a random direction
                dir = randomDirection();
                int rid = rc.getID() % 6;

                if (rid == 0 || rid == 1 || rid == 2 ) {
                    treeMode();
                }
                else if (rid == 3) {
                    buildTankMode();
                }
                else if (rid == 4) {
                    buildLumberjackMode();
                }
                else if (rid == 5){
                    buildScoutMode();
                }
            }
            catch (Exception e) {
                System.out.println("Gardner  Exception");
                e.printStackTrace();
            }
            }
        }

    private static void buildScoutMode() {
        try {
            int scoutCount = rc.readBroadcast(2);
            if (scoutCount <= 5) {
            if (rc.canBuildRobot(RobotType.SCOUT, dir)) {
                rc.buildRobot(RobotType.SCOUT,dir);
            }

            }
        }
        catch (Exception e) {
            System.out.println("Gardner  Exception");
            e.printStackTrace();
        }
    }


    public static void buildTankMode() throws GameActionException {
        try {

        if (rc.canBuildRobot(RobotType.TANK, dir)) {
            rc.buildRobot(RobotType.TANK,dir);
        }
        }
        catch (Exception e) {
            System.out.println("Gardner  Exception");
            e.printStackTrace();
        }

    }

    public static void buildLumberjackMode() throws GameActionException {
        try {

            if (rc.canBuildRobot(RobotType.LUMBERJACK, dir)) {
                rc.buildRobot(RobotType.LUMBERJACK,dir);
            }
        }
        catch (Exception e) {
            System.out.println("Gardner  Exception");
            e.printStackTrace();
        }

    }

    public static void treeMode() throws GameActionException {
        if (rc.getTeamBullets() > 1000) {
            rc.donate(300);
        }

        // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
        try {
            if (rc.senseNearbyRobots(5, us).length > 0 && rc.senseNearbyRobots(5,us)[0].type == RobotType.TANK) {
                return;
            }
            else {
            TreeInfo[] myTrees = rc.senseNearbyTrees();

            for (TreeInfo tree : myTrees) {
                MapLocation mapLocation = tree.location;
                if (tree.getHealth() < 25) {
                    if (rc.canWater(mapLocation)) {
                        rc.water(mapLocation);
                    }
                }

            }


            // Generate a random direction
            dir = randomDirection();

            if (rc.canPlantTree(dir)) {
                rc.plantTree(dir);
            }

            // Move randomly
            tryMove(randomDirection());

            }

            // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
            Clock.yield();

        } catch (Exception e) {
            System.out.println("Gardener Exception");
            e.printStackTrace();
        }
    }
}
