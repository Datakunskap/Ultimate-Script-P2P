package script.quests.nature_spirit.tasks;

import script.quests.nature_spirit.NatureSpirit;
import script.quests.nature_spirit.data.Location;
import script.quests.nature_spirit.data.Quest;
import script.quests.nature_spirit.wrappers.WalkingWrapper;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.Pickable;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.input.menu.ActionOpcodes;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Pickables;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

public class NatureSpirit3 extends Task {


    @Override
    public boolean validate() {
        return Quest.NATURE_SPIRIT.getVarpValue() == 20;
    }

    @Override
    public int execute() {
        if (!Location.NATURE_GROTTO_AREA.contains(Players.getLocal())) {
            WalkingWrapper.walkToNatureGrotto();
            Movement.toggleRun(true);
            return NatureSpirit.getLoopReturn();
        }
        if (Inventory.isFull()) {
            WalkingWrapper.consumeFirstConsumable();
        }

        if (!Inventory.contains("Mirror")) {

            Pickable bowl = Pickables.getNearest("Washing bowl");
            Pickable mirror = Pickables.getNearest("Mirror");

            if (mirror == null && bowl != null && bowl.interact("Take")) {
                Time.sleepUntil(() -> Inventory.contains("Washing bowl"), 5000);
            }

            if (mirror != null && mirror.interact("Take")) {
                Time.sleepUntil(() -> Inventory.contains("Mirror"), 5000);
            }
        }

        if (Dialog.isOpen()) {
            Dialog.processContinue();

            if (Dialog.isViewingChatOptions())
                Dialog.process("Ok, thanks.");

            return NatureSpirit.getLoopReturn();
        }

        if (!Dialog.isOpen() && Inventory.contains("Mirror")) {
            Npc filliman = Npcs.getNearest("Filliman Tarlock");

            if (filliman != null && filliman.interact("Talk-to")) {
                Time.sleepUntil(Dialog::isOpen, 5000);
            }

            if (filliman == null && !Dialog.isOpen()) {
                WalkingWrapper.enterGrotto();
            }

            Inventory.getFirst("Mirror").interact("Use");
            Time.sleepUntil(Inventory::isItemSelected, 5000);
            Time.sleep(600, 800);

            if (filliman != null && filliman.interact(ActionOpcodes.ITEM_ON_NPC)) {
                Log.fine("Using Mirror");
                Time.sleepUntil(() -> !Players.getLocal().isAnimating() && Dialog.isOpen(), 5000);
            }
        }

        return NatureSpirit.getLoopReturn();
    }
}
