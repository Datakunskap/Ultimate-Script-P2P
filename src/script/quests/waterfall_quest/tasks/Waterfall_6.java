package script.quests.waterfall_quest.tasks;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.BankLocation;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Bank;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.EquipmentSlot;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;
import script.quests.nature_spirit.data.Quest;

import static org.rspeer.runetek.api.input.menu.ActionOpcodes.ITEM_ON_OBJECT;
import static script.quests.waterfall_quest.data.Quest.WATERFALL;
import static script.quests.waterfall_quest.data.Quest.WITCHES_HOUSE;


public class Waterfall_6 extends Task {

    private Area KeyArea = Area.rectangular(2581, 9889, 2597, 9877);
    private static final Position Ledgeposition = new Position(2511, 3463);
    private static final Position Door = new Position(2568, 9893);
    private static final Position Pos2 = new Position(2545, 9830);
    private static final Position Bitch = new Position(2521, 3495);
    private Area FinalRoom = Area.rectangular(2575, 9902, 2558, 9917);
    private Area RaftArea2 = Area.rectangular(2506, 3470, 2518, 3460);
    final int Airs = 6;
    final int Earth = 6;
    final int Waters = 6;
    @Override
    public boolean validate() {
        return WITCHES_HOUSE.getVarpValue() == 7 && WATERFALL.getVarpValue() == 6;
    }

    @Override
    public int execute() {

        if(FinalRoom.contains(Players.getLocal())) {
            SceneObject pillar1 = SceneObjects.getFirstAt(new Position(2562, 9910));
            SceneObject pillar2 = SceneObjects.getFirstAt(new Position(2562, 9912));
            SceneObject pillar3 = SceneObjects.getFirstAt(new Position(2562, 9914));
            SceneObject pillar4 = SceneObjects.getFirstAt(new Position(2569, 9910));
            SceneObject pillar5 = SceneObjects.getFirstAt(new Position(2569, 9912));
            SceneObject pillar6 = SceneObjects.getFirstAt(new Position(2569, 9914));
            if (Inventory.getFirst(556) != null) {
                if (Inventory.getFirst(556).getStackSize() == Airs) {
                    Inventory.getFirst(556).interact("Use");
                    RandomSleep();
                    pillar1.interact(ITEM_ON_OBJECT);
                    Time.sleep(4000);
                }
            }
            if (Inventory.getFirst(557) != null) {
                if (Inventory.getFirst(557).getStackSize() == Earth) {
                    Inventory.getFirst(557).interact("Use");
                    RandomSleep();
                    pillar1.interact(ITEM_ON_OBJECT);
                    Time.sleep(4000);
                }
            }
            if (Inventory.getFirst(555) != null) {
                if (Inventory.getFirst(555).getStackSize() == Waters) {
                    Inventory.getFirst(555).interact("Use");
                    RandomSleep();
                    pillar1.interact(ITEM_ON_OBJECT);
                    Time.sleep(4000);
                }
            }
            if (Inventory.getFirst(556) != null) {
                if (Inventory.getFirst(556).getStackSize() == Airs - 1) {
                    Inventory.getFirst(556).interact("Use");
                    RandomSleep();
                    pillar2.interact(ITEM_ON_OBJECT);
                    Time.sleep(4000);
                }
            }
            if (Inventory.getFirst(557) != null) {
                if (Inventory.getFirst(557).getStackSize() == Earth - 1) {
                    Inventory.getFirst(557).interact("Use");
                    RandomSleep();
                    pillar2.interact(ITEM_ON_OBJECT);
                    Time.sleep(4000);
                }
            }
            if (Inventory.getFirst(555) != null) {
                if (Inventory.getFirst(555).getStackSize() == Waters - 1) {
                    Inventory.getFirst(555).interact("Use");
                    RandomSleep();
                    pillar2.interact(ITEM_ON_OBJECT);
                    Time.sleep(4000);
                }
            }
            if (Inventory.getFirst(556) != null) {
                if (Inventory.getFirst(556).getStackSize() == Airs - 2) {
                    Inventory.getFirst(556).interact("Use");
                    RandomSleep();
                    pillar3.interact(ITEM_ON_OBJECT);
                    Time.sleep(4000);
                }
            }
            if (Inventory.getFirst(557) != null) {
                if (Inventory.getFirst(557).getStackSize() == Earth - 2) {
                    Inventory.getFirst(557).interact("Use");
                    RandomSleep();
                    pillar3.interact(ITEM_ON_OBJECT);
                    Time.sleep(4000);
                }
            }
            if (Inventory.getFirst(555) != null) {
                if (Inventory.getFirst(555).getStackSize() == Waters - 2) {
                    Inventory.getFirst(555).interact("Use");
                    RandomSleep();
                    pillar3.interact(ITEM_ON_OBJECT);
                    Time.sleep(4000);
                }
            }
            if (Inventory.getFirst(556) != null) {
                if (Inventory.getFirst(556).getStackSize() == Airs - 3) {
                    Inventory.getFirst(556).interact("Use");
                    RandomSleep();
                    pillar4.interact(ITEM_ON_OBJECT);
                    Time.sleep(4000);
                }
            }
            if (Inventory.getFirst(557) != null) {
                if (Inventory.getFirst(557).getStackSize() == Earth - 3) {
                    Inventory.getFirst(557).interact("Use");
                    RandomSleep();
                    pillar4.interact(ITEM_ON_OBJECT);
                    Time.sleep(4000);
                }
            }
            if (Inventory.getFirst(555) != null) {
                if (Inventory.getFirst(555).getStackSize() == Waters - 3) {
                    Inventory.getFirst(555).interact("Use");
                    RandomSleep();
                    pillar4.interact(ITEM_ON_OBJECT);
                    Time.sleep(4000);
                }
            }
            if (Inventory.getFirst(556) != null) {
                if (Inventory.getFirst(556).getStackSize() == Airs - 4) {
                    Inventory.getFirst(556).interact("Use");
                    RandomSleep();
                    pillar5.interact(ITEM_ON_OBJECT);
                    Time.sleep(4000);
                }
            }
            if (Inventory.getFirst(557) != null) {
                if (Inventory.getFirst(557).getStackSize() == Earth - 4) {
                    Inventory.getFirst(557).interact("Use");
                    RandomSleep();
                    pillar5.interact(ITEM_ON_OBJECT);
                    Time.sleep(4000);
                }
            }
            if (Inventory.getFirst(555) != null) {
                if (Inventory.getFirst(555).getStackSize() == Waters - 4) {
                    Inventory.getFirst(555).interact("Use");
                    RandomSleep();
                    pillar5.interact(ITEM_ON_OBJECT);
                    Time.sleep(4000);
                }
            }
            if (Inventory.getFirst(556) != null) {
                if (Inventory.getFirst(556).getStackSize() == Airs - 5) {
                    Inventory.getFirst(556).interact("Use");
                    RandomSleep();
                    pillar6.interact(ITEM_ON_OBJECT);
                    Time.sleep(4000);
                }
            }
            if (Inventory.getFirst(557) != null) {
                if (Inventory.getFirst(557).getStackSize() == Earth - 5) {
                    Inventory.getFirst(557).interact("Use");
                    RandomSleep();
                    pillar6.interact(ITEM_ON_OBJECT);
                    Time.sleep(4000);
                }
            }
            if (Inventory.getFirst(555) != null) {
                if (Inventory.getFirst(555).getStackSize() == Waters - 5) {
                    Inventory.getFirst(555).interact("Use");
                    RandomSleep();
                    pillar6.interact(ITEM_ON_OBJECT);
                    Time.sleep(4000);
                }
            }
            if (Inventory.getFirst(556) == null && Inventory.getFirst(557) == null && Inventory.getFirst(555) == null) {
                if(Equipment.isOccupied(EquipmentSlot.NECK)){
                    EquipmentSlot.NECK.unequip();
                    RandomSleep();
                }
                if(Inventory.contains(295)){
                    Inventory.getFirst(295).interact("Use");
                    RandomSleep();
                    SceneObjects.getNearest(2006).interact(ITEM_ON_OBJECT);
                    Time.sleep(4000);
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