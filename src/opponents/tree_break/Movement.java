package tree_break;

import battlecode.common.Direction;
import battlecode.common.GameActionException;
import battlecode.common.MapLocation;
import battlecode.common.RobotInfo;

/**
 * Created by Matt on 1/11/17.
 */
public class Movement extends Globals {

    public static void moveToEnemy() throws GameActionException {
        RobotInfo[] sensedRobots = rc.senseNearbyRobots(-1, them);
        MapLocation[] broadcastingBots = rc.senseBroadcastingRobotLocations();

        Direction toEnemy = null;

        // First move towards
        if (sensedRobots.length > 0) {
            toEnemy = new Direction(here, sensedRobots[0].location);
        }
        else {
            if (broadcastingBots.length > 0) {
                toEnemy = new Direction(here, broadcastingBots[0]);
            }
            else {
                int x = rc.readBroadcast(0);
                int y = rc.readBroadcast(1);

                if (x != 0 || y != 0) {
                    toEnemy = new Direction(here, new MapLocation(x,y));
                }
                else {
                    toEnemy = randomDirection();
                }
            }
        }
        tryMove(toEnemy);
    }
    public static void moveToEnemy(RobotInfo[] sensedRobots) throws GameActionException {
        MapLocation[] broadcastingBots = rc.senseBroadcastingRobotLocations();

        Direction toEnemy = null;

        // First move towards
        if (sensedRobots.length > 0) {
            toEnemy = new Direction(here, sensedRobots[0].location);
        }
        else {
            if (broadcastingBots.length > 0) {
                toEnemy = new Direction(here, broadcastingBots[0]);
            }
            else {
                int x = rc.readBroadcast(0);
                int y = rc.readBroadcast(1);

                if (x != 0 || y != 0) {
                    toEnemy = new Direction(here, new MapLocation(x,y));
                }
                else {
                    toEnemy = randomDirection();
                }
            }
        }
        tryMove(toEnemy);
    }

    public static Direction directionToEnemy() throws GameActionException {
        RobotInfo[] sensedRobots = rc.senseNearbyRobots(-1, them);
        MapLocation[] broadcastingBots = rc.senseBroadcastingRobotLocations();

        Direction toEnemy = null;

        // First move towards
        if (sensedRobots.length > 0) {
            toEnemy = new Direction(here, sensedRobots[0].location);
        }
        else {
            if (broadcastingBots.length > 0) {
                toEnemy = new Direction(here, broadcastingBots[0]);
            }
            else {
                int x = rc.readBroadcast(0);
                int y = rc.readBroadcast(1);

                if (x != 0 || y != 0) {
                    toEnemy = new Direction(here, new MapLocation(x,y));
                }
                else {
                    toEnemy = randomDirection();
                }
            }
        }
        return toEnemy;
    }

    public static Direction directionToEnemy(RobotInfo[] sensedRobots) throws GameActionException {
        MapLocation[] broadcastingBots = rc.senseBroadcastingRobotLocations();

        Direction toEnemy = null;

        // First move towards
        if (sensedRobots.length > 0) {
            toEnemy = new Direction(here, sensedRobots[0].location);
        }
        else {
            if (broadcastingBots.length > 0) {
                toEnemy = new Direction(here, broadcastingBots[0]);
            }
            else {
                int x = rc.readBroadcast(0);
                int y = rc.readBroadcast(1);

                if (x != 0 || y != 0) {
                    toEnemy = new Direction(here, new MapLocation(x,y));
                }
                else {
                    toEnemy = randomDirection();
                }
            }
        }
        return toEnemy;
    }
}
