package script.quests.nature_spirit.tasks;

import script.quests.nature_spirit.NatureSpirit;
import script.quests.nature_spirit.data.Location;
import script.quests.nature_spirit.wrappers.WalkingWrapper;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.input.menu.ActionOpcodes;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;

public class NatureSpirit6 extends Task {

    @Override
    public boolean validate() {
        return false /*Quest.NATURE_SPIRIT.getVarpValue() == 40*/;
    }

    @Override
    public int execute() {
        if (Dialog.isOpen()) {
            if (Dialog.canContinue())
                Dialog.processContinue();
        }

        if (Location.ROTTING_LOG_POSITION.distance() > 20) {
            Movement.walkTo(Location.ROTTING_LOG_POSITION, () -> WalkingWrapper.shouldBreakWalkLoop()
                    || (Location.ROTTING_LOG_POSITION.distance() <= 20) && SceneObjects.getNearest("Rotting log") != null && SceneObjects.getNearest("Rotting log").distance() < 4);
        }

        SceneObject log = SceneObjects.getNearest("Rotting log");
        Item spell = Inventory.getFirst("Druidic spell");

        if (log != null  && log.distance() < 4 && spell != null && spell.interact(ActionOpcodes.ITEM_ACTION_0)) {
            Time.sleepUntil(() -> Players.getLocal().isAnimating(), 2000);
            Time.sleepUntil(() -> !Players.getLocal().isAnimating(), 5000);
        }

        return NatureSpirit.getLoopReturn();
    }
}
