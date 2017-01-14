package opponents.michael_player;

/**
 * Created by Matt on 1/9/17.
 */


import battlecode.common.*;

public class Globals {


    public static RobotController rc;
    public static MapLocation here;
    public static Team us;
    public static Team them;
    public static int myID;
    public static RobotType myType;

    public static int roundNum;

    public static int numberOfInitialArchon;

    public static MapLocation[] ourInitialArchonLocations;
    public static MapLocation[] theirInitialArchonLocations;

    public static MapLocation centerOfOurInitialArchons;
    public static MapLocation centerOfTheirInitialArchons;
    public static MapLocation centerOfAllInitialArchons;

//    public static RobotInfo[] visibleHostiles = null;
//    public static RobotInfo[] visibleEnemies = null;
//    public static RobotInfo[] visibleZombies = null;
//    public static RobotInfo[] visibleAllies = null;
//    public static RobotInfo[] attackableHostiles = null;

    public static final int TARGET_X_CHANNEL = 0;
    public static final int TARGET_Y_CHANNEL = 1;
    public static final int TARGET_ID_CHANNEL = 2;

    public static void init(RobotController theRC) {
        rc = theRC;
        us = rc.getTeam();
        them = us.opponent();
        myID = rc.getID();
        myType = rc.getType();
        ourInitialArchonLocations = rc.getInitialArchonLocations(us);
        theirInitialArchonLocations = rc.getInitialArchonLocations(them);
        numberOfInitialArchon = ourInitialArchonLocations.length;
        centerOfOurInitialArchons = new MapLocation(0, 0);
        centerOfTheirInitialArchons = new MapLocation(0, 0);
        centerOfAllInitialArchons = new MapLocation(0, 0);
        here = rc.getLocation();
    }

    public static void update() {
        here = rc.getLocation();
        roundNum = rc.getRoundNum();
    }


    /**
     * Returns a random Direction
     *
     * @return a random Direction
     */
    static Direction randomDirection() {
        return new Direction((float) Math.random() * 2 * (float) Math.PI);
    }

    static Direction moveBounce(Direction dir) throws GameActionException {
        // First, try intended direction
        if (rc.canMove(dir) && !rc.hasMoved()) {
            rc.move(dir);
            return dir;
        }

        for (int i = 0; i < 10; i++) {
            dir = randomDirection();
            if (rc.canMove(dir) && !rc.hasMoved()) {
                rc.move(dir);
                return dir;
            }
        }

        return dir;
    }

    /**
     * Only let our bots broadcast on certain rounds.  This way the we can listen on other rounds and instantly locate enemies
     *
     * @param channel
     * @param data
     * @throws GameActionException
     */
    public static void tryBroadcast(int channel, int data) throws GameActionException {
        if (roundNum % 2 == 1) {
            rc.broadcast(channel, data);
        }
    }

    /**
     * True if only enemy is broadcasting
     *
     * @return
     */
    public static boolean isCleanBroadcastRound() {
        return (roundNum % 2 == 0);
    }

    /**
     * Attempts to move in a given direction, while avoiding small obstacles directly in the path.
     *
     * @param dir The intended direction of movement
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir) throws GameActionException {
        return tryMove(dir, 20, 3);
    }

    /**
     * Attempts to move in a given direction, while avoiding small obstacles direction in the path.
     *
     * @param dir           The intended direction of movement
     * @param degreeOffset  Spacing between checked directions (degrees)
     * @param checksPerSide Number of extra directions checked on each side, if intended direction was unavailable
     * @return true if a move was performed
     * @throws GameActionException
     */
    static boolean tryMove(Direction dir, float degreeOffset, int checksPerSide) throws GameActionException {

        // First, try intended direction
        if (rc.canMove(dir)) {
            rc.move(dir);
            return true;
        }

        // Now try a bunch of similar angles
        boolean moved = false;
        int currentCheck = 1;

        while (currentCheck <= checksPerSide) {
            // Try the offset of the left side
            if (rc.canMove(dir.rotateLeftDegrees(degreeOffset * currentCheck))) {
                rc.move(dir.rotateLeftDegrees(degreeOffset * currentCheck));
                return true;
            }
            // Try the offset on the right side
            if (rc.canMove(dir.rotateRightDegrees(degreeOffset * currentCheck))) {
                rc.move(dir.rotateRightDegrees(degreeOffset * currentCheck));
                return true;
            }
            // No move performed, try slightly further
            currentCheck++;
        }

        // A move never happened, so return false.
        return false;
    }

    /**
     * A slightly more complicated example function, this returns true if the given bullet is on a collision
     * course with the current robot. Doesn't take into account objects between the bullet and this robot.
     *
     * @param bullet The bullet in question
     * @return True if the line of the bullet's path intersects with this robot's current position.
     */
    static boolean willCollideWithLocation(BulletInfo bullet, MapLocation location) {
        // Get relevant bullet information
        Direction propagationDirection = bullet.dir;
        MapLocation bulletLocation = bullet.location;

        // Calculate bullet relations to this robot
        Direction directionToRobot = bulletLocation.directionTo(location);
        float distToRobot = bulletLocation.distanceTo(location);
        float theta = propagationDirection.radiansBetween(directionToRobot);

        // If theta > 90 degrees, then the bullet is traveling away from us and we can break early
        if (Math.abs(theta) > Math.PI / 2) {
            return false;
        }

        // distToRobot is our hypotenuse, theta is our angle, and we want to know this length of the opposite leg.
        // This is the distance of a line that goes from myLocation and intersects perpendicularly with propagationDirection.
        // This corresponds to the smallest radius circle centered at our location that would intersect with the
        // line that is the path of the bullet.
        float perpendicularDist = (float) Math.abs(distToRobot * Math.sin(theta)); // soh cah toa :)

        return (perpendicularDist <= rc.getType().bodyRadius);
    }

    public static void evadeBullets() throws GameActionException {
        boolean onCollisionCourse;
        MapLocation currentLoc = rc.getLocation();
        BulletInfo[] bullets = rc.senseNearbyBullets();

        for (BulletInfo bullet : bullets) {
            if (willCollideWithLocation(bullet, currentLoc)) {
                Direction toEvade;
                do {
                    toEvade = randomDirection();
                    MapLocation newLoc = generateNewLocation(currentLoc, toEvade);
                    onCollisionCourse = false;
                    for (BulletInfo b : bullets) {
                        if (willCollideWithLocation(b, newLoc)) {
                            onCollisionCourse = true;
                        }
                    }
                } while (onCollisionCourse);

                if (rc.canMove(toEvade) && !rc.hasMoved()) {
                    rc.move(toEvade);
                }
            }
        }
    }

    public static MapLocation generateNewLocation(MapLocation currentLoc, Direction direction) {
        return new MapLocation(currentLoc.x + direction.getDeltaX(RobotType.SCOUT.strideRadius),
                currentLoc.y + direction.getDeltaY(RobotType.SCOUT.strideRadius));
    }

}