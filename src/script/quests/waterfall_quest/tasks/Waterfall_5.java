package script.quests.waterfall_quest.tasks;

import api.API;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;
import script.wrappers.MovementBreaks;

import static script.quests.nature_spirit.data.Quest.WATERFALL;
import static script.quests.witches_house.data.Quest.WITCHES_HOUSE;

public class Waterfall_5 extends Task {

    private static final int KEY = 298;
    private static final int CRATE = 1999;
    private static final int DOOR = 2002;

    private static final String SEARCH_ACTION = "Search";
    private static final String OPEN_ACTION = "Open";

    private static final Position DOOR_ONE_POSITION = new Position(2568, 9893);
    private static final Position DOOR_TWO_POSITION = new Position(2566, 9901);

    private static final Area KEY_AREA = Area.rectangular(2581, 9889, 2597, 9877);
    private static final Area DOOR_AREA = Area.rectangular(2562, 9901, 2570, 9894);
    private static final Area FINAL_ROOM_AREA = Area.rectangular(2555, 9918, 2574, 9902);

    @Override
    public boolean validate() {
        return WITCHES_HOUSE.getVarpValue() == 7 && WATERFALL.getVarpValue() == 5;
    }

    @Override
    public int execute() {

        Log.info("Waterfall_5");

        API.runFromAttacker();

        API.doDialog();

        API.toggleRun();

        API.drinkStaminaPotion();

        API.doEating(15);

        if (!API.inventoryHasItem(false, KEY, 1)) {
            if (!API.playerIsAt(KEY_AREA)) {
                Movement.walkTo(KEY_AREA.getCenter(), MovementBreaks::shouldBreakOnTarget);
            }
            if (API.playerIsAt(KEY_AREA)) {
                API.interactWithSceneobjectWithoutMoving(CRATE, SEARCH_ACTION);
                Time.sleepUntil(() -> API.inventoryHasItem(false, KEY, 1), 5000);
            }
        }

        if (API.inventoryHasItem(false, KEY, 1)) {
            if (!API.playerIsAt(DOOR_AREA)) {
                API.interactWithSceneobject(DOOR, OPEN_ACTION, DOOR_ONE_POSITION);
                Time.sleepUntil(() -> API.playerIsAt(DOOR_AREA), 1000);
            }
            if (API.playerIsAt(DOOR_AREA)) {
                if (DOOR_TWO_POSITION.distance() >= 1) {
                    Movement.setWalkFlag(DOOR_TWO_POSITION);
                    Time.sleepUntil(()-> DOOR_TWO_POSITION.distance() < 1, 5000);
                }
                if (DOOR_TWO_POSITION.distance() < 1) {
                    API.interactWithSceneobjectWithoutMoving(DOOR, OPEN_ACTION);
                    Time.sleepUntil(() -> API.playerIsAt(FINAL_ROOM_AREA), 5000);
                }
            }
        }

        return API.lowRandom();
    }

}