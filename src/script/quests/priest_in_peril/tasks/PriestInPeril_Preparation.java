package script.quests.priest_in_peril.tasks;

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
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;
import script.wrappers.SleepWrapper;

import java.util.function.Predicate;

import static script.quests.waterfall_quest.data.Quest.PRIEST_IN_PERIL;
import static script.quests.waterfall_quest.data.Quest.WATERFALL;

public class PriestInPeril_Preparation extends Task {

    private boolean hasItems = true;
    private boolean hasGear = false;
    public static boolean readyToStartPriestInPeril = false;

    private static final String COINS = "Coins";
    private static final String GLORY = "Amulet of glory(6)";
    private static final String ADAMANT_SCIMITAR = "Adamant scimitar";
    private static final String RING_OF_RECOIL = "Ring of recoil";
    private static final String RUNE_ESSENCE = "Rune essence";
    private static final String BUCKET = "Bucket";
    private static final String VARROCK_TELEPORT = "Varrock teleport";
    private static final String STAMINA_POTION = "Stamina potion(4)";
    private static final String TUNA = "Tuna";
    private static final String[] GEAR = {GLORY, ADAMANT_SCIMITAR, RING_OF_RECOIL};
    private static final String WIELD = "Wield";
    private static final String WEAR = "Wear";

    Predicate<Item> glory = x -> x.getName().contains("Amulet of glory(");

    private static final Area GE_AREA = Area.rectangular(3157, 3489, 3171, 3477);

    @Override
    public boolean validate() {
        return WATERFALL.getVarpValue() == 10
                && PRIEST_IN_PERIL.getVarpValue() == 0
                && Skills.getLevel(Skill.MAGIC) >= 13
                && !PriestInPeril_Preparation.readyToStartPriestInPeril;
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

        if (new Position(2603, 9912).distance() < 10) {
            if (Inventory.getFirst("Lumbridge teleport").interact("Break")) {
                Time.sleepUntil(()-> new Position(2603,9912).distance() > 10, SleepWrapper.longSleep7500());
            }
        }

        if (!hasGear) {
            if (Equipment.contains(glory)
                    && Equipment.contains(ADAMANT_SCIMITAR)
                    && Equipment.contains(RING_OF_RECOIL)) {
                Log.info("Setting hasGear to true");
                hasGear = true;
            }
        }

        if (!readyToStartPriestInPeril) {
            if (Equipment.contains(GEAR)
                    && Inventory.getCount(false, STAMINA_POTION) == 3
                    && Inventory.getCount(true, VARROCK_TELEPORT) == 5
                    && Inventory.getCount(false, TUNA) > 3) {
                Log.info("Setting readyToStartWitchesHouse to true");
                readyToStartPriestInPeril = true;
            }
        }

        if (hasItems && !hasGear) {
            if (!Inventory.contains(GEAR)) {
                if (!Bank.isOpen()) {
                    if (Bank.open()) {
                        Time.sleepUntil(Bank::isOpen, SleepWrapper.longSleep7500());
                    }
                }
                if (Bank.isOpen()) {
                    for (String i : GEAR) {
                        if (Bank.contains(i)) {
                            if (Bank.withdraw(i, 1)) {
                                Time.sleepUntil(() -> Inventory.contains(i), SleepWrapper.longSleep7500());
                            }
                        }
                    }
                }
            }
            if (Inventory.contains(GEAR)) {
                for (String i : GEAR) {
                    if (!Equipment.contains(i)) {
                        if (Inventory.contains(GEAR)) {
                            Item item = Inventory.getFirst(i);
                            if (item != null) {
                                if (item.containsAction(WIELD)) {
                                    if (item.interact(WIELD)) {
                                        Time.sleepUntil(() -> Equipment.contains(i), 5000);
                                    }
                                }
                            }
                            if (item != null) {
                                if (item.containsAction(WEAR)) {
                                    if (item.interact(WEAR)) {
                                        Time.sleepUntil(() -> Equipment.contains(i), 5000);
                                    }
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
                    withdrawItem(STAMINA_POTION, 3, false);
                    withdrawItem(VARROCK_TELEPORT, 5, true);
                    withdrawItem(TUNA, 10, false);
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
