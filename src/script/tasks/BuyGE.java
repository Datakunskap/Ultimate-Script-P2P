package script.tasks;

import api.component.ExWorldHopper;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.GrandExchange;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.input.Keyboard;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.providers.RSGrandExchangeOffer;
import org.rspeer.runetek.providers.RSWorld;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;
import script.wrappers.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class BuyGE extends Task {

    private HashMap<String, Integer> SUPPLIES;
    private Iterator<String> itemsIterator;
    private HashSet<String> items;
    private String itemToBuy;
    private boolean checkedBank;
    private int coinsToSpend;

    public BuyGE() {
        SUPPLIES = SupplyMapWrapper.getCurrentSupplyMap();
    }

    @Override
    public boolean validate() {
        if (!GEWrapper.isBuySupplies() || !Game.isLoggedIn() || Players.getLocal() == null)
            return false;

        SUPPLIES = SupplyMapWrapper.getCurrentSupplyMap();

        if (itemsIterator != null || GEWrapper.itemsStillActive(RSGrandExchangeOffer.Type.BUY))
            return true;

        if (SUPPLIES != null && !GEWrapper.hasSupplies(SUPPLIES) && itemsIterator == null) {

            Log.fine("Buying Supplies");
            items = new HashSet<>();
            items.addAll(Arrays.asList(SUPPLIES.keySet().toArray(new String[0])));

            itemsIterator = items.iterator();
            itemToBuy = itemsIterator.next();
            return true;
        }

        if (SUPPLIES != null && GEWrapper.isBuySupplies()) {
            doneRestockingHelper();
        }
        return false;
    }

    @Override
    public int execute() {

        if (!GEWrapper.GE_AREA_LARGE.contains(Players.getLocal())) {
            WalkingWrapper.walkToPosition(BankLocation.GRAND_EXCHANGE.getPosition());
            return SleepWrapper.shortSleep600();
        }

        RSWorld world = Worlds.get(Worlds.getCurrent());
        if (world != null && !world.isMembers()) {
            ExWorldHopper.randomInstaHopInPureP2p();
            return SleepWrapper.shortSleep600();
        }

        if (!checkedBank) {
            Log.info("Checking Bank");
            BankWrapper.openAndDepositAll(true, SUPPLIES.keySet());
            Bank.close();
            Time.sleepUntil(Bank::isClosed, 1000, 5000);
            checkedBank = true;
        }

        coinsToSpend = Inventory.getCount(true, "Coins");

        if (!GrandExchange.isOpen()) {
            Bank.close();
            GEWrapper.openGE();
            return SleepWrapper.shortSleep600();
        }

        if (itemsIterator != null && !GEWrapper.itemsStillActive(RSGrandExchangeOffer.Type.BUY)) {
            if (stillNeedsItem(itemToBuy)) {
                if (GEWrapper.buy(itemToBuy, getQuantity(itemToBuy, true), getPrice(itemToBuy), false)) {
                    Log.info("Buying: " + getQuantity(itemToBuy, true) + " " + itemToBuy);
                    if (Time.sleepUntil(() -> GrandExchange.getFirst(x -> x.getItemName().toLowerCase().equals(itemToBuy.toLowerCase())) != null, 8000)) {
                        if (itemsIterator.hasNext()) {
                            itemToBuy = itemsIterator.next();
                        } else {
                            itemsIterator = null;
                        }
                    }
                }
            } else {
                Log.info("Already has: " + getQuantity(itemToBuy, true) + " " + itemToBuy);
                if (itemsIterator.hasNext()) {
                    itemToBuy = itemsIterator.next();
                } else {
                    itemsIterator = null;
                }
            }
        }

        if (!GrandExchange.getView().equals(GrandExchange.View.OVERVIEW)) {
            GrandExchange.open(GrandExchange.View.OVERVIEW);
        }

        if (GEWrapper.itemsStillActive(RSGrandExchangeOffer.Type.BUY)) {
            GrandExchange.collectAll();
            Keyboard.pressEnter();
        }

        if (!GEWrapper.itemsStillActive(RSGrandExchangeOffer.Type.BUY) && itemsIterator == null) {
            doneRestockingHelper();
        }

        return SleepWrapper.mediumSleep1000();
    }

    private void doneRestockingHelper() {
        Log.fine("Done Restocking");
        GEWrapper.setBuySupplies(false, false, SUPPLIES);
        if (SUPPLIES.size() > 10) {
            BankWrapper.openAndDepositAll(false, SUPPLIES.keySet());
        } else {
            BankWrapper.openAndDepositAll(false, false, SUPPLIES.keySet());
            if (SUPPLIES.containsKey("Salve graveyard teleport") && Bank.contains("Salve graveyard teleport")) {
                Bank.withdrawAll("Salve graveyard teleport");
                Time.sleepUntil(() -> !Bank.contains("Salve graveyard teleport"), 8000);
            }
        }

        if (Inventory.contains("Silver sickle (b)")) {
            Inventory.getFirst("Silver sickle (b)").interact(a -> true);
            Time.sleepUntil(() -> Equipment.contains("Silver sickle (b)"), 5000);
        }

        Bank.close();
        Interfaces.closeAll();
        Time.sleepUntil(() -> !Bank.isOpen() && !GrandExchange.isOpen(), 5000);
        checkedBank = false;
    }

    private boolean stillNeedsItem(String itemToBuy) {
        return (!Inventory.contains(itemToBuy) || Inventory.getCount(true, itemToBuy) < getQuantity(itemToBuy, true)) && !Equipment.contains(itemToBuy);
    }

    private int getQuantity(String item, boolean checkEnough) {
        int quantity = SUPPLIES.get(item);
        int price = 0;
        if (checkEnough) {
            price = getPrice(item);
        }

        if (quantity > 0) {
            if ((price * quantity) > coinsToSpend) {
                return coinsToSpend / price;
            }
            return SUPPLIES.get(item);
        }

        return 1;
    }

    private int getPrice(String item) {
        int price;

        try {
            price = PriceCheckService.getPrice(item).getBuyAverage();
        } catch (Exception e) {
            return coinsToSpend / getQuantity(item, false);
        }

        price += ((int) (price * .80));

        if (price <= 0 || price > coinsToSpend) {
            return coinsToSpend / getQuantity(item, false);
        }

        return price;
    }
}
