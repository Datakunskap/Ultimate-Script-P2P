package script.quests.witches_house;

import org.rspeer.script.task.Task;
import script.quests.witches_house.tasks.*;
import script.tasks.BuySupplies;

import java.util.HashMap;

public class WitchesHouse {

    private static HashMap<String, Integer> supplies;

    public static void setSupplyMap(HashMap<String, Integer> supplyMap) {
        supplies = supplyMap;
    }

    public static final Task[] TASKS = {
            new BuySupplies(supplies),
            new WitchesHouse_Preparation(),
            new WitchesHouse_0(),
            new WitchesHouse_1(),
            new WitchesHouse_2(),
            new WitchesHouse_3(),
            new WitchesHouse_4()
    };

}
