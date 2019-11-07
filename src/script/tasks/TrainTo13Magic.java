package script.tasks;

import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;
import script.data.IDs;
import script.data.Strings;
import script.wrappers.SleepWrapper;

public class TrainTo13Magic extends Task {

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
            "Stamina potion",
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
    public static final String[] ALL_ITEMS_NEEDED_FOR_MAGIC_TRAINING = {"Staff of air", "Staff of fire", "Glory", "Ring of wealth (5)", "Stamina potion(4)", "Mind rune", "Water rune", "Earth rune", "Lumbridge teleport", "Tuna"};
    public static final String[] POSSIBLE_NOTED_ITEMS = {"Amulet of glory(6)", "Ring of wealth (5)", "Tuna", "Stamina potion(4)"};

    @Override
    public boolean validate() {
        return Inventory.containsAll(IDs.ALL_ITEMS_NEEDED_FOR_ACCOUNT_PREPERATION);
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

        if (Inventory.contains(x -> x.getName().contains(Strings.STAMINA_POTION))) {
            if (!Movement.isStaminaEnhancementActive()) {
                Item staminaPotion = Inventory.getFirst(x -> x.getName().contains(Strings.STAMINA_POTION));
                Log.info("I am drinking a stamina potion");
                if (staminaPotion.interact(Strings.DRINK_ACTION)) {
                    Time.sleepUntil(() -> Movement.isStaminaEnhancementActive(), SleepWrapper.mediumSleep1500());
                }
            }
        }

        if(Inventory.containsAnyExcept(ALL_ITEMS_NEEDED_FOR_MAGIC_TRAINING)){
            if(!Bank.isOpen()){
                if(Bank.open()){
                    Time.sleepUntil(()-> Bank.isOpen(), SleepWrapper.longSleep7500());
                }
            }
            if(Bank.isOpen()){
                if(Bank.depositInventory()){
                    Time.sleepUntil(()-> Inventory.isEmpty(), SleepWrapper.longSleep7500());
                }
            }
        }

        return SleepWrapper.shortSleep350();
    }
}
