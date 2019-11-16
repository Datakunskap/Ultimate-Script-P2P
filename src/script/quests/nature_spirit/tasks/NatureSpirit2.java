package script.quests.nature_spirit.tasks;

import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.Inventory;
import script.data.Locations;
import script.quests.nature_spirit.NatureSpirit;
import script.quests.nature_spirit.data.Location;
import script.quests.nature_spirit.data.Quest;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;
import script.quests.nature_spirit.wrappers.WalkingWrapper;

public class NatureSpirit2 extends Task {

    @Override
    public boolean validate() {
        return Quest.NATURE_SPIRIT.getVarpValue() == 10 || Quest.NATURE_SPIRIT.getVarpValue() == 15;
    }

    @Override
    public int execute() {
        if (!Locations.NATURE_GROTTO_AREA.contains(Players.getLocal())) {
            WalkingWrapper.walkToNatureGrotto();
            return NatureSpirit.getLoopReturn();
        }

        Npc filliman = Npcs.getNearest("Filliman Tarlock");

        if (Inventory.contains("Ghostspeak amulet")) {
            Inventory.getFirst("Ghostspeak amulet").interact(a -> true);
            Time.sleepUntil(() -> Equipment.contains("Ghostspeak amulet"), 5000);
        }

        if (!Dialog.isOpen() && filliman == null) {
            WalkingWrapper.enterGrotto();
        }

        if (filliman != null && !Dialog.isOpen() && filliman.interact("Talk-to")) {
            Time.sleepUntil(Dialog::isOpen, 5000);
        }

        if (Dialog.isOpen()) {
            if (Dialog.canContinue())
                Dialog.processContinue();

            Dialog.process("I'm wearing an amulet of ghost speak!");
        }

        return NatureSpirit.getLoopReturn();
    }
}
