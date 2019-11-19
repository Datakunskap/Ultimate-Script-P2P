package script.quests.waterfall_quest.tasks;

import api.API;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

import static script.quests.nature_spirit.data.Quest.WITCHES_HOUSE;
import static script.quests.waterfall_quest.data.Quest.WATERFALL;

public class Waterfall_0 extends Task {

    public static final Position ALMERA_POSITION = new Position(2521, 3495);
    public static final String ALMERA = "Almera";

    public static final String CHAT_OPTION_1 = "How can I help?";

    @Override
    public boolean validate() {
        return WITCHES_HOUSE.getVarpValue() == 7
                && WATERFALL.getVarpValue() == 0
                && Waterfall_Preparation.readyToStartWaterfall;
    }

    @Override
    public int execute() {

        Log.info("Waterfall_0");

        API.runFromAttacker();

        API.doDialog();

        API.toggleRun();

        API.drinkStaminaPotion();

        API.talkTo(ALMERA, ALMERA_POSITION);
        Dialog.process(CHAT_OPTION_1);

        return API.lowRandom();
    }

}