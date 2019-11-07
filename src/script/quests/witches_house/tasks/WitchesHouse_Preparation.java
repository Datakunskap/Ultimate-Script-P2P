package script.quests.witches_house.tasks;

import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

import java.util.function.Predicate;

import static script.quests.witches_house.data.Quest.THE_RESTLESS_GHOST;
import static script.quests.witches_house.data.Quest.WITCHES_HOUSE;

public class WitchesHouse_Preparation extends Task {

    private boolean hasItems = true;
    private boolean hasGear = false;
    public static boolean readyToStartWitchesHouse = false;

    Predicate<Item> glory = x -> x.getName().contains("Amulet of glory(");

    private static final String COINS = "Coins";
    private static final String GLORY = "Amulet of glory(6)";
    private static final String STAFF_OF_AIR = "Staff of air";
    private static final String RING_OF_WEALTH = "Ring of wealth (5)";
    private static final String CHEESE = "Cheese";
    private static final String MIND_RUNE = "Mind rune";
    private static final String FIRE_RUNE = "Fire rune";
    private static final String FALADOR_TELEPORT = "Falador teleport";
    private static final String LEATHER_GLOVES = "Leather gloves";
    private static final String TUNA = "Tuna";
    private static final String[] ITEMS_NEEDED = {GLORY, STAFF_OF_AIR, RING_OF_WEALTH, CHEESE, MIND_RUNE, FIRE_RUNE, FALADOR_TELEPORT, LEATHER_GLOVES, TUNA};
    private static final String[] GEAR = {GLORY, STAFF_OF_AIR};
    private static final String WIELD = "Wield";
    private static final String WEAR = "Wear";

    private static final Area GE_AREA = Area.rectangular(3157, 3489, 3171, 3477);

    @Override
    public boolean validate() {
        return THE_RESTLESS_GHOST.getVarpValue() == 5
                && WITCHES_HOUSE.getVarpValue() == 0
                && Skills.getLevel(Skill.MAGIC) >= 13
                && !readyToStartWitchesHouse;
    }

    @Override
    public int execute() {

        Player local = Players.getLocal();

        if (Dialog.canContinue()) {
            Log.info("Processing dialog");
            Dialog.processContinue();
        }

        if (!Movement.isRunEnabled()) {
            if (Movement.getRunEnergy() > Random.mid(5, 30)) {
                Log.info("Toggling run");
                Movement.toggleRun(true);
            }
        }

        if (!hasGear) {
            if (Equipment.contains(glory)
                    && Equipment.contains(STAFF_OF_AIR)) {
                Log.info("Setting hasGear to true");
                hasGear = true;
            }
        }

        if (!readyToStartWitchesHouse) {
            if (Equipment.contains(GEAR)
                    && Inventory.getCount(CHEESE) == 2
                    && Inventory.getCount(true, MIND_RUNE) == 100
                    && Inventory.getCount(true, FIRE_RUNE) == 300
                    && Inventory.getCount(true, FALADOR_TELEPORT) == 5
                    && Inventory.getCount(TUNA) == 10
                    && Inventory.getCount(LEATHER_GLOVES) == 1) {
                Log.info("Setting readyToStartWitchesHouse to true");
                readyToStartWitchesHouse = true;
            }
        }

        if (!Equipment.contains(glory)) {
            if (Inventory.contains(glory)) {
                Inventory.getFirst(glory).interact("Wear");
            }
        }

        if (hasItems && !hasGear) {
            for (String i : GEAR) {
                if (!Equipment.contains(i)) {
                    if (Inventory.contains(GEAR)) {
                        if (Inventory.getFirst(i).containsAction(WIELD)) {
                            if (Inventory.getFirst(i).interact(WIELD)) {
                                Time.sleepUntil(() -> Equipment.contains(i), 5000);
                            }
                        }
                        if (Inventory.getFirst(i).containsAction(WEAR)) {
                            if (Inventory.getFirst(i).interact(WEAR)) {
                                Time.sleepUntil(() -> Equipment.contains(i), 5000);
                            }
                        }
                    }
                }
            }
        }
        if (hasItems && hasGear) {
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
                    withdrawItem(CHEESE, 2);
                    withdrawItem(MIND_RUNE, 100);
                    withdrawItem(FIRE_RUNE, 300);
                    withdrawItem(FALADOR_TELEPORT, 5);
                    withdrawItem(TUNA, 10);
                    withdrawItem(LEATHER_GLOVES, 1);
                }
            }
        }

        return lowRandom();

    }

    public int lowRandom() {
        return Random.mid(299, 444);
    }

    public void withdrawItem(String item, int amount) {
        if (!Bank.isOpen()) {
            Bank.open();
        }
        if (Bank.isOpen()) {
            if (!Inventory.contains(item)) {
                if (Bank.withdraw(item, amount)) {
                    Time.sleepUntil(() -> Inventory.getCount(item) == amount, Random.mid(3000, 5000));
                }
            }
        }
    }

}
