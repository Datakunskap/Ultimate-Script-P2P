package script.quests.waterfall_quest.tasks;

import api.API;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

import static script.quests.nature_spirit.data.Quest.WITCHES_HOUSE;
import static script.quests.witches_house.data.Quest.WATERFALL;

public class Waterfall_1 extends Task {

    public static final String BOY = "Boy";

    public static final Position BOY_POSITION = new Position(2512,3481);

    public static final Area RAFT_AREA = Area.rectangular(2508, 3482, 2514, 3476);

    @Override
    public boolean validate() {
        return WITCHES_HOUSE.getVarpValue() == 7
                && WATERFALL.getVarpValue() == 1;
    }

    @Override
    public int execute() {

        Log.info("Waterfall_1");

        API.runFromAttacker();

        API.doDialog();

        API.toggleRun();

        API.drinkStaminaPotion();

        API.talkTo(BOY, BOY_POSITION);

        return API.lowRandom();
    }

}