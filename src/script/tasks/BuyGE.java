package script.tasks;

import api.component.ExWorldHopper;
import org.rspeer.runetek.api.Definitions;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.Worlds;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.GrandExchange;
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.input.Keyboard;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.providers.RSGrandExchangeOffer;
import org.rspeer.runetek.providers.RSItemDefinition;
import org.rspeer.runetek.providers.RSWorld;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;
import script.quests.nature_spirit.data.Quest;
import script.tasks.fungus.Fungus;
import script.wrappers.*;

import java.util.*;

public class BuyGE extends Task {

    private LinkedHashMap<String, Integer> SUPPLIES;
    private Iterator<Map.Entry<String, Integer>> itemsIterator;
    private LinkedHashMap<String, Integer> items;
    private String itemToBuy;
    private int coinsToSpend;

    public BuyGE() {
        SUPPLIES = SupplyMapWrapper.getCurrentSupplyMap();
    }

    @Override
    public boolean validate() {
        if (!GEWrapper.isBuySupplies() || !Game.isLoggedIn() || Players.getLocal() == null) {
            if (BankWrapper.hasCheckedBank()) {
                BankWrapper.setHasCheckedBank(false);
            }
            return false;
        }

        SUPPLIES = SupplyMapWrapper.getCurrentSupplyMap();

        if (itemsIterator != null || GEWrapper.itemsStillActive(RSGrandExchangeOffer.Type.BUY))
            return true;

        if (SUPPLIES != null && !GEWrapper.hasSupplies(SUPPLIES) && itemsIterator == null) {

            Log.fine("Buying Supplies");
            items = new LinkedHashMap<>(SUPPLIES);

            itemsIterator = items.entrySet().iterator();
            itemToBuy = itemsIterator.next().getKey();
            return true;
        }

        if (SUPPLIES != null && GEWrapper.isBuySupplies()) {
            doneRestockingHelper();
        }
        return false;
    }

    @Override
    public int execute() {
        if (!Game.isLoggedIn() || Players.getLocal() == null)
            return 2000;

        if (BankLocation.GRAND_EXCHANGE.getPosition().distance() > 15) {
            if (Inventory.contains("Varrock teleport")) {
                Fungus.useTeleportTab("Varrock teleport");
            }
            WalkingWrapper.walkToPosition(BankLocation.GRAND_EXCHANGE.getPosition());
            return SleepWrapper.shortSleep600();
        }

        if (!BankWrapper.hasCheckedBank()) {
            Log.info("Checking Bank");
            BankWrapper.doBanking(true, SUPPLIES);
            if (Bank.isOpen()) {
                Bank.close();
                Time.sleepUntil(Bank::isClosed, 1000, 5000);
            }
            if (Inventory.contains("Coins")) {
                BankWrapper.setHasCheckedBank(true);
            }
        }

        RSWorld world = Worlds.get(Worlds.getCurrent());
        if (world != null && !world.isMembers()) {
            Log.info("World Hopping to P2P");
            ExWorldHopper.randomInstaHopInPureP2p();
            return SleepWrapper.mediumSleep1000();
        }

        coinsToSpend = Inventory.getCount(true, "Coins");

        if (!GrandExchange.isOpen()) {
            Bank.close();
            GEWrapper.openGE();
            return SleepWrapper.mediumSleep1000();
        }

        if (itemsIterator != null && !GEWrapper.itemsStillActive(RSGrandExchangeOffer.Type.BUY)) {
            if (stillNeedsItem(itemToBuy)) {
                if (GEWrapper.buy(itemToBuy, getQuantityToBuy(itemToBuy, true), getPrice(itemToBuy), false)) {
                    Log.info("Buying: " + getQuantityToBuy(itemToBuy, true) + " " + itemToBuy);
                    if (Time.sleepUntil(() -> GrandExchange.getFirst(x -> x.getItemName().toLowerCase().equals(itemToBuy.toLowerCase())) != null, 20_000)) {
                        if (itemsIterator.hasNext()) {
                            itemToBuy = itemsIterator.next().getKey();
                        } else {
                            itemsIterator = null;
                        }
                    }
                }
            } else {
                Log.info("Already has: " + items.get(itemToBuy) + " " + itemToBuy);
                if (itemsIterator.hasNext()) {
                    itemToBuy = itemsIterator.next().getKey();
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
        if (Quest.NATURE_SPIRIT.getVarpValue() >= 75) {
            BankWrapper.doBanking(false, false, SupplyMapWrapper.getMortMyreFungusKeepMap());
        } else {
            int totalQuantity = 0;
            for (Map.Entry<String, Integer> entry : SUPPLIES.entrySet()) {
                RSItemDefinition item = Definitions.getItem(entry.getKey(), RSItemDefinition::isTradable);
                if (item.isStackable()) {
                    totalQuantity += 1;
                } else {
                    totalQuantity += entry.getValue();
                }
            }

            boolean withdrawNoted = totalQuantity > Inventory.getFreeSlots();
            BankWrapper.doBanking(false, withdrawNoted, SUPPLIES);
        }

        Bank.close();
        Time.sleepUntilForDuration(() -> !Bank.isOpen() && !GrandExchange.isOpen(), Random.nextInt(400, 600), 5000);

        if (Inventory.contains("Silver sickle (b)")) {
            Inventory.getFirst("Silver sickle (b)").interact(a -> true);
            Time.sleepUntil(() -> Equipment.contains("Silver sickle (b)"), 5000);
        }
        BankWrapper.setHasCheckedBank(false);
        GEWrapper.setBuySupplies(false, false, SUPPLIES);
    }

    private boolean stillNeedsItem(String itemToBuy) {
        return getQuantityToBuy(itemToBuy, true) > 0 && !Equipment.contains(itemToBuy);
    }

    private int getQuantityToBuy(String item, boolean checkEnough) {
        int quantity = items.get(item) - Inventory.getCount(true, item);

        if (quantity > 0) {
            if (checkEnough) {
                int price = getPrice(item);

                if ((price * quantity) > coinsToSpend) {
                    Log.severe("Not enough gp for" + quantity + " " + item + ": Buying AMAP");
                    return coinsToSpend / price;
                }
            }
            return quantity;
        }
        return 0;
    }

    private int getPrice(String item) {
        int q = getQuantityToBuy(item, false);
        if (q > 0) {
            return coinsToSpend / q;
        }
        return coinsToSpend;
    }
}
