package script.quests.witches_house.tasks;

import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Inventory;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;

import static org.rspeer.runetek.api.input.menu.ActionOpcodes.ITEM_ON_NPC;
import static script.quests.witches_house.data.Quest.WITCHES_HOUSE;


public class WitchesHouse_2 extends Task {

    private static final Position Pot = new Position(2899, 3473);
    private static final Position Ladder = new Position(2906, 3476);

    private Area House = Area.rectangular(2901, 3476, 2937, 3459, 0);
    private Area Basement = Area.rectangular(2901, 9890, 2937, 9850);

    @Override
    public boolean validate() {
        return WITCHES_HOUSE.getVarpValue() == 2;
    }

    @Override
    public int execute() {

        if (!Inventory.contains(2410)) {
            SceneObject Cupboard = SceneObjects.newQuery().nameContains("Cupboard").actions("Open", "search").reachable().results().nearest();
            if (Cupboard != null) {
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
        if (Inventory.contains(2410)) {
            SceneObject Ladder = SceneObjects.newQuery().nameContains("Ladder").actions("Climb-up").reachable().results().nearest();
            if (Ladder == null) {
                interactWithObject(2866);
            }
            if (Ladder != null) {
                Ladder.click();
                RandomSleep();
            }
            Position Cheese = new Position(2903, 3466);
            if (SceneObjects.getNearest(24686) != null) {
                if (SceneObjects.getNearest(24686).containsAction("Open")) {
                    SceneObjects.getNearest(24686).click();
                    RandomSleep();
                }
            }
            if (SceneObjects.getNearest(24686) == null) {
                if (Cheese.distance() > 1) {
                    Movement.setWalkFlag(Cheese);
                    RandomSleep();
                    RandomSleep();
                }
            }
            if (Cheese.distance() < 1) {
                if (!Interfaces.isOpen(229)) {
                    if (Inventory.contains("Cheese")) {
                        Inventory.getFirst("Cheese").interact("Drop");
                        RandomSleep();
                        RandomSleep();
                    }
                }
                if (Npcs.getNearest("Mouse") != null) {
                    Inventory.getFirst(2410).interact("Use");
                    RandomSleep();
                    Npcs.getNearest("Mouse").interact(ITEM_ON_NPC);
                    RandomSleep();
                }
            }
        }

        return 600;
    }

    public void interactWithObject(int ID) {
        if (SceneObjects.getNearest(ID) != null) {
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