package opponents.michael_player;

import battlecode.common.*;

/**
 * Created by Matt on 1/9/17.
 */
public class BotSoldier extends Globals {
    public static void loop() throws GameActionException {
        System.out.println("I'm an soldier!");
        Team enemy = rc.getTeam().opponent();
        Direction moveDir = randomDirection();

        // The code you want your robot to perform every round should be in this loop
        while (true) {
            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                update();
                MapLocation myLocation = rc.getLocation();

                // See if there are any nearby enemy robots
                RobotInfo[] robots = rc.senseNearbyRobots(-1, enemy);

                // If there are some...
                if (robots.length > 0) {
                    // And we have enough bullets, and haven't attacked yet this turn...
                    if (rc.canFireSingleShot()) {
                        // ...Then fire a bullet in the direction of the enemy.
                        rc.fireSingleShot(rc.getLocation().directionTo(robots[0].location));
                    }
                }

                // Move
                evadeBullets();
                for (RobotInfo robot : robots) {
                    if (robot.getTeam() == them) {
                        if (rc.canMove(rc.getLocation()) && !rc.hasMoved()) {
                            rc.move(rc.getLocation());
                        }
                    }
                }
                moveDir = moveBounce(moveDir);

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Soldier Exception");
                e.printStackTrace();
            }
        }
    }
}
