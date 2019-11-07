package script.quests.priest_in_peril.tasks;

import org.rspeer.runetek.adapter.component.Item;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.script.task.Task;
import script.wrappers.WalkingWrapper;

import static script.quests.waterfall_quest.data.Quest.PRIEST_IN_PERIL;
import static script.quests.waterfall_quest.data.Quest.WATERFALL;

public class PriestInPeril_35 extends Task {

    private static final String STAMINA_POTION = "Stamina potion(";

    private static final Position DREZEL_POSITION = new Position(3439, 9897, 0);

    @Override
    public boolean validate() {
        return WATERFALL.getVarpValue() == 10 && PRIEST_IN_PERIL.getVarpValue() == 35;
    }

    @Override
    public int execute() {
        Player local = Players.getLocal();

        if (Dialog.canContinue()) {
            Dialog.processContinue();
        }

        if (!Movement.isRunEnabled()) {
            if (Movement.getRunEnergy() > Random.mid(5, 30)) {
                Movement.toggleRun(true);
            }
        }

        if (!Movement.isStaminaEnhancementActive()) {
            if (Inventory.contains(x -> x.getName().contains(STAMINA_POTION))) {
                Item staminaPotion = Inventory.getFirst(x -> x.getName().contains(STAMINA_POTION));
                if (staminaPotion.interact("Drink")) {
                    Time.sleepUntil(() -> Movement.isStaminaEnhancementActive(), 5000);
                }
            }
        }

        if (!Inventory.contains("Rune essence")) {
            if (BankLocation.VARROCK_EAST.getPosition().distance() > 10) {
                Movement.walkToRandomized(BankLocation.VARROCK_EAST.getPosition());
            }
            if (BankLocation.VARROCK_EAST.getPosition().distance() <= 10) {
                if (!Bank.isOpen()) {
                    if (Bank.open()) {
                        Time.sleepUntil(() -> Bank.isOpen(), 20000);
                    }
                }
                if (Bank.isOpen()) {
                    if (!Inventory.isEmpty()) {
                        if (Bank.depositInventory()) {
                            Time.sleepUntil(() -> Inventory.isEmpty(), 5000);
                        }
                    }
                    if(Inventory.isEmpty()){
                        if(Bank.withdraw("Varrock teleport", 1)){
                            Time.sleepUntil(()-> Inventory.contains("Varrock teleport"), 5000);
                        }
                        if(Bank.withdraw("Stamina potion(4)", 1)){
                            Time.sleepUntil(()-> Inventory.contains("Stamina potion(4)"), 5000);
                        }
                        if(Bank.withdraw("Rune essence", 25)){
                            Time.sleepUntil(()-> Inventory.contains("Rune essence"), 5000);
                        }
                    }
                }
            }
        }

        if(Inventory.contains("Rune essence")){
            if(DREZEL_POSITION.distance() > 10){
                Movement.walkTo(DREZEL_POSITION, WalkingWrapper::shouldBreakOnRunenergy);
            }
            if(DREZEL_POSITION.distance() <= 10){
                Npc drezel = Npcs.getNearest("Drezel");
                if(!Dialog.isOpen()){
                    if(drezel.interact("Talk-to")){
                        Time.sleepUntil(()-> Dialog.isOpen(), 5000);
                    }
                }
                if(Dialog.isOpen()){
                    if(Dialog.canContinue()){
                        Dialog.processContinue();
                    }
                }
            }
        }

        return lowRandom();
    }

    public int lowRandom() {
        return Random.mid(299, 444);
    }

}
