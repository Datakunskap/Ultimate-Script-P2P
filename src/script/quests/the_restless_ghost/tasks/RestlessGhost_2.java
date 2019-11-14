package script.quests.the_restless_ghost.tasks;

import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;

import java.util.function.Predicate;

import static script.quests.the_restless_ghost.data.Quest.THE_RESTLESS_GHOST;


public class RestlessGhost_2 extends Task {
    private static final Predicate<String> YEP_NOW_TELL_ME_PREDICATE = o -> o.contains("Yep, now tell me what");

    private final Player local = Players.getLocal();

    public int randomSleep(){
        if (Movement.isRunEnabled()) {
            return Random.mid(2500,4000);
        } else {
            return Random.mid(4000,9000);
        }
    }

    @Override
    public boolean validate() {
        return THE_RESTLESS_GHOST.getVarpValue() == 2;
    }

    @Override
    public int execute() {
        final Predicate<Item> Necklace = item -> item.getId() == 552;
        if (Movement.getRunEnergy() > 20 && !Movement.isRunEnabled()) { //Turn on run if it's off with over 20 energy
            Movement.toggleRun(true);

        }
        if (Inventory.getFirst(Necklace) != null) {
            Inventory.getFirst(Necklace).interact("Wear");
        }
        if (Dialog.isOpen()){
            if (Dialog.canContinue()) {
                if (Dialog.processContinue()) {
                }
            }
            Dialog.process("Yep, now tell me what");
        } else {
                Npc ghost = Npcs.getNearest(922);
                SceneObject coffinClosed = SceneObjects.getNearest(2145);
                SceneObject coffinOpen = SceneObjects.getNearest(15061);

                    if (ghost != null) {
                        ghost.interact("Talk-to");
                    } else {
                        if (coffinClosed != null) {
                            Movement.walkTo(coffinClosed);
                            Time.sleepUntil(() -> (local.isMoving()), 10000);
                            Time.sleepUntil(() -> (!local.isMoving()), randomSleep());
                            coffinClosed.interact("Open");
                        } else if (coffinOpen != null) {
                            Movement.walkTo(coffinOpen);
                            Time.sleepUntil(() -> (local.isMoving()), 10000);
                            Time.sleepUntil(() -> (!local.isMoving()), randomSleep());
                            coffinOpen.interact("Search");
                        } else {
                            Movement.walkTo(new Position(3249, 3192, 0));
                            Time.sleepUntil(() -> (local.isMoving()), 10000);
                            Time.sleepUntil(() -> (!local.isMoving()), randomSleep());
                        }
                    }

        }
        return 300;
    }
}


