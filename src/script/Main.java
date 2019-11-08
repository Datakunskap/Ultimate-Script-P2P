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

import java.util.HashMap;

@ScriptMeta(developer = "Streagrem", name = "LOL", desc = "LOL")
public class Main extends TaskScript {

    private static final String[] ALL_ITEMS_NEEDED_FOR_ACCOUNT_PREPERATION = new String[]{
            "Lumbridge teleport",
            "Staff of air",
            "Staff of fire",
            "Amulet of glory(6)",
            "Ring of wealth (5)",
            "Air rune",
            "Mind rune",
            "Water rune",
            "Fire rune",
            "Earth rune",
            "Tuna",
            "Stamina potion(4)",
            "Cheese",
            "Leather gloves",
            "Falador teleport",
            "Games necklace(8)",
            "Rope",
            "Adamant scimitar",
            "Ring of recoil",
            "Bucket",
            "Rune essence",
            "Varrock teleport",
            "Silver sickle",
            "Dragon bones",
            "Burning amulet(5)"
    };

    @Override
    public void onStart() {

        submit(new BuySupplies(ALL_ITEMS_NEEDED_FOR_ACCOUNT_PREPERATION),
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
