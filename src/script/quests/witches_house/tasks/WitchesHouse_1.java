package script.quests.witches_house.tasks;

import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Equipment;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

import static script.quests.witches_house.data.Quest.WITCHES_HOUSE;


public class WitchesHouse_1 extends Task {

    private static final Position Pot = new Position(2899, 3473);
    private static final Position Ladder = new Position(2906, 3476);

    private Area House = Area.rectangular(2901, 3476, 2937, 3459, 0);
    private Area Basement = Area.rectangular(2901, 9890, 2937, 9850);
    @Override
    public boolean validate() {
        return WITCHES_HOUSE.getVarpValue() == 1;
    }

    @Override
    public int execute() {
        Player me = Players.getLocal();
        if(!Inventory.contains(2409)) {
            if (Pot.distance() > 10) {
                Movement.walkTo(Pot);
            }
            if (Pot.distance() <= 10) {
                interactWithObject(2867);
                if (Dialog.isOpen()) {
                    Dialog.processContinue();
                }
            }
        }

        if(Inventory.contains(2409) && !House.contains(me)){
            interactWithObject(2861);
        }
        if(House.contains(me)) {
            SceneObject Ladder = SceneObjects.newQuery().nameContains("Ladder").actions("Climb-down").reachable().results().nearest();
            if (Ladder == null) {
                interactWithObject(24686);
            }
            if (Ladder != null) {
                Ladder.click();
                RandomSleep();
            }
        }
            if (!Inventory.contains(1059) && !Equipment.contains(1059)) {
                if(SceneObjects.getNearest(24692) != null){
                    SceneObjects.getNearest(24692).click();
                    RandomSleep();
                }
            }
            if (Inventory.contains(1059) && !Equipment.contains(1059)) {
                Inventory.getFirst(1059).click();
                RandomSleep();
            }
            if(Equipment.contains(1059)){
                SceneObject Cupboard = SceneObjects.newQuery().nameContains("Cupboard").actions("Open").reachable().results().nearest();
                if(Cupboard == null){
                    interactWithObject(2865);
                }
                if(Cupboard != null){
                    Cupboard.click();
                    RandomSleep();
                }
                if(SceneObjects.getNearest(2869) != null){
                    SceneObjects.getNearest(2869).click();
                    RandomSleep();
                    if (Dialog.isOpen()) {
                        Dialog.processContinue();
                    }
                }
            }
            if (!Inventory.contains(2410)) {
                if(Inventory.isFull()){
                    if(Inventory.contains("Lobster")){
                        Inventory.getFirst("Lobster").click();
                        Time.sleep(1000);
                    }
                }
                SceneObject Cupboard = SceneObjects.newQuery().nameContains("Cupboard").actions("Open", "search").reachable().results().nearest();
                if(Cupboard == null && !Dialog.isOpen()){
                    if(SceneObjects.getNearest(2869) != null){
                        SceneObjects.getNearest(2869).click();
                        RandomSleep();
                    }
                }
                if(Cupboard != null){
                    Log.info("True");
                    Cupboard.click();
                    RandomSleep();
                }
                if (SceneObjects.getNearest(2869) != null) {
                    SceneObjects.getNearest(2869).click();
                    RandomSleep();
                    if (Dialog.isOpen()) {
                        Dialog.processContinue();
                    }
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