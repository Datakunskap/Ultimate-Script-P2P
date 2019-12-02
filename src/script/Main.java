package script;

import api.bot_management.BotManagement;
import api.bot_management.data.LaunchedClient;
import org.rspeer.RSPeer;
import org.rspeer.runetek.api.commons.StopWatch;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.event.listeners.DeathListener;
import org.rspeer.runetek.event.listeners.LoginResponseListener;
import org.rspeer.runetek.event.listeners.RenderListener;
import org.rspeer.runetek.event.types.DeathEvent;
import org.rspeer.runetek.event.types.LoginResponseEvent;
import org.rspeer.runetek.event.types.RenderEvent;
import org.rspeer.runetek.providers.subclass.GameCanvas;
import org.rspeer.script.GameAccount;
import org.rspeer.script.ScriptMeta;
import org.rspeer.script.task.Task;
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
import script.wrappers.*;

import static org.rspeer.runetek.event.types.LoginResponseEvent.Response.INVALID_CREDENTIALS;
import static org.rspeer.runetek.event.types.LoginResponseEvent.Response.RUNESCAPE_UPDATE_2;

@ScriptMeta(developer = "Streagrem", name = "LOL", desc = "LOL")
public class Main extends TaskScript implements RenderListener, DeathListener, LoginResponseListener {

    public static final String MULE_NAME = "ScatGrem";
    private static final int MULE_AMOUNT = 1_000_000;
    public static final int MULE_WORLD = 454;
    private static final int MULE_AMOUNT_TO_KEEP = 500_000;
    public static final Position MULE_POSITION = new Position(3203, 3388, 1);
    public static final String API_KEY = "S1Z8S8QHPE0LST3E2H07T8YABM63L17AW738NN61LAT0CT9NQG38JLDUDY7FCX5YG0ZVZ4"; // Not used atm

    private ScriptPaint paint;
    private StopWatch runtime;

    public StopWatch getRuntime() {
        return runtime;
    }

    public void submitNextTaskSet(Task... tasks) {
        removeAll();
        submit(tasks);

        if (!GameCanvas.isInputEnabled()) {
            GameCanvas.setInputEnabled(true);
        }
    }

    @Override
    public void onStart() {
        Log.fine("Script Started");
        GameAccount account = RSPeer.getGameAccount();
        Log.info(account.getUsername() + ":" + account.getPassword());
        runtime = StopWatch.start();
        paint = new ScriptPaint(this);

        submit(new GetStartersGold(),
                new SellGE(),
                new Mule(MULE_AMOUNT, MULE_NAME, MULE_POSITION, MULE_WORLD, MULE_AMOUNT_TO_KEEP),
                new MemberWorldChecker(),
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

    public int playerDeaths = 0;

    @Override
    public void notify(DeathEvent e) {
        if (e.getSource().equals(Players.getLocal())) {
            Log.severe("You Died");

            int varp = Quest.NATURE_SPIRIT.getVarpValue();

            if (varp <= 75 || !Inventory.contains(i -> i.getName().contains("Burning amulet"))) {
                playerDeaths ++;
            }

            if (varp > 0 && varp < 75) {
                BankWrapper.doBanking(false, false, SupplyMapWrapper.getNatureSpiritKeepMap());
            }
        }
    }

    @Override
    public void notify(LoginResponseEvent loginResponseEvent) {
        if (loginResponseEvent.getResponse().equals(LoginResponseEvent.Response.ACCOUNT_DISABLED) ||
                loginResponseEvent.getResponse().equals(INVALID_CREDENTIALS)
        ) {

            setStopping(true);

        } else if (loginResponseEvent.getResponse().equals(LoginResponseEvent.Response.RUNESCAPE_UPDATE) ||
                loginResponseEvent.getResponse().equals(RUNESCAPE_UPDATE_2)) {

            String[] info = null;
            int world = WorldhopWrapper.getCurrentWorld() > 0 ? WorldhopWrapper.getCurrentWorld() : 359;

            try {
                for (LaunchedClient client : BotManagement.getRunningClients()) {
                    if (client.getProxyIp() != null && !client.getProxyIp().isEmpty()) {
                        info = new String[]{RSPeer.getGameAccount().getUsername(), RSPeer.getGameAccount().getPassword(), client.getProxyIp(), "8000"};
                    }
                }

                Thread.sleep(Random.nextInt(120_000, 360_000));
                new ClientQuickLauncher("LOL", false, world).launchClient(info);
                RSPeer.shutdown();
                setStopping(true);

            } catch (Exception e) {
                Log.info(e);
                setStopping(true);
                e.printStackTrace();
            }
        }
    }
}
