package script.quests.waterfall_quest.tasks;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

import static script.quests.waterfall_quest.data.Quest.WATERFALL;
import static script.quests.waterfall_quest.data.Quest.WITCHES_HOUSE;


public class Waterfall_0 extends Task {

    private static final Position Bitch = new Position(2521, 3495);
    private Npc bitch;

    @Override
    public boolean validate() {
        return WITCHES_HOUSE.getVarpValue() == 7
                && WATERFALL.getVarpValue() == 0
                && Waterfall_Preparation.readyToStartWaterfall;
    }

    @Override
    public int execute() {
        bitch = Npcs.getNearest(4181);
        if (!Inventory.contains(x -> x.getName().contains("Games")) || !Inventory.contains("Tuna")) {
            DoBanking();
            if (Bank.isOpen()) {
                RandomSleep();
                Bank.depositInventory();
                RandomSleep();
                if (Bank.contains(x -> x.getName().contains("Games"))) {
                    Bank.withdraw(x -> x.getName().contains("Games"), 1);
                    RandomSleep();
                }
                WithdrawItem("Tuna", 10);

            }
        }

        if (Inventory.contains(x -> x.getName().contains("Games")) && Inventory.contains("Tuna")) {
            if (Bitch.distance() > 100) {
                Movement.walkToRandomized(Bitch);
            }
        }
        if (Bitch.distance() < 250) {
            if (Bitch.distance() > 10) {
                Movement.walkToRandomized(Bitch);
            }
            if (bitch != null) {
                if (Bitch.distance() <= 10 && !Dialog.isOpen()) {
                    bitch.click();
                }
                if (Dialog.isOpen() && Interfaces.getComponent(219, 1) == null) {
                    Dialog.processContinue();
                    RandomSleep();
                }
                if (getComponentOptions(1).contains("How can")) {
                    clickDialogComponenet(1);
                }
            }
        }

        return 600;
    }

    private void WithdrawItem(String Name, int amount) {
        if (Bank.contains(Name)) {
            Bank.withdraw(Name, amount);
            Time.sleepUntil(() -> Inventory.contains(Name), Random.mid(2500, 5850));
            Time.sleep(250);
        }
    }

    public void DoBanking() {
        if (BankLocation.getNearest().getPosition().distance() > 2) {
            Movement.walkToRandomized(BankLocation.getNearest().getPosition());
        }
        if (BankLocation.getNearest().getPosition().distance() <= 2 && !Bank.isOpen()) {
            Npc NpcsBank = Npcs.getNearest(Npc -> (Npc.getName().equals("Banker") && Npc.containsAction("Bank")));
            if (NpcsBank != null) {
                NpcsBank.interact(s -> s.equals("Bank") || s.equals("Use"));
                Time.sleepUntil(() -> Bank.isOpen(), Random.mid(8500, 16850)); // randomise movements to bank
            }
        }
        if (BankLocation.getNearest().getPosition().distance() <= 5 && !Bank.isOpen()) {
            Log.info("Using Booth");
            SceneObject sceneObjectBank = SceneObjects.newQuery().nameContains("Bank", "Chest", "chest").actions("Bank").within(5).reachable().results().limit(1).nearest();
            if (sceneObjectBank != null) {
                sceneObjectBank.click();
                Time.sleepUntil(() -> Bank.isOpen(), Random.mid(5500, 8850));
            }
        }
    }

    public void clickDialogComponenet(int Option) {
        if (Interfaces.getComponent(219, 1, Option) != null) {
            Interfaces.getComponent(219, 1, Option).click();
            RandomSleep();
        }
    }

    public String getComponentOptions(int Option) {
        String Text = "Null";
        if (Interfaces.getComponent(219, 1, Option) != null) {
            Text = Interfaces.getComponent(219, 1, Option).getText();
        }
        return Text;
    }

    public String getDialogOptions() {
        String Text = "Null";
        if (Interfaces.getComponent(219, 1, 0) != null) {
            Text = Interfaces.getComponent(219, 1, 0).getText();
        }
        return Text;
    }

    public String getDialog() {
        String Text = "Null";
        if (Interfaces.getComponent(263, 1, 0) != null) {
            Text = Interfaces.getComponent(263, 1, 0).getText();
        }
        return Text;
    }

    public void RandomSleep() {
        Time.sleep(Random.nextInt(250, 550));
    }
}