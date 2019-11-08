package script.tasks;

import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.GrandExchange;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.input.Keyboard;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.providers.RSGrandExchangeOffer;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;
import script.wrappers.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;

public class BuySupplies extends Task {

    private final HashMap<String, Integer> SUPPLIES;
    private Iterator<String> itemsIterator;
    private HashSet<String> items;
    private String itemToBuy;
    private boolean checkedBank;
    private int coinsToSpend;
    private boolean keepItems;

    public BuySupplies(boolean keepItems) {
        SUPPLIES = SupplyMapWrapper.getCurrentSupplyMap();
        this.keepItems = keepItems;
    }

    public BuySupplies() {
        SUPPLIES = SupplyMapWrapper.getCurrentSupplyMap();
    }

    @Override
    public boolean validate() {
        if (!GEWrapper.isBuySupplies() || !Game.isLoggedIn() || Players.getLocal() == null)
            return false;

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

        if (GEWrapper.isBuySupplies()) {
            Log.fine("Done Restocking");
            doneRestockingHelper();
        }
        return false;
    }

    @Override
    public int execute() {

        if (!GEWrapper.GE_AREA_LARGE.contains(Players.getLocal())) {
            Movement.walkTo(BankLocation.GRAND_EXCHANGE.getPosition(), WalkingWrapper::shouldBreakOnTarget);
            Movement.toggleRun(true);
            return SleepWrapper.shortSleep600();
        }

        if (!checkedBank) {
            Log.info("Checking Bank");
            if (keepItems) {
                BankWrapper.openAndDepositAll(true, SUPPLIES.keySet());
            } else {
                BankWrapper.openAndDepositAll(true);
                itemsIterator = null;
                items = new HashSet<>(BankWrapper.getItemsNeeded(SUPPLIES));
                if (items.size() > 0) {
                    itemsIterator = items.iterator();
                    itemToBuy = itemsIterator.next();
                }
            }
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
                if (GEWrapper.buy(itemToBuy, getQuantity(itemToBuy), getPrice(itemToBuy), false)) {
                    Log.info("Buying: " + getQuantity(itemToBuy) + " " + itemToBuy);
                    if (Time.sleepUntil(() -> GrandExchange.getFirst(x -> x.getItemName().toLowerCase().equals(itemToBuy.toLowerCase())) != null, 8000)) {
                        if (itemsIterator.hasNext()) {
                            itemToBuy = itemsIterator.next();
                        } else {
                            itemsIterator = null;
                        }
                    }
                }
            } else {
                Log.info("Already has: " + getQuantity(itemToBuy) + " " + itemToBuy);
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
            Log.fine("Done Restocking");
            doneRestockingHelper();
        }

        return SleepWrapper.shortSleep600();
    }

    private void doneRestockingHelper() {
        GEWrapper.setBuySupplies(false, false, SUPPLIES);
        if (keepItems) {
            BankWrapper.openAndDepositAll(false, SUPPLIES.keySet());
        } else {
            BankWrapper.openAndDepositAll();
        }
        Bank.close();
        Interfaces.closeAll();
        Time.sleepUntil(() -> !Bank.isOpen() && !GrandExchange.isOpen(), 5000);
    }

    private boolean stillNeedsItem(String itemToBuy) {
        return (!Inventory.contains(itemToBuy) || Inventory.getCount(true, itemToBuy) < getQuantity(itemToBuy)) && !Equipment.contains(itemToBuy);
    }

    private int getQuantity(String item) {
        if (SUPPLIES.get(item) > 0)
            return SUPPLIES.get(item);

        return 1;
    }

    private int getPrice(String item) {
        int price;

        try {
            price = PriceCheckService.getPrice(item).getBuyAverage();
        } catch (Exception e) {
            return coinsToSpend;
        }

        price += ((int) (price * .50));

        if (price <= 0 || price > coinsToSpend) {
            return coinsToSpend;
        }

        return price;
    }

}
