package script.quests.priest_in_peril.tasks;

import org.rspeer.runetek.adapter.scene.SceneObject;
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
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.runetek.providers.RSGrandExchangeOffer;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;
import script.quests.priest_in_peril.data.Quest;
import script.wrappers.GEWrapper;
import script.wrappers.SleepWrapper;
import script.wrappers.WalkingWrapper;

import java.util.*;

public class BuySupplies extends Task {

    private final HashMap<String, Integer> SUPPLIES;
    private Iterator<String> itemsIterator;
    private HashSet<String> items;
    private String itemToBuy;
    private boolean checkedBank;
    private int coinsToSpend;
    private Position lastPosition;

    public BuySupplies(final HashMap<String, Integer> SUPPLIES) {
        this.SUPPLIES = SUPPLIES;
    }

    @Override
    public boolean validate() {
        if (itemsIterator != null || GEWrapper.itemsStillActive(RSGrandExchangeOffer.Type.BUY))
            return true;

        if (Quest.NATURE_SPIRIT.getVarpValue() <= 65
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
            if (lastPosition == null)
                lastPosition = Players.getLocal().getPosition();

            Movement.walkTo(BankLocation.GRAND_EXCHANGE.getPosition(), WalkingWrapper::shouldBreakOnTarget);
            if (!Movement.isRunEnabled()) {
                Movement.toggleRun(true);
            }

            SceneObject bridge = SceneObjects.getNearest("Bridge");
            if (bridge != null && bridge.containsAction("Jump") && bridge.interact(a -> true)) {
                Time.sleepUntil(() -> !Players.getLocal().isAnimating(), 5000);
            }
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
            if (lastPosition.distance() > 3) {
                Movement.walkTo(lastPosition);
            }
        }

        return Random.low(800, 1500);
    }

    private boolean stillNeedsItem(String itemToBuy) {
        return (!Inventory.contains(itemToBuy) || Inventory.getCount(true, itemToBuy) < getQuantity(itemToBuy)) && !Equipment.contains(itemToBuy);
    }

    private int getQuantity(String item) {

        return 1;
    }

    private int getPrice(String item) {
        return coinsToSpend / items.size();
    }

}
