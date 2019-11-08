package script.wrappers;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.Definitions;
import org.rspeer.runetek.api.Game;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.GrandExchange;
import org.rspeer.runetek.api.component.GrandExchangeSetup;
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.providers.RSGrandExchangeOffer;
import org.rspeer.runetek.providers.RSItemDefinition;

import java.util.Arrays;
import java.util.HashMap;

public class GEWrapper {

    public static Area GE_AREA_LARGE = Area.polygonal(
            new Position(3148, 3490, 0),
            new Position(3152, 3501, 0),
            new Position(3165, 3506, 0),
            new Position(3176, 3502, 0),
            new Position(3181, 3489, 0),
            new Position(3177, 3477, 0),
            new Position(3164, 3473, 0),
            new Position(3152, 3477, 0));

    private static final int SELL_ALL = 0;
    private static final int TIMEOUT = 6000;
    private static final String EXCHANGE_ACTION = "Exchange";
    private static final String GE_NPC_NAME = "Grand Exchange Clerk";
    private static boolean buySupplies;
    private static boolean sellItems;

    public static void setSellItems(boolean sellItems) {
        GEWrapper.sellItems = sellItems;
    }

    public static boolean isSellItems() {
        return sellItems;
    }

    public static void setBuySupplies(boolean buySupplies, HashMap<String, Integer> supplyMap) {
        GEWrapper.buySupplies = buySupplies;
        SupplyMapWrapper.setSupplyMap(supplyMap);
    }

    public static void setBuySupplies(boolean buySupplies) {
        GEWrapper.buySupplies = buySupplies;
    }

    public static boolean isBuySupplies() {
        return buySupplies;
    }

    public static boolean hasSupplies(HashMap<String, Integer> map) {
        return hasSupplies(map.keySet().toArray(new String[0]));
    }

    public static boolean hasSupplies(String[] supplies) {
        if (!Game.isLoggedIn() || Players.getLocal() == null)
            return true;

        if (!Inventory.containsAll(supplies)) {
            for (String item : supplies) {
                if (!Equipment.contains(item) && !Inventory.contains(item)) {
                    return false;
                }
            }
        }
        return true;
    }

    private static boolean exchange(RSGrandExchangeOffer.Type type, RSItemDefinition item, int quantity, int price, boolean toBank) {
        return exchange(type, quantity, price, toBank, item);
    }

    private static boolean exchange(RSGrandExchangeOffer.Type type, int quantity, int price, boolean toBank, RSItemDefinition item) {
        if (!GrandExchange.isOpen()) {
            //GrandExchange.open();

            // placeholder
            Npc clerk = Npcs.getNearest(GE_NPC_NAME);
            return clerk != null ? clerk.interact(EXCHANGE_ACTION) : Movement.walkToRandomized(BankLocation.GRAND_EXCHANGE.getPosition());
        }

        return collectFinishedOffers(toBank)
                && createOffer(type)
                && setItem(item, 10)
                && setItemPrice(price)
                && setItemQuantity(quantity)
                && GrandExchangeSetup.confirm();

    }

    private static boolean setItem(RSItemDefinition item, int tries) {
        if (item.getId() < 0 && tries >= 0)
            setItem(item, tries-1);
        if (tries < 0)
            return false;
        return GrandExchangeSetup.setItem(item.getId());
    }

    private static boolean collectFinishedOffers(boolean toBank) {
        if (hasNotAnyFinishedOffers()) return true;
        GrandExchange.collectAll(toBank);
        return Time.sleepUntil(GEWrapper::hasNotAnyFinishedOffers, TIMEOUT);
    }

    private static boolean createOffer(RSGrandExchangeOffer.Type offerType) {
        if (GrandExchangeSetup.isOpen()) return true;
        return GrandExchange.createOffer(offerType)
                && Time.sleepUntil(GrandExchangeSetup::isOpen, TIMEOUT);
    }

    private static boolean hasNotAnyFinishedOffers() {
        return Arrays.stream(GrandExchange.getOffers())
                .noneMatch(it -> it.getProgress() == RSGrandExchangeOffer.Progress.FINISHED);
    }

    private static boolean isItemPriceSettled(int desired) {
        return GrandExchangeSetup.getPricePerItem() == desired;
    }

    private static boolean isItemQuantitySettled(int desired) {
        return GrandExchangeSetup.getQuantity() == desired;
    }

    private static boolean setItemPrice(int price) {
        return GrandExchangeSetup.setPrice(price)
                && Time.sleepUntil(() -> isItemPriceSettled(price), TIMEOUT);
    }

    private static boolean setItemQuantity(int quantity) {
        if (quantity == SELL_ALL) return true;

        return GrandExchangeSetup.setQuantity(quantity)
                && Time.sleepUntil(() -> isItemQuantitySettled(quantity), TIMEOUT);
    }

    public static boolean buy(int id, int quantity, int price, boolean toBank) {
        return exchange(RSGrandExchangeOffer.Type.BUY, Definitions.getItem(id), quantity, price, toBank);
    }

    public static boolean buy(String name, int quantity, int price, boolean toBank) {
        return exchange(RSGrandExchangeOffer.Type.BUY, Definitions.getItem(name, x -> x.isTradable() || x.isNoted()), quantity, price, toBank);
    }

    public static boolean sell(int id, int quantity, int price, boolean toBank) {
        return exchange(RSGrandExchangeOffer.Type.SELL, Definitions.getItem(id), quantity, price, toBank);
    }

    public static boolean sell(String name, int quantity, int price, boolean toBank) {
        return exchange(RSGrandExchangeOffer.Type.SELL, Definitions.getItem(name, x -> x.isTradable() || x.isNoted()), quantity, price, toBank);
    }

    public static void openGE() {
        Npc n = Npcs.getNearest(x -> x != null && x.getName().contains("Grand Exchange Clerk"));
        if (n != null) {
            Time.sleepUntil(() -> n.interact("Exchange"), 1000, 10000);
            Time.sleep(700, 1300);
        }
    }

    public static void closeGE() {
        if (GrandExchange.isOpen()) {
            Movement.walkToRandomized(Players.getLocal().getPosition().randomize(4));
            Time.sleepUntil(() -> !GrandExchange.isOpen(), 2000, 6000);
        }
    }

    public static boolean itemsStillActive(RSGrandExchangeOffer.Type offerType) {
        return (GrandExchange.isOpen() || GrandExchangeSetup.isOpen()) &&
                (GrandExchange.getOffers(offerType).length > 0 || GrandExchange.getFirstActive() != null);
    }

}
