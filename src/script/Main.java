package script;

import org.rspeer.runetek.api.commons.StopWatch;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.movement.position.Area;
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
import script.tasks.*;
import script.tasks.fungus.Fungus;
import script.tasks.training.magic.TrainTo13;
import script.tasks.training.prayer.TrainTo50;
import script.wrappers.PriceCheckService;

import java.awt.*;
import java.util.HashMap;

@ScriptMeta(developer = "Streagrem", name = "LOL", desc = "LOL")
public class Main extends TaskScript implements RenderListener {

    private static final int MULE_AMOUNT = 2_000_000;
    private static final String MULE_NAME = "Madman38snur";
    private static final int MULE_WORLD = 393;
    private static final int MULE_AMOUNT_TO_KEEP = 500_000;
    private static final Area MULE_AREA = Area.rectangular(3176, 3470, 3179, 3468);

    private ScriptPaint paint;
    private StopWatch runtime;

    public StopWatch getRuntime() {
        return runtime;
    }

    @Override
    public void onStart() {
        Log.fine("Script Started");
        runtime = StopWatch.start();
        paint = new ScriptPaint(this);

        submit( new GetStartersGold(),
                new SellGE(),
                new Mule(MULE_AMOUNT, MULE_NAME, MULE_AREA.getCenter(), MULE_WORLD, MULE_AMOUNT_TO_KEEP),
                new BuyGE(),
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
