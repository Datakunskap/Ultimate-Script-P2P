package script.quests.waterfall_quest.tasks;

import api.API;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

import static script.quests.waterfall_quest.data.Quest.WATERFALL;
import static script.quests.witches_house.data.Quest.WITCHES_HOUSE;

public class Waterfall_7 extends Task {

    private static final int CHALICE = 2014;

    private static final String EAT_ACTION = "Eat";
    private static final String GLARIALS_URN = "Glarial's urn";

    private static final Position CHALICE_POSITION = new Position(2603, 9910, 0);

    @Override
    public boolean validate() {
        return WITCHES_HOUSE.getVarpValue() == 7 && WATERFALL.getVarpValue() == 8;
    }

    @Override
    public int execute() {

        Log.info("Waterfall_7");

        API.doDialog();

        SceneObject chalice = SceneObjects.getNearest(CHALICE);
        if (chalice != null) {
            if (!Dialog.isOpen()) {
                if (Inventory.getFreeSlots() < 5) {
                    Item food = Inventory.getFirst(x -> x.containsAction(EAT_ACTION));
                    food.interact(EAT_ACTION);
                    Time.sleep(API.lowRandom());
                }
                if (Inventory.getFreeSlots() >= 5) {
                    API.useItemOn(GLARIALS_URN, CHALICE, CHALICE_POSITION);
                }
            }
        }

        return API.lowRandom();
    }

}