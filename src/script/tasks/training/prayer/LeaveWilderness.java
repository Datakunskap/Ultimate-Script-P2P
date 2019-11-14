package script.tasks.training.prayer;

import api.component.ExWilderness;
import org.rspeer.runetek.api.component.tab.Skill;
import org.rspeer.runetek.api.component.tab.Skills;
import org.rspeer.runetek.api.movement.Movement;
import org.rspeer.runetek.api.movement.position.Position;
import org.rspeer.script.task.Task;
import script.wrappers.GEWrapper;
import script.wrappers.SupplyMapWrapper;
import script.wrappers.WalkingWrapper;

public class LeaveWilderness extends Task {

    private static final Position LVL_30_POSITION = new Position(3069, 3746, 0);;

    @Override
    public boolean validate() {
        return Skills.getLevel(Skill.PRAYER) == 50 && ExWilderness.getLevel() >= 30;
    }

    @Override
    public int execute() {
        boolean isHighLevelWilderness = ExWilderness.getLevel() > 30;

        if (isHighLevelWilderness) {
            if (Movement.getDaxWalker().isUseTeleports()) {
                Movement.getDaxWalker().setUseTeleports(false);
            }

            WalkingWrapper.walkToPosition(LVL_30_POSITION);
        } else {
            if (!Movement.getDaxWalker().isUseTeleports()) {
                Movement.getDaxWalker().setUseTeleports(true);
            }
            GEWrapper.setBuySupplies(true, true, SupplyMapWrapper.getMortMyreFungusItemsMap());
        }

        return 1000;
    }
}
