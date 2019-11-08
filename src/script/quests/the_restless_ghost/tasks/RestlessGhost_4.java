package script.quests.the_restless_ghost.tasks;


import org.rspeer.runetek.adapter.Interactable;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.Varps;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

import static script.quests.the_restless_ghost.data.Quest.THE_RESTLESS_GHOST;


public class RestlessGhost_4 extends Task {

    @Override
    public boolean validate() {
        return THE_RESTLESS_GHOST.getVarpValue() == 4;

    }

    private void useItemOn(String itemName, Interactable target) {
        if (Inventory.isItemSelected()) {
            if (target.interact("Use")) {
                Time.sleepUntil(() -> Varps.get(107) != 4, 30 * 1000);
            }
        } else {
            Inventory.getFirst(itemName).interact("Use");
        }
    }

    public int randomSleep() {
        if (Movement.isRunEnabled()) {
            return Random.mid(2500, 4000);
        } else {
            return Random.mid(4000, 9000);
        }
    }

    @Override
    public int execute() {

        Player local = Players.getLocal();

        Log.info("hello");
        SceneObject ladder = SceneObjects.getFirstAt(new Position(3103, 9576, 0));
        SceneObject coffinClosed = SceneObjects.getNearest(2145);
        SceneObject coffinOpen = SceneObjects.getNearest(15061);
        if(ladder == null){
            Log.info("1");
        }
        if(coffinClosed == null){
            Log.info("2");
        }
        if(coffinOpen == null){
            Log.info("3");
        }
        if (Movement.getRunEnergy() > 20 && !Movement.isRunEnabled()) { //Turn on run if it's off with over 20 energy
            Movement.toggleRun(true);

        }
        if (Inventory.contains(553)) {
            if (ladder != null) {
                if (Movement.walkToRandomized(ladder)) {
                    if (ladder.interact("Climb-Up")) {
                        Time.sleepUntil(() -> ladder == null, 5000);
                    }
                }
            }
            if (coffinClosed != null) {
                Movement.walkTo(coffinClosed);
                Time.sleepUntil(() -> (local.isMoving()), 10000);
                Time.sleepUntil(() -> (!local.isMoving()), randomSleep());
                coffinClosed.interact("Open");
            } else if (coffinOpen != null) {
                Movement.walkTo(coffinOpen);
                Time.sleepUntil(() -> (local.isMoving()), 10000);
                Time.sleepUntil(() -> (!local.isMoving()), randomSleep());
                useItemOn("Ghost's Skull", coffinOpen);

            }
            if (ladder == null && coffinClosed == null && coffinOpen == null) {
                Movement.walkTo(new Position(3249, 3192, 0));
                Time.sleepUntil(() -> (local.isMoving()), 10000);
                Time.sleepUntil(() -> (!local.isMoving()), randomSleep());
            }
        }

        return 300;
    }
}



