package script.quests.witches_house.tasks;

import api.API;
import org.rspeer.runetek.adapter.scene.Npc;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.commons.math.Random;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.Interfaces;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.runetek.api.scene.Npcs;
import org.rspeer.script.task.Task;
import org.rspeer.ui.Log;

import static api.API.lowRandom;
import static script.quests.witches_house.data.Quest.THE_RESTLESS_GHOST;
import static script.quests.witches_house.data.Quest.WITCHES_HOUSE;

public class WitchesHouse_0 extends Task {

    public static final String BOY_NAME = "Boy";
    public static final Position BOY_POSITION = new Position(2929, 3456);

    @Override
    public boolean validate() {
        return THE_RESTLESS_GHOST.getVarpValue() == 5
                && WITCHES_HOUSE.getVarpValue() == 0
                && Skills.getLevel(Skill.MAGIC) >= 13
                && Skills.getLevel(Skill.PRAYER) < 50
                && WitchesHouse_Preparation.readyToStartWitchesHouse;
    }

    @Override
    public int execute() {

        Log.info("WitchesHouse_0");

        API.doDialog();

        API.toggleRun();

        API.talkTo(BOY_NAME, BOY_POSITION);
        Dialog.process("What's the matter?");
        Dialog.process("Ok, I'll see what I can do.");

        return API.lowRandom();

    }

}
