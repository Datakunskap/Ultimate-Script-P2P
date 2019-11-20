package script.quests.the_restless_ghost.tasks;

import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;
import api.API;
import script.quests.the_restless_ghost.data.Quest;
import script.wrappers.MovementBreaks;

public class RestlessGhost_4 extends Task {

    public static final String GHOSTS_SKULL = "Ghost's skull";

    public static final Position GHOST_POSITION = new Position(3249, 3192);

    @Override
    public boolean validate() {
        return Quest.THE_RESTLESS_GHOST.getVarpValue() == 4 && Skills.getLevel(Skill.MAGIC) >= 13;
    }

    @Override
    public int execute() {

        Log.info("TheRestlessGhost_4");

        API.runFromAttacker();

        API.doDialog();

        API.toggleRun();

        API.drinkStaminaPotion();

        if (GHOST_POSITION.distance() > 10 || !GHOST_POSITION.isPositionInteractable()) {
            Log.info("Walking to the ghost");
            Movement.walkTo(GHOST_POSITION, MovementBreaks::shouldBreakOnRunenergy);
        }

        if (GHOST_POSITION.distance() <= 10) {
            SceneObject coffin = SceneObjects.getNearest("Coffin");
            if (coffin.containsAction("Close")) {
                if (!coffin.getPosition().isPositionInteractable()) {
                    Movement.walkTo(coffin.getPosition());
                }
                if (coffin.getPosition().isPositionInteractable()) {
                    API.useItemOn(GHOSTS_SKULL, "Coffin", GHOST_POSITION);
                }
            }
            if (coffin.containsAction("Open")) {
                Log.info("Opening the chest");
                coffin.interact("Open");
                Time.sleepUntil(() -> !coffin.containsAction("Open"), 5000);
            }
        }

        return API.lowRandom();
    }

}