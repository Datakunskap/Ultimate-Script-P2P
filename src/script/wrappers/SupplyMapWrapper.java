package script.wrappers;

import java.util.HashMap;

public class SupplyMapWrapper {
    private static HashMap<String, Integer> supplyMap;

    public static HashMap<String, Integer> getCurrentSupplyMap() {
        return supplyMap;
    }
    
    public static void setSupplyMap(HashMap<String, Integer> supplyMap) {
        SupplyMapWrapper.supplyMap = supplyMap;
    } 

    public static HashMap<String, Integer> getRestlessGhostItemsMap() {
        supplyMap = new HashMap<>();
        return new HashMap<>();
    }

    public static HashMap<String, Integer> getMortMyreFungusItemsMap() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("Ring of dueling(8)", 11);
        map.put("Salve graveyard teleport", 80);
        map.put("Silver sickle (b)", 1);
        supplyMap = new HashMap<>(map);
        return map;
    }

    public static HashMap<String, Integer> getWitchesHouseItemsMap() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("Amulet of glory(6)", 1);
        map.put("Staff of air", 1);
        map.put("Cheese", 2);
        map.put("Mind rune", 100);
        map.put("Fire rune", 300);
        map.put("Falador teleport", 5);
        map.put("Tuna", 10);
        map.put("Leather gloves", 1);
        supplyMap = new HashMap<>(map);
        return map;
    }

    public static HashMap<String, Integer> getWaterfallItemsMap() {
        //TODO: Add items
        HashMap<String, Integer> map = new HashMap<>();
        supplyMap = new HashMap<>(map);
        return map;
    }

    public static HashMap<String, Integer> getPriestInPerilItemsMap() {
        //TODO: Add items
        HashMap<String, Integer> map = new HashMap<>();
        supplyMap = new HashMap<>(map);
        return map;
    }

    public static HashMap<String, Integer> getNatureSpiritItemsMap() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("Silver sickle", 1);
        map.put("Salve graveyard teleport", 3);
        supplyMap = new HashMap<>(map);
        return map;
    }

    public static HashMap<String, Integer> getStartingItemsMap() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("Lumbridge teleport", 10);
        map.put("Staff of air", 1);
        map.put("Staff of fire", 1);
        map.put("Amulet of glory(6)", 5);
        map.put("Ring of wealth (5)", 2);
        map.put("Air rune", 1000);
        map.put("Mind rune", 1000);
        map.put("Water rune", 200);
        map.put("Fire rune", 300);
        map.put("Earth rune", 200);
        map.put("Tuna", 100);
        map.put("Stamina potion(4)", 10);
        map.put("Cheese", 2);
        map.put("Leather gloves", 1);
        map.put("Falador teleport", 5);
        map.put("Games necklace(8)", 1);
        map.put("Rope", 2);
        map.put("Adamant scimitar", 1);
        map.put("Ring of recoil", 1);
        map.put("Bucket", 1);
        map.put("Rune essence", 50);
        map.put("Varrock teleport", 5);
        map.put("Silver sickle", 1);
        map.put("Dragon bones", 300);
        map.put("Burning amulet(5)", 5);
        map.put("Salve graveyard teleport", 3);
        supplyMap = new HashMap<>(map);
        return map;
    }
}
