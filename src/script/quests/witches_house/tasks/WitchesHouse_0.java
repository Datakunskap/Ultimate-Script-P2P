package script.quests.witches_house.tasks;

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

import static script.quests.witches_house.data.Quest.THE_RESTLESS_GHOST;
import static script.quests.witches_house.data.Quest.WITCHES_HOUSE;

public class WitchesHouse_0 extends Task {

    private static final Position Boy = new Position(2929, 3456);

    @Override
    public boolean validate() {
        return THE_RESTLESS_GHOST.getVarpValue() == 5
                && WITCHES_HOUSE.getVarpValue() == 0
                && Skills.getLevel(Skill.MAGIC) >= 13
                && WitchesHouse_Preparation.readyToStartWitchesHouse;
    }

    @Override
    public int execute() {

        if (Dialog.canContinue()) {
            Dialog.processContinue();
        }

        if (!Movement.isRunEnabled()) {
            if (Movement.getRunEnergy() > Random.mid(5, 30)) {
                Movement.toggleRun(true);
            }
        }

        Npc boy = Npcs.getNearest(3994);
        if (Boy.distance() > 10) {
            Movement.walkToRandomized(Boy);
        }
        if (boy != null) {
            if (Boy.distance() <= 10 && !Dialog.isOpen()) {
                boy.click();
                Time.sleepUntil(()-> Dialog.isOpen(), 5000);
            }
        }
        if (Dialog.isOpen() && Interfaces.getComponent(219, 1) == null) {
            Dialog.processContinue();
            lowRandom();
        }
        if (getComponentOptions(1).contains("What's")) {
            clickDialogComponenet(1);
        }
        if (getComponentOptions(1).contains("Ok")) {
            clickDialogComponenet(1);
        }

        return lowRandom();

    }

    public int lowRandom() {
        return Random.mid(299, 444);
    }

    public void clickDialogComponenet(int Option) {
        if (Interfaces.getComponent(219, 1, Option) != null) {
            Interfaces.getComponent(219, 1, Option).click();
            lowRandom();
        }
    }

    public String getComponentOptions(int Option) {
        String Text = "Null";
        if (Interfaces.getComponent(219, 1, Option) != null) {
            Text = Interfaces.getComponent(219, 1, Option).getText();
        }
        return Text;
    }

}
