package script.quests.nature_spirit.tasks;

import nature_spirit.Main;
import nature_spirit.data.Location;
import nature_spirit.data.Quest;
import nature_spirit.wrappers.WalkingWrapper;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;

public class NatureSpirit10 extends Task {
    @Override
    public boolean validate() {
        return Quest.NATURE_SPIRIT.getVarpValue() == 75;
    }

    @Override
    public int execute() {
        if (Dialog.isOpen()) {
            if (Dialog.canContinue()) {
                Dialog.processContinue();
            }

            return Main.getLoopReturn();
        }

        if (!Location.NATURE_GROTTO_AREA.contains(Players.getLocal())) {

            SceneObject grotto = SceneObjects.getNearest(3525);
            if (grotto != null && grotto.interact("Exit")) {
                Time.sleepUntil(() -> Players.getLocal().isAnimating(), 2000);
                Time.sleepUntil(() -> !Players.getLocal().isAnimating(), 5000);
            }
        }

        if (Location.NATURE_GROTTO_AREA.contains(Players.getLocal())) {
            SceneObject bridge = SceneObjects.getNearest("Bridge");

            if (bridge != null && bridge.interact("Jump")) {
                Time.sleepUntil(() -> !Location.NATURE_GROTTO_AREA.contains(Players.getLocal()), 5000);
            }
        }

        Movement.walkTo(BankLocation.getNearest().getPosition(), WalkingWrapper::shouldBreakWalkLoop);
        Movement.toggleRun(true);

        return Main.getLoopReturn();
    }
}
