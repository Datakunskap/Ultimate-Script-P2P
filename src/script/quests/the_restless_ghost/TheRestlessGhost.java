package script.quests.the_restless_ghost;

import org.rspeer.script.task.Task;
import script.quests.the_restless_ghost.tasks.*;
import script.tasks.BuySupplies;

import java.util.HashMap;

public class TheRestlessGhost {

    private static HashMap<String, Integer> supplies;

    public static void setSupplyMap(HashMap<String, Integer> supplyMap) {
        supplies = supplyMap;
    }

    public static final Task[] TASKS = {
            new BuySupplies(supplies),
            new RestlessGhost_0(),
            new RestlessGhost_1(),
            new RestlessGhost_2(),
            new RestlessGhost_3(),
            new RestlessGhost_4()
    };

}
