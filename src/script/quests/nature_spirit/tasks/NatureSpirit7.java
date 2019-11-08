package script.quests.nature_spirit.tasks;

import script.quests.nature_spirit.NatureSpirit;
import script.quests.nature_spirit.data.Location;
import script.quests.nature_spirit.data.Quest;
import script.quests.nature_spirit.wrappers.WalkingWrapper;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.input.menu.ActionOpcodes;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

public class NatureSpirit7 extends Task {

    @Override
    public boolean validate() {
        return   Quest.NATURE_SPIRIT.getVarpValue() == 40
                || Quest.NATURE_SPIRIT.getVarpValue() == 45
                || Quest.NATURE_SPIRIT.getVarpValue() == 50;
    }

    @Override
    public int execute() {
        if (Dialog.isOpen()) {
            Dialog.processContinue();
            return NatureSpirit.getLoopReturn();
        }

        if (!Inventory.contains(i -> i.getName().equalsIgnoreCase("Druidic spell"))
                && !Inventory.contains("Mort myre fungus")) {

            if (!Location.NATURE_GROTTO_AREA.contains(Players.getLocal())) {
                WalkingWrapper.walkToNatureGrotto();
            } else {
                SceneObject grotto = SceneObjects.getNearest("Grotto");

                if (!Dialog.isOpen() && grotto != null && grotto.interact(a -> true)) {
                    Time.sleepUntil(Dialog::isOpen, 5000);
                }

                if (Dialog.isOpen()) {
                    if (Dialog.canContinue()) {
                        Dialog.processContinue();
                    }

                    if (Dialog.isViewingChatOptions()) {
                        Dialog.process("Could I have another bloom scroll please?");
                    }
                }
            }
        }

        if (Inventory.contains(i -> i.getName().equalsIgnoreCase("Druidic spell"))
                && !Inventory.contains("Mort myre fungus")) {

            if (Location.NATURE_GROTTO_AREA.contains(Players.getLocal())) {
                SceneObject bridge = SceneObjects.getNearest("Bridge");

                if (bridge != null && !Players.getLocal().isMoving() && bridge.interact(a -> true)) {
                    Time.sleepUntil(() -> !Location.NATURE_GROTTO_AREA.contains(Players.getLocal()), 5000);
                }
            }

            if (Location.ROTTING_LOG_POSITION.distance() > 1) {
                Movement.walkTo(Location.ROTTING_LOG_POSITION, script.wrappers.WalkingWrapper::shouldBreakOnTarget);
            }

            SceneObject log = SceneObjects.getNearest("Rotting log");
            Item spell = Inventory.getFirst("Druidic spell");

            if (SceneObjects.getNearest(3509) == null && log != null && log.getPosition().equals(Players.getLocal().getPosition())) {
                Movement.walkTo(Location.ROTTING_LOG_POSITION.translate(Random.nextInt(-1, 1), Random.nextInt(-1, 1)));
            }

            if (log != null && log.distance() <= 1 && spell != null && spell.interact(ActionOpcodes.ITEM_ACTION_0)) {

                Time.sleepUntil(() -> SceneObjects.getNearest(3509) != null && !Players.getLocal().isAnimating(), 6000);
                Time.sleep(600, 800);

            }

            SceneObject fungiLog = SceneObjects.getNearest(3509);

            while (fungiLog != null && !Inventory.contains("Mort myre fungus")) {
                if (fungiLog.click()) {
                    Time.sleepUntil(() -> Inventory.contains("Mort myre fungus"), 5000);
                } else {
                    Log.severe("Cant Pick Fungi");
                }
                fungiLog = SceneObjects.getNearest(3509);
                Time.sleep(300, 600);
            }
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
