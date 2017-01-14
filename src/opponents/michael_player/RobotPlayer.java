
package michael_player;

import battlecode.common.*;

import java.util.Map;

public class RobotPlayer extends Globals {
    /**
     * run() is the method that is called when a robot is instantiated in the Battlecode world.
     * If this method returns, the robot dies!
     **/
    @SuppressWarnings("unused")
    public static void run(RobotController theRC) throws GameActionException {
        Globals.init(theRC);

        // Here, we've separated the controls into a different method for each RobotType.
        // You can add the missing ones or rewrite this into your own control structure.
        switch (rc.getType()) {
            case ARCHON:
                BotArchon.loop();
                break;
            case GARDENER:
                BotGardner.loop();
                break;
            case SOLDIER:
                BotSoldier.loop();
                break;
            case LUMBERJACK:
                BotLumberjack.loop();
                break;
            case TANK:
                BotTank.loop();
                break;
            case SCOUT:
                BotScout.loop();
                break;
        }
    }


}