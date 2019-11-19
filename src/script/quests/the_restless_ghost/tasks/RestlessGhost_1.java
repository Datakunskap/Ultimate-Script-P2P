package script.quests.the_restless_ghost.tasks;

import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.script.task.Task;
import api.API;
import org.rspeer.ui.Log;
import script.quests.the_restless_ghost.data.Quest;

public class RestlessGhost_1 extends Task {

    public static final Position FATHER_URHNEY_POSITION = new Position(3147, 3175);

    @Override
    public boolean validate() {
        return Quest.THE_RESTLESS_GHOST.getVarpValue() == 1 && Skills.getLevel(Skill.MAGIC) >= 13;
    }

    @Override
    public int execute() {

        Log.info("TheRestlessGhost_1");

        API.runFromAttacker();

        API.doDialog();

        API.toggleRun();

        API.drinkStaminaPotion();

        API.talkTo("Father Urhney", FATHER_URHNEY_POSITION);
        Dialog.process("Father Aereck sent me to talk to you.");
        Dialog.process("He's got a ghost haunting his graveyard.");

        return API.lowRandom();
    }

}



