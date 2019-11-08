package script;

import org.rspeer.runetek.api.commons.StopWatch;
import org.rspeer.runetek.event.listeners.RenderListener;
import org.rspeer.runetek.event.types.RenderEvent;
import org.rspeer.runetek.providers.subclass.GameCanvas;
import org.rspeer.script.ScriptMeta;
import org.rspeer.script.task.TaskScript;
import org.rspeer.ui.Log;
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
import script.tasks.fungus.SellGE;
import script.tasks.training.magic.TrainTo13;
import script.tasks.training.prayer.TrainTo50;
import script.wrappers.PriceCheckService;

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
        Log.fine("Script Started");
        runtime = StopWatch.start();
        paint = new ScriptPaint(this);

        submit(new BuySupplies(true),
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
                new SellGE(),
                new Fungus()
        );

        if (!GameCanvas.isInputEnabled()) {
            GameCanvas.setInputEnabled(true);
        }
    }

    @Override
    public void onStop() {
        Log.severe("Script Stopped");
        PriceCheckService.dispose();
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
