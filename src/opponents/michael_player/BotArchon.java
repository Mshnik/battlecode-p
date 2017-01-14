package opponents.michael_player;

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
        Direction moveDir = randomDirection();

        // The code you want your robot to perform every round should be in this loop
        while (true) {
            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                update();

                // Randomly attempt to build a gardener
                Direction dir = randomDirection();
                if (rc.canHireGardener(dir)) {
                    rc.hireGardener(dir);
                }

                // Move randomly
                moveDir = moveBounce(moveDir);

                // Broadcast archon's location for other robots on the team to know
                MapLocation myLocation = rc.getLocation();
                // TODO screwing with Matt's scout broadcasting code
//                rc.broadcast(0, (int) myLocation.x);
//                rc.broadcast(1, (int) myLocation.y);

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Archon Exception");
                e.printStackTrace();
            }
        }
    }
}
