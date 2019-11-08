package script.quests.nature_spirit.tasks;

import nature_spirit.Main;
import nature_spirit.data.Quest;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.input.menu.ActionOpcodes;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

public class NatureSpirit4 extends Task {

    private boolean usedJournal;

    @Override
    public boolean validate() {
        return Quest.NATURE_SPIRIT.getVarpValue() == 25;
    }

    @Override
    public int execute() {
        if (Dialog.isOpen()) {
            Dialog.processContinue();
            return Main.getLoopReturn();
        }

        SceneObject tree = SceneObjects.getNearest("Grotto tree");
        if (!Inventory.contains("Journal") && tree != null && tree.interact("Search")) {
            Time.sleepUntil(() -> Inventory.contains("Journal"), 5000);
        }

        if (Inventory.contains("Journal")) {
            SceneObject grotto = SceneObjects.getNearest("Grotto");

            if (!Dialog.isOpen() && grotto != null && grotto.interact(a -> true)) {
                Time.sleepUntil(Dialog::isOpen, 5000);
            }

            Inventory.getFirst("Journal").interact("Use");
            Time.sleepUntil(Inventory::isItemSelected, 5000);
            Time.sleep(600, 800);

            Npc filliman = Npcs.getNearest("Filliman Tarlock");
            if (filliman != null && filliman.interact(ActionOpcodes.ITEM_ON_NPC)) {
                Log.fine("Using Journal");
                Time.sleepUntil(() -> !Players.getLocal().isAnimating() && Dialog.isOpen(), 5000);
            }
        }

        return Main.getLoopReturn();
    }
}
