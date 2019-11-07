package script.quests.waterfall_quest.tasks;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
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


public class Waterfall_4 extends Task {


    private Area Dungeon = Area.rectangular(2523, 9850, 2559, 9808);
    private static final Position Ledgeposition = new Position(2511, 3463);
    private static final Position Pos1 = new Position(2541, 9844);
    private static final Position Pos2 = new Position(2545, 9830);
    private static final Position Bitch = new Position(2521, 3495);
    private Area RaftArea = Area.rectangular(2507, 3483, 2515, 3476);
    private Area RaftArea2 = Area.rectangular(2506, 3470, 2518, 3460);
    @Override
    public boolean validate() {
        return WITCHES_HOUSE.getVarpValue() == 7 && WATERFALL.getVarpValue() == 4;
    }

    @Override
    public int execute() {
        SceneObject Tomb;
        SceneObject tomb;
        tomb = SceneObjects.getNearest(x -> x.getName().contains("Glarial's"));

        if(Skills.getCurrentLevel(Skill.HITPOINTS) <= Skills.getLevel(Skill.HITPOINTS) - 4){
            if(Inventory.contains("Tuna")){
                Inventory.getFirst("Tuna").click();
                RandomSleep();
            }
        }
        if(Dungeon.contains(Players.getLocal())) {
            if (!Inventory.contains(295)) {
                Tomb = SceneObjects.newQuery().ids(1994, 1995).results().nearest();
                if (Tomb == null) {
                    Movement.setWalkFlag(Pos1);
                    RandomSleep();
                    RandomSleep();
                }
                if (Tomb != null) {
                    Tomb.click();
                    RandomSleep();
                }
            }
            if (Inventory.contains(295) && !Inventory.contains(296)) {
                if(tomb == null) {
                    Movement.setWalkFlag(Pos2);
                    RandomSleep();
                }
                if(tomb != null){
                    tomb.click();
                    RandomSleep();
                    RandomSleep();
                }
            }
            if(Inventory.containsAll(295, 296)) {
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
            }
        if(!Dungeon.contains(Players.getLocal()) && !Inventory.containsAll(296, 555, 556, 557, 361, 954) || (!Inventory.containsAll(296, 555, 556, 557, 361, 954) && !Equipment.contains(295))){
            DoBanking();
        }
        if(Bank.isOpen()){
            RandomSleep();
            RandomSleep();
            Bank.depositInventory();
            RandomSleep();
            RandomSleep();
            RandomSleep();
            WithdrawItem("Air rune", 6);
            WithdrawItem("Earth rune", 6);
            WithdrawItem("Water rune", 6);
            WithdrawItem("Glarial's pebble", 1);
            WithdrawItem("Glarial's amulet", 1);
            WithdrawItem("Rope", 1);
            WithdrawItem("Glarial's urn", 1);
            WithdrawItem("Lumbridge teleport", 1);
            RandomSleep();
            WithdrawItem("Tuna", 10);
            RandomSleep();
        }
        if(Inventory.containsAll(296, 555, 556, 557, 361, 954) || (Inventory.containsAll(296, 555, 556, 557, 361, 954) && Equipment.contains(295))){
            if (!RaftArea.contains(Players.getLocal()) && !RaftArea2.contains(Players.getLocal())) {
            if (Bitch.distance() < 250) {
                if (Bitch.distance() > 10) {
                    Movement.walkToRandomized(Bitch);
                }
            }
            if (Bitch.distance() <= 8) {
                if(Inventory.contains(295)){
                    Inventory.getFirst(295).click();
                    RandomSleep();
                }
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
            }
            if (RaftArea.contains(Players.getLocal())) {
                if(SceneObjects.getNearest(1996) != null){
                    Inventory.getFirst(954).interact("Use");
                    RandomSleep();
                    SceneObjects.getNearest(1996).interact(ITEM_ON_OBJECT);
                    RandomSleep();
                    Time.sleepUntil(() -> !RaftArea.contains(Players.getLocal()),5850);
                    }
            }
            if(RaftArea2.contains(Players.getLocal())){
                if(Ledgeposition.distance() > 0) {
                    Inventory.getFirst(954).interact("Use");
                    RandomSleep();
                    SceneObjects.getNearest(2020).interact(ITEM_ON_OBJECT);
                    RandomSleep();
                    Time.sleepUntil(() -> !RaftArea.contains(Players.getLocal()), 5850);
                }
                if(Ledgeposition.distance() < 1) {
                    if(Equipment.contains(295)){
                        if(SceneObjects.getNearest(2010) != null){
                            SceneObjects.getNearest(2010).click();
                            RandomSleep();
                            RandomSleep();
                        }
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