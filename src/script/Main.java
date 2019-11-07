package script;

import org.rspeer.script.ScriptMeta;
import org.rspeer.script.task.TaskScript;
import script.quests.nature_spirit.NatureSpirit;
import script.quests.priest_in_peril.PriestInPeril;
import script.quests.the_restless_ghost.TheRestlessGhost;
import script.quests.waterfall_quest.WaterfallQuest;
import script.quests.witches_house.WitchesHouse;
import script.tasks.BuyItemsNeeded;
import script.tasks.BuySupplies;
import script.tasks.GetStartersGold;
import script.tasks.training.magic.TrainTo13;
import script.tasks.training.prayer.TrainTo50;

@ScriptMeta(developer = "Streagrem", name = "LOL", desc = "LOL")
public class Main extends TaskScript {
    @Override
    public void onStart() {

        submit(new BuySupplies(),
                new GetStartersGold(),
                new BuyItemsNeeded(),
                new TrainTo13(),
                new TrainTo50()
        );

        submit(TheRestlessGhost.TASKS);
        submit(WitchesHouse.TASKS);
        submit(WaterfallQuest.TASKS);
        submit(PriestInPeril.TASKS);
        submit(NatureSpirit.TASKS);

    }
}
