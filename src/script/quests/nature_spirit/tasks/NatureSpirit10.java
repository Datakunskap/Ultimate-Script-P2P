package script.quests.nature_spirit.tasks;

import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;
import script.quests.nature_spirit.NatureSpirit;
import script.quests.nature_spirit.data.Location;
import script.quests.nature_spirit.data.Quest;
import script.wrappers.WalkingWrapper;

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

            return NatureSpirit.getLoopReturn();
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

        Movement.walkTo(BankLocation.getNearest().getPosition(), WalkingWrapper::shouldBreakOnTarget);
        Movement.toggleRun(true);

        return NatureSpirit.getLoopReturn();
    }
}
