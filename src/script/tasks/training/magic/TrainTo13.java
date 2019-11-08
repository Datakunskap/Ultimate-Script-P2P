package script.tasks.training.magic;

import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.tab.*;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;
import script.wrappers.GEWrapper;
import script.wrappers.SleepWrapper;
import script.wrappers.SupplyMapWrapper;


public class TrainTo13 extends Task {

    boolean hasItems = false;
    boolean wearingGear = false;
    boolean boughtItems = false;

    private static final String[] ALL_ITEMS_NEEDED_FOR_ACCOUNT_PREPERATION = new String[]{
            "Lumbridge teleport",
            "Staff of air",
            "Staff of fire",
            "Amulet of glory(6)",
            "Ring of wealth (5)",
            "Air rune",
            "Mind rune",
            "Water rune",
            "Earth rune",
            "Tuna",
            "Stamina potion(4)",
            "Cheese",
            "Leather gloves",
            "Falador teleport",
            "Games necklace(8)",
            "Rope",
            "Adamant scimitar",
            "Ring of recoil",
            "Bucket",
            "Rune essence",
            "Varrock teleport",
            "Silver sickle"
    };

    public static final String TUNA = "Tuna";
    public static final String STAFF_OF_AIR = "Staff of air";
    public static final String GLORY = "Amulet of glory(6)";

    private static final Area TRAINING_AREA = Area.rectangular(3194, 3299, 3209, 3285);

    public static final String[] ALL_ITEMS_NEEDED_FOR_MAGIC_TRAINING = {"Staff of air", "Amulet of glory(6)", "Ring of wealth (5)", "Stamina potion(4)", "Mind rune", "Water rune", "Earth rune", "Lumbridge teleport", "Tuna"};


    @Override
    public boolean validate() {
        if(!boughtItems){
            if(Inventory.containsAll(ALL_ITEMS_NEEDED_FOR_ACCOUNT_PREPERATION)){
                Log.info("Setting boughtItem to true");
                boughtItems = true;
            } else {
                GEWrapper.setBuySupplies(true, SupplyMapWrapper.getStartingItemsMap());
            }
        }
        return boughtItems && Skills.getLevel(Skill.MAGIC) < 13;
    }

    @Override
    public int execute() {

        Player local = Players.getLocal();

        if (Dialog.canContinue()) {
            Log.info("I am continuing the dialog");
            Dialog.processContinue();
        }

        if (!Movement.isRunEnabled()) {
            if (Movement.getRunEnergy() > Random.mid(5, 30)) {
                Log.info("I am toggling run");
                Movement.toggleRun(true);
            }
        }

        if (!hasItems) {
            if (Inventory.containsAll(ALL_ITEMS_NEEDED_FOR_MAGIC_TRAINING)) {
                Log.info("Setting hasItems to true");
                hasItems = true;
            }
        }

        if (!wearingGear) {
            if (Equipment.containsAll(GLORY, STAFF_OF_AIR)) {
                Log.info("Setting wearingGear to true");
                wearingGear = true;
            }
        }

        if (Inventory.containsAnyExcept(ALL_ITEMS_NEEDED_FOR_MAGIC_TRAINING)) {
            if (!Bank.isOpen()) {
                if (Bank.open()) {
                    Time.sleepUntil(Bank::isOpen, SleepWrapper.longSleep7500());
                }
            }
            if (Bank.isOpen()) {
                if (Bank.depositInventory()) {
                    Time.sleepUntil(Inventory::isEmpty, SleepWrapper.longSleep7500());
                }
                if (Inventory.isEmpty()) {
                    withdrawItem("Staff of air", 1, false);
                    withdrawItem("Amulet of glory(6)", 1, false);
                    withdrawItem("Ring of wealth (5)", 1,false );
                    withdrawItem("Stamina potion(4)", 1,false);
                    withdrawItem("Mind rune", 1000, true);
                    withdrawItem("Water rune", 200,true);
                    withdrawItem("Earth rune", 200,true );
                    withdrawItem("Lumbridge teleport", 5, true);
                    withdrawItem("Tuna", 15,false);
                }
            }
        }

        if (hasItems && !wearingGear) {
            if (Inventory.contains(362)) {
                if (!Bank.isOpen()) {
                    Bank.open();
                }
                if (Bank.isOpen()) {
                    if (Bank.depositAll(362)) {
                        if (Time.sleepUntil(() -> Bank.contains(361), 5000)) {
                            if (Bank.withdrawAll(361)) {
                                Time.sleepUntil(() -> Inventory.getCount(false, TUNA) >= 15, 5000);
                            }
                        }
                    }
                }
            }
            if (!Inventory.contains(362)) {
                if (Inventory.contains(GLORY)) {
                    Item glory = Inventory.getFirst(GLORY);
                    glory.interact("Wear");
                }
                if (Inventory.contains(STAFF_OF_AIR)) {
                    Item staffOfAir = Inventory.getFirst(STAFF_OF_AIR);
                    staffOfAir.interact("Wield");
                }
            }
        }
        if (hasItems && wearingGear) {
            if (!TRAINING_AREA.contains(local)) {
                Movement.walkToRandomized(TRAINING_AREA.getCenter());
            }
            if (TRAINING_AREA.contains(local)) {
                if (!Magic.Autocast.isEnabled()) {
                    if (Skills.getLevel(Skill.MAGIC) < 5) {
                        if (Magic.Autocast.getSelectedSpell() != Spell.Modern.WIND_STRIKE) {
                            Magic.Autocast.select(Magic.Autocast.Mode.OFFENSIVE, Spell.Modern.WIND_STRIKE);
                        }
                    }
                    if (Skills.getLevel(Skill.MAGIC) >= 5 && Skills.getLevel(Skill.MAGIC) < 9) {
                        if (Magic.Autocast.getSelectedSpell() != Spell.Modern.WATER_STRIKE) {
                            Magic.Autocast.select(Magic.Autocast.Mode.OFFENSIVE, Spell.Modern.WATER_STRIKE);
                        }
                    }
                    if (Skills.getLevel(Skill.MAGIC) >= 9 && Skills.getLevel(Skill.MAGIC) < 13) {
                        if (Magic.Autocast.getSelectedSpell() != Spell.Modern.EARTH_STRIKE) {
                            Magic.Autocast.select(Magic.Autocast.Mode.OFFENSIVE, Spell.Modern.EARTH_STRIKE);
                        }
                    }
                }
                if (Magic.Autocast.isEnabled()) {
                    if (Skills.getLevel(Skill.MAGIC) < 5) {
                        if (Magic.Autocast.getSelectedSpell() != Spell.Modern.WIND_STRIKE) {
                            Magic.Autocast.select(Magic.Autocast.Mode.OFFENSIVE, Spell.Modern.WIND_STRIKE);
                        }
                    }
                    if (Skills.getLevel(Skill.MAGIC) >= 5 && Skills.getLevel(Skill.MAGIC) < 9) {
                        if (Magic.Autocast.getSelectedSpell() != Spell.Modern.WATER_STRIKE) {
                            Magic.Autocast.select(Magic.Autocast.Mode.OFFENSIVE, Spell.Modern.WATER_STRIKE);
                        }
                    }
                    if (Skills.getLevel(Skill.MAGIC) >= 9 && Skills.getLevel(Skill.MAGIC) < 13) {
                        if (Magic.Autocast.getSelectedSpell() != Spell.Modern.EARTH_STRIKE) {
                            Magic.Autocast.select(Magic.Autocast.Mode.OFFENSIVE, Spell.Modern.EARTH_STRIKE);
                        }
                    }
                    if (local.getTargetIndex() == -1) {
                        Npc targetNpc = Npcs.getNearest(x -> x.getName().equals("Cow") && x.getTarget() != null && x.getTarget().equals(local) || x.getName().equals("Cow") && x.getTargetIndex() == -1 && x.getHealthPercent() > 0);
                        if (targetNpc != null
                                && targetNpc.interact("Attack"))
                            Time.sleepUntil(() -> local.getTargetIndex() != -1, 5000);
                    }
                    if (Players.getLocal().getHealthPercent() <= 30) {
                        if (Inventory.contains(TUNA)) {
                            Inventory.getFirst(TUNA).interact("Eat");
                            Time.sleepUntil(() -> Players.getLocal().getHealthPercent() > 40, Random.mid(2500, 5850));
                            Time.sleep(449, 740);
                        }
                    }
                }
            }
        }

        return SleepWrapper.shortSleep350();
    }

    public void withdrawItem(String item, int amount, boolean stack) {
        if (Inventory.getCount(item) < amount) {
            if (Bank.withdraw(item, amount)) {
                Time.sleepUntil(() -> Inventory.getCount(stack, item) == amount, SleepWrapper.longSleep7500());
            }
        }
    }

}
