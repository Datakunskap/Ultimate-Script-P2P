package script;

import org.rspeer.runetek.api.commons.StopWatch;
import org.rspeer.runetek.event.listeners.RenderListener;
import org.rspeer.runetek.event.types.RenderEvent;
import org.rspeer.script.ScriptMeta;
import org.rspeer.script.task.TaskScript;
import script.paint.ScriptPaint;
import script.quests.nature_spirit.NatureSpirit;
import script.quests.priest_in_peril.PriestInPeril;
import script.quests.the_restless_ghost.TheRestlessGhost;
import script.quests.waterfall_quest.WaterfallQuest;
import script.quests.witches_house.WitchesHouse;
import script.tasks.BuyItemsNeeded;
import script.tasks.BuySupplies;
import script.tasks.GetStartersGold;
import script.tasks.fungus.Fungus;
import script.tasks.training.magic.TrainTo13;
import script.tasks.training.prayer.TrainTo50;

import java.util.HashMap;

@ScriptMeta(developer = "Streagrem", name = "LOL", desc = "LOL")
public class Main extends TaskScript implements RenderListener {

    private ScriptPaint paint;
    private StopWatch runtime;
    private HashMap<String, Integer> ALL_ITEMS_NEEDED_FOR_ACCOUNT_PREPERATION;

    public StopWatch getRuntime() {
        return runtime;
    }

    @Override
    public void onStart() {
        runtime = StopWatch.start();
        paint = new ScriptPaint(this);

        setStartingItemsMap();
        setRestlessGhostItemsMap();
        setWitchesHouseItemsMap();
        setWaterfallItemsMap();
        setPriestInPerilItemsMap();
        setNatureSpiritItemsMap();

        submit(new BuySupplies(ALL_ITEMS_NEEDED_FOR_ACCOUNT_PREPERATION),
                new GetStartersGold(),
                new BuyItemsNeeded(),
                new TrainTo13()
        );

        submit(TheRestlessGhost.TASKS);
        submit(WitchesHouse.TASKS);
        submit(WaterfallQuest.TASKS);
        submit(PriestInPeril.TASKS);
        submit(NatureSpirit.TASKS);

        submit(new TrainTo50(),
                new Fungus()
        );

    }

    private void setRestlessGhostItemsMap() {
        HashMap<String, Integer> map = new HashMap<>();
        TheRestlessGhost.setSupplyMap(map);
    }

    private void setWitchesHouseItemsMap() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("Amulet of glory(6)", 1);
        map.put("Staff of air", 1);
        map.put("Ring of wealth (5)", 1);
        map.put("Cheese", 2);
        map.put("Mind rune", 100);
        map.put("Fire rune", 300);
        map.put("Falador teleport", 5);
        map.put("Leather gloves", 1);
        map.put("Tuna", 10);
        WitchesHouse.setSupplyMap(map);
    }

    private void setWaterfallItemsMap() {
        //TODO: Add items
        HashMap<String, Integer> map = new HashMap<>();
        WaterfallQuest.setSupplyMap(map);
    }

    private void setPriestInPerilItemsMap() {
        //TODO: Add items
        HashMap<String, Integer> map = new HashMap<>();
        PriestInPeril.setSupplyMap(map);
    }

    private void setNatureSpiritItemsMap() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("Silver sickle", 1);
        map.put("Ghostspeak amulet", 1);
        NatureSpirit.setSupplyMap(map);
    }

    private void setStartingItemsMap() {
        ALL_ITEMS_NEEDED_FOR_ACCOUNT_PREPERATION = new HashMap<>();
        ALL_ITEMS_NEEDED_FOR_ACCOUNT_PREPERATION.put("Lumbridge teleport", 10);
        ALL_ITEMS_NEEDED_FOR_ACCOUNT_PREPERATION.put("Staff of air", 1);
        ALL_ITEMS_NEEDED_FOR_ACCOUNT_PREPERATION.put("Staff of fire", 1);
        ALL_ITEMS_NEEDED_FOR_ACCOUNT_PREPERATION.put("Amulet of glory(6)", 5);
        ALL_ITEMS_NEEDED_FOR_ACCOUNT_PREPERATION.put("Ring of wealth (5)", 2);
        ALL_ITEMS_NEEDED_FOR_ACCOUNT_PREPERATION.put("Air rune", 1000);
        ALL_ITEMS_NEEDED_FOR_ACCOUNT_PREPERATION.put("Mind rune", 1000);
        ALL_ITEMS_NEEDED_FOR_ACCOUNT_PREPERATION.put("Water rune", 200);
        ALL_ITEMS_NEEDED_FOR_ACCOUNT_PREPERATION.put("Fire rune", 200);
        ALL_ITEMS_NEEDED_FOR_ACCOUNT_PREPERATION.put("Earth rune", 300);
        ALL_ITEMS_NEEDED_FOR_ACCOUNT_PREPERATION.put("Tuna", 100);
        ALL_ITEMS_NEEDED_FOR_ACCOUNT_PREPERATION.put("Stamina potion(4)", 10);
        ALL_ITEMS_NEEDED_FOR_ACCOUNT_PREPERATION.put("Cheese", 2);
        ALL_ITEMS_NEEDED_FOR_ACCOUNT_PREPERATION.put("Leather gloves", 1);
        ALL_ITEMS_NEEDED_FOR_ACCOUNT_PREPERATION.put("Falador teleport", 5);
        ALL_ITEMS_NEEDED_FOR_ACCOUNT_PREPERATION.put("Games necklace(8)", 1);
        ALL_ITEMS_NEEDED_FOR_ACCOUNT_PREPERATION.put("Rope", 2);
        ALL_ITEMS_NEEDED_FOR_ACCOUNT_PREPERATION.put("Adamant scimitar", 1);
        ALL_ITEMS_NEEDED_FOR_ACCOUNT_PREPERATION.put("Ring of recoil", 1);
        ALL_ITEMS_NEEDED_FOR_ACCOUNT_PREPERATION.put("Bucket", 1);
        ALL_ITEMS_NEEDED_FOR_ACCOUNT_PREPERATION.put("Rune essence", 50);
        ALL_ITEMS_NEEDED_FOR_ACCOUNT_PREPERATION.put("Varrock teleport", 5);
        ALL_ITEMS_NEEDED_FOR_ACCOUNT_PREPERATION.put("Silver sickle", 1);
        ALL_ITEMS_NEEDED_FOR_ACCOUNT_PREPERATION.put("Dragon bones", 300);
        ALL_ITEMS_NEEDED_FOR_ACCOUNT_PREPERATION.put("Burning amulet(5)", 5);
    }

    @Override
    public void notify(RenderEvent e) {
        try {
            if (runtime != null) {
                paint.notify(e);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
}
