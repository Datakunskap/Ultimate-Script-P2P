package script.tasks;

import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.GrandExchange;
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.input.Keyboard;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.providers.RSGrandExchangeOffer;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;
import script.wrappers.BankWrapper;
import script.wrappers.GEWrapper;
import script.wrappers.SleepWrapper;

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

    public BuySupplies(final HashMap<String, Integer> SUPPLIES) {
        this.SUPPLIES = SUPPLIES;
    }

    public BuySupplies(final String[] SUPPLIES) {
        this.SUPPLIES = new HashMap<>();
        for (String s : SUPPLIES) {
            this.SUPPLIES.put(s, -1);
        }
    }

    @Override
    public boolean validate() {
        if (itemsIterator != null || GEWrapper.itemsStillActive(RSGrandExchangeOffer.Type.BUY))
            return true;

        if (GEWrapper.isBuySupplies()
                && !GEWrapper.hasSupplies(SUPPLIES) && itemsIterator == null) {

            Log.fine("Buying Supplies");
            items = new HashSet<>();
            items.addAll(Arrays.asList(SUPPLIES.keySet().toArray(new String[0])));

            itemsIterator = items.iterator();
            itemToBuy = itemsIterator.next();
            return true;
        }

        return false;
    }

    @Override
    public int execute() {

        if (!GEWrapper.GE_AREA_LARGE.contains(Players.getLocal())) {
            Movement.walkTo(BankLocation.GRAND_EXCHANGE.getPosition());
            return SleepWrapper.mediumSleep1500();
        }

        if (!checkedBank) {
            BankWrapper.openAndDepositAll(true, SUPPLIES.keySet().toArray(new String[0]));
            Bank.close();
            Time.sleepUntil(Bank::isClosed, 1000, 5000);
            checkedBank = true;
        }

        coinsToSpend = Inventory.getCount(true, "Coins");

        if (!GrandExchange.isOpen()) {
            Bank.close();
            GEWrapper.openGE();
            return SleepWrapper.mediumSleep1500();
        }

        if (itemsIterator != null && !GEWrapper.itemsStillActive(RSGrandExchangeOffer.Type.BUY)) {
            if (stillNeedsItem(itemToBuy)) {
                if (GEWrapper.buy(itemToBuy, getQuantity(itemToBuy), getPrice(itemToBuy), false)) {
                    Log.info("Buying: " + getQuantity(itemToBuy) + " " + itemToBuy);
                    if (Time.sleepUntil(() -> GrandExchange.getFirst(x -> x.getItemName().toLowerCase().equals(itemToBuy)) != null, 8000)) {
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
            GEWrapper.setBuySupplies(false);
            GEWrapper.closeGE();
        }

        return SleepWrapper.mediumSleep1500();
    }

    private boolean stillNeedsItem(String itemToBuy) {
        return (!Inventory.contains(itemToBuy) || Inventory.getCount(true, itemToBuy) < getQuantity(itemToBuy)) && !Equipment.contains(itemToBuy);
    }

    private int getQuantity(String item) {
        if (SUPPLIES.get(item) > 0) {
            return SUPPLIES.get(item);
        } else {
            if (item.equalsIgnoreCase("Lumbridge teleport"))
                return 10;
            if (item.equalsIgnoreCase("Staff of air"))
                return 1;
            if (item.equalsIgnoreCase("Staff of fire"))
                return 1;
            if (item.equalsIgnoreCase("Amulet of glory(6)"))
                return 5;
            if (item.equalsIgnoreCase("Ring of wealth (5)"))
                return 2;
            if (item.equalsIgnoreCase("Mind rune"))
                return 1000;
            if (item.equalsIgnoreCase("Air rune"))
                return 1000;
            if (item.equalsIgnoreCase("Water rune"))
                return 200;
            if (item.equalsIgnoreCase("Earth rune"))
                return 200;
            if (item.equalsIgnoreCase("Fire rune"))
                return 300;
            if (item.equalsIgnoreCase("Tuna"))
                return 100;
            if (item.equalsIgnoreCase("Stamina potion(4)"))
                return 10;
            if (item.equalsIgnoreCase("Cheese"))
                return 2;
            if (item.equalsIgnoreCase("Leather gloves"))
                return 1;
            if (item.equalsIgnoreCase("Falador teleport"))
                return 5;
            if (item.equalsIgnoreCase("Games necklace(8)"))
                return 1;
            if (item.equalsIgnoreCase("Rope"))
                return 2;
            if (item.equalsIgnoreCase("Adamant scimitar"))
                return 1;
            if (item.equalsIgnoreCase("Ring of recoil"))
                return 1;
            if (item.equalsIgnoreCase("Bucket"))
                return 1;
            if (item.equalsIgnoreCase("Rune essence"))
                return 50;
            if (item.equalsIgnoreCase("Varrock teleport"))
                return 5;
            if (item.equalsIgnoreCase("Silver sickle"))
                return 1;
            if (item.equalsIgnoreCase("Dragon bones"))
                return 300;
            if (item.equalsIgnoreCase("Burning amulet(5)"))
                return 5;
        }
        return 1;
    }

    private int getPrice(String item) {
        if (item.equalsIgnoreCase("Dragon bones")) {
            return 3500;
        }
        return coinsToSpend / getQuantity(item);
    }

}
