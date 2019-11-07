package script.quests.waterfall_quest.tasks;

import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.GrandExchange;
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

import static script.quests.waterfall_quest.data.Quest.WATERFALL;
import static script.quests.waterfall_quest.data.Quest.WITCHES_HOUSE;

public class Waterfall_Preparation extends Task {

    private boolean hasItems = true;
    private boolean hasGear = false;
    public static boolean readyToStartWaterfall = false;

    Predicate<Item> glory = x -> x.getName().contains("Amulet of glory(");

    private static final String COINS = "Coins";
    private static final String GLORY = "Amulet of glory(6)";
    private static final String STAFF_OF_AIR = "Staff of air";
    private static final String RING_OF_WEALTH = "Ring of wealth (5)";
    private static final String AIR_RUNE = "Air rune";
    private static final String WATER_RUNE = "Water rune";
    private static final String EARTH_RUNE = "Earth rune";
    private static final String LUMBRIDGE_TELEPORT = "Lumbridge teleport";
    private static final String GAMES_NECKLACE = "Games necklace(8)";
    private static final String ROPE = "Rope";
    private static final String TUNA = "Tuna";
    private static final String[] GEAR = {GLORY, STAFF_OF_AIR};
    private static final String WIELD = "Wield";
    private static final String WEAR = "Wear";

    private static final Area GE_AREA = Area.rectangular(3157, 3489, 3171, 3477);

    @Override
    public boolean validate() {
        return WITCHES_HOUSE.getVarpValue() == 7
                && WATERFALL.getVarpValue() == 0
                && Skills.getLevel(Skill.MAGIC) >= 13
                && !Waterfall_Preparation.readyToStartWaterfall;
    }

    @Override
    public int execute() {

        Log.info("Hi");

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

        if (!readyToStartWaterfall) {
            if (Equipment.contains(glory)
                    && Equipment.contains(STAFF_OF_AIR)
                    && Inventory.getCount(false, GAMES_NECKLACE) == 1
                    && Inventory.getCount(false, TUNA) >= 4) {
                Log.info("Setting readyToStartWitchesHouse to true");
                readyToStartWaterfall = true;
            }
        }

        if (hasItems && !hasGear) {
            if (GrandExchange.isOpen()) {
                Movement.setWalkFlag(local.getPosition());
                Time.sleepUntil(() -> !GrandExchange.isOpen(), 5000);
            }
            if (!GrandExchange.isOpen()) {
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
        }
        if (hasItems && hasGear) {
            if (!Bank.isOpen()) {
                Bank.open();
            }
            if (Bank.isOpen()) {
                if (Inventory.contains(COINS)) {
                    if (Bank.depositInventory()) {
                        Time.sleepUntil(() -> Inventory.isEmpty(), 5000);
                    }
                }
                if (!Inventory.contains(COINS)) {
                    withdrawItem(GAMES_NECKLACE, 1, false);
                    withdrawItem(TUNA, 7, false);
                }
            }
        }

        return lowRandom();

    }

    public int lowRandom() {
        return Random.mid(299, 444);
    }

    public void withdrawItem(String item, int amount, boolean stack) {
        if (!Bank.isOpen()) {
            Bank.open();
        }
        if (Bank.isOpen()) {
            if (!Inventory.contains(item)) {
                if (Bank.withdraw(item, amount)) {
                    Time.sleepUntil(() -> Inventory.getCount(stack, item) == amount, Random.mid(3000, 5000));
                }
            }
        }
    }

}
