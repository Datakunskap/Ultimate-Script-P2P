package script.tasks;

import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
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
import script.wrappers.GEWrapper;
import script.wrappers.SleepWrapper;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

public class BuySupplies extends Task {

    private static final String[] ALL_ITEMS_NEEDED_FOR_ACCOUNT_PREPERATION = new String[]{
            "Lumbridge teleport",
            "Staff of air",
            "Staff of fire",
            "Amulet of glory(6)",
            "Ring of wealth (5)",
            "Air rune",
            "Mind rune",
            "Water rune",
            "Earth rune",
            "Tuna",
            "Stamina potion",
            "Cheese",
            "Leather gloves",
            "Falador teleport",
            "Games necklace(8)",
            "Rope",
            "Adamant scimitar",
            "Ring of recoil",
            "Bucket",
            "Rune essence",
            "Varrock teleport",
            "Silver sickle"
    };

    private Iterator<String> itemsIterator;
    private HashSet<String> items;
    private String itemToBuy;
    private boolean checkedBank;
    private int coinsToSpend;

    @Override
    public boolean validate() {
        if (itemsIterator != null || GEWrapper.itemsStillActive(RSGrandExchangeOffer.Type.BUY))
            return true;

        if (GEWrapper.isBuySupplies()
                && !GEWrapper.hasSupplies(ALL_ITEMS_NEEDED_FOR_ACCOUNT_PREPERATION) && itemsIterator == null) {

            Log.fine("Buying Supplies");
            items = new HashSet<>();
            items.addAll(Arrays.asList(ALL_ITEMS_NEEDED_FOR_ACCOUNT_PREPERATION));

            itemsIterator = items.iterator();
            itemToBuy = itemsIterator.next();
            return true;
        }

        GEWrapper.setBuySupplies(false);
        return false;
    }

    @Override
    public int execute() {

        if (!GEWrapper.GE_AREA_LARGE.contains(Players.getLocal())) {
            Movement.walkTo(BankLocation.GRAND_EXCHANGE.getPosition());
            return SleepWrapper.shortSleep350();
        }

        if (!checkedBank) {
            while (!Bank.isOpen() && Game.isLoggedIn()) {
                Bank.open();
                Time.sleep(600, 1000);
            }
            Bank.withdrawAll("Coins");
            Time.sleepUntil(() -> !Bank.contains("Coins"), 5000);
            Bank.close();
            Time.sleepUntil(Bank::isClosed, 1000, 5000);
            checkedBank = true;
        }

        coinsToSpend = Inventory.getCount(true, "Coins");

        // check GP
        if (itemsIterator != null && !GEWrapper.itemsStillActive(RSGrandExchangeOffer.Type.BUY) && stillNeedsItem(itemToBuy)) {
            if (coinsToSpend < (getPrice(itemToBuy) * getQuantity(itemToBuy))) {
                Log.severe("NOT ENOUGH GP  |  " + itemToBuy + " : " + (getPrice(itemToBuy) * getQuantity(itemToBuy)));
                itemsIterator = null;
                return SleepWrapper.shortSleep350();
            }
        }

        if (!GrandExchange.isOpen()) {
            Bank.close();
            GEWrapper.openGE();
            return SleepWrapper.shortSleep350();
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
        }

        return Random.low(800, 1500);
    }

    private boolean stillNeedsItem(String itemToBuy) {
        return (!Inventory.contains(itemToBuy) || Inventory.getCount(true, itemToBuy) < getQuantity(itemToBuy)) && !Equipment.contains(itemToBuy);
    }

    private int getQuantity(String item) {
        if (item.equalsIgnoreCase("Lumbridge teleport"))
            return 10;
        if (item.equalsIgnoreCase("Staff of air"))
            return 1;
        if (item.equalsIgnoreCase("Staff of fire"))
            return 1;
        if (item.equalsIgnoreCase("Amulet of glory(6)"))
            return 2;
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
        if (item.equalsIgnoreCase("Tuna"))
            return 100;
        if (item.equalsIgnoreCase("Stamina potion"))
            return 10;
        if (item.equalsIgnoreCase("Cheese"))
            return 2;
        if (item.equalsIgnoreCase("Leather gloves"))
            return 1;
        if (item.equalsIgnoreCase("Falador teleport"))
            return 5;
        if (item.equalsIgnoreCase("Games necklace (8)"))
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

        return 1;
    }

    private int getPrice(String item) {
        return coinsToSpend / items.size();
    }

}
