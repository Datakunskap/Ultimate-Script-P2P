package script.tasks.fungus;

import org.rspeer.runetek.adapter.component.InterfaceComponent;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.GrandExchange;
import org.rspeer.runetek.api.component.Interfaces;
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
import script.wrappers.WalkingWrapper;

import java.util.HashSet;

public class SellGE extends Task {

    private Item[] itemsToSell;
    private InterfaceComponent restrictedMsg = Interfaces.getComponent(465, 25);
    private String status;
    private int gpStart;
    private HashSet<String> itemsToKeep;

    // Items to keep will be in bank after
    public SellGE(HashSet<String> itemsToKeep) {
        this.itemsToKeep = itemsToKeep;
    }

    @Override
    public boolean validate() {
        if (!GEWrapper.isSellItems())
            return false;

        if (itemsToSell == null) {
            return true;
        }

        if (itemsLeftToSell() || GEWrapper.itemsStillActive(RSGrandExchangeOffer.Type.SELL)) {
            return true;
        }

        if (GEWrapper.isSellItems()) {
            GEWrapper.setSellItems(false);
            Interfaces.closeAll();
            Time.sleepUntil(() -> !GrandExchange.isOpen() && !Bank.isOpen(), 2000);
            BankWrapper.updateInventoryValue();
        }
        itemsToSell = null;
        return false;
    }

    @Override
    public int execute() {
        Player me = Players.getLocal();

        if (!GEWrapper.GE_AREA_LARGE.contains(me)) {
            Movement.walkTo(BankLocation.GRAND_EXCHANGE.getPosition(), ()
                    -> WalkingWrapper.shouldBreakOnTarget() || WalkingWrapper.shouldBreakOnRunenergy());
            Movement.toggleRun(true);
            return SleepWrapper.shortSleep600();
        }

        if (itemsToSell == null) {

            BankWrapper.openAndDepositAll(true);
            // Items to keep will be in bank after
            BankWrapper.withdrawSellableItems(itemsToKeep);

            Item[] sellableItems = Inventory.getItems(i -> i.getId() != 995);

            if (sellableItems != null && sellableItems.length > 0) {
                Log.info("Selling");
                itemsToSell = sellableItems;
                gpStart = Inventory.getCount("Coins");
            } else {
                Log.severe("Nothing To Sell");
                Bank.close();
                GEWrapper.setSellItems(false);
                return SleepWrapper.shortSleep600();
            }
        }

        if (!GrandExchange.isOpen()) {
            GEWrapper.openGE();
            Log.fine("Selling " + itemsToSell.length + " Item(s)");
            return 1000;
        }

        if (itemsLeftToSell()) {
            for (int i = 0; i < itemsToSell.length; i++) {
                status = "Selling";
                if (itemsToSell[i] != null && GrandExchange.getOffers(RSGrandExchangeOffer.Type.SELL).length < 3) {
                    if (GEWrapper.sell(itemsToSell[i].getId(), itemsToSell[i].getStackSize(), Random.nextInt(1, 3), false)) {
                        Log.info("Selling: " + itemsToSell[i].getName());
                        final int index = i;
                        if (Time.sleepUntil(() -> GrandExchange.getFirst(x -> x.getItemId() == itemsToSell[index].getId()) != null,8000)) {
                            itemsToSell[i] = null;
                        }
                    } else {
                        itemsToSell = Inventory.getItems(x -> x.getId() != 995 && x.isExchangeable());
                    }
                }
            }
        }

        if (!GrandExchange.getView().equals(GrandExchange.View.OVERVIEW)) {
            GrandExchange.open(GrandExchange.View.OVERVIEW);
        }

        if (GrandExchange.getOffers(RSGrandExchangeOffer.Type.SELL).length > 0) {
            GrandExchange.collectAll();
            Time.sleep(300, 600);
            Keyboard.pressEnter();
        }

        return SleepWrapper.shortSleep600();
    }

    private boolean itemsLeftToSell() {
        if (itemsToSell == null || itemsToSell.length < 1) {
            return false;
        }
        for (Item i : itemsToSell) {
            if (i != null) {
                return true;
            }
        }
        return false;
    }
}
