package script.quests.witches_house.tasks;

import api.API;
import org.rspeer.runetek.adapter.scene.Player;
import org.rspeer.runetek.api.commons.Time;
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

    private static final String KEY_NAME = "Door key";
    private static final String POT_NAME = "Potted plant";
    private static final String DOOR_NAME = "Door";
    private static final String LADDER_NAME = "Ladder";
    private static final String LEATHER_GLOVES = "Leather gloves";
    private static final String MAGNET = "Magnet";
    private static final String CUPBOARD = "Cupboard";
    private static final String GATE = "Gate";

    private static final Position POT_POSITION = new Position(2899, 3474);
    private static final Position DOOR_POSITION = new Position(2900, 3473);
    private static final Position DOOR_TO_LADDER_POSITION = new Position(2902, 3474);
    private static final Position LADDER_POSITION = new Position(2907, 3476);
    private static final Position CUPBOARD_POSITION = new Position(2898, 9873);

    private static final Area HOUSE_OUTSIDE = Area.rectangular(2900, 3485, 2883, 3459);
    private static final Area HOUSE_INSIDE = Area.rectangular(2901, 3476, 2907, 3468, 0);
    private static final Area LADDER_AREA = Area.rectangular(2901, 3476, 2907, 3475);
    private static final Area HOUSE_MAIN_PART = Area.rectangular(2901, 3474, 2907, 3468);
    private static final Area BASEMENT = Area.rectangular(2898, 9877, 2908, 9870);
    private static final Area GARDEN_MAIN = Area.rectangular(2937, 3467, 2900, 3459);
    private static final Area GARDEN_FOUNTAIN = Area.rectangular(2908, 3475, 2913, 3467);
    private static final Area MOUSE_AREA = Area.rectangular(2900, 3467, 2903, 3466);


    @Override
    public boolean validate() {
        return WITCHES_HOUSE.getVarpValue() == 1;
    }

    @Override
    public int execute() {

        Log.info("WitchesHouse_1");

        Player local = Players.getLocal();

        API.doDialog();

        API.toggleRun();

        if (Inventory.contains(KEY_NAME)) {
            if (!BASEMENT.contains(local) || !HOUSE_OUTSIDE.contains(local) || !GARDEN_FOUNTAIN.contains(local) || !HOUSE_INSIDE.contains(local) || !MOUSE_AREA.contains(local) || !GARDEN_FOUNTAIN.contains(local)) {
                if (local.getAnimation() == -1) {
                    Movement.walkTo(HOUSE_OUTSIDE.getCenter());
                }
            }
        }
        if (!Inventory.contains(KEY_NAME)) {
            Log.info("I don't have the key");
            API.interactWithSceneobject(POT_NAME, "Look-under", POT_POSITION);
        }
        if (!HOUSE_INSIDE.contains(local) && HOUSE_OUTSIDE.contains(local)) {
            if (Inventory.contains(KEY_NAME)) {
                Log.info("I have the key");
                API.useItemOn(KEY_NAME, DOOR_NAME, DOOR_POSITION);
                Time.sleepUntil(() -> !HOUSE_INSIDE.contains(local), 5000);
            }
        }
        if (HOUSE_INSIDE.contains(local)) {
            if (!Inventory.contains(MAGNET)) {
                if (API.playerIsAt(HOUSE_MAIN_PART) && !LADDER_POSITION.isPositionWalkable()) {
                    API.interactWithSceneobject(24686, "Open", DOOR_TO_LADDER_POSITION);
                }
                if (API.playerIsAt(HOUSE_MAIN_PART) && LADDER_POSITION.isPositionWalkable()) {
                    API.interactWithSceneobject(LADDER_NAME, "Climb-down", LADDER_POSITION);
                }
                if (API.playerIsAt(LADDER_AREA)) {
                    Log.info("1");
                    API.interactWithSceneobject(LADDER_NAME, "Climb-down", LADDER_POSITION);
                }
            }
        }
        if (BASEMENT.contains(local)) {
            if (!Inventory.contains(MAGNET)) {
                if (!API.isWearingItem(LEATHER_GLOVES)) {
                    API.wearItem(LEATHER_GLOVES);
                }
                if (API.isWearingItem(LEATHER_GLOVES)) {
                    if (!CUPBOARD_POSITION.isPositionInteractable()) {
                        SceneObjects.getNearest(GATE).interact("Open");
                        Time.sleepUntil(() -> !SceneObjects.getNearest(GATE).containsAction("Open"), 3000);
                    }
                    if (CUPBOARD_POSITION.isPositionInteractable()) {
                        API.interactWithSceneobject(CUPBOARD, "Open", CUPBOARD_POSITION);
                        API.interactWithSceneobject(CUPBOARD, "Search", CUPBOARD_POSITION);
                    }
                }
            }
        }


        return API.lowRandom();
    }
}