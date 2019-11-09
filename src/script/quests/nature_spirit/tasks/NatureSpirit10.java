package script.quests.nature_spirit.tasks;

import org.rspeer.runetek.adapter.scene.SceneObject;
import org.rspeer.runetek.api.commons.Time;
import org.rspeer.runetek.api.component.Dialog;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import org.rspeer.runetek.api.scene.Players;
import org.rspeer.runetek.api.scene.SceneObjects;
import org.rspeer.script.task.Task;
import script.data.Locations;
import script.quests.nature_spirit.NatureSpirit;
import script.quests.nature_spirit.data.Location;
import script.quests.nature_spirit.data.Quest;
import script.quests.nature_spirit.wrappers.WalkingWrapper;

public class NatureSpirit10 extends Task {
    @Override
    public boolean validate() {
        return Quest.NATURE_SPIRIT.getVarpValue() == 75
                && Skills.getLevel(Skill.PRAYER) < 50
                && (Locations.NATURE_GROTTO_AREA.contains(Players.getLocal())
                        || Locations.INSIDE_GROTTO_AREA.contains(Players.getLocal()));
    }

    @Override
    public int execute() {
        if (Dialog.isOpen()) {
            if (Dialog.canContinue()) {
                Dialog.processContinue();
            }

            return NatureSpirit.getLoopReturn();
        }

        if (Locations.NATURE_GROTTO_AREA.contains(Players.getLocal())
                || Locations.INSIDE_GROTTO_AREA.contains(Players.getLocal())) {

            WalkingWrapper.exitAndLeaveGrotto();
        }

        return NatureSpirit.getLoopReturn();
    }
}
