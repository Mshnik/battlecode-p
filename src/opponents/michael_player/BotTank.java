package michael_player;

import battlecode.common.Clock;
import battlecode.common.RobotInfo;
import battlecode.common.Direction;

/**
 * Created by Matt on 1/9/17.
 */
public class BotTank extends Globals {

    public static void loop() {
        System.out.println("I'm a tank");
        Direction moveDir = randomDirection();

        // The code you want your robot to perform every round should be in this loop
        while (true) {
            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                update();

                RobotInfo[] robots = rc.senseNearbyRobots(-1, them);

                // shoot bullets
                if (robots.length > 0) {
                    // And we have enough bullets, and haven't attacked yet this turn...
                    if (rc.canFirePentadShot()) {
                        // ...Then fire a bullet in the direction of the enemy.
                        rc.firePentadShot(rc.getLocation().directionTo(robots[0].location));
                    }
                }

                for (RobotInfo robot : robots) {
                    if (robot.getTeam() == them) {
                        Direction toEnemy = rc.getLocation().directionTo(robot.getLocation());
                        if (rc.canMove(toEnemy) && !rc.hasMoved()) {
                            rc.move(toEnemy);
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
