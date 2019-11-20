package script.quests.waterfall_quest.tasks;

import api.API;
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
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

import static api.API.STAMINA_POTION;
import static script.quests.waterfall_quest.data.Quest.WATERFALL;
import static script.quests.witches_house.data.Quest.WITCHES_HOUSE;

public class Waterfall_Preparation extends Task {

    private boolean hasItems = true;
    private boolean hasGear = false;
    public static boolean readyToStartWaterfall = false;

    private static final String COINS = "Coins";
    private static final String GLORY = "Amulet of glory(";
    private static final String STAFF_OF_AIR = "Staff of air";
    private static final String GAMES_NECKLACE = "Games necklace(";
    private static final String Monkfish = "Monkfish";
    private static final String[] GEAR = {GLORY, STAFF_OF_AIR};
    private static final String WIELD = "Wield";
    private static final String WEAR = "Wear";

    @Override
    public boolean validate() {
        return WITCHES_HOUSE.getVarpValue() == 7
                && WATERFALL.getVarpValue() == 0
                && Skills.getLevel(Skill.MAGIC) >= 13
                && !Waterfall_Preparation.readyToStartWaterfall;
    }

    @Override
    public int execute() {

        Log.info("Waterfall_Preparation");

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
            if (!Equipment.contains(x -> x.getName().contains(GLORY))) {
                if (!Inventory.contains(STAFF_OF_AIR)) {
                    API.withdrawItem(false, STAFF_OF_AIR, 1);
                }
                if (Inventory.contains(STAFF_OF_AIR)) {
                    API.wearItem(STAFF_OF_AIR);
                }
            }
            if (Equipment.contains(x -> x.getName().contains(GLORY))
                    && Equipment.contains(x -> x.getName().contains(STAFF_OF_AIR))) {
                Log.info("Setting hasGear to true");
                hasGear = true;
            }
        }

        if (!readyToStartWaterfall) {
            if (Equipment.contains(x -> x.getName().contains(GLORY))
                    && Equipment.contains(STAFF_OF_AIR)
                    && Inventory.getCount(false, x -> x.getName().contains(GAMES_NECKLACE)) == 1
                    && Inventory.getCount(false, Monkfish) >= 12
                    && Inventory.getCount(false, x -> x.getName().contains(STAMINA_POTION)) >= 4) {
                Log.info("Setting readyToStartWaterfallQuest to true");
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
                    API.withdrawItem(false, GAMES_NECKLACE, 1);
                    API.withdrawItem(false, Monkfish, 12);
                    API.withdrawItem(false, STAMINA_POTION, 4);
                }
            }
        }

        return API.lowRandom();

    }

}

