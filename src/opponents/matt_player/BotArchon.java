package matt_player;

import battlecode.common.Clock;
import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;


/**
 * Created by Matt on 1/9/17.
 */
public class BotArchon extends Globals {
    public static void loop() throws GameActionException {
        System.out.println("I'm an archon!");
        while (true) {
            try {
                Globals.update();
                turn();
                Clock.yield();
            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
        }
    }

    public static void turn() throws GameActionException {
        if (roundNum == 1) {
            firstTurn();
        }
        else if (roundNum < 500) {
            lowRoundBehavior();
        }
        else {
            otherBehavior();
        }
    }

    private static void firstTurn() throws GameActionException {
        System.out.println("First round");
        int gardenerCount = rc.readBroadcast(GARDENER_LIFE_CHANNEL);

        if (gardenerCount < 2) {
            int dirs = 6;
            float directionOffset = (float) Math.PI / dirs;

            //try directions to build gardeners in
            for (int i = 1; i < dirs; i++) {
                Direction d = new Direction((i * directionOffset));
                if (rc.canHireGardener(d)) {
                    rc.hireGardener(d);
                    gardenerCount++;
                    rc.broadcast(GARDENER_LIFE_CHANNEL, gardenerCount);
                }
            }
        }
    }





    public static void lowRoundBehavior() throws GameActionException {
        Direction dir = randomDirection();
        if (rc.canHireGardener(dir)) {
            rc.hireGardener(dir);
        }

    }

    public static void otherBehavior() throws GameActionException {
        Direction dir = randomDirection();
        tryMove(randomDirection());

    }
}
