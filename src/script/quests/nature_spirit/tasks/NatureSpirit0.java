package script.quests.nature_spirit.tasks;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;
import script.quests.nature_spirit.NatureSpirit;
import script.quests.nature_spirit.data.Location;
import script.quests.nature_spirit.data.Quest;
import script.quests.nature_spirit.wrappers.WalkingWrapper;

public class NatureSpirit0 extends Task {

    @Override
    public boolean validate() {
        return Quest.NATURE_SPIRIT.getVarpValue() == 0;
    }

    @Override
    public int execute() {
        if (!Location.DUNGEON_AREA.contains(Players.getLocal()) && Location.ENTRANCE.distance() > 3) {
            Movement.walkTo(Location.ENTRANCE, WalkingWrapper::shouldBreakWalkLoop);
            if (Location.ENTRANCE.distance() > 3 && !Movement.isRunEnabled()) {
                Movement.toggleRun(true);
            }
        } else {
            SceneObject trapdoor = SceneObjects.getNearest("Trapdoor");
            if (trapdoor != null && trapdoor.interact(a -> true)) {
                Time.sleepUntil(() -> SceneObjects.getNearest("Trapdoor") == null, 1000, 10_000);
            }
        }

        Npc drezel = Npcs.getNearest("Drezel");

        if (drezel == null || !drezel.isPositionInteractable()) {
            Movement.walkTo(Location.DREZEL_POSITION);
        }
        else if (!Dialog.isOpen()) {
            drezel.interact("Talk-to");
        }

        if (Dialog.isOpen()) {
            Dialog.process("Is there anything else interesting to do around here?",
                            "Well, what is it, I may be able to help?",
                            "Yes, I'll go and look for him.",
                            "Yes, I'm sure.");

            if (Dialog.canContinue())
                Dialog.processContinue();
        }

        return NatureSpirit.getLoopReturn();
    }

    private boolean hasPies() {
        return Inventory.getCount("Apple pie") >= 3 && Inventory.getCount("Meat pie") >= 3;
    }
}
