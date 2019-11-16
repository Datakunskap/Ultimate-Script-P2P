package script;

import org.rspeer.RSPeer;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.StopWatch;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.event.listeners.DeathListener;
import org.rspeer.runetek.event.listeners.RenderListener;
import org.rspeer.runetek.event.types.DeathEvent;
import org.rspeer.runetek.event.types.RenderEvent;
import org.rspeer.runetek.providers.subclass.GameCanvas;
import org.rspeer.script.GameAccount;
import org.rspeer.script.ScriptMeta;
import org.rspeer.script.task.TaskScript;
import org.rspeer.ui.Log;
import script.paint.ScriptPaint;
import script.quests.nature_spirit.NatureSpirit;
import script.quests.priest_in_peril.PriestInPeril;
import script.quests.the_restless_ghost.TheRestlessGhost;
import script.quests.waterfall_quest.WaterfallQuest;
import script.quests.waterfall_quest.data.Quest;
import script.quests.witches_house.WitchesHouse;
import script.tasks.*;
import script.tasks.fungus.Fungus;
import script.tasks.training.magic.TrainTo13;
import script.tasks.training.prayer.LeaveWilderness;
import script.tasks.training.prayer.TrainTo50;
import script.wrappers.BankWrapper;
import script.wrappers.GEWrapper;
import script.wrappers.PriceCheckService;
import script.wrappers.SupplyMapWrapper;

import java.util.HashMap;

@ScriptMeta(developer = "Streagrem", name = "LOL", desc = "LOL")
public class Main extends TaskScript implements RenderListener, DeathListener {

    public static final String MULE_NAME = "ScatGrem";
    private static final int MULE_AMOUNT = 1_000_000;
    public static final int MULE_WORLD = 393;
    private static final int MULE_AMOUNT_TO_KEEP = 500_000;
    public static final Area MULE_AREA = Area.rectangular(3176, 3470, 3179, 3468);
    public static final String API_KEY = "JV5ML4DE4M9W8Z5KBE00322RDVNDGGMTMU1EH9226YCVGFUBE6J6OY1Q2NJ0RA8YAPKO70"; // Not used atm

    private ScriptPaint paint;
    private StopWatch runtime;

    public StopWatch getRuntime() {
        return runtime;
    }

    @Override
    public void onStart() {
        Log.fine("Script Started");
        GameAccount account = RSPeer.getGameAccount();
        Log.info(account.getUsername() + ":" + account.getPassword());
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
                new LeaveWilderness(),
                new Fungus()
        );

        if (!GameCanvas.isInputEnabled()) {
            GameCanvas.setInputEnabled(true);
        }
    }

    @Override
    public void onStop() {
        Log.severe("Script Stopped");
        if (BankWrapper.isMuleing()) {
            Mule.logoutMule();
        }
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

    @Override
    public void notify(DeathEvent e) {
        if (e.getSource().equals(Players.getLocal())) {
            Log.severe("You Died");
            int varp = Quest.NATURE_SPIRIT.getVarpValue();
            if (varp > 0 && varp < 75) {
                BankWrapper.doBanking(false, false, SupplyMapWrapper.getNatureSpiritKeepMap());
            }
        }
    }
}
