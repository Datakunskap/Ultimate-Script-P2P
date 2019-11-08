package script.quests.nature_spirit.tasks;

import nature_spirit.Main;
import nature_spirit.data.Location;
import nature_spirit.data.Quest;
import nature_spirit.wrappers.WalkingWrapper;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.input.menu.ActionOpcodes;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

public class NatureSpirit8 extends Task {
    @Override
    public boolean validate() {
        return Quest.NATURE_SPIRIT.getVarpValue() == 55;
    }

    @Override
    public int execute() {
        if (Dialog.isOpen()) {
            if (Dialog.canContinue()) {
                Dialog.processContinue();
            }

            if (Dialog.isViewingChatOptions()) {
                Dialog.process("I think I've solved the puzzle!");
            }
            return Main.getLoopReturn();
        }

        if (!Location.NATURE_GROTTO_AREA.contains(Players.getLocal())) {
            WalkingWrapper.walkToNatureGrotto();
        }

        if (Location.NATURE_GROTTO_AREA.contains(Players.getLocal())) {

            if (Main.useItemOnObject("Mort myre fungus", 3527)) {
                Time.sleep(1600, 2200);
                Log.info("Placed Fungus");
            }

            if (Main.useItemOnObject("A used spell", 3529)) {
                Time.sleep(1600, 2200);
                Log.info("Placed Spell");
            }

            SceneObject orangeStone = SceneObjects.getNearest(3528);

            if (!Players.getLocal().getPosition().equals(orangeStone.getPosition())) {
                Movement.setWalkFlag(orangeStone);
                orangeStone.interact(ActionOpcodes.OBJECT_ACTION_0);
                Time.sleepUntil(() -> Players.getLocal().getPosition().equals(orangeStone.getPosition()), 5000);
            }

            if (Players.getLocal().getPosition().equals(orangeStone.getPosition())) {

                Npc filliman = Npcs.getNearest("Filliman Tarlock");
                SceneObject grotto = SceneObjects.getNearest("Grotto");

                if (filliman == null) {
                    if (!Dialog.isOpen() && grotto != null && grotto.interact(a -> true)) {
                        Time.sleepUntil(Dialog::isOpen, 5000);
                    }
                } else {
                    filliman.interact("Talk-to");
                    Time.sleepUntil(Dialog::isOpen, 5000);
                }
            }
        }

        return Main.getLoopReturn();
    }
}
