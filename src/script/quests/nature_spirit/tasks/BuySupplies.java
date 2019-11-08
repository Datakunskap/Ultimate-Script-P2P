package script.quests.nature_spirit.tasks;

import nature_spirit.Main;
import nature_spirit.data.Quest;
import nature_spirit.wrappers.BankWrapper;
import nature_spirit.wrappers.GEWrapper;
import nature_spirit.wrappers.WalkingWrapper;
import org.rspeer.runetek.adapter.scene.SceneObject;
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

import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;

public class BuySupplies extends Task {

    private static String[] NATURE_SPIRIT_SUPPLIES = new String[] {"Silver sickle", "Ghostspeak amulet"};
    private Iterator<String> itemsIterator;
    private HashSet<String> items;
    private String itemToBuy;
    private boolean checkedBank;
    private int coinsToSpend;
    private Position lastPosition;

    @Override
    public boolean validate() {
        if (itemsIterator != null || GEWrapper.itemsStillActive(RSGrandExchangeOffer.Type.BUY))
            return true;

        if (Quest.NATURE_SPIRIT.getVarpValue() <= 15
                && !GEWrapper.hasSupplies(NATURE_SPIRIT_SUPPLIES) && itemsIterator == null) {

            Log.fine("Buying Supplies");
            items = new HashSet<>();
            items.addAll(Arrays.asList(NATURE_SPIRIT_SUPPLIES));

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

            Movement.walkTo(BankLocation.GRAND_EXCHANGE.getPosition(), WalkingWrapper::shouldBreakWalkLoop);
            if (!Movement.isRunEnabled()) {
                Movement.toggleRun(true);
            }

            SceneObject bridge = SceneObjects.getNearest("Bridge");
            if (bridge != null && bridge.containsAction("Jump") && bridge.interact(a -> true)) {
                Time.sleepUntil(() -> !Players.getLocal().isAnimating(), 5000);
            }
            return Main.getLoopReturn();
        }

        if (!checkedBank) {
            BankWrapper.openAndDepositAll(true, NATURE_SPIRIT_SUPPLIES);
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
                return Main.getLoopReturn();
            }
        }

        if (!GrandExchange.isOpen()) {
            Bank.close();
            GEWrapper.openGE();
            return Main.getLoopReturn();
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
            BankWrapper.openAndDepositAll(NATURE_SPIRIT_SUPPLIES);
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
