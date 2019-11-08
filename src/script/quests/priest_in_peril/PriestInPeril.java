package script.quests.priest_in_peril;

import org.rspeer.script.task.Task;
import script.quests.priest_in_peril.tasks.*;

import java.util.HashMap;

public class PriestInPeril {

    private static HashMap<String, Integer> supplies;

    public static void setSupplyMap(HashMap<String, Integer> supplyMap) {
        supplies = supplyMap;
    }

    public static final Task[] TASKS = {
            new BuySupplies(supplies),
            new PriestInPeril_Preparation(),
            new PriestInPeril_0(),
            new PriestInPeril_1(),
            new PriestInPeril_2(),
            new PriestInPeril_3(),
            new PriestInPeril_4(),
            new PriestInPeril_5(),
            new PriestInPeril_6(),
            new PriestInPeril_7(),
            new PriestInPeril_8(),
            new PriestInPeril_10(),
            new PriestInPeril_35(),
            new PriestInPeril_60()
    };

}
