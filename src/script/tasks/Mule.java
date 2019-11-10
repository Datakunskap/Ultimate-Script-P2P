package script.tasks;

import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.*;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import org.rspeer.runetek.api.input.Keyboard;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;
import script.wrappers.*;

import java.io.*;
import java.util.NavigableMap;
import java.util.TreeMap;

public class Mule extends Task {

    private final int muleKeep;
    private boolean trading;
    private int begWorld = -1;
    private static final String MULE_FILE_PATH = org.rspeer.script.Script.getDataDirectory() + "\\mule.txt";
    private boolean banked;
    private String status;
    private boolean soldItems;
    private int gp;
    private final int muleAmount;
    private final Position mulePosition;
    private final int muleWorld;
    private final String muleName;

    public Mule(int muleAmount, String muleName, Position mulePosition, int muleWorld, int muleKeep) {
        this.muleAmount = muleAmount;
        this.muleName = muleName;
        this.mulePosition = mulePosition;
        this.muleWorld = muleWorld;
        this.muleKeep = muleKeep;
    }

    @Override
    public boolean validate() {
        return Skills.getLevel(Skill.PRAYER) > 49
                && (Inventory.getCount(true, "Coins") >= muleAmount || Bank.getCount("Coins") >= muleAmount);
        //return (!GEWrapper.isSellItems() && BankWrapper.getTotalValue() >= muleAmount) || trading;
    }

    private void loginMule() {
        String status1;
        try {
            File file = new File(MULE_FILE_PATH);

            if (!file.exists()) {
                file.createNewFile();
            }
            PrintWriter pw = new PrintWriter(file);
            pw.println("mule");
            pw.close();

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

    @Override
    public int execute() {
        BankWrapper.setMuleing(true);

        if (!GEWrapper.GE_AREA_LARGE.contains(Players.getLocal()) && !banked) {
            Movement.walkTo(BankLocation.GRAND_EXCHANGE.getPosition(), ()
                    -> {
                if (WalkingWrapper.shouldBreakOnTarget() || WalkingWrapper.shouldBreakOnRunenergy()) {
                    if (!Movement.isRunEnabled()) {
                        Movement.toggleRun(true);
                    }
                }
                if (GEWrapper.GE_AREA_LARGE.contains(Players.getLocal())) {
                    return true;
                }
                return false;
            });
            return SleepWrapper.shortSleep350();
        }
        if (GrandExchange.isOpen()) {
            GEWrapper.closeGE();
        }

        if (!soldItems) {
            GEWrapper.setSellItems(true);
            soldItems = true;
            return SleepWrapper.shortSleep350();
        }

        if (!banked) {
            Log.info("Withdrawing Items To Mule");
            banked = true;

            BankWrapper.openAndDepositAll(false);
            Time.sleepUntil(Inventory::isEmpty, 2000, 8000);
            Bank.setWithdrawMode(Bank.WithdrawMode.NOTE);
            Time.sleepUntil(() -> Bank.getWithdrawMode().equals(Bank.WithdrawMode.NOTE), 2000, 8000);

            Log.fine("Withdrawing Coins");
            Item coins = Bank.getFirst("Coins");
            if (coins != null) {
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

        if (Worlds.getCurrent() != muleWorld) {
            begWorld = Worlds.getCurrent();
            WorldHopper.hopTo(muleWorld);

            if (Dialog.isOpen()) {
                if (Dialog.canContinue()) {
                    Dialog.processContinue();
                }
                Dialog.process(x -> x != null && x.toLowerCase().contains("future"));
                Dialog.process(x -> x != null && (x.toLowerCase().contains("switch") || x.toLowerCase().contains("yes")));
                Time.sleepUntil(() -> !Dialog.isProcessing(), 10000);
            }

            Time.sleepUntil(() -> Worlds.getCurrent() == muleWorld && Players.getLocal() != null, 10000);
            return SleepWrapper.shortSleep600();
        }

        if (Dialog.canContinue()) {
            Dialog.processContinue();
            Time.sleep(1000);
        }

        loginMule();

        if (mulePosition.distance() > 3) {
            Movement.walkTo(mulePosition.randomize(3), WalkingWrapper::shouldBreakOnTarget);
            Movement.toggleRun(true);
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
                        while (Trade.isOpen(false)) {
                            attempts++;
                            Log.info("Entering trade offer");
                            Item[] tradeItems = Inventory.getItems();

                            for (Item o : tradeItems) {
                                if (o.getId() == 995) {
                                    Trade.offer("Coins", x -> x.contains("X"));
                                    Time.sleepUntil(EnterInput::isOpen, 8000);
                                    EnterInput.initiate(o.getStackSize() - muleKeep);
                                    Keyboard.pressEnter();
                                } else {
                                    Trade.offerAll(i -> i.getId() == o.getId());
                                }
                                Time.sleepUntil(() -> Trade.contains(true, i -> i.getId() == o.getId() && i.getStackSize() == o.getStackSize()), 2000, 8000);
                            }
                            if (Trade.contains(true, "Coins")) {
                                Log.info("Trade entered & accepted");
                                Trade.accept();
                                Time.sleepUntil(() -> Trade.isOpen(true), 5000);
                                break;
                            }
                            if (attempts > 6) {
                                break;
                            }
                        }
                    }
                    if (Trade.isOpen(true)) {
                        // handle second trade window...
                        Time.sleep(500, 1500);
                        if (Trade.accept()) {
                            Time.sleep(3000);
                            Log.fine("Trade completed shutting down mule");
                            soldItems = false;
                            logoutMule();
                            trading = false;
                            BankWrapper.updateInventoryValue();
                            BankWrapper.setAmountMuled(BankWrapper.getAmountMuled() + (gp - muleKeep));
                            //main.setRandMuleKeep(main.minKeep, main.maxKeep);
                            if (begWorld != -1) {
                                WorldHopper.hopTo(begWorld);
                                Time.sleepUntil(() -> Worlds.getCurrent() == begWorld, 10_000);
                            }
                            Time.sleep(8000, 10000);
                            BankWrapper.setMuleing(false);
                            banked = false;
                            BankWrapper.openAndDepositAll(true, false, SupplyMapWrapper.getMortMyreFungusItemsMap().keySet());
                        }
                        Time.sleep(700);
                    }

                }
            }
        }

        return 500;
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

