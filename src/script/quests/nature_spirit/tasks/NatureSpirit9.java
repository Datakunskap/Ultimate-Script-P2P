package script.quests.nature_spirit.tasks;

import nature_spirit.Main;
import nature_spirit.data.Location;
import nature_spirit.data.Quest;
import nature_spirit.wrappers.WalkingWrapper;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;

public class NatureSpirit9 extends Task {
    @Override
    public boolean validate() {
        return Quest.NATURE_SPIRIT.getVarpValue() == 60
                || Quest.NATURE_SPIRIT.getVarpValue() == 65 || Quest.NATURE_SPIRIT.getVarpValue() == 70;
    }

    @Override
    public int execute() {
        if (Dialog.isOpen()) {
            if (Dialog.canContinue()) {
                Dialog.processContinue();
            }

            if (Dialog.isViewingChatOptions()) {
                Dialog.process("Ok, thanks.");
            }
            return Main.getLoopReturn();
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

        return Main.getLoopReturn();
    }
}
