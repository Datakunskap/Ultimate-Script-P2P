package script.quests.waterfall_quest.tasks;

import api.API;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

import static script.quests.waterfall_quest.data.Quest.WATERFALL;
import static script.quests.witches_house.data.Quest.WITCHES_HOUSE;

public class Waterfall_2 extends Task {

    public static final String ROCK = "Rock";
    public static final String SWIM_TO = "Swim to";
    public static final String SEARCH_ACTION = "Search";
    public static final String READ_ACTION = "Read";
    public static final String CLIMB_UP = "Climb-up";
    public static final int STAIRCASE = 16671;
    public static final Area RAFT_AREA = Area.rectangular(2508, 3482, 2514, 3476);
    public static final int BOOKCASE = 1989;
    public static final String BOOK_ON_BAXTORIAN = "Book on baxtorian";

    public static final Position BOOKCASE_POSITION = new Position(2519, 3427, 1);
    public static final Position STAIRCASE_POSITION = new Position(2518, 3431, 0);

    private static final Area HOUSE_UPSTAIRS = Area.rectangular(2516, 3431, 2520, 3424, 1);

    @Override
    public boolean validate() {
        return WITCHES_HOUSE.getVarpValue() == 7 && WATERFALL.getVarpValue() == 2;
    }

    @Override
    public int execute() {

        Log.info("Waterfall_2");

        API.runFromAttacker();

        API.doDialog();

        API.toggleRun();

        API.drinkStaminaPotion();

        if (API.isOnFloorLevel(0)) {
            Log.info("More than 5 tiles away from bookcase");
            if (API.playerIsAt(RAFT_AREA)) {
                Log.info("I'm at the raft area");
                API.interactWithSceneobjectWithoutMoving(ROCK, SWIM_TO);
                Log.info("I'm interacting with the rock");
            }
            if (!API.playerIsAt(RAFT_AREA)) {
                if (!API.playerIsAt(HOUSE_UPSTAIRS)) {
                    API.interactWithSceneobject(STAIRCASE, CLIMB_UP, STAIRCASE_POSITION);
                }
            }
        }

        if (API.isOnFloorLevel(1)) {
            if (!API.inventoryHasItem(false, BOOK_ON_BAXTORIAN, 1)) {
                API.interactWithSceneobject(BOOKCASE, SEARCH_ACTION, BOOKCASE_POSITION);
            }
            if (API.inventoryHasItem(false, BOOK_ON_BAXTORIAN, 1)) {
                Log.info("I have" + " " + BOOK_ON_BAXTORIAN);
                API.interactWithItemInInventory(BOOK_ON_BAXTORIAN, READ_ACTION);
            }
        }

        return API.mediumRandom();
    }
}