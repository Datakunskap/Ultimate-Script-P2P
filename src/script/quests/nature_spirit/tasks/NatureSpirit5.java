package script.quests.nature_spirit.tasks;

import nature_spirit.Main;
import nature_spirit.data.Location;
import nature_spirit.data.Quest;
import nature_spirit.wrappers.WalkingWrapper;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;

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

            if (Location.NATURE_GROTTO_AREA.contains(Players.getLocal())){
                SceneObject bridge = SceneObjects.getNearest("Bridge");
                if (bridge != null && bridge.interact(a -> true)) {
                    Time.sleepUntil(() -> !Location.NATURE_GROTTO_AREA.contains(Players.getLocal()), 5000);
                }
            }

            if (Location.DREZEL_POSITION.distance() > 3) {
                Movement.walkTo(Location.DREZEL_POSITION, WalkingWrapper::shouldBreakWalkLoop);
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

        return Main.getLoopReturn();
    }
}
