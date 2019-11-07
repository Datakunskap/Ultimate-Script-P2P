package script.quests.waterfall_quest.tasks;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.*;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

import static org.rspeer.runetek.api.input.menu.ActionOpcodes.ITEM_ON_OBJECT;
import static script.quests.waterfall_quest.data.Quest.WATERFALL;
import static script.quests.waterfall_quest.data.Quest.WITCHES_HOUSE;


public class Waterfall_3 extends Task {

    private static final Position DugeonEntrance = new Position(2533, 3155);
    private Area Dungeon = Area.rectangular(2505, 9585, 2558, 9547);
    private Area ThroughGate = Area.rectangular(2523, 9576, 2507, 9585);
    private static final Position GlarialEnt = new Position(2555, 3444);

    @Override
    public boolean validate() {
        return WITCHES_HOUSE.getVarpValue() == 7 && WATERFALL.getVarpValue() == 3;
    }

    @Override
    public int execute() {
        if(Skills.getCurrentLevel(Skill.HITPOINTS) <= Skills.getLevel(Skill.HITPOINTS) - 8){
            if(Inventory.contains("Tuna")){
                Inventory.getFirst("Tuna").click();
                RandomSleep();
            }
        }
        if(!Inventory.contains(294)) {
            if (Players.getLocal().getFloorLevel() == 1) {
                if (SceneObjects.getNearest(16673) != null) {
                    SceneObjects.getNearest(16673).click();
                    RandomSleep();
                    RandomSleep();
                }
            }
            if (Players.getLocal().getFloorLevel() != 1) {
                if (DugeonEntrance.distance() > 5 && !Dungeon.contains(Players.getLocal())) {
                    Movement.walkToRandomized(DugeonEntrance);
                }
            }
            if (SceneObjects.getNearest(5250) != null) {
                SceneObjects.getNearest(5250).click();
                RandomSleep();
            }

            if (Dungeon.contains(Players.getLocal())) {
                if (SceneObjects.getNearest(1990) != null && !Inventory.contains(293)) {
                    SceneObjects.getNearest(1990).click();
                    RandomSleep();
                    RandomSleep();
                }
                if (Inventory.contains(293)) {
                    if (SceneObjects.getNearest(1991) != null && !ThroughGate.contains(Players.getLocal())) {
                        if (SceneObjects.getNearest(1991).distance() > 5) {
                            Movement.setWalkFlag(SceneObjects.getNearest(1991).getPosition());
                        }
                        if (SceneObjects.getNearest(1991).distance() <= 5) {
                            Inventory.getFirst(293).interact("Use");
                            RandomSleep();
                            SceneObjects.getNearest(1991).interact(ITEM_ON_OBJECT);
                            RandomSleep();
                        }
                    }
                }
                if (ThroughGate.contains(Players.getLocal())) {
                    Npc Glory = Npcs.getNearest(4183);
                    if (Glory != null) {
                        if (!Dialog.isOpen()) {
                            Glory.click();
                            RandomSleep();
                        }
                        if (Dialog.isOpen()) {
                            if (Interfaces.getComponent(219, 1) == null) {
                                Dialog.processContinue();
                                RandomSleep();
                            }
                            if (getComponentOptions(2).contains("How can")) {
                                clickDialogComponenet(2);
                            }
                        }
                    }
                }
            }
        }
        if(Inventory.contains(294) && Equipment.getOccupiedSlots().length > 0){
            if(ThroughGate.contains(Players.getLocal())){
                if (Inventory.contains(x -> x.getName().contains("Games") && !Equipment.contains(i -> i.getName().contains("Games")))) {
                    Inventory.getFirst(x -> x.getName().contains("Games")).click();
                    RandomSleep();
                    RandomSleep();
                }
                if (Equipment.contains(x -> x.getName().contains("Games"))) {
                    if(!Tabs.open(Tab.EQUIPMENT)){
                        Tabs.open(Tab.EQUIPMENT);
                    }
                    Equipment.interact(x -> x.getName().contains("Games"), "Barbarian outpost");
                    RandomSleep();
                }
            }
            DoBanking();
        }
        if(Bank.isOpen()){
            RandomSleep();
            Bank.depositEquipment();
            RandomSleep();
            Bank.depositAllExcept(294);
            RandomSleep();
            WithdrawItem("Glarial's pebble", 1);
            RandomSleep();
            Bank.withdraw(x -> x.getName().contains("Games"), 1);
            RandomSleep();
            WithdrawItem("Tuna", 10);
        }
        if(Inventory.containsAll("Tuna", "Glarial's pebble") && Inventory.contains(x -> x.getName().contains("Games")) && Equipment.getOccupiedSlots().length < 1){
            if(GlarialEnt.distance() > 5){
                Movement.walkToRandomized(GlarialEnt);
            }
            if(GlarialEnt.distance() <= 8){
                Inventory.getFirst(294).interact("Use");
                RandomSleep();
                SceneObjects.getNearest(1992).interact(ITEM_ON_OBJECT);
                RandomSleep();
                Time.sleepUntil(() -> Bank.isOpen(), Random.mid(8500, 16850)); // randomise movements to bank
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