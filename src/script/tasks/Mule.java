package script.tasks;

import api.component.ExWorldHopper;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.EnterInput;
import org.rspeer.runetek.api.component.GrandExchange;
import org.rspeer.runetek.api.component.Trade;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import org.rspeer.runetek.api.input.Keyboard;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.Script;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;
import script.wrappers.*;

import java.io.*;
import java.util.Calendar;
import java.util.NavigableMap;
import java.util.TreeMap;

public class Mule extends Task {

    private static final String MULE_FILE_PATH = Script.getDataDirectory() + "\\mule.txt";
    private final int muleKeep;
    private boolean trading;
    private int begWorld = -1;
    private boolean banked;
    private int gp;
    private final int muleAmount;
    private final Position mulePosition;
    private final int muleWorld;
    private final String muleName;
    private static final String CRLF = "\r\n";
    private Calendar calendar;

    public Mule(int muleAmount, String muleName, Position mulePosition, int muleWorld, int muleKeep) {
        this.muleAmount = muleAmount;
        this.muleName = muleName;
        this.mulePosition = mulePosition;
        this.muleWorld = muleWorld;
        this.muleKeep = muleKeep;
    }

    @Override
    public boolean validate() {
        java.util.TimeZone tz = java.util.TimeZone.getTimeZone("GMT-1");
        calendar = java.util.Calendar.getInstance(tz);

        return Skills.getLevel(Skill.PRAYER) >= 50 && !GEWrapper.isSellItems()
                && ((Inventory.getCount(true, "Coins") >= muleAmount || Bank.getCount("Coins") >= muleAmount || trading)
                || (calendar.get(java.util.Calendar.HOUR_OF_DAY) == 8 && !BankWrapper.hasBanTimeMuled()));
    }

    @Override
    public int execute() {
        BankWrapper.setMuleing(true);

        if (GrandExchange.isOpen()) {
            GEWrapper.closeGE();
        }

        if (!banked) {
            Log.info("Withdrawing Items To Mule");

            BankWrapper.doBanking(false);
            Time.sleepUntil(Inventory::isEmpty, 2000, 8000);

            Log.fine("Withdrawing Coins");
            Item coins = Bank.getFirst("Coins");
            if (coins != null) {
                banked = true;
                gp = coins.getStackSize()/* - Script.OGRESS_START_GP*/;
                Bank.withdraw("Coins", gp);
            } else {
                Log.severe("Cant find coins");
            }
            Time.sleepUntil(() -> Inventory.contains("Coins"), 1000, 5000);

            BankWrapper.updateBankValue();
            Bank.close();
            Time.sleep(300, 800);
            BankWrapper.updateInventoryValue();
            return SleepWrapper.shortSleep600();
        }

        loginMule();

        if (mulePosition.distance() < 8 && Worlds.getCurrent() != muleWorld && Game.isLoggedIn()) {
            if (begWorld < 1 && Worlds.getCurrent() != muleWorld) {
                begWorld = Worlds.getCurrent();
            }

            Time.sleepUntil(() -> ExWorldHopper.instaHopTo(muleWorld), 5000, 15_000);
            return SleepWrapper.shortSleep600();
        }

        if (mulePosition.distance() > 3 || Players.getLocal().getFloorLevel() != mulePosition.getFloorLevel()) {
            if (!WalkingWrapper.walkToPosition(mulePosition)) {

                WalkingWrapper.walkToPosition(new Position(mulePosition.getX(), mulePosition.getY(), Players.getLocal().getFloorLevel()));

                if (mulePosition.distance() <= 3 && Players.getLocal().getFloorLevel() != mulePosition.getFloorLevel()) {
                    SceneObject ladder = SceneObjects.getNearest(o -> o.containsAction("Climb-up"));
                    if (ladder != null) {
                        ladder.interact("Climb-up");
                        Time.sleepUntil(() -> Players.getLocal().getFloorLevel() == mulePosition.getFloorLevel(), 5000);
                    }
                }
            }
            return SleepWrapper.shortSleep600();
        }

        Player mulePlayer = Players.getNearest(muleName);

        if (mulePlayer != null && mulePlayer.distance() < 4) {

            if (!Inventory.isEmpty()) {

                if (Players.getNearest(muleName) != null && !Trade.isOpen()) {
                    Players.getNearest(muleName).interact("Trade with");
                    Time.sleep(3000, 5000);
                }
                if (!Inventory.isEmpty()) {
                    if (Trade.isOpen(false)) {
                        trading = true;
                        // handle first trade window...
                        int attempts = 0;
                        while (Trade.isOpen(false) && attempts < 7) {
                            Log.info("Entering trade offer");
                            Item tradeItem = Inventory.getFirst("Coins");

                            if (tradeItem != null) {
                                Trade.offer("Coins", x -> x.contains("X"));
                                Time.sleepUntil(EnterInput::isOpen, 5000);
                                EnterInput.initiate(tradeItem.getStackSize() - muleKeep);
                                Keyboard.pressEnter();

                                if (Time.sleepUntil(() -> Trade.contains(true, tradeItem.getName()), 6000)) {
                                    Log.info("Trade entered & accepted");
                                    Trade.accept();
                                    Time.sleepUntil(() -> Trade.isOpen(true), 6000);
                                    break;
                                }
                            }
                            Time.sleep(400, 800);
                            attempts++;
                        }
                    }
                    if (Trade.isOpen(true)) {
                        // handle second trade window...
                        Time.sleep(500, 1500);
                        if (Trade.accept()) {
                            Time.sleep(3000);
                            Log.fine("Trade completed shutting down mule");
                            trading = false;
                            banked = false;
                            logoutMule();

                            BankWrapper.updateInventoryValue();
                            BankWrapper.setAmountMuled(BankWrapper.getAmountMuled() + (gp - muleKeep));

                            GEWrapper.setBuySupplies(true, false, SupplyMapWrapper.getMortMyreFungusItemsMap());
                            if (calendar.get(java.util.Calendar.HOUR_OF_DAY) == 8 || calendar.get(java.util.Calendar.HOUR_OF_DAY) == 9) {
                                BankWrapper.setHasBanTimeMuled(true);
                            }

                            BankWrapper.setMuleing(false);

                            SceneObject ladder = SceneObjects.getNearest(o -> o.containsAction("Climb-down"));
                            if (Players.getLocal().getFloorLevel() != 0 && ladder != null) {
                                ladder.interact("Climb-down");
                                Time.sleepUntil(() -> Players.getLocal().getFloorLevel() == 0, 8000);
                            }


                            Time.sleep(4000, 6500);
                            if (begWorld != -1) {
                                ExWorldHopper.instaHopTo(begWorld);
                            } else {
                                ExWorldHopper.randomInstaHopInPureP2p();
                            }
                            return 3000;
                        }
                        Time.sleep(700);
                    }

                }
            }
        }

        return 500;
    }

    public static void loginMule() {
        try {
            File file = new File(MULE_FILE_PATH);

            if (!file.exists()) {
                file.createNewFile();
            }
            PrintWriter pw = new PrintWriter(file);
            pw.println("mule");
            pw.close();

            String status1;
            FileReader fr = new FileReader(file);
            BufferedReader br = new BufferedReader(fr);

            while (((status1 = br.readLine())) != null) {
                Log.info(status1);
            }

            br.close();
        } catch (IOException e) {
            Log.info("File not found");
        }
    }

    public static void logoutMule() {
        try {
            File file = new File(MULE_FILE_PATH);

            if (!file.exists()) {
                Log.info("Logout file not found");
            }
            PrintWriter pw = new PrintWriter(file);
            pw.println("done");
            pw.close();

            Log.info("done");

        } catch (IOException e) {
            Log.info("File not found");
        }
    }

    private static final NavigableMap<Long, String> suffixes = new TreeMap<>();

    static {
        suffixes.put(1_000L, "k");
        suffixes.put(1_000_000L, "M");
        suffixes.put(1_000_000_000L, "B");
        suffixes.put(1_000_000_000_000L, "T");
        suffixes.put(1_000_000_000_000_000L, "P");
        suffixes.put(1_000_000_000_000_000_000L, "E");
    }
}

