package script.quests.the_restless_ghost.tasks;

import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Area;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.script.task.Task;
import api.API;
import org.rspeer.ui.Log;
import script.quests.the_restless_ghost.data.Quest;
import script.wrappers.MovementBreaks;

public class RestlessGhost_3 extends Task {

    public static final Position LADDER_POSITION = new Position(3104, 3162, 0);
    public static final Position ALTAR_POSITION = new Position(3120, 9566, 0);
    public static final Area WIZARD_TOWER_DOWNSTAIRS = Area.rectangular(3092, 9581, 3125, 9551);

    @Override
    public boolean validate() {
        return Quest.THE_RESTLESS_GHOST.getVarpValue() == 3;
    }

    @Override
    public int execute() {

        Log.info("TheRestlessGhost_3");

        API.runFromAttacker();

        API.doDialog();

        API.toggleRun();

        API.drinkStaminaPotion();

        if(API.playerIsAt(WIZARD_TOWER_DOWNSTAIRS)){
            Log.info("Searching the altar for the skull");
            API.interactWithSceneobject("Altar", "Search", ALTAR_POSITION);
        }
        if (!API.playerIsAt(WIZARD_TOWER_DOWNSTAIRS)) {
            Log.info("Walking to the altar");
            Movement.walkTo(ALTAR_POSITION, MovementBreaks::shouldBreakOnRunenergy);
        }

        return API.lowRandom();
    }

}
