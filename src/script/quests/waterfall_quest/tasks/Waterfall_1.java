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
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

import static script.quests.waterfall_quest.data.Quest.WATERFALL;
import static script.quests.waterfall_quest.data.Quest.WITCHES_HOUSE;


public class Waterfall_1 extends Task {

    private static final Position Bitch = new Position(2521, 3495);
    private Npc boy;
    private Area RaftArea = Area.rectangular(2508, 3482, 2514, 3476);

    @Override
    public boolean validate() {
        return WITCHES_HOUSE.getVarpValue() == 7
                && WATERFALL.getVarpValue() == 1;
    }

    @Override
    public int execute() {
        boy = Npcs.getNearest(4182);
        if (!RaftArea.contains(Players.getLocal())){
            SceneObject Raft = SceneObjects.newQuery().actions("Board").nameContains("Log").reachable().results().nearest();
        if (Raft == null) {
            if (SceneObjects.getNearest(1558) != null) {
                SceneObjects.getNearest(1558).click();
                RandomSleep();
            }
        }
        if (Raft != null) {
            Raft.click();
            RandomSleep();
        }
    }
        if(RaftArea.contains(Players.getLocal())){
            if(!Dialog.isOpen()) {
                if (boy != null) {
                    boy.click();
                }
            }
                    if (Dialog.isOpen() && Interfaces.getComponent(219, 1) == null) {
                        Dialog.processContinue();
                        RandomSleep();
                    }
                    if (getComponentOptions(2).contains("How can")) {
                        clickDialogComponenet(2);
                    }
            }
        return 600;
    }

    private void WithdrawItem(String Name, int amount){
        if(Bank.contains(Name)){
            Bank.withdraw(Name, amount);
            Time.sleepUntil(() -> Inventory.contains(Name), Random.mid(2500, 5850));
            Time.sleep(250);
        }
    }

    public void DoBanking(){
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
            SceneObject sceneObjectBank = SceneObjects.newQuery().nameContains("Bank", "Chest").actions("Use", "Bank").within(5).reachable().results().limit(1).nearest();
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

    public String getComponentOptions(int Option){
        String Text = "Null";
        if(Interfaces.getComponent(219, 1, Option) != null){
            Text = Interfaces.getComponent(219, 1, Option).getText();
        }
        return Text;
    }

    public String getDialogOptions(){
        String Text = "Null";
        if(Interfaces.getComponent(219, 1, 0) != null){
            Text = Interfaces.getComponent(219, 1, 0).getText();
        }
        return Text;
    }

    public String getDialog(){
        String Text = "Null";
        if(Interfaces.getComponent(263, 1, 0) != null){
            Text = Interfaces.getComponent(263, 1, 0).getText();
        }
        return Text;
    }

    public void RandomSleep(){
        Time.sleep(Random.nextInt(250, 550));
    }
}