package script.quests.the_restless_ghost.tasks;

import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;

import static script.quests.the_restless_ghost.data.Quest.THE_RESTLESS_GHOST;

public class RestlessGhost_3 extends Task {

    private static final Position LADDER_POSITION = new Position(3105, 3160, 0);

    @Override
    public boolean validate() {
        return THE_RESTLESS_GHOST.getVarpValue() == 3;
    }

    @Override
    public int execute() {
        Player local = Players.getLocal();
        if (Movement.getRunEnergy() > 20 && !Movement.isRunEnabled()) { //Turn on run if it's off with over 20 energy
            Movement.toggleRun(true);

        }
        SceneObject altar = SceneObjects.getNearest(2146);
        if (altar == null) {
            SceneObject ladder = SceneObjects.getNearest(2147);
            if (ladder == null) {
                Movement.walkToRandomized(LADDER_POSITION);
            }
            if (ladder != null) {
                if (!ladder.isPositionInteractable()) {
                    Movement.walkToRandomized(ladder);
                }
                if (ladder.isPositionInteractable()) {
                    if (ladder.interact("Climb-down")) {
                        Time.sleepUntil(()-> altar != null, 5000);
                    }
                }
            }
        }
        if (altar != null) {
            if (!Inventory.contains(553)) {
                if (!altar.isPositionInteractable()) {
                    Movement.walkToRandomized(altar);
                }
                if (altar.isPositionInteractable()) {
                    altar.interact("Search");
                }
            }
            if (Inventory.contains(553)) {
                Movement.walkToRandomized(new Position(3105, 3160, 0));
            }
        }
        return 300;
    }
}
