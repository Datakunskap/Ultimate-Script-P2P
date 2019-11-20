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
        map.put("Silver sickle (b)", 1);
        map.put("Varrock teleport", 1);
        map.put("Salve graveyard teleport", 80);
        return map;
    }

    public static HashMap<String, Integer> getMortMyreFungusKeepMap() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("Ring of dueling(8)", 1);
        map.put("Salve graveyard teleport", 1000);
        map.put("Silver sickle (b)", 1);
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
        map.put("Monkfish", 10);
        map.put("Leather gloves", 1);
        return map;
    }

    public static HashMap<String, Integer> getWaterfallItemsMap() {
        //TODO: Add items
        HashMap<String, Integer> map = new HashMap<>();
        return map;
    }

    public static HashMap<String, Integer> getPriestInPerilItemsMap() {
        //TODO: Add items
        HashMap<String, Integer> map = new HashMap<>();
        return map;
    }

    public static HashMap<String, Integer> getNatureSpiritKeepMap() {
        HashMap<String, Integer> map = new HashMap<>();
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

    public static HashMap<String, Integer> getNatureSpiritItemsMap() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("Silver sickle", 1);
        map.put("Jug of wine", 10);
        map.put("Monkfish", 5);
        map.put("Salve graveyard teleport", 3);
        return map;
    }

    public static HashMap<String, Integer> getPrayerItemsMap() {
        HashMap<String, Integer> map = new HashMap<>();
        map.put("Dragon Bones", 300);
        map.put("Amulet of glory(6)", 1);
        map.put("Burning amulet(5)", 1);
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
        map.put("Salve graveyard teleport", 3);
        return map;
    }
}
