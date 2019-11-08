package script.quests.waterfall_quest;

import org.rspeer.script.task.Task;
import script.quests.waterfall_quest.tasks.*;

import java.util.HashMap;

public class WaterfallQuest {

    private static HashMap<String, Integer> supplies;

    public static void setSupplyMap(HashMap<String, Integer> supplyMap) {
        supplies = supplyMap;
    }

    public static final Task[] TASKS = {
            new Waterfall_Preparation(),
            new Waterfall_0(),
            new Waterfall_1(),
            new Waterfall_2(),
            new Waterfall_3(),
            new Waterfall_4(),
            new Waterfall_5(),
            new Waterfall_6(),
            new Waterfall_7()};

}
