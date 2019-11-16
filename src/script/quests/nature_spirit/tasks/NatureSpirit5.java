package script.quests.nature_spirit.tasks;

import org.rspeer.runetek.api.component.tab.Inventory;
import script.quests.nature_spirit.NatureSpirit;
import script.quests.nature_spirit.data.Location;
import script.quests.nature_spirit.data.Quest;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;
import script.tasks.fungus.Fungus;
import script.wrappers.WalkingWrapper;

public class NatureSpirit5 extends Task {
    @Override
    public boolean validate() {
        return Quest.NATURE_SPIRIT.getVarpValue() == 30 || Quest.NATURE_SPIRIT.getVarpValue() == 35;
    }

    @Override
    public int execute() {
        if (Dialog.isViewingChatOptions()) {
            Dialog.process("How can I help?");
        }

        if (Dialog.canContinue())
            Dialog.processContinue();

        if (Quest.NATURE_SPIRIT.getVarpValue() == 35) {

            if (Location.NATURE_GROTTO_AREA.contains(Players.getLocal())) {
                if (Inventory.contains("Salve graveyard teleport")) {
                    Fungus.useSalveGraveyardTeleport();
                }

                SceneObject bridge = SceneObjects.getNearest("Bridge");
                if (bridge != null && bridge.interact(a -> true)) {
                    Time.sleepUntil(() -> !Location.NATURE_GROTTO_AREA.contains(Players.getLocal()), 5000);
                }
            }

            if (Location.DREZEL_POSITION.distance() > 3) {
                WalkingWrapper.walkToPosition(Location.DREZEL_POSITION);
            }

            Npc drezel = Npcs.getNearest("Drezel");
            if (!Dialog.isOpen() && drezel != null && drezel.interact("Talk-to")) {
                Time.sleepUntil(Dialog::isOpen, 5000);
            }

            if (Dialog.isOpen()) {
                if (Dialog.canContinue())
                    Dialog.processContinue();
            }

        }

        return NatureSpirit.getLoopReturn();
    }
}
