package script.tasks;

import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.api.Game;
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
import java.net.InetAddress;
import java.net.Socket;
import java.util.NavigableMap;
import java.util.TreeMap;

public class Mule extends Task {

    private final int muleKeep;
    private boolean trading;
    private int begWorld = -1;
    private boolean banked;
    private boolean soldItems;
    private int gp;
    private final int muleAmount;
    private final Position mulePosition;
    private final int muleWorld;
    private final String muleName;
    private final String muleIP;

    public Mule(String muleIP, int muleAmount, String muleName, Position mulePosition, int muleWorld, int muleKeep) {
        this.muleAmount = muleAmount;
        this.muleName = muleName;
        this.mulePosition = mulePosition;
        this.muleWorld = muleWorld;
        this.muleKeep = muleKeep;
        this.muleIP = muleIP;
    }

    @Override
    public boolean validate() {
        return Skills.getLevel(Skill.PRAYER) > 49
                && (Inventory.getCount(true, "Coins") >= muleAmount || Bank.getCount("Coins") >= muleAmount);
        //return (!GEWrapper.isSellItems() && BankWrapper.getTotalValue() >= muleAmount) || trading;
    }

    @Override
    public int execute() {
        BankWrapper.setMuleing(true);

        if (!GEWrapper.GE_AREA_LARGE.contains(Players.getLocal()) && !banked) {
            WalkingWrapper.walkToPosition(BankLocation.GRAND_EXCHANGE.getPosition());
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

        //loginMule();
        send(muleIP, "Trade:" + Players.getLocal().getName() + ":" + Worlds.getCurrent() + ":" + 0);

        if (mulePosition.distance() > 3) {
            WalkingWrapper.walkToPosition(mulePosition);
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
                            trading = false;
                            send(muleIP, "Done:" + Players.getLocal().getName());

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

    /**
     * Send method
     *
     * @param message - TRADE = Activate login , DONE - Turn off login
     */
    private static void send(String ip, String message) {
        try {
            sendTradeRequest(ip, message);
        } catch (Exception e) {
            Log.severe(e);
            e.printStackTrace();
        }
    }

    /**
     * Sends message to server from client (Slave)
     *
     * @param message - TRADE = Activate login , DONE - Turn off login
     * @throws IOException
     * @throws InterruptedException
     * @throws ClassNotFoundException
     */
    private static void sendTradeRequest(String ip, String message) throws IOException, InterruptedException, ClassNotFoundException {
        //get the localhost IP address, if server is running on some other IP, you need to use that
        //InetAddress host = InetAddress.getLocalHost();
        Socket socket = null;
        ObjectOutputStream oos = null;
        //establish socket connection to server
        socket = new Socket(ip, 9876);
        //write to socket using ObjectOutputStream
        oos = new ObjectOutputStream(socket.getOutputStream());
        Log.fine("Sending request to Socket Server");
        oos.writeObject(message);
        //read the server response message
        //close resources
        oos.close();
        Thread.sleep(500);
    }

    public static void logoutMule(String ip) {
        send(ip, "Done:" + Players.getLocal().getName());
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

