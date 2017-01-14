package michael_player;

import battlecode.common.*;

/**
 * Created by Matt on 1/9/17.
 */
public class BotScout extends Globals {

    private static Direction moveDir = randomDirection();

    public static void loop() {
        System.out.println("I'm a scout");

        // The code you want your robot to perform every round should be in this loop
        while (true) {
            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                update();

                // Move and evade bullets
                evadeBullets();
                mobileMode();
                moveDir = moveBounce(moveDir);

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Scout Exception");
                e.printStackTrace();
            }
        }
    }

    public static boolean inTree = false;

    public static int cantFindEnemyCount = 0;

    public static void mobileMode() throws GameActionException {
        int targetX = rc.readBroadcast(TARGET_X_CHANNEL);
        int targetY = rc.readBroadcast(TARGET_Y_CHANNEL);
        int targetID = rc.readBroadcast(TARGET_ID_CHANNEL);

        System.out.println("targetX: " + targetX);
        System.out.println("targetY: " + targetY);
        System.out.println("targetID: " + targetID);
        if (inTree) {
            System.out.println("Staying");
        }

        RobotInfo[] robots = rc.senseNearbyRobots(-1, them);

        MapLocation target = new MapLocation(targetX, targetY);

        if (targetID != 0) {
            targetMode(robots, target, targetID);
        } else {
            noTargetMode(robots);
        }
    }

    public static void targetMode(RobotInfo[] robots, MapLocation target, int targetID) throws GameActionException {
        // target exists
        for (int i = 0; i < robots.length; i++) {
            RobotInfo enemyCurRobot = robots[i];
            // target found
            if (enemyCurRobot.getID() == targetID) {
                // enemy moved
                if (!enemyCurRobot.location.isWithinDistance(target, (float) 0.5)) {
                    rc.broadcast(TARGET_X_CHANNEL, (int) enemyCurRobot.location.x);
                    rc.broadcast(TARGET_Y_CHANNEL, (int) enemyCurRobot.location.y);
                }

                // enemy died
                if (enemyCurRobot.getHealth() <= 1) {
                    trySingleShot(enemyCurRobot.location);
                    rc.broadcast(TARGET_X_CHANNEL, 0);
                    rc.broadcast(TARGET_Y_CHANNEL, 0);
                    rc.broadcast(TARGET_ID_CHANNEL, 0);
                    System.out.println("Enemy annihilated");
                    return;
                }

                // hide in trees
                TreeInfo[] trees = rc.senseNearbyTrees(target, 5, them);
                for (int j = 0; j < trees.length; j++) {
                    TreeInfo curTree = trees[j];
                    rc.setIndicatorDot(curTree.location, 255, 0, 255);
                    if (curTree.location.isWithinDistance(here, (float) 2.5)) {
                        if (rc.canMove(curTree.location)) {
                            rc.move(curTree.location);
                            inTree = true;
                            break;
                        }
                    }
                }

                if (!inTree) {
                    if (rc.canMove(here.directionTo(target)) && !rc.hasMoved()) {
                        rc.move(here.directionTo(target));
                    }
                }

                trySingleShot(target);
                return;
            }
        }
        // target not found
        if (rc.canMove(here.directionTo(target)) && !rc.hasMoved()) {
            cantFindEnemyCount++;
            rc.move(here.directionTo(target));
            if (cantFindEnemyCount > 20) {
                rc.broadcast(TARGET_X_CHANNEL, 0);
                rc.broadcast(TARGET_Y_CHANNEL, 0);
                rc.broadcast(TARGET_ID_CHANNEL, 0);
                cantFindEnemyCount = 0;
            }
        }
    }

    public static void noTargetMode(RobotInfo[] robots) throws GameActionException {
        // target doesn't exist
        inTree = false;
        if (robots.length > 0) {
            for (int i = 0; i < robots.length; i++) {
                if (robots[i].type == RobotType.GARDENER) {
                    MapLocation target = robots[i].location;
                    Globals.tryBroadcast(TARGET_X_CHANNEL, (int) target.x);
                    Globals.tryBroadcast(TARGET_Y_CHANNEL, (int) target.y);
                    Globals.tryBroadcast(TARGET_ID_CHANNEL, robots[i].getID());

                    if (rc.canMove(here.directionTo(target)) && !rc.hasMoved()) {
                        rc.move(here.directionTo(target));
                    }

                    trySingleShot(target);

                    return;
                }
            }
        }
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
}
