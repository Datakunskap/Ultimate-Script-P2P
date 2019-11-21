package script.quests.witches_house.tasks;

import api.API;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

import static api.API.withdrawItem;
import static script.quests.the_restless_ghost.data.Quest.THE_RESTLESS_GHOST;
import static script.quests.witches_house.data.Quest.WITCHES_HOUSE;

public class WitchesHouse_Preparation extends Task {

    boolean hasGear;
    boolean hasItems;

    public static boolean readyToStartWitchesHouse;

    private static final String COINS = "Coins";
    private static final String GLORY = "Amulet of glory(";
    private static final String STAFF_OF_AIR = "Staff of air";
    private static final String RING_OF_WEALTH = "Ring of wealth (5)";
    private static final String CHEESE = "Cheese";
    private static final String MIND_RUNE = "Mind rune";
    private static final String FIRE_RUNE = "Fire rune";
    private static final String FALADOR_TELEPORT = "Falador teleport";
    private static final String LEATHER_GLOVES = "Leather gloves";
    private static final String Monkfish = "Monkfish";
    private static final String[] ITEMS_NEEDED = {STAFF_OF_AIR, RING_OF_WEALTH, CHEESE, MIND_RUNE, FIRE_RUNE, FALADOR_TELEPORT, LEATHER_GLOVES, Monkfish};

    @Override
    public boolean validate() {
        return THE_RESTLESS_GHOST.getVarpValue() == 5
                && WITCHES_HOUSE.getVarpValue() == 0
                && Skills.getLevel(Skill.MAGIC) >= 13
                && Skills.getLevel(Skill.PRAYER) < 50
                && !readyToStartWitchesHouse;
    }

    @Override
    public int execute() {

        Log.info("WitchHouse_Preparation");

        API.doDialog();

        API.toggleRun();

        if (!hasGear) {
            if (!Equipment.contains(x -> x.getName().contains(GLORY))) {
                if (!Inventory.contains(x -> x.getName().contains(GLORY))) {
                    Log.info("I don't have a glory");
                    API.withdrawItem(false, GLORY, 1);
                    Log.info("Withdrew the glory");
                }
                if (Inventory.contains(x -> x.getName().contains(GLORY))) {
                    Log.info("Got glory in invent");
                    API.wearItem(GLORY);
                    Log.info("Wielded the glory");
                }
            }
            if (!Equipment.contains(x -> x.getName().contains(STAFF_OF_AIR))) {
                if (!Inventory.contains(STAFF_OF_AIR)) {
                    API.withdrawItem(false, STAFF_OF_AIR, 1);
                }
                if (Inventory.contains(STAFF_OF_AIR)) {
                    API.wearItem(STAFF_OF_AIR);
                }
            }
            if (Equipment.contains(x -> x.getName().contains(STAFF_OF_AIR))
                    && Equipment.contains(x -> x.getName().contains(STAFF_OF_AIR))) {
                Log.info("Setting hasGear to true");
            }
            if (Equipment.contains(x -> x.getName().contains(GLORY)) && Equipment.contains(x -> x.getName().contains(STAFF_OF_AIR))) {
                hasGear = true;
            }
        }

        if (hasGear) {
            if (!Bank.isOpen()) {
                Bank.open();
            }
            if (Bank.isOpen()) {
                if (Inventory.containsAnyExcept(ITEMS_NEEDED)) {
                    if (Bank.depositInventory()) {
                        Time.sleepUntil(() -> Inventory.isEmpty(), 5000);
                    }
                }
                if (!Inventory.contains(COINS)) {
                    withdrawItem(false,CHEESE, 2);
                    withdrawItem(true,MIND_RUNE, 100);
                    withdrawItem(true,FIRE_RUNE, 300);
                    withdrawItem(true,FALADOR_TELEPORT, 5);
                    withdrawItem(false,Monkfish, 15);
                    withdrawItem(false,LEATHER_GLOVES, 1);
                    Log.info("Setting hasItems to true");
                    hasItems = true;
                }
            }
        }

        if (!readyToStartWitchesHouse && hasGear && hasItems) {
            if (API.isWearingItem(GLORY)
                    && API.isWearingItem(STAFF_OF_AIR)
                    && API.inventoryHasItem(false, CHEESE, 2)
                    && API.inventoryHasItem(true, MIND_RUNE, 100)
                    && API.inventoryHasItem(true, FIRE_RUNE, 300)
                    && API.inventoryHasItem(true, FALADOR_TELEPORT, 5)
                    && API.inventoryHasItem(false, Monkfish, 15)
                    && API.inventoryHasItem(false, LEATHER_GLOVES, 1)) {
                Log.info("Setting readyToStartWitchesHouse to true");
                readyToStartWitchesHouse = true;
            }
        }

        return API.lowRandom();

    }

}
