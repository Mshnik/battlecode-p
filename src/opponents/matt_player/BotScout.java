package opponents.matt_player;

import battlecode.common.*;


/**
 * Created by Matt on 1/9/17.
 */
public class BotScout extends Globals {

    public static boolean bouncing = true;


    public static Direction moveDir;
    public static void loop() {
        moveDir = randomDirection();
        RobotInfo mom;
        TreeInfo[] trees;

        try {
            RobotInfo[] nearbyRobots = rc.senseNearbyRobots(1, us);
            if (nearbyRobots.length > 0) {
                mom = rc.senseNearbyRobots(1, us)[0];
            }
            trees = rc.senseNearbyTrees(2, us);
            for (int i = 0; i < trees.length; i++) {
                if (rc.canMove(trees[i].location)) {
                    rc.move(trees[i].location);
                    bouncing = false;
                }
            }
        }
        catch (Exception e) {
            System.out.println("Scout exception");
            e.printStackTrace();
        }


        while (true) {
            try {
                Globals.update();


                if (!bouncing) {
                    mobileMode();
//                    stationaryMode();
                }

                else {
                    mobileMode();
                }


                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Soldier Exception");
                e.printStackTrace();
            }
        }
    }


    public static void stationaryMode() throws GameActionException {
        RobotInfo[] robots = rc.senseNearbyRobots(-1, them);
        if (robots.length > 0) {

        }
    }


    public static void killGardener() throws GameActionException {

    }

    public static void trySingleShot(MapLocation target) throws GameActionException {
        System.out.println("Tried single shot");

        if (here.isWithinDistance(target, (float) 2.5)) {
            if (rc.canFireSingleShot()) {
                System.out.println("Shooting");
                rc.fireSingleShot(here.directionTo(target));
            }
        }
    }


    public static boolean staying = true;

    public static void mobileMode() throws GameActionException {
        int targetX = rc.readBroadcast(TARGET_X_CHANNEL);
        int targetY = rc.readBroadcast(TARGET_Y_CHANNEL);
        int targetID = rc.readBroadcast(TARGET_ID_CHANNEL);

        System.out.println("targetX: "+targetX);
        System.out.println("targetY: "+targetY);
        System.out.println("targetID: "+targetID);
        if (staying) {
            System.out.println("Staying");
        }

        RobotInfo[] robots = rc.senseNearbyRobots(-1, them);

        MapLocation target = new MapLocation(targetX, targetY);

        if (targetID != 0) {
            for (int i = 0; i < robots.length; i++) {
                RobotInfo enemyCurRobot = robots[i];
                if (enemyCurRobot.getID() == targetID) {
                    if (!enemyCurRobot.location.isWithinDistance(target, (float) 0.5)) {
                        rc.broadcast(TARGET_X_CHANNEL, (int) enemyCurRobot.location.x);
                        rc.broadcast(TARGET_Y_CHANNEL, (int) enemyCurRobot.location.y);
                    }

                    if (enemyCurRobot.getHealth() <= 1) {
                        rc.broadcast(TARGET_X_CHANNEL,0);
                        rc.broadcast(TARGET_Y_CHANNEL,0);
                        rc.broadcast(TARGET_ID_CHANNEL,0);
                        System.out.println("Enemy annihilated");
                    }

                    TreeInfo[] trees = rc.senseNearbyTrees(target, 5, them);
                    for (int j = 0; j < trees.length; j++) {
                        TreeInfo curTree = trees[j];
                        rc.setIndicatorDot(curTree.location, 255, 0, 255);
                        if (curTree.location.isWithinDistance(here, (float) 2.5)) {
                            if (rc.canMove(curTree.location)) {
                                rc.move(curTree.location);
                                staying = true;
                                break;
                            }
                        }
                    }


                    if (!staying) {
                        if (!rc.hasMoved()) {
                            if (rc.canMove(target)) {
                                rc.move(target);
                            }
                            else {
                                float moveDist = (float) 1.0;
                                while (!rc.hasMoved()) {
                                    if (rc.canMove(here.directionTo(target), moveDist)) {
                                        rc.move(here.directionTo(target), moveDist);
                                    }
                                    moveDist /= 2.0;
                                }
                            }
                        }
                    }

                    trySingleShot(target);
                }
            }
        }


        else {
            staying = false;
            if (robots.length > 0) {
                for (int i = 0; i < robots.length; i++) {
                    if (robots[i].type == RobotType.GARDENER) {
                        target = robots[i].location;
                        Globals.tryBroadcast(TARGET_X_CHANNEL,(int) target.x);
                        Globals.tryBroadcast(TARGET_Y_CHANNEL,(int) target.y);
                        Globals.tryBroadcast(TARGET_ID_CHANNEL, robots[i].getID());


                        if (rc.canMove(here.directionTo(target)) && !rc.hasMoved()) {
                            rc.move(here.directionTo(target));
                        }

                        trySingleShot(target);

                        break;
                    }
                }
                if (!rc.hasMoved()) {
                    moveDir = moveBounce(moveDir);
                }

            }
            else {
                moveDir = moveBounce(moveDir);
            }

        }
    }

}
