package script.quests.nature_spirit.tasks;

import nature_spirit.Main;
import nature_spirit.data.Location;
import nature_spirit.data.Quest;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;

public class NatureSpirit2 extends Task {

    @Override
    public boolean validate() {
        return Quest.NATURE_SPIRIT.getVarpValue() == 10 || Quest.NATURE_SPIRIT.getVarpValue() == 15;
    }

    @Override
    public int execute() {
        Npc filliman = Npcs.getNearest("Filliman Tarlock");
        SceneObject grotto = SceneObjects.getNearest("Grotto");
        SceneObject bridge = SceneObjects.getNearest("Bridge");

        if (!Dialog.isOpen() && filliman == null && grotto != null && grotto.interact(a -> true)) {
            if (!Time.sleepUntil(Dialog::isOpen, 5000)) {

                if (bridge != null && bridge.interact(a -> true)) {
                    Time.sleepUntil(() -> !Location.NATURE_GROTTO_AREA.contains(Players.getLocal()), 5000);
                    Time.sleep(2000, 2500);
                    bridge.interact(a -> true);
                }
            }
        }

        if (filliman != null && !Dialog.isOpen() && filliman.interact("Talk-to")) {
            Time.sleepUntil(Dialog::isOpen, 5000);
        }

        if (Dialog.isOpen()) {
            if (Dialog.canContinue())
                Dialog.processContinue();

            Dialog.process("I'm wearing an amulet of ghost speak!");
        }

        return Main.getLoopReturn();
    }
}
