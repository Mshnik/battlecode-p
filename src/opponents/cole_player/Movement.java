package opponents.cole_player;

import battlecode.common.*;

/**
 * Created by cole on 1/11/17.
 */
public class Movement extends Globals {


    public static void routeInit(MapLocation newGoal) throws GameActionException {
        goal = newGoal;
        goalDirection = here.directionTo(newGoal);
    }

    public static void routeRun() throws GameActionException {
        try {
            //move directly toward the goal if we have a direct path
            if (rc.canMove(goalDirection, (float) 0.5) && !rc.hasMoved()) { // see if we can move 0.5 units directly toward our goal
                rc.move(goalDirection);
                return;
            }


        } catch (Exception e){
            System.out.println("Bug Route Move Error");
            e.printStackTrace();
        }
    }

}
