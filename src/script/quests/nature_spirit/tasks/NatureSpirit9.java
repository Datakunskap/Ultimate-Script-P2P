package script.quests.nature_spirit.tasks;

import org.rspeer.runetek.api.component.tab.Inventory;
import script.data.Locations;
import script.quests.nature_spirit.NatureSpirit;
import script.quests.nature_spirit.data.Location;
import script.quests.nature_spirit.data.Quest;
import script.quests.nature_spirit.wrappers.WalkingWrapper;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;
import script.tasks.fungus.Fungus;

public class NatureSpirit9 extends Task {
    @Override
    public boolean validate() {
        return Quest.NATURE_SPIRIT.getVarpValue() == 60
                || Quest.NATURE_SPIRIT.getVarpValue() == 65 || Quest.NATURE_SPIRIT.getVarpValue() == 70;
    }

    @Override
    public int execute() {
        if (!Inventory.contains("Silver sickle")) {
            Fungus.getSilverSickleB();
        }

        if (!Locations.NATURE_GROTTO_AREA.contains(Players.getLocal())
                && !Locations.INSIDE_GROTTO_AREA.contains(Players.getLocal())) {
            WalkingWrapper.walkToNatureGrotto();
        }

        if (Dialog.isOpen()) {
            if (Dialog.canContinue()) {
                Dialog.processContinue();
            }

            if (Dialog.isViewingChatOptions()) {
                Dialog.process("Ok, thanks.");
            }
            return NatureSpirit.getLoopReturn();
        }

        if (Location.NATURE_GROTTO_AREA.contains(Players.getLocal())) {
            WalkingWrapper.enterGrotto();
        }

        if (!Dialog.isOpen() && !Location.NATURE_GROTTO_AREA.contains(Players.getLocal())) {

            SceneObject grotto = SceneObjects.getNearest(3520);
            if (grotto != null && grotto.interact("Search")) {
                Time.sleepUntil(() -> Players.getLocal().isAnimating(), 2000);
                Time.sleepUntil(() -> !Players.getLocal().isAnimating(), 5000);
            }
        }

        return NatureSpirit.getLoopReturn();
    }
}
