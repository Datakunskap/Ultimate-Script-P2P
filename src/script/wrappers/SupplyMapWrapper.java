package script.wrappers;

import java.util.LinkedHashMap;

public class SupplyMapWrapper {
    private static LinkedHashMap<String, Integer> supplyMap;

    public static LinkedHashMap<String, Integer> getCurrentSupplyMap() {
        return supplyMap;
    }
    
    public static void setSupplyMap(LinkedHashMap<String, Integer> supplyMap) {
        SupplyMapWrapper.supplyMap = supplyMap;
    } 

    public static LinkedHashMap<String, Integer> getRestlessGhostItemsMap() {
        supplyMap = new LinkedHashMap<>();
        return new LinkedHashMap<>();
    }

    public static LinkedHashMap<String, Integer> getMortMyreFungusItemsMap() {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        map.put("Ring of dueling(8)", 11);
        map.put("Silver sickle (b)", 1);
        map.put("Varrock teleport", 1);
        map.put("Salve graveyard teleport", 80);
        return map;
    }

    public static LinkedHashMap<String, Integer> getMortMyreFungusKeepMap() {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        map.put("Ring of dueling(8)", 1);
        map.put("Salve graveyard teleport", 1000);
        map.put("Silver sickle (b)", 1);
        return map;
    }

    public static LinkedHashMap<String, Integer> getWitchesHouseItemsMap() {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        map.put("Amulet of glory(6)", 1);
        map.put("Staff of air", 1);
        map.put("Cheese", 2);
        map.put("Mind rune", 100);
        map.put("Fire rune", 300);
        map.put("Falador teleport", 5);
        map.put("Monkfish", 10);
        map.put("Leather gloves", 1);
        return map;
    }

    public static LinkedHashMap<String, Integer> getWaterfallItemsMap() {
        //TODO: Add items
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        return map;
    }

    public static LinkedHashMap<String, Integer> getPriestInPerilItemsMap() {
        //TODO: Add items
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        return map;
    }

    public static LinkedHashMap<String, Integer> getNatureSpiritKeepMap() {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        map.put("Silver sickle", 1);
        map.put("Salve graveyard teleport", 3);
        map.put("Jug of wine", 10);
        map.put("Monkfish", 5);
        map.put("Mort myre fungus", 1);
        map.put("Druidic spell", 1);
        map.put("A used spell", 1);
        map.put("Ghostspeak amulet", 1);
        return map;
    }

    public static LinkedHashMap<String, Integer> getNatureSpiritItemsMap() {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        map.put("Silver sickle", 1);
        map.put("Jug of wine", 10);
        map.put("Monkfish", 5);
        map.put("Salve graveyard teleport", 3);
        return map;
    }

    public static LinkedHashMap<String, Integer> getPrayerItemsMap() {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
        map.put("Dragon Bones", 300);
        map.put("Amulet of glory(6)", 1);
        map.put("Burning amulet(5)", 1);
        return map;
    }

    public static LinkedHashMap<String, Integer> getStartingItemsMap() {
        LinkedHashMap<String, Integer> map = new LinkedHashMap<>();
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
        map.put("Monkfish", 100);
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
        map.put("Jug of wine", 20);
        map.put("Salve graveyard teleport", 4);
        return map;
    }
}
