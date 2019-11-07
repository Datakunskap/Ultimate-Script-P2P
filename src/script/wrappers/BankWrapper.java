package script.wrappers;

import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.StopWatch;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.Movement;

import java.time.Duration;

public class BankWrapper {

    private static int bankValue = -1;
    private static int inventoryValue = -1;
    private static int startingValue;
    private static boolean tradeRestricted = true;
    private static StopWatch startValueTimer;

    public static int getTotalValue() {
        return bankValue + inventoryValue;
    }

    public static int getBankValue() {
        return bankValue;
    }

    public static int getInventoryValue() {
        return inventoryValue;
    }

    public static int getTotalValueGained() {
        return (getTotalValue() - (startingValue > 0 ? startingValue : getTotalValue()));
    }

    public static boolean isTradeRestricted() {
        return tradeRestricted;
    }


    public static void updateBankValue() {
        boolean includeTradeRestricted = !isTradeRestricted();
        int newValue = PriceCheckService.getBankValue(includeTradeRestricted);

        if (bankValue == -1) {
            startingValue += newValue;
        }

        bankValue = newValue;
    }

    public static void updateInventoryValue() {
        boolean includeTradeRestricted = !isTradeRestricted();
        int newValue = PriceCheckService.getInventoryValue(includeTradeRestricted);

        if (inventoryValue == -1) {
            startValueTimer = StopWatch.start();
        }
        if (startValueTimer != null && startValueTimer.exceeds(Duration.ofSeconds(10))) {
            startingValue += newValue;
            startValueTimer = null;
        }

        inventoryValue = newValue;
    }

    private static void openAndDepositAll(boolean keepAllCoins, int numCoinsToKeep, String... itemsToKeep) {
        //Log.fine("Depositing Inventory");
        while (!openNearest() && Game.isLoggedIn()) {
            Time.sleep(1000);
        }

        Bank.depositInventory();
        Time.sleepUntil(Inventory::isEmpty, 2000, 8000);
        Time.sleep(300, 600);
        inventoryValue = 0;
        updateBankValue();


        if (numCoinsToKeep > 0) {
            Bank.withdraw(995, numCoinsToKeep);
            Time.sleepUntil(()
                            -> Inventory.contains(995) && Inventory.getCount(true, 995) >= numCoinsToKeep,
                    1000, 5000);
        }
        if (keepAllCoins) {
            Bank.withdrawAll(995);
            Time.sleepUntil(() -> Inventory.contains(995), 1000, 5000);
        }

        if (itemsToKeep != null && itemsToKeep.length > 0) {
            for (String i : itemsToKeep) {
                Bank.withdrawAll(x -> x.getName().equalsIgnoreCase(i));
                Time.sleepUntil(() -> Inventory.contains(x -> x.getName().equalsIgnoreCase(i)),
                        2000, 8000);
            }
        }

        updateBankValue();
    }

    public static void openAndDepositAll(boolean keepAllCoins, String... itemsToKeep) {
        openAndDepositAll(keepAllCoins, 0, itemsToKeep);
    }

    public static void openAndDepositAll(int numCoinsToKeep, String... itemsToKeep) {
        openAndDepositAll(false, numCoinsToKeep, itemsToKeep);
    }

    public static void openAndDepositAll(boolean keepAllCoins) {
        openAndDepositAll(keepAllCoins, 0);
    }

    public static void openAndDepositAll(int numCoinsToKeep) {
        openAndDepositAll(false, numCoinsToKeep);
    }

    public static void openAndDepositAll(String... itemsToKeep) {
        openAndDepositAll(false, 0, itemsToKeep);
    }

    public static boolean openNearest() {
        if (Bank.isOpen()) {
            return true;
        }
        return Movement.walkTo(BankLocation.getNearest().getPosition());
    }
}
