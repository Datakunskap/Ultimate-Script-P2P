package script.wrappers;

import com.acuitybotting.common.utils.ExecutorUtil;
import com.allatori.annotations.DoNotRename;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.annotations.SerializedName;
import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.ui.Log;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.ScheduledThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class PriceCheckService {

    private static final String OSBUDDY_EXCHANGE_SUMMARY_URL = "https://storage.googleapis.com/osb-exchange/summary.json";
    private static final String RSBUDDY_EXCHANGE_SUMMARY_URL = "https://rsbuddy.com/exchange/summary.json";
    private static Gson g = new Gson();
    private static Map<String, Integer> itemNameMapping = new HashMap<>();
    private static Map<Integer, ItemPrice> prices = new HashMap<>();
    private static int reloadMinutes = 30;
    private static boolean isReloadEnabled = true;
    private static HashSet<String> failedItemPriceNames = new HashSet<>();

    private static ScheduledThreadPoolExecutor executor = ExecutorUtil.newScheduledExecutorPool(1, Throwable::printStackTrace);
    private static ScheduledFuture<?> task;

    public static int getInventoryValue() {
        int total = Inventory.getCount(true, "Coins");
        Item[] invItems;

        invItems = Inventory.getItems();

        return getItemPrices(total, invItems);
    }

    public static int getBankValue() {
        int total = Bank.getCount("Coins");
        Item[] bankItems;

        bankItems = Bank.getItems();

        return getItemPrices(total, bankItems);
    }

    private static int getItemPrices(int total, Item[] items) {
        for (Item item : items) {
            if (item.isExchangeable() && !item.isNoted() && item.getId() != 995) {
                int itemValue = 0;
                try {
                    itemValue = PriceCheckService.getPrice(item.getId()).getSellAverage() * item.getStackSize();
                } catch (Exception ignored) { }

                if (itemValue <= 0 && prices.get(item.getId()) != null) {
                    itemValue = prices.get(item.getId()).getSellAverage();
                }
                /*if (itemValue <= 0 && !failedItemPriceNames.contains(item.getName())) {
                    reload(OSBUDDY_EXCHANGE_SUMMARY_URL);
                    try {
                        itemValue = PriceCheckService.getPrice(item.getId()).getSellAverage() * item.getStackSize();
                    } catch (Exception e) {
                        Log.severe("Failed Getting Item Price: " + item.getName());
                        failedItemPriceNames.add(item.getName());
                        reload();
                    }
                }*/

                total += itemValue;
            }
        }
        return total;
    }

    public static void purgeFailedPriceCache() {
        failedItemPriceNames.clear();
    }

    public static ItemPrice getPrice(String name) {
        if (prices.size() == 0) {
            reload();
        }
        int id = itemNameMapping.getOrDefault(name.toLowerCase(), -1);
        return id == -1 ? null : getPrice(id);
    }

    public static ItemPrice getPrice(int id) {
        if (prices.size() == 0) {
            reload();
        }
        return prices.getOrDefault(id, null);
    }

    public static void reload() {
        reload(RSBUDDY_EXCHANGE_SUMMARY_URL);
    }

    public static void reload(String url) {
        if (!isReloadEnabled && prices.size() > 0) {
            return;
        }
        if (task == null && isReloadEnabled) {
            task = executor.scheduleAtFixedRate(PriceCheckService::reload, reloadMinutes, reloadMinutes, TimeUnit.MINUTES);
        }
        try {
            HttpResponse<String> node = Unirest.get(url).asString();
            if (node.getStatus() != 200) {
                System.out.println(node.getBody());
                Log.severe("PriceCheck", "Failed to load prices. Result: " + node.getBody());
                return;
            }
            JsonObject o = g.fromJson(node.getBody(), JsonObject.class);
            for (String s : o.keySet()) {
                ItemPrice price = g.fromJson(o.get(s).getAsJsonObject(), ItemPrice.class);
                int id = Integer.parseInt(s);
                String name = price.name.toLowerCase();
                itemNameMapping.remove(name);
                itemNameMapping.put(name, id);
                prices.remove(id);
                prices.put(id, price);
            }
        } catch (UnirestException e) {
            e.printStackTrace();
            Log.severe(e);
        }
    }

    public static void dispose() {
        if (task != null) {
            task.cancel(true);
        }
        executor.shutdown();
        executor.shutdownNow();
    }

    public static void setShouldReload(boolean value) {
        isReloadEnabled = value;
    }

    @DoNotRename
    public class ItemPrice {

        @DoNotRename
        private String name;
        @DoNotRename
        private boolean members;
        @DoNotRename
        @SerializedName("buy_average")
        private int buyAverage;
        @DoNotRename
        @SerializedName("sell_average")
        private int sellAverage;
        @DoNotRename
        @SerializedName("overall_average")
        private int overallAverage;

        public String getName() {
            return name;
        }

        public boolean isMembers() {
            return members;
        }

        public int getBuyAverage() {
            return buyAverage;
        }

        public int getSellAverage() {
            return sellAverage;
        }

        public int getOverallAverage() {
            return overallAverage;
        }
    }
}
