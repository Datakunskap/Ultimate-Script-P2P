package script.quests.witches_house;

import org.rspeer.script.task.Task;
import script.quests.witches_house.tasks.*;

public class WitchesHouse {

    public static final Task[] TASKS = {
            new WitchesHouse_Preparation(),
            new WitchesHouse_0(),
            new WitchesHouse_1(),
            new WitchesHouse_2(),
            new WitchesHouse_3(),
            new WitchesHouse_4()
    };

}
