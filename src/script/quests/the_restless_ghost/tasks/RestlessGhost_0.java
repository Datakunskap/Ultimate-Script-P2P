package script.quests.the_restless_ghost.tasks;

import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.script.task.Task;
import api.API;
import org.rspeer.ui.Log;
import script.quests.the_restless_ghost.data.Quest;

public class RestlessGhost_0 extends Task {

    public static final Position FATHER_AERECK_POSITION = new Position(3243, 3208);

    @Override
    public boolean validate() {
        return Quest.THE_RESTLESS_GHOST.getVarpValue() == 0 && Skills.getLevel(Skill.MAGIC) >= 13;
    }

    @Override
    public int execute() {

        Log.info("TheRestlessGhost_0");

        API.doEating(7);

        API.runFromAttacker();

        API.doDialog();

        API.toggleRun();

        API.drinkStaminaPotion();

        API.talkTo("Father Aereck", FATHER_AERECK_POSITION);
        Dialog.process("I'm looking for a quest!");
        Dialog.process("Ok, let me help then.");

        return API.lowRandom();
    }

}


