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
import script.wrappers.GEWrapper;
import script.wrappers.SleepWrapper;

public class BuyItemsNeeded extends Task {

    @Override
    public boolean validate() {
        return Inventory.containsAll(IDs.TUTORIAL_ISLAND_ITEMS)
                && Inventory.getCount(true, Strings.COINS) >= GetStartersGold.AMOUNT_TO_RECEIVE
                || Inventory.containsOnly(Strings.COINS)
                && Inventory.getCount(true,Strings.COINS) >= GetStartersGold.AMOUNT_TO_RECEIVE
                && Skills.getLevel(Skill.MAGIC) <= 1;
    }

    @Override
    public int execute() {

        Log.info("1");

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
                    Time.sleepUntil(Movement::isStaminaEnhancementActive, SleepWrapper.mediumSleep1500());
                }
            }
        }

        if(Inventory.containsAll(IDs.TUTORIAL_ISLAND_ITEMS)){
            if(!Bank.isOpen()){
                Log.info("Opening bank");
                if(Bank.open()){
                    Time.sleepUntil(Bank::isOpen, SleepWrapper.longSleep7500());
                }
            }
            if(Bank.isOpen()){
                Log.info("Depositing everything expect coins");
                if(Bank.depositAllExcept(Strings.COINS)){
                    Time.sleepUntil(()-> Inventory.containsOnly(Strings.COINS), SleepWrapper.longSleep7500());
                }
            }
        }

        if(Inventory.containsOnly(Strings.COINS)){
            Log.info("Buying the needed items");
            GEWrapper.setBuySupplies(true);
        }

        return SleepWrapper.shortSleep350();
    }
}
