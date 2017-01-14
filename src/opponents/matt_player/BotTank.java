package matt_player;

import battlecode.common.Clock;
import battlecode.common.RobotInfo;

/**
 * Created by Matt on 1/9/17.
 */
public class BotTank extends Globals {

    public static void loop() {
        Globals.update();
        System.out.println("I'm a tank");

        // The code you want your robot to perform every round should be in this loop
        while (true) {

            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                Globals.update();

                RobotInfo[] robots = rc.senseNearbyRobots(-1, them);


                Movement.moveToEnemy(robots);

                // If there are some...
                if (robots.length > 0) {
                    // And we have enough bullets, and haven't attacked yet this turn...
                    if (rc.canFirePentadShot()) {
                        // ...Then fire a bullet in the direction of the enemy.
                        rc.firePentadShot(rc.getLocation().directionTo(robots[0].location));
                    }
                }


                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Soldier Exception");
                e.printStackTrace();
            }
        }
    }
}
