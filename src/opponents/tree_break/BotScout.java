package tree_break;

import battlecode.common.*;

import java.util.Map;

/**
 * Created by Matt on 1/9/17.
 */
public class BotScout extends Globals{

    public static void loop() {
        System.out.println("I'm a scout");


        // The code you want your robot to perform every round should be in this loop
        while (true) {
            Globals.update();
            // Try/catch blocks stop unhandled exceptions, which cause your robot to explode
            try {
                Globals.update();
                MapLocation myLocation = rc.getLocation();


                // See if there are any nearby enemy robots
                RobotInfo[] robots = rc.senseNearbyRobots(-1, them);

                // If there are some...
                if (robots.length > 0) {
                    MapLocation robotPlace = robots[0].location;
                    Globals.tryBroadcast(0,(int) robotPlace.x);
                    Globals.tryBroadcast(1,(int) robotPlace.y);


                    // And we have enough bullets, and haven't attacked yet this turn...
                    if (rc.canFireSingleShot()) {
                        // ...Then fire a bullet in the direction of the enemy.
                        rc.fireSingleShot(rc.getLocation().directionTo(robots[0].location));
                    }
                }



                // Move randomly
                tryMove(randomDirection());

                // Clock.yield() makes the robot wait until the next turn, then it will perform this loop again
                Clock.yield();

            } catch (Exception e) {
                System.out.println("Soldier Exception");
                e.printStackTrace();
            }
        }
    }
}
