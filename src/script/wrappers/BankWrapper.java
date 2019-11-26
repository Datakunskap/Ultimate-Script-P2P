package script.wrappers;

import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.ui.Log;
import script.quests.nature_spirit.data.Quest;

import java.util.*;

public class BankWrapper {

    private static int bankValue = -1;
    private static int inventoryValue = -1;
    private static int startingValue;
    private static boolean isMuleing;
    private static int amountMuled;
    private static boolean hasCheckedBank;

    public static int getTotalValue() {
        return getBankValue() + getInventoryValue();
    }

    public static int getBankValue() {
        return Math.max(bankValue, 0);
    }

    public static int getInventoryValue() {
        return Math.max(inventoryValue, 0);
    }

    public static int getTotalValueGained() {
        return (getTotalValue() - (startingValue > 0 ? startingValue : getTotalValue()) + getAmountMuled());
    }

    public static void updateBankValue() {
        int newValue = PriceCheckService.getBankValue();

        if (bankValue == -1) {
            startingValue += newValue;
        }

        bankValue = newValue;
    }

    public static void updateInventoryValue() {
        int newValue = PriceCheckService.getInventoryValue();

        if (inventoryValue == -1) {
            startingValue += newValue;
        }

        inventoryValue = newValue;
    }

    private static void doBanking(boolean keepAllCoins, int numCoinsToKeep, boolean withdrawNoted,
                                  Set<String> set, LinkedHashMap<String, Integer> map, String... itemsToKeep) {
        if (BankLocation.getNearest().getPosition().distance() > 5) {
            if (GEWrapper.isBuySupplies() || GEWrapper.isSellItems()) {
                Log.info("Walking To GE Bank");
                WalkingWrapper.walkToPosition(BankLocation.GRAND_EXCHANGE.getPosition());
            } else {
                Log.info("Walking To Nearest Bank");
                Movement.getDaxWalker().walkToBank();
            }
        }

        for (int tries = 10; !openNearest() && Game.isLoggedIn() && tries > 0; tries--) {
            Log.info("Opening Bank");
            Time.sleep(SleepWrapper.mediumSleep1000());
            tries--;
        }

        Bank.depositInventory();
        Time.sleepUntilForDuration(Inventory::isEmpty, Random.nextInt(500, 800), 10_000);

        if (numCoinsToKeep > 0) {
            Bank.withdraw("Coins", numCoinsToKeep);
            Time.sleepUntilForDuration(()
                    -> Inventory.contains(995) && Inventory.getCount(true, 995) >= numCoinsToKeep, Random.nextInt(500, 800), 10_000);
        }
        if (keepAllCoins) {
            Bank.withdrawAll("Coins");
            Time.sleepUntilForDuration(() -> Inventory.contains("Coins"), Random.nextInt(1000, 1200), 10_000);
        }

        if (withdrawNoted) {
            if (Bank.getWithdrawMode() != Bank.WithdrawMode.NOTE) {
                Bank.setWithdrawMode(Bank.WithdrawMode.NOTE);
                Time.sleepUntilForDuration(() -> Bank.getWithdrawMode() == Bank.WithdrawMode.NOTE, Random.nextInt(500, 800), 10_000);
            }
        } else {
            if (Bank.getWithdrawMode() != Bank.WithdrawMode.ITEM) {
                Bank.setWithdrawMode(Bank.WithdrawMode.ITEM);
                Time.sleepUntilForDuration(() -> Bank.getWithdrawMode() == Bank.WithdrawMode.ITEM, Random.nextInt(500, 800), 10_000);
            }
        }
        Time.sleep(300, 600);

        if (itemsToKeep != null && itemsToKeep.length > 0) {
            for (String i : itemsToKeep) {
                if (Bank.contains(x -> x.getName().equalsIgnoreCase(i))) {
                    Log.info("Withdrawing: " + i);
                    if (withdrawNoted) {
                        Bank.withdrawAll(x -> x.getName().equalsIgnoreCase(i));
                    } else {
                        Bank.withdraw(x -> x.getName().equalsIgnoreCase(i), 1);
                    }
                    Time.sleepUntilForDuration(() -> Inventory.contains(x -> x.getName().equalsIgnoreCase(i)), Random.nextInt(500, 800), 10_000);
                } else if (Bank.contains(i.substring(0, i.length() - 3))) {
                    Bank.withdraw(x -> x.getName().contains(i.substring(0, i.length() - 3)), 1);
                    Time.sleepUntilForDuration(() -> Inventory.contains(x -> x.getName().contains(i.substring(0, i.length() - 3))),
                            Random.nextInt(500, 800), 10_000);
                }
                Time.sleep(200, 400);
            }
        }

        if (set != null && set.size() > 0) {
            for (String i : set) {
                if (Bank.contains(x -> x.getName().equalsIgnoreCase(i))) {
                    Log.info("Withdrawing: " + i);
                    if (withdrawNoted) {
                        Bank.withdrawAll(x -> x.getName().equalsIgnoreCase(i));
                    } else {
                        Bank.withdraw(x -> x.getName().equalsIgnoreCase(i), 1);
                    }
                    Time.sleepUntilForDuration(() -> Inventory.contains(x -> x.getName().equalsIgnoreCase(i)), Random.nextInt(500, 800), 10_000);
                } else if (Bank.contains(i.substring(0, i.length() - 3))) {
                    Bank.withdraw(x -> x.getName().contains(i.substring(0, i.length() - 3)), 1);
                    Time.sleepUntilForDuration(() -> Inventory.contains(x -> x.getName().contains(i.substring(0, i.length() - 3))),
                            Random.nextInt(500, 800), 10_000);
                }
                Time.sleep(200, 400);
            }
        }

        if (map != null && map.size() > 0) {
            for (Map.Entry<String, Integer> entry : map.entrySet()) {
                String item = entry.getKey();
                int amount = entry.getValue();

                if (Bank.contains(item)) {
                    Log.info("Withdrawing: " + item);
                    Bank.withdraw(i -> i.getName().equalsIgnoreCase(item), amount);
                    Time.sleepUntilForDuration(() -> Inventory.contains(x -> x.getName().equalsIgnoreCase(item))
                                    && (Inventory.getCount(true, x -> x.getName().equalsIgnoreCase(item)) >= amount),
                            Random.nextInt(500, 800), 10_000);
                } else if (Bank.contains(item.substring(0, item.length() - 3))) {
                    Bank.withdraw(i -> i.getName().contains(item.substring(0, item.length() - 3)), amount);
                    Time.sleepUntilForDuration(() -> Inventory.contains(i -> i.getName().contains(item.substring(0, item.length() - 3))),
                            Random.nextInt(500, 800), 10_000);
                }
                Time.sleep(200, 400);
            }
        }

        if (Quest.NATURE_SPIRIT.getVarpValue() >= 75) {
            updateBankValue();
            updateInventoryValue();
        }
    }

    public static void doBanking(boolean keepAllCoins, Set<String> itemsToKeepSet) {
        doBanking(keepAllCoins, 0, true, itemsToKeepSet, null);
    }

    public static void doBanking(boolean keepAllCoins, LinkedHashMap<String, Integer> map) {
        doBanking(keepAllCoins, 0, true, null, map);
    }

    public static void doBanking(boolean keepAllCoins, boolean withdrawNoted, String... itemsToKeep) {
        doBanking(keepAllCoins, 0, withdrawNoted, null, null, itemsToKeep);
    }

    public static void doBanking(boolean keepAllCoins, boolean withdrawNoted, Set<String> set) {
        doBanking(keepAllCoins, 0, withdrawNoted, set, null);
    }

    public static void doBanking(boolean keepAllCoins, boolean withdrawNoted, LinkedHashMap<String, Integer> map) {
        doBanking(keepAllCoins, 0, withdrawNoted, null, map);
    }

    public static void doBanking(boolean keepAllCoins) {
        doBanking(keepAllCoins, 0, true, null, null);
    }


    public static boolean openNearest() {
        if (Bank.isOpen()) {
            return true;
        }
        return Bank.open();
    }

    public static void withdrawSellableItems(Set<String> itemsToKeep) {
        if (!Bank.isOpen() || Bank.getItems().length < 1) {
            Bank.open();
            Time.sleepUntil(() -> Bank.getItems().length > 0, 8000);
        }
        if (!Bank.getWithdrawMode().equals(Bank.WithdrawMode.NOTE)) {
            Bank.setWithdrawMode(Bank.WithdrawMode.NOTE);
            Time.sleepUntilForDuration(() -> Bank.getWithdrawMode() == Bank.WithdrawMode.NOTE, Random.nextInt(500, 800), 12_500);
        }

        Item[] sellables = Bank.getItems(s
                -> (s.getName().equalsIgnoreCase("Mort myre fungus")
                || s.getName().contains("Dragon bones"))
                || (s.isExchangeable() && !itemsToKeep.contains(s.getName())
                && PriceCheckService.getPrice(s.getId()) != null
                && (PriceCheckService.getPrice(s.getId()).getSellAverage() * s.getStackSize() > 5000)));

        for (Item s : sellables) {
            Bank.withdrawAll(i -> i.getName().equalsIgnoreCase(s.getName()));
            Time.sleepUntilForDuration(() -> Inventory.contains(s.getName()), Random.mid(500, 800), 10_000);
            Time.sleep(200, 400);
        }

        if (itemsToKeep.contains("Silver sickle") && Inventory.getCount(true, "Coins") < 10_000 && Bank.getCount("Coins") < 10_000) {
            Bank.withdrawAll("Salve graveyard teleport");
            Time.sleepUntilForDuration(() -> Inventory.contains("Salve graveyard teleport"), Random.mid(500, 800), 10_000);
        }

        updateBankValue();
        updateInventoryValue();
    }

    public static HashSet<String> getItemsNeeded(LinkedHashMap<String, Integer> itemsToBuy) {
        HashSet<String> set = new HashSet<>();
        if (itemsToBuy != null && itemsToBuy.size() > 0) {

            set = new HashSet<>(itemsToBuy.keySet());

            for (Map.Entry<String, Integer> i : itemsToBuy.entrySet()) {
                if (Bank.contains(x
                        -> x.getName().toLowerCase().equals(i.getKey().toLowerCase())
                        && x.getStackSize() >= i.getValue())) {

                    set.remove(i.getKey());
                }
            }
        }
        return set;
    }

    public static void setMuleing(boolean isMuleing) {
        BankWrapper.isMuleing = isMuleing;
    }

    public static boolean isMuleing() {
        return isMuleing;
    }

    public static int getAmountMuled() {
        return amountMuled;
    }

    public static void setAmountMuled(int amountMuled) {
        BankWrapper.amountMuled = amountMuled;
    }

    public static boolean hasCheckedBank() {
        return hasCheckedBank;
    }

    public static void setHasCheckedBank(boolean hasCheckedBank) {
        BankWrapper.hasCheckedBank = hasCheckedBank;
    }

    private static boolean hasBanTimeMuled;

    public static void setHasBanTimeMuled(boolean muled) {
        hasBanTimeMuled = muled;
    }

    public static boolean hasBanTimeMuled() {
        return hasBanTimeMuled;
    }
}
