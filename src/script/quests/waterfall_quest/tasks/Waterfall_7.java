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
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

import static org.rspeer.runetek.api.input.menu.ActionOpcodes.ITEM_ON_OBJECT;
import static script.quests.waterfall_quest.data.Quest.WATERFALL;
import static script.quests.waterfall_quest.data.Quest.WITCHES_HOUSE;


public class Waterfall_7 extends Task {


    private Area FinalRoom = Area.rectangular(2575, 9902, 2558, 9917);

    @Override
    public boolean validate() {
        return WITCHES_HOUSE.getVarpValue() == 7 && WATERFALL.getVarpValue() == 8;
    }

    @Override
    public int execute() {

                if(SceneObjects.getNearest(2014) != null){
                    if(!Dialog.isOpen()) {
                        Inventory.getFirst(296).interact("Use");
                        RandomSleep();
                        SceneObjects.getNearest(2014).interact(ITEM_ON_OBJECT);
                        Time.sleep(2000);
                    }
                    if(Dialog.isOpen()){
                        Dialog.processContinue();
                    }
                }

        return 400;
    }



    private void WithdrawItem(String Name, int amount){
        if(Bank.contains(Name)){
            Bank.withdraw(Name, amount);
            Time.sleepUntil(() -> Inventory.contains(Name), Random.mid(2500, 5850));
            Time.sleep(250);
        }
    }

    public void DoBanking(){
        if (BankLocation.getNearest().getPosition().distance() > 3) {
            Movement.walkToRandomized(BankLocation.getNearest().getPosition());
        }
        if (BankLocation.getNearest().getPosition().distance() <= 10 && !Bank.isOpen()) {
            Npc NpcsBank = Npcs.getNearest(Npc -> (Npc.getName().equals("Banker") && Npc.containsAction("Bank")));
            if (NpcsBank != null) {
                NpcsBank.interact(s -> s.equals("Bank") || s.equals("Use"));
                Time.sleepUntil(() -> Bank.isOpen(), Random.mid(8500, 16850)); // randomise movements to bank
            }
        }
        if (BankLocation.getNearest().getPosition().distance() <= 5 && !Bank.isOpen()) {
            Log.info("Using Booth");
            SceneObject sceneObjectBank = SceneObjects.newQuery().actions("Use", "Bank").within(5).reachable().results().limit(1).nearest();
            if (sceneObjectBank != null) {
                sceneObjectBank.interact(ITEM_ON_OBJECT);
                Time.sleepUntil(() -> Bank.isOpen(), Random.mid(5500, 8850));
            }
        }
    }

    public void clickDialogComponenet(int Option) {
        if (Interfaces.getComponent(219, 1, Option) != null) {
            Interfaces.getComponent(219, 1, Option).interact(ITEM_ON_OBJECT);
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