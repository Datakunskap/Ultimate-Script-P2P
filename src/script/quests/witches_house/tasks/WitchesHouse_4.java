package script.quests.witches_house.tasks;

import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.Pickables;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;

import static script.quests.witches_house.data.Quest.WITCHES_HOUSE;


public class WitchesHouse_4 extends Task {

    private static final Position Pot = new Position(2899, 3473);
    private static final Position Ladder = new Position(2906, 3476);
    private Area Shed = Area.rectangular(2934, 3467, 2937, 3459);
    private Area House = Area.rectangular(2901, 3476, 2937, 3459, 0);
    private Area Basement = Area.rectangular(2901, 9890, 2937, 9850);
    private Npc boy;
    private static final Position Boy = new Position(2929, 3456);
    @Override
    public boolean validate() {
        return WITCHES_HOUSE.getVarpValue() == 6;
    }

    @Override
    public int execute() {
        Player me = Players.getLocal();
       if(!Inventory.contains(2407)){
           if(Pickables.getNearest(2407) != null){
               Pickables.getNearest(2407).click();
               RandomSleep();
           }
       }
       if(Inventory.contains(2407) && Inventory.contains(8009) && Shed.contains(Players.getLocal())){
           Inventory.getFirst(8009).click();
           RandomSleep();
           RandomSleep();
       }
       if(!Shed.contains(Players.getLocal())){
           boy = Npcs.getNearest(3994);
           if(Boy.distance() > 10){
               Movement.walkToRandomized(Boy);
           }
           if(Boy.distance() <= 10 && !Dialog.isOpen()){
               boy.click();
           }
           if(Dialog.isOpen() && Interfaces.getComponent(219, 1) == null) {
               Dialog.processContinue();
               RandomSleep();
           }
       }
        return 600;
    }

    public void interactWithObject(int ID){
        if(SceneObjects.getNearest(ID) != null){
            SceneObjects.getNearest(ID).click();
            RandomSleep();
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