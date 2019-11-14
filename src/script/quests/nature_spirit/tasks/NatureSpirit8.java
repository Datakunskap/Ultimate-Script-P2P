package script.quests.nature_spirit.tasks;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.input.menu.ActionOpcodes;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;
import script.data.Locations;
import script.quests.nature_spirit.NatureSpirit;
import script.quests.nature_spirit.data.Location;
import script.quests.nature_spirit.data.Quest;
import script.quests.nature_spirit.wrappers.WalkingWrapper;
import script.tasks.fungus.Fungus;

public class NatureSpirit8 extends Task {

    private boolean needsToPick;

    @Override
    public boolean validate() {
        return Quest.NATURE_SPIRIT.getVarpValue() == 55;
    }

    @Override
    public int execute() {
        if (!Inventory.contains("Silver sickle")) {
            Fungus.getSilverSickleB();
        }

        if (Dialog.isOpen()) {
            Dialog.processContinue();
        }

        if (needsToPick) {
            NatureSpirit.doFungusPicking();
            if (Inventory.contains("Mort myre fungus")) {
                needsToPick = false;
            }
            return NatureSpirit.getLoopReturn();
        }

        if (!Locations.NATURE_GROTTO_AREA.contains(Players.getLocal())
                && !Locations.INSIDE_GROTTO_AREA.contains(Players.getLocal())) {
            WalkingWrapper.walkToNatureGrotto();
        }

        SceneObject orangeStone = SceneObjects.getNearest(3528);

        if (Dialog.isOpen()) {
            if (orangeStone != null && orangeStone.getPosition().equals(Players.getLocal().getPosition())) {
                InterfaceComponent hmm = Interfaces.getComponent(231, 4);
                if (hmm != null && hmm.isVisible() && hmm.getText().contains("Hmm, something still")) {
                    needsToPick = true;
                }
            }

            if (Dialog.canContinue()) {
                Dialog.processContinue();
            }

            if (Dialog.isViewingChatOptions()) {
                Dialog.process("I think I've solved the puzzle!");
            }
            return NatureSpirit.getLoopReturn();
        }

        if (!Location.NATURE_GROTTO_AREA.contains(Players.getLocal())) {
            WalkingWrapper.walkToNatureGrotto();
        }

        if (Location.NATURE_GROTTO_AREA.contains(Players.getLocal())) {
            int numFungi = Inventory.getCount(true, "Mort myre fungus");
            int numUsedSpells = Inventory.getCount(true, "A used spell");

            if (NatureSpirit.useItemOnObject("Mort myre fungus", 3527)) {
                Time.sleepUntil(() -> Inventory.getCount(true, "Mort myre fungus") < numFungi, 5000);
                Log.info("Placed Fungus");
            }

            if (NatureSpirit.useItemOnObject("A used spell", 3529)) {
                Time.sleepUntil(() -> Inventory.getCount(true, "A used spell") < numUsedSpells, 5000);
                Log.info("Placed Spell");
            }

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

        return NatureSpirit.getLoopReturn();
    }
}
