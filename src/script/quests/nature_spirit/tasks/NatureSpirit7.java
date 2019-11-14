package script.quests.nature_spirit.tasks;

import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.script.task.Task;
import script.quests.nature_spirit.NatureSpirit;
import script.quests.nature_spirit.data.Location;
import script.quests.nature_spirit.data.Quest;
import script.quests.nature_spirit.wrappers.WalkingWrapper;
import script.wrappers.BankWrapper;

public class NatureSpirit7 extends Task {

    @Override
    public boolean validate() {
        return   Quest.NATURE_SPIRIT.getVarpValue() == 40
                || Quest.NATURE_SPIRIT.getVarpValue() == 45
                || Quest.NATURE_SPIRIT.getVarpValue() == 50;
    }

    @Override
    public int execute() {
        if (Inventory.contains("Ghostspeak amulet")) {
            Inventory.getFirst("Ghostspeak amulet").interact(a -> true);
            Time.sleepUntil(() -> Equipment.contains("Ghostspeak amulet"), 5000);
        }

        if (!Equipment.contains("Ghostspeak amulet")) {
            WalkingWrapper.exitAndLeaveGrotto();
            BankWrapper.openAndDepositAll(false, false, "Mort myre fungus", "Ghostspeak amulet", "Druidic spell", "A used spell", "Silver sickle");
        }

        if (Dialog.isOpen()) {
            Dialog.processContinue();
            return NatureSpirit.getLoopReturn();
        }

        if (!Inventory.contains("Mort myre fungus")) {
            NatureSpirit.doFungusPicking();
        }

        if (Inventory.contains("Mort myre fungus")) {
            if (!Location.NATURE_GROTTO_AREA.contains(Players.getLocal())) {
                WalkingWrapper.walkToNatureGrotto();
            } else {
                WalkingWrapper.enterGrotto();
            }
        }

        return NatureSpirit.getLoopReturn();
    }
}
