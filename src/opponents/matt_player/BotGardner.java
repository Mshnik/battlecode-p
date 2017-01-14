package matt_player;

import battlecode.common.*;
import jdk.nashorn.internal.runtime.GlobalConstants;

/**
 * Created by Matt on 1/9/17.
 */
public class BotGardner extends Globals {

    public static void loop() throws GameActionException {


        // The code you want your robot to perform every round should be in this loop
        while (true) {
            Globals.update();

            if (roundNum == 2) {
                secondRound();
            }


            if (rc.getTeamBullets() >= 10000) {
                rc.donate(10_000);
            }
            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {

                waterTree();

                // Generate a random direction
                Direction dir = randomDirection();

                if (rc.canPlantTree(dir)) {
                    rc.plantTree(dir);
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

    private static void secondRound() throws GameActionException {
        int scoutCount = rc.readBroadcast(SCOUT_LIFE_CHANNEL);
        System.out.println("Scout count == "+scoutCount);
        if (scoutCount == 0) {
            int dirs = 6;
            float directionOffset = (float) Math.PI / dirs;

            //try directions to build gardeners in
            for (int i = 1; i < dirs; i++) {
                Direction dir = new Direction((i * directionOffset));
                if (rc.canBuildRobot(RobotType.SCOUT, dir)) {
                    rc.buildRobot(RobotType.SCOUT, dir);
                    scoutCount++;
                    rc.broadcast(SCOUT_LIFE_CHANNEL, scoutCount);
                }
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

//    public static void moveAway() {
//
//    }
}
