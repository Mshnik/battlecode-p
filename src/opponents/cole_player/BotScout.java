package opponents.cole_player;


import battlecode.common.*;

import static battlecode.common.GameConstants.BROADCAST_MAX_CHANNELS;

/**
 * Created by cole on 1/11/17.
 */
public class BotScout extends Globals {
    public static void loop() throws GameActionException {
        System.out.println("I'm a Scout. Fear me!");
        Direction moveDir = new Direction(0);
        try{
            moveDir = randomDirection();
        } catch(Exception e){
            System.out.println("Scout Error");
            e.printStackTrace();
        }

        while(true){
            try{
                update();
                int enemyCount = 0;
                RobotInfo[] nearbyRobots = rc.senseNearbyRobots();
                for(int i = 0; i < nearbyRobots.length; i++){
                    if(nearbyRobots[i].team == them){
                        enemyCount++;
                    }
                }
                if(enemyCount > 2){
                    int x = (int) Math.floor(here.x);
                    int y = (int) Math.floor(here.y);
                    System.out.println("Enemy Here: X: " + x + ", Y:" + y);
                    rc.broadcast(2, x);
                    rc.broadcast(3, y);
                }
                moveDir = moveBounce(moveDir);


                Clock.yield();
            } catch (Exception e){
                System.out.println("Scout Exception:");
                e.printStackTrace();
            }
        }
    }
}
