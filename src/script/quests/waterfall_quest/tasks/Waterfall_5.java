package script.quests.waterfall_quest.tasks;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;
import script.wrappers.WalkingWrapper;

import static org.rspeer.runetek.api.input.menu.ActionOpcodes.ITEM_ON_OBJECT;
import static script.quests.waterfall_quest.data.Quest.WATERFALL;
import static script.quests.waterfall_quest.data.Quest.WITCHES_HOUSE;


public class Waterfall_5 extends Task {


    private Area KeyArea = Area.rectangular(2581, 9889, 2597, 9877);
    private static final Position Ledgeposition = new Position(2511, 3463);
    private static final Position Door = new Position(2568, 9893);
    private static final Position Pos2 = new Position(2545, 9830);
    private static final Position Bitch = new Position(2521, 3495);
    private Area FinalRoom = Area.rectangular(2575, 9902, 2558, 9917);
    private Area RaftArea2 = Area.rectangular(2506, 3470, 2518, 3460);
    @Override
    public boolean validate() {
        return WITCHES_HOUSE.getVarpValue() == 7 && WATERFALL.getVarpValue() == 5;
    }

    @Override
    public int execute() {

        if (Skills.getCurrentLevel(Skill.HITPOINTS) <= Skills.getLevel(Skill.HITPOINTS) - 8) {
            if (Inventory.contains("Tuna")) {
                Inventory.getFirst("Tuna").click();
                RandomSleep();
            }
        }
        if (!Inventory.contains(298)) {

            if (!KeyArea.contains(Players.getLocal())) {
                Movement.walkTo(KeyArea.getCenter(), WalkingWrapper::shouldBreakOnTarget);
                RandomSleep();
                RandomSleep();

            }


            if (KeyArea.contains(Players.getLocal())) {
                SceneObject Bookcases = SceneObjects.newQuery().within(KeyArea).nameContains("Crate").reachable().actions("Search").ids(1999).results().random();
                Bookcases.click();
                Time.sleepUntil(() -> Inventory.contains(292), Random.mid(2500, 4850));
                RandomSleep();
            }
        }
        Log.info(Door.distance());
        if (Inventory.contains(298)) {
            if (Players.getLocal().getY() < 9893) {
                if(Door.distance() > 4) {
                    Movement.walkTo(Door, WalkingWrapper::shouldBreakOnTarget);
                    RandomSleep();
                    RandomSleep();
                }
            }
            if(Door.distance() <= 10){
                if (Players.getLocal().getY() <= 9893) {
                    if (SceneObjects.getNearest(2002) != null) {
                        Inventory.getFirst(298).interact("Use");
                        Log.info("Line 78");
                        RandomSleep();
                        SceneObjects.getNearest(2002).interact(ITEM_ON_OBJECT);
                        RandomSleep();
                    }
                }
                if(Players.getLocal().getY() > 9893 && !FinalRoom.contains(Players.getLocal())) {
                    SceneObject Door = SceneObjects.newQuery().within(new Position(2566, 9901), 1).actions("Open").results().nearest();
                    if (Door != null) {
//                        Inventory.getFirst(298).interact("Use");
                        Log.info("Line 89");
                        RandomSleep();
                        Door.click();
                        RandomSleep();
                        Time.sleepUntil(() -> FinalRoom.contains(Players.getLocal()), 5850);
                    }
                }
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